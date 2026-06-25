package team8.InputOutputPackage;

import java.lang.*;
import team8.BattlePackage.*;
import team8.RandomPackage.*;
import team8.FightPackage.*;
import team8.DamagePackage.*;
import team8.InputOutputPackage.*;
import team8.PlayablePackage.*;
import team8.RoundPackage.*;

/**
 * Boundary class that handles output to the user(s)
 */
public enum OutputManager {
    OUTPUT;
    private String output;

    /**
     * Default Constructor
     */
    OutputManager() {
    }
    /**
     * Method that prints out any String that it is given
     * @param output
     */
    public void printOutput(String output) {
        System.out.print(output);
    }
}
