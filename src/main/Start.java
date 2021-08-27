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
        //Creates a new static instance of the Start class.
        Start.INSTANCE = new Start();

        //Creates an instance of the ClothSimulation.
        ClothSimulation simulation = Start.INSTANCE.createWindow(1000, 1000);

        //Begins the simulation.
        simulation.run();
    }

    /**
     *
     * @param width
     * @param height
     * @return
     */
    public ClothSimulation createWindow(int width, int height){
        //Create a new JFrame instance.
        frame = new JFrame("Cloth Simulation");

        //Create a new JPanel instance for use in an options menu.
        JPanel options = new JPanel();

        //Sets the layout of the JFrame to a {@link BorderLayout}
        frame.setLayout(new BorderLayout());

        //Sets the width and height of the JFrame
        frame.setSize(width, height);

        //Sets the default close operation for the JFrame
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //Creates a new instance of the ClothSimulation.
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

        //Add the ClothSimulation JPanel to the appropriate place within the JFrame.
        frame.add(clothSimulation, BorderLayout.CENTER);

        //Add the options JPanel to the appropriate place within the JFrame.
        frame.add(options, BorderLayout.SOUTH);

        //Slider visuals
        setupSliderVisuals(windSlider, "Wind", 1, 0);
        setupSliderVisuals(gravitySlider, "Gravity", 1, (int)(clothSimulation.cloth.getGravityStrength()*10));
        setupSliderVisuals(dampeningSlider, "Dampening", 10, (int)(clothSimulation.cloth.getDampeningCoeff()*100));

        //Slider Text
        setupSliderTextVisuals(windText);
        setupSliderTextVisuals(gravityText);
        setupSliderTextVisuals(dampeningText);

        //Button visuals
        setupButtonVisuals(toggleShowJunctions);
        setupButtonVisuals(toggleShowConnectors);
        setupButtonVisuals(toggleShowStress);
        setupButtonVisuals(selectJunctionColor);
        setupButtonVisuals(selectConnectorColor);

        //Sets the background of the options JPanel to black
        options.setBackground(Color.BLACK);

        //Sets the layout of the options' menu.
        options.setLayout(new GridLayout(6,2));

        //Adds all the options to the options JPanel.
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

        //Pack the frame.
        frame.pack();

        //Set the frame to visible.
        frame.setVisible(true);

        //Return the instance of the current ClothSimulation.
        return clothSimulation;
    }

    /**
     *
     * @param slider
     * @param name
     * @param tickSpacing
     * @param initialValue
     */
    public void setupSliderVisuals(JSlider slider, String name, int tickSpacing, int initialValue){
        slider.setMajorTickSpacing(tickSpacing);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setValue(initialValue);
        slider.setToolTipText(name);
        slider.setBackground(Color.BLACK);
        slider.setForeground(Color.WHITE);
        Dictionary dictionary2 = slider.getLabelTable();
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
    }

    /**
     *
     * @param label
     */
    public void setupSliderTextVisuals(JLabel label){
        label.setFont(new Font("ClearSans", Font.BOLD, 20));
        label.setBackground(Color.BLACK);
        label.setForeground(Color.WHITE);
        label.setOpaque(true);
    }

    /**
     *
     * @param button
     */
    public void setupButtonVisuals(JButton button){
        button.setFont(new Font("ClearSans", Font.BOLD, 20));
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setFocusable(false);
    }

}
