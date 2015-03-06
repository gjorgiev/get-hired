package com.gjorgiev.gethired.fragments.search;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

public class ShowNetworkSettings {
	Context context;
	Activity activity;
	
	public ShowNetworkSettings(Context context, Activity activity) {
		this.context = context;
		this.activity = activity;
	}
	
	public void showNetworkSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
      
        // Setting Dialog Title
        alertDialog.setTitle("Network connection required");
  
        // Setting Dialog Message
        alertDialog.setMessage("Enable Wi-fi or Mobile Data?");

        alertDialog.setNegativeButton("Wi-fi", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
                activity.startActivity(intent);
            }
        });
        
        alertDialog.setPositiveButton("Mobile Data", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                activity.startActivity(intent);
            }
        });
  
        // Showing Alert Message
        alertDialog.show();
    }

}
