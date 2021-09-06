package calendar;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static javax.swing.BoxLayout.X_AXIS;
import static javax.swing.BoxLayout.Y_AXIS;

public class DefaultView extends JFrame {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(DefaultView.class);
    private JLabel statusLabel;
    private JMenuBar menuBar;
    private JMenu fileMenu, viewMenu;
    private JMenuItem exit, dayView, monthView;
    private ButtonGroup viewType;
    private JPanel westPanel, prevNextPanel, mainDisplay;
    private JButton today, left, right, newEvent;
    private JLabel selectedView, displayDay, displayDate;
    private LocalDate date;
    private DateTimeFormatter dateMonthFormat, dayFormat, monthYearFormat;

    public boolean isDayViewSelected() {
        return dayViewSelected;
    }

    public void setDayViewSelected(boolean dayViewSelected) {
        this.dayViewSelected = dayViewSelected;
    }

    private boolean dayViewSelected;

    DefaultView() {
        logger.info("Initializing Frame ..");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        dayViewSelected = true;
        date = LocalDate.now();
        dateMonthFormat = DateTimeFormatter.ofPattern("dd MMMM yyy");
        dayFormat = DateTimeFormatter.ofPattern("EEEE");
        monthYearFormat = DateTimeFormatter.ofPattern("MMMM yyyy");

        setMenuBar();
        setMainContent();

        statusLabel = new JLabel("This shows what actions are being performed");
        this.add(statusLabel, BorderLayout.SOUTH);
        this.pack();
    }

    private void setMenuBar() {
        menuBar = new JMenuBar();

        menuBar.add(setFileMenu());
        menuBar.add(setViewMenu());

        this.setJMenuBar(menuBar);
    }

    private void setMainContent() {
        this.add(setWestPanel(), BorderLayout.WEST);
        this.add(setCenterPanel(), BorderLayout.CENTER);
    }

    private JMenu setFileMenu() {
        //Create File menu
        fileMenu = new JMenu("File");
        fileMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                logger.info("menuSelected :" + e.toString());
                statusLabel.setText(String.format("Clicked on %s menu", fileMenu.getText()));
            }

            @Override
            public void menuDeselected(MenuEvent e) {
                logger.info("menuDeselected :" + e.toString());

            }

            @Override
            public void menuCanceled(MenuEvent e) {
                logger.info("menuCanceled :" + e.toString());

            }
        });

        //Create Exit menu item
        exit = new JMenuItem("Exit");

        //Adding event listener to exit app on click
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.info("Exiting from Calendar via Menubar->File->Exit .. " + e.getActionCommand());
                statusLabel.setText(e.getActionCommand());
                System.exit(0);
            }
        });

        //Finally add the exit item to the menu
        fileMenu.add(exit);

        return fileMenu;
    }

    private JMenu setViewMenu() {
        //create new menu
        viewMenu = new JMenu("View");

        //create a group for radio buttons
        viewType = new ButtonGroup();

        //create and configure day view radio button
        dayView = new JRadioButtonMenuItem("Day View");
        dayView.setMnemonic(KeyEvent.VK_D);
        dayView.setSelected(true);

        //create and configure month view radio button
        monthView = new JRadioButtonMenuItem("Month View");
        monthView.setMnemonic(KeyEvent.VK_M);

        //add radio buttons to group
        viewType.add(dayView);
        viewType.add(monthView);

        dayView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText(String.format("Switched to %s", dayView.getText()));
                setDayViewSelected(true);
                updateDateDisplay();
            }
        });

        monthView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText(String.format("Switched to %s", monthView.getText()));
                setDayViewSelected(false);
                updateDateDisplay();
            }
        });

        viewMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                logger.info("menuSelected :" + e.toString());
                statusLabel.setText(String.format("Clicked on %s menu", viewMenu.getText()));
            }

            @Override
            public void menuDeselected(MenuEvent e) {
                logger.info("menuDeselected :" + e.toString());
            }

            @Override
            public void menuCanceled(MenuEvent e) {
                logger.info("menuCanceled :" + e.toString());
            }
        });


        //add radio buttons to Menu
        viewMenu.add(dayView);
        viewMenu.add(monthView);

        return viewMenu;
    }

    private JPanel setWestPanel() {
        westPanel = new JPanel();
        today = new JButton("Today");
        left = new JButton("<");
        right = new JButton(">");
        newEvent = new JButton("+");

        prevNextPanel = new JPanel();
        prevNextPanel.setLayout(new BoxLayout(prevNextPanel, X_AXIS));
        prevNextPanel.add(left);
        prevNextPanel.add(right);


        westPanel.setLayout(new BoxLayout(westPanel, Y_AXIS));
        westPanel.setBorder(BorderFactory.createTitledBorder("Navigation Pane"));
        westPanel.add(today);
        westPanel.add(prevNextPanel);
        westPanel.add(newEvent);
        westPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        today.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText(e.getActionCommand());
                date = LocalDate.now();
                updateDateDisplay();
            }
        });

        left.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText(e.getActionCommand());
                date = (isDayViewSelected()? date.minusDays(1) : date.minusMonths(1));
                updateDateDisplay();
            }
        });

        right.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText(e.getActionCommand());
                date = (isDayViewSelected()? date.plusDays(1) : date.plusMonths(1));
                updateDateDisplay();
            }
        });

        return westPanel;
    }

    private JPanel setCenterPanel() {
        mainDisplay = new JPanel();
        mainDisplay.setLayout(new BoxLayout(mainDisplay, Y_AXIS));

        selectedView = new JLabel("Day view");
        displayDay = new JLabel(date.format(dayFormat));
        displayDate = new JLabel(date.format(dateMonthFormat));

        mainDisplay.add(selectedView);
        mainDisplay.add(displayDay);
        mainDisplay.add(displayDate);

        return mainDisplay;
    }

    public void updateDateDisplay() {
        logger.info("Current date is: {}, and view type is: {}", date.toString(), isDayViewSelected());

        displayDay.setText(date.format(dayFormat));
        displayDay.setVisible(isDayViewSelected());

        if(isDayViewSelected()){
            selectedView.setText("Day View");
            displayDate.setText(date.format(dateMonthFormat));
        }else{
            selectedView.setText("Month View");
            displayDate.setText(date.format(monthYearFormat));
        }
    }
}
