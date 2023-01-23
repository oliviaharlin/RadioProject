package org.example.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;

/**
 * Class representing the task used to get all programs for a specific channel.
 *
 * @author Olivia Harlin
 * @umu-id olha0073
 * @çs-id id19ohn
 *
 * @version 1.0
 * @date 23-01-09
 */
public class ProgramTask extends RadioTask {
    private ArrayList<Program> programList = new ArrayList<>();
    private Consumer<ArrayList<Program>> callback;
    private ArrayList<Program> newList;
    private LocalDateTime today = LocalDateTime.now();
    private Program program;

    /**
     * Class constructor.
     * @param connection - the established connection
     * @param url - the url used to connect to the API
     * @param callback - the callback used to return the programs
     */
    public ProgramTask(HttpURLConnection connection, URL url, Consumer<ArrayList<Program>> callback){
        super(connection, url);
        this.callback = callback;
    }

    /**
     * Performs the task.
     */
    @Override
    public void run() {

        super.run();

        try {
            newList = new ArrayList<>();
            JSONObject sr = super.getObject().getJSONObject("sr");
            JSONObject programs = sr.getJSONObject("schedule");
            JSONArray programArray = programs.getJSONArray("scheduledepisode");

            // get all programs
            allPrograms(programArray);

            // sort out programs within time range (6h before - 12 after) and return through callback
            ArrayList<Program> returnList = programsWithinTimeRange();

            callback.accept(returnList);

        } catch (JSONException | MalformedURLException | URISyntaxException e) {
            System.out.println(e);
            try {
                // create temporary objects for indicating that no programs exist
                newList.add(new Program("tomt", "test", "2022-02-02T00:00:00Z", "2022-02-02T00:00:00Z", "https://upload.wikimedia.org/wikipedia/commons/1/11/Blue_question_mark_icon.svg"));
                callback.accept(newList);

            } catch (URISyntaxException | MalformedURLException ex) {
                System.out.println(ex);
            }
        }

    }


    /**
     * Gets all programs for the current channel.
     * @param programArray - the JSONArray containing the programs as JSONObjects
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    private void allPrograms(JSONArray programArray) throws MalformedURLException, URISyntaxException {
        for (int i = 0; i < programArray.length(); i++) {
            JSONObject temp = programArray.getJSONObject(i);
            if(temp.has("imageurl")) {
                program = new Program(temp.getString("title"), temp.getString("description"), temp.getString("starttimeutc"), temp.getString("endtimeutc"), temp.getString("imageurl"));
            } else{
                program = new Program(temp.getString("title"), temp.getString("description"), temp.getString("starttimeutc"), temp.getString("endtimeutc"), "https://upload.wikimedia.org/wikipedia/commons/1/11/Blue_question_mark_icon.svg");
            }
            programList.add(program);
        }
    }

    /**
     * Gets all programs within the time range (6h before - 12h after current time).
     * @return an array list containing the programs within the time range
     */
    private ArrayList<Program> programsWithinTimeRange(){
        LocalDateTime firstLimit = today.minus(Duration.ofHours(6));
        LocalDateTime secondLimit = today.plus(Duration.ofHours(12));
        ArrayList<Program> newList = new ArrayList<>();

        for (int i = 0 ; i < programList.size() ; i++){
            if(programList.get(i).getStartTime().isAfter(firstLimit) && programList.get(i).getStartTime().isBefore(secondLimit)){
                newList.add(programList.get(i));
            }
        }

        return newList;
    }

}
