/*
 * third: That's How I Roll Dice
 *     A dice roller for roleplaying nerds.
 *         http://swords.id.au/third/
 * 
 * Copyright (c) 2010, Brendan Jurd <bj@swords.id.au>
 * All rights reserved.
 * 
 * third is open-source, licensed under the Simplified BSD License, a copy of
 * which can be found in the file LICENSE at the top level of the source code.
 */
package au.id.swords.third2;

import android.util.SparseIntArray;

import java.util.Iterator;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class ThirdConfig
{
    static final int DEFAULT_DX_SIDES = 3;
    static final int[] SIDES = {2, 4, 6, 8, 10, 12, 20, 100};

    private int mId = -1;
    private String mName = "";
    private final Vector<ThirdConfig> mIncludes = new Vector<>();
    private final Vector<ThirdTrigger> mTriggers = new Vector<>();
    private final SparseIntArray mDice = new SparseIntArray();
    private int mDxSides;
    private int mDx;
    private int mMul;
    private int mMod;

    ThirdConfig()
    {
        init();
    }

    ThirdConfig(JSONObject json)
    {
        mId = json.optInt("id");
        mName = json.optString("name");
        mDxSides = json.optInt("dx_sides");
        mDx = json.optInt("dx");
        mMul = json.optInt("multiplier");
        mMod = json.optInt("modifier");

        if(mDxSides == 0)
            mDxSides = DEFAULT_DX_SIDES;

        if(mMul == 0)
            mMul = 1;

        for(int i: SIDES)
            mDice.put(i, json.optInt(colName(i)));

        JSONArray triggers = json.optJSONArray("triggers");
        if(triggers != null)
        {
            for(int i = 0; i < triggers.length(); i++)
            {
                try
                {
                    mTriggers.add(new ThirdTrigger(triggers.optJSONObject(i)));
                }
                catch(ThirdTrigger.InfiniteLoop e) {}
            }
        }
    }

    ThirdConfig(int id, String name)
    {
        mId = id;
        mName = name;
        init();
    }

    ThirdConfig(int id, ThirdConfig config)
    {
        mId = id;
        mName = config.getName();
        update(config);
    }

    void update(ThirdConfig config)
    {
        init();
        mDxSides = config.getDxSides();
        mDx = config.getDx();
        mMul = config.getMultiplier();
        mMod = config.getModifier();
        for(int i: SIDES)
            mDice.put(i, config.getDie(i));

        for(ThirdConfig include: config.getIncludes())
            mIncludes.add(include);
    }

    private void init()
    {
        mDx = 0;
        mDxSides = DEFAULT_DX_SIDES;
        mMul = 1;
        mMod = 0;

        mDice.clear();
        for(int i: SIDES)
            mDice.put(i, 0);

        mIncludes.clear();
        mTriggers.clear();
    }

    void reset()
    {
        mName = "";
        init();
    }

    String colName(int sides)
    {
        return String.format("d%d", sides);
    }

    void addInclude(ThirdConfig include)
    {
        mIncludes.add(include);
    }

    boolean hasInclude(int id)
    {
        for(ThirdConfig include: mIncludes)
        {
            if(id == include.getId())
                return true;
        }
        return false;
    }

    void removeInclude(int id)
    {
        for(int i = 0; i < mIncludes.size(); i++)
        {
            if(id == mIncludes.get(i).getId())
                mIncludes.remove(i);
        }
    }

    JSONObject toJSON()
    {
        JSONObject json = new JSONObject();
        try {
            json.put("id", mId);
            json.put("name", mName);
            json.put("dx_sides", mDxSides);
            json.put("dx", mDx);
            json.put("multiplier", mMul);
            json.put("modifier", mMod);
            for(int i: SIDES)
                json.put(colName(i), mDice.get(i));

            json.put("triggers", new JSONArray());
            for(ThirdTrigger trigger: mTriggers)
                json.accumulate("triggers", trigger.toJSON());

            json.put("includes", new JSONArray());
            for(ThirdConfig include: mIncludes)
                json.accumulate("includes", include.getId());
        }
        catch(JSONException e)
        {
        }

        return json;
    }

    int getId()
    {
        return mId;
    }

    String getName()
    {
        return mName;
    }

    int getDie(int die)
    {
        return mDice.get(die);
    }

    Vector<Integer> getDice()
    {
        Vector<Integer> v = new Vector<>();
        for(int sides: SIDES)
        {
            int count = mDice.get(sides, 0);
            int die = sides;
            if(count < 0)
                die = -sides;
            for(int i = 0; i < Math.abs(count); i++)
                v.add(die);
        }

        if(mDx != 0 && mDxSides != 0)
        {
            int die = mDxSides;
            if(mDx < 0)
                die = -mDxSides;

            for(int i = 0; i < Math.abs(mDx); i++)
                v.add(die);
        }
        return v;
    }

    Integer getDx()
    {
        return mDx;
    }

    Integer getDxSides()
    {
        return mDxSides;
    }

    Integer getMultiplier()
    {
        return mMul;
    }

    Integer getModifier()
    {
        return mMod;
    }

    Vector<ThirdConfig> getIncludes()
    {
        return mIncludes;
    }

    Vector<ThirdTrigger> getTriggers()
    {
        return mTrigger;
    }

    int getMin()
    {
        int min = 0;

        for(ThirdConfig inc: mIncludes)
            min += inc.getMin();

        for(ThirdTrigger trigger: mTriggers)
            min += trigger.getMin();

        for(int sides: SIDES)
            min += mDice.get(sides, 0);

        min += mDx;
        return (min * mMul) + mMod;
    }

    /*
     * Return whether the outcome has an upper bound.
     *
     * This is false if any of the configuration's triggers are unbounded,
     * otherwise it is true.
     */
    boolean isBounded()
    {
        for(ThirdTrigger trigger: mTriggers)
        {
            if(!trigger.isBounded())
                return false;
        }
        return true;
    }

    int getMax()
    {
        int max = 0;

        for(ThirdConfig inc: mIncludes)
            max += inc.getMax();

        for(ThirdTrigger trigger: mTriggers)
            max += trigger.getMax();

        for(int sides: SIDES)
            max += sides * mDice.get(sides);

        max += mDxSides * mDx;
        return (max * mMul) + mMod;
    }

    Integer getRange()
    {
        if(isBounded())
            return getMax() - getMin();
        else
            return null;
    }

    String describeRange()
    {
        if(isBounded())
            return String.format("%d - %d", getMin(), getMax());
        else
            return String.format("%d - ∞", getMin());
    }

    private String describeDie(int sides, int count)
    {
        if(count == 1)
            return String.format("d%d", sides);
        else
            return String.format("%dd%d", count, sides);
    }

    String describe()
    {
        StringBuilder sb = new StringBuilder();

        for(int sides: SIDES)
        {
            int count = mDice.get(sides);
            if(count != 0)
                ThirdUtil.append(sb, " + ", describeDie(sides, count));
        }

        if(mDx != 0 && mDxSides != 0)
        {
            ThirdUtil.append(sb, " + ", describeDie(mDx, mDxSides));
        }

        if(mTriggers.size() > 0)
        {
            StringBuilder triggers = new StringBuilder();
            for(ThirdTrigger trigger: mTriggers)
                ThirdUtil.append(triggers, ", ", trigger);

            sb.append("(").append(triggers).append(")");
        }

        for(ThirdConfig inc: mIncludes)
            ThirdUtil.append(sb, " + ", inc.describeInclude());

        if(mMul != 1 && sb.length() > 0)
            ThirdUtil.append(sb, " * ", String.valueOf(mMul));

        if(mMod != 0)
        {
            if(mMod < 0)
                sb.append(String.format(" - %d", Math.abs(mMod)));
            else
                sb.append(String.format(" + %d", Math.abs(mMod)));
        }
        return sb.toString();
    }

    String describeInclude()
    {
        return "[" + mName + "]";
    }

    @Override
    public String toString()
    {
        return mName + " " + describe();
    }

    void setName(String s)
    {
        mName = s;
    }

    void setDie(Integer die, Integer value)
    {
        mDice.put(die, value);
    }

    void setDx(Integer value)
    {
        mDx = value;
    }

    void setDxSides(Integer sides)
    {
        mDxSides = sides;
    }

    void setMultiplier(Integer value)
    {
        mMul = value;
    }

    void setModifier(Integer value)
    {
        mMod = value;
    }
}
