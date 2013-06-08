package gui;

import components.BFooter;
import components.BMenuBar;
import components.BPanel;
import components.BSwitch;
import components.BTextPane;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
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
    private JComponent logoPane;

    public HomeScreen() {
        //init components
        menubar = new BMenuBar();
        homeScreenPanel = new HomeScreenPanel();
        footer = new BFooter();
        logoPane = new JComponent() {
                Image logo = BToolkit.getImage("logo");
                Point pt = new Point(270, 20);

                @Override
                public void paint(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                    g2d.drawImage(logo, pt.x, pt.y, (int) (logo.getWidth(this) / 1.5), (int) (logo.getHeight(this) / 1.5), this);
                }
            };
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
        
        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 1;
        gc.gridheight = 3;
        gc.weightx = 1;
        gc.weighty = 1;
        this.add(logoPane, gc);

    }

    public HomeScreenPanel getHomeScreenPanel() {
        return homeScreenPanel;
    }

    class HomeScreenPanel extends JComponent {

        private GridBagConstraints gc;
        private int panelOpacity;
        private BSwitch serverSwitch;
        private BTextPane log;
        private JComponent logoPane;

        public HomeScreenPanel() {

            //variabili
            panelOpacity = 255;
            serverSwitch = new BSwitch();
            log = new BTextPane();

            //setup le variabili
            serverSwitch.setFont(BSettings.getFont("BSwitch", 12));
            serverSwitch.setPreferredSize(new Dimension(100, 50));

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