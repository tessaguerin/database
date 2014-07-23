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
	  public static final String KEY_ID = "_id";
	  public static final String KEY_TITLE = "title";

	  public static final String TABLE_COMPOSER = "composer";
	  public static final String KEY_ID_COMPOSER = "id";
	  public static final String KEY_NAME_COMPOSER = "name_composer";

	  public static final String TABLE_DIFFICULTY = "difficulty";
	  public static final String KEY_ID_DIFFICULTY = "id";
	  public static final String KEY_NOTE_DIFFICULTY = "note";
	  
	 
	  private static final String DATABASE_NAME = "Mydb";
	  private static final int DATABASE_VERSION = 1;

	  // Database creation sql statement
	  private static final String DATABASE_CREATE = "create table "
	      + TABLE_TITLE + "(" + KEY_ID
	      + " integer PRIMARY KEY AUTOINCREMENT, " + KEY_TITLE
	      + " text not null);";
	  
	  private static final String DATABASE_CREATE_COMPOSER = "create table "
		      + TABLE_COMPOSER + "(" + KEY_ID
		      + " integer PRIMARY KEY AUTOINCREMENT, " + KEY_NAME_COMPOSER
		      + " text not null);";
	  
	  private static final String DATABASE_CREATE_DIFFICULTY = "create table "
		      + TABLE_DIFFICULTY + "(" + KEY_ID
		      + " integer PRIMARY KEY AUTOINCREMENT, " + KEY_NOTE_DIFFICULTY
		      + " int not null);";

	  public MySQLiteHelper(Context context) {
		  super(context, DATABASE_NAME, null, DATABASE_VERSION);
	  }

	  public void onCreate(SQLiteDatabase database) {
	    database.execSQL(DATABASE_CREATE);
	    database.execSQL(DATABASE_CREATE_COMPOSER);
	    database.execSQL(DATABASE_CREATE_DIFFICULTY);
	  }

	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(MySQLiteHelper.class.getName(),
	        "Upgrading database from version " + oldVersion + " to "
	            + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_TITLE);
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPOSER);
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_DIFFICULTY);
	    onCreate(db);
	    
	    db.execSQL("INSERT INTO" + TABLE_COMPOSER + "VALUES('Bach');");
	  }


	  
	  public void addTitle (title title){
		  
		  // 1. get reference to writable DB
//          SQLiteDatabase db = this.getWritableDatabase();
          String countQuery = "SELECT * FROM " + TABLE_TITLE;
        		  SQLiteDatabase db = this.getReadableDatabase();
        		  Cursor cursor = db.rawQuery(countQuery, null);
        		  cursor.moveToFirst();
//           db.query("title", new String[]{
//        		   "_iD",
//        		   "title"
//           }, null, null, null, null, null, null);
		  
//		  Cursor c= db.rawQuery("SELECT * FROM" + TABLE_COMPOSER, null);
//		  c.moveToFirst();
//		  Log.d("Bach", c.getString(c.getColumnIndex("name")));
   
  // 1. get reference to writable DB
//        SQLiteDatabase db = this.getWritableDatabase();

  // 2. create ContentValues to add key "column"/value
//  ContentValues values = new ContentValues();
//  values.put(COLUMN_TITLE, title.getTitle()); // get title
 

  // 3. insert
//  db.insert(TABLE_TITLE, // table
//        null, //nullColumnHack
//          values); // key/value -> keys = column names/ values = column values

     // 4. close
 db.close();
 }


}

