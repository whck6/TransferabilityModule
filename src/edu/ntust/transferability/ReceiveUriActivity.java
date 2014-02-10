package edu.ntust.transferability;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import edu.ntust.transferability.task.LoginTask;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ReceiveUriActivity extends TransferabilityModule
{
	private EditText etusername;
	private EditText etpassword;
	private EditText etchecksum;
	private ProgressBar pb;
	private TextView tvVerifyFail;
	private AlertDialog alert;
	private File eviFile;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
		
		onNewIntent(getIntent());
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onDestroy()
	{
		unregisterReceiver(onDownloadComplete);
		if (alert != null)
		{
			alert.dismiss();
			alert = null;
		}
		
		super.onDestroy();
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		Log.d(ViewAppsActivity.tag, "!!!onNewIntent");

		setIntent(intent);

		handleViewIntent();

		View layout = LayoutInflater.from(this).inflate(R.layout.dialog_login_receive, null);
		alert = createLoginForm(layout);
		alert.show();

		initUI();
		setAlertUI();

		super.onNewIntent(intent);
	}

	private void setAlertUI()
	{
		alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				pb.setVisibility(View.VISIBLE);
				tvVerifyFail.setVisibility(View.GONE);
				etusername.setVisibility(View.GONE);
				etpassword.setVisibility(View.GONE);

				try
				{
					JSONObject eviJSONData = readJSONData(eviFile);
					Log.d(ViewAppsActivity.tag, eviJSONData.toString());

					Map<String, String> auth = new HashMap<String, String>();
					auth.put("url", TransferabilityModule.SERVER_URL + "/notary/api/transferability");
					auth.put("username", etusername.getText().toString());
					auth.put("password", etpassword.getText().toString());
					auth.put("androidid", getDeviceID());
					auth.put("app_id", eviJSONData.getString("app_id"));
					auth.put("key", eviJSONData.getString("key"));
					auth.put("p_id", eviJSONData.getString("p_id"));
					auth.put("pre_username", eviJSONData.getString("username"));
					auth.put("method", "post");
					auth.put("type", "verify");

					login(auth); // verify user
				}
				catch (JSONException e)
				{
					e.printStackTrace();
				}
			}

		});
		alert.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
		alert.getButton(AlertDialog.BUTTON_NEUTRAL).setVisibility(View.GONE);
	}

	private void initUI()
	{
		tvVerifyFail = (TextView) alert.findViewById(R.id.tvVerifyFail);
		etusername = (EditText) alert.findViewById(R.id.etusername);
		etpassword = (EditText) alert.findViewById(R.id.etpassword);
		etchecksum = (EditText) alert.findViewById(R.id.etchecksum);
		pb = (ProgressBar) alert.findViewById(R.id.pb);

		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					JSONObject eviJSONData = readJSONData(eviFile);
					etchecksum.setText((CharSequence) eviJSONData.getString("key"));
				}
				catch (JSONException e)
				{
					e.printStackTrace();
				}
			}
		});

	}

	private void handleViewIntent()
	{
		Log.d(ViewAppsActivity.tag, "!!!handleViewIntent");

		// Get the Intent action
		Intent mIntent = getIntent();
		String action = mIntent.getAction();
		/*
		 * For ACTION_VIEW, the Activity is being asked to display data. Get the
		 * URI.
		 */
		if (TextUtils.equals(action, Intent.ACTION_VIEW))
		{
			// Get the URI from the Intent
			Uri beamUri = mIntent.getData();
			/*
			 * Test for the type of URI, by getting its scheme value
			 */
			if (TextUtils.equals(beamUri.getScheme(), "file"))
			{
				handleFileUri(beamUri.getPath(), getFilesDir().getPath() + "/transfer.txt");
				eviFile = new File(getFilesDir().getPath(), "transfer.txt");

			}
			else if (TextUtils.equals(beamUri.getScheme(), "content"))
			{
				//
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void login(Map<String, String> map)
	{
		LoginTask task = new LoginTask(this);
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, map);
	}

	@Override
	public void serverResponse(JSONObject jobject)
	{
		Log.d(ViewAppsActivity.tag, "!!!" + jobject.toString());
		if (!jobject.isNull("login"))
		{
			try
			{
				if (jobject.get("login").equals("pass"))
				{
					// Valid transfer
					tvVerifyFail.setText(getString(R.string.valid_transfer));
					runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							// TODO Auto-generated method stub
							alert.getButton(AlertDialog.BUTTON_NEGATIVE).setVisibility(View.GONE);
							alert.getButton(AlertDialog.BUTTON_POSITIVE).setVisibility(View.GONE);
							alert.getButton(AlertDialog.BUTTON_NEUTRAL).setVisibility(View.VISIBLE);

						}
					});

					Log.d(ViewAppsActivity.tag,
							"!!!" + String.format("%s/notary/apps/%s", TransferabilityModule.SERVER_URL, jobject.getString("app_uri")));
					downloadFile(Uri.parse(String.format("%s/notary/apps/%s", TransferabilityModule.SERVER_URL, jobject.getString("app_uri"))));
					// return;
				}
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
		runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				pb.setVisibility(View.GONE);
				tvVerifyFail.setVisibility(View.VISIBLE);
			}
		});
	}

}
