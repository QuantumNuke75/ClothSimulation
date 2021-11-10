package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;

public class Start {
    JSlider windSlider = new JSlider(-10, 10);
    JSlider gravitySlider = new JSlider(-10, 10);
    JSlider dampeningSlider = new JSlider(0, 100);
    JSlider maxStressSlider = new JSlider(0, 1000);
    JLabel windText = new JLabel("Wind", SwingConstants.CENTER);
    JLabel gravityText = new JLabel("Gravity", SwingConstants.CENTER);
    JLabel dampeningText = new JLabel("Dampening", SwingConstants.CENTER);
    JLabel maxStressText = new JLabel("Max Stress", SwingConstants.CENTER);

    JButton toggleShowJunctions = new JButton("Show Junctions: Off");
    JButton toggleShowConnectors = new JButton("Show Connectors: Off");
    JButton toggleShowStress = new JButton("Show Stress: Off");
    JButton toggleShading = new JButton("Show Shading: Off");

    JButton startSimulation = new JButton("Start Simulation");
    JButton createVideo = new JButton("Create Video");

    JButton selectJunctionColor = new JButton("Junction Color Picker");
    JButton selectConnectorColor = new JButton("Connector Color Picker");

    /**
     * The main method.
     *
     * @param args - The command line arguments.
     */
    public static void main(String[] args) {

        //Creates a new static instance of the Start class.
        Variables.start = new Start();

        //Creates an instance of the ClothSimulation.
        Variables.clothSimulation = Variables.start.createWindow(1000, 1000);

        //Begins the simulation.
        Variables.clothSimulation.run();
    }

    /**
     * Creates a new instance of the JFrame and the ClothSimulation.
     *
     * @param width - The width of the window.
     * @param height - The height of the window.
     * @return The new instance of the {@link ClothSimulation}
     */
    public ClothSimulation createWindow(int width, int height) {
        //Create a new JFrame instance.
        Variables.window = new JFrame("Cloth Simulation");

        //Create a new JPanel instance for use in the options menu.
        JPanel options = new JPanel();

        //Sets the layout of the JFrame to a BorderLayout
        Variables.window.setLayout(new BorderLayout());

        //Sets the width and height of the JFrame
        Variables.window.setSize(width, height);

        //Sets the default close operation for the JFrame
        Variables.window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //Creates a new instance of the ClothSimulation.
        Variables.clothSimulation = new ClothSimulation();

        //Start simulation button
        startSimulation.addActionListener(e -> {
            Variables.clothSimulation.togglePause();
            if(Variables.isSimulationPaused)
                startSimulation.setText("Unpause Simulation");
            else
                startSimulation.setText("Pause Simulation");
        });

        //Create video button
        createVideo.addActionListener(e -> {
            Variables.clothSimulation.createAsyncVideoThread();
        });

        //Change listener for Wind slider.
        windSlider.addChangeListener(e -> {
            Variables.windStrengthX = windSlider.getValue() / 10f;
        });

        //Change listener for Gravity slider.
        gravitySlider.addChangeListener(e -> {
            Variables.gravityStrength = gravitySlider.getValue() / 10f;
        });

        //Change listener for Dampening slider.
        dampeningSlider.addChangeListener(e -> {
            Variables.dampeningCoeff = dampeningSlider.getValue() / 100f;
        });

        maxStressSlider.addChangeListener(e -> {
            Variables.maxStress = maxStressSlider.getValue();
        });

        //Change listener for Junction show toggle
        toggleShowJunctions.addActionListener(e -> {
            Variables.drawJunction = !Variables.drawJunction;
            toggleShowJunctions.setText(("Show Junctions: " + (Variables.drawJunction ? "On" : "Off")));
        });

        //Change listener for Connector show toggle
        toggleShowConnectors.addActionListener(e -> {
            Variables.drawConnectors = !Variables.drawConnectors;
            toggleShowConnectors.setText(("Show Connectors: " + (Variables.drawConnectors ? "On" : "Off")));
        });

        //Change listener for Stress show toggle
        toggleShowStress.addActionListener(e -> {
            Variables.showStress = !Variables.showStress;
            toggleShowStress.setText(("Show Stress: " + (Variables.showStress ? "On" : "Off")));
        });

        //Change listener for Shading show toggle
        toggleShading.addActionListener(e -> {
            Variables.showShading = !Variables.showShading;
            toggleShading.setText(("Show Shading: " + (Variables.showShading ? "On" : "Off")));
        });

        //Action listener for opening the color picker for Junctions.
        selectJunctionColor.addActionListener(e -> {
            Variables.junctionColor = JColorChooser.showDialog(Variables.clothSimulation, "Junction Color Picker", null);
        });

        //Action listener for opening the color picker for Connectors.
        selectConnectorColor.addActionListener(e -> {
            Variables.connectorColor = JColorChooser.showDialog(Variables.clothSimulation, "Connector Color Picker", null);
        });

        //Add the ClothSimulation JPanel to the appropriate place within the JFrame.
        Variables.window.add(Variables.clothSimulation, BorderLayout.CENTER);

        //Add the options JPanel to the appropriate place within the JFrame.
        Variables.window.add(options, BorderLayout.SOUTH);

        //Slider visuals
        setupSliderVisuals(windSlider, "Wind", 1, 0);
        setupSliderVisuals(gravitySlider, "Gravity", 1, (int) (Variables.gravityStrength * 10));
        setupSliderVisuals(dampeningSlider, "Dampening", 10, (int) (Variables.dampeningCoeff * 100));
        setupSliderVisuals(maxStressSlider, "Max Stress", 100, (int) (Variables.maxStress));

        //Slider Text
        setupSliderTextVisuals(windText);
        setupSliderTextVisuals(gravityText);
        setupSliderTextVisuals(dampeningText);
        setupSliderTextVisuals(maxStressText);

        //Button visuals
        setupButtonVisuals(toggleShowJunctions);
        setupButtonVisuals(toggleShowConnectors);
        setupButtonVisuals(toggleShowStress);
        setupButtonVisuals(toggleShading);
        setupButtonVisuals(selectJunctionColor);
        setupButtonVisuals(selectConnectorColor);
        setupButtonVisuals(startSimulation);
        setupButtonVisuals(createVideo);

        //Sets the background of the options JPanel to black
        options.setBackground(Color.BLACK);

        //Sets the layout of the options' menu.
        options.setLayout(new GridLayout(8, 2));

        //Adds all the options to the options JPanel.
        //Add wind options
        options.add(windText);
        options.add(windSlider);

        //Add gravity option
        options.add(gravityText);
        options.add(gravitySlider);

        //Add dampening option
        options.add(dampeningText);
        options.add(dampeningSlider);

        //Add stress option
        options.add(maxStressText);
        options.add(maxStressSlider);

        //Toggle options
        options.add(toggleShowJunctions);
        options.add(toggleShowConnectors);
        options.add(toggleShowStress);
        options.add(toggleShading);

        //Color options
        options.add(selectJunctionColor);
        options.add(selectConnectorColor);

        //Start sim
        options.add(startSimulation);

        //Video Creation
        options.add(createVideo);

        //Pack the frame.
        Variables.window.pack();

        //Set the frame to visible.
        Variables.window.setVisible(true);
        Variables.window.setExtendedState(Variables.window.MAXIMIZED_BOTH);

        //Return the instance of the current ClothSimulation.
        return Variables.clothSimulation;
    }

    /**
     * Method to setup all the visual elements of a JSlider.
     *
     * @param slider       - The given JSlider.
     * @param name         - The name of the JSlider.
     * @param tickSpacing  - The interval spacing.
     * @param initialValue - The initial value of the JSlider.
     */
    public void setupSliderVisuals(JSlider slider, String name, int tickSpacing, int initialValue) {
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
     * Method to set up all the visuals for a JLabel.
     *
     * @param label - The given JLabel.
     */
    public void setupSliderTextVisuals(JLabel label) {
        label.setFont(new Font("ClearSans", Font.BOLD, 20));
        label.setBackground(Color.BLACK);
        label.setForeground(Color.WHITE);
        label.setOpaque(true);
    }

    /**
     * Method to set up all the visuals for a JButton.
     *
     * @param button - The given JButton.
     */
    public void setupButtonVisuals(JButton button) {
        button.setFont(new Font("ClearSans", Font.BOLD, 20));
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setFocusable(false);
    }
}
