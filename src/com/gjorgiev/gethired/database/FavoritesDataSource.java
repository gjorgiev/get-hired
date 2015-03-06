package com.gjorgiev.gethired.database;

import java.util.ArrayList;

import com.gjorgiev.data.Job;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class FavoritesDataSource {
	  // Database fields
	  private SQLiteDatabase database;
	  private MySQLiteHelper dbHelper;
	  private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
	      MySQLiteHelper.COLUMN_JOBTITLE, MySQLiteHelper.COLUMN_COMPANY, MySQLiteHelper.COLUMN_LOCATION, MySQLiteHelper.COLUMN_SNIPPET, MySQLiteHelper.COLUMN_URL, MySQLiteHelper.COLUMN_DATETIME};
	  
	  public FavoritesDataSource(Context context) {
		    dbHelper = new MySQLiteHelper(context);
	  		}
	  public void open() throws SQLException {
		    database = dbHelper.getWritableDatabase();
	  		}
	  public void close() {
		    dbHelper.close();
		  }
	  public Job addFavorites(String jobTitle, String company, String location, String snippet, String url, Long datetime) {
		    ContentValues values = new ContentValues();
		    values.put(MySQLiteHelper.COLUMN_JOBTITLE, jobTitle);
		    values.put(MySQLiteHelper.COLUMN_COMPANY, company);
		    values.put(MySQLiteHelper.COLUMN_LOCATION, location);
		    values.put(MySQLiteHelper.COLUMN_SNIPPET, snippet);
		    values.put(MySQLiteHelper.COLUMN_URL, url);
		    values.put(MySQLiteHelper.COLUMN_DATETIME, datetime);
		    long insertId = database.insert(MySQLiteHelper.TABLE_FAVORITES, null,
		        values);
		    Cursor cursor = database.query(MySQLiteHelper.TABLE_FAVORITES,
		        allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
		        null, null, null);
		    cursor.moveToFirst();
		    Job newJob = cursorToJob(cursor);
		    cursor.close();
		    return newJob;
		  }
	  private Job cursorToJob(Cursor cursor) {
		    Job job = new Job(cursor.getString(1), cursor.getString(4), cursor.getString(3), cursor.getString(2),cursor.getString(5),cursor.getLong(6));
		    job.setId(cursor.getLong(0));
		    return job;
		  }
	  public void deleteJob(Job job) {
		    long id = job.getId();
		    System.out.println("Job deleted with id: " + id);
		    database.delete(MySQLiteHelper.TABLE_FAVORITES, MySQLiteHelper.COLUMN_ID
		        + " = " + id, null);
		  }
	  public ArrayList<Job> getAllJobs() {
		  ArrayList<Job> jobs = new ArrayList<Job>();

		    Cursor cursor = database.query(MySQLiteHelper.TABLE_FAVORITES,
		        allColumns, null, null, null, null, null);

		    cursor.moveToLast();
		    while (!cursor.isBeforeFirst()) {
		      Job job = cursorToJob(cursor);
		      jobs.add(job);
		      cursor.moveToPrevious();
		    }
		    // make sure to close the cursor
		    cursor.close();
		    return jobs;
		  }
}
