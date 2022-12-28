package org.example.View;

import org.example.Model.Channel;
import org.example.Model.Program;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    private JMenu channelMenu;
    private JTable programTable;
    private DefaultTableModel tableModel;
    private String[][] currentTableContent;
    private ArrayList<Program> programs;
    private JLabel label;
    private JButton refreshBtn;
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
        channelMenu = new JMenu("Channels");
        frame.setJMenuBar(menuBar);
        menuBar.add(channelMenu);
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

        refreshBtn = new JButton("↺");
        refreshBtn.addActionListener(buttonListener);
        refreshBtn.setActionCommand("refresh");

        return upperPanel;
    }

    /**
     * Fills the menu with all radio channels.
     * @param list - All channels to be dislayed in the menu
     */
    public void fillMenu(ArrayList<Channel> list){
        for(int i = 0 ; i < list.size() ; i++){
            JMenuItem temp = new JMenuItem(list.get(i).getName());
            channelMenu.add(temp);
            temp.addActionListener(buttonListener);
            temp.setActionCommand(list.get(i).getName());
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
     * Creates the pop-up that appears when a program is clicked on.
     */
    private void createPopUp(){
        programTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {

                // get index of selected row
                int row = programTable.getSelectedRow();

                // set cover image as pop-up icon
                JLabel icon = new JLabel(scaleCoverImage(row));
                JPanel panel = new JPanel(new GridBagLayout());
                panel.add(icon);

                JPanel panel2 = new JPanel(new BorderLayout());
                // create and add area for text to be displayed
                panel2.add(createOutputField(row), BorderLayout.NORTH);
                panel2.add(panel, BorderLayout.CENTER);

                // create pop-up
                JDialog popUp = new JDialog(frame, currentTableContent[row][0]);
                popUp.add(panel2);
                popUp.setSize(400, 400);

                // place at center of screen
                popUp.setLocationRelativeTo(null);

                popUp.setVisible(true);

            }
        });
    }

    /**
     * Creates the area for output text.
     * @param row - the index of the selected row
     * @return the area as a JPanel
     */
    private JPanel createOutputField(int row){
        JPanel textPanel = new JPanel(new BorderLayout());
        JTextPane description = new JTextPane();

        // make text center aligned
        StyledDocument doc = description.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        // make text display description and time
        description.setText("\n" + programs.get(row).getDescription() + "\n\n Sänds mellan " + programs.get(row).getStartTime().toLocalTime().toString() + "-" + programs.get(row).getEndTime().toLocalTime().toString() + "\n");

        description.setEditable(false);
        textPanel.add(description);

        return textPanel;
    }

    /**
     * Creates icon from cover image
     * @param row - the index of the current row
     * @return the image as a ImageIcon
     */
    private ImageIcon scaleCoverImage(int row){
        ImageIcon icon = new ImageIcon(programs.get(row).getImage());
        Image image = icon.getImage();

        // set size
        image = image.getScaledInstance(200,200, Image.SCALE_DEFAULT);

        return new ImageIcon(image);
    }

    /**
     * Displays data to the GUI.
     * @param data - The data as a two-dimensional string array
     * @param channel - The name of the current channel
     */
    public void displayPrograms(String[][] data, ArrayList<Program> programs, String channel){

        // adds refresh button
        upperPanel.add(refreshBtn, BorderLayout.SOUTH);

        // if there is data to display...
        if(data != null) {
            label.setText("Tablå för " + channel);
            this.programs = programs;
            currentTableContent = data;
            tableModel = new DefaultTableModel(data, columnNames);
            programTable.setModel(tableModel);

            // if there is NO data to display...
        } else  {
            programTable.setModel(new DefaultTableModel());
            label.setText("Det finns ingen tablå att visa för " + channel);
        }

        // repaint table
        programTable.repaint();
    }

}

