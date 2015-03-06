package com.gjorgiev.gethired.adapter;
import com.gjorgiev.fragments_home.FavoritesFragment;
import com.gjorgiev.fragments_home.RecentFragment;
import com.gjorgiev.fragments_home.TopRatedFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			// Favorites fragment activity
			return new FavoritesFragment();
		case 1:
			// Recent fragment activity
			return new RecentFragment();
		}

		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 2;
	}

}
