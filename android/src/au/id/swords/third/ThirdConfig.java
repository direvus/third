package au.id.swords.third;

import android.content.ContentValues;
import android.database.Cursor;
import java.util.Map;
import java.util.Vector;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Enumeration;
import java.lang.StringBuilder;

public class ThirdConfig
{
    static int[] mSides = {2, 4, 6, 8, 10, 12, 20, 100};
    Integer mId;
    String mName;
    LinkedHashMap mDice = new LinkedHashMap();
    Integer mMul;
    Integer mMod;

    public ThirdConfig()
    {
        mName = new String();
        for(int i: mSides)
            mDice.put(i, 0);
        mMul = 1;
        mMod = 0;
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

        mMul = cur.getInt(cur.getColumnIndex("multiplier"));
        mMod = cur.getInt(cur.getColumnIndex("modifier"));
    }

    public ContentValues getValues()
    {
        ContentValues vals = new ContentValues();
        vals.put("name", mName);
        for(int i: mSides)
            vals.put(colName(i), getDie(i));

        vals.put("multiplier", mMul);
        vals.put("modifier", mMod);
        vals.put("dx", 0);
        return vals;
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
        while(keys.hasNext())
        {
            Integer key = (Integer)keys.next();
            Integer val = (Integer)mDice.get(key);
            Integer sign = (val < 0) ? -1 : 1;
            for(Integer i = 0; i < Math.abs(val); i++)
            {
                v.add(sign * key);
            }
        }
        return v;
    }

    public Integer getMultiplier()
    {
        return mMul;
    }

    public Integer getModifier()
    {
        return mMod;
    }

    public Integer getMin()
    {
        Integer min = new Integer(0);
        Iterator vals = mDice.values().iterator();
        while(vals.hasNext())
            min += (Integer)vals.next();

        return (min * mMul) + mMod;
    }

    public Integer getMax()
    {
        Integer max = new Integer(0);
        Iterator keys = mDice.keySet().iterator();
        while(keys.hasNext())
        {
            Integer key = (Integer)keys.next();
            max += key * (Integer)mDice.get(key);
        }
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

    public String describeConfig()
    {
        StringBuilder sb = new StringBuilder();
        Vector<String> sv = new Vector<String>();
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

        Iterator it = sv.iterator();
        while(it.hasNext())
        {
            sb.append(it.next());
            if(it.hasNext())
                sb.append(" + ");
        }

        if(mMul != 1)
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

    public String toString()
    {
        return mName + " " + describeConfig();
    }

    public void setName(String s)
    {
        mName = s;
    }

    public void setDie(Integer die, Integer value)
    {
        mDice.put(die, value);
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
