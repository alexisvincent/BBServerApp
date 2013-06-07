package toolkit;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;

/**
 *
 * @author alexisvincent
 */
public class BSettings {

    public static boolean STATE_HomeScreen_isAnimating;
    private static Font fontSaxMono;

    static {
        //init fonts
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            //saxmono
            fontSaxMono = Font.createFont(Font.TRUETYPE_FONT, BSettings.class.getResourceAsStream("/resources/fonts/saxmono.ttf"));
            ge.registerFont(fontSaxMono);
            //helvatica
        } catch (FontFormatException | IOException ex) {
            System.out.println(ex);
        }

        //init colours

        //init states
        STATE_HomeScreen_isAnimating = false;
    }
//returns the font

    public static Font getFont(String componentName, int size) {
        float fontSize = size;
        Font font = fontSaxMono.deriveFont(14f);
        switch (componentName) {
            case "button":
                fontSize = (size == 0) ? 14f : (float) size;
                font = fontSaxMono.deriveFont(fontSize);
                break;
            case "label":
                fontSize = (size == 0) ? 16f : (float) size;
                font = fontSaxMono.deriveFont(fontSize);
                break;
            case "text":
                fontSize = (size == 0) ? 14f : (float) size;
                font = fontSaxMono.deriveFont(fontSize);
                break;
        }
        return font;
    }

    public static Font getFont(String componentName) {
        return getFont(componentName, 0);
    }

    public static Font getFont() {
        return getFont("DefaultFont", 0);
    }
}