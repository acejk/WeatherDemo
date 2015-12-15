package com.ace.weathertest.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2015/12/15 0015.
 */
public class HttpUtil {
    public static void sendHttpRequest(final String address, final HttpCallBackListener listener) {
        new Thread(){
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL httpUrl = new URL(address);
                    connection = (HttpURLConnection) httpUrl.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);
                    InputStream in = connection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line = null;
                    while((line = br.readLine()) != null) {
                            response.append(line);
                    }

                    if(listener != null) {
                        listener.onfinish(response.toString());
                    }


                } catch (MalformedURLException e) {
                    e.printStackTrace();

                    if(listener != null) {
                        listener.onError(e);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }.start();
    }
}
