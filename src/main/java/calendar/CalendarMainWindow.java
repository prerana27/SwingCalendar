package calendar;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static javax.swing.BoxLayout.Y_AXIS;

public class CalendarMainWindow extends JFrame {
    private static final Logger logger = Logger.getLogger("calendar.CalendarMainWindow");
    public static final int WEST_WIDTH = 250, APP_WIDTH = 1090, APP_HEIGHT = 680, ICON_GAP = 15;
    private static final String status = "Clicked on \"%s\"";
    private JLabel statusLabel;
    private JMenuBar myMenuBar;
    private JMenu fileMenu, viewMenu;
    private JMenuItem exit, dayView, monthView;
    private ButtonGroup viewType;
    private JPanel westPanel, prevNextPanel, mainDisplay;
    private JButton today, left, right, newEvent;
    private JLabel selectedView, displayDay, displayDate;
    private LocalDate date;
    private DateTimeFormatter dateMonthFormat, dayFormat, monthYearFormat;
    private Dimension smallButton, mediumButton, mappingSize;
    private boolean dayViewSelected;
    private DayViewComponent dayViewComponent;
    private JScrollPane dayViewScrollPane;
    public static Map<String, java.util.List<EventDetails>> eventsMap;

    public boolean isDayViewSelected() {
        return dayViewSelected;
    }

    public void setDayViewSelected(boolean dayViewSelected) {
        this.dayViewSelected = dayViewSelected;
    }

    //initializing some variables
    private void init() {
        //this is toggled on/off to keep track of which view is selected from menu
        dayViewSelected = true;

        //setting up date utilities
        dayFormat = DateTimeFormatter.ofPattern("EEEE");
        monthYearFormat = DateTimeFormatter.ofPattern("MMMM yyyy");
        dateMonthFormat = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        statusLabel = new JLabel("This space shows what actions are being performed");
        statusLabel.setMinimumSize(new Dimension(APP_WIDTH, 20));
        statusLabel.setVerticalTextPosition(SwingConstants.TOP);

        eventsMap = new HashMap<>();
        dayViewComponent = new DayViewComponent(date, eventsMap);
        this.date = dayViewComponent.getCurrentDate();

        smallButton = new Dimension(100, 50);
        mediumButton = new Dimension(200, 50);
        mappingSize = new Dimension(200, 40);
    }

    CalendarMainWindow() {
        //initialise Date, dateformat, status label etc
        init();

        //setting basic stuff for the JFrame
        logger.info("Initializing Frame ..");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout(5, 5));
        this.setTitle("Prerana's Calendar");

        //set up the Menu Bar
        setMenuBar();

        //Set up the content pane for the JFrame
        setMainContent();

        setCurrentTime();

        //pack and make the frame ready!
        this.pack();
        this.setMinimumSize(new Dimension(APP_WIDTH, APP_HEIGHT));
    }

    private void setCurrentTime() {
        Timer timer = new Timer(1000 * 60, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(LocalTime.now().getSecond() == 0)
                    dayViewComponent.repaintOnUpdate();
            }
        });
        timer.start();
    }

    private void setMenuBar() {
        myMenuBar = new JMenuBar();

        //add "File" menu to the menu bar
        myMenuBar.add(setFileMenu());

        //add "View" menu to the menu bar
        myMenuBar.add(setViewMenu());
        myMenuBar.setBorder(null);

        this.setJMenuBar(myMenuBar);
    }

    private void setMainContent() {
        this.add(setWestPanel(), BorderLayout.WEST);
        this.add(setCenterPanel(), BorderLayout.CENTER);
        this.add(statusLabel, BorderLayout.SOUTH);
    }

    private JMenu setFileMenu() {
        //Create File menu
        fileMenu = new JMenu("File");

        //add listener to update status when anything is clicked
        fileMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                logger.info("menuSelected :" + e.toString());
                statusLabel.setText(String.format(status, fileMenu.getText()));
            }

            @Override
            public void menuDeselected(MenuEvent e) {
                logger.info("menuDeselected :" + e.toString());
                statusLabel.setText(String.format("%s was deselected.", fileMenu.getText()));

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
                statusLabel.setText(String.format(status, exit.getText()));
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

        //add radio buttons to group to ensure mutually exclusive behaviour
        viewType.add(dayView);
        viewType.add(monthView);

        //add action listener to change main display when day view is selected
        dayView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText(String.format("Switched to %s", dayView.getText()));
                setDayViewSelected(true);
                updateDateDisplay();
            }
        });

        //add action listener to change main display when month view is selected
        monthView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText(String.format("Switched to %s", monthView.getText()));
                setDayViewSelected(false);
                updateDateDisplay();
            }
        });

        //add action listener to show when "View" menu is selected
        viewMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                logger.info("menuSelected :" + e.toString());
                statusLabel.setText(String.format(status, viewMenu.getText()));
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
        //this is the base panel that will contain the buttons
        westPanel = new JPanel();
        westPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        today = new JButton("Today");
        newEvent = new JButton("New Event");

        //creating another panel with default flow layout to have the "<" and ">" buttons next to each other
        prevNextPanel = new JPanel();

        left = new JButton();
        right = new JButton();
        prevNextPanel.add(left);
        prevNextPanel.add(right);
        prevNextPanel.setMaximumSize(new Dimension(300, 50));
        prevNextPanel.setAlignmentX(CENTER_ALIGNMENT);
        prevNextPanel.setBackground(Color.WHITE);

        //attempt to "beautify" buttons with icons
        beautifyButtons();

        westPanel.setLayout(new BoxLayout(westPanel, Y_AXIS));
        westPanel.setBackground(Color.WHITE);

        //dont know of a better way to create empty space between the title of panel and next container
        westPanel.add(Box.createRigidArea(new Dimension(WEST_WIDTH, 20)));
        westPanel.add(today);
        westPanel.add(prevNextPanel);
        westPanel.add(newEvent);
        westPanel.add(Box.createRigidArea(new Dimension(WEST_WIDTH, 20)));
        westPanel.add(addEventTypeMap());

        //adding action listened to "Today" to update the main display
        today.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText(String.format(status, today.getText()));
                date = dayViewComponent.setCurrentDate(LocalDate.now());
                updateDateDisplay();
            }
        });

        //adding action listened to "<" to update the main display
        left.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText(String.format(status, "Left"));
                date = dayViewComponent.updateDate(isDayViewSelected(), false);
                updateDateDisplay();
            }
        });

        //adding action listened to ">" to update the main display
        right.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText(String.format(status, "Right"));
                date = dayViewComponent.updateDate(isDayViewSelected(), true);
                updateDateDisplay();
            }
        });

        //adding action listened to "New event" to update the main display
        newEvent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText(String.format(status, newEvent.getText()));
                new NewEventDialogue(dayViewComponent);
            }
        });


        return westPanel;
    }

    private JPanel addEventTypeMap() {
        JPanel mapping = new JPanel();
        mapping.setBackground(Color.WHITE);
        mapping.setMaximumSize(new Dimension(200, 600));

        mapping.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        JLabel heading = new JLabel(" K E Y   M A P ");
        heading.setForeground(Color.DARK_GRAY);
        heading.setBackground(Color.WHITE);
        heading.setOpaque(true);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setMaximumSize(mappingSize);
        heading.setMinimumSize(mappingSize);
        heading.setPreferredSize(mappingSize);

        JLabel work = new JLabel(" W O R K ");
        work.setOpaque(true);
        work.setHorizontalAlignment(SwingConstants.CENTER);
        work.setBackground(NewEventDialogue.WORK);
        work.setMaximumSize(mappingSize);
        work.setMinimumSize(mappingSize);
        work.setPreferredSize(mappingSize);

        JLabel family = new JLabel(" F A M I L Y ");
        family.setOpaque(true);
        family.setHorizontalAlignment(SwingConstants.CENTER);
        family.setBackground(NewEventDialogue.FAMILY);
        family.setMaximumSize(mappingSize);
        family.setMinimumSize(mappingSize);
        family.setPreferredSize(mappingSize);

        JLabel health = new JLabel(" H E A L T H ");
        health.setOpaque(true);
        health.setHorizontalAlignment(SwingConstants.CENTER);
        health.setBackground(NewEventDialogue.HEALTH);
        health.setMaximumSize(mappingSize);
        health.setMinimumSize(mappingSize);
        health.setPreferredSize(mappingSize);

        JLabel vacation = new JLabel(" V A C A T I O N ");
        vacation.setOpaque(true);
        vacation.setHorizontalAlignment(SwingConstants.CENTER);
        vacation.setBackground(NewEventDialogue.VACATION);
        vacation.setMaximumSize(mappingSize);
        vacation.setMinimumSize(mappingSize);
        vacation.setPreferredSize(mappingSize);

        mapping.add(heading, gbc);
        mapping.add(Box.createRigidArea(new Dimension(WEST_WIDTH, 20)), gbc);
        mapping.add(work, gbc);
        mapping.add(Box.createRigidArea(new Dimension(WEST_WIDTH, 20)), gbc);
        mapping.add(vacation, gbc);
        mapping.add(Box.createRigidArea(new Dimension(WEST_WIDTH, 20)), gbc);
        mapping.add(family, gbc);
        mapping.add(Box.createRigidArea(new Dimension(WEST_WIDTH, 20)), gbc);
        mapping.add(health, gbc);
        mapping.add(Box.createRigidArea(new Dimension(WEST_WIDTH, 20)), gbc);
        mapping.setAlignmentX(CENTER_ALIGNMENT);
        return mapping;
    }

    private void beautifyButtons() {
        //adding icon and attempting to beautify the "Today" button
        ImageIcon todayIcon = new ImageIcon(getClass().getResource("/images/today.png"));
        today.setIcon(todayIcon);
        today.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
        today.setMaximumSize(mediumButton);
        today.setIconTextGap(ICON_GAP);
        today.setAlignmentX(CENTER_ALIGNMENT);

        //adding icon and attempting to beautify the "<" button
        ImageIcon leftIcon = new ImageIcon(getClass().getResource("/images/left.png"));
        left.setIcon(leftIcon);
        left.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));
        left.setMaximumSize(smallButton);

        //adding icon and attempting to beautify the ">" button
        ImageIcon rightIcon = new ImageIcon(getClass().getResource("/images/right.png"));
        right.setIcon(rightIcon);
        right.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));
        right.setMaximumSize(smallButton);

        //adding icon and attempting to beautify the new event button
        ImageIcon newIcon = new ImageIcon(getClass().getResource("/images/new.jpg"));
        newEvent.setIcon(newIcon);
        newEvent.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
        newEvent.setMaximumSize(mediumButton);
        newEvent.setIconTextGap(ICON_GAP);
        newEvent.setAlignmentX(CENTER_ALIGNMENT);
    }

    private JPanel setCenterPanel() {
        //this is the panel that contains everything in the Center part of Border Layout
        mainDisplay = new JPanel();
        mainDisplay.setLayout(new BoxLayout(mainDisplay, Y_AXIS));

        //Label to display selected view
        selectedView = new JLabel();
        selectedView.setFont(new Font("SansSerif", Font.PLAIN, 18));
        selectedView.setForeground(Color.GRAY);
        selectedView.setAlignmentX(Component.LEFT_ALIGNMENT);
        selectedView.setHorizontalTextPosition(SwingConstants.LEFT);

        //label to display either day view date ot month view month
        displayDate = new JLabel();
        displayDate.setFont(new Font("SansSerif", Font.BOLD, 26));
        displayDate.setAlignmentX(Component.LEFT_ALIGNMENT);

        //label to disaply the day of week for day view
        displayDay = new JLabel();
        displayDay.setFont(new Font("SansSerif", Font.PLAIN, 20));
        displayDay.setAlignmentX(Component.LEFT_ALIGNMENT);


        //adding them to the main panel
        mainDisplay.add(Box.createVerticalStrut(5));
        mainDisplay.add(selectedView);
        mainDisplay.add(Box.createVerticalStrut(9));
        mainDisplay.add(displayDate);
        mainDisplay.add(Box.createVerticalStrut(5));
        mainDisplay.add(displayDay);

        dayViewScrollPane = new JScrollPane(dayViewComponent);
        dayViewScrollPane.setPreferredSize(dayViewComponent.getMinimumSize());
        dayViewScrollPane.getVerticalScrollBar().setUnitIncrement(10);
        dayViewScrollPane.setBorder(null);

        mainDisplay.add(dayViewScrollPane);
        mainDisplay.setBackground(Color.WHITE);

        //setting default day view text in the labels
        updateDateDisplay();

        return mainDisplay;
    }

    private void updateDateDisplay() {
        logger.info(String.format("Updating date display.. current date being displayed is: %s, and day view  is: %s", date, isDayViewSelected()));

        displayDay.setText(date.format(dayFormat));
        displayDay.setVisible(false);
        displayDate.setVisible(!isDayViewSelected());
        dayViewScrollPane.setVisible(isDayViewSelected());

        //using the boolean from radio buttons to determine what labels to show and what date format to use
        if (isDayViewSelected()) {
            selectedView.setText("Day View");
            displayDate.setText(date.format(dateMonthFormat));
        } else {
            selectedView.setText("Month View");
            displayDate.setText(date.format(monthYearFormat));
        }
    }
}