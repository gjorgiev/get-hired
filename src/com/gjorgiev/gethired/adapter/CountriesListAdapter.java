package com.gjorgiev.gethired.adapter;

import java.util.Locale;

import com.gjorgiev.gethired.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class CountriesListAdapter extends ArrayAdapter<String> {
	private final Context context;
	private final String[] values;
 
	public CountriesListAdapter(Context context, String[] values) {
		super(context, R.layout.country_list_item, values);
		this.context = context;
		this.values = values;
	}
 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		// assign the view we are converting to a local variable
		View v = convertView;

		// first check to see if the view is null. if so, we have to inflate it.
		// to inflate it basically means to render, or show, the view.
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.country_list_item, parent, false);
		}
		
		//TextView textView = (TextView) v.findViewById(R.id.txtViewCountryName);
		ImageView imageView = (ImageView) v.findViewById(R.id.imgViewFlag);
		
    	String[] g=values[position].split(",");
    	//textView.setText(GetCountryZipCode(g[1]).trim());

    	
    	String pngName = g[1].trim().toLowerCase();
    	imageView.setImageResource(context.getResources().getIdentifier("drawable/" + pngName, null, context.getPackageName()));
		return v;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		// assign the view we are converting to a local variable
				View v = convertView;

				// first check to see if the view is null. if so, we have to inflate it.
				// to inflate it basically means to render, or show, the view.
				if (v == null) {
					LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					v = inflater.inflate(R.layout.country_list_item, parent, false);
				}
				
				TextView textView = (TextView) v.findViewById(R.id.txtViewCountryName);
				ImageView imageView = (ImageView) v.findViewById(R.id.imgViewFlag);
				
		    	String[] g=values[position].split(",");
		    	textView.setText(GetCountryZipCode(g[1]).trim());

		    	
		    	String pngName = g[1].trim().toLowerCase();
		    	imageView.setImageResource(context.getResources().getIdentifier("drawable/" + pngName, null, context.getPackageName()));
				return v;
	}
	
	private String GetCountryZipCode(String ssid){
        Locale loc = new Locale("", ssid);
        
        return loc.getDisplayCountry().trim();
    }
}