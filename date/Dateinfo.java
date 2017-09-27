package date;

// Huiliang & Yingjing on 9/26/2017

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class Dateinfo {
  public static String getCurrentTimeGMT() {
    Date date = new Date();
    return Dateinfo.timeGMTFormat(date);
  }

  public static String timeGMTFormat(Date date) {
    DateFormat dateFormat = new SimpleDateFormat("EEE. dd MMM yyyy HH:mm:ss z");
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    return dateFormat.format(date);
  }

  public static Date stringToDate(String dateString) throws Exception {
    DateFormat dateformat = new SimpleDateFormat("EEE. dd MMM yyyy HH:mm:ss z");
    dateformat.setTimeZone(TimeZone.getTimeZone("GMT"));
    return dateformat.parse(dateString);
  }


  public static String fileLastModified(File file) {
    long lastModifiedLong = file.lastModified();
    Date lastModifedDate = new Date(lastModifiedLong);
    return Dateinfo.timeGMTFormat(lastModifedDate);
  }

    /*  Testing
     
    public static void main(String[] args) {
        try {
     
            File file = new File("/users/huilianghuang/desktop/data.txt");
            System.out.println(DateUtil.fileLastModified(file));
            System.out.println(new Date(file.lastModified()));
            
            //      Date cuurent = new Date();
            //      Date currentDateGMT = DateUtil.stringToDate(DateUtil.getCurrentTimeGMT());
            //      System.out.println(currentDateGMT.toString());
            //      System.out.println(cuurent.toString());
            //      System.out.println(DateUtil.getCurrentTimeGMT());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     */
}
