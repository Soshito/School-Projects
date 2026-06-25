package team8.SeasonPackage;

import team8.BattlePackage.*;
import team8.RandomPackage.*;
import team8.FightPackage.*;
import team8.DamagePackage.*;
import team8.InputOutputPackage.*;
import team8.PlayablePackage.*;
import team8.RoundPackage.*;
import team8.PlayablePackage.*;

import java.util.List;

public class SeasonInitializer
{
    private List<Playable> petList;
    private int numFights;
    private SeasonManager seasonManager;

    /**
     * Basic constructor
     * @param petList - a list of Playables representing the pets in the season
     */
    public SeasonInitializer(List<Playable> petList, int numFights)
    {
        this.petList = petList;
        this.numFights = numFights;
    }

    /**
     * Calls the necessary methods to instantiate and run a season
     */
    public void runSeason()
    {
        seasonManager = new SeasonManager(new Season(), petList, numFights);
        seasonManager.roundRobin();
        seasonManager.runBattles();
    }
}
