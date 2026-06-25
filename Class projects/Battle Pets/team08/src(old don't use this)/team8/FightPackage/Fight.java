package team8.FightPackage;

import java.util.ArrayList;
import java.util.List;
import team8.PlayablePackage.*;


/**
 * Entity class to hold the necessary data for fights
 */
public class Fight
{
    private List<Playable> petList;
    private List<Playable> winners = new ArrayList<>();
    private boolean isOver;

    /**
     * constructor for a fight
     * @param petList - the Playables participating in the fight
     */
    public Fight(List<Playable> petList)
    {
        this.petList = petList;
    }

    /**
     * returns the list of Playables participating in the fight
     * @return - the list of Playables participating in the fight
     */
    public List<Playable> getPetList() {
        return petList;
    }

    /**
     * returns the list of Playables that tracks the winner(s) of a fight
     * @return the list of Playables that won the fight
     */
    public List<Playable> getWinner() {
        return winners;
    }

    /**
     * Adds a Playable to the winners list
     * @param winner - a Playable that win the fight
     */
    public void addWinner(Playable winner) {
        this.winners.add(winner);
    }

    /**
     * returns whether the fight is over as a boolean
     * @return a boolean representing whether the fight is over
     */
    public boolean getIsOver() {
        return isOver;
    }

    /**
     * Updates the attribute that tracks whether the fight is over
     * @param over - a boolean representing whether the fight is over
     */
    public void setIsOver(boolean over) {
        isOver = over;
    }
}