package calendar;


import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class MyCalendar {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(MyCalendar.class);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                logger.info("Starting my calendar ..");
                DefaultView calendar = new DefaultView();
                calendar.setVisible(true);
            }
        });
    }
}
