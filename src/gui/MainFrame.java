package gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 *
 * @author alexisvincent
 */
public class MainFrame extends JFrame {
    
    private GridBagConstraints gc;
    private static HomeScreen homeScreen;
    
    static {
        homeScreen = new HomeScreen();
    }

    public MainFrame() {

        //configure this damn FRAME O.o
        this.setResizable(false);
        this.setUndecorated(true);
        this.setSize(400, 400);
        this.setLocationRelativeTo(null);
        this.setBackground(new Color(0, 0, 0, 0));

        //begin adding components
        this.setLayout(new GridBagLayout());
        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 1;
        gc.gridheight = 1;
        gc.fill = GridBagConstraints.BOTH;
        gc.anchor = GridBagConstraints.CENTER;
        gc.weightx = 1;
        gc.weighty = 1;
        this.addPanel(homeScreen);
        
        //starting animation
        
    }
    
    public static HomeScreen getHomeScreen() {
        return homeScreen;
    }

    public final void addPanel(JComponent panel) {
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                //check all components on this frame (or rather it's content pane)
                for (int i = 0; i < MainFrame.this.getContentPane().getComponentCount(); i++) {
                    //we only want to hide all OTHER components except the component we set to visible
                    if (!e.getComponent().equals(MainFrame.this.getContentPane().getComponent(i))) {
                        MainFrame.this.getContentPane().getComponent(i).setVisible(false);
                    }
                }
            }
        });
        this.add(panel, gc);
    }
}
