package org.example.Controller;

import org.example.Model.Channel;
import org.example.Model.ChannelTask;
import org.example.View.ChannelView;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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

        // create GUI and fill menu with the channels
        SwingUtilities.invokeLater(() -> {
            view = new ChannelView(buttonListener);
            view.show();
            view.fillMenu(channelsList);
        });
    }

    /**
     * Creates action listener and performs different actions
     * depending on the incoming message.
     */
    private void createActionListener(){
        buttonListener = e -> {

            if (e.getActionCommand() != null) {

                // find channel matching the channel chosen from the menu
                for (int i = 0; i < channelsList.size(); i++) {
                    if (channelsList.get(i).getName().equals(e.getActionCommand())) {

                        // fetch programs for current channel
                        fetchPrograms(channelsList.get(i), e.getActionCommand());
                    }
                }
            } else {
                System.out.println("something went wrong...");

            }
        };
    }

    /**
     * Gets programs from API or cached data depending on action
     * and calls on method for displaying them to the GUI.
     * @param channel - the current radio channel
     * @param actionCommand - the incoming action message
     */
    private void fetchPrograms(Channel channel, String actionCommand){

        // if programs was recently fetched and no refresh request was sent - use cached data
        if (channel.hasFetchedPrograms() && !(actionCommand.equals("refresh"))) {
            view.displayPrograms(channel.getProgramTable(), channel.getProgramList(), channel.getName());

        } else {
            // ...otherwise try making a new get-request to API
            try {
                channel.setPrograms();
                view.displayPrograms(channel.getProgramTable(), channel.getProgramList(), channel.getName());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        }
    }
}
