package com.gjorgiev.gethired.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper{

	  public static final String TABLE_FAVORITES = "favorites";
	  public static final String TABLE_RECENT = "recent";
	  public static final String TABLE_RECENT_SEARCH = "recent_search";
	  public static final String COLUMN_ID = "_id";
	  public static final String COLUMN_JOBTITLE = "jobTitle";
	  public static final String COLUMN_COMPANY = "company";
	  public static final String COLUMN_LOCATION = "location";
	  public static final String COLUMN_SNIPPET = "snippet";
	  public static final String COLUMN_URL = "url";
	  public static final String COLUMN_DATETIME = "datetime";
	  
	  
	  //Fields for table recent_search
	  public static final String COLUMN_KEYWORD = "keyword";
	  public static final String COLUMN_TIMESTAMP = "timestamp";
	  public static final String COLUMN_COUNTRY = "country";
	  

	  private static final String DATABASE_NAME = "jobs.db";
	  private static final int DATABASE_VERSION = 1;
	
	  // Database creation sql statement
	  private static final String CREATE_FAVORITES = "create table "
	      + TABLE_FAVORITES + "(" + COLUMN_ID + " integer primary key autoincrement, " 
			  					+ COLUMN_JOBTITLE + " text not null, "
			  					+ COLUMN_COMPANY + " text not null, "
			  					+ COLUMN_LOCATION + " text not null, "
			  					+ COLUMN_URL + " text not null, "
			  					+ COLUMN_SNIPPET + " text not null, "
			  					+ COLUMN_DATETIME + " text not null);";
	  // Create RECENT table
	  private static final String CREATE_RECENT = "create table "
	      + TABLE_RECENT + "(" + COLUMN_ID + " integer primary key autoincrement, " 
			  					+ COLUMN_JOBTITLE + " text not null, "
			  					+ COLUMN_COMPANY + " text not null, "
			  					+ COLUMN_LOCATION + " text not null, "
			  					+ COLUMN_URL + " text not null, "
			  					+ COLUMN_SNIPPET + " text not null, "
			  					+ COLUMN_DATETIME + " text not null);";
	  
	  // Create recent_search table
	  private static final String CREATE_RECENT_SEARCH = "create table "
		      + TABLE_RECENT_SEARCH + "(" + COLUMN_ID + " integer primary key autoincrement, " 
				  					+ COLUMN_KEYWORD + " text not null, "
				  					+ COLUMN_LOCATION + " text not null, "
				  					+ COLUMN_TIMESTAMP + " text not null, "
				  					+ COLUMN_COUNTRY + " text not null);";
	  
	  public MySQLiteHelper(Context context) {
		    super(context, DATABASE_NAME, null, DATABASE_VERSION);
		  }

	  @Override
	  public void onCreate(SQLiteDatabase database) {
	    database.execSQL(CREATE_FAVORITES);
	    database.execSQL(CREATE_RECENT);
	    database.execSQL(CREATE_RECENT_SEARCH);
	  }

	  @Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(MySQLiteHelper.class.getName(),
	        "Upgrading database from version " + oldVersion + " to "
	            + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECENT);
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECENT_SEARCH);
	    onCreate(db);
	  }
}
