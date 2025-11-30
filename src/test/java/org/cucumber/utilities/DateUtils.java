package org.cucumber.utilities;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {

    public static String getEpochToDatetime (long epoch){
//        return new Date (epoch).toString();
        return new Date (epoch).toInstant().toString();
    }

    public static String getEpochToDate (long epoch){
//        return new Date (epoch).toString();
        return new Date (epoch).toString();
    }

    public static String getDatetime (String dateStr, String dateFormat) {
        SimpleDateFormat dateParser = new SimpleDateFormat(dateFormat);
        Date date = null;
        try {
            date = dateParser.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        System.out.println(date);
//        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(date));
    }


    public static Date getDate (String dateStr, String dateFormat) {
        SimpleDateFormat dateParser = new SimpleDateFormat(dateFormat);
        Date date = null;
        try {
            date = dateParser.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date getDate2(String dateStr, String dateFormat) {
        SimpleDateFormat dateParser = new SimpleDateFormat(dateFormat);

        // Set the timezone to UTC
        dateParser.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date = null;
        try {
            date = dateParser.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getIsoDateNow () {
//        String dateNowIso = ZonedDateTime.now( ZoneOffset.UTC ).format( DateTimeFormatter.ISO_INSTANT );
        String dateNowIso = ZonedDateTime.now( ZoneOffset.UTC ).format( DateTimeFormatter.ofPattern( "yyyy-MM-dd'T'HH:mm:ss'Z'" ));
//        System.out.println("dateNewIso = " + dateNowIso);
        return dateNowIso;
    }

    public static String getIsoDateNow (String format) {
//        String dateNowIso = ZonedDateTime.now( ZoneOffset.UTC ).format( DateTimeFormatter.ISO_INSTANT );
        String dateNowIso = ZonedDateTime.now( ZoneOffset.UTC ).format( DateTimeFormatter.ofPattern( format ));
//        System.out.println("dateNewIso = " + dateNowIso);
        return dateNowIso;
    }
    public static String getEpochToFormatted (long epoch, String format){

        SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date date = null;
        try {
            date = dateParser.parse(new Date (epoch).toInstant().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        System.out.println(date);
//        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return (new SimpleDateFormat(format).format(date));
//        return (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(date));

    }
    public static String getUIDateEntry (String dateStr, String dateFormat) {
        SimpleDateFormat dateParser = new SimpleDateFormat(dateFormat);
        Date date = null;
        try {
            date = dateParser.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        System.out.println(date);
//        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return (new SimpleDateFormat("MM/dd/yyyy").format(date));
//        return (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(date));
    }

    public static String getFormatedToday (String dateFormat) {
        Date today = new Date();
        return (new SimpleDateFormat(dateFormat).format(today));
    }
    public static String getFormatedDate (Date date, String dateFormat) {
//        Date today = new Date();
        return (new SimpleDateFormat(dateFormat).format(date));
    }

    public static String getNewFormatedDate (String oldFormatDate, String dateFormat) {
        SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date date = null;
        try {
            date = dateParser.parse(oldFormatDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (new SimpleDateFormat(dateFormat).format(date));
    }


    public static String getNewFormatedDate2 (String oldFormatDate, String dateFormat) {
        SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        //"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"


        Date date = null;
        try {
            date = dateParser.parse(oldFormatDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (new SimpleDateFormat(dateFormat).format(date));
    }



    public static String getXdayLater (String dateFormat , int daysToAdd) {
        LocalDate today = LocalDate.now();
        LocalDate tomorrowTime = today.plusDays(daysToAdd);
        String tomorrowDateStr = tomorrowTime.format(DateTimeFormatter.ofPattern(dateFormat));

        return tomorrowDateStr;
    }
    public static String getXdayLater (LocalDate today, String dateFormat , int daysToAdd) {

        LocalDate tomorrowTime = today.plusDays(daysToAdd);
        String tomorrowDateStr = tomorrowTime.format(DateTimeFormatter.ofPattern(dateFormat));

        return tomorrowDateStr;
    }
    public static String getXdayLater (LocalDateTime today, String dateFormat , int daysToAdd) {

        LocalDateTime tomorrowTime = today.plusDays(daysToAdd);
        String tomorrowDateStr = tomorrowTime.format(DateTimeFormatter.ofPattern(dateFormat));

        return tomorrowDateStr;
    }

}
