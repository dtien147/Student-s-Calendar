package hcmus.studentscalendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Blue on 12/13/2015.
 */
@Root
public class Event implements Comparable {
    @Element private String name;
    @Element(required = false) private String description;
    @Element private Calendar startDate;
    @Element private Calendar endDate;
    @Element private Priority priority;
    @Element(required = false) private EventType eventType;
    @Element private boolean allDay;
    @Element private int id;
    @Element private ReminderTime reminderTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy");
    public static SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm  a");

    public Event() {

    }

    public ReminderTime getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(ReminderTime reminderTime) {
        this.reminderTime = reminderTime;
    }

    public Event(EventType eventType, String name, String description, Calendar startDate,
                 Calendar endDate, Priority priority, boolean allDay, ReminderTime reminderTime) {
        this.id = Data.getInstance().findSmallestIdAvailable();
        this.eventType = eventType;

        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.priority = priority;
        this.allDay = allDay;
        this.reminderTime = reminderTime;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    @Override
    public int compareTo(Object event) {
        Event convertedEvent = (Event) event;
        return this.getStartDate().compareTo(convertedEvent.getStartDate());
    }

    public String getStartTime() {
    	return timeFormat.format(startDate.getTime()).toString();
    }
}
