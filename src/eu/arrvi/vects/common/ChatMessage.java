package eu.arrvi.vects.common;

/**
 * Chat message parameter. Format: authorID;message.
 */
public class ChatMessage extends CommandParameter {
    /**
     * Server ID
     */
    public final static int SERVER = -1;

    /**
     * Parameter pattern
     */
    public final static String PATTERN = "^-?[0-9]+;.+$";
    
    private final int author;
    private final String message;

    /**
     * Creates chat message command parameter. Used in CHT command.
     * 
     * @param author id of author of this message
     * @param message message body
     */
    public ChatMessage(int author, String message) {
        this.author = author;
        this.message = message;
    }

    /**
     * Creates new message by parsing command string
     * @param command string to be parsed according to pattern
     * @return command parameter represented by given string
     */
    public static CommandParameter getFromString(String command) {
        String[] parts = command.split(";");
        if ( parts.length != 2 ) return null;
        return new ChatMessage(Integer.parseInt(parts[0]), parts[1]);
    }

    /**
     * Returns message's author.
     * @return author of this message.
     */
    public int getAuthor() {
        return author;
    }

    /**
     * Returns body of the message
     * @return body of this mesage
     */
    public String getMessage() {
        return message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("%d;%s", author, message);
    }
}
