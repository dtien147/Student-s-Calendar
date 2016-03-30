package hcmus.studentscalendar;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Blue on 1/3/2016.
 */
public enum ReminderTime {
    NoReminder("No reminder"),
    FiveSeconds("5 seconds"),
    FifteenSeconds("15 seconds"),
    OneMinute("1 minute"),
    FiveMinutes("5 minutes"),
    FifteenMinutes("15 minutes"),
    ThirtyMinutes("30 minutes"),
    OneHour("1 hour"),
    ThreeHours("3 hours"),
    SixHours("6 hours"),
    TwelveHours("12 hours"),
    OneDay("1 day"),
    ThreeDays("3 days"),
    OneWeek("1 week");

    private String reminderTime;

    private ReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }

    @Override
    public String toString() {
        return reminderTime;
    }

    private static final Map<String, ReminderTime> map = new HashMap<>();

    static {
        for (ReminderTime en : values()) {
            map.put(en.toString(), en);
        }
    }

    public static ReminderTime valueFor(String name) {
        return map.get(name);
    }

    public static int getTimeInSecond(ReminderTime reminderTime) {
        switch (reminderTime) {
            case NoReminder:
                return 0;
            case FiveSeconds:
                return 5;
            case FifteenSeconds:
                return 15;
            case OneMinute:
                return 60;
            case FiveMinutes:
                return 5 * 60;
            case FifteenMinutes:
                return 15 * 60;
            case ThirtyMinutes:
                return 30 * 60;
            case OneHour:
                return 60 * 60;
            case ThreeHours:
                return 3 * 60 * 60;
            case SixHours:
                return 6 * 60 * 60;
            case TwelveHours:
                return 12 * 60 * 60;
            case OneDay:
                return 24 * 60 * 60;
            case ThreeDays:
                return 3 * 24 * 60 * 60;
            case OneWeek:
                return 7 * 24 * 60 * 60;
            default:
                return 0;
        }
    }
}
