package calendar;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static javax.swing.BoxLayout.Y_AXIS;

public class DefaultView extends JFrame {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(DefaultView.class);
    private static final int WEST_WIDTH = 250, APP_WIDTH = 600, APP_HEIGHT = 450, ICON_GAP = 15;
    private String status = "Clicked on \"%s\"";
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
    private Dimension small, medium;

    public boolean isDayViewSelected() {
        return dayViewSelected;
    }

    public void setDayViewSelected(boolean dayViewSelected) {
        this.dayViewSelected = dayViewSelected;
    }

    private boolean dayViewSelected;

    private void init() {
        dayViewSelected = true;
        date = LocalDate.now();
        dateMonthFormat = DateTimeFormatter.ofPattern("dd MMMM yyy");
        dayFormat = DateTimeFormatter.ofPattern("EEEE");
        monthYearFormat = DateTimeFormatter.ofPattern("MMMM yyyy");
        statusLabel = new JLabel("This space shows what actions are being performed");
        statusLabel.setMinimumSize(new Dimension(APP_WIDTH, 20));
        statusLabel.setVerticalTextPosition(SwingConstants.TOP);

        small = new Dimension(100, 50);
        medium = new Dimension(200, 50);
    }

    DefaultView() {
        init();
        logger.info("Initializing Frame ..");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setTitle("Prerana's Calendar");
        this.setLayout(new BorderLayout(5, 5));


        setMenuBar();
        setMainContent();

        this.add(statusLabel, BorderLayout.SOUTH);
        this.pack();
        this.setMinimumSize(new Dimension(APP_WIDTH, APP_HEIGHT));
    }

    private void setMenuBar() {
        myMenuBar = new JMenuBar();

        myMenuBar.add(setFileMenu());
        myMenuBar.add(setViewMenu());

        this.setJMenuBar(myMenuBar);
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
        westPanel = new JPanel();
        today = new JButton("Today");
        left = new JButton();
        right = new JButton();
        newEvent = new JButton("New Event");

        prevNextPanel = new JPanel();
        prevNextPanel.add(left);
        prevNextPanel.add(right);
        prevNextPanel.setMaximumSize(new Dimension(300, 50));
        prevNextPanel.setAlignmentX(CENTER_ALIGNMENT);

        ImageIcon todayIcon = new ImageIcon(getClass().getResource("/images/today.png"));
        today.setIcon(todayIcon);
        today.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
        today.setMaximumSize(medium);
        today.setIconTextGap(ICON_GAP);
        today.setAlignmentX(CENTER_ALIGNMENT);

        ImageIcon leftIcon = new ImageIcon(getClass().getResource("/images/left.png"));
        left.setIcon(leftIcon);
        left.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));
        left.setMaximumSize(small);


        ImageIcon rightIcon = new ImageIcon(getClass().getResource("/images/right.png"));
        right.setIcon(rightIcon);
        right.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));
        right.setMaximumSize(small);


        ImageIcon newIcon = new ImageIcon(getClass().getResource("/images/new.jpg"));
        newEvent.setIcon(newIcon);
        newEvent.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
        newEvent.setMaximumSize(medium);
        newEvent.setIconTextGap(ICON_GAP);
        newEvent.setAlignmentX(CENTER_ALIGNMENT);


        westPanel.setLayout(new BoxLayout(westPanel, Y_AXIS));
        westPanel.setBorder(BorderFactory.createTitledBorder("Navigation Pane"));
        westPanel.add(Box.createRigidArea(new Dimension(WEST_WIDTH, 20)));
        westPanel.add(today);
        westPanel.add(prevNextPanel);
        westPanel.add(newEvent);
        westPanel.add(Box.createRigidArea(new Dimension(WEST_WIDTH, 20)));

        today.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText(String.format(status, today.getText()));
                date = LocalDate.now();
                updateDateDisplay();
            }
        });

        left.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText(String.format(status, "Left"));
                date = (isDayViewSelected() ? date.minusDays(1) : date.minusMonths(1));
                updateDateDisplay();
            }
        });

        right.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText(String.format(status, "Right"));
                date = (isDayViewSelected() ? date.plusDays(1) : date.plusMonths(1));
                updateDateDisplay();
            }
        });

        newEvent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText(String.format(status, newEvent.getText()));
                new NewEventDialogue(date, statusLabel);
            }
        });

        return westPanel;
    }

    private JPanel setCenterPanel() {
        mainDisplay = new JPanel();
        mainDisplay.setBorder(BorderFactory.createTitledBorder("Calendar"));
        mainDisplay.setLayout(new BoxLayout(mainDisplay, Y_AXIS));

        selectedView = new JLabel();
        selectedView.setFont(new Font("Sans Serif", Font.BOLD, 24));
        selectedView.setAlignmentX(Component.CENTER_ALIGNMENT);

        displayDay = new JLabel();
        displayDay.setFont(new Font("Sans Serif", Font.PLAIN, 18));
        displayDay.setAlignmentX(Component.CENTER_ALIGNMENT);

        displayDate = new JLabel();
        displayDate.setFont(new Font("Sans Serif", Font.PLAIN, 22));
        displayDate.setAlignmentX(Component.CENTER_ALIGNMENT);


        mainDisplay.add(Box.createVerticalStrut(20));
        mainDisplay.add(selectedView);
        mainDisplay.add(Box.createVerticalStrut(20));
        mainDisplay.add(displayDay);
        mainDisplay.add(Box.createVerticalStrut(20));
        mainDisplay.add(displayDate);
        updateDateDisplay();

        return mainDisplay;
    }

    private void updateDateDisplay() {
        logger.info("Current date is: {}, and view type is: {}", date, isDayViewSelected());

        displayDay.setText(date.format(dayFormat));
        displayDay.setVisible(isDayViewSelected());

        if (isDayViewSelected()) {
            selectedView.setText("Day View");
            displayDate.setText(date.format(dateMonthFormat));
        } else {
            selectedView.setText("Month View");
            displayDate.setText(date.format(monthYearFormat));
        }
    }
}
