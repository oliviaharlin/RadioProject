package org.example.Model;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Timer;

/**
 * Class representing a radio channel.
 *
 * @author Olivia Harlin
 * @umu-id olha0073
 * @çs-id id19ohn
 *
 * @version 1.0
 * @date 23-01-09
 */
public class Channel {
    private String name;
    private int id;
    private ArrayList<Program> programs;
    private Boolean fetchedPrograms;
    private String[][] data;
    private URL channelURL;
    private LocalDate today = LocalDate.now();
    private ArrayList<URL> images;

    /**
     * Class constructor. Creates a new radio channel and sets request URL.
     * @param name - the name of the channel
     * @param id - the channel ID
     * @throws MalformedURLException
     */
    public Channel(String name, int id) throws MalformedURLException {
        this.name = name;
        this.id = id;
        fetchedPrograms = false;
        programs = new ArrayList<>();
        channelURL = new URL("http://api.sr.se/api/v2/scheduledepisodes?channelid=" + id +"&pagination=false&date="+today);
    }

    /**
     * Gets the channel name.
     * @return the channel name
     */
    public String getName(){
        return name;
    }

    /**
     * Gets the channels current programs and saves them in
     * an arraylist.
     * @throws IOException
     */
    public void setPrograms() throws IOException{

        HttpURLConnection programConnection = (HttpURLConnection) channelURL.openConnection();
        programConnection.setRequestMethod("GET");
        programConnection.connect();

        ProgramTask programTask;
        Timer timer = new Timer();

        // fetch programs from API once every hour
        timer.schedule(programTask = new ProgramTask(programConnection, channelURL, (c)-> {
            programs = c;
        }), 0, 3600000);

        int i = 0;

        // wait for programs to be fetched...
        while (programs.size() == 0){
            System.out.println(programs.size());
        }

        fetchedPrograms = true;
        createProgramTable();
    }

    /**
     * Returns true or false depending on whether the programs
     * have been fetched or not.
     * @return true if programs have been fetched, otherwise false
     */
    public Boolean hasFetchedPrograms(){
        return fetchedPrograms;
    }

    /**
     * Creates program table to be displayed in GUI.
     */
    private void createProgramTable(){

        // create two-dimensional string array containing the program name and the times
        if(!programs.get(0).getTitle().equals("tomt")) {

            data = new String[programs.size()][3];

            for (int i = 0; i < programs.size(); i++) {
                String[] programData = {programs.get(i).getTitle(), programs.get(i).getStartTime().toString(), programs.get(i).getEndTime().toString()};
                for (int j = 0; j < programData.length; j++) {
                    data[i][j] = programData[j];
                }
            }

            // if first program in list is named "tomt", return null since this indicates no programs were found
        } else {
            data = null;
        }
    }

    /**
     * Gets a list of all programs.
     * @return an arraylist containing the channels
     * radio programs
     */
    public ArrayList<Program> getProgramList(){
        return programs;
    }

    /**
     * Gets the program table to be displayed in
     * the GUI.
     * @return A two-dimensional string array containing
     * the program data to be displayed in the GUI.
     */
    public String[][] getProgramTable(){
        return data;
    }
}
