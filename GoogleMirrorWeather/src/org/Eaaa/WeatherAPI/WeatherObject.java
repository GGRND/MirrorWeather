package org.Eaaa.WeatherAPI;

import com.google.gson.Gson;


public class WeatherObject {

    public static final int TEMPERATURE = 0;
    public static final int WIND_SPEED = 1;
    public static final int WIND_DIRECTION = 2;
    public static final int RAIN = 3;
    public static final int ICON = 4;

    public static final int SIZE = 5;

    private WeatherAPI api;
    private long time;
    private String[] values;

    public WeatherObject(WeatherAPI api) {
        this.values = new String[SIZE];
        this.api = api;
        this.time = -1;
    }

    /**
     * set the Time in milliseconds
     *
     * @param time Long, time in milliseconds
     */
    public void setTime(long time) {
        this.time = time;
    }

    /**
     * Used to get time in milliseconds, returns -1 if time hasn't been set.
     * @return Long, time in milliseconds
     */
    public long getTime() {
        return time;
    }


    /**
     * Used for updating the values of a certain type
     * @param type Int, the type that will be updated
     * @param value String, the value
     */
    public void setString(int type, String value) {
        if (0 <= type && type < SIZE) {
            this.values[type] = value;
        }
    }

    /**
     * Used when the String value is needed of a certain type
     * @param type Int of the desired type
     * @return String
     */
    public String getString(int type) {
        if (0 <= type && type < SIZE) {
            return values[type];
        }
        return null;
    }
    
    


//    /**
//     * Used to update the text on a TextView
//     *
//     * @param type Int of the desired type
//     * @param view TextView that needs to be updated
//     */
//    public void setText(int type, TextView view){
//        if (view != null && 0 <= type && type < SIZE) {
//            view.setText(values[type]);
//        }
//    }
//
//    /**
//     * Used to set the Image on a ImageView
//     *
//     * @param view ImageView that needs to be updated
//     */
//    public void setIcon(ImageView view) {
//        if(view != null) {
//            api.setIcon(view, values[ICON]);
//        }
//    }

}
