package com.Hackathon.EB_Bill_Calculator;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class calculateAsyncTask extends AsyncTask<String,Void, String>{

    private ICallback callback;

    public calculateAsyncTask(ICallback callback,Context context) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... url) {
        String responseString = null;
        try {
            URL getUrl = new URL(url[0]);
            HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
            connection.setConnectTimeout(50000);
            connection.setReadTimeout(50000);
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()),8192);
            responseString = input.readLine();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String responseString) {
        callback.OnTaskComplete(responseString);
    }
}
