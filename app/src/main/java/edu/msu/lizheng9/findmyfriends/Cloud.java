package edu.msu.lizheng9.findmyfriends;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Kevin on 4/30/2018.
 */

public class Cloud {
    private static String SET_USER_URL = "http://webdev.cse.msu.edu/~lizheng9/cse476/project3/login.php";
    public String xmlJsonArray = "";
    public static void logStream(InputStream stream) {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream));

        Log.e("476", "logStream: If you leave this in, code after will not work!");
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                Log.e("476", line);
            }
        } catch (IOException ex) {
            return;
        }
    }


    public boolean User(String username, String deviceId){
        if((username.length() ==0 )){
            return false;
        }
        String query, url_ = "";
        query = SET_USER_URL + "?user=" + username + "&device=" + deviceId;

        InputStream stream = null;
        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK) {
                return false;
            }

            stream = conn.getInputStream();
          //  logStream(stream);
        } catch (MalformedURLException e) {
            // Should never happen
            return false;
        } catch (IOException ex) {
            return false;
        }

        try {
            XmlPullParser xml = Xml.newPullParser();
            xml.setInput(stream, "UTF-8");

            xml.nextTag();      // Advance to first tag
            xml.require(XmlPullParser.START_TAG, null, "fmf");

            String status = xml.getAttributeValue(null, "status");
            if(status.equals("no")) {
                return false;
            }
            // We are done
        } catch(XmlPullParserException ex) {
            return false;
        } catch(IOException ex) {
            return false;
        }finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public boolean getUsers(){

        String query, url_ = "";
        query = "http://webdev.cse.msu.edu/~lizheng9/cse476/project3/locations.php";

        InputStream stream = null;
        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK) {
                return false;
            }

            stream = conn.getInputStream();
            //  logStream(stream);
        } catch (MalformedURLException e) {
            // Should never happen
            return false;
        } catch (IOException ex) {
            return false;
        }

        try {
            XmlPullParser xml = Xml.newPullParser();
            xml.setInput(stream, "UTF-8");

            xml.nextTag();      // Advance to first tag
            xml.require(XmlPullParser.START_TAG, null, "fmf");

            String status = xml.getAttributeValue(null, "status");
            if(status.equals("no")) {
                return false;
            }
            xmlJsonArray = xml.getAttributeValue(null,"xmlJsonArray");
            // We are done
        } catch(XmlPullParserException ex) {
            return false;
        } catch(IOException ex) {
            return false;
        }finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
