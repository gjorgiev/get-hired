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
import com.gjorgiev.indeed.tasks.GithubServiceHelper;

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

public class GithubFragment extends Fragment{
	private ArrayList<Job> jobs = new ArrayList<Job>();
	private JobAdapter jobs_adapter;
	private String github_url;
	private TextView txtNoResult;
	private ProgressBar progressBar;
	Jobs jobs_detail;
	ListView lv;
	private RecentDataSource datasource;
	GithubServiceTask serviceTask;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.list_fragment, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		github_url = getActivity().getIntent().getStringExtra("github_url");
		jobs_detail = new Jobs();
		serviceTask = new GithubServiceTask();
        try {
            serviceTask.execute(github_url);
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
	public void onViewCreated(View view, Bundle savedInstanceState) {
		lv = (ListView) view.findViewById(R.id.listView);
		txtNoResult = (TextView)view.findViewById(R.id.txtNoResults);
		txtNoResult.setText("Github returned no results, please change your criteria and try again");
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
	
    public void setJobs(ArrayList<Job> jobs){
    	for (Job job : jobs) {
    		jobs_detail.addItem(job);
		}
    	this.jobs = jobs;
    	jobs_adapter = new JobAdapter(getActivity(),R.layout.list_item, jobs);
    	lv.setAdapter(jobs_adapter);
    }
    
    private class GithubServiceTask extends AsyncTask<String, Integer, String>{
    	private static final String debugTag = "GithubServiceTask";
    	
    	@Override
        protected void onPreExecute() {
            super.onPreExecute(); 
        	progressBar.setVisibility(View.VISIBLE);
        }
    	
    	@Override
    	protected String doInBackground(String... params) {
    		try {
            	Log.d(debugTag,"Background:" + Thread.currentThread().getName());
                String result = GithubServiceHelper.downloadFromServer(params);
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
                txtNoResult.setVisibility(View.VISIBLE);
                return;
            }

    		try{
    			respArray = new JSONArray(result);
    			for(int i=0; i<respArray.length(); i++) {
    				if(isCancelled())
    					break;
    					JSONObject job = respArray.getJSONObject(i);
    					String jobTitle = job.getString("title");
    					String company = job.getString("company");
    					String formattedLocation = job.getString("location");
    					String snippet = job.getString("description");
    					String url = job.getString("url");
    					String dateTimeString = job.getString("created_at");
    	    			Long datetime = null;
    	    			Date date = null;
    					try {
    						date = new SimpleDateFormat("E MMM dd hh:mm:ss 'UTC' yyyy", Locale.US).parse(dateTimeString);
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
