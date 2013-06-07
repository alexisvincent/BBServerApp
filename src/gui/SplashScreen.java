package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.swing.JWindow;
import toolkit.BToolkit;

/**
 *
 * @author alexisvincent
 */
public class SplashScreen extends JWindow {

    private static BufferedImage splash;

    static {
        splash = (BufferedImage) BToolkit.getImage("splash");
    }

    public SplashScreen() {
        this.setBackground(new Color(0, 0, 0, 0));
        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        int splashWidth = splash.getWidth();
        int splashHeight = splash.getHeight();
        this.setBounds(screenWidth / 2 - splashWidth / 2, screenHeight / 2 - splashHeight / 2, splashWidth, splashHeight);
        this.setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.drawImage(splash, 0, 0, this);
    }
}
