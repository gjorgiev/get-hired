package com.gjorgiev.gethired.adapter;

import java.util.ArrayList;

import com.gjorgiev.data.Job;
import com.gjorgiev.gethired.R;
import com.gjorgiev.gethired.database.FavoritesDataSource;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class JobAdapter extends ArrayAdapter<Job>{
	private ArrayList<Job> objects;
	private Context context;
	private ArrayList<Job> jobs;
	private FavoritesDataSource datasource;
	
	public JobAdapter(Context context, int textViewResourceId, ArrayList<Job> objects) {
		super(context, textViewResourceId, objects);
		this.objects = objects;
		this.context = context;
	}
	public View getView(int position, View convertView, ViewGroup parent){

		// assign the view we are converting to a local variable
		View v = convertView;

		// first check to see if the view is null. if so, we have to inflate it.
		// to inflate it basically means to render, or show, the view.
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.list_item, null);
		}

		/*
		 * Recall that the variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
		 * iterates through the list we sent it)
		 * 
		 * Therefore, i refers to the current Item object.
		 */
		Job i = objects.get(position);

		if (i != null) {

			// This is how you obtain a reference to the TextViews.
			// These TextViews are created in the XML files we defined.

			TextView txtJobTitle = (TextView) v.findViewById(R.id.jobTitle);
			TextView txtCompany = (TextView) v.findViewById(R.id.company);
			TextView txtLocation = (TextView) v.findViewById(R.id.location);
			final CheckBox star = (CheckBox)v.findViewById(R.id.chk_Favourites);
			TextView txtDateTime = (TextView) v.findViewById(R.id.datetime);
			
			//get attached data
			final String jobTitle = i.getJobTitle();
			final String location = i.getFormattedLocation();
			final String company = i.getCompany();
			final String snippet = i.getSnippet();
			final String url = i.getUrl();
			final Long datetime = i.getDatetime();
			CharSequence  datetime_formatted = DateUtils.getRelativeTimeSpanString(datetime, System.currentTimeMillis(),0L, DateUtils.FORMAT_ABBREV_ALL);
			//set the star
			final Job fav = getFavorite(url);
			if(fav == null)
				star.setChecked(false);
			else
				star.setChecked(true);
			
		    
		    star.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					datasource = new FavoritesDataSource(context);
				    datasource.open();
				    final Job fav = getFavorite(url);
				      // save the favorite job to the database
					if(fav == null){
					      datasource.addFavorites(jobTitle, company, location, snippet, url,datetime);
					      star.setChecked(true);
					}
					else{
						datasource.deleteJob(fav);
						star.setChecked(false);
					}
					datasource.close();
				}		
			});
			
			
			if (jobTitle != null){
				txtJobTitle.setText(jobTitle);
			}
			if (company != null){
				txtCompany.setText(company);
			}
			if (location != null){
				txtLocation.setText(location);
			}
			txtDateTime.setText(" - " + datetime_formatted);
			
		}

		// the view must be returned to our activity
		return v;
	}
	private Job getFavorite(String url) {
		FavoritesDataSource datasource;
	    datasource = new FavoritesDataSource(context);
	    datasource.open();
		jobs = datasource.getAllJobs();
		datasource.close();
		for (Job job : jobs) {
			if(url.equals(job.getUrl())){
				return job;
			}
		}
		return null;
	}
}
