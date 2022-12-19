package MultipleStopWatchTry;

import java.awt.*;
import java.awt.event.*;
import java.util.regex.*;
import javax.swing.*;

public class StopWatchPanel extends JPanel implements ActionListener, Runnable {
    private static final Insets stdInset = new Insets(5, 20, 5, 5);
    private JTextField name, time;
    private JButton startStopButton, changeBackgroundButton, deleteActivityButton;
    private long setTime, lapTime, startTime, timePassed;
    private Boolean running = false;
    private Thread timerThread;
    private final Runnable displayUpdater = new Runnable() {
        public void run() {
            time.setText(millisToStr(timePassed));
        }
    };

    private void saveTime() {
        time.getCaret().setVisible(false);
        time.setEditable(false);
        setTime = strToMillis(time.getText());
        lapTime = setTime;
        time.setText(millisToStr(setTime));
    }

    public StopWatchPanel(final JFrame parentFrame) {

        // name field ------------------------------------------
        name = new JTextField("Activity");
        name.setMargin(stdInset);
        name.setEditable(false);
        // double click: edit
        name.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2)
                    name.setEditable(true);
            }
        });
        // focus lost: save
        name.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                name.setEditable(false);
                parentFrame.pack();
            }
        });
        // enter: save
        name.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    name.setEditable(false);
                    parentFrame.pack();
                }
            }
        });

        // time field ------------------------------------------
        time = new JTextField("00:00:00");
        time.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        time.setMargin(stdInset);
        time.setEditable(false);
        // double click: stop timer and edit
        time.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    stopTimer();
                    time.setEditable(true);
                    time.getCaret().setVisible(true);
                }
            }
        });
        // focus lost: save
        time.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                saveTime();
                parentFrame.pack();
            }
        });
        // enter: save
        time.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    saveTime();
                    parentFrame.pack();
                }
            }
        });

        // buttons ------------------------------------------
        startStopButton = new JButton("Start / Stop");
        startStopButton.setBackground(Color.RED);
        startStopButton.addActionListener(this);

        changeBackgroundButton = new JButton("Color");
        changeBackgroundButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(parentFrame,
                        "Choose Background Color", getBackground());
                if (newColor != null)
                    name.setBackground(newColor);
            }
        });

        deleteActivityButton = new JButton("Delete");
        deleteActivityButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                parentFrame.remove(StopWatchPanel.this);
                parentFrame.pack();
                parentFrame.setLocationRelativeTo(null);
                parentFrame.setVisible(true);
                parentFrame.repaint();
            }
        });

        // widget layout ------------------------------------------
        setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        add(name);
        add(time);
        add(startStopButton);
        add(changeBackgroundButton);
        add(deleteActivityButton);
    }

    // convert string from 00:00:00.000 to milliseconds
    public static long strToMillis(String time) {
        long result = 0;
        try {
            Pattern p = Pattern.compile("([0-9]{1,2}):([0-9]{1,2}):([0-9]{1,2})(\\.([0-9]{1,3}))?");
            Matcher m = p.matcher(time.trim());
            m.find();
            result += Long.parseLong(m.group(1)) * 3600000
                    + Long.parseLong(m.group(2)) * 60000 + Long.parseLong(m.group(3))
                    * 1000;
            if (m.group(5) != null)
                result += Long.parseLong(m.group(5));
        } catch (Exception e) {
            // don't complain, just return 0
            return 0;
        }
        return result;
    }

    // convert milliseconds to 00:00:00.000
    public static String millisToStr(long time) {
        long ms = time;
        long h = ms / 3600000;
        ms = ms % 3600000;
        long m = ms / 60000;
        ms = ms % 60000;
        long s = ms / 1000;
        ms = ms % 1000;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    // start stop button action
    public void actionPerformed(ActionEvent e) {
        if (time.isEditable())
            return;
        if (running) {
            running = false;
            try {
                timerThread.join();
            } catch (InterruptedException ie) {
            }
            startStopButton.setBackground(Color.RED);
            lapTime = timePassed;
            time.setText(millisToStr(timePassed));
        } else {
            startTime = System.currentTimeMillis();
            running = true;
            startStopButton.setBackground(Color.GREEN);
            timerThread = new Thread(this);
            timerThread.start();
        }
    }

    // stop the timer and clean up the thread.
    public void stopTimer() {
        running = false;
        try {
            if (timerThread != null && timerThread.isAlive()) {
                timerThread.join();
            }
        } catch (Exception ie) {
        }
    }

    @Override
    public void run() {
        try {
            while (running) {
                timePassed = lapTime + (System.currentTimeMillis() - startTime);
                if (timePassed < 0) {
                    timePassed = 0;
                    lapTime = 0;
                    running = false;
                }
                SwingUtilities.invokeAndWait(displayUpdater);
                Thread.sleep(50);
            }
        } catch (Exception e) {
        }
    }
}
