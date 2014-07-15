package com.ecml;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

;


// This class is responsible for creating the DB
public class MySQLiteHelper extends SQLiteOpenHelper {
	
	  public static final String TABLE_TITLE = "title";
	  public static final String COLUMN_ID = "_id";
	  public static final String COLUMN_TITLE = "title";
	  
//	  public static final String TABLE_COMPOSER = "composer";
//	  public static final String COLUMN_NAME = "name";

	  private static final String DATABASE_NAME = "Mydb";
	  private static final int DATABASE_VERSION = 1;

	  // Database creation sql statement
	  private static final String DATABASE_CREATE = "create table "
	      + TABLE_TITLE + "(" + COLUMN_ID
	      + " integer PRIMARY KEY AUTOINCREMENT, " + COLUMN_TITLE
	      + " text not null);";
	  
//	  private static final String DATABASE_CREATE_COMPOSER = "create table "
//		      + TABLE_COMPOSER + "(" + COLUMN_NAME
//		      + " integer PRIMARY KEY AUTOINCREMENT, ";

	  public MySQLiteHelper(Context context) {
		  super(context, DATABASE_NAME, null, DATABASE_VERSION);
	  }

	  public void onCreate(SQLiteDatabase database) {
	    database.execSQL(DATABASE_CREATE);
//	    database.execSQL(DATABASE_CREATE_COMPOSER);
	  }

	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(MySQLiteHelper.class.getName(),
	        "Upgrading database from version " + oldVersion + " to "
	            + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_TITLE);
//	    db.execSQL("INSERT INTO composer VALUES ('Bach');");
//	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPOSER);
	    onCreate(db);
	  }
	  
//	  public void addTitle (title title){
		  
		  // 1. get reference to writable DB
//          SQLiteDatabase db = this.getWritableDatabase();
		  
//		  Cursor c= db.rawQuery("SELECT * FROM composer", null);
//		  c.moveToFirst();
//		  Log.d("Bach", c.getString(c.getColumnIndex("name")));
   
  // 1. get reference to writable DB
 //         SQLiteDatabase db = this.getWritableDatabase();

  // 2. create ContentValues to add key "column"/value
//  ContentValues values = new ContentValues();
//  values.put(KEY_TITLE, title.getTitle()); // get title
 

  // 3. insert
//  db.insert(TABLE_TITLE, // table
 //        null, //nullColumnHack
//          values); // key/value -> keys = column names/ values = column values

  // 4. close
 // db.close();
//}

	// Books Table Columns names
//	    private static final String KEY_ID = "_id";
//	    private static final String KEY_TITLE = "title";
	
	 
//	    private static final String[] COLUMNS = {KEY_ID,KEY_TITLE};
	 
	    
//	    public title getTitle(int id){
	 
	        // 1. get reference to readable DB
//	        SQLiteDatabase db = this.getReadableDatabase();
	 
	        // 2. build query
//	        Cursor cursor =
//	                db.query(TABLE_TITLE	, // a. table
//	                COLUMNS, // b. column names
//	                " id = ?", // c. selections
//	                new String[] { String.valueOf(id) }, // d. selections args
//	                null, // e. group by
//	                null, // f. having
//	                null, // g. order by
//	                null); // h. limit
	 
	        // 3. if we got results get the first one
//	        if (cursor != null)
//	            cursor.moveToFirst();
	 
	        // 4. build book object
//	        title title = new title();
//	        title.setId(Integer.parseInt(cursor.getString(0)));
//	        title.setTitle(cursor.getString(1));

	 
	        // 5. return book
//	        return title;
//	    }

}

