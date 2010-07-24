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
    private SQLiteDatabase db;

    public ThirdDb(Context ctx)
    {
        ThirdDbOpener dbo = new ThirdDbOpener(ctx);
        db = dbo.getWritableDatabase();
    }

    public void close()
    {
        db.close();
    }

    public Cursor getAllProfiles()
    {
        return db.query("profile", null, null, null, null, null, "_id");
    }

    public Cursor getPresets(Integer profile)
    {
        return db.query("preset", null, "profile = ?",
                        new String[] {profile.toString()}, null, null, null);
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
