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

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ThirdDb
{
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "presets";
    private SQLiteDatabase mDb;

    public ThirdDb(Context ctx)
    {
        ThirdDbOpener dbo = new ThirdDbOpener(ctx);
        mDb = dbo.getWritableDatabase();
    }

    public void close()
    {
        mDb.close();
    }

    public Cursor getAllProfiles()
    {
        return mDb.query("profile", null, null, null, null, null, "_id");
    }

    public Cursor getPresets(Integer profile)
    {
        return mDb.query("preset", null, "profile = ?",
                        new String[] {profile.toString()}, null, null, null);
    }

    public long addProfile(String name)
    {
        ContentValues vals = new ContentValues();
        vals.put("name", name);
        return mDb.insert("profile", "", vals);
    }

    public int renameProfile(int id, String name)
    {
        String[] args = new String[] {String.valueOf(id)};
        ContentValues vals = new ContentValues();
        vals.put("name", name);
        return mDb.update("profile", vals, "_id = ?", args);
    }

    public int deleteProfile(int id)
    {
        String[] args = new String[] {String.valueOf(id)};
        mDb.delete("preset", "profile = ?", args);
        return mDb.delete("profile", "_id = ?", args);
    }

    public long addPreset(int profile, ThirdConfig config)
    {
        ContentValues vals = config.getValues();
        vals.put("profile", profile);
        return mDb.insert("preset", "", vals);
    }

    public int renamePreset(int id, String name)
    {
        String[] args = new String[] {String.valueOf(id)};
        ContentValues vals = new ContentValues();
        vals.put("name", name);
        return mDb.update("preset", vals, "_id = ?", args);
    }

    public int updatePreset(int id, ThirdConfig config)
    {
        String[] args = new String[] {String.valueOf(id)};
        ContentValues vals = config.getValues();
        return mDb.update("preset", vals, "_id = ?", args);
    }

    public int deletePreset(int id)
    {
        String[] args = new String[] {String.valueOf(id)};
        return mDb.delete("preset", "_id = ?", args);
    }

    public class ThirdDbOpener extends SQLiteOpenHelper
    {
        private static final String INIT_PROFILE = 
            "CREATE TABLE profile (" +
            " _id INTEGER PRIMARY KEY AUTOINCREMENT," +
            " name TEXT NOT NULL); ";
        private static final String INIT_PRESET = 
            "CREATE TABLE preset (" +
            " _id INTEGER PRIMARY KEY AUTOINCREMENT," +
            " profile INT NOT NULL REFERENCES profile(_id)," +
            " name TEXT," +
            " d2 INTEGER NOT NULL," +
            " d4 INTEGER NOT NULL," +
            " d6 INTEGER NOT NULL," +
            " d8 INTEGER NOT NULL," +
            " d10 INTEGER NOT NULL," +
            " d12 INTEGER NOT NULL," +
            " d20 INTEGER NOT NULL," +
            " d100 INTEGER NOT NULL," +
            " dx INTEGER NOT NULL," +
            " dx_sides INTEGER," +
            " multiplier INTEGER NOT NULL," +
            " modifier INTEGER NOT NULL);";

        public ThirdDbOpener(Context ctx)
        {
            super(ctx, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(INIT_PROFILE);
            db.execSQL(INIT_PRESET);

            ContentValues def = new ContentValues();
            def.put("name", "Default");
            db.insert("profile", null, def);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int v1, int v2)
        {
            db.execSQL("DROP TABLE IF EXISTS preset;");
            db.execSQL("DROP TABLE IF EXISTS profile;");
            onCreate(db);
        }
    }
}
