package com.hisaab.imp.routine;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.hisaab.core.ResultsListener;

import android.os.AsyncTask;
import android.util.Log;

public class GetReply extends AsyncTask<Hashtable, Void, String> {

	@Override
	protected String doInBackground(Hashtable... params) {
		Hashtable data =params[0];
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(data.get("URL").toString());
		// Add your data
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("data", data.get("data").toString()));

		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			Log.d("myapp", "works till here. 2");
			Log.d("myapp",data.get("URL").toString() );
			Log.d("myapp", data.get("data").toString());
			try {
				HttpResponse response = httpclient.execute(httppost);
				Log.d("myapp", "works till here. 2");
				String responseBody = EntityUtils
						.toString(response.getEntity());
				Log.d("myapp", responseBody);
				return responseBody;
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	ResultsListener listener;

	public void setOnResultsListener(ResultsListener listener) {
		this.listener = listener;
	}

	@Override
	protected void onPostExecute(String result) {
		listener.onResultsSucceeded(result);

	}

}
