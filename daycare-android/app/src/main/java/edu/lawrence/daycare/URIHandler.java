package edu.lawrence.daycare;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Joe Gregg on 10/17/2016.
 */

public class URIHandler {

    public static final String hostName = "10.0.2.2:8085";
    //public static final String hostName = "143.44.72.125:8085"; // real device, real host

    public static String doGet(String uri,String failure) {
        InputStream is = null;

        try {
            Log.d("Daycare","GET uri: " + uri);
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            if (response != 200)
                return failure;

            ByteArrayOutputStream sink = new ByteArrayOutputStream();
            copy(conn.getInputStream(), sink, 3000);
            byte[] buffer = sink.toByteArray();
            String result = new String(buffer, "UTF8");
            Log.d("Daycare","Received: " + result);
            return result;

        } catch(Exception ex) {
            Log.d("Daycare","Exception in doGet: "+ex.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                }
            }
        }
        return failure;
    }

    public static String doPut(String uri, String data) {
        InputStream is = null;

        try {
            Log.d("Daycare","PUT uri: " + uri);
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            Log.d("Daycare","PUT data: " + data);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(data.getBytes());
            os.flush();

            // Starts the query
            conn.connect();

            is = conn.getInputStream();
            int response = conn.getResponseCode();
            if (response != 200)
                return "";

            ByteArrayOutputStream sink = new ByteArrayOutputStream();
            copy(conn.getInputStream(), sink, 3000);
            byte[] buffer = sink.toByteArray();
            String result = new String(buffer, "UTF8");
            Log.d("Daycare","Received: " + result);
            return result;
        } catch(Exception ex) {
            Log.d("Daycare","Exception in doPut: "+ex.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                }
            }
        }
        return "";
    }

    public static String doPost(String uri, String data) {
        InputStream is = null;

        try {
            Log.d("Daycare","Post uri: " + uri);
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            Log.d("Daycare","Posted: " + data);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(data.getBytes());
            os.flush();

            // Starts the query
            conn.connect();

            is = conn.getInputStream();
            int response = conn.getResponseCode();
            if (response != 200)
                return "";

            ByteArrayOutputStream sink = new ByteArrayOutputStream();
            copy(conn.getInputStream(), sink, 3000);
            byte[] buffer = sink.toByteArray();
            String result = new String(buffer, "UTF8");
            Log.d("Daycare","Received: " + result);
            return result;
        } catch(Exception ex) {
            Log.d("Daycare","Exception in doPost: "+ex.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                }
            }
        }
        return "";
    }

    public static void doDelete(String uri) {
        try {
            Log.d("Daycare","DELETE uri: " + uri);
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");

            // Starts the query
            conn.connect();
            int responseCode = conn.getResponseCode();
        } catch (Exception ex) {

        }
    }

    /* Helper method to assist with reading chunked data. */
    private static void copy(InputStream in, OutputStream out , int bufferSize)
            throws IOException
    {
        // Read bytes and write to destination until eof
        byte[] buf = new byte[bufferSize];
        int len = 0;
        while ((len = in.read(buf)) >= 0)
        {
            out.write(buf, 0, len);
        }
    }
}
