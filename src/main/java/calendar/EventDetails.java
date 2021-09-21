package calendar;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public class EventDetails {

    private String eventName;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate eventDate;
    private long timeDiff;

    public Map<String, Boolean> getTypes() {
        return types;
    }

    public void setTypes(Map<String, Boolean> types) {
        this.types = types;
    }

    private Map<String, Boolean> types;

    //TODO implement checklists stuff
    public EventDetails(String eventName, LocalTime startTime, LocalTime endTime, LocalDate eventDate, Map<String, Boolean> types) {
        this.eventName = eventName;
        this.startTime = startTime;
        setEndTime(endTime);
        this.eventDate = eventDate;
        this.types = types;
    }

    public long getTimeDiff() {
        return timeDiff;
    }

    public void setTimeDiff(long timeDiff) {
        this.timeDiff = timeDiff;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
        if (startTime.until(endTime, ChronoUnit.MINUTES) < 0) {
            this.endTime = LocalTime.of(23, 59);
        }
        this.timeDiff = startTime.until(this.endTime, ChronoUnit.MINUTES);
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    @Override
    public String toString() {
        return String.format("%s on %s between %s - %s with time diff of %s and the following types checked: %s",
                this.eventName, this.eventDate, this.startTime, this.endTime, this.timeDiff, getCheckedTypes());
    }

    public String getCheckedTypes() {
        StringBuilder stringBuilder = new StringBuilder("");
        for (String key : this.types.keySet()) {
            if (this.types.get(key))
                stringBuilder.append(key).append(" ");
        }
        return stringBuilder.toString();
    }
}
