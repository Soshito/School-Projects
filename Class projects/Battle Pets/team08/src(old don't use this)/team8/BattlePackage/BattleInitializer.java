package team8.BattlePackage;

import team8.BattlePackage.Battle;
import team8.BattlePackage.BattleManager;
import team8.InputOutputPackage.InputManager;
import team8.InputOutputPackage.OutputManager;
import team8.PlayablePackage.*;
import team8.RandomPackage.RandomSingleton;

import java.util.*;

public class BattleInitializer
{
    private InputManager inputManager = InputManager.INPUT;
    private OutputManager outputManager = OutputManager.OUTPUT;
    private RandomSingleton random = RandomSingleton.INSTANCE;
    private List<Playable> petList;




    /**
     * basic constructor
     */
    public BattleInitializer(List<Playable> petList) {
        this.petList = petList;
    }

    /**
     * Takes input from user to build a game based off the specified parameters
     */
    public Battle setParams()
    {
        int numPlaying = petList.size();
        return fightSetup(numPlaying);
    }



    /**
     * used when initializing the fights
     * @param numPlaying - an int representing the number of players
     * @return - the initialized battle
     */
    private Battle fightSetup(int numPlaying) {
        outputManager.printOutput("Enter the number of fights for the battle--> ");
        int numFightsChoice = inputManager.getInt();
        while(numFightsChoice < 1)
        {
            outputManager.printOutput("You must have at least 1 fight.\n");
            numFightsChoice = inputManager.getInt();
        }
        int numFights = numFightsChoice;
        outputManager.printOutput("Enter the random seed for the battle--> ");
        int randomSeed = inputManager.getInt();
        random.setRandom(new Random(randomSeed));
        outputManager.printOutput("\n\n");
        return new Battle(petList, numFights);
    }


}
