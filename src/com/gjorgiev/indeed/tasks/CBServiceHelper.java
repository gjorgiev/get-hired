package com.gjorgiev.indeed.tasks;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class CBServiceHelper {
	private static final String webUrl = "http://api.careerbuilder.com/v1/jobsearch/?DeveloperKey=WDHR5BM6C14M21F6LG58";
	private static final int HTTP_STATUS_OK = 200;
	private static byte[] buff = new byte[1024];
	private static final String logTag = "CBServiceHelper";

	public static class ApiException extends Exception {
		private static final long serialVersionUID = 1L;

		public ApiException(String msg) {
			super(msg);
		}

		public ApiException(String msg, Throwable thr) {
			super(msg, thr);
		}
	}

	public static synchronized String downloadFromServer(String... params)
			throws ApiException {
		String retval = null;
		String serviceUrl = params[0];

		String url = webUrl + serviceUrl;

		Log.d(logTag, "Fetching " + url);

		// create an http client and a request object.
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);

		try {

			// execute the request

			HttpResponse response = client.execute(request);
			System.out.println("client.execute = OK");
			StatusLine status = response.getStatusLine();
			if (status.getStatusCode() != HTTP_STATUS_OK) {
				// handle error here
				System.out.println("Invalid response from server");
				throw new ApiException("Invalid response from server"
						+ status.toString());
			}

			// process the content.
			HttpEntity entity = response.getEntity();
			InputStream ist = entity.getContent();
			ByteArrayOutputStream content = new ByteArrayOutputStream();

			int readCount = 0;
			while ((readCount = ist.read(buff)) != -1) {
				content.write(buff, 0, readCount);
			}
			retval = new String(content.toByteArray());

		} catch (Exception e) {
			System.out.println("Problem connecting to the server");
			throw new ApiException("Problem connecting to the server "
					+ e.getMessage(), e);
		}

		return retval;
	}

}
