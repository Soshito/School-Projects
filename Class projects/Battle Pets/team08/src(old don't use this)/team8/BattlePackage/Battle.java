package team8.BattlePackage;

import java.util.ArrayList;
import java.util.List;
import team8.BattlePackage.*;
import team8.RandomPackage.*;
import team8.FightPackage.*;
import team8.DamagePackage.*;
import team8.InputOutputPackage.*;
import team8.PlayablePackage.*;
import team8.RoundPackage.*;

/**
 * Entity class to hold the necessary data for battles
 */
public class Battle
{
    private List<Playable> petList;
    private Playable winner;
    private List<Playable> winners = new ArrayList<>(1);
    private List<List<Playable>> fightWinners = new ArrayList<>();
    private int numOfFights;
    private RandomSingleton random = RandomSingleton.INSTANCE;

    /**
     * Constructor for constructing a pet list and number of fights
     * @param petList - petList of type team8.PlayablePackage.Playable to keep track of pets
     * @param numOfFights - numOfFights to keep track of the number of fights
     */
    public Battle(List<Playable> petList, int numOfFights)
    {
        this.petList = petList;
        this.numOfFights = numOfFights;
    }

    /**
     * @return Returns the winner of the battle
     */
    public Playable getWinner()
    {
        return winner;
    }

    /**
     * Sets the winner of the battle
     * @param winner - winner of type team8.PlayablePackage.Playable
     */
    public void setWinner(Playable winner)
    {
        this.winner = winner;
    }

    /**
     * Sets the pet list of type team8.PlayablePackage.Playable
     * @param petList - Pet list of type team8.PlayablePackage.Playable
     */
    public void setPetList(List<Playable> petList)
    {
        this.petList = petList;
    }

    /**
     * @return Returns the pet list so that we can keep track of the pets in the list
     */
    public List<Playable> getPetList()
    {
        return petList;
    }

    /**
     * Sets the number of fights for a given battle
     * @param numOfFights - The number of fights for a battle of type integer
     */
    public void setNumOfFights(int numOfFights)
    {
        this.numOfFights = numOfFights;
    }

    /**
     * @return Returns the number of fights for a given battle
     */
    public int getNumOfFights()
    {
        return numOfFights;
    }

    /**
     * @return Returns the winners of fights for a given battle
     */
    public List<List<Playable>> getFightWinners() {
        return fightWinners;
    }


    /**
     * @return Returns the winner(s) of a battle
     */
    public List<Playable> getWinners() {
        return winners;
    }

    /**
     * adds a winning Playable to the winners list
     * @param winner - a Playable that won the battle
     */
    public void addWinner(Playable winner) {
        this.winners.add(winner);
    }
}