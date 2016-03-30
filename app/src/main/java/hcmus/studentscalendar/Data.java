package hcmus.studentscalendar;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 * Created by Blue on 12/13/2015.
 */
@Root(name="DATA")
public class Data {
    private static Data data;
    private static String filePath = "/data/data/hcmus.studentscalendar/files/Data.xml";
    private static String icsPath = "/data/data/hcmus.studentscalendar/files/Moodle.ics";
    @ElementList private ArrayList<Event> events;
    @Element private boolean canNotify = true;
    @Element private boolean canAlarm = true;
    @Element private boolean showNumberOfEvent = false;
    @ElementList private ArrayList<String> subjectsHidden;
    @Element (required = false) private String icsUrl = "";
    @Element private boolean autoSync = true;
    @Element (required = false) private String password = "";

    private int numberOfNewAssignment = 0;

    public static void setFilePath(String filePath) {
        Data.filePath = filePath;
    }

    public static void setIcsPath(String icsPath) {
        Data.icsPath = icsPath;
    }

    public static String getIcsPath() {
        return icsPath;
    }

    public String getIcsUrl() {
        return icsUrl;
    }

    public void setIcsUrl(String icsUrl) {
        this.icsUrl = icsUrl;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public boolean isShowNumberOfEvent() {
        return showNumberOfEvent;
    }

    public void setShowNumberOfEvent(boolean showNumberOfEvent) {
        this.showNumberOfEvent = showNumberOfEvent;

    }

    public boolean isCanAlarm() {
        return canAlarm;
    }

    public void setCanAlarm(boolean canAlarm) {
        this.canAlarm = canAlarm;
    }

    public boolean isAutoSync() {
        return autoSync;
    }

    public void setAutoSync(boolean autoSync) {
        this.autoSync = autoSync;
    }

    public boolean isCanNotify() {
        return canNotify;
    }

    public void setCanNotify(boolean canNotify) {
        this.canNotify = canNotify;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private Data() {
        events = new ArrayList<>();
        subjectsHidden = new ArrayList<>();
    }

    public int getNumberOfNewAssignment() {
        return numberOfNewAssignment;
    }

    public void setNumberOfNewAssignment(int numberOfNewAssignment) {
        this.numberOfNewAssignment = numberOfNewAssignment;
    }

    public void addEvent(Event newEvent) {
        if(newEvent instanceof Assignment) {
            boolean assignmentExist = false;
            Assignment newAssignment = (Assignment) newEvent;

            for (Event event : events) {
                if(event instanceof Assignment) {
                    Assignment assignment = (Assignment) event;
                    if(assignment.getId() == newAssignment.getId()) {
                        assignmentExist = true;
                        newEvent.setPriority(event.getPriority());
                        newEvent.setReminderTime(event.getReminderTime());
                        events.remove(event);
                        break;
                    }
                }
            }

            events.add(newEvent);

            if(assignmentExist == false) {
                numberOfNewAssignment++;
            }
        }
        else {
            events.add(newEvent);

        }

        sortByTime();
        save();
    }
    
    public void removeEvent(int index) {
        events.remove(index);
    }
    
    public Event getEvent(int index) {
    	return events.get(index);
    }

    public static Data getInstance() {
        if(data == null) {
            data = new Data();
            data.load();
        }
        return data;
    }

    public ArrayList<Event> getEvents(Calendar findDate) {
        ArrayList<Event> eventsSatisfy = new ArrayList<>();
        int  i = 0;
        while(i < events.size()) {
            Calendar eventDate = events.get(i).getStartDate();

            if(eventDate.get(Calendar.DAY_OF_MONTH) == findDate.get(Calendar.DAY_OF_MONTH)
                    && eventDate.get(Calendar.MONTH) == findDate.get(Calendar.MONTH)
                    && eventDate.get(Calendar.YEAR) == findDate.get(Calendar.YEAR)) {
                eventsSatisfy.add(events.get(i));
            }

            i++;
        }

        for(int position = eventsSatisfy.size() - 1; position >= 0; position--) {
            Event event = eventsSatisfy.get(position);
            if(event instanceof Assignment) {
                if(isHidden(((Assignment) event).getSubject())) {
                    eventsSatisfy.remove(position);
                }
            }
        }

        return eventsSatisfy;
    }

    public void hideSubject(String subject) {
        subjectsHidden.add(subject);
    }

    public void showSubject(String subject) {
        subjectsHidden.remove(Ulitites.Search.indexOfStringInStringList(subjectsHidden, subject));
    }

    public boolean isHidden(String subject) {
        if(Ulitites.Search.indexOfStringInStringList(subjectsHidden, subject) >= 0) {
            return true;
        }

        return false;
    }

    public void save() {
        File xmlFile = new File(filePath);
        // Serialize the Person
        try {
            xmlFile.delete();
            Serializer serializer = new Persister();
            serializer.write(data, xmlFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load() {
        File xmlFile = new File(filePath);
        if (xmlFile.exists()) {
            try {
                Serializer serializer = new Persister();
                data = serializer.read(Data.class, xmlFile);
            } catch (Exception e) {
                xmlFile.delete();
                data = new Data();
            }
        }
    }

    public void reset() {
        File xmlFile = new File(filePath);
        File icsFile = new File(icsPath);
        try {
            xmlFile.delete();
            icsFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public ArrayList<Event> getEventsFrom(Calendar startDate) {
        ArrayList<Event> eventsSatisfy = new ArrayList<>();
        int  i = 0;
        while(i < events.size()) {
            if(events.get(i).getStartDate().compareTo(startDate) >= 0) {
                eventsSatisfy.add(events.get(i));
            }

            i++;
        }

        for(int position = eventsSatisfy.size() - 1; position >= 0; position--) {
            Event event = eventsSatisfy.get(position);
            if(event instanceof Assignment) {
                if(Data.getInstance().isHidden(((Assignment) event).getSubject())) {
                    eventsSatisfy.remove(position);
                }
            }
        }

        return eventsSatisfy;
    }

    public void sortByPriority() {
        for(int i = 0; i < events.size() - 1; i++) {
            for(int j = i + 1; j < events.size(); j++) {
                if(events.get(i).getPriority().compareTo(events.get(j).getPriority()) < 0) {
                    Event temp = events.get(i);
                    events.set(i, events.get(j));
                    events.set(j, temp);
                }
                else {
                    if (events.get(i).getPriority().compareTo(events.get(j).getPriority()) == 0 &&
                            events.get(i).compareTo(events.get(j)) > 0) {
                        Event temp = events.get(i);
                        events.set(i, events.get(j));
                        events.set(j, temp);
                    }
                }
            }
        }
    }

    public void sortByTime() {
        Collections.sort(events);
    }

    public ArrayList<Event> search(String eventName) {
        ArrayList<Event> eventsSatisfy = new ArrayList<>();
        for (Event event : events) {
            if(event.getName().contains(eventName)) {
                eventsSatisfy.add(event);
            }
        }

        return eventsSatisfy;
    }

    public void deleteAllAssignments() {
        for (int i = events.size() - 1; i >= 0; i--) {
            if(events.get(i) instanceof Assignment) {
                removeEvent(i);
            }
        }
    }

    public boolean isShowAll() {
        return (subjectsHidden.size() == 0);
    }

    public int findSmallestIdAvailable() {
        ArrayList<Event> eventsTemp = (ArrayList<Event>) events.clone();

        //Sort by event id
        for(int i = 0; i < eventsTemp.size() - 1; i++) {
            for(int j = i + 1; j < eventsTemp.size(); j++) {
                if(eventsTemp.get(i).getId() < eventsTemp.get(j).getId()) {
                    Event temp = eventsTemp.get(i);
                    eventsTemp.set(i, eventsTemp.get(j));
                    eventsTemp.set(j, temp);
                }
            }
        }

        int id = 0;
        for(id = 0; id < eventsTemp.size(); id++) {
            if(eventsTemp.get(id).getId() != id) {
                break;
            }
        }
        return id;
    }

    public int indexOf(int id) {
        for(int i = 0; i < events.size(); i++) {
            if(events.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    public int size() {
        return events.size();
    }
}
