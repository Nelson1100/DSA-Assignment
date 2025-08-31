package entity;

public enum Instruction {
    BEFORE_MEAL("Take 30 minutes before meals"),
    AFTER_MEAL("Take after meals"),
    BEFORE_SLEEP("Take before going to sleep"),
    WHEN_NEEDED("Take only when needed"),
    ONCE_DAILY("Take once daily"),
    TWICE_DAILY("Take twice daily (morning and evening)"),
    THREE_TIMES_DAILY("Take three times daily (morning, afternoon, night)"),
    EVERY_6_HOURS("Take every 6 hours"),
    EVERY_8_HOURS("Take every 8 hours"),
    MORNING_ONLY("Take in the morning only"),
    NIGHT_ONLY("Take at night only");

    private final String label;

    Instruction(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
    
    
}
