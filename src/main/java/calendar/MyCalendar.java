package calendar;

import javax.swing.*;
import java.util.logging.Logger;

public class MyCalendar {
    private static Logger logger = Logger.getLogger("calendar.MyCalendar");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                logger.info("Starting my calendar ..");
                CalendarMainWindow calendar = new CalendarMainWindow();
                calendar.setVisible(true);
            }
        });
    }
}
