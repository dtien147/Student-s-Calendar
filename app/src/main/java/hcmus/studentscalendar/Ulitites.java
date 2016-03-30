package hcmus.studentscalendar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Blue on 12/24/2015.
 */
public class Ulitites {
    public static class ICSConverter {
        public static SimpleDateFormat vEventDate = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");

        public static Assignment ConvertFrom(String VEvent) throws ParseException {
            Assignment assignment = new Assignment();

            String name = VEvent.substring(VEvent.indexOf("SUMMARY:")
                            + ("SUMMARY:").length(),
                    VEvent.indexOf("\nDESCRIPTION"));
            String description = VEvent.substring(VEvent.indexOf("DESCRIPTION:")
                    + ("DESCRIPTION:").length(),
                    VEvent.indexOf("\nCLASS"));
            String startDate = VEvent.substring(VEvent.indexOf("DTSTART:")
                            + ("DTSTART:").length(),
                    VEvent.indexOf("\nCATEGORIES"));
            String subject = VEvent.substring(VEvent.indexOf("CATEGORIES:")
                            + ("CATEGORIES:").length(),
                    VEvent.indexOf("\nEND"));
            String uId = VEvent.substring(VEvent.indexOf("UID:") + ("UID:").length(),
                    VEvent.indexOf("SUMMARY:"));
            int id = Integer.parseInt(uId.substring(0, uId.indexOf('@')));

            name = name.replace("\n\t ", "");
            description = description.replace("\n\t ", "");
            name = name.replace("\\n", "\n");
            description = description.replace("\\n", "\n");
            name = name.replace("\\,", ",");
            description = description.replace("\\,", ",");
            subject = subject.replace("amp\\;", "");

            assignment.setName(name);
            assignment.setDescription(description);
            assignment.setAllDay(false);
            assignment.setPriority(Priority.Normal);
            assignment.setEventType(null);
            assignment.setSubject(subject);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(vEventDate.parse(startDate));
            calendar.add(Calendar.HOUR_OF_DAY, 7);
            assignment.setStartDate(calendar);
            assignment.setEndDate(calendar);
            assignment.setId(id);
            assignment.setReminderTime(ReminderTime.NoReminder);

            return assignment;
        }
    }

    public static class StringConverter {
        //http://www.java2s.com/Code/Java/File-Input-Output/ConvertInputStreamtoString.htm

        public static String convertStreamToString(InputStream is) throws Exception {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            return sb.toString();
        }

        public static String getStringFromFile (String filePath) throws Exception {
            File fl = new File(filePath);
            FileInputStream fin = new FileInputStream(fl);
            String ret = convertStreamToString(fin);
            //Make sure you close all streams.
            fin.close();
            return ret;
        }
    }

    public static class Search {
        //-1 = not found
        public static int indexOfStringInStringList(ArrayList<String> arr, String targetValue) {
            for(int i = 0 ; i < arr.size(); i++){
                if(arr.get(i).equals(targetValue)) {
                    return i;
                }
            }
            return -1;
        }
    }
}
