package org.example.Model;

import org.json.JSONArray;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Class representing the task used to get all channels.
 *
 * @author Olivia Harlin
 * @umu-id olha0073
 * @çs-id id19ohn
 *
 * @version 1.0
 * @date 23-01-09
 */
public class ChannelTask extends RadioTask {
    private ArrayList<Channel> channelList;

    /**
     * Class constructor.
     * @param connection - The established connection.
     * @param url - the url used to connect to the API.
     */
    public ChannelTask(HttpURLConnection connection, URL url){
        super(connection, url);
    }

    /**
     * Performs the task.
     */
    @Override
    public void run() {

        super.run();

        JSONObject sr = super.getObject().getJSONObject("sr");
        JSONObject channels = sr.getJSONObject("channels");
        JSONArray channelArray = channels.getJSONArray("channel");

        channelList = new ArrayList<>();

        // gets all channels and saves them in a list
        for(int i = 0 ; i < channelArray.length() ; i++){
            JSONObject temp = channelArray.getJSONObject(i);
            Channel channel = null;
            try {
                channel = new Channel(temp.getString("name"), temp.getInt("id"));
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            channelList.add(channel);

        }
    }

    /**
     * Gets all channels.
     * @return all channels in an array list
     */
    public ArrayList<Channel> getChannels(){
        return channelList;
    }

}
