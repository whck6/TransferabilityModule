package edu.ntust.transferability;

import java.util.Map;

import org.json.JSONObject;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

public class MyBroadcastReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent)
	{
		if (TextUtils.equals(intent.getAction(), DownloadManager.ACTION_DOWNLOAD_COMPLETE))
		{
			Log.d(ViewAppsActivity.tag, "!!! download complete");
			
			TransferabilityModule tm = new TransferabilityModule()
			{
				
				@Override
				public void serverResponse(JSONObject jobject)
				{
					// TODO Auto-generated method stub
					
				}
				
				@Override
				protected void login(Map<String, String> map)
				{
					// TODO Auto-generated method stub
					
				}
			};
		}
	}
	


}
