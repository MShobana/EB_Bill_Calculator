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

public class calculateAsyncTask extends AsyncTask<Object,Void, Integer>{

    private int responseCode;
    private Context context;
    private StringBuilder response=new StringBuilder();

    @Override
    protected Integer doInBackground(Object... url) {
        try {
            URL getUrl = new URL((String)url[0]);
            HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
            connection.setConnectTimeout(50000);
            connection.setReadTimeout(50000);
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            responseCode = connection.getResponseCode();
            BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()),8192);
            String strLine = null;
            while ((strLine = input.readLine()) != null)
            {
                response.append(strLine);
            }
            String responseString = response.toString();
            JSONObject responseJson = new JSONObject(responseString);
            Integer cost = responseJson.getInt("cost");
            context=(Context)url[1];
            return cost;
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Integer cost) {
        Toast toast = Toast.makeText(context, cost.toString(), 1000);
        toast.show();
    }
}
