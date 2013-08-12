package main;

import components.BFooter;
import components.BMenuBar;
import gui.MainFrame;
import gui.SplashScreen;
import javax.swing.JFrame;
import requestEngine.RequestEngine;
import settingsEngine.ProfileEngine;
import socketEngine.SimpleSocketEngine;
import sqlEngine.SQLEngine;

/**
 *
 * @author alexisvincent
 */
public class Main {

    private static MainFrame mainFrame;
    private static SplashScreen splash;
    
    private static SimpleSocketEngine socketEngine;
    private static SQLEngine sqlEngine;
    private static RequestEngine requestEngine;
    private static ProfileEngine profileEngine;
    
    private static Main INSTANCE;

    static {
        //set global properties
        System.setProperty("apple.awt.graphics.EnableQ2DX", "true");
        System.setProperty("apple.awt.graphics.UseQuartz", "true");
        
        //splashScreen
        splash = new SplashScreen();
        splash.setVisible(true);
        
        profileEngine = new ProfileEngine();
        
        socketEngine = new SimpleSocketEngine(profileEngine.getFirstProfile().getServer().getServerPort());
        sqlEngine = new SQLEngine();
        requestEngine = new RequestEngine(sqlEngine);
        

        //init mainFrame
        mainFrame = new MainFrame();
        mainFrame.setVisible(true);
        splash.setVisible(false);
        
        BMenuBar.setMainFrame(mainFrame);
        BFooter.setHomeScreen(mainFrame.getHomeScreen());
    }
    
    public static void main(String[] args) {
        getINSTANCE();
    }

    public Main() {
    }

    public static JFrame getMainFrame() {
        return mainFrame;
    }
    
    public static Main getINSTANCE() {
        if (INSTANCE==null) {
            INSTANCE = new Main();
        }
        return INSTANCE;
    }

    public static SimpleSocketEngine getSocketEngine() {
        return socketEngine;
    }

    public static SQLEngine getSqlEngine() {
        return sqlEngine;
    }

    public static RequestEngine getRequestEngine() {
        return requestEngine;
    }

}
