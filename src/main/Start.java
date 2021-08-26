package main;

import javax.swing.*;
import java.applet.Applet;
import java.awt.*;
import java.util.Dictionary;
import java.util.Enumeration;

public class Start extends Applet {

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
        frame = new JFrame("Cloth Simulationr");
        JPanel options = new JPanel();
        frame.setLayout(new BorderLayout());
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        clothSimulation = new ClothSimulation(junctionCountX, junctionCountY);

        //Change listener for Wind slider.
        windSlider.addChangeListener(e -> {
            float windStrengthX = windSlider.getValue()/10f;
            clothSimulation.cloth.setWindStrengthX(windStrengthX);
        });

        //Change listener for Gravity slider.
        gravitySlider.addChangeListener(e -> {
            float gravityStrength = gravitySlider.getValue()/10f;
            clothSimulation.cloth.setGravityStrength(gravityStrength);
        });

        //Change listener for Dampening slider.
        dampeningSlider.addChangeListener(e -> {
            float dampeningStrength = dampeningSlider.getValue()/100f;
            clothSimulation.cloth.setDampeningCoeff(dampeningStrength);
        });

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

        options.setLayout(new GridLayout(6,1));
        options.add(windText);
        options.add(windSlider);
        options.add(gravityText);
        options.add(gravitySlider);
        options.add(dampeningText);
        options.add(dampeningSlider);

        frame.pack();
        frame.setVisible(true);
        return clothSimulation;
    }
}
