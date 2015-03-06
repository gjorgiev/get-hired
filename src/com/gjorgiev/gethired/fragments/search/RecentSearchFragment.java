package com.gjorgiev.gethired.fragments.search;

import java.util.ArrayList;

import com.gjorgiev.data.Query;
import com.gjorgiev.gethired.R;
import com.gjorgiev.gethired.adapter.QueryAdapter;
import com.gjorgiev.gethired.database.RecentSearchDataSource;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class RecentSearchFragment extends ListFragment{
	private RecentSearchDataSource datasource;
	private QueryAdapter query_adapter;
	private ArrayList<Query> queries;
	Search search;
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		updateRecentSearch();
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		String keyword = (String) queries.get(position).getKeyword();
		String location = (String) queries.get(position).getLocation();
		String country = (String) queries.get(position).getCountry();
		datasource.open();
		datasource.addRecentSearch(keyword, location, country);
		datasource.deleteQuery(queries.get(position));
		datasource.close();
		query_adapter.notifyDataSetChanged();
		if(isNetworkAvailable()){
			if(keyword.isEmpty()){
				alert("Enter keyword");
			}
			else{
				search = new Search(keyword, location, country, getActivity().getApplicationContext(), getActivity(),true);
		    	search.execute();
			}
		}
		else{
			new ShowNetworkSettings(getActivity(), getActivity()).showNetworkSettingsAlert();
		}
	}
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	public void alert(String msg) {
		// TODO Auto-generated method stub
		Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		updateRecentSearch();
	}
	
	private void updateRecentSearch(){
	    datasource = new RecentSearchDataSource(getActivity());
	    datasource.open();

	    this.queries = datasource.getAllQueries();
	    datasource.close();
    	query_adapter = new QueryAdapter(getActivity(), R.layout.query_item, queries);
    	setListAdapter(query_adapter);
	}
}
