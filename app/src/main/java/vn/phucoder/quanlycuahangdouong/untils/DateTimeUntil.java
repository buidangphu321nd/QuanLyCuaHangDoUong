package vn.phucoder.quanlycuahangdouong.untils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeUntil {
    public static final String DEFAULT_FORMAT_DATE = "dd/MM/yyyy";
    public static String convertDateToTimeStamp(String strDate) {
        String result = "";
        if (strDate != null) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(DEFAULT_FORMAT_DATE, Locale.ENGLISH);
                Date date = format.parse(strDate);
                if (date != null) {
                    Long timestamp = date.getTime() / 1000;
                    result = String.valueOf(timestamp);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static String convertTimeStampToDate(String strTimeStamp) {
        String result = "";
        if (strTimeStamp != null) {
            try {
                float floatTimestamp = Float.parseFloat(strTimeStamp);
                long timestamp = (long) (floatTimestamp * 1000);
                SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMAT_DATE, Locale.ENGLISH);
                Date date = (new Date(timestamp));
                result = sdf.format(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
