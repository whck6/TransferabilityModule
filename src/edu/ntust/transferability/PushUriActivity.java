package edu.ntust.transferability;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class PushUriActivity extends Activity
{

	private NfcAdapter nfcAdapter;

	private Uri[] uris = new Uri[1];
	private FileUriCallback mFileUriCallback;

	class FileUriCallback implements NfcAdapter.CreateBeamUrisCallback
	{
		public FileUriCallback()
		{
		}

		/**
		 * Create content URIs as needed to share with another device
		 */
		@Override
		public Uri[] createBeamUris(NfcEvent event)
		{
			return uris;
		}

	}

	@Override
	protected void onResume()
	{
		Log.d(ViewAppsActivity.tag, "!!! resume");
		super.onResume();
	}

	@Override
	protected void onPause()
	{
		Log.d(ViewAppsActivity.tag, "!!! pause");
		super.onPause();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setContentView(R.layout.activity_pushuri);
		extractBundle();

		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		// nfcAdapter.setNdefPushMessageCallback(this, this);

		mFileUriCallback = new FileUriCallback();
		nfcAdapter.setBeamPushUrisCallback(mFileUriCallback, this);
		nfcAdapter.setOnNdefPushCompleteCallback(new OnNdefPushCompleteCallback()
		{
			
			@Override
			public void onNdefPushComplete(NfcEvent event)
			{
				finish();
			}
		}, this);

		// Toast.makeText(this, getString(R.string.verify_pass),
		// Toast.LENGTH_SHORT).show();


		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart()
	{
		runBackgroundAnimation();
		super.onStart();
	}

	private void extractBundle()
	{
		Bundle extras = getIntent().getExtras();
		if (extras != null)
		{
			File requestFile = (File) extras.get("package");
//			requestFile.setReadable(true, false);
//			uris[0] = Uri.fromFile(requestFile);
			TextView tvRequestFile = (TextView) findViewById(R.id.tvRequestFile);
			tvRequestFile.setText((CharSequence) (requestFile.toString()));

			String appname = extras.getString("appname");
			TextView tvHint = (TextView) findViewById(R.id.tvHint);
			tvHint.setText((CharSequence) appname);

			File eviFile = (File) extras.get("evifile");
			eviFile.setReadable(true, false);
			uris[0] = Uri.fromFile(eviFile);
//			nfcAdapter.setBeamPushUris(uris, this);
		}
	}

	private void runBackgroundAnimation()
	{
		ImageView iv = (ImageView) findViewById(R.id.iv);
		iv.setBackgroundResource(R.drawable.animation);

		final AnimationDrawable animation = (AnimationDrawable) iv.getBackground();

		Timer timer = new Timer();
		timer.schedule(new TimerTask()
		{

			@Override
			public void run()
			{
				animation.start();
			}
		}, 100);
	}
}
