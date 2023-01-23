package org.example.View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;

/**
 * Class representing the GUI.
 *
 * @author Olivia Harlin
 * @umu-id olha0073
 * @çs-id id19ohn
 *
 * @version 1.0
 * @date 23-01-09
 */
public class ChannelView {
    private JFrame frame;
    final ActionListener buttonListener;
    private JPanel upperPanel;
    private JMenuBar menuBar;
    private JMenu p4Menu;
    private JMenu srMenu;
    private JMenu otherMenu;

    private JTable programTable;
    private DefaultTableModel tableModel;
    private JLabel label;
    private JButton refreshBtn;
    private String currentChannel;
    private String[] columnNames = {"Namn på program", "Starttid", "Sluttid"};

    /**
     * Class constructor.
     * @param buttonListener - the action listener used to determine button actions
     */
    public ChannelView(ActionListener buttonListener){
        this.buttonListener = buttonListener;
        initialize();
    }

    /**
     * Initializes the GUI.
     */
    public void initialize() {
        // build frame
        frame = buildFrame();

        // build panel
        upperPanel = buildUpperPanel();

        // create and initialize table
        initializeTable();

        // add panels to frame
        frame.add(upperPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(programTable));

        // create menu
        menuBar=new JMenuBar();
        p4Menu = new JMenu("P4");
        srMenu = new JMenu("SR");
        otherMenu = new JMenu("Andra kanaler");
        //new MenuScroller(channelMenu, 25);
        frame.setJMenuBar(menuBar);
        menuBar.add(p4Menu);
        menuBar.add(srMenu);
        menuBar.add(otherMenu);
    }


    /**
     * Makes the GUI visible.
     */
    public void show() {
        frame.setVisible(true);
    }


    /**
     * Builds the main frame.
     * @return the main frame as a JFrame
     */
    private JFrame buildFrame(){
        frame = new JFrame();
        frame.setTitle("Olivias radiohanterare");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout(10,10));
        frame.setLocationRelativeTo(null);
        frame.setSize(700, 500);
        frame.setMinimumSize(new Dimension(500, 200));

        return frame;
    }


    /**
     * Creates the upper panel.
     * @return the upper panel as a JPanel
     */
    private JPanel buildUpperPanel(){
        JPanel upperPanel = new JPanel();
        label = new JLabel("Välj en kanal i menyn för att visa tablå");
        upperPanel.add(label, BorderLayout.NORTH);

        refreshBtn = new JButton("Uppdatera radiodata");
        refreshBtn.addActionListener(buttonListener);

        return upperPanel;
    }

    /**
     * Fills the menu with all radio channels.
     * @param list - All channel names to be displayed in the menu
     */
    public void fillMenu(ArrayList<String> p4, ArrayList<String> sr, ArrayList<String> other){

        for(int i = 0 ; i < p4.size() ; i++){
            JMenuItem temp = new JMenuItem(p4.get(i));
            p4Menu.add(temp);
            temp.addActionListener(buttonListener);
            temp.setActionCommand(p4.get(i));
        }

        for(int i = 0 ; i < sr.size() ; i++){
            JMenuItem temp = new JMenuItem(sr.get(i));
            srMenu.add(temp);
            temp.addActionListener(buttonListener);
            temp.setActionCommand(sr.get(i));
        }

        for(int i = 0 ; i < other.size() ; i++){
            JMenuItem temp = new JMenuItem(other.get(i));
            otherMenu.add(temp);
            temp.addActionListener(buttonListener);
            temp.setActionCommand(other.get(i));
        }

    }

    /**
     * Initializes the table and table model.
     */
    private void initializeTable(){
        tableModel = new DefaultTableModel();
        programTable = new JTable(tableModel);
        programTable.setDefaultEditor(Object.class, null);

        createPopUp();
    }

    /**
     * Displays the popup window.
     * @param name - The name of the radio program
     * @param description - The description of the radio program
     * @param start - The start time of the radio program
     * @param end - The end time of the radio program
     * @param image - The image for the radio program
     */
    public void displayPopUp(String name, String description, String start, String end, URL image){

        // set cover image as pop-up icon
        JLabel icon = new JLabel(scaleCoverImage(image));
        JPanel panel = new JPanel(new GridBagLayout());
        panel.add(icon);

        JPanel panel2 = new JPanel(new BorderLayout());
        // create and add area for text to be displayed
        panel2.add(createOutputField(description, start, end), BorderLayout.NORTH);
        panel2.add(panel, BorderLayout.CENTER);

        // create pop-up
        JDialog popUp = new JDialog(frame, name);
        popUp.add(panel2);
        popUp.setSize(400, 400);

        // place at center of screen
        popUp.setLocationRelativeTo(null);

        popUp.setVisible(true);
    }


    /**
     * Creates the pop-up that appears when a program is clicked on.
     */
    private void createPopUp(){
        programTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JButton fakeBtn = new JButton();
                fakeBtn.addActionListener(buttonListener);

                // get index of selected row
                int row = programTable.getSelectedRow();

                // send action command to controller
                fakeBtn.setActionCommand("show " + row + " " + currentChannel);
                fakeBtn.doClick();

            }
        });
    }

    /**
     * Creates the area for output text.
     * @param descriptionText - The description of the radio program
     * @param start - The start time of the radio program
     * @param end - The end time of the radio program
     * @return the area as a JPanel
     */
    private JPanel createOutputField(String descriptionText, String start, String end){
        JPanel textPanel = new JPanel(new BorderLayout());
        JTextPane description = new JTextPane();

        // make text center aligned
        StyledDocument doc = description.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        // make text display description and time
        description.setText("\n" + descriptionText + "\n\n Sänds mellan " + start + "-" + end + "\n");

        description.setEditable(false);
        textPanel.add(description);

        return textPanel;
    }

    /**
     * Creates icon from cover image
     * @param imageURL - the url for the image
     * @return the image as a ImageIcon
     */
    private ImageIcon scaleCoverImage(URL imageURL){
        ImageIcon icon = new ImageIcon(imageURL);
        Image image = icon.getImage();

        image = image.getScaledInstance(200,200, Image.SCALE_DEFAULT);

        return new ImageIcon(image);
    }

    /**
     * Displays data to the GUI.
     * @param data - The data as a two-dimensional string array
     * @param channel - The name of the current channel
     */
    public synchronized void displayPrograms(String[][] data, String channel){
        currentChannel = channel;

        refreshBtn.setActionCommand("refresh " + currentChannel);

        // adds refresh button
        upperPanel.add(refreshBtn, BorderLayout.SOUTH);

        // if there is data to display...
        if(data != null) {
            label.setText("Tablå för " + currentChannel);
            tableModel = new DefaultTableModel(data, columnNames);
            programTable.setModel(tableModel);

            // if there is NO data to display...
        } else  {
            programTable.setModel(new DefaultTableModel());
            label.setText("Det finns ingen tablå att visa för " + currentChannel);
        }

        // repaint table
        programTable.repaint();
    }

    /**
     * Displays error messages to the view.
     * @param errorMsg - The message as a string
     */
    public void displayErrorMsg(String errorMsg){
        programTable.setModel(new DefaultTableModel());
        label.setText(errorMsg);
        programTable.repaint();
    }

}

