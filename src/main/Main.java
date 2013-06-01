package main;

import components.BFooter;
import components.BMenuBar;
import gui.MainFrame;
import gui.SplashScreen;
import javax.swing.JFrame;

/**
 *
 * @author alexisvincent
 */
public class Main {

    private static MainFrame mainFrame;
    private static SplashScreen splash;

    public static void main(String[] args) {
        new Main();
    }

    public Main() {

        //set global properties
        System.setProperty("apple.awt.graphics.EnableQ2DX", "true");
        System.setProperty("apple.awt.graphics.UseQuartz", "true");
        
        //splashScreen
        splash = new SplashScreen();
        splash.setVisible(true);

        //init mainFrame
        mainFrame = new MainFrame();
        mainFrame.setVisible(true);
        splash.setVisible(false);
        
        BMenuBar.setMainFrame(mainFrame);
        BFooter.setHomeScreen(mainFrame.getHomeScreen());

    }

    public static JFrame getMainFrame() {
        return mainFrame;
    }
}
