package hcmus.studentscalendar;

import java.util.Calendar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

public class AssignmentInfo extends AppCompatActivity {
    private EditText startDateTV;
    private EditText endDateTV;
    private EditText startTimeTV;
    private EditText endTimeTV;
    private EditText eventNameTV;
    private EditText eventDescriptionTV;
    private Spinner priority;
    private EditText subject;
    private int position;
    private Spinner reminderSpinner;
    private Data data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_info);
        data = Data.getInstance();

        startDateTV = (EditText) findViewById(R.id.assignmentInfoStartDate);
        endDateTV = (EditText) findViewById(R.id.assignmentInfoEndDate);
        startTimeTV = (EditText) findViewById(R.id.assignmentInfoStartTime);
        endTimeTV = (EditText) findViewById(R.id.assignmentInfoEndTime);
        eventNameTV = (EditText) findViewById(R.id.assignmentInfoName);
        eventDescriptionTV = (EditText) findViewById(R.id.assignmentInfoDescription);
        subject = (EditText) findViewById(R.id.assignmentSubject);

        ImageView eventIcon = (ImageView) findViewById(R.id.assignmentInfoIcon);
        eventIcon.setImageResource(EventType.eventIcon(null));

        priority = (Spinner) findViewById(R.id.assignmentInfoPriority);
        priority.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item, Priority.values()));

        reminderSpinner = (Spinner) findViewById(R.id.assignReminderSpinner);
        reminderSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item, ReminderTime.values()));

        loadAssignmentInfo();
    }

    public void loadAssignmentInfo() {
        Bundle extras = getIntent().getExtras();
        if(getIntent().hasExtra("position")) {
            position = extras.getInt("position");
        }
        else {
            int id = extras.getInt("id");
            position = data.indexOf(id);
        }

        Assignment assignment = (Assignment) data.getEvent(position);
        Calendar startDate = assignment.getStartDate();
        Calendar endDate = assignment.getEndDate();

        eventNameTV.setText(assignment.getName());
        eventDescriptionTV.setText(assignment.getDescription());
        startDateTV.setText(Event.dateFormat.format(startDate.getTime()));
        endDateTV.setText(Event.dateFormat.format(endDate.getTime()));
        startTimeTV.setText(Event.timeFormat.format(startDate.getTime()));
        endTimeTV.setText(Event.timeFormat.format(endDate.getTime()));
        priority.setSelection(((ArrayAdapter) priority.getAdapter())
                .getPosition(assignment.getPriority()));
        reminderSpinner.setSelection(((ArrayAdapter) reminderSpinner.getAdapter())
                .getPosition(assignment.getReminderTime()));
        subject.setText(assignment.getSubject());
    }

    public void save(View v) {
        Event event = data.getEvent(position);
        event.setPriority(Priority.valueOf(priority.getSelectedItem().toString()));
        event.setReminderTime(ReminderTime.valueFor(reminderSpinner.getSelectedItem().toString()));

        new Reminder().setReminder(this, event);

        data.save();

        Toast.makeText(this, "Assignment updated", Toast.LENGTH_SHORT).show();
        this.setResult(RESULT_OK);
        this.finish();
    }

    public void cancel(View v) {
        this.finish();
    }
}
