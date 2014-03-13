package com.enestit.apps.android.printcontacts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.contactpdf.R;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ContactsToPdf extends Activity implements OnClickListener {

	Button convertToPdf, saveToStorage, saveToEmail;
	JSONObject obj;
	JSONObject contactsObj = new JSONObject();
	JSONArray arobj = new JSONArray();
	int a = 0;
	String name, number, email;
	File path, file;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contactstopdf);
		initialize();
	}

	@SuppressLint("NewApi")
	private void initialize() {
		// TODO Auto-generated method stub
		convertToPdf = (Button) findViewById(R.id.convert);
		saveToStorage = (Button) findViewById(R.id.saveAsStorage);
		saveToEmail = (Button) findViewById(R.id.saveToEmail);
		saveToEmail.setOnClickListener(this);
		saveToStorage.setOnClickListener(this);
		convertToPdf.setOnClickListener(this);
		path = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		if (!path.exists()) {
			path.mkdirs();
		}
		file = new File(path, "Contacts.pdf");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.convert: {
			getNumber(this.getContentResolver());
			saveToEmail.setVisibility(View.VISIBLE);
			saveToStorage.setVisibility(View.VISIBLE);
			break;
		}
		case R.id.saveAsStorage: {
			generatePDF();
			break;
		}
		case R.id.saveToEmail: {
			sendPdf();
			break;
		}
		}
	}

	private void generatePDF() {
		// TODO Auto-generated method stub

		Document doc = new Document();
		try {

			if (!path.exists()) {
				path.mkdirs();
			}
			FileOutputStream fos = new FileOutputStream(file);
			PdfWriter.getInstance(doc, fos);
			doc.open();
			Paragraph p1 = new Paragraph("Your contact List :");
			p1.setAlignment(Element.ALIGN_CENTER);
			doc.add(p1);
			for (int a = 0; a < arobj.length(); a++) {
				JSONObject temp1 = arobj.getJSONObject(a);
				name = temp1.getString("name");
				number = temp1.getString("number");
				Paragraph p2 = new Paragraph("Contact Name = " + name
						+ "  and Contact Number = " + number);
				p2.setAlignment(Element.ALIGN_LEFT);
				doc.add(p2);
			}
		} catch (DocumentException de) {
			Log.e("PDFCreator", "DocumentException:" + de);
		} catch (IOException e) {
			Log.e("PDFCreator", "ioException:" + e);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			Toast.makeText(getApplicationContext(),
					"PDF Sucessfully created at " + path, 500).show();
			doc.close();
		}
	}

	@SuppressLint("InlinedApi")
	private void getNumber(ContentResolver cr2) {
		// TODO Auto-generated method stub
		Cursor cur = cr2.query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
				null, null);
		while (cur.moveToNext()) {
			try {
				obj = new JSONObject();
				obj.put("name",
						cur.getString(cur
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
				obj.put("number",
						cur.getString(cur
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
				arobj.put(a, obj);
				a++;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			contactsObj.put("contacts", arobj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			Toast.makeText(ContactsToPdf.this, "done", 500).show();
			cur.close();
		}
	}

	private void sendPdf() {
		// TODO Auto-generated method stub
		Log.d("url", Uri.parse("file://" + file) + "");
		Intent email = new Intent(android.content.Intent.ACTION_SEND);
		email.setType("plain/text");
		email.putExtra(android.content.Intent.EXTRA_EMAIL, "abc@mail.com");
		email.putExtra(android.content.Intent.EXTRA_SUBJECT, "Contacts backup");
		email.putExtra(android.content.Intent.EXTRA_STREAM,
				Uri.parse("file://" + file));
		email.putExtra(android.content.Intent.EXTRA_TEXT,
				"This is your contact backup");
		startActivity(email);
	}

}