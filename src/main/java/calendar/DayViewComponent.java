package calendar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.*;
import java.util.logging.Logger;

public class DayViewComponent extends JComponent {
    private static final Logger logger = Logger.getLogger("calendar.DayViewComponent");

    private static final int LEFT_PADDING_X0 = 20;
    private static final int HEADER_Y0 = 10;
    private static final int HEADER_HEIGHT = 30;
    private static final int HEADER_Y1 = HEADER_Y0 + HEADER_HEIGHT;
    private static final int COMPONENT_WIDTH = 800;
    private static final int COMPONENT_HEIGHT = 600;
    private static final int COMPONENT_MAX_HEIGHT = 1550;
    private static final int TIME_X0 = LEFT_PADDING_X0 + 10;
    private static final int TIME_LINE_X1 = COMPONENT_WIDTH - 2 * LEFT_PADDING_X0;
    private static final int TIME_BOX_HEIGHT = 60;
    private static int TIME_BOX_WIDTH;
    private static int TIME_LINE_X0;
    private static final int TIME_LINE_PADDING = 4;
    public static final double MINUTE_GRANULARITY = 15.0;

    public LocalDate getCurrentDate() {
        return currentDate;
    }

    public LocalDate setCurrentDate(LocalDate currentDate) {
        this.currentDate = currentDate;
        this.repaint();
        return this.currentDate;
    }

    public LocalDate updateDate(boolean isDayView, boolean isIncrease) {
        if (isDayView && isIncrease)
            this.currentDate = this.currentDate.plusDays(1);
        else if (isDayView)
            this.currentDate = this.currentDate.minusDays(1);
        else if (isIncrease)
            this.currentDate = this.currentDate.plusMonths(1);
        else
            this.currentDate = this.currentDate.minusMonths(1);
        this.repaint();
        return this.currentDate;
    }

    private LocalDate currentDate;
    private Font timeFont;
    private int maxWidthTime;
    private FontMetrics fontMetrics;
    private LocalTime START_TIME = LocalTime.of(0, 0);
    public Map<String, List<EventDetails>> eventsMap;

    DayViewComponent(LocalDate localDate, Map<String, List<EventDetails>> eventsMap) {
        this.currentDate = LocalDate.now();
        this.eventsMap = eventsMap;
        timeFont = new Font("SansSerif", Font.PLAIN, 14);
        fontMetrics = getFontMetrics(timeFont);
        maxWidthTime = fontMetrics.stringWidth("08:00") + 20;
        TIME_LINE_X0 = TIME_X0 + maxWidthTime;
        TIME_BOX_WIDTH = TIME_LINE_X1 - TIME_LINE_X0;

        addMouseListener(new ClickListener());
        addMouseMotionListener(new DragListener());

        this.setMinimumSize(new Dimension(COMPONENT_WIDTH, COMPONENT_HEIGHT));
        this.setSize(new Dimension(COMPONENT_WIDTH, COMPONENT_MAX_HEIGHT));
        this.setPreferredSize(new Dimension(COMPONENT_WIDTH, COMPONENT_MAX_HEIGHT));
    }

    @Override
    public void paintComponent(Graphics g) {
        drawWhiteBackground(g);
        drawDayDivision(g);
        drawCurrentTime(g);
        drawEvents(g);
    }

    private void drawWhiteBackground(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    private void drawDayDivision(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.LIGHT_GRAY);
        g2.setFont(timeFont);

        for (int i = 0; i <= 24; i++) {
            int y = HEADER_Y1 + i * TIME_BOX_HEIGHT;
            g2.drawString(START_TIME.plusHours(i).toString(), TIME_X0, y + TIME_LINE_PADDING);
            g2.drawLine(TIME_X0 + maxWidthTime, y, TIME_LINE_X1, y);
        }

    }

    private void drawCurrentTime(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.RED);
        int y = getTimeToPosn(LocalTime.now());
        g2.drawLine(TIME_X0 + maxWidthTime, y, TIME_LINE_X1, y);
    }

    private void drawEvents(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        for (String e : eventsMap.keySet()) {
            if (!currentDate.toString().equals(e)) continue;
            logger.info(String.format("Comparing dates %s %s %s", currentDate.toString(), e, currentDate.toString().equals(e)));
            List<EventDetails> events = eventsMap.get(e);
            int numberEvents = events.size();

            for (int i = 0; i < numberEvents; i++) {
                g2.setColor(new Color(255, 203, 199, 150));
                g2.fillRect(TIME_LINE_X0, getTimeToPosn(events.get(i).getStartTime()), TIME_BOX_WIDTH, TIME_BOX_HEIGHT);
            }
        }
    }

    public LocalTime getPosnToTime(int y) {
        int hour = ((y - HEADER_Y1) / TIME_BOX_HEIGHT) % 24;
        int min = ((y - HEADER_Y1 - (hour * TIME_BOX_HEIGHT)) * 60 / TIME_BOX_HEIGHT) % 60;
        if (min < 0) min = 0;
        LocalTime time = roundMinutes(LocalTime.of(hour, min));
        logger.info(String.format("%s converts to %s", y, time.toString()));
        return time;
    }

    public int getTimeToPosn(LocalTime time) {
        int y = HEADER_Y1 + time.getHour() * TIME_BOX_HEIGHT + (60 * time.getMinute() / TIME_BOX_HEIGHT);
        return y;
    }

    private class ClickListener extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            logger.info(String.format("mousePressed at %s which is %s", e.getY(), getPosnToTime(e.getY())));
            if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                checkForEvent(e);
                new NewEventDialogue(DayViewComponent.this, getPosnToTime(e.getY()));
                logger.info(String.format("Double Clicked at %s which is %s", e.getY(), getPosnToTime(e.getY())));
            }
        }
    }

    private class DragListener extends MouseMotionAdapter {
        public void mouseDragged(MouseEvent e) {
            logger.info(String.format("mouseDragged at %s which is %s", e.getY(), getPosnToTime(e.getY())));
        }
    }

    public LocalTime roundMinutes(LocalTime time) {
        int mod = (int)(time.getMinute() % MINUTE_GRANULARITY);
        return time.plusMinutes( mod < 8 ? -mod : (15-mod));
    }

    public void checkForEvent(MouseEvent e){

    }

    public void addEvent(EventDetails eventDetails) {
        if (eventsMap.containsKey(eventDetails.getEventDate().toString())) {
            eventsMap.get(eventDetails.getEventDate().toString()).add(eventDetails);
        } else {
            eventsMap.put(eventDetails.getEventDate().toString(), new ArrayList<>(Arrays.asList(eventDetails)));
        }
        this.repaint();
    }
}
