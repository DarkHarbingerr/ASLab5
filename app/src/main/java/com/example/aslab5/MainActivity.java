package com.example.aslab5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Dialog myDialog;

    private Button _openPDFBtn;
    private Button _downloadPDFBtn;
    private TextView _progressTextView;
    private TextView _totalWeightTextVeiw;
    private ProgressBar _progressBar;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String CheckBox_Key = "checkBoxIsChecked";
    private boolean isChecked = false;


    private String _url = "https://ntv.ifmo.ru/file/journal/1.pdf";
    private File _path;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDialog = new Dialog(this);
        LoadData();
        if(!isChecked){
            ShowPopUp();
        }

        _openPDFBtn = findViewById(R.id.open_pdf_btn);
        _downloadPDFBtn = findViewById(R.id.download_pdf_btn);
        _progressTextView = findViewById(R.id.progress_tw);
        _totalWeightTextVeiw = findViewById(R.id.total_tw);
        _progressBar = findViewById(R.id.progress_bar);

        _path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)  + File.separator + "ASLab5PDF");

        boolean _succes;
        if(!_path.exists())
            _succes = _path.mkdir();
        else
            _succes = true;
        if(!_succes)
            Toast.makeText(this, "Directory not found or not created", Toast.LENGTH_LONG).show();

        View.OnClickListener onClickListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent;
                switch (v.getId())
                {
                    case R.id.open_pdf_btn:
                        intent = new Intent(MainActivity.this, FilesContentList.class);
                        startActivity(intent);
                        break;
                    case R.id.download_pdf_btn:
                        DownloadFile();
                        break;
                }
            }
        };

        _downloadPDFBtn.setOnClickListener(onClickListener);
        _openPDFBtn.setOnClickListener(onClickListener);
    }

    private boolean IsConnection(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    private void DownloadFile()
    {
        _url = "https://ntv.ifmo.ru/file/journal/" + 1 + (int)(Math.random() * 100) + ".pdf";
        if(IsConnection(this))
        {
            new Downloader().execute(_url, _path.toString());
        } else {
            Toast.makeText(this, "No Internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void ShowPopUp()
    {
        myDialog.setContentView(R.layout.custom_popup);
        Button closePopUpBtn = myDialog.findViewById(R.id.popup_close_btn);
        final CheckBox checkBox = myDialog.findViewById(R.id.popup_checkBox);

        closePopUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                SaveData(checkBox.isChecked());
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    private void SaveData(boolean isChecked){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(CheckBox_Key, isChecked);
        editor.apply();
    }

    private void LoadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        isChecked = sharedPreferences.getBoolean(CheckBox_Key, false);
    }

    public class Downloader extends AsyncTask<String, String, Void> {
        private String fileName;
        private int totalSize;
        private int downloadedSize;
        private float percentProgress;
        private boolean isDownload = false;
        @Override
        protected Void doInBackground(String... params) {
            String stringURL = params[0];
            String filePath = params[1];
            try {
                URL sourceURL = new URL(stringURL);
                HttpURLConnection connection = (HttpURLConnection) sourceURL.openConnection();

                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    isDownload = true;
                    fileName = sourceURL.getFile().substring(sourceURL.getFile().lastIndexOf('/') + 1);

                    File filePDF = new File(filePath, fileName);
                    if (!filePDF.exists()) {
                        try {
                            filePDF.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    InputStream inputStream = connection.getInputStream();
                    FileOutputStream fileOutputStream = new FileOutputStream(filePDF);


                    totalSize = connection.getContentLength();
                    byte[] buffer = new byte[totalSize];
                    int bufferLength = -1;
                    while ((bufferLength = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, bufferLength);
                        downloadedSize += bufferLength;
                        percentProgress = ((float) downloadedSize / totalSize) * 100;
                        publishProgress(String.valueOf(totalSize / 1024), String.valueOf(percentProgress));
                    }

                    fileOutputStream.close();
                }
                } catch(IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            int progress = (int) Float.parseFloat(values[1]);
            _progressTextView.setText(String.valueOf(progress) + "%");
            _progressBar.setProgress(progress);
            _totalWeightTextVeiw.setText("Total Weight: " + values[0] + " KB");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(!isDownload){
                Toast.makeText(MainActivity.this, "File not found", Toast.LENGTH_SHORT).show();
            }
        }
    }




}

