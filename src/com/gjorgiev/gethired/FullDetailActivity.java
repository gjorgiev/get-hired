package com.gjorgiev.gethired;

import com.gjorgiev.gethired.R;

import android.net.Uri;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ShareActionProvider;

@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
public class FullDetailActivity extends Activity {

	private WebView myWebView;
	private String url;
	private String jobTitle;
	private ValueCallback<Uri> mUploadMessage;  
	private final static int FILECHOOSER_RESULTCODE=1; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_full_detail);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		url = getIntent().getStringExtra("url");
		jobTitle = getIntent().getStringExtra("jobTitle");
		myWebView = (WebView) findViewById(R.id.webview1);
		myWebView.getSettings().setBuiltInZoomControls(true);
		WebSettings webSettings = myWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		myWebView.loadUrl(url);
		myWebView.setWebViewClient(new MyWebViewClient());
		myWebView.setWebChromeClient(new WebChromeClient()  
	    {  
	           //The undocumented magic method override  
	           //Eclipse will swear at you if you try to put @Override here  
	        // For Android 3.0+
	        public void openFileChooser(ValueCallback<Uri> uploadMsg) {  

	            mUploadMessage = uploadMsg;  
	            Intent i = new Intent(Intent.ACTION_GET_CONTENT);  
	            i.addCategory(Intent.CATEGORY_OPENABLE);  
	            i.setType("image/*");  
	            FullDetailActivity.this.startActivityForResult(Intent.createChooser(i,"File Chooser"), FILECHOOSER_RESULTCODE);  

	           }

	        // For Android 3.0+
	           public void openFileChooser( ValueCallback uploadMsg, String acceptType ) {
	           mUploadMessage = uploadMsg;
	           Intent i = new Intent(Intent.ACTION_GET_CONTENT);
	           i.addCategory(Intent.CATEGORY_OPENABLE);
	           i.setType("*/*");
	           FullDetailActivity.this.startActivityForResult(
	           Intent.createChooser(i, "File Browser"),
	           FILECHOOSER_RESULTCODE);
	           }

	        //For Android 4.1
	           public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture){
	               mUploadMessage = uploadMsg;  
	               Intent i = new Intent(Intent.ACTION_GET_CONTENT);  
	               i.addCategory(Intent.CATEGORY_OPENABLE);  
	               i.setType("image/*");  
	               FullDetailActivity.this.startActivityForResult( Intent.createChooser( i, "File Chooser" ), FullDetailActivity.FILECHOOSER_RESULTCODE );

	           }

	    }); 
	}
	
	@Override  
	 protected void onActivityResult(int requestCode, int resultCode, Intent intent) {  
	  if(requestCode==FILECHOOSER_RESULTCODE)  
	  {  
	   if (null == mUploadMessage) return;  
	            Uri result = intent == null || resultCode != RESULT_OK ? null  
	                    : intent.getData();  
	            mUploadMessage.onReceiveValue(result);  
	            mUploadMessage = null;  
	  }
	} 
	//flipscreen not loading again
	@Override
	public void onConfigurationChanged(Configuration newConfig){        
	    super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.full_detail, menu);
		MenuItem item = menu.findItem(R.id.menu_item_share);
		ShareActionProvider myShareActionProvider = (ShareActionProvider) item.getActionProvider();
		Intent myIntent = new Intent();
		myIntent.setAction(Intent.ACTION_SEND);
		myIntent.putExtra(Intent.EXTRA_TEXT,jobTitle + " " + url + " - from Get Hired mobile app");
		myIntent.setType("text/plain");
		myShareActionProvider.setShareIntent(myIntent);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if (myWebView.canGoBack() == true) {
					myWebView.goBack();
				} else {
					finish();
				}
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			onBackPressed();
			return true;
		case R.id.menu_item_open_browser:
			if (!url.startsWith("https://") && !url.startsWith("http://")){
			    url = "http://" + url;
			}
			
			Intent openUrlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(myWebView.getUrl()));
			startActivity(openUrlIntent);
		}
		return super.onOptionsItemSelected(item);
	}

	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			findViewById(R.id.progressBar1).setVisibility(View.VISIBLE);
			findViewById(R.id.textView1).setVisibility(View.VISIBLE);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			findViewById(R.id.progressBar1).setVisibility(View.GONE);
			findViewById(R.id.textView1).setVisibility(View.GONE);
		}
	}

}
