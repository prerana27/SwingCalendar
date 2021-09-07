package calendar;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.logging.Logger;

import static javax.swing.BoxLayout.Y_AXIS;

public class NewEventDialogue extends JDialog {
    private static Logger logger = Logger.getLogger("calendar.NewEventDialogue");
    private static final int TEXT_LEN = 15;
    private static final String DEFAULT_NAME = "New Event", DEFAULT_START = "18:00", DEFAULT_END = "18:30";
    private JPanel basePanel, eventDetails, eventType, buttonsPanel;
    private JLabel statusLabel, nameLabel, dateLable, startLabel, endLabel;
    private JTextField nameText, dateText, startText, endText;
    private LocalDate date;
    private JButton save, cancel;
    private Dimension labelSize;

    NewEventDialogue(LocalDate date, JLabel statusLabel) {
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

    //this adds the first part with the text fields
    private JPanel addEventDetails() {
        eventDetails = new JPanel();
        eventDetails.setLayout(new BoxLayout(eventDetails, Y_AXIS));

        JPanel namePanel = new JPanel();
        nameLabel = new JLabel("Name:");
        nameLabel.setMaximumSize(labelSize);
        nameText = new JTextField(DEFAULT_NAME, TEXT_LEN);
        nameText.setForeground(Color.GRAY);
        namePanel.add(nameLabel);
        namePanel.add(nameText);

        JPanel datePanel = new JPanel();
        dateLable = new JLabel("Date:");
        dateLable.setMaximumSize(labelSize);
        dateText = new JTextField(date.toString(), TEXT_LEN);
        dateText.setForeground(Color.GRAY);
        datePanel.add(dateLable);
        datePanel.add(dateText);

        JPanel startPanel = new JPanel();
        startLabel = new JLabel("Start Time:");
        startLabel.setMaximumSize(labelSize);
        startText = new JTextField(DEFAULT_START, TEXT_LEN);
        startText.setForeground(Color.GRAY);
        startPanel.add(startLabel);
        startPanel.add(startText);

        JPanel endPanel = new JPanel();
        endLabel = new JLabel("End Time:");
        endLabel.setMaximumSize(labelSize);
        endText = new JTextField(DEFAULT_END, TEXT_LEN);
        endText.setForeground(Color.GRAY);
        endPanel.add(endLabel);
        endPanel.add(endText);

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
            public void changeColor(FocusEvent e, Color setColorTo){
                Object src = e.getSource();
                if (src.equals(nameText) && DEFAULT_NAME.equals(nameText.getText())) {
                    nameText.setForeground(setColorTo);
                    statusLabel.setText("Editing new event name");
                } else if (src.equals(dateText) && date.toString().equals(dateText.getText())) {
                    dateText.setForeground(setColorTo);
                    statusLabel.setText("Editing new event date");
                } else if (src.equals(startText) && DEFAULT_START.equals(startText.getText())) {
                    startText.setForeground(setColorTo);
                    statusLabel.setText("Editing new event start time");
                } else if (src.equals(endText) && DEFAULT_END.equals(endText.getText())) {
                    endText.setForeground(setColorTo);
                    statusLabel.setText("Editing new event end time");
                }
                logger.info(statusLabel.getText());
            }
        };

        //adding listeners for text fields
        nameText.addFocusListener(focusListener);
        dateText.addFocusListener(focusListener);
        startText.addFocusListener(focusListener);
        endText.addFocusListener(focusListener);

        eventDetails.add(namePanel);
        eventDetails.add(datePanel);
        eventDetails.add(startPanel);
        eventDetails.add(endPanel);
        return eventDetails;
    }

    //this adds the panel with the event type checkboxes
    private JPanel addEventTypes() {
        eventType = new JPanel();

        JCheckBox work = new JCheckBox("Work");
        JCheckBox family = new JCheckBox("Family");
        JCheckBox vacation = new JCheckBox("Vacation");
        JCheckBox health = new JCheckBox("Health");

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

        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText("Create new event dialog box cancelled.");
                logger.info(statusLabel.getText());
                NewEventDialogue.super.dispose();
            }
        });

        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText(String.format("New Event created with title \"%s\" on %s during %s - %s", nameText.getText(), dateText.getText(), startText.getText(), endText.getText()));
                logger.info(statusLabel.getText());
                NewEventDialogue.super.dispose();
            }
        });

        buttonsPanel.add(cancel);
        buttonsPanel.add(save);
        return buttonsPanel;
    }
}
