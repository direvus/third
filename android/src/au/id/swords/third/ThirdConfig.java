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
    static int[] sides = {2, 4, 6, 8, 10, 12, 20, 100};
    Integer id;
    String name;
    LinkedHashMap dice = new LinkedHashMap();
    Integer mul;
    Integer mod;

    public ThirdConfig()
    {
        name = new String();
        for(int i: sides)
            dice.put(i, 0);
        mul = 1;
        mod = 0;
    }

    public String colName(int sides)
    {
        return String.format("d%d", sides);
    }

    public ThirdConfig(Cursor cur)
    {
        id = cur.getInt(cur.getColumnIndex("_id"));
        name = cur.getString(cur.getColumnIndex("name"));
        for(int i: sides)
            dice.put(i, cur.getInt(cur.getColumnIndex(colName(i))));

        mul = cur.getInt(cur.getColumnIndex("multiplier"));
        mod = cur.getInt(cur.getColumnIndex("modifier"));
    }

    public ContentValues getValues()
    {
        ContentValues vals = new ContentValues();
        vals.put("name", name);
        for(int i: sides)
            vals.put(colName(i), getDie(i));

        vals.put("multiplier", mul);
        vals.put("modifier", mod);
        vals.put("dx", 0);
        return vals;
    }

    public String getName()
    {
        return name;
    }

    public Integer getDie(Integer die)
    {
        return (Integer)dice.get(die);
    }

    public Vector getDice()
    {
        Vector<Integer> v = new Vector<Integer>();
        Iterator keys = dice.keySet().iterator();
        while(keys.hasNext())
        {
            Integer key = (Integer)keys.next();
            Integer val = (Integer)dice.get(key);
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
        return mul;
    }

    public Integer getModifier()
    {
        return mod;
    }

    public Integer getMin()
    {
        Integer min = new Integer(0);
        Iterator vals = dice.values().iterator();
        while(vals.hasNext())
            min += (Integer)vals.next();

        return (min * mul) + mod;
    }

    public Integer getMax()
    {
        Integer max = new Integer(0);
        Iterator keys = dice.keySet().iterator();
        while(keys.hasNext())
        {
            Integer key = (Integer)keys.next();
            max += key * (Integer)dice.get(key);
        }
        return (max * mul) + mod;
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
        Iterator keys = dice.keySet().iterator();
        while(keys.hasNext())
        {
            Integer k = (Integer)keys.next();
            Integer v = (Integer)dice.get(k);
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

        if(mul != 1)
            sb.append(String.format(" * %d", mul));

        if(mod != 0)
        {
            if(mod < 0)
                sb.append(String.format(" - %d", Math.abs(mod)));
            else
                sb.append(String.format(" + %d", Math.abs(mod)));
        }
        return sb.toString();
    }

    public String toString()
    {
        return name + " " + describeConfig();
    }

    public void setName(String s)
    {
        name = s;
    }

    public void setDie(Integer die, Integer value)
    {
        dice.put(die, value);
    }

    public void setMultiplier(Integer value)
    {
        mul = value;
    }

    public void setModifier(Integer value)
    {
        mod = value;
    }
}
