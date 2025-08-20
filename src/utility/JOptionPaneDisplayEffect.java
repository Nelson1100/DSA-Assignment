package utility;

public final class JOptionPaneDisplayEffect {
    private JOptionPaneDisplayEffect() {} // prevent instantiation of this utility class
    
    // Returns a header-style string
    public static String formatHeader(String title) {
        return "\n" + center("==== " + title + " ====") + "\n";
    }
    
    // Returns a subheader-style string
    public static String formatSubheader(String title) {
        return "\n" + center("-- " + title + " --") + "\n";
    }
    
    // Returns a divider line
    public static String divider() {
        return "----------------------------------------";
    }
    
    // Centers a string in 40 characters
    public static String center(String text) {
        int width = 40;
        int pad = Math.max(0, (width - text.length()) / 2);
        return " ".repeat(pad) + text;
    }
    
    // Returns a full title block (header + divider)
    public static String titleBlock(String title) {
        return formatHeader(title) + divider() + "\n";
    }
}
