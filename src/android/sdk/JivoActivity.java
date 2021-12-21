package com.sevstar.jivosite.sdk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//************************/

public class JivoActivity extends Activity implements JivoDelegate{

	private static final ResourceHelper _R = ResourceHelper.INSTANCE;
	private static String TAG = "JivoActivity";
	private static String file_type     = "image/*";    // file types to be allowed for upload
	private final static int asw_file_req = 1;

	JivoSdk jivoSdk;
	String userToken = "";
	String payload = "";
	private String asw_pcam_message,asw_vcam_message;
	private ValueCallback<Uri> asw_file_message;
	private ValueCallback<Uri[]> asw_file_path;
	private boolean asw_wait_permissions = false;

	private WebView webView;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		Log.d(TAG, "onAcitivityResult");

		if (Build.VERSION.SDK_INT >= 21) {
			Uri[] results = null;
			if (resultCode == Activity.RESULT_CANCELED) {
				if (requestCode == asw_file_req) {
					// If the file request was cancelled (i.e. user exited camera),
					// we must still send a null value in order to ensure that future attempts
					// to pick files will still work.
					asw_file_path.onReceiveValue(null);
					return;
				}
			}
			if (resultCode == Activity.RESULT_OK) {
				if (requestCode == asw_file_req) {
					if (null == asw_file_path) {
						return;
					}
					ClipData clipData;
					String stringData;
					try {
						clipData = intent.getClipData();
						stringData = intent.getDataString();
					}catch (Exception e){
						clipData = null;
						stringData = null;
					}

					if (clipData == null && stringData == null && (asw_pcam_message != null || asw_vcam_message != null)) {
						results = new Uri[]{Uri.parse(asw_pcam_message != null ? asw_pcam_message:asw_vcam_message)};

					} else {
						if (null != clipData) { // checking if multiple files selected or not
							final int numSelectedFiles = clipData.getItemCount();
							results = new Uri[numSelectedFiles];
							for (int i = 0; i < clipData.getItemCount(); i++) {
								results[i] = clipData.getItemAt(i).getUri();
							}
						} else {
							results = new Uri[]{Uri.parse(stringData)};
						}
					}
				}
			}
			asw_file_path.onReceiveValue(results);
			asw_file_path = null;

		} else {
			if (requestCode == asw_file_req) {
				if (null == asw_file_message) return;
				Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
				asw_file_message.onReceiveValue(result);
				asw_file_message = null;
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle b = this.getIntent().getExtras();
		ActionBar a =  this.getActionBar();

		if (b != null) {
			if (b.containsKey("userToken")) {
				this.userToken = b.getString("userToken");
			}
			if (b.containsKey("payload")) {
				this.payload = b.getString("payload");
			}
			if (b.containsKey("activityTitle")) {
				a.setTitle(b.getString("activityTitle"));
			}
			if (b.containsKey("backButton") && b.getBoolean("backButton")) {
				a.setDisplayHomeAsUpEnabled(true);
			}
			if (b.containsKey("hideActivityTitle") && b.getBoolean("hideActivityTitle")) {
				requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
			}
		}

		String package_name = getApplication().getPackageName();
		setContentView(getApplication().getResources().getIdentifier("activity_jivo_web_view", "layout", package_name));

		String lang = Locale.getDefault().getLanguage().indexOf("ru") >= 0 ? "ru" : "en";

		//*********************************************************
		webView = (WebView) findViewById(_R.getIdentifier("jivowebview"));

		jivoSdk = new JivoSdk(webView, lang);
		jivoSdk.delegate = this;
		jivoSdk.prepare();
	}

	//*********************************************
	@Override
	public void onEvent(String name, String data) {
		if(name.equals("url.click")){
			if(data.length() > 2){
				String url = data.substring(1, data.length() - 1);
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				startActivity(browserIntent);
			}
		}
		if (name.equals("chat.ready")) {
			if (this.userToken.length() > 0){
				jivoSdk.execJS("setUserToken('" + this.userToken + "');");
			}
		}
		if (name.equals("page.ready")) {
			if (this.payload.length() > 0) {
				jivoSdk.execJS("setPayload(" + this.payload + ");");
			}
			String lang = Locale.getDefault().getLanguage().indexOf("ru") >= 0 ? "ru" : "en";
			jivoSdk.execJS(String.format("setData('%s', %s);", lang, Build.VERSION.SDK_INT <= 21 ? "false" : "true"));
		}
		if (name.equals("page.reload")) {
			webView.post( () -> jivoSdk.loadPage());
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		WebView wv = (WebView) findViewById(_R.getIdentifier("jivowebview"));
		wv.getSettings().setJavaScriptEnabled(false);
		wv.destroy();
		finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1) {
			asw_wait_permissions = false;
			if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
				showFileUploadIntent();
			} else {
				asw_file_path = null;
			}
		}
	}

	/*-- checking and asking for required file permissions --*/
	public boolean file_permission() {
		if(Build.VERSION.SDK_INT >= 21 && (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
			ActivityCompat.requestPermissions(JivoActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
			asw_wait_permissions = true;
			return false;
		}else{
			return true;
		}
	}

	//Creating image file for upload
	private File create_image() throws IOException {
		@SuppressLint("SimpleDateFormat")
		String file_name    = new SimpleDateFormat("yyyy_mm_ss").format(new Date());
		String new_name     = "file_"+file_name+"_";
		File sd_directory   = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		return File.createTempFile(new_name, ".jpg", sd_directory);
	}

	//Creating video file for upload
	private File create_video() throws IOException {
		@SuppressLint("SimpleDateFormat")
		String file_name    = new SimpleDateFormat("yyyy_mm_ss").format(new Date());
		String new_name     = "file_"+file_name+"_";
		File sd_directory   = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		return File.createTempFile(new_name, ".3gp", sd_directory);
	}

	private void showFileUploadIntent() {
		Intent takePictureIntent = null;
		Intent takeVideoIntent = null;

		takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePictureIntent.resolveActivity(JivoActivity.this.getPackageManager()) != null) {
			File photoFile = null;
			try {
				photoFile = create_image();
				takePictureIntent.putExtra("PhotoPath", asw_pcam_message);
			} catch (IOException ex) {
				Log.e(TAG, "Image file creation failed", ex);
			}
			if (photoFile != null) {
				asw_pcam_message = "file:" + photoFile.getAbsolutePath();
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
			} else {
				takePictureIntent = null;
			}
		}

		takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		if (takeVideoIntent.resolveActivity(JivoActivity.this.getPackageManager()) != null) {
			File videoFile = null;
			try {
				videoFile = create_video();
			} catch (IOException ex) {
				Log.e(TAG, "Video file creation failed", ex);
			}
			if (videoFile != null) {
				asw_vcam_message = "file:" + videoFile.getAbsolutePath();
				takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));
			} else {
				takeVideoIntent = null;
			}
		}

		Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
		contentSelectionIntent.setType(file_type);

		Intent[] intentArray;
		if (takePictureIntent != null && takeVideoIntent != null) {
			intentArray = new Intent[]{takePictureIntent, takeVideoIntent};
		} else if (takePictureIntent != null) {
			intentArray = new Intent[]{takePictureIntent};
		} else if (takeVideoIntent != null) {
			intentArray = new Intent[]{takeVideoIntent};
		} else {
			intentArray = new Intent[0];
		}

		Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
		chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
		chooserIntent.putExtra(Intent.EXTRA_TITLE, "Выберите файл");
		chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
		startActivityForResult(chooserIntent, asw_file_req);
	}

	class MyWebChromeClient extends WebChromeClient {
		// handling input[type="file"]
		public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams){
			asw_file_path = filePathCallback;

			if(JivoActivity.this.file_permission()) {
				JivoActivity.this.showFileUploadIntent();
			}
			return true;
		}
	}
}
