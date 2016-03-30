package hcmus.studentscalendar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Password extends AppCompatActivity {
    private String pass;
    private TextView password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        pass = "";
        password = (TextView) findViewById(R.id.password);
    }

    public void inputPass(View v) {
        pass += ((TextView) v).getText();
        password.setText(pass);
    }

    public void delete(View v) {
        pass = pass.substring(0, pass.length() - 1);
        password.setText(pass);
    }

    public void cancel(View v) {
        this.setResult(RESULT_CANCELED);
        this.finish();
    }

    public void OK(View v) {
        Intent intent = new Intent(this, ConfirmPassword.class);
        intent.putExtra("password", pass);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            Data.getInstance().setPassword(pass);
            Data.getInstance().save();
            this.setResult(RESULT_OK);
            this.finish();
        }
    }
}
