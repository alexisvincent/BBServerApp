package gui;

import components.BFooter;
import components.BMenuBar;
import components.BPanel;
import components.BSwitch;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JComponent;
import toolkit.BSettings;
import toolkit.BToolkit;

/**
 *
 * @author alexisvincent
 */
public class HomeScreen extends BPanel {
    //declare components

    private BMenuBar menubar;
    private HomeScreenPanel homeScreenPanel;
    private BFooter footer;

    public HomeScreen() {
        //init components
        menubar = new BMenuBar();
        homeScreenPanel = new HomeScreenPanel();
        footer = new BFooter();
        //set ResultScreen properties
        //add components to ResultScreen
        GridBagConstraints gc = new GridBagConstraints();
        this.setLayout(new GridBagLayout());

        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 1;
        gc.gridheight = 1;
        gc.weightx = 1;
        gc.weighty = 0;
        gc.fill = GridBagConstraints.BOTH;
        gc.anchor = GridBagConstraints.CENTER;
        this.add(menubar, gc);

        gc.gridy = 1;
        gc.weighty = 1;
        this.add(homeScreenPanel, gc);

        gc.gridy = 2;
        gc.weighty = 0;
        this.add(footer, gc);

    }

    public HomeScreenPanel getHomeScreenPanel() {
        return homeScreenPanel;
    }

    class HomeScreenPanel extends JComponent {

        private GridBagConstraints gc;
        private int panelOpacity;
        private BSwitch serverSwitch;

        public HomeScreenPanel() {
            
            //variabili
            panelOpacity = 255;
            serverSwitch = new BSwitch();
            
            //setup le variabili
            serverSwitch.setFont(BSettings.getFont("BSwitch"));
            serverSwitch.setPreferredSize(new Dimension(200, 100));

            //begin adding le variabili
            this.setLayout(new GridBagLayout());
            gc = new GridBagConstraints();
            gc.gridx = 0;
            gc.gridy = 0;
            gc.gridwidth = 1;
            gc.gridheight = 1;
            gc.weightx = 1;
            gc.weighty = 1;
            gc.ipadx = 0;
            gc.ipady = 0;
            gc.insets = new Insets(0, 0, 0, 0);
            gc.fill = GridBagConstraints.NONE;
            gc.anchor = GridBagConstraints.CENTER;
            this.add(serverSwitch, gc);
            

        }

        public void animate(String action) {
            switch (action) {
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setComposite(BToolkit.makeComposite(panelOpacity));
        }
    }
}