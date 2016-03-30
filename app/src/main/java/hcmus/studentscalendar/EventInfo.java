package hcmus.studentscalendar;

import java.util.Calendar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class EventInfo extends AppCompatActivity {
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
    private ImageView eventIcon;
    private CheckBox allDay;
    private Spinner reminderSpinner;
    private int position;
    private Data data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_info);
        data = Data.getInstance();

		startDateTV = (EditText) findViewById(R.id.eventInfoStartDate);
		endDateTV = (EditText) findViewById(R.id.eventInfoEndDate);
		startTimeTV = (EditText) findViewById(R.id.eventInfoStartTime);
		endTimeTV = (EditText) findViewById(R.id.eventInfoEndTime);
		eventNameTV = (EditText) findViewById(R.id.eventInfoName);
		eventDescriptionTV = (EditText) findViewById(R.id.eventInfoDescription);

        eventType = (Spinner) findViewById(R.id.eventInfoType);
        eventType.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item,
                EventType.values()));
        eventIcon = (ImageView) findViewById(R.id.eventInfoIcon);

		priority = (Spinner) findViewById(R.id.eventInfoPriority);
		priority.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item, Priority.values()));

        reminderSpinner = (Spinner) findViewById(R.id.infoReminderSpinner);
        reminderSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item, ReminderTime.values()));

        allDay = (CheckBox) findViewById(R.id.eventInfoAllDay);
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

        loadEventInfo();

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
	}

	public void loadEventInfo() {
        Bundle extras = getIntent().getExtras();
        if(getIntent().hasExtra("position")) {
            position = extras.getInt("position");
        }
        else {
            int id = extras.getInt("id");
            position = data.indexOf(id);
        }

		Event event = data.getEvent(position);
		startDate = event.getStartDate();
		endDate = event.getEndDate();

		eventNameTV.setText(event.getName());
		eventDescriptionTV.setText(event.getDescription());
		startDateTV.setText(Event.dateFormat.format(startDate.getTime()));
		endDateTV.setText(Event.dateFormat.format(endDate.getTime()));
		startTimeTV.setText(Event.timeFormat.format(startDate.getTime()));
		endTimeTV.setText(Event.timeFormat.format(endDate.getTime()));
        eventType.setSelection(((ArrayAdapter) eventType.getAdapter()).
                getPosition(event.getEventType()));
        priority.setSelection(((ArrayAdapter) priority.getAdapter())
                .getPosition(event.getPriority()));
        allDay.setChecked(event.isAllDay());
        reminderSpinner.setSelection(((ArrayAdapter) reminderSpinner.getAdapter())
                .getPosition(event.getReminderTime()));
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
                EventInfo.this,
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
                EventInfo.this,
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
                EventInfo.this,
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
                EventInfo.this,
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

	public void save(View v) {
		Event event = data.getEvent(position);
		event.setName(eventNameTV.getText().toString());
		event.setDescription(eventDescriptionTV.getText().toString());
		event.setStartDate(startDate);
		event.setEndDate(endDate);
        event.setEventType(EventType.valueFor(eventType.getSelectedItem().toString()));
        event.setPriority(Priority.valueOf(priority.getSelectedItem().toString()));
        event.setAllDay(allDay.isChecked());
        event.setReminderTime(ReminderTime.valueFor(reminderSpinner.getSelectedItem().toString()));

        new Reminder().setReminder(this, event);

        data.save();

        Toast.makeText(this, "Event updated", Toast.LENGTH_SHORT).show();
        this.setResult(RESULT_OK);
        this.finish();
    }

	public void cancel(View v) {
		this.finish();
	}

    private void delete() {
        data.removeEvent(position);
        data.save();
        Toast.makeText(this, "Event deleted", Toast.LENGTH_SHORT).show();
        this.setResult(RESULT_OK);
        this.finish();
    }

	public void delete(View v) {
        new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
                .setTitle("Event delete")
                .setMessage("Are you sure you want to delete event? This action cannot be undo")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        delete();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
	}

}
