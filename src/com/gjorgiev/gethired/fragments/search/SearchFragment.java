package com.gjorgiev.gethired.fragments.search;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.gjorgiev.gethired.R;
import com.gjorgiev.gethired.SearchActivity;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class SearchFragment extends Fragment{
		private TextView txtKeyword;
		private TextView txtLocation;
		private Button btnLocation;
		private Button btnClear;
		Search search;
		
	   @Override
	    public View onCreateView(LayoutInflater inflater, 
          ViewGroup container, Bundle savedInstanceState) {
	        // Inflate the layout for this fragment
	        View view =  inflater.inflate(R.layout.search_fragment, container, false);
	        btnLocation = (CheckBox)view.findViewById(R.id.btnLocation);
	        txtKeyword = (TextView)view.findViewById(R.id.txt_keyword);
	        txtLocation = (TextView)view.findViewById(R.id.txt_location);
	        btnClear = (Button)view.findViewById(R.id.btnClear);
	        btnLocation.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(isNetworkAvailable())
						new DetectLocationTask().execute();
					else
						new ShowNetworkSettings(getActivity(), getActivity()).showNetworkSettingsAlert();
				}
			});
	        btnClear.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					txtKeyword.setText("");
				}
			});
	        return view;
	   }
		public void alert(String msg) {
			// TODO Auto-generated method stub
			Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_LONG).show();
		}
		private class DetectLocationTask extends AsyncTask<Address, Integer, Address>{
			private LocationManager locationManager;
			private String provider;
			private Location location;
			
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				txtLocation.setHint("Detecting location ... ");
				super.onPreExecute();
			}
			@Override
			protected Address doInBackground(Address... params) {
		        // Initialize the location fields
				// Get the location manager
		        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
		        // Define the criteria how to select the locatioin provider -> use
		        // default
		        Criteria criteria = new Criteria();
		        provider = locationManager.getBestProvider(criteria, false);
				location = locationManager.getLastKnownLocation(provider);
		        if (location != null) {
			          System.out.println("Provider " + provider + " has been selected.");
			          float lat = (float) (location.getLatitude());
			          float lng = (float) (location.getLongitude());
			          
			          Geocoder geocoder = new Geocoder(getActivity(), Locale.ENGLISH);
			          try {
			              List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
			              Log.e("Addresses","-->"+addresses);
			              if(addresses != null) {
			                  return addresses.get(0);
			              }
			              else{
			            	  return null;
			              }
			          } catch (IOException e) {
			              // TODO Auto-generated catch block
			              e.printStackTrace();
			              return null;
			          }
			        }
			        else
			        {
			        	return null;
			        }
			}
			@Override
			protected void onPostExecute(Address result) {
				if(result != null){
					txtLocation.setText(result.getLocality());
					SearchActivity parent = (SearchActivity) getActivity();
					parent.setFlagFromChild(result.getCountryCode());
				}
				else {
					txtLocation.setHint("Try again or enter manually");
				}
				super.onPostExecute(result);
			}
		}
		private boolean isNetworkAvailable() {
		    ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
		}
		
}
