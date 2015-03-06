package com.gjorgiev.gethired.fragments.results;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gjorgiev.data.Job;
import com.gjorgiev.data.Jobs;
import com.gjorgiev.gethired.DetailActivity;
import com.gjorgiev.gethired.R;
import com.gjorgiev.gethired.ResultsActivity;
import com.gjorgiev.gethired.adapter.JobAdapter;
import com.gjorgiev.gethired.database.RecentDataSource;
import com.gjorgiev.indeed.tasks.IndeedServiceHelper;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class IndeedFragment extends Fragment{
	private ArrayList<Job> jobs = new ArrayList<Job>();
	private JobAdapter jobs_adapter;
	private String indeed_url;
	private ProgressBar progressBar;
	private TextView txtNoResult;
	Jobs jobs_detail;
	ListView lv;
	private RecentDataSource datasource;
	IndeedServiceTask serviceTask;
    int listJobsSize = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		indeed_url = getActivity().getIntent().getStringExtra("indeed_url");
		jobs_detail = new Jobs();
		serviceTask = new IndeedServiceTask();
        try {
            serviceTask.execute(indeed_url);
            
        }
        catch (Exception e)
        {
        	System.out.print("Greska: " +  e.getMessage());
            serviceTask.cancel(true);
            ((ResultsActivity) getActivity()).alert(getResources().getString(R.string.no_items));
        }
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		serviceTask.cancel(true);
		super.onDestroyView();
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
		txtNoResult.setText("Indeed returned no results, please change your criteria and try again");
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
				//Add current job to Recent 
				//get attached data
				final String jobTitle = jobs_detail.getItem(pos).getJobTitle();
				final String location = jobs_detail.getItem(pos).getFormattedLocation();
				final String company = jobs_detail.getItem(pos).getCompany();
				final String snippet = jobs_detail.getItem(pos).getSnippet();
				final String url = jobs_detail.getItem(pos).getUrl();
				final Long datetime = jobs_detail.getItem(pos).getDatetime();
			    datasource = new RecentDataSource(getActivity());
			    datasource.open();
			    
				Job recent = getRecent(url);
				if(recent == null){
				      datasource.addRecent(jobTitle, company, location, snippet, url, datetime);
				}
			    datasource.close();
				startActivity(intent);
				
			}
		});
		super.onViewCreated(view, savedInstanceState);
	}
	
	private Job getRecent(String url) {
		RecentDataSource datasource;
	    datasource = new RecentDataSource(getActivity());
	    datasource.open();
		jobs = datasource.getAllJobs();
		for (Job job : jobs) {
			if(url.equals(job.getUrl())){
				return job;
			}
		}
		datasource.close();
		return null;
	}
	
	public void alert(String msg) {
		// TODO Auto-generated method stub
		Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_LONG).show();
	}
	
    public void setJobs(ArrayList<Job> jobs){
    	for (Job job : jobs) {
    		jobs_detail.addItem(job);
		}
	    	this.jobs = jobs;
	    	jobs_adapter = new JobAdapter(getActivity(),R.layout.list_item, jobs);
	    	lv.setAdapter(jobs_adapter);
	    	jobs_adapter.setNotifyOnChange(true);
    }
    
    
    private class IndeedServiceTask extends AsyncTask<String, Integer, String>{
    	private static final String debugTag = "IndeedServiceTask";

    	@Override
        protected void onPreExecute() {
            super.onPreExecute(); 
            progressBar.setVisibility(View.VISIBLE);
        }
    	
    	@Override
    	protected String doInBackground(String... params) {
    		try {
            	Log.d(debugTag,"Background:" + Thread.currentThread().getName());
                String result = IndeedServiceHelper.downloadFromServer(params);
                return result;
            } catch (Exception e) {
                return new String();
            } 
    	}
    	@Override
    	protected void onPostExecute(String result) {
    		super.onPostExecute(result);
    		ArrayList<Job> jobData = new ArrayList<Job>();
    		JSONArray respArray = null;
    		progressBar.setVisibility(View.GONE);
            if (result.length() == 0) {
                ((ResultsActivity) getActivity()).alert ("Unable to find item data. Try again later.");
                return;
            }

    		try{
    			JSONObject respObject = new JSONObject(result);
    			respArray = respObject.getJSONArray("results");
    			for(int i=0; i<respArray.length(); i++) {
    				if(isCancelled())
    					break;
    					JSONObject job = respArray.getJSONObject(i);
    					String jobTitle = job.getString("jobtitle");
    					String company = job.getString("company");
    					String formattedLocation = job.getString("formattedLocation");
    					String snippet = job.getString("snippet");
    					String url = job.getString("url");
    					String dateTimeString = job.getString("date");
    	    			Long datetime = null;
    	    			Date date = null;
    					try {
    						date = new SimpleDateFormat("E, dd MMM yyyy hh:mm:ss", Locale.US).parse(dateTimeString);
    					} catch (ParseException e1) {
    						// TODO Auto-generated catch block
    						e1.printStackTrace();
    					}
    					if(date != null)
    						datetime = date.getTime();
    					
    					jobData.add(new Job(jobTitle, snippet, formattedLocation, company, url,datetime));
    					
    					System.out.println(jobTitle + snippet + formattedLocation + company);
    				}
    			}
    			catch (JSONException e){
    				e.printStackTrace();
    			}
    			if(!isCancelled())
    					setJobs(jobData);
    			progressBar.setVisibility(View.GONE);
        		if(respArray.length() == 0)
        			txtNoResult.setVisibility(View.VISIBLE);
        		
    		}
    }
    
}
