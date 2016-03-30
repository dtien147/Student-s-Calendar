package hcmus.studentscalendar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class AddEvent extends AppCompatActivity {
    private Calendar startDate;
    private Calendar endDate;
    private EditText startDateTV;
    private EditText endDateTV;
    private EditText startTimeTV;
    private EditText endTimeTV;
	private EditText eventNameTV;
	private EditText eventDescriptionTV;
    private Spinner eventType;
    private Spinner priority;
    private CheckBox allDay;
    private Spinner reminderSpinner;
    ImageView eventIcon;
    private Data data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        data = Data.getInstance();

        startDate = Calendar.getInstance();
        startDate.set(Calendar.MILLISECOND, 0);
        endDate = (Calendar)startDate.clone();
        endDate.add(Calendar.MINUTE, 1);

        startDateTV = (EditText) findViewById(R.id.addStartDate);
        endDateTV = (EditText) findViewById(R.id.addEndDate);
        startTimeTV = (EditText) findViewById(R.id.addStartTime);
        endTimeTV = (EditText) findViewById(R.id.addEndTime);
        eventNameTV = (EditText) findViewById(R.id.addEventName);
        eventDescriptionTV = (EditText) findViewById(R.id.addEventDescription);

        startDateTV.setText(Event.dateFormat.format(startDate.getTime()));
        endDateTV.setText(Event.dateFormat.format(endDate.getTime()));
        startTimeTV.setText(Event.timeFormat.format(startDate.getTime()));
        endTimeTV.setText(Event.timeFormat.format(endDate.getTime()));

        eventType = (Spinner) findViewById(R.id.addEventType);
        eventType.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item,
                EventType.values()));
        eventIcon = (ImageView) findViewById(R.id.addEventIcon);
        eventType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                eventIcon.setImageResource(EventType.eventIcon(EventType.
                        valueFor(eventType.getSelectedItem().toString())));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        priority = (Spinner) findViewById(R.id.addEventPriority);
        priority.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item, Priority.values()));
        priority.setSelection(((ArrayAdapter) priority.getAdapter())
                .getPosition(Priority.Normal));

        allDay = (CheckBox) findViewById(R.id.addAllDay);
        allDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setAllDay();
                    startTimeTV.setEnabled(false);
                    endTimeTV.setEnabled(false);
                } else {
                    startTimeTV.setEnabled(true);
                    endTimeTV.setEnabled(true);
                }
            }
        });
        
        reminderSpinner = (Spinner) findViewById(R.id.reminderSpinner);
        reminderSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item, ReminderTime.values()));
        reminderSpinner.setSelection(((ArrayAdapter) reminderSpinner.getAdapter())
                .getPosition(ReminderTime.NoReminder));
    }

    public void startDateClick(View v) {
        DatePickerDialog.OnDateSetListener callback=new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear,
                                  int dayOfMonth) {
                startDate.set(year, monthOfYear, dayOfMonth);
                fixConflict();
                startDateTV.setText(Event.dateFormat.format(startDate.getTime()));
            }
        };

        int ngay=startDate.get(Calendar.DAY_OF_MONTH);
        int thang=startDate.get(Calendar.MONTH);
        int nam=startDate.get(Calendar.YEAR);
        DatePickerDialog pic=new DatePickerDialog(
                AddEvent.this,
                callback, nam, thang, ngay);
        pic.setTitle("Start date");
        pic.show();
    }

    public void startTimeClick(View v) {
        TimePickerDialog.OnTimeSetListener callback=new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view,
                                  int hourOfDay, int minute) {
                startDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                startDate.set(Calendar.MINUTE, minute);
                fixConflict();
                startTimeTV.setText(Event.timeFormat.format(startDate.getTime()));
            }
        };

        int gio=startDate.get(Calendar.HOUR_OF_DAY);
        int phut=startDate.get(Calendar.MINUTE);
        TimePickerDialog time=new TimePickerDialog(
                AddEvent.this,
                callback, gio, phut, true);
        time.setTitle("Start time");
        time.show();
    }

    public void endDateClick(View v) {
        DatePickerDialog.OnDateSetListener callback=new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear,
                                  int dayOfMonth) {
                endDate.set(year, monthOfYear, dayOfMonth);
                fixConflict();
                endDateTV.setText(Event.dateFormat.format(endDate.getTime()));
            }
        };

        int ngay=endDate.get(Calendar.DAY_OF_MONTH);
        int thang=endDate.get(Calendar.MONTH);
        int nam=endDate.get(Calendar.YEAR);
        DatePickerDialog pic=new DatePickerDialog(
                AddEvent.this,
                callback, nam, thang, ngay);
        pic.setTitle("End date");
        pic.show();
    }

    public void endTimeClick(View v) {
        TimePickerDialog.OnTimeSetListener callback=new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view,
                                  int hourOfDay, int minute) {
                endDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                endDate.set(Calendar.MINUTE, minute);
                fixConflict();
                endTimeTV.setText(Event.timeFormat.format(endDate.getTime()));
            }
        };

        int gio=endDate.get(Calendar.HOUR_OF_DAY);
        int phut=endDate.get(Calendar.MINUTE);
        TimePickerDialog time=new TimePickerDialog(
                AddEvent.this,
                callback, gio, phut, true);
        time.setTitle("End time");
        time.show();
    }

    private void setAllDay() {
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 59);
        endDate.set(Calendar.HOUR_OF_DAY, 23);
        endDate.set(Calendar.MINUTE, 59);
        startTimeTV.setText(Event.timeFormat.format(startDate.getTime()));
        endTimeTV.setText(Event.timeFormat.format(endDate.getTime()));
    }

    public boolean fixConflict() {
        if(startDate.compareTo(endDate) >= 0) {
            endDate = (Calendar)startDate.clone();
            endDate.add(Calendar.MINUTE, 1);

            endTimeTV.setText(Event.timeFormat.format(endDate.getTime()));
            endDateTV.setText(Event.dateFormat.format(endDate.getTime()));
            if(allDay.isChecked()) {
                setAllDay();
            }
            return true;
        }

        return false;
    }
    
    public void accept(View v) {
    	if(!eventNameTV.getText().toString().equals("")) {
	    	Event newEvent = new Event(EventType.valueFor(eventType.getSelectedItem().toString()),
                    eventNameTV.getText().toString(),
	    			eventDescriptionTV.getText().toString(), 
	    			startDate, endDate, Priority.valueOf(priority.getSelectedItem().toString()),
                    allDay.isChecked(),
                    ReminderTime.valueFor(reminderSpinner.getSelectedItem().toString()));
	    	data.addEvent(newEvent);

            new Reminder().setReminder(this, newEvent);

            Toast.makeText(this, "Event created", Toast.LENGTH_SHORT).show();
    	}
        else {
            Toast.makeText(this, "Empty event not created", Toast.LENGTH_SHORT).show();
        }

        this.setResult(RESULT_OK);

        data.save();

        this.finish();
        return;
    }

    public void cancel(View v) {
        this.finish();
        return;
    }
}
