package com.example.kyle.joulieapp.Utils;

/**
 * Created by Kyle on 2017-03-24.
 */

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Yasir on 02/06/16.
 */
public class DateAxisValueFormatter implements IAxisValueFormatter
{

    public static final int DAY = 0;
    public static final int WEEK = 1;
    public static final int MONTH = 2;
    public static final int YEAR = 3;
    public static final int MAX = 4;

    private final String[] daysOfWeek = {"SUN", "MON", "TUES", "WED", "THURS", "FRI", "SAT"};
    private final String[] monthsOfYear = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};

    private int mDateType;

   // private long[] mReferenceTimestamps; // minimum timestamp in your data set
    private DateFormat mDataFormat;
    private Date mDate;
    private Calendar cal = Calendar.getInstance();

    public DateAxisValueFormatter(int dateType) {
        this.mDateType = dateType;
       // this.mReferenceTimestamps = referenceTimestamps;
        this.mDataFormat = new SimpleDateFormat("HH:mm");
        this.mDate = new Date();
    }

    /**
     * Called when a value from an axis is to be formatted
     * before being drawn. For performance reasons, avoid excessive calculations
     * and memory allocations inside this method.
     *
     * @param value the value to be formatted
     * @param axis  the axis the value belongs to
     * @return
     */
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        // convertedTimestamp = originalTimestamp - referenceTimestamp
        long originalTimestamp = (long) value;

        // Retrieve original timestamp
        long convertedTimestamp = originalTimestamp * 1000;

        // Convert timestamp to hour:minute
        switch(mDateType){
            case DAY:
                return getHour(convertedTimestamp);
            case WEEK:
                return getDayOfWeek(convertedTimestamp);
            case MONTH:
                return getDayOfMonth(convertedTimestamp);
            case YEAR:
                return getMonth(convertedTimestamp);
            default:
                return null;



        }

    }

    private String getHour(long timestamp){
        try{

            cal = Calendar.getInstance();
            cal.setTimeInMillis(timestamp);
            cal.get(Calendar.HOUR_OF_DAY);
           // Date d = new Date(timestamp*1000);
            //Log.d("HOURS",String.valueOf(d.getHours()));
            //Log.d("TIME BEFORE", String.valueOf(d.getTime()));
            //String time = this.mDataFormat.format(d.getTime());
            //String calTime = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
            //Log.d("TIME", time);
            return this.mDataFormat.format(cal.getTime());
        }
        catch(Exception ex){
            return "xx";
        }
    }

    private String getDayOfWeek(long timestamp){
        try{
            cal = Calendar.getInstance();
            cal.setTimeInMillis(timestamp);
            String mDay = daysOfWeek[cal.get(Calendar.DAY_OF_WEEK) - 1];
            return mDay;
        }
        catch(Exception ex){
            return "xx";
        }
    }

    private String getDayOfMonth(long timestamp) {
        try {
            cal = Calendar.getInstance();
            cal.setTimeInMillis(timestamp);
            String mDay = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
            String mDate = new SimpleDateFormat("dd/MMM").format(cal.getTime());
            return mDate;
        } catch (Exception ex) {
            return "xx";
        }
    }

    private String getMonth(long timestamp){
        try {
            cal = Calendar.getInstance();
            cal.setTimeInMillis(timestamp);
          //  String mMonth = daysOfWeek[]
            String mDate = new SimpleDateFormat("MMM").format(cal.getTime());
            return mDate;
        } catch (Exception ex) {
            return "xx";
        }
    }


}
