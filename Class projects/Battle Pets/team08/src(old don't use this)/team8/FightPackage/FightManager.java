package team8.FightPackage;

import team8.InputOutputPackage.*;
import team8.PlayablePackage.*;
import team8.RoundPackage.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Control class to handle the business logic for fights
 */
public class FightManager
{
    private InputManager inputManager = InputManager.INPUT;
    private OutputManager outputManager = OutputManager.OUTPUT;
    private Fight fight;

    /**
     * constructor for FightManagers
     * @param fight - the fight entity that the FightManager will manage
     */
    public FightManager(Fight fight)
    {
        this.fight = fight;
    }

    /**
     * @return Returns the fight entity being managed by this FightManager
     */
    public Fight getFight()
    {
        return fight;
    }

    /**
     * runs rounds until 1 or less pet(s) are awake then calculates the winner and resets all pets attributes
     */
    public void runRounds()
    {
        boolean allDiedAtOnce = false;
        int count = 1;
        int awakeCount = fight.getPetList().size();
        List<Playable> eligablePlayableWinners = new ArrayList<>();
        for(Playable pet: fight.getPetList())
        {
            eligablePlayableWinners.add(pet);
        }
        //eligablePlayableWinners.addAll(fight.getPetList());
        while(awakeCount >= 2)
        {
            List<Playable> lastEligableWinners = new ArrayList<>();
            for(Playable pet: eligablePlayableWinners)
            {
                lastEligableWinners.add(pet);
            }
            runRound(count);
            count += 1;
            awakeCount = 0;
            for(Playable pet: fight.getPetList())
            {
                if(pet.isAwake()){
                    awakeCount += 1;
                }
                else {
                    eligablePlayableWinners.remove(pet);
                }
            }
            if(awakeCount == 0)
            {
                allDiedAtOnce = true;
                calculateWinner(lastEligableWinners);
            }
        }
        if (! allDiedAtOnce)
        {
            calculateWinner(eligablePlayableWinners);
        }
        fight.setIsOver(true);
        for(Playable pet:fight.getPetList())
        {
            pet.reset();
        }
    }

    /**'
     * runs a single round, outputing the round number at the start
     * @param roundNum - the current round number for this fight
     */
    public void runRound(int roundNum)
    {
        outputManager.printOutput("Round number ");
        outputManager.printOutput(String.valueOf(roundNum));
        outputManager.printOutput(".\n\n");
        List<Playable> thisRoundPetList = new ArrayList<>();
        for(Playable pet: fight.getPetList())
        {
            if(pet.isAwake()){
                thisRoundPetList.add(pet);
            }
        }
        RoundManager roundManager = new RoundManager(thisRoundPetList);
        roundManager.runTurns();
    }

    /**
     * calculates the winner(s) by comparing the highest remaining HPs from pets awake at the start of the last round
     * @param possibleWinners - a list of PLayables awake at the start of the last round
     */
    public void calculateWinner(List<Playable> possibleWinners)
    {
        double highestRemainingHp = -Double.MAX_VALUE;
        for(Playable pet: possibleWinners)
        {
            if(pet.getCurrentHp() > highestRemainingHp)
            {
                highestRemainingHp = pet.getCurrentHp();
            }
        }
        for(Playable pet: possibleWinners)
        {
            if(pet.getCurrentHp() >= highestRemainingHp)
            {
                fight.addWinner(pet);
            }
        }
    }
}