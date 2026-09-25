import java.awt.Color;

// Each constant is a full palette. apply() pushes it into Constants, which is
// the single place the rest of the UI reads colors from - there is no
// generic Look-and-Feel machinery here on purpose, this app is small enough
// that "one place holds the current colors, everyone re-reads it" is simpler
// than a pluggable theming framework.
public enum Theme {
    DARK("Dark",
            new Color(24, 24, 24),      // bgWindow
            new Color(45, 45, 45),      // bgToolbar
            new Color(38, 38, 38),      // bgSidebar
            new Color(24, 24, 24),      // bgStatusBar
            new Color(15, 15, 15), 2,   // border, borderWidth
            new Color(230, 230, 230),   // textPrimary
            new Color(160, 160, 160),   // textMuted
            new Color(135, 206, 250),   // accent (sky blue)
            new Color(255, 69, 0),      // accentWarn (red-orange)
            new Color(144, 238, 144),   // accentSuccess (light green)
            new Color(45, 45, 45),      // buttonIdleBg
            new Color(15, 15, 15)),     // buttonSelectedBg

    LIGHT("Light",
            new Color(245, 245, 245),
            new Color(230, 230, 230),
            new Color(220, 220, 220),
            new Color(235, 235, 235),
            new Color(180, 180, 180), 1,
            new Color(30, 30, 30),
            new Color(90, 90, 90),
            new Color(0, 120, 215),     // accent - deeper blue, sky blue fails contrast on white
            new Color(200, 50, 0),
            new Color(30, 140, 30),
            new Color(230, 230, 230),
            new Color(0, 120, 215)),

    HIGH_CONTRAST("High-Contrast",
            Color.black,
            Color.black,
            Color.black,
            Color.black,
            Color.white, 3,
            Color.white,
            Color.yellow,
            Color.yellow,
            Color.red,
            Color.green,
            Color.black,
            Color.yellow);

    public final String label;
    public final Color bgWindow, bgToolbar, bgSidebar, bgStatusBar;
    public final Color border;
    public final int borderWidth;
    public final Color textPrimary, textMuted;
    public final Color accent, accentWarn, accentSuccess;
    public final Color buttonIdleBg, buttonSelectedBg;

    public static Theme current = DARK;

    Theme(String label, Color bgWindow, Color bgToolbar, Color bgSidebar, Color bgStatusBar,
          Color border, int borderWidth, Color textPrimary, Color textMuted,
          Color accent, Color accentWarn, Color accentSuccess,
          Color buttonIdleBg, Color buttonSelectedBg) {
        this.label = label;
        this.bgWindow = bgWindow;
        this.bgToolbar = bgToolbar;
        this.bgSidebar = bgSidebar;
        this.bgStatusBar = bgStatusBar;
        this.border = border;
        this.borderWidth = borderWidth;
        this.textPrimary = textPrimary;
        this.textMuted = textMuted;
        this.accent = accent;
        this.accentWarn = accentWarn;
        this.accentSuccess = accentSuccess;
        this.buttonIdleBg = buttonIdleBg;
        this.buttonSelectedBg = buttonSelectedBg;
    }

    public void apply() {
        Constants.bgWindow = bgWindow;
        Constants.bgToolbar = bgToolbar;
        Constants.bgSidebar = bgSidebar;
        Constants.bgStatusBar = bgStatusBar;
        Constants.border = border;
        Constants.borderWidth = borderWidth;
        Constants.textPrimary = textPrimary;
        Constants.textMuted = textMuted;
        Constants.accent = accent;
        Constants.accentWarn = accentWarn;
        Constants.accentSuccess = accentSuccess;
        Constants.buttonIdleBg = buttonIdleBg;
        Constants.buttonSelectedBg = buttonSelectedBg;
        current = this;
    }

    public static Theme fromLabel(String label) {
        for (Theme t : values()) {
            if (t.label.equals(label)) return t;
        }
        return DARK;
    }
}
