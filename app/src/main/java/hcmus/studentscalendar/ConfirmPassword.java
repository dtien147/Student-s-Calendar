package hcmus.studentscalendar;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ConfirmPassword extends AppCompatActivity {
    private String confirmPass;
    private TextView password;
    private String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_password);
        confirmPass = "";
        password = (TextView) findViewById(R.id.password2);
        if(getIntent().hasExtra("password")) {
            Bundle extras = getIntent().getExtras();
            pass = extras.getString("password");
        }
        else {
            pass = Data.getInstance().getPassword();
        }
    }

    public void inputPass(View v) {
        confirmPass += ((TextView) v).getText();
        password.setText(confirmPass);
    }

    public void delete(View v) {
        confirmPass = confirmPass.substring(0, confirmPass.length() - 1);
        password.setText(confirmPass);
    }

    public void cancel(View v) {
        this.setResult(RESULT_CANCELED);
        this.finish();
    }

    public void OK(View v) {
        if(confirmPass.equals(pass)) {
            this.setResult(RESULT_OK);
            this.finish();
        }
        else {
            TextView passStep = (TextView) findViewById(R.id.passStep);
            passStep.setText("Invalid");
            passStep.setTextColor(Color.RED);
            confirmPass = "";
            password.setText(confirmPass);
        }
    }
}
