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

import java.util.LinkedHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class ThirdProfile
{
    private String mName;
    private final LinkedHashMap<Integer, ThirdConfig> mPresets = new LinkedHashMap<>();

    ThirdProfile(JSONObject json)
    {
        mName = json.optString("name");

        JSONArray presets;
        presets = json.optJSONArray("presets");
        if(presets == null)
        {
            presets = new JSONArray();
        }

        SparseIntArray map = new SparseIntArray();
        for(int i = 0; i < presets.length(); i++)
        {
            JSONObject preset_json = presets.optJSONObject(i);
            ThirdConfig preset = new ThirdConfig(preset_json);
            mPresets.put(preset.getId(), preset);

            JSONArray includes = preset_json.optJSONArray("includes");
            for(int j = 0; j < includes.length(); j++)
                map.put(preset.getId(), includes.optInt(j));
        }

        /*
         * Now that we have accumulated all of the presets, resolve references
         * to inclusions.
         */
        for(int i = 0; i < map.size(); i++)
        {
            ThirdConfig preset = mPresets.get(map.keyAt(i));
            ThirdConfig include = mPresets.get(map.valueAt(i));

            if(preset != null && include != null)
                preset.addInclude(include);
        }
    }

    ThirdProfile(String name)
    {
        mName = name;
    }

    JSONObject toJSON()
    {
        JSONObject json = new JSONObject();
        try {
            json.put("name", mName);
            json.put("presets", new JSONArray());
            for(ThirdConfig preset: mPresets.values())
                json.accumulate("presets", preset.toJSON());
        }
        catch(JSONException e)
        {
        }
        return json;
    }

    String getName()
    {
        return mName;
    }

    void setName(String name)
    {
        mName = name;
    }

    @Override
    public String toString()
    {
        return mName;
    }

    LinkedHashMap<Integer, ThirdConfig> getPresets()
    {
        return mPresets;
    }

    int getNewId()
    {
        int id = 0;
        while(mPresets.containsKey(id))
            id++;
        return id;
    }

    ThirdConfig getPreset(int id){
        return mPresets.get(id);
    }

    ThirdConfig createPreset(String name)
    {
        int id = getNewId();
        ThirdConfig preset = new ThirdConfig(id, name);
        mPresets.put(id, preset);
        return preset;
    }

    ThirdConfig createPreset(ThirdConfig config)
    {
        int id = getNewId();
        ThirdConfig preset = new ThirdConfig(id, config);
        mPresets.put(id, preset);
        return preset;
    }

    ThirdConfig removePreset(int id)
    {
        for(ThirdConfig preset: mPresets.values())
        {
            preset.removeInclude(id);
        }
        return mPresets.remove(id);
    }

    void addInclude(int preset_id, int include_id)
    {
        if(preset_id == include_id)
            return;

        ThirdConfig preset = mPresets.get(preset_id);
        ThirdConfig include = mPresets.get(include_id);

        if(preset != null && include != null)
            preset.addInclude(include);
    }
}
