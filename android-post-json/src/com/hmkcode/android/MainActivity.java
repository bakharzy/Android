package com.hmkcode.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;


import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import com.hmkcode.android.vo.Action;

public class MainActivity extends Activity implements OnClickListener {

	TextView tvIsConnected;
	EditText etName,etUsername;
	Button btnPost;

	Action action;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// get reference to the views
		etName = (EditText) findViewById(R.id.etName);
		etUsername = (EditText) findViewById(R.id.etUsername);
		btnPost = (Button) findViewById(R.id.btnPost);
				
		// add click listener to Button "POST"
		btnPost.setOnClickListener(this);
		
	}

	public static String POST(String url, Action action){
		InputStream inputStream = null;
		String result = "";
		try {
			
			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();
			
			// 2. make POST request to the given URL
		    HttpPost httpPost = new HttpPost(url);
		    
		    // 3. build json String using Jacksin Library
		    String json = "";
		    ObjectMapper mapper = new ObjectMapper(); 
		    json = mapper.writeValueAsString(action);
		    
		    
		    // 5. set json to StringEntity
		    StringEntity se = new StringEntity(json);
		    
		    // 6. set httpPost Entity
		    httpPost.setEntity(se);
		    
		    // 7. Set some headers to inform server about the type of the content   
		    httpPost.setHeader("Accept", "application/json");
		    httpPost.setHeader("Content-type", "application/json");
		    
			// 8. Execute POST request to the given URL
			HttpResponse httpResponse = httpclient.execute(httpPost);
			
			// 9. receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();
			
		    
			// 10. convert inputstream to string
			if(inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "Did not work!";
		
		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}
		
		// 11. return result
		return result;
	}
	
	 @Override
		public void onClick(View view) {
		
			switch(view.getId()){
				case R.id.btnPost:
					if(!validate())
						Toast.makeText(getBaseContext(), "Enter some data!", Toast.LENGTH_LONG).show();
					// call AsynTask to perform network operation on separate thread
					new HttpAsyncTask().execute("http://data-bakharzy.rhcloud.com/api/app/applications/70dff194-2871-4ad8-9795-3f27f0021713/actions");
				break;
			}
			
		}
	
   
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
             
        	action = new Action();
		    Map<String, String> map = new HashMap<String,String>();
            map.put("network", "Wifi");
            map.put("button", "POST-button");
            action.setOptions(map);
        	action.setName(etName.getText().toString());
        	action.setUsername(etUsername.getText().toString());

            return POST(urls[0],action);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        	Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
       }
    }
	
	
	private boolean validate(){
		if(etName.getText().toString().trim().equals(""))
			return false;
		else if(etUsername.getText().toString().trim().equals(""))
			return false;
		else
			return true;	
	}
	private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
        
        inputStream.close();
        return result;
        
    }

	
}
