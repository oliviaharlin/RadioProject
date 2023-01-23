package org.example.Controller;

import org.example.Model.Channel;
import org.example.Model.ChannelTask;
import org.example.Model.Program;
import org.example.View.ChannelView;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;

/**
 * Controller class. Communicates with model and view.
 *
 * @author Olivia Harlin
 * @umu-id olha0073
 * @çs-id id19ohn
 *
 * @version 1.0
 * @date 23-01-09
 */
public class RadioController {
    private List<Thread> threads;
    private ActionListener buttonListener;
    private ArrayList<Channel> channelsList;
    private ChannelView view;
    private Timer timer;

    /**
     * Class constructor. Gets all radio channels from API and
     * creates GUI.
     * @throws IOException
     */
    public RadioController() throws IOException {
        threads = Collections.synchronizedList(new ArrayList<>());

        // establish connection to API
        URL url = new URL("http://api.sr.se/api/v2/channels/?pagination=false");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        // get radio channels from API
        ChannelTask channelTask = new ChannelTask(connection, url);
        channelTask.run();

        createActionListener();
        channelsList = channelTask.getChannels();

        ArrayList<String> p4Channels = new ArrayList<>();
        ArrayList<String> srChannels = new ArrayList<>();
        ArrayList<String> otherChannels = new ArrayList<>();

        for(int i = 0 ; i < channelsList.size() ; i++){
            if(channelsList.get(i).getName().startsWith("P4")){
                p4Channels.add(channelsList.get(i).getName());
            } else if(channelsList.get(i).getName().startsWith("SR")){
                srChannels.add(channelsList.get(i).getName());
            } else {
                otherChannels.add(channelsList.get(i).getName());
            }
        }

        // create GUI and fill menu with the channels
        SwingUtilities.invokeLater(() -> {
            view = new ChannelView(buttonListener);
            view.show();
            view.fillMenu(p4Channels, srChannels, otherChannels);
        });
    }


    /**
     * Creates action listener and performs different actions
     * depending on the incoming message.
     */
    private void createActionListener() {
        buttonListener = e -> {

            if (e.getActionCommand() != null) {

                if (e.getActionCommand().startsWith("show")) {
                    sendToPopUp(e.getActionCommand());

                } else if (e.getActionCommand().startsWith("refresh")) {
                    String[] stringSplit = e.getActionCommand().split("\\s+", 2);
                    String channel = stringSplit[1];
                    findChannelandStartThread(channel, e.getActionCommand());
                } else {
                    String channel = e.getActionCommand();
                    findChannelandStartThread(channel, e.getActionCommand());
                }

            }else{
                System.out.println("something went wrong...");
        }

        };
    }

    private void findChannelandStartThread(String channel, String actionCommand){

        // find channel matching the channel chosen from the menu
        for (int i = 0; i < channelsList.size(); i++) {
            if (channelsList.get(i).getName().equals(channel)) {

                threads.add(new fetchProgramsThread(channelsList.get(i), actionCommand));
                threads.get(threads.size() - 1).start();

            }
        }
    }


    private void sendToPopUp(String actionCommand){
        String[] stringSplit = actionCommand.split("\\s+", 3);
        String programIndex = stringSplit[1];
        String channel = stringSplit[2];


        for(int i = 0 ; i < channelsList.size() ; i++){
            if(channelsList.get(i).getName().equals(channel)){

                String description = channelsList.get(i).getProgramList().get(Integer.parseInt(programIndex)).getDescription();
                URL image = channelsList.get(i).getProgramList().get(Integer.parseInt(programIndex)).getImage();
                String programTitle = channelsList.get(i).getProgramList().get(Integer.parseInt(programIndex)).getTitle();
                String startTime = channelsList.get(i).getProgramList().get(Integer.parseInt(programIndex)).getStartTime().toLocalTime().toString();
                String endTime = channelsList.get(i).getProgramList().get(Integer.parseInt(programIndex)).getEndTime().toLocalTime().toString();

                view.displayPopUp(programTitle, description, startTime, endTime, image);
            }
        }
    }


    /**
     * Inner class fetchProgramsThread that extends Thread class.
     * Is responsible for starting new threads.
     */
    private class fetchProgramsThread extends Thread {
        private Channel channel;
        private String actionCommand;

        /**
         * Class constructor.
         */
        public fetchProgramsThread(Channel channel, String actionCommand){
            this.channel = channel;
            this.actionCommand = actionCommand;
        }

        public void run() {
            fetchPrograms(channel, actionCommand);
        }

        /**
         * Gets programs from API or cached data depending on action
         * and calls on method for displaying them to the GUI.
         * @param channel - the current radio channel
         * @param actionCommand - the incoming action message
         */
        private void fetchPrograms(Channel channel, String actionCommand){

            // if programs was recently fetched and no refresh request was sent - use cached data
            if (channel.hasFetchedPrograms() && !(actionCommand.startsWith("refresh"))) {
                view.displayPrograms(channel.getProgramTable(), channel.getName());

            } else {
                // ...otherwise try making a new get-request to API
                try {
                    channel.setPrograms();
                    view.displayPrograms(channel.getProgramTable(), channel.getName());
                } catch (IOException ex) {
                    view.displayErrorMsg("Något gick fel. Programmet gav följande felmeddelande: \n" + ex);
                } catch (InterruptedException e) {
                    view.displayErrorMsg("Något gick fel. Programmet gav följande felmeddelande: \n" + e);
                }

            }
        }
    }
}
