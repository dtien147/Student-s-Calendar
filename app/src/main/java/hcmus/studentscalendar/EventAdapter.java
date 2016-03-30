package hcmus.studentscalendar;

import android.provider.ContactsContract;
import android.widget.BaseAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class EventAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<Integer> items;
    private Data data;

    public EventAdapter(Context c) {
        mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        data = Data.getInstance();
        items = new ArrayList<>();
        refresh();
    }

    public int getCount() {
        return items.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        // Define a way to determine which layout to use, here it's just evens and odds.
        if(items.get(position) == 0) {
            return 0;
        }

        return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2; // Count of different layouts
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        if(items.get(position) == -1) {
            return false;
        }

        return true;
    }

    // create a new view for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
       // if (convertView == null) {  // if it's not recycled, initialize some attributes
            //Events date bar
            if(items.get(position) == -1) {
                v = mInflater.inflate(R.layout.events_date, null);
                v.setFocusable(false);
                v.setClickable(false);
                v.setEnabled(false);
                Date date = data.getEvent(items.get(position + 1)).getStartDate().getTime();
                TextView eventsDate = (TextView) v.findViewById(R.id.eventsDate);
                eventsDate.setText(Event.dateFormat.format(date));
            }
            //Event
            else {
                v = mInflater.inflate(R.layout.event, null);
                Event event = data.getEvent(items.get(position));

                TextView eventName = (TextView) v.findViewById(R.id.eventName);
                TextView eventStartTime = (TextView) v.findViewById(R.id.eventStartTime);
                ImageView eventType = (ImageView) v.findViewById(R.id.eventTypeImage);

                eventName.setText(event.getName());
                eventStartTime.setText(event.getStartTime());
                eventType.setImageResource(EventType.eventIcon(event.getEventType()));
                eventName.setTextColor(v.getResources()
                        .getColor(Priority.getTextColor(event.getPriority())));


                if(event.isAllDay()) {
                    eventStartTime.setText("All day");
                }

                if(event instanceof Assignment) {
                    TextView subject = (TextView) v.findViewById(R.id.subject);
                    subject.setText(((Assignment) event).getSubject());
                }
            }
       // }
        return v;
    }

    public void refresh() {
        ArrayList<Event> events;

        if(CalendarAdapter.getSelectedDate() == null) {
            events = data.getEventsFrom(CalendarAdapter.getCurrentDate());
        }
        else {
            events = data.getEvents(CalendarAdapter.getSelectedDate());
        }

        createEventList(events, data);

        notifyDataSetChanged();
    }

    public int getEventPosition(int position) {
        return items.get(position);
    }

    private void createEventList(ArrayList<Event> events, Data data) {
        items.clear();

        if(events.size() > 0) {
            items.add(-1);          //Add events date bar
            items.add(data.getEvents().indexOf(events.get(0)));           //Add position of event

            for(int i = 1 ; i <  events.size(); i++) {
                Date prevDate = events.get(i - 1).getStartDate().getTime();
                Date curDate = events.get(i).getStartDate().getTime();
                if(!Event.dateFormat.format(prevDate).equals(Event.dateFormat.format(curDate))) {
                    items.add(-1);      //Add events date bar
                }

                items.add(data.getEvents().indexOf(events.get(i)));
            }
        }
    }

    public void search(String eventName) {
        ArrayList<Event> events = data.search(eventName);

        createEventList(events, data);

        notifyDataSetChanged();
    }
}
