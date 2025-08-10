package utility;

import java.util.Scanner;

public final class ConsoleIO {
    private ConsoleIO () {} // prevent instantiation of this utility class
    
    /* ---------- Basic Reads ---------- */
    
    public static int readInt(Scanner sc, String prompt) {
        System.out.print(prompt);
        
        while (!sc.hasNextInt()) {
            System.out.print("Print enter a number: ");
            sc.next(); // discard invalid token
        }
        
        int val = sc.nextInt();
        sc.nextLine(); // consume newline
        
        return val;
    }
    
    // if no use this jiu delete ler
    public static int readIntInRange(Scanner sc, String prompt, int min, int max) {
        int v;
        
        do {
            v = readInt(sc, prompt);
            if (v < min || v > max)
                System.out.printf("Enter &d-%d.%n", min, max);
        } while (v < min || v > max);
        
        return v;
    }
    
    public static String readNonEmpty(Scanner sc, String prompt) {
        String s;
        
        do {
            System.out.print(prompt);
            s = sc.nextLine().trim();
            if (s.isEmpty())
                System.out.println("Value cannot be empty.");
        } while (s.isEmpty());
        
        return s;
    }
    
    public static boolean confirm(Scanner sc, String prompt) {
        String raw = "";
        
        while (!raw.equals("Y") && !raw.equals("N")) {
            System.out.print(prompt + "(Y/N): ");
            raw = sc.nextLine().trim().toUpperCase();
            if (!raw.equals("Y") && !raw.equals("N")) {
                System.out.println("Please enter Y or N.");
            }
        }
        
        return raw.equals("Y");
    }
    
    /* ---------- Enums ---------- */
    
    public static <E extends Enum<E>> E readEnum(Scanner sc, String label, Class<E> enumClass, String[] allowedNames) {
        // Build prompt options inline: (EXAMPLE_1/EXAMPLE_2)
        /* ---------- START ---------- */
        
        StringBuilder options = new StringBuilder("(");
        
        for (int i = 0; i < allowedNames.length; i++) {
            options.append(allowedNames[i]);
            if (i < allowedNames.length - 1)
                options.append("/");
        }
        
        options.append(")");
        
        /* ---------- END ---------- */
        
        String raw = "";
        boolean valid = false;
        
        while (!valid) {
            System.out.print(label + " " + options + ": ");
            raw = sc.nextLine().trim().toUpperCase();
            
            for (String name : allowedNames) {
                if (raw.equals(name)) {
                    valid = true;
                    break;
                }
            }
            
            if (!valid)
                System.out.println("Invalid choice. Please try again.");
        }
        
        return Enum.valueOf(enumClass, raw);
    }
    
    /* ---------- Flow Helpers ---------- */
    
    public static void pause(Scanner sc) {
        System.out.print("\nPress Enter to continue...");
        sc.nextLine();
    }
}
