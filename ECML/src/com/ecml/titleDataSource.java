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


	public class titleDataSource {

	  // Database fields
	  private SQLiteDatabase database;
	  private MySQLiteHelper dbHelper;
	  private String[] allColumns = { MySQLiteHelper.KEY_ID,
	      MySQLiteHelper.KEY_TITLE };

	  public titleDataSource(Context context) {
	    dbHelper = new MySQLiteHelper(context);
	  
	  }

	  public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	  }

	  public void close() {
	    dbHelper.close();
	  
	  }
	  
//	  public SQLiteDatabase getDatabase(){
//			return database;
//		}
	  

	  public title createTitle(String title) {
		  // Creation d'un content value:
	    ContentValues values = new ContentValues();
	    
	     // On lui ajoute des valeurs:
	    values.put(MySQLiteHelper.KEY_TITLE, title);
	    
	    // On insert l'objet dans la BDD via le ContentValues:
	    long insertId = database.insert(MySQLiteHelper.TABLE_TITLE, null,
	        values);
	    
	    Cursor cursor = database.query(MySQLiteHelper.TABLE_TITLE,
	        allColumns, MySQLiteHelper.KEY_ID + " = " + insertId, null,
	        null, null, null);
	    cursor.moveToFirst();
	    title newTitle = cursorToTitle(cursor);
	    cursor.close();
	    return newTitle;
	  }


	  public void deleteTitle(title title) {
	    long id = title.getId();
	    System.out.println("Comment deleted with id: " + id);
	    database.delete(MySQLiteHelper.TABLE_TITLE, MySQLiteHelper.KEY_ID
	        + " = " + id, null);
	  }

	  public List<title> getAllTitle() {
	    List<title> titles = new ArrayList<title>();

	    Cursor cursor = database.query(MySQLiteHelper.TABLE_TITLE,
	        allColumns, null, null, null, null, null);
	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	      title title = cursorToTitle(cursor);
	      titles.add(title);
	      cursor.moveToNext();
	    }
	    // make sure to close the cursor
	    cursor.close();
	    return titles;
	  }

	  private title cursorToTitle(Cursor cursor) {
	    title title = new title();
	    title.setId(cursor.getLong(0));
     title.setTitle(cursor.getString(1));
	    return title;
	  }
	  
	 
}
