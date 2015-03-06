package com.gjorgiev.gethired.fragments.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.gjorgiev.gethired.ResultsActivity;
import com.gjorgiev.gethired.database.RecentSearchDataSource;

public class Search {
	String indeed_url = "";
	String github_url = "";
	String cb_url = "";
	String sh_url = "";
	String usa_jobs_url = "";
	String keyword;
	String location;
	String country;
	Context context;
	Activity activity;
	Boolean recent;
	RecentSearchDataSource datasource;
	
	public Search(String keyword, String location, String country, Context context, Activity activity, Boolean recent) {
				this.keyword = keyword;
				this.location = location;
				this.country = country;
				this.context = context;
				this.activity = activity;
				this.recent = recent;
		}
	
	public void execute(){
		Intent searchResults = new Intent(context, ResultsActivity.class);
		String keywordTemp = keyword;
		String locationTemp = location;
		keyword = keywordTemp.replace(' ', '+');
		location = locationTemp.replace(' ', '+');
		indeed_url += "&q=" + keyword + "&l=" + location + "&co=" + country;
		github_url += "description=" + keyword + "&location=" + location;
		//Careerbuilder url
		if(country.equals("gb"))
			cb_url += "&Keywords=" + keyword + "&Location" + location + "&CountryCode=uk";
		else
			cb_url += "&Keywords=" + keyword + "&Location" + location + "&CountryCode=" + country;
		//Simplyhired url
		if(country.equals("us"))
			searchResults.putExtra("country","com");
		else
			if(country.equals("gb"))
				searchResults.putExtra("country","co.uk");
		else
			searchResults.putExtra("country",country);
		
		sh_url += "q-" + keyword + "&l=" + location;
		
		usa_jobs_url += "?Keyword=" + keyword + "&LocationName=" + location;	
		// sending data to new activity
		searchResults.putExtra("indeed_url",indeed_url);
		searchResults.putExtra("github_url",github_url);
		searchResults.putExtra("cb_url",cb_url);
		searchResults.putExtra("sh_url",sh_url);
		searchResults.putExtra("usa_jobs_url",usa_jobs_url);
		searchResults.putExtra("location",location);
		if(!recent){
		    datasource = new RecentSearchDataSource(activity);
		    datasource.open();
		    datasource.addRecentSearch(keywordTemp, locationTemp, country);
		    datasource.close();	
		}
		activity.startActivity(searchResults);
	}
}
