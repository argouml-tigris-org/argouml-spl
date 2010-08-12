package foo;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Log {
	 /** Mascara com a formatação de data. */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
    /**
     * @return a data atual formatada de acordo com o {@link #DATE_FORMAT}
     */
    private static String getDate() {
        return "[" + Log.DATE_FORMAT.format(Calendar.getInstance().getTime()) + "] ";
    }
    
	public static void info(String msg) {
		System.out.println(Log.getDate() + "[INFO] " + msg);
	}
	
	public static void debug(String msg) {
		//System.out.println(Log.getDate() + "[DEBUG] " +  msg);
	}
	
	public static void error(String msg) {
		System.err.println(Log.getDate() + "[ERROR] " +  msg);
	}		
}
