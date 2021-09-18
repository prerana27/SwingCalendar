package calendar;

import utilities.TimeSpinner;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.logging.Logger;

import static javax.swing.BoxLayout.Y_AXIS;

public class NewEventDialogue extends JDialog {
    private static Logger logger = Logger.getLogger("calendar.NewEventDialogue");
    private static final int TEXT_LEN = 15;
    private static final String DEFAULT_NAME = "New Event";
    private JPanel basePanel, eventDetails, eventType, buttonsPanel;
    private JLabel statusLabel, nameLabel, dateLable, startLabel, endLabel;
    private JTextField nameText, dateText;
    private JSpinner startTime, endTime;
    private LocalDate date;
    private JButton save, cancel;
    private Dimension labelSize;
    private JCheckBox work, family, vacation, health;


    NewEventDialogue(LocalDate date, JLabel statusLabel) {
        //setting up basic stuff for the dialogue box
        logger.info("Creating a new Dialogue box to add new event to the Calendar");
        this.date = date;
        this.statusLabel = statusLabel;
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        labelSize = new Dimension(80, 40);


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

    //this adds the first part with the text fields and spinners for time selection
    private JPanel addEventDetails() {
        eventDetails = new JPanel();
        eventDetails.setLayout(new BoxLayout(eventDetails, Y_AXIS));

        //this adds a label and a text field to a new panel to ensure Flow Layout
        JPanel namePanel = new JPanel();
        nameLabel = new JLabel("Name:");
        nameLabel.setMaximumSize(labelSize);
        nameText = new JTextField(DEFAULT_NAME, TEXT_LEN);
        nameText.setForeground(Color.GRAY);
        namePanel.add(nameLabel);
        namePanel.add(nameText);

        //this adds a label and a text field to a new panel to ensure Flow Layout
        JPanel datePanel = new JPanel();
        dateLable = new JLabel("Date:");
        dateLable.setMaximumSize(labelSize);
        dateText = new JTextField(date.toString(), TEXT_LEN);
        dateText.setForeground(Color.GRAY);
        datePanel.add(dateLable);
        datePanel.add(dateText);

        //this adds a label and a JSpinner to a new panel to ensure Flow Layout
        JPanel startPanel = new JPanel();
        startLabel = new JLabel("Start Time :");
        startLabel.setMaximumSize(labelSize);
        startPanel.add(startLabel);
        //spinners to select start hour and minute
        //TODO: change minute increment to 15min intervals
        startTime = new JSpinner(new TimeSpinner("09:00"));
        startTime.setEditor(new JSpinner.DateEditor(startTime, "HH:mm"));
        startPanel.add(startTime);

        //this adds a label and a JSpinner to a new panel to ensure Flow Layout
        JPanel endPanel = new JPanel();
        endLabel = new JLabel("End Time :");
        endLabel.setMaximumSize(labelSize);
        endPanel.add(endLabel);
        //spinners to select start hour and minute
        endTime = new JSpinner(new TimeSpinner("09:30"));
        endTime.setEditor(new JSpinner.DateEditor(endTime, "HH:mm"));
        endPanel.add(endTime);

        addFocusListeners();
        addSpinnerListener();
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
                    statusLabel.setText("Editing new event name");
                } else if (src.equals(dateText) && date.toString().equals(dateText.getText())) {
                    dateText.setForeground(setColorTo);
                    statusLabel.setText("Editing new event date");
                } else if (src.equals(startTime)) {
                    statusLabel.setText("Editing new event's start time");
                } else if (src.equals(endTime)) {
                    statusLabel.setText("Editing new event's end time");
                }
                logger.info(statusLabel.getText());
            }
        };

        //adding listeners for text fields
        nameText.addFocusListener(focusListener);
        dateText.addFocusListener(focusListener);
    }

    private void addSpinnerListener() {
        //change listener for the JSpinners - basically to update the status label for now
        ChangeListener changeListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (e.getSource().equals(startTime)) {
                    statusLabel.setText("Editing new event's start time to" + getTime(startTime));
                } else if (e.getSource().equals(endTime)) {
                    statusLabel.setText("Editing new event's end time to" + getTime(endTime));
                }
            }
        };

        //adding listener for the Spinners
        startTime.addChangeListener(changeListener);
        endTime.addChangeListener(changeListener);
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
                    statusLabel.setText(display);
                } else if (source.equals(family)) {
                    display = String.format(s, family.getText(), isSelected);
                    statusLabel.setText(display);
                } else if (source.equals(vacation)) {
                    display = String.format(s, vacation.getText(), isSelected);
                    statusLabel.setText(display);
                } else if (source.equals(health)) {
                    display = String.format(s, health.getText(), isSelected);
                    statusLabel.setText(display);
                }
                logger.info(statusLabel.getText());
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
                statusLabel.setText("Create new event dialog box cancelled.");
                logger.info(statusLabel.getText());
                NewEventDialogue.super.dispose();
            }
        });

        //action listener to show selected items on the status label
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText(String.format("New Event created with title \"%s\" on %s during %s - %s", nameText.getText(), dateText.getText(), getTime(startTime), getTime(endTime)));
                logger.info(statusLabel.getText());
                NewEventDialogue.super.dispose();
            }
        });

        buttonsPanel.add(cancel);
        buttonsPanel.add(save);
        return buttonsPanel;
    }

    //utility to extract only Time from the value returned by JSpinner
    private String getTime(JSpinner spinner) {
        Date inputDate = (Date) spinner.getValue();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(inputDate);
    }
}
