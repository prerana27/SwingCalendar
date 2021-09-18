package utilities;

import javax.swing.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimeSpinner extends SpinnerDateModel {
    private SimpleDateFormat formatterDefault;
    private Date defaultStart, defaultEnd;
    private String time;

    public TimeSpinner(String time) {
        this.time = time;
        formatterDefault = new SimpleDateFormat("HH:mm");
        try {
            defaultStart = formatterDefault.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Object getNextValue() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(defaultStart);
        cal.add(Calendar.MINUTE, 15);
        return cal.getTime();
    }

    public Object getPreviousValue() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(defaultStart);
        cal.add(Calendar.MINUTE, -15);
        return cal.getTime();
    }
}
