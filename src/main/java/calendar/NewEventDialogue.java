package calendar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.logging.Logger;

import static javax.swing.BoxLayout.Y_AXIS;

public class NewEventDialogue extends JDialog {
    private static Logger logger = Logger.getLogger("calendar.NewEventDialogue");
    private static final int TEXT_LEN = 15;
    public static final String DEFAULT_NAME = "New Event";
    private String eventName;
    private JPanel basePanel, eventDetails, eventType, buttonsPanel;
    private JLabel nameLabel, dateLable, startLabel, endLabel;
    private JTextField nameText, dateText;
    private JSpinner startHour, endHour, startMin, endMin;
    private LocalDate date;
    private JButton save, cancel;
    private Dimension labelSize;
    private JCheckBox work, family, vacation, health;
    private DayViewComponent dayViewComponent;
    private int default_h0 = 9, default_h1 = 9, default_m0 = 0, default_m1 = 30;
    private EventDetails currEvent;

    public void init() {
        //setting up basic stuff for the dialogue box
        logger.info("Creating a new Dialogue box to add new event to the Calendar");
        this.date = dayViewComponent.getCurrentDate();
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        labelSize = new Dimension(80, 40);
    }

    public void addPanel() {
        //panel that contains all other containers
        basePanel = new JPanel();
        basePanel.setLayout(new BoxLayout(basePanel, Y_AXIS));

        //adding different parts of this dialogue box
        basePanel.add(addEventDetails());
        basePanel.add(addEventTypes());
        basePanel.add(addButtons());

        this.add(basePanel);

        this.pack();
        this.setMinimumSize(new Dimension(400, 300));
        this.setVisible(true);
    }

    NewEventDialogue(DayViewComponent dayViewComponent) {
        this.dayViewComponent = dayViewComponent;
        init();
        addPanel();
    }

    NewEventDialogue(DayViewComponent dayViewComponent, LocalTime start, LocalTime end) {
        this.dayViewComponent = dayViewComponent;
        this.default_h0 = start.getHour();
        this.default_m0 = start.getMinute();
        this.default_h1 = end.getHour();
        this.default_m1 = end.getMinute();
        init();
        addPanel();
    }

    NewEventDialogue(DayViewComponent dayViewComponent, EventDetails eventDetails) {
        this.dayViewComponent = dayViewComponent;
        this.currEvent = eventDetails;
        this.eventName = eventDetails.getEventName();
        this.default_h1 = eventDetails.getEndTime().getHour();
        this.default_m1 = eventDetails.getEndTime().getMinute();
        this.default_h0 = eventDetails.getStartTime().getHour();
        this.default_m0 = eventDetails.getStartTime().getMinute();
        this.date = eventDetails.getEventDate();
        init();
        addPanel();
    }

    NewEventDialogue(DayViewComponent dayViewComponent, LocalTime start) {
        this.dayViewComponent = dayViewComponent;
        this.default_h0 = start.getHour();
        this.default_m0 = start.getMinute();
        LocalTime end = start.plusHours(1);
        this.default_h1 = end.getHour();
        this.default_m1 = end.getMinute();
        init();
        addPanel();
    }


    //this adds the first part with the text fields and spinners for time selection
    private JPanel addEventDetails() {
        eventDetails = new JPanel();
        eventDetails.setLayout(new BoxLayout(eventDetails, Y_AXIS));

        //this adds a label and a text field to a new panel to ensure Flow Layout
        JPanel namePanel = new JPanel();
        nameLabel = new JLabel("Name:");
        nameLabel.setMaximumSize(labelSize);
        nameText = new JTextField(getNameToUse(), TEXT_LEN);
        nameText.setForeground(Color.GRAY);
        namePanel.add(nameLabel);
        namePanel.add(nameText);

        //this adds a label and a text field to a new panel to ensure Flow Layout
        JPanel datePanel = new JPanel();
        dateLable = new JLabel("Date:");
        dateLable.setMaximumSize(labelSize);
        dateText = new JTextField(this.date.toString(), TEXT_LEN);
        dateText.setForeground(Color.GRAY);
        datePanel.add(dateLable);
        datePanel.add(dateText);

        //this adds a label and a JSpinner to a new panel to ensure Flow Layout
        JPanel startPanel = new JPanel();
        startLabel = new JLabel("Start Time :");
        startLabel.setMaximumSize(labelSize);
        startPanel.add(startLabel);

        //spinners to select start hour and minute
        startHour = new JSpinner(new SpinnerNumberModel(default_h0, 0, 23, 1));
        startPanel.add(startHour);
        startMin = new JSpinner(new SpinnerNumberModel(default_m0, 0, 59, 15));
        startMin.setEditor(new JSpinner.NumberEditor(startMin, "00"));
        startPanel.add(startMin);

        //this adds a label and a JSpinner to a new panel to ensure Flow Layout
        JPanel endPanel = new JPanel();
        endLabel = new JLabel("End Time :");
        endLabel.setMaximumSize(labelSize);
        endPanel.add(endLabel);

        //spinners to select start hour and minute
        endHour = new JSpinner(new SpinnerNumberModel(default_h1, 0, 23, 1));
        endPanel.add(endHour);
        endMin = new JSpinner(new SpinnerNumberModel(default_m1, 0, 59, 15));
        endMin.setEditor(new JSpinner.NumberEditor(endMin, "00"));
        endPanel.add(endMin);

        addFocusListeners();
        eventDetails.add(namePanel);
        eventDetails.add(datePanel);
        eventDetails.add(startPanel);
        eventDetails.add(endPanel);
        return eventDetails;
    }

    private void addFocusListeners() {
        //focus listener to have some responsive behavior with the text fields
        FocusListener focusListener = new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                changeColor(e, Color.BLACK);
            }

            @Override
            public void focusLost(FocusEvent e) {
                changeColor(e, Color.GRAY);
            }

            //when in focus, makes text black
            //when out of focus makes text grey if text is the default text
            public void changeColor(FocusEvent e, Color setColorTo) {
                Object src = e.getSource();
                if (src.equals(nameText) && DEFAULT_NAME.equals(nameText.getText())) {
                    nameText.setForeground(setColorTo);
                    logger.info("Editing new event name");
                } else if (src.equals(dateText) && date.toString().equals(dateText.getText())) {
                    dateText.setForeground(setColorTo);
                    logger.info("Editing new event date");
                } else if (src.equals(startHour)) {
                    logger.info("Editing new event's start time");
                } else if (src.equals(endHour)) {
                    logger.info("Editing new event's end time");
                }
            }
        };

        //adding listeners for text fields
        nameText.addFocusListener(focusListener);
        dateText.addFocusListener(focusListener);
    }

    private void addCheckboxListener() {
        //For now this listener is only used to update the status label
        ItemListener itemListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                Object source = e.getSource();
                boolean isSelected = e.getStateChange() == ItemEvent.SELECTED;

                String s = "Selection for %s is %s";
                String display;
                if (source.equals(work)) {
                    display = String.format(s, work.getText(), isSelected);
                } else if (source.equals(family)) {
                    display = String.format(s, family.getText(), isSelected);
                } else if (source.equals(vacation)) {
                    display = String.format(s, vacation.getText(), isSelected);
                } else {
                    display = String.format(s, health.getText(), isSelected);
                }
                logger.info(display);
            }
        };

        work.addItemListener(itemListener);
        family.addItemListener(itemListener);
        vacation.addItemListener(itemListener);
        health.addItemListener(itemListener);
    }

    //this adds the panel with the event type checkboxes
    private JPanel addEventTypes() {
        eventType = new JPanel();

        work = new JCheckBox("Work");
        family = new JCheckBox("Family");
        vacation = new JCheckBox("Vacation");
        health = new JCheckBox("Health");

        addCheckboxListener();

        eventType.add(work);
        eventType.add(vacation);
        eventType.add(family);
        eventType.add(health);

        return eventType;
    }

    private JPanel addButtons() {
        buttonsPanel = new JPanel();

        save = new JButton("Save");
        cancel = new JButton("Cancel");

        //action listener for the cancel button
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.info("Create new event dialog box cancelled.");
                NewEventDialogue.super.dispose();
            }
        });

        //action listener to show selected items on the status label
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.info(String.format("New Event created with title \"%s\" on %s during %s - %s", nameText.getText(), dateText.getText(), getTime(true), getTime(false)));
                if (currEvent != null) {
                    currEvent.setStartTime(getTime(true));
                    currEvent.setEndTime(getTime(false));
                    currEvent.setEventName(nameText.getText());
                    dayViewComponent.repaintOnUpdate();
                } else {
                    dayViewComponent.addEvent(new EventDetails(nameText.getText(), getTime(true), getTime(false), LocalDate.parse(dateText.getText())));
                }
                NewEventDialogue.super.dispose();
            }
        });

        buttonsPanel.add(cancel);
        buttonsPanel.add(save);
        return buttonsPanel;
    }

    //utility to extract only Time from the value returned by JSpinner
    private LocalTime getTime(boolean start) {
        if (start)
            return LocalTime.of((Integer) startHour.getValue(), (Integer) startMin.getValue());
        return LocalTime.of((Integer) endHour.getValue(), (Integer) endMin.getValue());
    }

    private String getNameToUse() {
        return (this.eventName == null || this.eventName.isEmpty() ? DEFAULT_NAME : this.eventName);
    }
}
