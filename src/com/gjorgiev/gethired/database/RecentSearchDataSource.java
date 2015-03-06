package com.gjorgiev.gethired.database;
import java.util.ArrayList;

import com.gjorgiev.data.Query;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class RecentSearchDataSource {
	  // Database fields
	  private SQLiteDatabase database;
	  private MySQLiteHelper dbHelper;
	  private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
	      MySQLiteHelper.COLUMN_KEYWORD, MySQLiteHelper.COLUMN_LOCATION, MySQLiteHelper.COLUMN_TIMESTAMP, MySQLiteHelper.COLUMN_COUNTRY};
	  
	  public RecentSearchDataSource(Context context) {
		    dbHelper = new MySQLiteHelper(context);
	  		}
	  public void open() throws SQLException {
		    database = dbHelper.getWritableDatabase();
	  		}
	  public void close() {
		    dbHelper.close();
		  }
	  @SuppressLint("SimpleDateFormat")
	public Query addRecentSearch(String keyword, String location, String country) {
		  	long now = System.currentTimeMillis();
		    ContentValues values = new ContentValues();
		    values.put(MySQLiteHelper.COLUMN_KEYWORD, keyword);
		    values.put(MySQLiteHelper.COLUMN_LOCATION, location);
		    values.put(MySQLiteHelper.COLUMN_TIMESTAMP, now);
		    values.put(MySQLiteHelper.COLUMN_COUNTRY, country);
		    long insertId = database.insert(MySQLiteHelper.TABLE_RECENT_SEARCH, null,
		        values);
		    Cursor cursor = database.query(MySQLiteHelper.TABLE_RECENT_SEARCH,
		        allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
		        null, null, null);
		    cursor.moveToFirst();
		    Query newJob = cursorToQuery(cursor);
		    cursor.close();
		    return newJob;
		  }
	  private Query cursorToQuery(Cursor cursor) {
		  	Query query = new Query(cursor.getString(1), cursor.getString(2),cursor.getString(4), cursor.getLong(3));
		    query.setId(cursor.getLong(0));
		    return query;
		  }
	  public void deleteQuery(Query query) {
		    long id = query.getId();
		    System.out.println("Query deleted with id: " + id);
		    database.delete(MySQLiteHelper.TABLE_RECENT_SEARCH, MySQLiteHelper.COLUMN_ID
		        + " = " + id, null);
		  }
	  public ArrayList<Query> getAllQueries() {
		  ArrayList<Query> queries = new ArrayList<Query>();

		    Cursor cursor = database.query(MySQLiteHelper.TABLE_RECENT_SEARCH,
		        allColumns, null, null, null, null, null);

		    cursor.moveToLast();
		    while (!cursor.isBeforeFirst()) {
		    	Query query = cursorToQuery(cursor);
		    	queries.add(query);
		    	cursor.moveToPrevious();
		    }
		    // make sure to close the cursor
		    cursor.close();
		    return queries;
		  }
	  public Query getLastQuery(){
		  Query query = null;
		  Cursor cursor = database.query(MySQLiteHelper.TABLE_RECENT_SEARCH,
			        allColumns, null, null, null, null, null);
		  cursor.moveToLast();
		  if(!cursor.isBeforeFirst())
		  		query = cursorToQuery(cursor);
		  cursor.close();
		  return query;  
	  }
}
