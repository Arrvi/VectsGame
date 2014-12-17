package eu.arrvi.vects.common;

/**
 * Created by Kris on 2014-12-17.
 */
public class SimpleInfo extends CommandParameter {
    public final static String PATTERN = "^.+$";
    private final String info;

    public SimpleInfo(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
    
    public static CommandParameter getFromString(String command) {
        return new SimpleInfo(command);
    }

    @Override
    public String toString() {
        return getInfo();
    }
}
