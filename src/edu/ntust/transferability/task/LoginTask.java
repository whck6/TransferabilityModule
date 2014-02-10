package edu.ntust.transferability.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import edu.ntust.transferability.TransferabilityModule;
import edu.ntust.transferability.ViewAppsActivity;
import android.os.AsyncTask;

public class LoginTask extends AsyncTask<Map<String, String>, Void, Void>
{
	TransferabilityModule tm;

	public LoginTask()
	{
		super();
	}

	public LoginTask(TransferabilityModule tm)
	{
		this.tm = tm;
	}
	
	@Override
	protected void onPostExecute(Void result)
	{
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	}

	@Override
	protected Void doInBackground(Map<String, String>... maps)
	{
		JSONObject failMessage = null;
		for (int i = 0; i < maps.length; i++)
		{
			HttpClient client = new DefaultHttpClient();
			HttpResponse resp = null;
			HttpPost post = new HttpPost(maps[i].get("url"));

			try
			{
				failMessage = new JSONObject("{\"login\":\"fail\"}");
				
				List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				if (maps[i].get("type").equals("text"))
				{
					pairs.add(new BasicNameValuePair("username", maps[i].get("username")));
					pairs.add(new BasicNameValuePair("password", maps[i].get("password")));
					pairs.add(new BasicNameValuePair("androidid", maps[i].get("androidid")));
					pairs.add(new BasicNameValuePair("app_id", maps[i].get("app_id")));
				}
				else if (maps[i].get("type").equals("verify"))
				{
					pairs.add(new BasicNameValuePair("username", maps[i].get("username")));
					pairs.add(new BasicNameValuePair("password", maps[i].get("password")));
					pairs.add(new BasicNameValuePair("androidid", maps[i].get("androidid")));
					pairs.add(new BasicNameValuePair("app_id", maps[i].get("app_id")));	
					pairs.add(new BasicNameValuePair("p_id", maps[i].get("p_id")));
					pairs.add(new BasicNameValuePair("pre_username", maps[i].get("pre_username")));	
					pairs.add(new BasicNameValuePair("key", maps[i].get("key")));
				}
				post.setEntity(new UrlEncodedFormEntity(pairs));

				resp = client.execute(post);
				tm.serverResponse(new JSONObject(EntityUtils.toString(resp.getEntity())));
			}
			catch (ClientProtocolException e)
			{
				tm.serverResponse(failMessage);
				e.printStackTrace();
			}
			catch (IOException e)
			{
				tm.serverResponse(failMessage);
				e.printStackTrace();
			}
			catch (ParseException e)
			{
				tm.serverResponse(failMessage);
				e.printStackTrace();
			}
			catch (JSONException e)
			{
				tm.serverResponse(failMessage);
				e.printStackTrace();
			}
		}
		return null;
	}

}
