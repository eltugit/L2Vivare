package gr.sr.debug;



import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Base64;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;


public class LogFormatter
        extends Formatter {
    public final OutputHandler outputHandler;

    public LogFormatter(OutputHandler guilogoutputhandler) {
        this.outputHandler = guilogoutputhandler;
    }

    public String format(LogRecord logrecord) {
        StringBuilder stringbuilder = new StringBuilder();
        Level level = logrecord.getLevel();
        stringbuilder.append("[" + level.getLocalizedName().toUpperCase() + "] ");

        stringbuilder.append(logrecord.getMessage());
        stringbuilder.append('\n');
        Throwable throwable = logrecord.getThrown();

        if (throwable != null) {

            StringWriter stringwriter = new StringWriter();
            throwable.printStackTrace(new PrintWriter(stringwriter));
            stringbuilder.append(stringwriter.toString());
        }

        return stringbuilder.toString();
    }

    public static String getText(String toDecode) {
        return new String(Base64.getDecoder().decode(toDecode));
    }
}
