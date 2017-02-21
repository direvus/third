/*
 *
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

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Vector;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class ThirdTrigger
{
    public enum Type
    {
        ADD,
        EXPLODE,
        REPLACE,
        MAX,
        MIN
    }

    public static final String ANY = "Any";
    private static final Pattern RE_SPLIT = Pattern.compile("\\s*,\\s*");
    private static final Pattern RE_RANGE = Pattern.compile("^\\d+-\\d+$");
    private static final Pattern RE_INT = Pattern.compile("^\\d+$");
    private int mDie;
    private LinkedHashSet<Integer> mResults = new LinkedHashSet<>();
    private Type mType;

    ThirdTrigger(int die, Type type, String results) throws InfiniteLoop
    {
        mDie = Math.abs(die);
        mType = type;
        setResults(results);
        validate();
    }

    ThirdTrigger(JSONObject json) throws InfiniteLoop
    {
        mDie = Math.abs(json.optInt("die"));
        mType = Type.valueOf(json.optString("type"));
        JSONArray results = json.optJSONArray("results");
        if(results != null)
        {
            for(int i = 0; i < results.length(); i++)
                addResult(results.optInt(i));
        }
        validate();
    }

    private void validate() throws InfiniteLoop
    {
        if(mType == Type.EXPLODE && matchesAll())
            throw new InfiniteLoop();
    }

    /*
     * Add an integer to this trigger's set of results.
     *
     * Only positive integers up to and including the die size will be added.
     * Invalid inputs will be ignored.
     */
    private void addResult(int result)
    {
        if(result > 0 && result <= mDie)
            mResults.add(result);
    }

    private void addResults(Collection<Integer> results)
    {
        for(Integer result: results)
            addResult(result);
    }

    private void setResults(Collection<Integer> results)
    {
        mResults.clear();
        addResults(results);
    }

    private void setResults(String ranges)
    {
        mResults.clear();
        String[] pieces = RE_SPLIT.split(ranges.trim());
        for(String piece: pieces)
        {
            piece = piece.trim();
            if(RE_RANGE.matcher(piece).matches())
            {
                int pos = piece.indexOf("-");
                int a = Integer.valueOf(piece.substring(0, pos));
                int b = Integer.valueOf(piece.substring(pos + 1));
                addResults(new Range(a, b).getIntegers());
            }
            else if(RE_INT.matcher(piece).matches())
            {
                addResult(Integer.valueOf(piece));
            }
        }
    }

    public String describeResults()
    {
        if(matchesAll())
            return ANY;

        Vector<Range> ranges = new Vector<>();
        Range range = new Range();
        for(int result: mResults)
        {
            if(range.lower == 0)
            {
                range.lower = result;
            }
            else if(result > range.upper + 1)
            {
                ranges.add(range);
                range = new Range(result, result);
            }
            range.upper = result;
        }
        ranges.add(range);

        StringBuilder sb = new StringBuilder();
        for(Range r: ranges)
            ThirdUtil.append(sb, ",", r);

        return sb.toString();
    }

    /*
     * Return the total net effect on minimum outcome of firing this trigger.
     */
    int getMin()
    {
        if(mType == Type.ADD && matchesAll())
            return 1;
        else
            return 0;
    }

    /*
     * Return whether the maximum outcome of this trigger is constrained.
     *
     * A return of false means that the outcome may in theory be infinitely
     * large.  In practice, the outcome will be constrained by the limits of
     * the data type.
     */
    boolean isBounded()
    {
        return mType != Type.EXPLODE;
    }

    /*
     * Return the total net effect on maximum outcome of firing this trigger.
     */
    int getMax()
    {
        if(mType == Type.ADD)
            return mDie;
        else
            return 0;
    }

    /*
     * Return whether this trigger fires for any result on its die.
     */
    boolean matchesAll()
    {
        return (mResults.size() == 0 || mResults.size() == mDie);
    }

    boolean matches(int die, int result)
    {
        return Math.abs(die) == mDie && (
                matchesAll() || mResults.contains(result));
    }

    /*
     * Return whether this trigger fires on the given conditions.
     *
     * 'die' is the number of sides on the die, 'result' is the result of that
     * die roll, and 'raw' indicates whether the result is an actual die roll,
     * as opposed to a result from a trigger.
     */
    boolean firesOn(int die, int result, boolean raw)
    {
        return matches(die, result) && (mType == Type.EXPLODE || raw);
    }

    /*
     * Resolve the outcome of firing this trigger.
     *
     * The 'initial' value is the result which caused the trigger to fire, and
     * 'result' is the new die roll resulting from firing the trigger.
     * Depending on the type of the trigger this new result may replace the
     * initial result, be added to the initial result, or be ignored in favour
     * of the initial result.
     */
    int resolve(int initial, int result)
    {
        int outcome = 0;
        switch(mType)
        {
            case ADD:
            case EXPLODE:
                outcome = initial + result;
                break;
            case REPLACE:
                outcome = result;
                break;
            case MAX:
                outcome = Math.max(initial, result);
                break;
            case MIN:
                outcome = Math.min(initial, result);
                break;
        }
        return outcome;
    }

    String getName()
    {
        return mType.name();
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(mType.name()).append(" d").append(mDie);
        if(!matchesAll())
            sb.append(" on ").append(describeResults());

        return sb.toString();
    }

    public String toString(ThirdConfig config)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(mType.name());
        if(config.getActiveDice().size() > 1)
            sb.append(" d").append(mDie);

        if(!matchesAll())
            sb.append(" on ").append(describeResults());

        return sb.toString();
    }

    JSONObject toJSON()
    {
        JSONObject json = new JSONObject();
        try {
            json.put("die", mDie);
            json.put("type", mType.name());
            json.put("results", new JSONArray());
            if(!matchesAll())
            {
                for(int result: mResults)
                    json.accumulate("results", result);
            }
        }
        catch(JSONException e)
        {
        }

        return json;
    }

    private static class Range
    {
        int lower;
        int upper;

        Range()
        {
        }

        Range(int a, int b)
        {
            lower = a;
            upper = b;
            normalise();
        }

        private void normalise()
        {
            if(lower > upper)
            {
                int temp = upper;
                upper = lower;
                lower = temp;
            }
        }

        public String toString()
        {
            normalise();
            if(lower == upper)
                return String.valueOf(lower);
            else
                return String.format("%d-%d", lower, upper);
        }

        /*
         * Return the set of integers described by this range.
         */
        LinkedHashSet<Integer> getIntegers()
        {
            normalise();
            LinkedHashSet<Integer> set = new LinkedHashSet<>();
            for(int i = lower; i <= upper; i++)
                set.add(i);

            return set;
        }
    }

    class InfiniteLoop extends Exception {}
}
