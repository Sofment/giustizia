package it.giustizia.amministrativa.parser.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by avsupport on 2/11/15.
 */
public class LoggerUtil {

    public static void i(String logMessage) {
        System.out.println(getFormattedTime() + " : " + logMessage);
    }

    private static String getFormattedTime() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss MM/dd/yyyy");
        return simpleDateFormat.format(date);
    }
}
