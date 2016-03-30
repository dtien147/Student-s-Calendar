package hcmus.studentscalendar;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;

/**
 * Created by Blue on 1/10/2016.
 */
public class Reminder extends BroadcastReceiver{
    private Data setting;

    public void setReminder(Context context, Event event) {
        if(event.getReminderTime() != ReminderTime.NoReminder) {
            Calendar cal = (Calendar) event.getStartDate().clone();
            cal.add(Calendar.SECOND, - ReminderTime.getTimeInSecond(event.getReminderTime()));
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(context, Reminder.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("id", event.getId());
            PendingIntent pi = PendingIntent.getBroadcast(context, event.getId(), i, PendingIntent.FLAG_CANCEL_CURRENT);
            am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        setting = Data.getInstance();
        Bundle extras = intent.getExtras();
        int id = extras.getInt("id");

        Data data = Data.getInstance();
        //Check if event had been deleted or updated reminderTime
        if(data.indexOf(id) != -1 &&
                data.getEvent(data.indexOf(id)).getReminderTime() != ReminderTime.NoReminder) {
            alarm(context);
            notify(context, id);
        }
    }

    private void alarm(Context context) {
        if(setting.isCanAlarm()) {
            Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context, sound);
            r.play();
        }
    }

    private void notify(Context context, int id) {
        Data data = Data.getInstance();
        Event event = data.getEvent(data.indexOf(id));

        Intent i = new Intent(context, EventInfo.class);

        if(event instanceof Assignment) {
            i = new Intent(context, AssignmentInfo.class);
        }

        i.putExtra("id", id);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        if(setting.isCanNotify()) {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(android.R.drawable.ic_menu_info_details)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .setContentTitle(event.getName())
                            .setContentText(Event.dateFormat.format(event.getStartDate().getTime()) +
                                    "\t" +
                                    Event.timeFormat.format(event.getStartDate().getTime()));
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            mNotificationManager.notify(event.getId(), mBuilder.build());
            //context.startActivity(i);
        }
    }

    public static void loadReminder(Context context) {
        Data data = Data.getInstance();
        Reminder reminder = new Reminder();
        for(int i = 0; i < data.size(); i++) {
            reminder.setReminder(context, data.getEvent(i));
        }
    }
}
