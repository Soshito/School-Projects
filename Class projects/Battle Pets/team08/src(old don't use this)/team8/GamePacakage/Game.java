package team8.GamePacakage;

import team8.BattlePackage.*;
import team8.FightPackage.FightInitializer;
import team8.InputOutputPackage.InputManager;
import team8.InputOutputPackage.OutputManager;
import team8.SeasonPackage.*;
import team8.PlayablePackage.Pet;
import team8.PlayablePackage.PetTypes;
import team8.PlayablePackage.Playable;
import team8.PlayablePackage.Player;
import team8.RandomPackage.RandomSingleton;

import java.util.*;

/**
 * Control class to handle the initialization of the game
 */
public class Game
{
    private InputManager inputManager = InputManager.INPUT;
    private OutputManager outputManager = OutputManager.OUTPUT;
    private RandomSingleton random = RandomSingleton.INSTANCE;
    private List<Playable> petList;
    private GameInitializer gameInitializer = new GameInitializer();


    /**
     * Basic constructor
     */
    public Game()
    {
    }

    /**
     * Prints the start of game prints and initializes the correct exhibition match based on user input
     */
    public void runGame()
    {
        gameInitializer.setParams();
        petList = gameInitializer.getPetList();
        int choice2 = 1;
        int choice = 0;
        while(choice2 == 1)
        {
            outputManager.printOutput("What game mode would you like to play?\n[1]--> Fight\n[2]--> Battle\n[3]--> Season\n\n");
            choice =inputManager.getInt();
            while(choice >= 1 && choice <= 3)
            {
                if(choice == 1)
                {
                    outputManager.printOutput("Enter the random seed for the fight--> ");
                    int randomSeed = inputManager.getInt();
                    random.setRandom(new Random(randomSeed));
                    outputManager.printOutput("\n\n");
                    runFight();
                    choice = 0;
                }
                else if(choice == 2)
                {
                    runBattle();
                    choice = 0;
                }
                else if(choice == 3)
                {
                    outputManager.printOutput("Enter the random seed for the season--> ");
                    int randomSeed = inputManager.getInt();
                    random.setRandom(new Random(randomSeed));
                    outputManager.printOutput("Enter the number of fights per battle--> ");
                    int numFights = inputManager.getInt();
                    outputManager.printOutput("\n\n");
                    runSeason(numFights);
                    choice = 0;
                }
            }
            outputManager.printOutput("\n\nWould you like to play again?\n[1]--> yes\n[2]--> no\n");
            choice2 = inputManager.getInt();
        }
    }


    /**
     * Initializes and runs a battle
     */
    public void runBattle()
    {
        BattleInitializer battleInitializer = new BattleInitializer(petList);
        BattleManager battleManager = new BattleManager(battleInitializer.setParams());
        battleManager.runFights();
    }

    /**
     * Initializes and runs a fight
     */
    public void runFight()
    {
        FightInitializer fightInitializer = new FightInitializer(petList);
        fightInitializer.runFight();
    }

    /**
     * Initializes and runs a season
     */
    public void runSeason(int numFights)
    {
        SeasonInitializer seasonInitializer = new SeasonInitializer(petList, numFights);
        seasonInitializer.runSeason();
    }

}