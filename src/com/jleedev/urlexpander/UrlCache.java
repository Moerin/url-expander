package com.jleedev.urlexpander;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import java.io.File;

public class UrlCache {
  private final UrlCacheOpenHelper helper;

  public UrlCache(Context context) {
    helper = new UrlCacheOpenHelper(context);
  }

  public Uri findUri(Uri key) {
    SQLiteDatabase db = helper.getReadableDatabase();
    String keyStr = key.toString();
    Cursor cursor = db.query("url_cache", new String[]{"long_url"}, "short_url = ?",
            new String[]{keyStr},
            null, null, null);
    Uri result;
    if (cursor.moveToFirst()) {
      result = Uri.parse(cursor.getString(0));
    } else {
      result = null;
    }
    db.close();
    return result;
  }

  public void putUri(Uri key, Uri value) {
    SQLiteDatabase db = helper.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put("short_url", key.toString());
    values.put("long_url", value.toString());
    db.insert("url_cache", null, values);
    db.close();
  }

  private static class UrlCacheOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = "UrlCacheOpenHelper";

    private static final String DATABASE_NAME = "db";
    private static final int DATABASE_VERSION = 3;
    private static final String URL_CACHE_TABLE_CREATE =
            "CREATE TABLE url_cache (short_url TEXT PRIMARY KEY NOT NULL, long_url TEXT NOT NULL);";

    public UrlCacheOpenHelper(Context context) {
      super(context, context.getCacheDir().getAbsolutePath() + File.separator + DATABASE_NAME, null,
              DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      db.execSQL(URL_CACHE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
              + newVersion + ", which will destroy all old data");
      db.execSQL("DROP TABLE IF EXISTS notes");
      onCreate(db);
    }
  }
}
