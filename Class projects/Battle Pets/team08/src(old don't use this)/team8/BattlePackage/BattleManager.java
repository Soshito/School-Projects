package team8.BattlePackage;

import team8.BattlePackage.*;
import team8.RandomPackage.*;
import team8.FightPackage.*;
import team8.DamagePackage.*;
import team8.InputOutputPackage.*;
import team8.PlayablePackage.*;
import team8.RoundPackage.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Control class to handle the business logic of battles
 */
public class BattleManager
{
    private InputManager inputManager = InputManager.INPUT;
    private OutputManager outputManager = OutputManager.OUTPUT;
    private Battle battle;
    private int[] petWins;
    /**
     * Constructs a given battle
     * @param battle A battle of type battle
     */
    public BattleManager(Battle battle)
    {
        this.battle = battle;
        this.petWins = new int[battle.getPetList().size()];
    }

    /**
     * @return Returns a battle
     */
    public Battle getBattle()
    {
        return battle;
    }

    /**
     * Runs a given fight. Displays the fight number, pet names, and pet types. Runs while the fight
     * isn't over, calls runRounds while the fight is not over in order to run the rounds of the fight.
     * Displays the winner(s) at the end of the fight.
     * @param fightNum - The fight number of type integer
     * @return Returns the winner of a fight
     */
    public List<Playable> runFight(int fightNum)
    {
        outputManager.printOutput("Fight number ");
        outputManager.printOutput(String.valueOf(fightNum));
        outputManager.printOutput("\n\nPets:\n");
        outputManager.printOutput(battle.getPetList().get(0).getPetName());
        outputManager.printOutput(" (");
        outputManager.printOutput(String.valueOf(battle.getPetList().get(0).getPetType()));
        outputManager.printOutput(")\n");
        outputManager.printOutput(battle.getPetList().get(1).getPetName());
        outputManager.printOutput(" (");
        outputManager.printOutput(String.valueOf(battle.getPetList().get(1).getPetType()));
        outputManager.printOutput(")\n\n");
        Fight fight = new Fight(battle.getPetList());
        FightManager fightManager = new FightManager(fight);
        while(!fight.getIsOver())
        {
            fightManager.runRounds();
        }
        if(fight.getWinner().size() > 1)
        {
            for(int i = 0; i < fight.getWinner().size(); i++)
            {
                if(i == fight.getWinner().size() - 1)
                {
                    outputManager.printOutput(fight.getWinner().get(1).getPetName());
                }
                else
                {
                    outputManager.printOutput(fight.getWinner().get(i).getPetName() + " and ");
                }
            }
            outputManager.printOutput(" are the winners of the fight!\n\n");
        }
        else
        {
            outputManager.printOutput(fight.getWinner().get(0).getPetName());
            outputManager.printOutput(" is the winner of the fight!\n\n");
        }
        return fight.getWinner();
    }

    /**
     * work-around for UUID not working in season mode
     * @param pet - Playable representing the pet whose place you want to find in the petList
     * @return an int representing the pet's place in the list
     */
    public int getPlace(Playable pet)
    {
        int place = 0;
        for(int i = 0; i < battle.getPetList().size();i++)
        {
            if(battle.getPetList().get(i).equals(pet))
            {
                place = i;
            }
        }
        return place;
    }

    /**
     * Calculates the winner of a battle by determining which pet won more fights. If they won the same
     * number of fights, then both pets win.
     */
    public void calculateWinner()
    {
        for(List<Playable> fightWinners:battle.getFightWinners())
        {
            for(Playable pet: fightWinners)
            {
                petWins[getPlace(pet)] += 1;
            }
        }
        int currentMost = 0;
        for(int numWins: petWins)
        {
            if(numWins > currentMost)
            {
                currentMost = numWins;
            }
        }
        for(int i = 0; i < petWins.length; i++)
        {
            if(petWins[i] == currentMost)
            {
                battle.addWinner(battle.getPetList().get(i));
            }
        }
    }

    /**
     * Runs the fights until the number of fights has been reached. Records the winner of each fight.
     * Once the number of set fights has been reached, the battle winner(s) is displayed along with
     * the number of fights that each pet won. The user is then prompted if they would like to play
     * again or not.
     * @return Returns the user's choice on if they would like to play again or not
     */
    public void runFights()
    {
        for(int i = 0; i < battle.getNumOfFights(); i++)
        {
            battle.getFightWinners().add(i, runFight(i+1));
        }
        calculateWinner();
        if(battle.getWinners().size() > 1)
        {
            for(int j = 0; j < battle.getWinners().size();j++)
            {
                if(j == battle.getWinners().size() - 1)
                {
                    outputManager.printOutput(String.valueOf(battle.getWinners().get(j).getPetName()));
                }
                else
                {
                    outputManager.printOutput(battle.getWinners().get(j).getPetName() + " and ");
                }
            }
            outputManager.printOutput(" won the battle!\n\nNumber of fights won for each pet:\n");
            for(Playable pet: battle.getPetList())
            {
                outputManager.printOutput(pet.getPetName());
                outputManager.printOutput(" won ");
                outputManager.printOutput(String.valueOf(petWins[getPlace(pet)]));
                outputManager.printOutput(" fights!\n");
            }
        }
        else
        {
            outputManager.printOutput(String.valueOf(battle.getWinners().get(0).getPetName()));
            outputManager.printOutput(" won the battle!\n\nNumber of fights won for each pet:\n");
            for(Playable pet: battle.getPetList())
            {
                outputManager.printOutput(pet.getPetName());
                outputManager.printOutput(" won ");
                outputManager.printOutput(String.valueOf(petWins[getPlace(pet)]));
                outputManager.printOutput(" fights!\n");
            }
            System.out.println("\n");
        }
    }

}