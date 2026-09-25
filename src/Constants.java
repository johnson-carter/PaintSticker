import java.awt.Color;
import java.awt.Font;

public class Constants {
    // Backgrounds
    static Color bgWindow;
    static Color bgToolbar;
    static Color bgSidebar;
    static Color bgStatusBar;

    // Chrome
    static Color border;
    static int borderWidth = 2;

    // Text
    static Color textPrimary;
    static Color textMuted;

    // Accents
    static Color accent;
    static Color accentWarn;
    static Color accentSuccess;

    // Tool-selector button states
    static Color buttonIdleBg;
    static Color buttonSelectedBg;

    // Typography - theme-independent, only color changes per theme
    static final Font FONT_LABEL = new Font("Verdana", Font.PLAIN, 12);
    static final Font FONT_BUTTON = new Font("Verdana", Font.PLAIN, 12);
    static final Font FONT_HEADING = new Font("Verdana", Font.BOLD, 12);
    static final Font FONT_GLYPH = new Font("Verdana", Font.BOLD, 16);
}
