package eu.arrvi.vects.common;

/**
 * Created by Kris on 2014-12-17.
 */
public class ChatMessage extends CommandParameter {
    public final static int SERVER = -1;
    
    public final static String PATTERN = "^-?[0-9]+;.+$";
    
    private final int author;
    private final String message;

    public ChatMessage(int author, String message) {
        this.author = author;
        this.message = message;
    }
    
    public static CommandParameter getFromString(String command) {
        String[] parts = command.split(";");
        return new ChatMessage(Integer.parseInt(parts[0]), parts[1]);
    }

    public int getAuthor() {
        return author;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return String.format("%d;%s", author, message);
    }
}
