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
package au.id.swords.third;

import android.content.ContentValues;
import android.database.Cursor;
import java.util.Map;
import java.util.Vector;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Enumeration;
import java.lang.StringBuilder;

public class ThirdConfig
{
    static int[] mSides = {2, 4, 6, 8, 10, 12, 20, 100};
    Integer mId;
    String mName;
    ArrayList<ThirdConfig> mIncludes = new ArrayList<ThirdConfig>();
    LinkedHashMap mDice = new LinkedHashMap();
    Integer mDxSides;
    Integer mDx;
    Integer mMul;
    Integer mMod;

    public ThirdConfig()
    {
        mId = 0;
        mName = new String();
        init();
    }

    public void init()
    {
        for(int i: mSides)
            mDice.put(i, 0);
        mDx = 0;
        mDxSides = 3;
        mMul = 1;
        mMod = 0;
    }

    public void reset()
    {
        init();
    }

    public String colName(int sides)
    {
        return String.format("d%d", sides);
    }

    public ThirdConfig(Cursor cur)
    {
        mId = cur.getInt(cur.getColumnIndex("_id"));
        mName = cur.getString(cur.getColumnIndex("name"));
        for(int i: mSides)
            mDice.put(i, cur.getInt(cur.getColumnIndex(colName(i))));

        mDx = cur.getInt(cur.getColumnIndex("dx"));
        mDxSides = cur.getInt(cur.getColumnIndex("dx_sides"));
        mMul = cur.getInt(cur.getColumnIndex("multiplier"));
        mMod = cur.getInt(cur.getColumnIndex("modifier"));
    }

    public void addInclude(ThirdConfig include)
    {
        mIncludes.add(include);
    }

    public ContentValues getValues()
    {
        ContentValues vals = new ContentValues();
        vals.put("name", mName);
        for(int i: mSides)
            vals.put(colName(i), getDie(i));

        vals.put("dx", mDx);
        vals.put("dx_sides", mDxSides);
        vals.put("multiplier", mMul);
        vals.put("modifier", mMod);
        return vals;
    }

    public Integer getId()
    {
        return mId;
    }

    public String getName()
    {
        return mName;
    }

    public Integer getDie(Integer die)
    {
        return (Integer)mDice.get(die);
    }

    public Vector getDice()
    {
        Vector<Integer> v = new Vector<Integer>();
        Iterator keys = mDice.keySet().iterator();
        Integer sign;
        while(keys.hasNext())
        {
            Integer key = (Integer)keys.next();
            Integer val = (Integer)mDice.get(key);
            sign = (val < 0) ? -1 : 1;
            for(Integer i = 0; i < Math.abs(val); i++)
                v.add(sign * key);
        }

        if(mDx != 0 && mDxSides != 0)
        {
            sign = (mDx < 0) ? -1 : 1;
            for(Integer i = 0; i < Math.abs(mDx); i++)
                v.add(sign * mDxSides);
        }
        return v;
    }

    public Integer getDx()
    {
        return mDx;
    }

    public Integer getDxSides()
    {
        return mDxSides;
    }

    public Integer getMultiplier()
    {
        return mMul;
    }

    public Integer getModifier()
    {
        return mMod;
    }

    public ArrayList<ThirdConfig> getIncludes()
    {
        return mIncludes;
    }

    public Integer getMin()
    {
        Integer min = new Integer(0);

        for(ThirdConfig inc: mIncludes)
        {
            min += inc.getMin();
        }

        Iterator vals = mDice.values().iterator();
        while(vals.hasNext())
            min += (Integer)vals.next();

        min += mDx;
        return (min * mMul) + mMod;
    }

    public Integer getMax()
    {
        Integer max = new Integer(0);

        for(ThirdConfig inc: mIncludes)
        {
            max += inc.getMax();
        }

        Iterator keys = mDice.keySet().iterator();
        while(keys.hasNext())
        {
            Integer key = (Integer)keys.next();
            max += key * (Integer)mDice.get(key);
        }
        max += mDxSides * mDx;
        return (max * mMul) + mMod;
    }

    public Integer getRange()
    {
        return getMax() - getMin();
    }

    public String describeRange()
    {
        return String.format("%d - %d", getMin(), getMax());
    }

    public String describe()
    {
        StringBuilder sb = new StringBuilder();
        Vector<String> sv = new Vector<String>();

        for(ThirdConfig inc: mIncludes)
        {
            sv.add(inc.describeInclude());
        }

        Iterator keys = mDice.keySet().iterator();
        while(keys.hasNext())
        {
            Integer k = (Integer)keys.next();
            Integer v = (Integer)mDice.get(k);
            if(v != 0)
            {
                if(v == 1)
                    sv.add(String.format("d%d", k));
                else
                    sv.add(String.format("%dd%d", v, k));
            }
        }

        if(mDx != 0 && mDxSides != 0)
        {
            if(mDx == 1)
                sv.add(String.format("d%d", mDxSides));
            else
                sv.add(String.format("%dd%d", mDx, mDxSides));
        }

        Iterator it = sv.iterator();
        while(it.hasNext())
        {
            sb.append(it.next());
            if(it.hasNext())
                sb.append(" + ");
        }

        if(mMul != 1 && sb.length() > 0)
            sb.append(String.format(" * %d", mMul));

        if(mMod != 0)
        {
            if(mMod < 0)
                sb.append(String.format(" - %d", Math.abs(mMod)));
            else
                sb.append(String.format(" + %d", Math.abs(mMod)));
        }
        return sb.toString();
    }

    public String describeInclude()
    {
        return "[" + mName + "]";
    }

    public String toString()
    {
        return mName + " " + describe();
    }

    public void setName(String s)
    {
        mName = s;
    }

    public void setDie(Integer die, Integer value)
    {
        mDice.put(die, value);
    }

    public void setDx(Integer value)
    {
        mDx = value;
    }

    public void setDxSides(Integer sides)
    {
        mDxSides = sides;
    }

    public void setMultiplier(Integer value)
    {
        mMul = value;
    }

    public void setModifier(Integer value)
    {
        mMod = value;
    }
}
