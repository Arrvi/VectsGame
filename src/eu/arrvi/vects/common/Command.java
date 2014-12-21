package eu.arrvi.vects.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Vects protocol command. It consists of command name and list of parameters.
 */
public class Command {
    public final static int TARGET_BROADCAST = -1;

    private final int target;
    private final String name;
    private List<CommandParameter> params;

    public Command() {
        this("NOP");
    }

    public Command(String name) {
        this(TARGET_BROADCAST, name, new ArrayList<CommandParameter>());
    }

    public Command(int target, String name) {
        this(target, name, new ArrayList<CommandParameter>());
    }

    public Command(int target, String name, CommandParameter param) {
        this.target = target;
        this.name = name;
        this.params = new ArrayList<>(2);
        this.params.add(param);
    }

    public Command(int target, String name, List<CommandParameter> params) {
        this.target = target;
        this.name = name.toUpperCase();
        this.params = params;
    }

    public Command(String name, CommandParameter parameter) {
        this(Command.TARGET_BROADCAST, name, parameter);
    }

    public String getName() {
        return name;
    }

    public List<CommandParameter> getParams() {
        return params;
    }
    public CommandParameter getFirstParam() {
        if ( params.size() == 0 ) 
            return null;
        return params.get(0);
    }

    public void setParams(List<CommandParameter> params) {
        this.params = params;
    }
    
    public void addParam(CommandParameter param) {
        params.add(param);
    }

    public int getTarget() {
        return target;
    }

    public String getCommandString() {
        StringBuilder build = new StringBuilder(name);
        
        if (params.size() > 0) {
            build.append(' ');
            for (Object param : params) {
                build.append(param.toString());
                build.append('|');
            }
            build.deleteCharAt(build.length()-1); // delete last separator
        }
        
        return build.toString();
    }

    @Override
    public String toString() {
        return getCommandString();
    }

    public static Command getCommandFromString(String command) {
        // Command consists of at least 3 letter name
        if ( command.length() < 3 ) return null;
        
        String name = command.substring(0, 3);
        Set<Object> params = new HashSet<>();
        
        if ( command.length() > 4 ) {
            command = command.substring(4);
            String[] paramStrings = command.split("\\|");
            for (String paramString : paramStrings) {
                params.add(parseParam(paramString));
            }
        }
        
        return null;
    }

    @SuppressWarnings("unchecked")
    private static Object parseParam(String paramString) {
        final String[] classNames = {"GameInfo", "VehiclePosition", "ChatMessage", "TrackPoint", "SimpleInfo"};

        for (String className : classNames) {
            try {
                Class<? extends CommandParameter> cls = 
                        (Class<? extends CommandParameter>) Class.forName("eu.arrvi.vects.common."+className);

                if (paramString.matches((String) cls.getField("PATTERN").get(null))) {
                    return cls.getMethod("getFromString", String.class);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        throw new IllegalArgumentException("Cannot match parameter to any PATTERN");
    }
}
