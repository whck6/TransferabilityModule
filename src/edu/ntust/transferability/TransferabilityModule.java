package edu.ntust.transferability;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import edu.ntust.transferability.inf.HttpConnectionCallback;
import edu.ntust.transferability.task.UploadFileTask;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

public abstract class TransferabilityModule extends Activity implements HttpConnectionCallback
{
	public static final String SERVER_URL = "http://192.168.0.104:8080";
	
	protected abstract void login(Map<String, String> map);

	protected BroadcastReceiver onDownloadComplete = new BroadcastReceiver()
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (TextUtils.equals(intent.getAction(), DownloadManager.ACTION_DOWNLOAD_COMPLETE))
			{
				Log.d(ViewAppsActivity.tag, Environment.DIRECTORY_DOWNLOADS);

				openFile(Environment.getExternalStorageDirectory() + "/Android/data/edu.ntust.transferability/files/temp.tmp");
				handleFileUri(Environment.getExternalStorageDirectory() + "/Android/data/edu.ntust.transferability/files/temp.tmp", getFilesDir().getPath() + "/temp");
			}
		}
	};

	private void openFile(String fileName)
	{
		Log.d(ViewAppsActivity.tag, fileName);
		Intent install = new Intent(Intent.ACTION_VIEW);
		install.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.android.package-archive");
		install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(install);

	}

	protected void uploadAPK(File file, String fileName)
	{
		UploadFileTask hcTask = new UploadFileTask(this, fileName);
		hcTask.setUrl(TransferabilityModule.SERVER_URL + "/notary/api/upload");
		hcTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, file);
	}

	protected AlertDialog createLoginForm(View view)
	{
		Builder ad = new AlertDialog.Builder(this);

		ad.setView(view);
		ad.setMessage(getString(R.string.login_message));
		ad.setCancelable(false);
		ad.setPositiveButton(getString(R.string.submit), null);
		ad.setNegativeButton(R.string.cancel, null);
		ad.setNeutralButton(R.string.close, null);

		return ad.create();
	}

	protected Long downloadFile(Uri uri)
	{
		// Uri uri = Uri.parse("https://copy.com/xQyPci0Tnbdy/Facebook.apk");
		// downloadFile(uri);

		DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
		DownloadManager.Request request = new Request(uri);
		request.setDestinationInExternalFilesDir(this, null, "temp.tmp");
		return dm.enqueue(request);
	}

	protected void handleFileUri(String sourcePath, String destinationPath)
	{
		Log.d(ViewAppsActivity.tag, "!!!handleFileUri");
		Log.d(ViewAppsActivity.tag, "!!!" + sourcePath);
		Log.d(ViewAppsActivity.tag, "!!!" + destinationPath);

		File sourceFile = new File(sourcePath);
		File destinationFile = new File(destinationPath);

		FileReader fr;
		FileWriter fw;
		try
		{
			fr = new FileReader(sourceFile);
			BufferedReader br = new BufferedReader(fr);

			fw = new FileWriter(destinationFile);
			StringBuilder sb = new StringBuilder();
			while (br.ready())
			{
				sb.append(br.readLine());
			}
			fw.write(sb.toString());
			fw.close();

			br.close();
			fr.close();

			sourceFile.delete();
		}
		catch (FileNotFoundException e1)
		{
			e1.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	protected JSONObject readJSONData(File f)
	{
		JSONObject data = null;
		try
		{
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			Log.d(ViewAppsActivity.tag, "!!!" + fr.toString());
			data = new JSONObject(br.readLine());
			fr.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return data;
	}
	
	protected String getAndroidID()
	{
		Log.d(ViewAppsActivity.tag, "!!!" + Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
		return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
	}
	
	protected String getDeviceID()
	{
		TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		Log.d(ViewAppsActivity.tag, "!!!" + tm.getDeviceId());
		return tm.getDeviceId();
	}
}
