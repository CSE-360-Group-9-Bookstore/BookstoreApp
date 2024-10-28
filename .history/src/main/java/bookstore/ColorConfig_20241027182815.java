package bookstore;

import javafx.scene.paint.Color;
import java.util.HashMap;
import java.util.Map;

public class ColorConfig {

    private static final int selectedScheme = 1; /

    public static class ColorScheme {
        public final Color background;
        public final Color buttonBackground;
        public final Color buttonText;
        public final Color textFieldBackground;
        public final Color textFieldText;
        public final Color textFieldPromptText;
        public final Color additionalText;
        public final Color topBar;

        public ColorScheme(Color background, Color buttonBackground, Color buttonText,
                           Color textFieldBackground, Color textFieldText,
                           Color textFieldPromptText, Color additionalText) {
            this.background = background;
            this.buttonBackground = buttonBackground;
            this.buttonText = buttonText;
            this.textFieldBackground = textFieldBackground;
            this.textFieldText = textFieldText;
            this.textFieldPromptText = textFieldPromptText;
            this.additionalText = additionalText;
            this.topBar = calculateDarkerColor(background, 0.9); // 10% darker
        }
    }

    
    private static final Map<Integer, ColorScheme> colorSchemes = new HashMap<>();

    static {
        // ASU brand colors as Scheme 1
        colorSchemes.put(1, new ColorScheme(
                Color.web("#84213F"),  // ASU Maroon
                Color.web("#FFC627"),  // ASU Gold
                Color.web("#000000"),  // ASU Rich Black (button text)
                Color.web("#A74A64"),  // Lighter ASU Maroon
                Color.web("#FFFFFF"),  // ASU White (textfield text)
                Color.web("#F0F0F0"),  // Lighter white for prompt text
                Color.web("#FFFFFF")   // Additional text (ASU White)
        ));


        // Scheme 2
        colorSchemes.put(2, new ColorScheme(
                Color.web("#3F3F74"),  // Custom Dark Blue
                Color.web("#27C6FF"),  // Custom Light Blue
                Color.web("#000000"),  // Black (button text)
                Color.web("#6464A7"),  // Custom Light Purple
                Color.web("#FFFFFF"),  // White (textfield text)
                Color.web("#F0F0F0"),  // Lighter white for prompt text
                Color.web("#FFFFFF")   // Additional text (White)
        ));

        // Scheme 3
        colorSchemes.put(3, new ColorScheme(
                Color.web("#2F4F2F"),  // Custom Green
                Color.web("#FFD700"),  // Custom Golden
                Color.web("#000000"),  // Black (button text)
                Color.web("#556B2F"),  // Custom Olive Green
                Color.web("#FFFFFF"),  // White (textfield text)
                Color.web("#F0F0F0"),  // Lighter white for prompt text
                Color.web("#FFFFFF")   // Additional text (White)
        ));

        // Scheme 4
        colorSchemes.put(4, new ColorScheme(
                Color.web("#800080"),  // Custom Purple
                Color.web("#FFDAB9"),  // Custom Peach
                Color.web("#000000"),  // Black (button text)
                Color.web("#9370DB"),  // Custom Medium Purple
                Color.web("#FFFFFF"),  // White (textfield text)
                Color.web("#F0F0F0"),  // Lighter white for prompt text
                Color.web("#FFFFFF")   // Additional text (White)
        ));

        // Scheme 5 (From the image, converted to HEX)
        colorSchemes.put(5, new ColorScheme(
                Color.web("#F3F3F3"),  // GMK WS2 (RGB equivalent: #F3F3F3)
                Color.web("#373847"),  // RAL 260 30 05 (RGB equivalent: #373847)
                Color.web("#000000"),  // Black (button text)
                Color.web("#65C4BE"),  // RAL 190 70 20 (RGB equivalent: #65C4BE)
                Color.web("#FFFFFF"),  // White (textfield text)
                Color.web("#F0F0F0"),  // Lighter white for prompt text
                Color.web("#FFFFFF")   // White (Additional text)
        ));
    }


    private static ColorScheme getCurrentScheme() {
        return colorSchemes.getOrDefault(selectedScheme, colorSchemes.get(1)); // Default to Scheme 1
    }


    public static final Color BACKGROUND = getCurrentScheme().background;
    public static final Color BUTTON_BACKGROUND = getCurrentScheme().buttonBackground;
    public static final Color BUTTON_TEXT = getCurrentScheme().buttonText;
    public static final Color TEXTFIELD_BACKGROUND = getCurrentScheme().textFieldBackground;
    public static final Color TEXTFIELD_TEXT = getCurrentScheme().textFieldText;
    public static final Color TEXTFIELD_PROMPT_TEXT = getCurrentScheme().textFieldPromptText;
    public static final Color ADDITIONAL_TEXT = getCurrentScheme().additionalText;
    public static final Color TOP_BAR = getCurrentScheme().topBar;


    public static String getButtonStyle() {
        return String.format(
                "-fx-background-color: %s; -fx-text-fill: %s; -fx-background-radius: 5px;",
                toHex(BUTTON_BACKGROUND),
                toHex(BUTTON_TEXT)
        );
    }


    public static String getTextFieldStyle() {
        return String.format(
                "-fx-background-color: %s; -fx-text-fill: %s; -fx-prompt-text-fill: %s;",
                toHex(TEXTFIELD_BACKGROUND),
                toHex(TEXTFIELD_TEXT),
                toHex(TEXTFIELD_PROMPT_TEXT)
        );
    }

    // Method to generate background CSS style    public static String getBackgroundStyle() {
        return String.format("-fx-background-color: %s;", toHex(BACKGROUND));
    }

   
    public static String getTopBarStyle() {
        return String.format("-fx-background-color: %s;", toHex(TOP_BAR));
    }

    // Helper method to convert Color to hex string
    private static String toHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    // Helper method to calculate a darker color
    private static Color calculateDarkerColor(Color color, double factor) {
        if (color.equals(Color.BLACK)) {
            return Color.BLACK; // If background is black, the top bar remains black
        }
        return new Color(
                Math.max(color.getRed() * factor, 0),
                Math.max(color.getGreen() * factor, 0),
                Math.max(color.getBlue() * factor, 0),
                color.getOpacity() // Preserve opacity
        );
    }
}
