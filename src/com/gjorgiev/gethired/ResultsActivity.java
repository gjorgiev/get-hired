package com.gjorgiev.gethired;

import com.gjorgiev.gethired.R;
import com.gjorgiev.gethired.adapter.ResultsTabPagerAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.mediation.admob.AdMobExtras;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class ResultsActivity extends FragmentActivity implements
		ActionBar.TabListener {

	private ViewPager viewPager;
	private ResultsTabPagerAdapter mAdapter;
	private ActionBar actionBar;
	// Tab titles
	private String[] tabs = { "Indeed", "CareerBuilder", "SymplyHired","UsaJobs", "Github" };
	private AdView adView;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results);

		// Initilization
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();
		mAdapter = new ResultsTabPagerAdapter(getSupportFragmentManager());

		viewPager.setAdapter(mAdapter);
		viewPager.setOffscreenPageLimit(9);
		//viewPager.setPageTransformer(true, new DepthPageTransformer());
		actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		//getParent().finish();
		// Adding Tabs
		for (String tab_name : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tab_name)
					.setTabListener(this));
		}

		/**
		 * on swiping the viewpager make respective tab selected
		 * */
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// make respected tab selected
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		
		
		
		// Look up the AdView as a resource and load a request.
	    adView = (AdView)this.findViewById(R.id.ad);
	    // add color parameters 
	    Bundle bundle = new Bundle();
	    bundle.putString("color_bg", "0099cc");
	    bundle.putString("color_bg_top", "FFFFFF");
	    bundle.putString("color_border", "FFFFFF");
	    bundle.putString("color_link", "FFFFFF");
	    bundle.putString("color_text", "FFFFFF");
	    bundle.putString("color_url", "FFFFFF");

	    AdMobExtras extras = new AdMobExtras(bundle);
	    AdRequest adRequest = new AdRequest.Builder()
	    	.addNetworkExtras(extras)
	    	.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
	    	.addTestDevice("C6A922E741DAFAF3BBE57B63F89AF4D2")
	    	.build();
	    adView.loadAd(adRequest);
	    

	    
	}
	
	@Override
	  public void onResume() {
	    super.onResume();
	    if (adView != null) {
	      adView.resume();
	    }
	  }

	  @Override
	  public void onPause() {
	    if (adView != null) {
	      adView.pause();
	    }
	    super.onPause();
	  }

	  /** Called before the activity is destroyed. */
	  @Override
	  public void onDestroy() {
	    // Destroy the AdView.
	    if (adView != null) {
	      adView.destroy();
	    }
	    super.onDestroy();
	  }

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// on tab selected
		// show respected fragment view
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.results_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	    	onBackPressed();
	        return true;
	    case R.id.action_search:
	    	goBack();
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
