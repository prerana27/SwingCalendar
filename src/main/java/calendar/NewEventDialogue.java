package calendar;

import ch.qos.logback.classic.Logger;
import javafx.scene.control.DatePicker;
import org.slf4j.LoggerFactory;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;

import static javax.swing.BoxLayout.Y_AXIS;

public class NewEventDialogue extends JDialog {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(NewEventDialogue.class);
    private static final int TEXT_LEN = 15;
    private static final String defaultName = "New Event", defaultStart = "18:00", defaultEnd = "18:30";
    private JPanel basePanel, eventDetails, eventType, buttonsPanel;
    private JLabel statusLabel, nameLabel, dateLable, startLabel, endLabel, separator;
    private JTextField nameText, dateText, startText, endText;
    private LocalDate date;
    private JButton save, cancel;
    private Dimension labelSize;

    NewEventDialogue(LocalDate date, JLabel statusLabel) {
        this.date = date;
        this.statusLabel = statusLabel;
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        labelSize = new Dimension(80, 40);
        separator = new JLabel("Select Categories for this event");

        basePanel = new JPanel();
        basePanel.setLayout(new BoxLayout(basePanel, Y_AXIS));
        basePanel.add(addEventDetails());
        basePanel.add(addEventTypes());
        basePanel.add(addButtons());
        this.add(basePanel);

        this.pack();
        this.setMinimumSize(new Dimension(400, 300));
        this.setVisible(true);
    }

    private JPanel addEventDetails() {
        eventDetails = new JPanel();
        eventDetails.setLayout(new BoxLayout(eventDetails, Y_AXIS));

        JPanel namePanel = new JPanel();
        nameLabel = new JLabel("Name:");
        nameLabel.setMaximumSize(labelSize);
        nameText = new JTextField(defaultName, TEXT_LEN);
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
        startText = new JTextField(defaultStart, TEXT_LEN);
        startText.setForeground(Color.GRAY);
        startPanel.add(startLabel);
        startPanel.add(startText);

        JPanel endPanel = new JPanel();
        endLabel = new JLabel("End Time:");
        endLabel.setMaximumSize(labelSize);
        endText = new JTextField(defaultEnd, TEXT_LEN);
        endText.setForeground(Color.GRAY);
        endPanel.add(endLabel);
        endPanel.add(endText);

        FocusListener focusListener = new FocusListener() {
            Color setColorTo;
            @Override
            public void focusGained(FocusEvent e) {
                setColorTo = Color.BLACK;
                changeColor(e);
            }

            @Override
            public void focusLost(FocusEvent e) {
                setColorTo = Color.GRAY;
                changeColor(e);
            }

            public void changeColor(FocusEvent e){
                Object src = e.getSource();
                if (src.equals(nameText) && defaultName.equals(nameText.getText())) {
                    nameText.setForeground(setColorTo);
                    statusLabel.setText("Editing new event name");
                } else if (src.equals(dateText) && date.toString().equals(dateText.getText())) {
                    dateText.setForeground(setColorTo);
                    statusLabel.setText("Editing new event date");
                } else if (src.equals(startText) && defaultStart.equals(startText.getText())) {
                    startText.setForeground(setColorTo);
                    statusLabel.setText("Editing new event start time");
                } else if (src.equals(endText) && defaultEnd.equals(endText.getText())) {
                    endText.setForeground(setColorTo);
                    statusLabel.setText("Editing new event end time");
                }
            }
        };

        //adding interaction for text fields
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

                logger.info(e.toString());
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
                NewEventDialogue.super.dispose();
            }
        });

        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText(String.format("New Event created with title \"%s\" on %s during %s - %s", nameText.getText(), dateText.getText(), startText.getText(), endText.getText()));
                NewEventDialogue.super.dispose();
            }
        });

        buttonsPanel.add(cancel);
        buttonsPanel.add(save);
        return buttonsPanel;
    }
}
