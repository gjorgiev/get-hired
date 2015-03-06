package com.gjorgiev.gethired.adapter;

import java.util.ArrayList;

import com.gjorgiev.data.Query;
import com.gjorgiev.gethired.R;
import com.gjorgiev.gethired.database.RecentSearchDataSource;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class QueryAdapter extends ArrayAdapter<Query>{
	private ArrayList<Query> objects;
	private Context context;
	
	public QueryAdapter(Context context, int textViewResourceId, ArrayList<Query> objects) {
		super(context, textViewResourceId, objects);
		this.objects = objects;
		this.context = context;
	}
	public View getView(int position, View convertView, ViewGroup parent){

		// assign the view we are converting to a local variable
		View v = convertView;

		// first check to see if the view is null. if so, we have to inflate it.
		// to inflate it basically means to render, or show, the view.
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.query_item, null);
		}

		/*
		 * Recall that the variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
		 * iterates through the list we sent it)
		 * 
		 * Therefore, i refers to the current Item object.
		 */
		Query i = objects.get(position);

		if (i != null) {
			final Query temp = i;
			// This is how you obtain a reference to the TextViews.
			// These TextViews are created in the XML files we defined.

			TextView keyword = (TextView) v.findViewById(R.id.query_keyword);
			TextView location = (TextView) v.findViewById(R.id.query_location);
			TextView timestamp = (TextView) v.findViewById(R.id.query_timestamp);
			Button deleteItem = (Button) v.findViewById(R.id.chk_delete_item);
			deleteItem.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					RecentSearchDataSource datasource = new RecentSearchDataSource(context);
					datasource.open();
					datasource.deleteQuery(temp);
					datasource.close();
					datasource.open();
					objects = datasource.getAllQueries();
					datasource.close();
					
					notifyDataSetChanged();
				}
			});
			CharSequence  formatted_timestamp = DateUtils.getRelativeTimeSpanString(i.getTimestamp(), System.currentTimeMillis(),0L, DateUtils.FORMAT_ABBREV_ALL);
			ImageView imageView = (ImageView) v.findViewById(R.id.list_image);
			imageView.setImageResource(context.getResources().getIdentifier("drawable/" + i.getCountry(), null, context.getPackageName()));
			
			if (keyword != null){
				keyword.setText(i.getKeyword());
			}
			if (location != null){
				location.setText(i.getLocation());
			}
			if (timestamp != null){
				timestamp.setText(formatted_timestamp);
			}
		}

		// the view must be returned to our activity
		return v;
	}
	@Override
	public int getCount() {
	    return objects.size();
	}
}
