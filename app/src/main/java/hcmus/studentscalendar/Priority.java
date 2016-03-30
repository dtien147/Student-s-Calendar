package hcmus.studentscalendar;

/**
 * Created by Blue on 12/13/2015.
 */
public enum Priority {
    Low("Low"),
    Normal("Normal"),
    High("High");

    private String priority;

    private Priority(String priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return priority;
    }

    public static int getTextColor(Priority priority) {
        switch (priority) {
            case Low:
                return R.color.low;
            case High:
                return R.color.high;
            default:
                return R.color.normal;
        }
    }

}
