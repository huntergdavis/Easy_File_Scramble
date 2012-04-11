package com.hunterdavis.easyfilescramble;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

public class EasyFileScramble extends Activity {

	int SELECT_FILE = 122;

	String filePath = "";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Create an anonymous implementation of OnClickListener
		OnClickListener loadButtonListner = new OnClickListener() {
			public void onClick(View v) {
				// do something when the button is clicked

				// in onCreate or any event where your want the user to
				Intent intent = new Intent(v.getContext(), FileDialog.class);
				intent.putExtra(FileDialog.START_PATH, "/sdcard");
				startActivityForResult(intent, SELECT_FILE);

			}
		};

		// Create an anonymous implementation of OnClickListener
		OnClickListener saveButtonListner = new OnClickListener() {
			public void onClick(View v) {
				// do something when the button is clicked

				scrambleFile();

			}
		};

		Button loadButton = (Button) findViewById(R.id.loadButton);
		loadButton.setOnClickListener(loadButtonListner);

		Button saveButton = (Button) findViewById(R.id.scramblebutton);
		saveButton.setOnClickListener(saveButtonListner);

		// Look up the AdView as a resource and load a request.
		AdView adView = (AdView) this.findViewById(R.id.adView);
		adView.loadAd(new AdRequest());

	}

	public void scrambleFile() {

		// get a handle to the name
		// read in .old file
		// write out file
		// delete .old file?

		
		// rename file to .old
		String fileString = filePath;
		File oldfile = new File(fileString);
		File newfile = new File(fileString + ".old");
		oldfile.renameTo(newfile);
		
		FileInputStream is = null;
		try {
			is = new FileInputStream(fileString + ".old");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// now try to open the first output file
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(fileString);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		byte[] buffer = new byte[8 * 1024];
		byte[] opposite = new byte[8 * 1024];
		int read; 
		byte tempbyte;
		try {
			while ((read = is.read(buffer)) > 0) {
				for (int i = 0; i < read; i++) {
					tempbyte = buffer[i]; 
					opposite[i] = (byte) ~tempbyte;
				}
				
				try {
					os.write(opposite,0,read);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		newfile.delete();
		
		Toast.makeText(getBaseContext(),
				"Scrambled or Descrambled " + fileString, Toast.LENGTH_SHORT)
				.show();

	}

	public void saveMD5File() {
		String md5FileName = filePath + ".MD5";
		EditText t = (EditText) findViewById(R.id.mdfive);
		String md5ActualText = t.getText().toString();
		String fileNameString = getFileName();
		String fileString = md5ActualText + " *" + fileNameString;

		OutputStream os = null;

		// now try to open the first output file
		try {
			os = new FileOutputStream(md5FileName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			os.write(fileString.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Toast.makeText(getBaseContext(),
				"Scrambled or Descrambled " + md5FileName, Toast.LENGTH_SHORT)
				.show();

	}

	public String getFileName() {
		int slashloc = filePath.lastIndexOf("/");
		if (slashloc < 0) {
			return filePath;
		} else {
			return filePath.substring(slashloc + 1);
		}
	}

	public void onActivityResult(final int requestCode, int resultCode,
			final Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == SELECT_FILE) {
				filePath = data.getStringExtra(FileDialog.RESULT_PATH);
				// set the filename txt
				changeFileNameText(filePath);
				Button enabButton = (Button) findViewById(R.id.scramblebutton);
				enabButton.setEnabled(true);
			}
		} else if (resultCode == RESULT_CANCELED) {
		}
	}

	public void changeFileNameText(String newFileName) {
		TextView t = (TextView) findViewById(R.id.fileText);
		t.setText(newFileName);
	}

	public static String md5(String fileString) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			InputStream is = null;
			try {
				is = new FileInputStream(fileString);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			byte[] buffer = new byte[8 * 1024];
			int read;
			try {
				while ((read = is.read(buffer)) > 0) {
					digest.update(buffer, 0, read);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String h = Integer.toHexString(0xFF & messageDigest[i]);
				while (h.length() < 2)
					h = "0" + h;
				hexString.append(h);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

}