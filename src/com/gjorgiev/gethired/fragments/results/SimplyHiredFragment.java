package com.gjorgiev.gethired.fragments.results;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.gjorgiev.data.Job;
import com.gjorgiev.data.Jobs;
import com.gjorgiev.gethired.DetailActivity;
import com.gjorgiev.gethired.R;
import com.gjorgiev.gethired.ResultsActivity;
import com.gjorgiev.gethired.adapter.JobAdapter;
import com.gjorgiev.gethired.database.RecentDataSource;

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

public class SimplyHiredFragment extends Fragment{
	private ArrayList<Job> jobs = new ArrayList<Job>();
	private JobAdapter jobs_adapter;
	private String sh_url = "/a/jobs-api/xml-v2/";
	private TextView txtNoResult;
	private ProgressBar progressBar;
	Jobs jobs_detail;
	ListView lv;
	private RecentDataSource datasource;
	
	// All static variables
	static final String URL_START = "http://api.simplyhired.";
	static final String URL_END = "?pshid=57374&ssty=5&cflg=r&jbd=yumfer.jobamatic.com";
	static String URL_FULL = "";
	static String URL_COUNTRY;
	// XML node keys
	static final String KEY_ITEM = "r"; // parent node
	static final String KEY_JOB_TITLE = "jt";
	static final String KEY_COMPANY = "cn"; 
	static final String KEY_SNIPPET = "e";
	static final String KEY_LOCATION = "loc";
	static final String KEY_DATE = "dp";
	SHServiceTask serviceTask;
	
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
		sh_url += getActivity().getIntent().getStringExtra("sh_url");
		URL_COUNTRY = getActivity().getIntent().getStringExtra("country");
		URL_FULL = URL_START + URL_COUNTRY + sh_url + URL_END;
		jobs_detail = new Jobs();
		serviceTask = new SHServiceTask();
        try {
            serviceTask.execute(URL_FULL);
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
		txtNoResult.setText("SimplyHired returned no results, please change your criteria and try again");
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
    private class SHServiceTask extends AsyncTask<String, Integer, String>{
    	private static final String debugTag = "CBServiceTask";
    	XMLParser parser = new XMLParser();

    	@Override
        protected void onPreExecute() {
            super.onPreExecute(); 
            progressBar.setVisibility(View.VISIBLE);
    	}
    	
    	@Override
    	protected String doInBackground(String... params) {
    		try {
            	Log.d(debugTag,"Background:" + Thread.currentThread().getName());
        		String xml = XMLParser.getXmlFromUrl(params);
                return xml;
            } catch (Exception e) {
            	System.out.println("SimplyHiredFragment error in doInBackground method");
                return new String();
            }
    	}
    	@Override
    	protected void onPostExecute(String result) {
    		super.onPostExecute(result);
    		//progDialog.dismiss();
    		if(result != null){
    				Document doc = parser.getDomElement(result); // getting DOM element
		    		ArrayList<Job> jobData = new ArrayList<Job>();
		    		NodeList nl = doc.getElementsByTagName(KEY_ITEM);
		    		NodeList srcNl = doc.getElementsByTagName("src");
		    		// looping through all item nodes <item>
		    		doc.getDocumentElement().normalize();
		    		for (int i = 0; i < nl.getLength(); i++) {
		    			if(isCancelled())
							break;
		    			Element e = (Element) nl.item(i);
		    			Element src = (Element)srcNl.item(i);
		    			// adding each child node to arraylist
		    			String jobTitle = parser.getValue(e, KEY_JOB_TITLE);
		    			String company = parser.getValue(e, KEY_COMPANY);
		    			String formattedLocation = parser.getValue(e, KEY_LOCATION);
		    			String snippet = parser.getValue(e, KEY_SNIPPET);
		    			String url = "";
		    			Node node = srcNl.item(i);
		                if (node.getNodeType() == Node.ELEMENT_NODE){
		                	
			    			System.out.println(src.getTagName() + " has attributes: " + src.hasAttributes());

			    	        if (src.getTagName().startsWith("src"))
			    	        {
			    	            url =  src.getAttribute("url");
			    	        }
		                }

		    			String dateTimeString = parser.getValue(e, KEY_DATE);
		    			Long datetime = null;
		    			Date date = null;
						try {
							date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).parse(dateTimeString);
						} catch (ParseException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						if(date != null)
							datetime = date.getTime();
		    			
		    			jobData.add(new Job(jobTitle, snippet, formattedLocation, company, url,datetime));
		    			
		    			System.out.println(jobTitle + snippet + formattedLocation + company + " url: " + url);
		    		}
		    		if(!isCancelled())
						setJobs(jobData);
		    		progressBar.setVisibility(View.GONE);
		    		if(nl.getLength() == 0){
		    			txtNoResult.setVisibility(View.VISIBLE);
		    		}
    		}

    	}
    }
    
}
