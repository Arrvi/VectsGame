package eu.arrvi.vects.common;

import java.util.HashSet;
import java.util.Set;

/**
 * Vects protocol command. It consists of command name and list of parameters.
 */
public class Command {
    private final String name;
    private Set<Object> params;

    public Command() {
        this("NOP");
    }

    public Command(String name) {
        this(name, new HashSet<Object>());
    }

    public Command(String name, Set<Object> params) {
        this.name = name;
        this.params = params;
    }

    public String getName() {
        return name;
    }

    public Set<Object> getParams() {
        return params;
    }

    public void setParams(Set<Object> params) {
        this.params = params;
    }
    
    public void addParam(Object param) {
        params.add(param);
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

    private static Object parseParam(String paramString) {
        if ( paramString.matches(VehiclePosition.PATTERN) ) {
            return VehiclePosition.getPositionFromString(paramString);
        }
        else if (paramString.matches(TrackPoint.PATTERN)) {
            return TrackPoint.getPointFromString(paramString);
        }
        return null;
    }
}
