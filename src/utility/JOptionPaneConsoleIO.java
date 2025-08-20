package utility;

import javax.swing.JOptionPane;

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
}
