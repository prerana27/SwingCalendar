package calendar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class DayViewComponent extends JComponent {
    private static final Logger logger = Logger.getLogger("calendar.DayViewComponent");

    private static final int LEFT_PADDING_X0 = 20;
    private static final int HEADER_Y0 = 20;
    private static final int HEADER_HEIGHT = 30;
    private static final int HEADER_Y1 = HEADER_Y0 + HEADER_HEIGHT;
    private static final int COMPONENT_WIDTH = 800;
    private static final int COMPONENT_HEIGHT = 600;
    private static final int COMPONENT_MAX_HEIGHT = 1550;
    private static final int TIME_X0 = LEFT_PADDING_X0 + 10;
    private static final int TIME_BOX_HEIGHT = 60;
    private static final int TIME_LINE_PADDING = 4;

    private LocalDate currentDate;
    private Font timeFont;
    private LocalTime START_TIME = LocalTime.of(0, 0);
    private HashMap<LocalDate, List<EventDetails>> eventsMap;

    DayViewComponent(LocalDate localDate) {
        this.currentDate = localDate;
        timeFont = new Font("SansSerif", Font.PLAIN, 14);

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                logger.info(String.format("mousePressed at %s which is %s", e.getY(), getPosnToTime(e.getY())));
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                logger.info(String.format("mouseDragged at %s which is %s", e.getY(), getPosnToTime(e.getY())));
            }
        });

        this.setMinimumSize(new Dimension(COMPONENT_WIDTH, COMPONENT_HEIGHT));
        this.setSize(new Dimension(COMPONENT_WIDTH, COMPONENT_MAX_HEIGHT));
        this.setPreferredSize(new Dimension(COMPONENT_WIDTH, COMPONENT_MAX_HEIGHT));
    }

    @Override
    public void paintComponent(Graphics g) {
        drawWhiteBackground(g);
        drawDayDivision(g);
    }

    private void drawWhiteBackground(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    private void drawDayDivision(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.LIGHT_GRAY);
        g2.setFont(timeFont);

        FontMetrics fontMetrics = getFontMetrics(timeFont);
        int max = fontMetrics.stringWidth("08:00") + 20;

        for (int i = 0; i <= 24; i++) {
            int y = HEADER_Y1 + i * TIME_BOX_HEIGHT;
            g2.drawString(START_TIME.plusHours(i).toString(), TIME_X0, y + TIME_LINE_PADDING);
            g2.drawLine(TIME_X0 + max, y, COMPONENT_WIDTH - 2 * LEFT_PADDING_X0, y);
        }

    }

    public LocalTime getPosnToTime(int y) {
        int hour = ((y - HEADER_Y1) / TIME_BOX_HEIGHT) % 24;
        LocalTime time = LocalTime.of(hour, 0);
        logger.info(String.format("%s converts to %s", y, time.toString()));
        return time;
    }

    public int getTimeToPosn(LocalTime time) {
        int y = HEADER_Y1 + time.getHour() * TIME_BOX_HEIGHT;
        logger.info(String.format("%s converts to %s", time.toString(), y));
        return y;
    }
}
