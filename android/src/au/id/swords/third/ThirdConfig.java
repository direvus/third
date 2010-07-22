package au.id.swords.third;

import java.util.Map;
import java.util.Vector;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Enumeration;
import java.lang.StringBuilder;

public class ThirdConfig
{
    static int[] sides = {2, 4, 6, 8, 10, 12, 20, 100};
    LinkedHashMap dice = new LinkedHashMap();
    Integer mul;
    Integer mod;

    public ThirdConfig()
    {
        for(int i: sides)
            dice.put(i, 0);
        mul = 1;
        mod = 0;
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

    public String toString()
    {
        Vector<String> sv = new Vector<String>();
        StringBuilder sb = new StringBuilder();
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
}
