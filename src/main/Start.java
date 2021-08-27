package main;

import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import java.applet.Applet;
import java.awt.*;
import java.util.Dictionary;
import java.util.Enumeration;

public class Start{

    //Instance of this class.
    public static Start INSTANCE;

    //Width and height of the Cloth.
    int junctionCountX = 20;
    int junctionCountY = 20;

    //The instance of the ClothSimulation.
    ClothSimulation clothSimulation;


    //Swing components
    JFrame frame;
    JSlider windSlider = new JSlider(-10, 10);
    JSlider gravitySlider = new JSlider(-10, 10);
    JSlider dampeningSlider = new JSlider(0, 100);
    JLabel windText = new JLabel("Wind", SwingConstants.CENTER);
    JLabel gravityText = new JLabel("Gravity", SwingConstants.CENTER);
    JLabel dampeningText = new JLabel("Dampening", SwingConstants.CENTER);

    JButton toggleShowJunctions = new JButton("Show Junctions: Off");
    JButton toggleShowConnectors = new JButton("Show Connectors: Off");
    JButton toggleShowStress = new JButton("Show Stress: Off");

    JButton selectJunctionColor = new JButton("Junction Color Picker");
    JButton selectConnectorColor = new JButton("Connector Color Picker");

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        Start.INSTANCE = new Start();
        ClothSimulation simulation = Start.INSTANCE.createWindow(2000, 2000);
        simulation.run();
    }

    /**
     *
     * @param width
     * @param height
     * @return
     */
    public ClothSimulation createWindow(int width, int height){
        frame = new JFrame("Cloth Simulation");
        JPanel options = new JPanel();
        frame.setLayout(new BorderLayout());
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        clothSimulation = new ClothSimulation(junctionCountX, junctionCountY);

        //Change listener for Wind slider.
        windSlider.addChangeListener(e -> {
            clothSimulation.cloth.setWindStrengthX(windSlider.getValue()/10f);
        });

        //Change listener for Gravity slider.
        gravitySlider.addChangeListener(e -> {
            clothSimulation.cloth.setGravityStrength(gravitySlider.getValue()/10f);
        });

        //Change listener for Dampening slider.
        dampeningSlider.addChangeListener(e -> {
            clothSimulation.cloth.setDampeningCoeff(dampeningSlider.getValue()/100f);
        });

        //Change listener for Junction show toggle
        toggleShowJunctions.addActionListener(e ->{
            clothSimulation.setDrawJunction(!clothSimulation.isDrawJunction());
            toggleShowJunctions.setText(("Show Junctions: " + (clothSimulation.isDrawJunction()?"On":"Off")));
        });

        //Change listener for Connector show toggle
        toggleShowConnectors.addActionListener(e ->{
            clothSimulation.setDrawConnectors(!clothSimulation.isDrawConnectors());
            toggleShowConnectors.setText(("Show Connectors: " + (clothSimulation.isDrawConnectors()?"On":"Off")));
        });

        //Change listener for Stress show toggle
        toggleShowStress.addActionListener(e ->{
            clothSimulation.setShowStress(!clothSimulation.isShowStress());
            toggleShowStress.setText(("Show Stress: " + (clothSimulation.isShowStress()?"On":"Off")));
        });

        //Action listener for opening the color picker for Junctions.
        selectJunctionColor.addActionListener(e ->{
            clothSimulation.setJunctionColor(JColorChooser.showDialog(clothSimulation, "Junction Color Picker", null));
        });

        //Action listener for opening the color picker for Connectors.
        selectConnectorColor.addActionListener(e ->{
            clothSimulation.setConnectorColor(JColorChooser.showDialog(clothSimulation, "Connector Color Picker", null));
        });

        //Slider visuals
        windSlider.setMajorTickSpacing(1);
        windSlider.setPaintTicks(true);
        windSlider.setPaintLabels(true);
        windSlider.setToolTipText("Wind");
        windSlider.setBackground(Color.BLACK);
        windSlider.setForeground(Color.WHITE);
        Dictionary dictionary = windSlider.getLabelTable();
        if (dictionary != null) {
            Enumeration keys = dictionary.keys();
            while (keys.hasMoreElements()) {
                JLabel label = (JLabel) dictionary.get(keys.nextElement());
                label.setForeground(Color.WHITE);
                label.setOpaque(true);
                label.setBackground(Color.BLACK);
                label.setFont(new Font("ClearSans", Font.BOLD, 12));
            }
        }


        gravitySlider.setMajorTickSpacing(1);
        gravitySlider.setPaintTicks(true);
        gravitySlider.setPaintLabels(true);
        gravitySlider.setValue((int)(clothSimulation.cloth.getGravityStrength()*10));
        gravitySlider.setToolTipText("Gravity");
        gravitySlider.setBackground(Color.BLACK);
        gravitySlider.setForeground(Color.WHITE);
        Dictionary dictionary1 = gravitySlider.getLabelTable();
        if (dictionary1 != null) {
            Enumeration keys = dictionary1.keys();
            while (keys.hasMoreElements()) {
                JLabel label = (JLabel) dictionary1.get(keys.nextElement());
                label.setForeground(Color.WHITE);
                label.setOpaque(true);
                label.setBackground(Color.BLACK);
                label.setFont(new Font("ClearSans", Font.BOLD, 12));
            }
        }

        dampeningSlider.setMajorTickSpacing(10);
        dampeningSlider.setPaintTicks(true);
        dampeningSlider.setPaintLabels(true);
        dampeningSlider.setValue((int)(clothSimulation.cloth.getDampeningCoeff()*100));
        dampeningSlider.setToolTipText("Gravity");
        dampeningSlider.setBackground(Color.BLACK);
        dampeningSlider.setForeground(Color.WHITE);
        Dictionary dictionary2 = dampeningSlider.getLabelTable();
        if (dictionary2 != null) {
            Enumeration keys = dictionary2.keys();
            while (keys.hasMoreElements()) {
                JLabel label = (JLabel) dictionary2.get(keys.nextElement());
                label.setForeground(Color.WHITE);
                label.setOpaque(true);
                label.setBackground(Color.BLACK);
                label.setFont(new Font("ClearSans", Font.BOLD, 12));
            }
        }

        frame.add(clothSimulation, BorderLayout.CENTER);
        frame.add(options, BorderLayout.SOUTH);

        //Sliders
        windText.setFont(new Font("ClearSans", Font.BOLD, 20));
        windText.setBackground(Color.BLACK);
        windText.setForeground(Color.WHITE);
        windText.setOpaque(true);

        gravityText.setFont(new Font("ClearSans", Font.BOLD, 20));
        gravityText.setBackground(Color.BLACK);
        gravityText.setForeground(Color.WHITE);
        gravityText.setOpaque(true);

        dampeningText.setFont(new Font("ClearSans", Font.BOLD, 20));
        dampeningText.setBackground(Color.BLACK);
        dampeningText.setForeground(Color.WHITE);
        dampeningText.setOpaque(true);

        //Buttons
        toggleShowJunctions.setFont(new Font("ClearSans", Font.BOLD, 20));
        toggleShowJunctions.setBackground(Color.BLACK);
        toggleShowJunctions.setForeground(Color.WHITE);
        toggleShowJunctions.setOpaque(true);
        toggleShowJunctions.setFocusable(false);

        toggleShowConnectors.setFont(new Font("ClearSans", Font.BOLD, 20));
        toggleShowConnectors.setBackground(Color.BLACK);
        toggleShowConnectors.setForeground(Color.WHITE);
        toggleShowConnectors.setOpaque(true);
        toggleShowConnectors.setFocusable(false);

        toggleShowStress.setFont(new Font("ClearSans", Font.BOLD, 20));
        toggleShowStress.setBackground(Color.BLACK);
        toggleShowStress.setForeground(Color.WHITE);
        toggleShowStress.setOpaque(true);
        toggleShowStress.setFocusable(false);

        selectJunctionColor.setFont(new Font("ClearSans", Font.BOLD, 20));
        selectJunctionColor.setBackground(Color.BLACK);
        selectJunctionColor.setForeground(Color.WHITE);
        selectJunctionColor.setOpaque(true);
        selectJunctionColor.setFocusable(false);

        selectConnectorColor.setFont(new Font("ClearSans", Font.BOLD, 20));
        selectConnectorColor.setBackground(Color.BLACK);
        selectConnectorColor.setForeground(Color.WHITE);
        selectConnectorColor.setOpaque(true);
        selectConnectorColor.setFocusable(false);

        options.setBackground(Color.BLACK);

        options.setLayout(new GridLayout(6,2));
        options.add(windText);
        options.add(windSlider);
        options.add(gravityText);
        options.add(gravitySlider);
        options.add(dampeningText);
        options.add(dampeningSlider);
        options.add(toggleShowJunctions);
        options.add(toggleShowConnectors);
        options.add(toggleShowStress);
        options.add(selectJunctionColor);
        options.add(selectConnectorColor);

        frame.pack();
        frame.setVisible(true);
        return clothSimulation;
    }
}
