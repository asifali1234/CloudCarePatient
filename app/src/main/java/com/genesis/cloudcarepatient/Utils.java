package com.genesis.cloudcarepatient;

/**
 *
 */
public class Utils {
    public static String getStringActivity(int key) {
        switch (key) {
            case 0:
                return "In vehicle";
            case 1:
                return "On bicycle";
            case 2:
                return "On foot";
            case 3:
                return "Still";
            case 4:
                return "Drunk";
            case 5:
                return "Tilting";
            default:
                return "Drunk";
        }
    }
}

