package com.ecml;


	import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

	import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


	public class composerDataSource {

	  // Database fields
	  private SQLiteDatabase database;
	  private MySQLiteHelper dbHelper;
	  private String[] allKeys = { MySQLiteHelper.KEY_ID_COMPOSER,
	      MySQLiteHelper.KEY_NAME_COMPOSER };

	  public composerDataSource(Context context) {
	    dbHelper = new MySQLiteHelper(context);
	  
	  }

	  public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	  }

	  public void close() {
	    dbHelper.close();
	  
	  }
		// Return all data in the database.
		public Cursor getAllRows() {
			String where = null;
			Cursor c = 	database.query(true, MySQLiteHelper.TABLE_COMPOSER, allKeys, 
								where, null, null, null, null, null);
			if (c != null) {
				c.moveToFirst();
			}
			return c;
		}

		// Get a specific row (by rowId)
		public Cursor getRow(long rowId) {
			String where = MySQLiteHelper.KEY_ID_COMPOSER + "=" + rowId;
			Cursor c = 	database.query(true, MySQLiteHelper.TABLE_COMPOSER, allKeys, 
							where, null, null, null, null, null);
			if (c != null) {
				c.moveToFirst();
			}
			return c;
		}
	  
//	  public SQLiteDatabase getDatabase(){
//			return database;
//		}
	  

		public void insertData(String name) {
			ContentValues cv = new ContentValues();
			cv.put(MySQLiteHelper.KEY_NAME_COMPOSER, name);
			database.insert(MySQLiteHelper.TABLE_COMPOSER, null, cv);
		}
		
		public Cursor readData() {
			 String[] allColumns = new String[] { MySQLiteHelper.KEY_ID,MySQLiteHelper.KEY_NAME_COMPOSER };
			 Cursor c = database.query(MySQLiteHelper.TABLE_COMPOSER, allColumns, null,null, null, null, null);
			 if (c != null) {
			  c.moveToFirst();
			 }
			 return c;
			}

		public int updateData(long composerID, String composerName) {
			ContentValues cvUpdate = new ContentValues();
			cvUpdate.put(MySQLiteHelper.KEY_NAME_COMPOSER, composerName);
			int i = database.update(MySQLiteHelper.TABLE_COMPOSER, cvUpdate,
					MySQLiteHelper.KEY_ID + " = " + composerID, null);
			return i;
		}


		public void deleteComposer(long composerID) {
			database.delete(MySQLiteHelper.TABLE_COMPOSER, MySQLiteHelper.KEY_ID + "="
					+ composerID, null);
		}


	  public List<composer> getAllComposer() {
		    List<composer> composers = new ArrayList<composer>();

		    Cursor cursor = database.query(MySQLiteHelper.TABLE_COMPOSER,
		        allKeys, null, null, null, null, null);
		    cursor.moveToFirst();
		    while (!cursor.isAfterLast()) {
		      composer composer = cursorToComposer(cursor);
		      composers.add(composer);
		      cursor.moveToNext();
		    }
		    // make sure to close the cursor
		    cursor.close();
		    return composers;
		  }


	  private composer cursorToComposer(Cursor cursor) {
	    composer composer = new composer();
	    composer.setId(cursor.getLong(0));
     composer.setNameComposer(cursor.getString(1));
	    return composer;
	  }
	}