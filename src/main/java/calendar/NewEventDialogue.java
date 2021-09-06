package calendar;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.time.LocalDate;

import static javax.swing.BoxLayout.Y_AXIS;

public class NewEventDialogue extends JDialog {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(NewEventDialogue.class);
    private static final int TEXT_LEN = 15;
    private JPanel basePanel, eventDetails, eventType, buttonsPanel;
    private JLabel statusLabel, nameLabel, dateLable, startLabel, endLabel;
    private JTextField nameText, dateText, startText, endText;
    private LocalDate date;
    private JButton save, cancel;
    
    NewEventDialogue(LocalDate date, JLabel statusLabel){
        this.date = date;
        this.statusLabel = statusLabel;
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        basePanel = new JPanel();
        basePanel.setLayout(new BoxLayout(basePanel, Y_AXIS));
        basePanel.add(addEventDetails());
        basePanel.add(addEventTypes());
        basePanel.add(addButtons());
        this.add(basePanel);
        
        this.pack();
        this.setVisible(true);
    }
    
    private JPanel addEventDetails(){
        eventDetails = new JPanel();
        eventDetails.setLayout(new BoxLayout(eventDetails, Y_AXIS));

        JPanel namePanel = new JPanel();
        nameLabel = new JLabel("Name:");
        //todo - make it placeholder style
        nameText = new JTextField("New Event", TEXT_LEN);
        namePanel.add(nameLabel);
        namePanel.add(nameText);

        JPanel datePanel = new JPanel();
        dateLable = new JLabel("Date:");
        dateText = new JTextField(date.toString(), TEXT_LEN);
        datePanel.add(dateLable);
        datePanel.add(dateText);

        JPanel startPanel = new JPanel();
        startLabel = new JLabel("Start Time:");
        startText = new JTextField(TEXT_LEN);
        startPanel.add(startLabel);
        startPanel.add(startText);

        JPanel endPanel = new JPanel();
        endLabel = new JLabel("End Time:");
        endText = new JTextField(TEXT_LEN);
        endPanel.add(endLabel);
        endPanel.add(endText);

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

    private JPanel addButtons(){
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
                statusLabel.setText(String.format("New Event created titled %s on %s during %s  %s", nameText.getText(), dateText.getText(), startText.getText(), endText.getText()));
                NewEventDialogue.super.dispose();
            }
        });

        buttonsPanel.add(cancel);
        buttonsPanel.add(save);
        return buttonsPanel;
    }
}
