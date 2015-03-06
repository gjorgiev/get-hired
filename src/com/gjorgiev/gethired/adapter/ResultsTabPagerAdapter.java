package com.gjorgiev.gethired.adapter;
import com.gjorgiev.gethired.fragments.results.CBFragment;
import com.gjorgiev.gethired.fragments.results.GithubFragment;
import com.gjorgiev.gethired.fragments.results.IndeedFragment;
import com.gjorgiev.gethired.fragments.results.SimplyHiredFragment;
import com.gjorgiev.gethired.fragments.results.UsaJobsFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ResultsTabPagerAdapter extends FragmentPagerAdapter {

	public ResultsTabPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			// Indeed fragment 
			return new IndeedFragment();
		case 1:
			// CareerBuilder fragment 
			return new CBFragment();
		case 2:
			// SymplyHired fragment 
			return new SimplyHiredFragment();
		case 3:
			// UsaJobsFragment fragment 
			return new UsaJobsFragment();
		case 4:
			// GitHub fragment 
			return new GithubFragment();
		}

		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 5;
	}

}
