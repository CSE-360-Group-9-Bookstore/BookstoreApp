package bookstore;

import javafx.scene.paint.Color;

public class ColorConfig {

    // ASU brand colors
    public static final Color ASU_MAROON = Color.web("#8C1D40");
    public static final Color ASU_GOLD = Color.web("#FFC627");
    public static final Color ASU_WHITE = Color.web("#FFFFFF");
    public static final Color ASU_RICH_BLACK = Color.web("#000000");

    // Desaturated or lighter versions
    public static final Color LIGHTER_MAROON = Color.web("#A74A64");  // Lighter version of ASU Maroon

    // Define color constants for the interface
    public static final Color BACKGROUND = ASU_MAROON;
    public static final Color BUTTON_BACKGROUND = ASU_GOLD;
    public static final Color BUTTON_TEXT = ASU_RICH_BLACK;
    public static final Color TEXTFIELD_BACKGROUND = LIGHTER_MAROON;
    public static final Color TEXTFIELD_TEXT = ASU_WHITE;
    public static final Color TEXTFIELD_PROMPT_TEXT = Color.web("#F0F0F0");  // Lighter white for prompt text
    public static final Color ADDITIONAL_TEXT = Color.WHITE;


    // Method to generate button CSS style (e.g., Login Button)
    public static String getButtonStyle() {
        return String.format(
                "-fx-background-color: %s; -fx-text-fill: %s; -fx-background-radius: 5px;",
                toHex(BUTTON_BACKGROUND),
                toHex(BUTTON_TEXT)
        );
    }

    // Method to generate text field CSS style
    public static String getTextFieldStyle() {
        return String.format(
                "-fx-background-color: %s; -fx-text-fill: %s; -fx-prompt-text-fill: %s;",
                toHex(TEXTFIELD_BACKGROUND),
                toHex(TEXTFIELD_TEXT),
                toHex(TEXTFIELD_PROMPT_TEXT)
        );
    }

    // Method to generate background CSS style
    public static String getBackgroundStyle() {
        return String.format("-fx-background-color: %s;", toHex(BACKGROUND));
    }

    // Method to generate top bar CSS style
    public static String getTopBarStyle() {
        return String.format("-fx-background-color: %s;", toHex(ASU_RICH_BLACK));
    }

    // Helper method to convert Color to hex string
    private static String toHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
}
