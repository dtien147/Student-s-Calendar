package hcmus.studentscalendar;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

public class Settings extends AppCompatActivity {
    private static class RequestCode {
        public final static int ENABLE_PASSWORD = 0;
        public final static int DISABLE_PASSWORD = 1;
        public final static int CHANGE_PASSWORD = 2;
        public final static int NEW_PASSWORD = 3;
    }

    private CheckBox notificationCB;
    private CheckBox soundCB;
    private CheckBox showNumberOfEventsCB;
    private CheckBox autoSync;
    private CheckBox passwordCB;
    private TextView changePasswordTV;
    private Data settings;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        context = this;

        settings = Data.getInstance();
        notificationCB = (CheckBox) findViewById(R.id.notificationCB);
        notificationCB.setChecked(settings.isCanNotify());
        showNumberOfEventsCB = (CheckBox) findViewById(R.id.showNOECB);
        showNumberOfEventsCB.setChecked(settings.isShowNumberOfEvent());
        soundCB = (CheckBox) findViewById(R.id.soundCB);
        soundCB.setChecked(settings.isCanAlarm());
        autoSync = (CheckBox) findViewById(R.id.autoSync);
        autoSync.setChecked(settings.isAutoSync());
        changePasswordTV = (TextView) findViewById(R.id.changePass);

        if(settings.getIcsUrl().equals("")) {
            TextView showSA = (TextView) findViewById(R.id.showSATV);
            showSA.setEnabled(false);
            showSA.setTextColor(getResources().getColor(R.color.disabled));

            autoSync.setEnabled(false);
            autoSync.setTextColor(getResources().getColor(R.color.disabled));
        }

        passwordCB = (CheckBox) findViewById(R.id.passwordCB);
        if(! settings.getPassword().equals("")) {
            passwordCB.setChecked(true);
            changePasswordTV.setEnabled(true);
            changePasswordTV.setTextColor(Color.BLACK) ;
        }

        passwordCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                if(cb.isChecked()) {
                    Intent intent = new Intent(context, Password.class);
                    startActivityForResult(intent, RequestCode.ENABLE_PASSWORD);
                }
                else {
                    Intent intent = new Intent(context, ConfirmPassword.class);
                    startActivityForResult(intent, RequestCode.DISABLE_PASSWORD);
                }
            }
        });
    }

    public void showSubjectAssignments(View v) {
        Intent intent = new Intent(this, ShowSubjectAssignments.class);
        startActivityForResult(intent, 0);
    }

    public void cancel(View v) {
        settings.setCanNotify(notificationCB.isChecked());
        settings.setCanAlarm(soundCB.isChecked());
        settings.setShowNumberOfEvent(showNumberOfEventsCB.isChecked());
        settings.setAutoSync(autoSync.isChecked());
        settings.save();
        this.setResult(RESULT_OK);
        finish();
    }

    public void deleteAllAssignments(View v) {
        new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
                .setTitle("Delete all assignemnt")
                .setMessage("Are you sure you want to delete all assignment? " +
                        "This action cannot be undo")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        settings.deleteAllAssignments();
                        settings.save();
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

    public void resetData(View v) {
        new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
                .setTitle("Data reset")
                .setMessage("Are you sure you want to reset data? This action cannot be undo")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        settings.reset();
                        restartApplication();
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

    //Source: http://stackoverflow.com/questions/6609414/how-to-programatically-restart-android-app
    private void restartApplication() {
        Intent mStartActivity = new Intent(this, MainActivity.class);
        mStartActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent mPendingIntent = PendingIntent.getActivity(
                this, 0,
                mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 500, mPendingIntent);
        System.exit(0);
    }

    public void changePassword(View v) {
        Intent intent = new Intent(context, ConfirmPassword.class);
        startActivityForResult(intent, RequestCode.CHANGE_PASSWORD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case RequestCode.ENABLE_PASSWORD:
                    changePasswordTV.setEnabled(true);
                    changePasswordTV.setTextColor(Color.BLACK) ;
                    break;
                case RequestCode.DISABLE_PASSWORD:
                    settings.setPassword("");
                    settings.save();
                    changePasswordTV.setEnabled(false);
                    changePasswordTV.setTextColor(getResources().getColor(R.color.disabled));
                    passwordCB.setChecked(false);
                    break;
                case RequestCode.CHANGE_PASSWORD:
                    Intent intent = new Intent(context, Password.class);
                    startActivityForResult(intent, RequestCode.NEW_PASSWORD);
                    break;
            }
        }
        else {
            switch (requestCode) {
                case RequestCode.ENABLE_PASSWORD:
                    passwordCB.setChecked(false);
                    break;
                case RequestCode.DISABLE_PASSWORD:
                    passwordCB.setChecked(true);
                    break;
            }
        }
    }
}
