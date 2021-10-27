package main;

import javax.swing.*;
import java.awt.*;

public class Variables {

    //Final Variables
    public static final int SIMULATION_WIDTH = 1000;
    public static final int SIMULATION_HEIGHT = SIMULATION_WIDTH;

    public static final int JUNCTION_COUNT_X = 21;
    public static final int JUNCTION_COUNT_Y = 21;
    public static final int JUNCTION_DISTANCE = 20;

    //One-change Variables
    public static ClothSimulation clothSimulation;
    public static Cloth cloth;
    public static Start start;
    public static JFrame window;

    //Settings Variables
    public static float windStrengthX = 0.0f;
    public static float gravityStrength = 0.3f;
    public static float dampeningCoeff = 0.99f;
    public static float newWindStrengthX;
    public static double maxStress = 225;

    public static boolean drawJunction = false;
    public static boolean drawConnectors = true;
    public static boolean showStress = true;
    public static boolean showShading = false;

    public static Color junctionColor = Color.BLUE;
    public static Color connectorColor = Color.GREEN;




}
