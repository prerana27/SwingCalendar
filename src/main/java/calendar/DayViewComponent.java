package calendar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
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
    private boolean isDragging = false;
    private EventDetails evt = null;
    private int startY;

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
        timeFont = new Font("SansSerif", Font.PLAIN, 13);
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
        g2.setColor(new Color(212, 212, 212, 150));
        g2.setFont(timeFont);

        for (int i = 0; i <= 24; i++) {
            int y = HEADER_Y1 + i * TIME_BOX_HEIGHT;
            g2.setColor(new Color(212, 212, 212, 150));
            g2.drawLine(TIME_X0 + maxWidthTime, y, TIME_LINE_X1, y);
            g2.setColor(Color.GRAY);
            g2.drawString(START_TIME.plusHours(i).toString(), TIME_X0, y + TIME_LINE_PADDING);
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

            //TODO check drawing for events post 23:45
            for (int i = 0; i < numberEvents; i++) {
                EventDetails curr = events.get(i);
                int boxHeight = (int) curr.getStartTime().until(curr.getEndTime(), ChronoUnit.MINUTES) * TIME_BOX_HEIGHT / 60;
                g2.setColor(new Color(255, 203, 199, 150));
                g2.fillRect(TIME_LINE_X0, getTimeToPosn(curr.getStartTime()), TIME_BOX_WIDTH, boxHeight);
                g2.setColor(Color.GRAY);
                g2.setFont(timeFont);
                g2.drawString(curr.getEventName(), TIME_LINE_X0 + 5, getTimeToPosn(curr.getStartTime()) + 13);
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

    private class ClickListener implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            logger.info(String.format("mousePressed at %s which is %s", e.getY(), getPosnToTime(e.getY())));
            if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                if (checkForEvent(e) == null)
                    new NewEventDialogue(DayViewComponent.this, getPosnToTime(e.getY()));
                else
                    new NewEventDialogue(DayViewComponent.this, checkForEvent(e));
                logger.info(String.format("Double Clicked at %s which is %s", e.getY(), getPosnToTime(e.getY())));
            }
        }


        @Override
        public void mouseReleased(MouseEvent e) {
            if (evt == null && isDragging) {
                logger.info(String.format("Creting event for points %s %s", startY, e.getY()));
                addEvent(new EventDetails(NewEventDialogue.DEFAULT_NAME, getPosnToTime(startY), getPosnToTime(e.getY()), currentDate));
                DayViewComponent.this.repaint();
            }
            isDragging = false;
            evt = null;
        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    private class DragListener implements MouseMotionListener {

        @Override
        public void mouseDragged(MouseEvent e) {
            logger.info(String.format("mouseDragged at %s which is %s", e.getY(), getPosnToTime(e.getY())));
            if (checkForEvent(e) != null) {
                evt = checkForEvent(e);
                isDragging = true;
                updateEvent(e);
            }

            if (evt != null && isDragging) {
                updateEvent(e);
            }

            if (evt == null && !isDragging) {
                startY = e.getY();
                isDragging = true;
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {

        }

        public void updateEvent(MouseEvent e) {
            evt.setStartTime(getPosnToTime(e.getY()));
            evt.setEndTime(evt.getStartTime().plusMinutes(evt.getTimeDiff()));
            logger.info(String.format("%s moved to %s - %s", evt.getEventName(), evt.getStartTime(), evt.getEndTime()));
            DayViewComponent.this.repaint();
        }
    }

    public LocalTime roundMinutes(LocalTime time) {
        int mod = (int) (time.getMinute() % MINUTE_GRANULARITY);
        return time.plusMinutes(mod < 8 ? -mod : (15 - mod));
    }

    public EventDetails checkForEvent(MouseEvent e) {
        if (!eventsMap.containsKey(this.currentDate.toString())) return null;

        List<EventDetails> events = eventsMap.get(this.currentDate.toString());

        for (EventDetails evt : events) {
            int y0 = getTimeToPosn(evt.getStartTime());
            int y1 = getTimeToPosn(evt.getEndTime());

            if (e.getY() > y0 && e.getY() < y1)
                return evt;
        }
        return null;
    }

    public void addEvent(EventDetails eventDetails) {
        if (eventsMap.containsKey(eventDetails.getEventDate().toString())) {
            eventsMap.get(eventDetails.getEventDate().toString()).add(eventDetails);
        } else {
            eventsMap.put(eventDetails.getEventDate().toString(), new ArrayList<>(Arrays.asList(eventDetails)));
        }
        logger.info(eventDetails.toString());
        this.repaint();
    }

    public void repaintOnUpdate() {
        this.repaint();
    }
}
