package team8.InputOutputPackage;

import java.lang.*;
import java.util.InputMismatchException;
import java.util.Scanner;
import team8.BattlePackage.*;
import team8.RandomPackage.*;
import team8.FightPackage.*;
import team8.DamagePackage.*;
import team8.InputOutputPackage.*;
import team8.PlayablePackage.*;
import team8.RoundPackage.*;

/**
 * Boundary class to handle all input gathering from the user(s)
 */
public enum InputManager {
    INPUT;
    private Scanner scan = new Scanner(System.in);
    private OutputManager outputManager = OutputManager.OUTPUT;

    //Doesn't have exception handling because anything can be a string

    /**
     * Gets a String input from the user
     * @return String from user input
     */
    public String getInput() {
        scan.useDelimiter("\n");
        String string = scan.next().trim();
        while(string.isEmpty())
        {
            outputManager.printOutput("You must have an input.");
            string = scan.next().trim();
        }
        return string;
    }

    /**
     * Gets an int input from the user
     * @return int from user input
     */
    public int getInt() {
        Integer integer = null;
        while(integer == null)
        {
            try
            {
                integer = scan.nextInt();
            }
            catch (InputMismatchException e)
            {
                outputManager.printOutput("You must enter an integer.");
                scan.next();
            }
        }

        return integer;
    }

    /**
     * Gets a double input from the user
     * @return double from user input
     */
    public double getDouble() {
        Double dub = null;
        while(dub == null)
        {
            try
            {
                dub = scan.nextDouble();
            }
            catch(InputMismatchException e)
            {
                outputManager.printOutput("You must enter a double.");
                scan.next();
            }

        }
        return dub;
    }

    InputManager() {

    }
}