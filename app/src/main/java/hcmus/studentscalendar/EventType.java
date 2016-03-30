package hcmus.studentscalendar;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Blue on 12/13/2015.
 */
public enum EventType {
    Activity("Activity"),
    GroupMeeting("Group meeting"),
    Festival("Festival"),
    Seminar("Seminar"),
    Report("Report"),
    PartTimeJob("Part-time job"),
    Birthday("Birthday");

    private String eventType;

    private EventType(String eventType){
        this.eventType = eventType;
    }

    @Override public String toString(){
        return eventType;
    }

    private static final Map<String, EventType> map = new HashMap<>();
    static {
        for (EventType en : values()) {
            map.put(en.toString(), en);
        }
    }

    public static EventType valueFor(String name) {
        return map.get(name);
    }

    public static int eventIcon(EventType eventType) {
        if(eventType != null) {
            switch (eventType) {
                case Birthday:
                    return R.drawable.birthday;
                case Activity:
                    return R.drawable.activity;
                case Festival:
                    return R.drawable.festival;
                case Seminar:
                    return R.drawable.seminar;
                case GroupMeeting:
                    return R.drawable.groupmeeting;
                case PartTimeJob:
                    return R.drawable.job;
                case Report:
                    return R.drawable.report;
                default:
                    return R.drawable.assignment;
            }
        }
        else {
            return R.drawable.assignment;
        }
    }
}
