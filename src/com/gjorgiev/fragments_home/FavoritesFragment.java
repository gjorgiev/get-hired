package com.gjorgiev.fragments_home;

import java.util.ArrayList;

import com.gjorgiev.data.Job;
import com.gjorgiev.data.Jobs;
import com.gjorgiev.gethired.DetailActivity;
import com.gjorgiev.gethired.R;
import com.gjorgiev.gethired.adapter.JobAdapter;
import com.gjorgiev.gethired.database.FavoritesDataSource;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class FavoritesFragment extends Fragment{
	private FavoritesDataSource datasource;
	private JobAdapter jobs_adapter;
	private ArrayList<Job> jobs;
	private TextView txtNoResult;
	private ProgressBar progressBar;
	private ListView lv;
	Jobs jobs_detail;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		updateFavorites();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.list_fragment, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		lv = (ListView) view.findViewById(R.id.listView);
		txtNoResult = (TextView)view.findViewById(R.id.txtNoResults);
		txtNoResult.setText("Favorites list is empty, you can start searching for job!");
		txtNoResult.setVisibility(View.GONE);
		progressBar = (ProgressBar)view.findViewById(R.id.progressBarCB);
		progressBar.setVisibility(View.GONE);
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long id) {
				int pos = position;

				Bundle bundle = new Bundle();
				bundle.putSerializable("jobs", jobs_detail);
				Intent intent = new Intent(getActivity(), DetailActivity.class);
				intent.putExtras(bundle);
				intent.putExtra("pos", pos);
				startActivity(intent);
				
			}
		});
		super.onViewCreated(view, savedInstanceState);
	}
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		updateFavorites();
	}
	
	private void updateFavorites(){
		jobs_detail = new Jobs();
	    datasource = new FavoritesDataSource(getActivity());
	    datasource.open();

	    this.jobs = datasource.getAllJobs();
    	for (Job job : jobs) {
    		jobs_detail.addItem(job);
		}
    	datasource.close();
    	if(jobs.isEmpty())
    		txtNoResult.setVisibility(View.VISIBLE);
    	else{
    		jobs_adapter = new JobAdapter(getActivity(), R.layout.list_item, jobs);
    		lv.setAdapter(jobs_adapter);
    		txtNoResult.setVisibility(View.GONE);
    	}
	}
	
}
