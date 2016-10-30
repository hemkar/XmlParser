package com.example.hemkar.topten;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {
    private String mFileContents;
    private Button btnParser;
    private ListView listApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnParser = (Button) findViewById(R.id.btnParse);
        listApps=(ListView) findViewById(R.id.top10ListView);
        btnParser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseApplication parseApplication= new ParseApplication(mFileContents);
                parseApplication.process();
                ArrayAdapter<Applications> arrayadapter= new ArrayAdapter<Applications>(MainActivity.this,
                        R.layout.list_item,parseApplication.getApplications());
                listApps.setAdapter(arrayadapter);

            }
        });

        DownloadData downloadData=new DownloadData();
        downloadData.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml");
    }


    private class DownloadData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            mFileContents = downloadXMLFile(params[0]);
            if(mFileContents == null) {
                Log.d("DownloadData", "Error downloading");
            }

            return mFileContents;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("DownloadData", "Result was: " + result);
        }

        private String downloadXMLFile(String urlPath) {
            StringBuilder tempBuffer = new StringBuilder();
            try {
                URL url = new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                Log.d("DownloadData", "The response code was " + response);
                InputStream is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);

                int charRead;
                char[] inputBuffer = new char[500];
                while(true) {
                    charRead = isr.read(inputBuffer);
                    if(charRead <=0) {
                        break;
                    }
                    tempBuffer.append(String.copyValueOf(inputBuffer, 0, charRead));
                }

                return tempBuffer.toString();


            } catch(IOException e) {
                Log.d("DownloadData", "IO Exception reading data: " + e.getMessage());
//                e.printStackTrace();
            } catch(SecurityException e) {
                Log.d("DownloadData", "Security exception. Needs permissions? " + e.getMessage());
            }

            return null;
        }
    }
}