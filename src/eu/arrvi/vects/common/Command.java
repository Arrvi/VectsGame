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

    /**
     * Creates empty command (NOP)
     */
    public Command() {
        this("NOP");
    }

    /**
     * Creates new command of given type with no parameters. Target is set to broadcast.
     * @param name name of the command
     */
    public Command(String name) {
        this(TARGET_BROADCAST, name, new ArrayList<CommandParameter>());
    }

    /**
     * Creates new command of given type to given target with no parameters
     * @param target id of target client
     * @param name name of the command
     */
    public Command(int target, String name) {
        this(target, name, new ArrayList<CommandParameter>());
    }

    /**
     * Creates new command of given type with single parameter. It will be sent to given target.
     * @param target id of target client
     * @param name name of the command
     * @param param command only parameter
     */
    public Command(int target, String name, CommandParameter param) {
        this.target = target;
        this.name = name.toUpperCase();
        this.params = new ArrayList<>(2);
        this.params.add(param);
    }

    /**
     * Creates new command of given type with parameters. It will be sent to given target
     * @param target id of target client
     * @param name name of the command
     * @param params list of command parameters
     */
    public Command(int target, String name, List<CommandParameter> params) {
        this.target = target;
        this.name = name.toUpperCase();
        this.params = params;
    }

    /**
     * Creates new command of given type with parameters. Target is set to broadcast.
     * @param name name of the command
     * @param parameter list of command parameters
     */
    public Command(String name, CommandParameter parameter) {
        this(Command.TARGET_BROADCAST, name, parameter);
    }

    /**
     * Returns name of this command. It has to be in form of 3 capital letters.
     * @return name of this command
     */
    public String getName() {
        return name;
    }

    /**
     * Returns list of command parameters. It may be 
     * @return list of this command parameters.
     */
    public List<CommandParameter> getParams() {
        return params;
    }

    /**
     * Returns first param of this command. Useful in single param commands.
     * @return null if command has no parameters, first or only parameter otherwise
     */
    public CommandParameter getFirstParam() {
        if ( params.size() == 0 ) 
            return null;
        return params.get(0);
    }

    /**
     * Set all parameters of this command. Kinda afterthought.
     * @param params list of command parameters
     */
    public void setParams(List<CommandParameter> params) {
        this.params = params;
    }

    /**
     * Add param to this command. It is added at the end of the list.
     * @param param command parameter to be added
     */
    public void addParam(CommandParameter param) {
        params.add(param);
    }

    /**
     * Returns id of target client. Usually it is TCP port of local socket which the client is connected to. 
     * @return id of target client
     */
    public int getTarget() {
        return target;
    }

    /**
     * Returns formal string representation of this command consistent with protocol specification. At this point
     * it is ready to be sent through network.
     * @return formal string representation of this command
     */
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

    /**
     * Returns formal representation of this command.
     * @see eu.arrvi.vects.common.Command#getCommandString()
     * @return string representation of this command
     */
    @Override
    public String toString() {
        return getCommandString();
    }

    /**
     * Parses command string and returns corresponding command.
     * @param command string representation of a command to be parsed
     * @return null if string is less than 3 character long, command object representation of given string otherwise
     */
    public static Command getCommandFromString(int target, String command) {
        // Command consists of at least 3 letter name
        if ( command.length() < 3 ) return null;
        
        String name = command.substring(0, 3);
        List<CommandParameter> params = new ArrayList<>();
        
        if ( command.length() > 4 ) {
            command = command.substring(4);
            String[] paramStrings = command.split("\\|");
            for (String paramString : paramStrings) {
                CommandParameter param = parseParam(paramString);
                
                if (param != null)
                    params.add(param);
            }
        }
        
        return new Command(target, name, params);
    }

    @SuppressWarnings("unchecked")
    private static CommandParameter parseParam(String paramString) {
        final String[] classNames = {"GameInfo", "VehiclePosition", "ChatMessage", "TrackPoint", "SimpleInfo"};

        for (String className : classNames) {
            try {
                Class<? extends CommandParameter> cls = 
                        (Class<? extends CommandParameter>) Class.forName("eu.arrvi.vects.common."+className);

                if (paramString.matches((String) cls.getField("PATTERN").get(null))) {
                    return ((CommandParameter) cls.getMethod("getFromString", String.class).invoke(paramString));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }
}
