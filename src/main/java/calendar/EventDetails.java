package calendar;

import java.time.LocalDate;
import java.time.LocalTime;

public class EventDetails {

    private String eventName;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate eventDate;

    //TODO implement checklists stuff
    public EventDetails(String eventName, LocalTime startTime, LocalTime endTime, LocalDate eventDate){
        this.eventName = eventName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.eventDate = eventDate;
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
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }
}
