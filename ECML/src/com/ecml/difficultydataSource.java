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


	public class difficultydataSource {

	  // Database fields
	  private SQLiteDatabase database;
	  private MySQLiteHelper dbHelper;
	  private String[] allKeys = { MySQLiteHelper.KEY_ID_DIFFICULTY,
	      MySQLiteHelper.KEY_NOTE_DIFFICULTY };

	  public difficultydataSource(Context context) {
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
			Cursor c = 	database.query(true, MySQLiteHelper.TABLE_DIFFICULTY, allKeys, 
								where, null, null, null, null, null);
			if (c != null) {
				c.moveToFirst();
			}
			return c;
		}

		// Get a specific row (by rowId)
		public Cursor getRow(long rowId) {
			String where = MySQLiteHelper.KEY_ID_DIFFICULTY + "=" + rowId;
			Cursor c = 	database.query(true, MySQLiteHelper.TABLE_DIFFICULTY, allKeys, 
							where, null, null, null, null, null);
			if (c != null) {
				c.moveToFirst();
			}
			return c;
		}
	  
//	  public SQLiteDatabase getDatabase(){
//			return database;
//		}
	  

		public void insertData(String note) {
			ContentValues cv = new ContentValues();
			cv.put(MySQLiteHelper.KEY_NOTE_DIFFICULTY, note);
			database.insert(MySQLiteHelper.TABLE_COMPOSER, null, cv);
		}
		
		public Cursor readData() {
			 String[] allColumns = new String[] { MySQLiteHelper.KEY_ID,MySQLiteHelper.KEY_NOTE_DIFFICULTY };
			 Cursor c = database.query(MySQLiteHelper.TABLE_DIFFICULTY, allColumns, null,null, null, null, null);
			 if (c != null) {
			  c.moveToFirst();
			 }
			 return c;
			}

		public int updateData(long composerID, int note_difficulty) {
			ContentValues cvUpdate = new ContentValues();
			cvUpdate.put(MySQLiteHelper.KEY_NOTE_DIFFICULTY, note_difficulty);
			int i = database.update(MySQLiteHelper.TABLE_DIFFICULTY, cvUpdate,
					MySQLiteHelper.KEY_ID + " = " + composerID, null);
			return i;
		}


		public void deleteComposer(long difficultyID) {
			database.delete(MySQLiteHelper.TABLE_DIFFICULTY, MySQLiteHelper.KEY_ID + "="
					+ difficultyID, null);
		}


	  public List<difficulty> getAllDifficulty() {
		    List<difficulty> difficulties = new ArrayList<difficulty>();

		    Cursor cursor = database.query(MySQLiteHelper.TABLE_DIFFICULTY,
		        allKeys, null, null, null, null, null);
		    cursor.moveToFirst();
		    while (!cursor.isAfterLast()) {
		      difficulty difficulty = cursorToDifficulty(cursor);
		      difficulties.add(difficulty);
		      cursor.moveToNext();
		    }
		    // make sure to close the cursor
		    cursor.close();
		    return difficulties;
		  }


	  private difficulty cursorToDifficulty(Cursor cursor) {
	    difficulty difficulty = new difficulty();
	    difficulty.setId(cursor.getLong(0));
     difficulty.setNoteDifficulty(cursor.getString(1));
	    return difficulty;
	  }
	
//	  public SQLiteDatabase getDatabase(){
//			return database;
//		}
	
	}