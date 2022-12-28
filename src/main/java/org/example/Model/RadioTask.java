package org.example.Model;

import org.json.JSONObject;
import org.json.XML;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.TimerTask;

/**
 * Abstract class representing a task used to get something from the API.
 *
 * @author Olivia Harlin
 * @umu-id olha0073
 * @çs-id id19ohn
 *
 * @version 1.0
 * @date 23-01-09
 */
public abstract class RadioTask extends TimerTask {
    private HttpURLConnection connection;
    private URL url;
    private JSONObject object;

    /**
     * Class constructor.
     * @param connection - The established connection.
     * @param url - the url used to connect to the API.
     */
    public RadioTask(HttpURLConnection connection, URL url){
        this.connection = connection;
        this.url = url;

    }

    /**
     * Creates the connection to the API and converts
     * the XML object to JSON.
     */
    public void run() {
        int response = 0;
        try {
            response = connection.getResponseCode();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (response != 200) {
            throw new RuntimeException("HttpResponseCode: " + response);

        } else { // if successful

            StringBuilder stringBuilder = new StringBuilder();
            Scanner scanner;
            try {
                scanner = new Scanner(url.openStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            while (scanner.hasNext()) {
                stringBuilder.append(scanner.nextLine());
            }

            scanner.close();

            object = XML.toJSONObject(String.valueOf(stringBuilder));

        }
    }

    /**
     * Returns the JSONObjects.
     * @return the JSONObject
     */
    public JSONObject getObject(){
        return object;
    }
}