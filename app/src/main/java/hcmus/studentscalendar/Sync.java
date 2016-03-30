package hcmus.studentscalendar;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Blue on 12/28/2015.
 */
public class Sync extends AsyncTask<String, String, String> {
    private Data data;

    //Source: http://stackoverflow.com/questions/2642777/trusting-all-certificates-using-httpclient-over-https/6378872#6378872
    public final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    /**
     * Trust every server - dont check for any certificate
     */
    public void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(String... f_url) {
        int count;
        try {
            HttpURLConnection http = null;

            URL url = new URL(f_url[0]);

            if (url.getProtocol().toLowerCase().equals("https")) {


                trustAllHosts();
                HttpsURLConnection urlConnection =
                        (HttpsURLConnection) url.openConnection();
                urlConnection.setHostnameVerifier(DO_NOT_VERIFY);
                http = urlConnection;
            } else {
                http = (HttpURLConnection) url.openConnection();
            }
            // this will be useful so that you can show a tipical 0-100%
            // progress bar
            int lenghtOfFile = http.getContentLength();

            // download the file
            InputStream input = new BufferedInputStream(url.openStream(),
                    8192);

            // Output stream
            OutputStream output = new FileOutputStream(Data.getIcsPath());

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }

        return null;
    }

    public void sync() {
        data = Data.getInstance();
        try {
            execute(data.getIcsUrl()).get();
            String VEvents = Ulitites.StringConverter.getStringFromFile(Data.getIcsPath());

            while (VEvents.indexOf("VEVENT") > 0) {
                String VEvent = VEvents.substring(VEvents.indexOf("BEGIN:VEVENT")
                        , VEvents.indexOf("END:VEVENT") + ("END:VEVENT").length());
                VEvents = VEvents.replace(VEvent, "");
                Assignment assignment = Ulitites.ICSConverter.ConvertFrom(VEvent);
                data.addEvent(assignment);
            }
        }
        catch (Exception e) {

        }
    }
}
