package hcmus.studentscalendar;

import java.util.Calendar;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Blue on 12/16/2015.
 */
@Root
public class Assignment extends Event {
    @Element private String subject;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Assignment () {

    }
}
