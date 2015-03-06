package com.gjorgiev.gethired;
import com.gjorgiev.data.Jobs;
import com.gjorgiev.gethired.R;
import com.gjorgiev.gethired.fragments.results.DetailFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ShareActionProvider;
import android.widget.Toast;

@SuppressLint("NewApi")
public class DetailActivity extends FragmentActivity{
	Jobs jobs;
	int pos;
	private DescAdapter adapter;
	private ViewPager pager;
	ShareActionProvider myShareActionProvider;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		// Get the feed object and the position from the Intent
		jobs = (Jobs) getIntent().getExtras().get("jobs");
		pos = getIntent().getExtras().getInt("pos");

		// Initialize the views
		adapter = new DescAdapter(getSupportFragmentManager());
		pager = (ViewPager) findViewById(R.id.pager);
		// Set Adapter to pager:
		pager.setAdapter(adapter);
		pager.setCurrentItem(pos);
		pager.setOnPageChangeListener(mOnPageChangeListener);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.detail_swipe, menu);
		MenuItem item = menu.findItem(R.id.menu_item_share);
		
		myShareActionProvider = (ShareActionProvider)item.getActionProvider();
		myShareActionProvider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
		myShareActionProvider.setShareIntent(createShareIntent(jobs.getItem(pager.getCurrentItem()).getUrl(), jobs.getItem(pager.getCurrentItem()).getJobTitle()));
		
		return super.onCreateOptionsMenu(menu);
	}
	
	private final OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {

	    @Override
	    public void onPageScrollStateChanged(int arg0) {
	        // TODO Auto-generated method stub
	    }

	    @Override
	    public void onPageScrolled(int arg0, float arg1, int arg2) {
	        // TODO Auto-generated method stub

	    }

	    @Override
	    public void onPageSelected(int arg0) {
	        // TODO Auto-generated method stub
	    	myShareActionProvider.setShareIntent(createShareIntent(jobs.getItem(pager.getCurrentItem()).getUrl(), jobs.getItem(pager.getCurrentItem()).getJobTitle()));
	    }

	};
	
	 private Intent createShareIntent(String url, String jobTitle) {
		  Intent shareIntent = new Intent(Intent.ACTION_SEND);
		        shareIntent.setType("text/plain");
		        shareIntent.putExtra(Intent.EXTRA_TITLE, jobTitle);
		        shareIntent.putExtra(Intent.EXTRA_TEXT, jobTitle + " " + url + " - from Get Hired mobile app");
		        return shareIntent;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	    	onBackPressed();
	        return true;
	    case R.id.action_next:
	    	if(isNetworkAvailable()){
		    	Intent fullDetail = new Intent(getApplicationContext(), FullDetailActivity.class);
				// sending data to new activity
				fullDetail.putExtra("url",jobs.getItem(pager.getCurrentItem()).getUrl());
				fullDetail.putExtra("jobTitle",jobs.getItem(pager.getCurrentItem()).getJobTitle());
				startActivity(fullDetail);
	    	} else {
	    		alert("Please turn on your Wi-Fi or Mobile Data");
	    	}
	        return true;
	    case R.id.menu_item_share:
			return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	public class DescAdapter extends FragmentStatePagerAdapter {
		public DescAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return jobs.getItemCount();
		}

		@Override
		public Fragment getItem(int position) {
			DetailFragment frag = new DetailFragment();

			Bundle bundle = new Bundle();
			bundle.putSerializable("jobs", jobs);
			bundle.putInt("pos", position);
			frag.setArguments(bundle);
			return frag;
		}

	}
	public void alert(String msg) {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
	}
}
