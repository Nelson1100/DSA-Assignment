package utility;

public final class ScannerDisplayEffect {
//    public static void clearScreen(){
//        Scanner sc = new Scanner(System.in);
//        System.out.println("Press Enter to continue...");
//        sc.nextLine();
//        for (int i = 0; i < 50; i++)
//            System.out.println();
//    }
//    
//    public static void drawLine(){
//        System.out.println("=========================================");
//    }
    
    private ScannerDisplayEffect() {} // prevent instantiation of this utility class
    
    public static void clearScreen() {
        for (int i = 0; i < 50; i++)
            System.out.println();
    }
    
    public static void printHeader(String title) {
        String line = repeat("=", Math.max(36, Math.min(80, title.length() + 12)));
        System.out.println(line);
        System.out.println(center(title, line.length()));
        System.out.println(line);
    }
    
    public static void printSubheader(String text) {
        System.out.println(text);
        System.out.println(repeat("-", Math.max(12, text.length())));
    }
    
    public static void printDivider() {
        System.out.println(repeat("-", 40));
    }
    
    /* ---------- Private Helpers ---------- */
        
    private static String center(String s, int width) {
        if (s.length() >= width)
            return s;
        int pad = (width - s.length()) / 2;
        return repeat(" ", pad) + s;
    }
    
    private static String repeat(String s, int n) {
        return s.repeat(Math.max(0, n));
    }
}
