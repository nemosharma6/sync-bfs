import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

    public static String logFile;
    public static FileWriter fileWriter;
    private static DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");

    private static String time() {
        Date d = new Date();
        return dateTimeFormat.format(d);
    }

    public static void init(String logFile) {
        Log.logFile = logFile;
        try {
            fileWriter = new FileWriter(logFile);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void write(String node, String msg){
        try {
            fileWriter.write(time() + " - " + node + " : " + msg + "\n");
            fileWriter.flush();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

}
