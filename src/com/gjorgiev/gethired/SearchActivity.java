package com.gjorgiev.gethired;

import com.gjorgiev.data.Query;
import com.gjorgiev.gethired.R;
import com.gjorgiev.gethired.adapter.CountriesListAdapter;
import com.gjorgiev.gethired.database.RecentSearchDataSource;
import com.gjorgiev.gethired.fragments.search.Search;
import com.gjorgiev.gethired.fragments.search.ShowNetworkSettings;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActivity extends FragmentActivity {
	Search search;
	View searchView;
	TextView txtKeyword;
	TextView txtLocation;
	Spinner country_spinner;
	String[] resourseList;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		getActionBar().setDisplayHomeAsUpEnabled(true);
    	searchView = findViewById(R.id.SearchFragment);
    	txtKeyword = (TextView) searchView.findViewById(R.id.txt_keyword);
    	txtLocation = (TextView) searchView.findViewById(R.id.txt_location);
    	
        ActionBar actionBar = getActionBar();
        // add the custom view to the action bar
        actionBar.setCustomView(R.layout.actionbar_view);
        country_spinner = (Spinner) actionBar.getCustomView().findViewById(R.id.country);
        actionBar.setDisplayShowCustomEnabled(true); 
        resourseList=this.getResources().getStringArray(R.array.CountryCodes);
        CountriesListAdapter adapter = new CountriesListAdapter(this, resourseList);
        adapter.setDropDownViewResource(R.layout.country_list_item);
        country_spinner.setAdapter(adapter);
        setFlag();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_USE_LOGO | ActionBar.DISPLAY_SHOW_HOME);

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		setFlag();
		super.onStart();
	}
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	private void setFlag(){
		String country = "US";
		RecentSearchDataSource datasource = new RecentSearchDataSource(this);
		datasource.open();
		Query query = datasource.getLastQuery();
		datasource.close();
		if(query != null)
			country = query.getCountry();
		country = country.toUpperCase();
        int position = 0;
        for (int i = 0; i < resourseList.length; i++) {
			if(resourseList[i].contains(country))
				position = i;
		}
        country_spinner.setSelection(position);
	}
	
	public void setFlagFromChild(String country){
		country = country.toUpperCase();
        int position = 0;
        for (int i = 0; i < resourseList.length; i++) {
			if(resourseList[i].contains(country))
				position = i;
		}
        country_spinner.setSelection(position);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case R.id.action_find:
	    	int position = country_spinner.getPositionForView(country_spinner.getSelectedView());
	    	String[] g=resourseList[position].split(",");
	    	String country = g[1].trim().toLowerCase();
	    	String keyword = (String) txtKeyword.getText().toString();
			String location = (String) txtLocation.getText().toString();
			
			if(isNetworkAvailable()){
				if(keyword.isEmpty()){
					alert("Enter keyword");
				}
				else{
					search = new Search(keyword, location, country, getApplicationContext(), this, false);
			    	search.execute();
				}
			}
			else{
				new ShowNetworkSettings(this, this).showNetworkSettingsAlert();
			}
			return true;
	    case R.id.action_favorites:
	    	Intent favoritesIntent = new Intent(getApplicationContext(), MainActivity.class);
	    	startActivity(favoritesIntent);
	    	return true;
	    }
	    return super.onOptionsItemSelected(item);
	    
	}
	
	private void goBack() {
		// TODO Auto-generated method stub
        Intent upIntent = NavUtils.getParentActivityIntent(this);
        if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
            // This activity is NOT part of this app's task, so create a new task
            // when navigating up, with a synthesized back stack.
            TaskStackBuilder.create(this)
                    // Add all of this activity's parents to the back stack
                    .addNextIntentWithParentStack(upIntent)
                    // Navigate up to the closest parent
                    .startActivities();
        } else {
            // This activity is part of this app's task, so simply
            // navigate up to the logical parent activity.
            NavUtils.navigateUpTo(this, upIntent);
        }
	}
	public void alert(String msg) {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
	}
	
	
}
