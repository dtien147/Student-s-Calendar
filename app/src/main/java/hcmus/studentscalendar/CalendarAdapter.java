package hcmus.studentscalendar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Blue on 12/10/2015.
 */
public class CalendarAdapter extends BaseAdapter {
    private static Calendar currentDate;
    private static Calendar selectedDate;
    private TextView currentDayView;
    private Calendar displayMonth;
    private ArrayList<Integer> days;
    private View selectedView;
    private Data data;
    LayoutInflater vi;

    public static Calendar getSelectedDate() {
        return selectedDate;
    }

    public static Calendar getCurrentDate() {
        return currentDate;
    }

    public CalendarAdapter(Context c, Calendar monthCalendar) {
        vi = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        currentDate = monthCalendar;
        displayMonth = (Calendar) monthCalendar.clone();
        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        selectedView = new View(c);
        data = Data.getInstance();
        this.days = new ArrayList<>();
        refreshDays();
    }

    public void cancelSelection() {
        selectedDate = null;
        selectedView.setBackgroundResource(R.color.calendarViewBackground);
    }

    public void select(View v, int position)
    {
        selectedView.setBackgroundResource(R.color.calendarViewBackground);
        v.setBackgroundResource(R.color.daySelected);
        selectedView = v;

        findSelectedDate(position);
    }

    public int getCount() {
        return days.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new view for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        TextView dayView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            v = vi.inflate(R.layout.day, null);

        }

        dayView = (TextView)v.findViewById(R.id.day);
        dayView.setTextColor(Color.BLACK);
        dayView.setText(days.get(position).toString());

        TextView eventsNumber = (TextView) v.findViewById(R.id.eventNumber);

        //Show number of events of date
        Calendar findDate = (Calendar) displayMonth.clone();
        eventsNumber.setVisibility(View.INVISIBLE);

        findDate.set(Calendar.DAY_OF_MONTH, days.get(position));
        findDate.set(Calendar.HOUR_OF_DAY, 23);
        findDate.set(Calendar.MINUTE, 59);
        findDate.set(Calendar.SECOND, 59);
        ArrayList<Event> events = data.getEvents(findDate);
        Integer size = events.size();

        if(size > 0) {
            eventsNumber.setVisibility(View.VISIBLE);
            eventsNumber.setText(size.toString());
        }

        if(!data.isShowNumberOfEvent()) {
            eventsNumber.setText("");
        }

        // disable empty days from the beginning
        if((position < 7 && days.get(position) > 20)
                || (position > 28 && days.get(position) < 7)) {
            dayView.setClickable(false);
            dayView.setFocusable(false);
            dayView.setTextColor(v.getResources().getColor(R.color.disabled));
            eventsNumber.setVisibility(View.INVISIBLE);
        }
        else {
            // highlight current day
            if (findDate.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR)
                    && findDate.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH)
                    && days.get(position) == currentDate.get(Calendar.DAY_OF_MONTH)) {
                dayView.setTextColor(v.getResources().getColor(R.color.theme));
                dayView.setTag(new Integer(position));
                currentDayView = dayView;
            }
        }

        return v;
    }

    public void refreshDays() {
        if(displayMonth.get(Calendar.MONTH) != currentDate.get(Calendar.MONTH)) {
            cancelSelection();
            currentDayView.setTextColor(Color.BLACK);
        }

        if(selectedDate != null &&
                displayMonth.get(Calendar.MONTH) != selectedDate.get(Calendar.MONTH)) {
            selectedView.setBackgroundResource(R.color.calendarViewBackground);
        }

        // clear items
        days.clear();

        Calendar currentDay = (Calendar) displayMonth.clone();
        currentDay.set(Calendar.DAY_OF_MONTH, 1);
        while (currentDay.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            currentDay.add(Calendar.DAY_OF_MONTH, -1);
        }

        while(currentDay.get(Calendar.DAY_OF_MONTH) < displayMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
                || currentDay.get(Calendar.MONTH) != displayMonth.get(Calendar.MONTH)){
            days.add(currentDay.get(Calendar.DAY_OF_MONTH));
            currentDay.add(Calendar.DAY_OF_MONTH, +1);
        }

        if(currentDay.get(Calendar.DAY_OF_MONTH) == 29) {
            days.add(currentDay.get(Calendar.DAY_OF_MONTH));
            currentDay.add(Calendar.DAY_OF_MONTH, +1);
        }

        while(days.size() % 7 != 0){
            days.add(currentDay.get(Calendar.DAY_OF_MONTH));
            currentDay.add(Calendar.DAY_OF_MONTH, +1);
        }

        notifyDataSetChanged();
    }

    public void addMonth(int value) {
        displayMonth.add(Calendar.MONTH, value);
        refreshDays();
    }

    public Date getDisplayMonth() {
        return displayMonth.getTime();
    }

    public void showToday() {
        selectedView.setBackgroundResource(R.color.calendarViewBackground);
        displayMonth = (Calendar) currentDate.clone();
        refreshDays();
        currentDayView.setBackgroundResource(R.color.daySelected);
        selectedView = currentDayView;

        findSelectedDate((int) selectedView.getTag());
    }

    private void findSelectedDate(int position) {
        selectedDate = (Calendar) displayMonth.clone();

        selectedDate.set(Calendar.DAY_OF_MONTH, days.get(position));
        selectedDate.set(Calendar.HOUR_OF_DAY, 23);
        selectedDate.set(Calendar.MINUTE, 59);

        if((position < 7 && days.get(position) > 20)
                || (position > 30 && days.get(position) < 7)) {
            selectedDate.add(Calendar.MONTH, -1);
        }
    }
}