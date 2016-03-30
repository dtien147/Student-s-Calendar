package hcmus.studentscalendar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    static class RequestCode {
        public final static int NEW_EVENT = 0;
        public final static int EVENT_INFO = 1;
        public final static int SETTINGS = 2;
        public final static int ASSIGNMENT_INFO = 3;
        public final static int LOG_IN = 4;
        public final static int CONFIRM_PASSWORD = 5;
    }

    public CalendarAdapter calendarAdapter;
    public EventAdapter eventAdapter;
    public static SimpleDateFormat monthFormat = new SimpleDateFormat("MMM yyyy");
    private Data data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Data.setFilePath(getFilesDir().getPath() + "/" + "Data.xml");
        Data.setIcsPath(getFilesDir().getPath() + "/" + "Moodle.ics");
        data = Data.getInstance();

        if(data.getPassword().equals("")) {
            load();
        }
        else {
            Intent intent = new Intent(this, ConfirmPassword.class);
            startActivityForResult(intent, RequestCode.CONFIRM_PASSWORD);
        }
    }

    private void load() {
        loadCalendar();

        //Remind log in and sync if not
        if(data.isAutoSync()) {
            sync();
        }
        Reminder.loadReminder(this);
    }

    public void notificationNewEvent() {
        /*
        int mId = 1000;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.icon)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setContentTitle("New assignment")
                        .setContentText("You have " +
                                data.getNumberOfNewAssignment() +
                                " new assignment");
        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(mId, mBuilder.build());
        */

        new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
                .setTitle("New assignments")
                .setMessage("You have " +
                        data.getNumberOfNewAssignment() +
                        " new assignment")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
        data.setNumberOfNewAssignment(0);
    }

    public void sync() {
        if (data.getIcsUrl().equals("")) {
            showLogIn();
        } else {
            try {
                new Sync().sync();

                if(data.getNumberOfNewAssignment() > 0) {
                    notificationNewEvent();
                }

            } catch (Exception ignored) {

            }

            eventAdapter.refresh();
            calendarAdapter.notifyDataSetChanged();

            Toast.makeText(this, "Sync completed", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshMonthDisplay() {
        TextView monthTV = (TextView) findViewById(R.id.monthText);
        Date month = calendarAdapter.getDisplayMonth();
        monthTV.setText(monthFormat.format(month).toString());
    }

    private void loadCalendar() {
        Calendar monthCalendar = Calendar.getInstance();
        calendarAdapter = new CalendarAdapter(this, monthCalendar);
        refreshMonthDisplay();

        GridView calendarView = (GridView) findViewById(R.id.calendarView);
        calendarView.setAdapter(calendarAdapter);

        eventAdapter = new EventAdapter(this);
        ListView eventsView;
        eventsView = (ListView) findViewById(R.id.eventsView);
        eventsView.setAdapter(eventAdapter);
        eventsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                viewEvent(eventAdapter.getEventPosition(position));
            }
        });

        calendarView.setOnTouchListener(new MonthSwipeTouchListener(this, calendarView,
                calendarAdapter) {
            public void onSwipeRight() {
                calendarAdapter.addMonth(-1);
                refreshMonthDisplay();
            }

            public void onSwipeLeft() {
                calendarAdapter.addMonth(1);
                refreshMonthDisplay();
            }

            public void onClick() {
                eventAdapter.refresh();
            }
        });
    }

    public void addEvent(View v) {
        Intent intent = new Intent(this, AddEvent.class);
        startActivityForResult(intent, RequestCode.NEW_EVENT);
    }

    public void viewEvent(int position) {
        if (data.getEvent(position) instanceof Assignment) {
            Intent intent = new Intent(this, AssignmentInfo.class);
            intent.putExtra("position", position);
            startActivityForResult(intent, RequestCode.ASSIGNMENT_INFO);
        } else {
            Intent intent = new Intent(this, EventInfo.class);
            intent.putExtra("position", position);
            startActivityForResult(intent, RequestCode.EVENT_INFO);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Refresh Events
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RequestCode.LOG_IN:
                    sync();
                    eventAdapter.refresh();
                    calendarAdapter.notifyDataSetChanged();
                    break;
                case RequestCode.CONFIRM_PASSWORD:
                    load();
                    break;
                default:
                    eventAdapter.refresh();
                    calendarAdapter.notifyDataSetChanged();
                    break;
            }
        }
        else {
            if(requestCode == RequestCode.CONFIRM_PASSWORD) {
                System.exit(0);
            }
        }
    }

    public void showSettings() {
        Intent intent = new Intent(this, Settings.class);
        startActivityForResult(intent, RequestCode.SETTINGS);
    }

    public void showPopUpMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.actions, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.settings:
                        showSettings();
                        return true;
                    case R.id.sync:
                        sync();
                        return true;
                    case R.id.search:
                        search();
                        return true;
                    case R.id.account:
                        LogIn();
                        return true;
                    case R.id.sortByTime:
                        data.sortByTime();
                        eventAdapter.refresh();
                        return true;
                    case R.id.sortByPriority:
                        data.sortByPriority();
                        eventAdapter.refresh();
                        return true;
                    default:
                        return false;
                }
            }
        });

        popup.show();
    }

    private void LogIn() {
        //Logged
        if(! data.getIcsUrl().equals("")) {
            new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
                    .setTitle("Log out")
                    .setMessage("You are logged. Are you sure you want to log out?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            showLogIn();
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
        else {
            showLogIn();
        }
    }

    private void showLogIn() {
        data.deleteAllAssignments();
        eventAdapter.refresh();
        calendarAdapter.refreshDays();
        Intent intent = new Intent(this, LogIn.class);
        startActivityForResult(intent, RequestCode.LOG_IN);
    }

    private void search() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);

        alert.setTitle("Search");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        input.setHint("Event name");
        input.setTextColor(getResources().getColor(R.color.calendarViewBackground));
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                eventAdapter.search(input.getText().toString());
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    public void showToday(View v) {
        calendarAdapter.showToday();
        refreshMonthDisplay();
        eventAdapter.refresh();
    }

    public void showUpcoming(View v) {
        calendarAdapter.cancelSelection();
        eventAdapter.refresh();
    }
}
