
package tests;

import components.BButton;
import components.BPanel;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JFrame;

/**
 * 
 * @author alexisit12
 */
public class TestMain {

    public static void main(String[] args) {
        new TestMain();
        mainFrame.setVisible(true);
        System.out.println("test");
    }
    private static JFrame mainFrame;
    private BButton button;
    
    public TestMain() {
        //set global properties
        System.setProperty("apple.awt.graphics.EnableQ2DX", "true");
        System.setProperty("apple.awt.graphics.UseQuartz", "true");
        //System.setProperty("sun.awt.noerasebackground","true");
        
        //init mainFrame
        mainFrame = new JFrame();
        mainFrame.setContentPane(new BPanel());
        mainFrame.setLayout(new GridBagLayout());
        mainFrame.setPreferredSize(new Dimension(400,400));
        mainFrame.setMinimumSize(new Dimension(400,400));
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        System.out.println("text");
        
        GridBagConstraints gc = new GridBagConstraints();
        
        button = new BButton("Test");
        button.setPreferredSize(new Dimension(60,30));
        
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.CENTER;
        mainFrame.add(button, gc);
    }

}
