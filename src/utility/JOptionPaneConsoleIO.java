package utility;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public final class JOptionPaneConsoleIO {
    private JOptionPaneConsoleIO () {} // prevent instantiation of this utility class
    
    // Reads a non-empty string
    public static String readNonEmpty(String prompt) {
        String input = "";
        boolean valid = false;
        
        while(!valid) {
            input = JOptionPane.showInputDialog(null, prompt, "Input Required", JOptionPane.QUESTION_MESSAGE);
            
            if (input == null)
                return null; // User clicked Cancel
            
            input = input.trim();
            
            if (!input.isEmpty()) {
                valid = true;
            } else {
                showError("Input cannot be empty.");
            }
        }
        
        return input;
    }
    
    // Reads any integer
    public static int readInt(String prompt) {
        boolean valid = false;
        int value = -1;
        
        while(!valid) {
            String input = JOptionPane.showInputDialog(null, prompt, "Enter Integer", JOptionPane.QUESTION_MESSAGE);
            
            if (input == null) 
                return -1; // User clicked Cancel
            
            try {
                value = Integer.parseInt(input.trim());
                valid = true;
            } catch (NumberFormatException e) {
                showError("Please enter a valid integer.");
            }
        }
        
        return value;
    }
    
    // Reads an integer within a specific range
    public static int readIntInRange(String prompt, int min, int max) {
        int value = -1;
        boolean valid = false;
        
        while (!valid) {
            value = readInt(prompt + " (" + min + "–" + max + ")");
            
            if (value == -1){
                break;
            }
            
            if (value >= min && value <= max) {
                valid = true;
            } else {
                showError("Please enter a number between " + min + " and " + max + ".");
            }
        }
        
        return value;
    }
    
    public static int readOption(String prompt, String title, String[] options) {
        return JOptionPane.showOptionDialog(
                null,
                prompt,
                title,
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );
    }
    
    // Reads an enum value via dropdown
    public static <T extends Enum<T>> T readEnum(String prompt, Class<T> enumType, String[] options) {
        T selectedEnum = null;
        boolean valid = false;
        
        while (!valid) {
            String choice = (String) JOptionPane.showInputDialog(
                    null,
                    prompt,
                    "Choose",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );
            
            if (choice == null) 
                return null; // User clicked Cancel
            
            for (T constant : enumType.getEnumConstants()) {
                if (constant.name().equalsIgnoreCase(choice)) {
                    selectedEnum = constant;
                    valid = true;
                    break;
                }
            }
            
            if (!valid) 
                showError("Invalid selection. Please try again.");
        }
        
        return selectedEnum;
    }
    
    // Shows a message and waits for OK
    public static void pause() {
        JOptionPane.showMessageDialog(null, "Press OK to continue...", "Pause", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Shows an error dialog
    public static void showError(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Shows a general info dialog
    public static void showInfo(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Shows a dialog with no icon
    public static void showPlain(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Message", JOptionPane.PLAIN_MESSAGE);
    }
    
    // Shows a dialog with no icon (custom title)
    public static void showPlain(String msg, String title) {
        JOptionPane.showMessageDialog(null, msg, title, JOptionPane.PLAIN_MESSAGE);
    }
    
    // Let user to confirm an action
    public static boolean confirmDialog(String message, String title) {
        int result = JOptionPane.showConfirmDialog(
                null,
                message,
                title,
                JOptionPane.YES_NO_OPTION
        );
        
        return result == JOptionPane.YES_OPTION;
    }
    
    // Format into monospaced columns
    public static void showMonospaced(String title, String content) {
        JTextArea ta = new JTextArea(content);
        ta.setFont(new Font("Monospaced", Font.PLAIN, 12));
        ta.setEditable(false);
        ta.setOpaque(false);
        ta.setCaretPosition(0);
        ta.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(760, 700));
        sp.setBorder(null);
        
        JOptionPane.showMessageDialog(
                null,
                sp,
                title,
                JOptionPane.PLAIN_MESSAGE
        );
    }
    
    // Report display
    public static String line(char c, int length) {
        return String.valueOf(c).repeat(length);
    }
    
    public static String center(String text, int width) {
        int pad = Math.max(0, (width - text.length()) / 2);
        return " ".repeat(pad) + text;
    }
    
    public static String reportHeader(String moduleName, String reportTitle, int width) {
        StringBuilder sb = new StringBuilder();
        sb.append(line('=', width)).append("\n");
        sb.append(center("TUNKU ABDUL RAHMAN UNIVERSITY OF MANAGEMENT AND TECHNOLOGY", width)).append("\n");
        sb.append(center(moduleName.toUpperCase(), width)).append("\n\n");
        sb.append(center(reportTitle.toUpperCase(), width)).append("\n");
        sb.append(center("-".repeat(reportTitle.length() + 6), width)).append("\n\n");
        sb.append("Generated at: ").append(
            java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("EEEE, MMM dd yyyy, hh:mm a")
            )).append("\n");
         sb.append(line('*', width)).append("\n\n");
        return sb.toString();
    }
    
    public static String sectionTitle(String title, int width) {
        return center(title, width) + "\n" + line('-', width) + "\n";
    }
    
    public static String reportFooter(int width) {
        return "\n" + line('*', width) + "\n" +
               center("END OF REPORT", width) + "\n" +
               line('=', width);
    }
    
    public static String readOptional(String message) {
        String input = JOptionPane.showInputDialog(null, message);
        if (input == null) {
            return null; // Cancel pressed
        }
        return input.trim(); // Allow empty input
    }
    
    public static String readDropdown(String message, String[] options) {
        return (String) JOptionPane.showInputDialog(
                null, message, "Select Option",
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]
        );
    }
}
