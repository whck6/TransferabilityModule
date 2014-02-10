package edu.ntust.transferability.task;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import edu.ntust.transferability.TransferabilityModule;
import edu.ntust.transferability.ViewAppsActivity;
import android.os.AsyncTask;
import android.util.Log;

public class UploadFileTask extends AsyncTask<File, Void, Void>
{
	private String url;
	private String filename;
	private TransferabilityModule module;

	public UploadFileTask()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public UploadFileTask(TransferabilityModule module, String filename)
	{
		super();
		this.module = module;
		this.filename = filename;
	}

	@Override
	protected Void doInBackground(File... files)
	{
		HttpClient client = new DefaultHttpClient();
		HttpResponse resp = null;
		HttpPost post = new HttpPost(url);
		JSONObject failMessage = null;

		for (File file : files)
		{

			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

			FileBody filebody = new FileBody(file, ContentType.MULTIPART_FORM_DATA, filename);
			builder.addPart("file", filebody);

			HttpEntity entity = builder.build();
			post.setEntity(entity);

			try
			{
				failMessage = new JSONObject("{\"upload\":\"error\"}");

				resp = client.execute(post);
				module.serverResponse(new JSONObject(EntityUtils.toString(resp.getEntity())));
			}
			catch (ClientProtocolException e)
			{
				module.serverResponse(failMessage);
				Log.d(ViewAppsActivity.tag, "!!!" + e.toString());
				e.printStackTrace();
			}
			catch (IOException e)
			{
				module.serverResponse(failMessage);
				Log.d(ViewAppsActivity.tag, "!!!" + e.toString());
				e.printStackTrace();
			}
			catch (ParseException e)
			{
				module.serverResponse(failMessage);
				Log.d(ViewAppsActivity.tag, "!!!" + e.toString());
				e.printStackTrace();
			}
			catch (JSONException e)
			{
				module.serverResponse(failMessage);
				Log.d(ViewAppsActivity.tag, "!!!" + e.toString());
				e.printStackTrace();
			}
		}

		return null;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

}
