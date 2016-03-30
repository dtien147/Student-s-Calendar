package hcmus.studentscalendar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class ShowSubjectAssignments extends AppCompatActivity {
    private CheckBox all;
    private Data data;
    private boolean hideOneSubject = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_subject_assignments);
        data = Data.getInstance();
        all = (CheckBox) findViewById(R.id.all);
        getSubject();

        all.setChecked(data.isShowAll());
        all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LinearLayout layout = (LinearLayout)findViewById(R.id.subjectsLayout);
                //layout.child(0) = AllCheckBox
                if(isChecked == false && hideOneSubject) {
                    return;
                }

                for(int i = 1 ; i < layout.getChildCount(); i++) {
                    LinearLayout v = (LinearLayout) layout.getChildAt(i);
                    CheckBox subject = (CheckBox) v.getChildAt(0);
                    subject.setChecked(isChecked);
                    hideOneSubject = false;
                }
            }
        });
    }

    public void cancel(View v) {
        finish();
    }

    public void getSubject() {
        LinearLayout layout = (LinearLayout)findViewById(R.id.subjectsLayout);
        layout.removeAllViews();
        layout.addView(all);
        ArrayList<String> subjects = new ArrayList<>();
        for(Event event : data.getEvents())
            if (event instanceof Assignment) {
                Assignment assignment = (Assignment) event;
                String subject = assignment.getSubject();
                if (Ulitites.Search.indexOfStringInStringList(subjects, subject) < 0) {
                    subjects.add(subject);
                    createCheckBox(subject);
                }
            }
    }

    public void createCheckBox(String subjectName) {
        View v = LayoutInflater.from(this).inflate(R.layout.default_cb, null);
        LinearLayout layout = (LinearLayout)findViewById(R.id.subjectsLayout);
        CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkBox);
        if(! data.isHidden(subjectName)) {
            checkBox.setChecked(true);
        }
        checkBox.setText(subjectName);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String subject = buttonView.getText().toString();
                if (isChecked == true) {
                    data.showSubject(subject);
                    LinearLayout layout = (LinearLayout)findViewById(R.id.subjectsLayout);
                    int i = 1;
                    //layout.child(0) = AllCheckBox
                    for(i = 1 ; i < layout.getChildCount(); i++) {
                        LinearLayout v = (LinearLayout) layout.getChildAt(i);
                        CheckBox subjectCB = (CheckBox) v.getChildAt(0);
                        if(! subjectCB.isChecked()){
                            break;
                        }
                    }
                    if(i == layout.getChildCount()) {
                        all.setChecked(true);
                    }
                }
                else {
                    data.hideSubject(subject);
                    hideOneSubject = true;
                    all.setChecked(false);
                }
            }
        });
        layout.addView(v);
    }
}
