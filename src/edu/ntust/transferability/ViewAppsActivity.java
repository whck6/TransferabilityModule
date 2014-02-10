package edu.ntust.transferability;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import edu.ntust.transferability.inf.HttpConnectionCallback;
import edu.ntust.transferability.task.LoginTask;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ViewAppsActivity extends TransferabilityModule implements CreateNdefMessageCallback, HttpConnectionCallback, OnItemClickListener
{
	public final static String tag = ViewAppsActivity.class.getName();

	private SparseBooleanArray checkedArray = new SparseBooleanArray();

	private File requestFile;
	private File eviFile;
	private String requestFileName;

	private EditText etusername;
	private EditText etpassword;
	private ProgressBar pb;
	private TextView tvVerifyFail;
	private ListView viewapps_listview;

	private AlertDialog alert;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_viewapps);
		viewapps_listview = (ListView) findViewById(R.id.viewapps_listview);
		
//		Uri uri = Uri.parse(TransferabilityModule.SERVER_URL + "/notary/apps/blue.apk");
//		downloadFile(uri);
		
//		Log.d(tag, Build.MODEL);
	}

	@Override
	protected void onStart()
	{
		PackageManager pm = getPackageManager();
		if (!pm.hasSystemFeature(PackageManager.FEATURE_NFC))
		{
			Log.d(tag, "!!!unsupported");
		}
		else
		{
			Log.d(tag, "!!!support");
		}

		ArrayList<ApplicationInfo> appList = new ArrayList<ApplicationInfo>();
		List<ApplicationInfo> appicationinfoList = pm.getInstalledApplications(PackageManager.GET_META_DATA);
		for (ApplicationInfo applicationInfo : appicationinfoList)
		{
			if (applicationInfo.sourceDir.indexOf("/data/app/") != -1)
			{
				appList.add(applicationInfo);
			}
		}
		viewapps_listview.setAdapter((new MyAdapter(this, appList, checkedArray)));
		viewapps_listview.setOnItemClickListener(this);

		super.onStart();
	}

	@Override
	protected void onDestroy()
	{
		//unregisterReceiver(onDownloadComplete);
		super.onDestroy();
	}

	public void onClickBtnSubmit(final View v)
	{
		int count = ((MyAdapter) viewapps_listview.getAdapter()).getCheckedCount();
		if (count != 1)
		{
			Toast.makeText(v.getContext(), getString(R.string.none_choose), Toast.LENGTH_SHORT).show();
			return;
		}

		View layout = LayoutInflater.from(this).inflate(R.layout.dialog_login, null);
		alert = createLoginForm(layout);
		alert.show();

		alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				etusername = (EditText) alert.findViewById(R.id.etusername);
				etpassword = (EditText) alert.findViewById(R.id.etpassword);
				tvVerifyFail = (TextView) alert.findViewById(R.id.tvVerifyFail);
				pb = (ProgressBar) alert.findViewById(R.id.pb);
				pb.setVisibility(View.VISIBLE);

				tvVerifyFail.setVisibility(View.GONE);
				etusername.setVisibility(View.GONE);
				etpassword.setVisibility(View.GONE);

				try
				{
					Map<String, String> auth = new HashMap<String, String>();
					auth.put("url", TransferabilityModule.SERVER_URL + "/notary/api/auth");
					auth.put("username", etusername.getText().toString());
					auth.put("password", etpassword.getText().toString());
					auth.put("androidid", getDeviceID());
					auth.put("app_id", Hash.sha256(requestFile));
					auth.put("method", "post");
					auth.put("type", "text");
					login(auth); // verify user
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

		});
		alert.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				alert.dismiss();
			}
		});
		alert.getButton(AlertDialog.BUTTON_NEUTRAL).setVisibility(View.GONE);

		// Dialog dialog = new Dialog(this,
		// android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
		// dialog.setContentView(R.layout.dialog_login);
		// dialog.setCancelable(false);
		// dialog.show();
	}

	@Override
	public void onResume()
	{
		super.onResume();

		// Check to see that the Activity started due to an Android Beam
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction()))
		{
			Log.d(tag, "NDEF");
			processIntent(getIntent());
		}
		else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction()))
		{
			Log.d(tag, "TECH");
		}
		else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(getIntent().getAction()))
		{
			Log.d(tag, "TAG");
		}
	}

	/**
	 * Parses the NDEF Message from the intent and prints to the TextView
	 */
	void processIntent(Intent intent)
	{
		Log.d(tag, "NDEF1");
		Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		// only one message sent during the beam
		Log.d(tag, "NDEF2");
		NdefMessage msg = (NdefMessage) rawMsgs[0];
		Log.d(tag, "NDEF3");
		NdefRecord[] records = msg.getRecords();
		Log.d(tag, "NDEF4");
		Toast.makeText(this, new String(records[0].getPayload()), Toast.LENGTH_LONG).show();
		// record 0 contains the MIME type, record 1 is the AAR, if present
		// textView.setText(new String(msg.getRecords()[0].getPayload()));
	}

	@Override
	public NdefMessage createNdefMessage(NfcEvent event)
	{
		NdefMessage msg = null;
		String transferFile = "1.jpg";
		File extDir = Environment.getExternalStorageDirectory();
		try
		{
			FileInputStream fis = new FileInputStream(new File(extDir, transferFile));
			ArrayList<String> list = new ArrayList<String>();

			byte[] bytes = new byte[1];
			while (fis.read(bytes) != -1)
			{
				list.add(bytes.toString());
			}

			NdefRecord[] records = new NdefRecord[list.size()];
			for (int i = 0; i < records.length; i++)
			{
				records[i] = NdefRecord.createMime("application/edu.ntust.transferability", list.get(i).getBytes());
			}
			// msg = new NdefMessage(new NdefRecord[] {
			// NdefRecord.createMime("application/edu.ntust.transferability",
			// "AAA".getBytes()) });
			msg = new NdefMessage(records);
			fis.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return msg;
	}

	@Override
	public void serverResponse(JSONObject jobject)
	{
		Log.d(tag, jobject.toString());

		try
		{
			if (!jobject.isNull("login"))
			{
				if (jobject.get("login").equals("pass"))
				{

					// login success
					
					JSONObject eviFileDetail = new JSONObject();
					eviFileDetail.put("key", jobject.get("key"));
					eviFileDetail.put("p_id", jobject.get("p_id"));
					eviFileDetail.put("username", jobject.get("username"));
					eviFileDetail.put("app_id", jobject.get("app_id"));
					Log.d(tag, "!!!" + eviFileDetail.toString());
					
					Log.d(tag, "!!!" + this.getFilesDir().getPath() + "/transfer.txt");
					eviFile = new File(this.getFilesDir().getPath(), "transfer.txt");
					if (!eviFile.exists())
					{
						eviFile.createNewFile();
					}
					
					FileWriter fw = new FileWriter(eviFile);
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(eviFileDetail.toString()); // write transfer evidence
					bw.close();
					
					uploadAPK(requestFile, jobject.getString("key")); // upload which want to transfer APK
					return;
				}
			}
			else if (jobject.get("upload").equals("success"))
			{
				// upload success
				alert.dismiss();
				
				Intent i = new Intent();
				i.putExtra("package", requestFile);
				i.putExtra("appname", requestFileName);
				i.putExtra("evifile", eviFile);
				i.setClass(ViewAppsActivity.this, PushUriActivity.class);
				startActivity(i);
				
				return;
			}

			// upload process occur error. e.g. size limit, network
			// connection...
			if (!jobject.isNull("upload"))
			{
				tvVerifyFail.setText((CharSequence) jobject.get("upload"));
			}

		}
		catch (JSONException e)
		{
			Log.d(tag, "!!!" + e.toString());
			e.printStackTrace();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// login fail
		runOnUiThread(new Runnable()
		{
			public void run()
			{
				pb.setVisibility(View.GONE);
				tvVerifyFail.setVisibility(View.VISIBLE);
				etusername.setVisibility(View.VISIBLE);
				etpassword.setVisibility(View.VISIBLE);
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public void login(Map<String, String> map)
	{
		LoginTask hcTask = new LoginTask(this);
		hcTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, map);
	}

	@Override
	public void onItemClick(AdapterView<?> l, View v, int position, long arg3)
	{
		MyAdapter adapter = (MyAdapter) l.getAdapter();
		ApplicationInfo info = adapter.getList().get(position);
		Log.d(tag, info.loadLabel(this.getPackageManager()).toString());
		Log.d(tag, info.sourceDir);

		int count = adapter.getCheckedCount();

		CheckBox cb = (CheckBox) v.findViewById(R.id.cb);
		if (cb.isChecked())
		{
			cb.setChecked(false);
			adapter.getCheckedList().put(position, false);
		}
		else if (count == 0)
		{
			Toast.makeText(this, "mark " + info.loadLabel(this.getPackageManager()).toString(), Toast.LENGTH_SHORT).show();
			requestFile = new File(info.sourceDir);
			requestFileName = info.loadLabel(this.getPackageManager()).toString();
			cb.setChecked(true);
			adapter.getCheckedList().put(position, true);
		}
		else
		{
			Toast.makeText(v.getContext(), getString(R.string.double_choose), Toast.LENGTH_LONG).show();
		}
	}

}
