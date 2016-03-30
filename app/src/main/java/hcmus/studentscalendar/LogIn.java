package hcmus.studentscalendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.SslError;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LogIn extends AppCompatActivity {
    private EditText userET;
    private EditText passET;
    private boolean logged;
    private AlertDialog dialog;
    private Data data;

    private class MyJavaScriptInterface {

        private Context ctx;

        MyJavaScriptInterface(Context ctx) {
            this.ctx = ctx;
        }

        @JavascriptInterface
         public void getIcsUrl(String html) {
            String icsUrl;

            String userid = html.substring(html.indexOf("name=\"userid\" value=\"") +
                            ("name=\"userid\" value=\"").length(),
                    html.indexOf("\"><input type=\"hidden\" name=\"authtoken\""));
            String authtoken = html.substring(html.indexOf("name=\"authtoken\" value=\"") +
                            ("name=\"authtoken\" value=\"").length(),
                    html.indexOf("\"><input type=\"submit\" name=\"generateurl\""));
            icsUrl = "https://courses.fit.hcmus.edu.vn/calendar/export_execute.php?userid=" +
                    userid +
                    "&authtoken=" +
                    authtoken +
                    "&preset_what=all&preset_time=recentupcoming";
            data.setIcsUrl(icsUrl);
            data.save();

            Toast.makeText(ctx, "Log in completed", Toast.LENGTH_SHORT);

            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        CookieManager.getInstance().removeAllCookie();
        data = Data.getInstance();
        data.setIcsUrl("");
        data.save();
        logged = false;

        dialog = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
                .setTitle("Log in")
                .setMessage("Please wait")
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_info).create();

        final WebView myWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.addJavascriptInterface(new MyJavaScriptInterface(this), "HtmlViewer");

        userET = (EditText) findViewById(R.id.userET);
        passET = (EditText) findViewById(R.id.passET);

        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (url.contains("https://courses.fit.hcmus.edu.vn/calendar/export.php")) {
                    view.loadUrl("javascript:window.HtmlViewer.getIcsUrl" +
                            "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                } else {
                    if(! logged) {
                        view.loadUrl("javascript: {" +
                                "document.getElementById('username').value = '" +
                                userET.getText().toString() + "';" +
                                "document.getElementById('password').value = '" +
                                passET.getText().toString() + "';" +
                                "document.getElementById('loginbtn').click();" +
                                "};");
                        logged = true;
                    }
                    else {
                        dialog.dismiss();
                        TextView invalidLogin = (TextView) findViewById(R.id.invalidLogin);
                        invalidLogin.setVisibility(View.VISIBLE);
                        logged = false;
                    }
                }
            }

        });

    }

    public void logIn(View v) {
        if(isNetworkAvailable()) {
            dialog.show();
            final WebView myWebView = (WebView) findViewById(R.id.webview);
            myWebView.loadUrl("https://courses.fit.hcmus.edu.vn/calendar/export.php");
        }
        else {
           new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
                   .setTitle("Network problem")
                   .setMessage("Can't connect to internet, check your network connection.")
                   .setIcon(android.R.drawable.ic_dialog_alert)
                   .setNegativeButton("OK", null)
                   .show();
        }
    }

    public void later(View v) {
        this.setResult(RESULT_CANCELED);
        this.finish();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
