package main;

public class Utils {
    /**
     * Converts FPS to MSPF.
     * @param fps - The FPS value to convert to MSPF.
     * @returns the MSPF value.
     */
    public static long FPStoMSPF(double fps){
        // frames/second TO milliseconds/frame
        return (long) ((1/fps) * 1000);
    }

    /**
     * Converts MSPF to FPS.
     * @param ms - The MSPF value to convert to FPS.
     * @returns the FPS value.
     */
    public static double MSPFtoFPS(double ms){
        // milliseconds/frame to frames/second
        return 1/(ms/1000);
    }
}
