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

class ThirdUtil
{
    /*
     * Append 'content' to the 'builder', separated with 'delimiter'.
     *
     * If this is the first item to be appended to the builder (i.e. it is
     * currently empty), do not insert the delimiter.
     */
    public static StringBuilder append(StringBuilder builder, String delimiter, Object content)
    {
        if(builder.length() > 0)
            return builder.append(delimiter).append(content);
        else
            return builder.append(content);
    }
}
