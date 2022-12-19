package MultipleStopWatchTry;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class StopWatchFrame extends javax.swing.JFrame {
    private static StopWatchFrame uniqueStopwatch = new StopWatchFrame();
    private static JButton addActivityButton;

    private StopWatchFrame() {}

    public static StopWatchFrame getInstance(){
        return uniqueStopwatch;
    }

    public void createApp(){
        // make main window
        setTitle("Time Tracker");
        setLayout(new java.awt.GridLayout(15, 1));
        setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Add button. ------------------------------------------------
        addActivityButton = new JButton("Add Activity");
        addActivityButton.setBackground(new Color(150,125,0));
        addActivityButton.addActionListener(this::actionPerformed);

        // Create and add first Stopwatch.----------------------------------
        add(addActivityButton);
        add(new StopWatchPanel(this));

        // show the main window
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        repaint();

    }

    // Create new Stopwatch, repaint UI.
    public void actionPerformed(ActionEvent e){
        add(new StopWatchPanel(this));
        setVisible(true);
        repaint();
    }
}
