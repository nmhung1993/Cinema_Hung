package base;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tran Huu Tam on 7/10/2016.
 */
public class DateTimeUtility {

    private static final List<String> timesStringViet = Arrays.asList("năm", "tháng", "ngày", "giờ", "phút", "giây");

    public static String formatDateStart(long milliseconds) {
        return new SimpleDateFormat("dd/MM").format(milliseconds);
    }

    public static String formatDateToDayMonth(long milliseconds) {
        return new SimpleDateFormat("dd MMMM").format(milliseconds);
    }

    public static String formatDateToDayMonthYear(long milliseconds) {
        return new SimpleDateFormat("dd/MM/yyyy").format(milliseconds);
    }

    public static String formatFullTime(long milliseconds) {
        return new SimpleDateFormat("HH:mm").format(milliseconds);
    }

    public static String formatEvent(long startMilliseconds, long endMilliseconds) {
        String timeStart = new SimpleDateFormat("hh:mma").format(startMilliseconds);
        String timeEnd = new SimpleDateFormat("hh:mma").format(endMilliseconds);
        String time = timeStart + " - " + timeEnd;
        return time;
    }

    public static String formatFullEventTime(long startMilliseconds, long endMilliseconds) {
        String dateStart = new SimpleDateFormat("EEEE, dd MMMM, yyyy").format(startMilliseconds);
        String dateEnd = new SimpleDateFormat("EEEE, dd MMMM, yyyy").format(endMilliseconds);
        String timeStart = new SimpleDateFormat("hh:mma").format(startMilliseconds);
        String timeEnd = new SimpleDateFormat("hh:mma").format(endMilliseconds);
        if (dateStart.equalsIgnoreCase(dateEnd)) {
            return dateStart + " " + timeStart + " - " + timeEnd;
        } else {
            return dateStart + " - " + dateEnd + " " + timeStart + " - " + timeEnd;
        }
    }

    public static String format(long milliseconds) {
        return new SimpleDateFormat("dd/MM/yyyy hh:mma").format(milliseconds);
    }

    public static String formatDayofWeek(long milliseconds) {
        return new SimpleDateFormat("EEEE").format(milliseconds);
    }

    public static String formatTime(long milliseconds) {
        return new SimpleDateFormat("hh:mma").format(milliseconds);
    }

    public static String getHour(long milliseconds) {
        return new SimpleDateFormat("HH").format(milliseconds);
    }

    public static String getMinute(long milliseconds) {
        return new SimpleDateFormat("mm").format(milliseconds);
    }

    public static String getDay(long milliseconds) {
        return new SimpleDateFormat("dd").format(milliseconds);
    }

    public static String getMonth(long milliseconds) {
        return new SimpleDateFormat("M").format(milliseconds);
    }

    public static String getYear(long milliseconds) {
        return new SimpleDateFormat("yyyy").format(milliseconds);
    }

    public static String formatDateToMonth(long milliseconds) {
        return new SimpleDateFormat("MMMM").format(milliseconds);
    }

    public static String formatDate(long milliseconds) {
        return new SimpleDateFormat("dd/MM/yyyy").format(milliseconds);
    }

    public static String formatDateEvent(long timeStart, long timeEnd, long date) {
        String start = new SimpleDateFormat(" hh:mma ").format(timeStart);
        String end = new SimpleDateFormat(" hh:mma ").format(timeEnd);
        String dateStart = new SimpleDateFormat("dd/MM").format(timeStart);
        String dateEnd = new SimpleDateFormat("dd/MM").format(timeEnd);
        if (dateStart.equalsIgnoreCase(dateEnd)) {
            return dateStart + " " + start + "-" + end;
        } else {
            dateStart = new SimpleDateFormat("dd/MM").format(timeStart);
            dateEnd = new SimpleDateFormat("dd/MM").format(timeEnd);
            return dateStart + "-" + dateEnd + " " + start + "-" + end;
        }
    }

    /**
     * Checking two timestamp to know they are in one day or not
     *
     * @param time1 timestamp 1
     * @param time2 timestamp 2
     * @return True if they are in one day
     */
    public static boolean isOneDay(long time1, long time2) {
        return formatDateToDayMonthYear(time1).equalsIgnoreCase(formatDateToDayMonthYear(time2));
    }

    public static long getTimeStartOfDay(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis); // compute start of the day for the timestamp
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public static long getTimeEndOfDay(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis); // compute start of the day for the timestamp
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTimeInMillis();
    }


    public static final List<Long> times = Arrays.asList(
            TimeUnit.DAYS.toMillis(365),
            TimeUnit.DAYS.toMillis(30),
            TimeUnit.DAYS.toMillis(1),
            TimeUnit.HOURS.toMillis(1),
            TimeUnit.MINUTES.toMillis(1),
            TimeUnit.SECONDS.toMillis(1));

    /**
     * Here is the place format time
     *
     * @param dateFrom the Past of time want to know where (millis)
     * @return format like 1 day ago, 1 hour ago, 20:00 (in current day) or full display time
     */
    public static String formatTime(Long dateFrom) {
        return DateTimeUtility.toDuration(System.currentTimeMillis(), dateFrom, Locale.getDefault().getDisplayLanguage());
    }

    private static String toDuration(long now, long time, String language) {
        StringBuilder res = new StringBuilder();
        long sevenDay = 604800000;

        // Display the day if that day longer than 7 days
        if (now - time > sevenDay) {
            return formatDate(time);
        }

        for (int i = 0; i < times.size(); i++) {
            Long current = times.get(i);
            long temp = (now - time) / current;
            if (temp > 0) {
                res.append(temp).append(" ").append(timesStringViet.get(i)).append(temp > 1 ? "" : "")
                        .append(" trước");
                break;
            }
        }
        if ("".equals(res.toString()))
            return "Vừa xong";
        else {
            String s = res.toString().toLowerCase();
            if (s.equalsIgnoreCase("1 ngày trước"))
                return "hôm qua";
            return res.toString();
        }
    }

    public static boolean isDateInCurrentWeek(long date) {
        Date date1 = new Date(date);
        return isDateInCurrentWeek(date1);
    }

    public static boolean isTheSameDay(long day1, long day2){
        return getTimeStartOfDay(day1) == getTimeStartOfDay(day2);
    }
    private static boolean isDateInCurrentWeek(Date date) {
        Calendar currentCalendar = Calendar.getInstance();
        int week = currentCalendar.get(Calendar.WEEK_OF_YEAR);
        int year = currentCalendar.get(Calendar.YEAR);
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTime(date);
        int targetWeek = targetCalendar.get(Calendar.WEEK_OF_YEAR);
        int targetYear = targetCalendar.get(Calendar.YEAR);
        return week == targetWeek && year == targetYear;
    }
}
