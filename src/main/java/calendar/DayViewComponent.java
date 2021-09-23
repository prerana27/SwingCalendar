package calendar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.*;
import java.util.logging.Logger;

public class DayViewComponent extends JComponent {
    private static final Logger logger = Logger.getLogger("calendar.DayViewComponent");

    private static final int LEFT_PADDING_X0 = 20;
    private static final int HEADER_Y0 = 10;
    private static final int HEADER_HEIGHT = 60;
    private static int HEADER_Y1;
    private static final int COMPONENT_WIDTH = 800;
    private static final int COMPONENT_HEIGHT = 600;
    private static final int COMPONENT_MAX_HEIGHT = 1500;
    private static final int TIME_X0 = LEFT_PADDING_X0 + 10;
    private static final int TIME_LINE_X1 = COMPONENT_WIDTH - 2 * LEFT_PADDING_X0;
    private static final int TIME_BOX_HEIGHT = 60;
    private static int TIME_BOX_WIDTH;
    private static int TIME_LINE_X0;
    private static final int TIME_LINE_PADDING = 4;
    public static final double MINUTE_GRANULARITY = 15.0;
    private static final int LAST_LINE_Y = HEADER_Y1 + 24 * TIME_BOX_HEIGHT;
    private static final Color DRAG_FILL = new Color(226, 226, 226, 150);
    private boolean isDragging = false;
    private DateTimeFormatter dateMonthFormat, dayFormat;
    private EventDetails evt = null;
    private int startY, endY = -1;

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
    private Font timeFont, headingFont;
    private int maxWidthTime;
    private FontMetrics fontMetrics;
    private LocalTime START_TIME = LocalTime.of(0, 0);
    public Map<String, List<EventDetails>> eventsMap;

    DayViewComponent(LocalDate localDate, Map<String, List<EventDetails>> eventsMap) {
        this.currentDate = LocalDate.now();
        this.eventsMap = eventsMap;
        dateMonthFormat = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        dayFormat = DateTimeFormatter.ofPattern("EEEE");
        timeFont = new Font("SansSerif", Font.PLAIN, 13);
        headingFont = new Font("SansSerif", Font.BOLD, 26);
        fontMetrics = getFontMetrics(timeFont);
        maxWidthTime = fontMetrics.stringWidth("08:00") + 20;
        HEADER_Y1 = HEADER_Y0 + HEADER_HEIGHT + 2 * getFontMetrics(headingFont).getHeight();
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
        drawDate(g);
        drawDayDivision(g);
        drawCurrentTime(g);
        drawEvents(g);
        drawDragFeedback(g);
    }

    private void drawDate(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setFont(headingFont);
        g2.setColor(Color.BLACK);
        g2.drawString(this.currentDate.format(dateMonthFormat), TIME_X0, HEADER_Y0 + getFontMetrics(headingFont).getHeight());
        g2.drawString(this.currentDate.format(dayFormat), TIME_X0, HEADER_Y0 + 2 * getFontMetrics(headingFont).getHeight());
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

            for (int i = 0; i < numberEvents; i++) {
                EventDetails curr = events.get(i);
                int boxHeight = (int) curr.getStartTime().until(curr.getEndTime(), ChronoUnit.MINUTES) * TIME_BOX_HEIGHT / 60;

                if (curr.isDragged())
                    g2.setColor(DRAG_FILL);
                else
                    g2.setColor(new Color(228, 235, 255, 150));

                g2.fillRoundRect(TIME_LINE_X0, getTimeToPosn(curr.getStartTime()), TIME_BOX_WIDTH, boxHeight, 10, 10);

                String output = String.format("%s (%s - %s)", curr.getEventName(), curr.getStartTime(), curr.getEndTime());
                drawEventTypes(g2, curr.getTypes(),
                        TIME_LINE_X0 + 5 + fontMetrics.stringWidth(output) + TIME_LINE_PADDING,
                        getTimeToPosn(curr.getStartTime()) + 2);

                g2.setFont(timeFont);
                g2.setColor(Color.DARK_GRAY);
                g2.drawString(output, TIME_LINE_X0 + 5, getTimeToPosn(curr.getStartTime()) + 13);
            }
        }
    }

    private void drawEventTypes(Graphics2D g2, Map<String, Boolean> types, int x, int y) {
        int i = 1;
        for (String key : types.keySet()) {
            if (types.get(key)) {
                g2.setColor(Color.WHITE);
                g2.fillOval(x + (i * 22), y, 13, 13);
                g2.setColor(getColor(key));
                g2.fillOval(x + (i * 22), y + 1, 10, 10);
                i++;
            }
        }
    }

    private void drawDragFeedback(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        if (isDragging && endY>=0 && endY > startY) {
            g2.setColor(DRAG_FILL);
            g2.fillRoundRect(TIME_LINE_X0, startY, TIME_BOX_WIDTH, endY - startY, 10, 10);
        }

    }

    public LocalTime getPosnToTime(int y) {
        if (y > LAST_LINE_Y)
            return LocalTime.of(23, 59);
        int hour = ((y - HEADER_Y1) / TIME_BOX_HEIGHT) % 24;
        int min = ((y - HEADER_Y1 - (hour * TIME_BOX_HEIGHT)) * 60 / TIME_BOX_HEIGHT) % 60;
        if (min < 0) min = 0;
        if (hour < 0) hour = 0;
        LocalTime time = roundMinutes(LocalTime.of(hour, min));
        logger.info(String.format("%s converts to %s", y, time.toString()));
        return time;
    }

    public int getTimeToPosn(LocalTime time) {
        return HEADER_Y1 + time.getHour() * TIME_BOX_HEIGHT + (60 * time.getMinute() / TIME_BOX_HEIGHT);
    }

    private class ClickListener implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getY() > LAST_LINE_Y)
                return;
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
            if (evt == null && isDragging && e.getY() > startY) {
                logger.info(String.format("Creating event for points %s %s", startY, e.getY()));
                addEvent(new EventDetails(NewEventDialogue.DEFAULT_NAME, getPosnToTime(startY), getPosnToTime(e.getY()), currentDate));
                DayViewComponent.this.repaint();
            }
            isDragging = false;
            if (evt != null)
                evt.setDragged(false);
            evt = null;
            endY = -1;
            DayViewComponent.this.repaint();
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
            if (evt == null && checkForEvent(e) != null) {
                evt = checkForEvent(e);
                isDragging = true;
                updateEvent(e);
                evt.setDragged(true);
            } else if (evt != null && isDragging) {
                updateEvent(e);
            } else if (evt == null && !isDragging) {
                startY = e.getY();
                isDragging = true;
            } else if (evt == null && isDragging) {
                endY = e.getY();
                repaintOnUpdate();
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {

        }

        public void updateEvent(MouseEvent e) {
            evt.setStartTime(getPosnToTime(e.getY()));
            evt.setEndTime(evt.getStartTime().plusMinutes(evt.getTimeDiff()));
            logger.info(String.format("%s moved to %s - %s", evt.getEventName(), evt.getStartTime(), evt.getEndTime()));
            if (evt.getTimeDiff() == 0) removeEvent(evt);
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
        if (eventDetails.getTimeDiff() == 0) return;
        if (eventsMap.containsKey(eventDetails.getEventDate().toString())) {
            eventsMap.get(eventDetails.getEventDate().toString()).add(eventDetails);
        } else {
            eventsMap.put(eventDetails.getEventDate().toString(), new ArrayList<>(Arrays.asList(eventDetails)));
        }
        logger.info(String.format("Created event : %s", eventDetails));
        this.repaint();
    }

    public void removeEvent(EventDetails eventDetails) {
        if (eventsMap.containsKey(eventDetails.getEventDate().toString())) {
            eventsMap.get(eventDetails.getEventDate().toString()).remove(eventDetails);
        }
        logger.info(String.format("Removed event : %s", eventDetails));
        this.repaint();
    }

    public void repaintOnUpdate() {
        this.repaint();
    }

    private Color getColor(String key) {
        switch (key) {
            case "Work":
                return NewEventDialogue.WORK;
            case "Family":
                return NewEventDialogue.FAMILY;
            case "Vacation":
                return NewEventDialogue.VACATION;
            case "Health":
                return NewEventDialogue.HEALTH;
        }
        return new Color(0, 0, 0, 0);
    }
}
