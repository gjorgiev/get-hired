package com.gjorgiev.gethired.fragments.results;
import java.util.ArrayList;

import com.gjorgiev.data.Job;
import com.gjorgiev.data.Jobs;
import com.gjorgiev.gethired.FullDetailActivity;
import com.gjorgiev.gethired.R;
import com.gjorgiev.gethired.database.FavoritesDataSource;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class DetailFragment extends Fragment{
	private int fPos;
	Jobs fJobs;
	private FavoritesDataSource datasource;
	private ArrayList<Job> jobs;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fJobs = (Jobs) getArguments().getSerializable("jobs");
		fPos = getArguments().getInt("pos");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.detail_fragment, container, false);

		TextView txtJobTitle = (TextView)view.findViewById(R.id.jobTitleDetails);
		TextView txtCompany = (TextView)view.findViewById(R.id.companyDetails);
		TextView txtLocation = (TextView)view.findViewById(R.id.locationDetails);
		TextView txtDateTime = (TextView)view.findViewById(R.id.datetimeDetails);
		WebView wvSnippet = (WebView)view.findViewById(R.id.snippetDetails);
		final CheckBox star = (CheckBox)view.findViewById(R.id.chk_FavouritesDetails);
		final Button btnApply = (Button)view.findViewById(R.id.btn_apply);
		String mime = "text/html";
		String encoding = "utf-8";
		
		//get attached data
		final String jobTitle = fJobs.getItem(fPos).getJobTitle();
		final String location = fJobs.getItem(fPos).getFormattedLocation();
		final String company = fJobs.getItem(fPos).getCompany();
		final String snippet = fJobs.getItem(fPos).getSnippet();
		final String url = fJobs.getItem(fPos).getUrl();
		final Long datetime = fJobs.getItem(fPos).getDatetime();
		CharSequence  datetime_formatted = DateUtils.getRelativeTimeSpanString(datetime, System.currentTimeMillis(),0L, DateUtils.FORMAT_ABBREV_ALL);		
		// Set the views
		txtJobTitle.setText(jobTitle);
		txtLocation.setText(location);
		txtCompany.setText(company);
		txtDateTime.setText(" - " + datetime_formatted);
		String content = "<body style='margin: 0; padding: 0'>";
		content += snippet;
		wvSnippet.getSettings().setJavaScriptEnabled(true);
		wvSnippet.setPadding(0, 0, 0, 0);
		wvSnippet.loadDataWithBaseURL(null, content, mime, encoding, null);
		
		//set the star
		final Job fav = getFavorite(url);
		if(fav == null)
			star.setChecked(false);
		else
			star.setChecked(true);
		
	    
	    star.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				datasource = new FavoritesDataSource(getActivity());
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
	    
		btnApply.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isNetworkAvailable()){
					Intent fullDetail = new Intent(getActivity().getApplicationContext(), FullDetailActivity.class);
						// sending data to new activity
						fullDetail.putExtra("url",url);
						fullDetail.putExtra("jobTitle",jobTitle);
						startActivity(fullDetail);
				} else {
					alert("Please turn on your Wi-Fi or Mobile Data");
				}
			}		
		});
		return view;
	}
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	private Job getFavorite(String url) {
		FavoritesDataSource datasource;
	    datasource = new FavoritesDataSource(getActivity());
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
	
	public void alert(String msg) {
		// TODO Auto-generated method stub
		Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_LONG).show();
	}
}
