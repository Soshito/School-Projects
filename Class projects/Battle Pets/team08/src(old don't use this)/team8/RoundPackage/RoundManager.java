package team8.RoundPackage;

import team8.BattlePackage.*;
import team8.RandomPackage.*;
import team8.FightPackage.*;
import team8.DamagePackage.*;
import team8.InputOutputPackage.*;
import team8.PlayablePackage.*;
import team8.RoundPackage.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Control class to handle the business logic for rounds
 */
public class RoundManager
{
    private InputManager inputManager = InputManager.INPUT;

    private OutputManager outputManger = OutputManager.OUTPUT;

    private Round round;
    private DamageManager damageManager;

    public RoundManager(List<Playable> petList)
    {
        this.damageManager = new DamageManager();
        this.round = new Round(petList);
    }
    public Round getRound()
    {
        return this.round;
    }

    /**
     * iterates through all the pets in the round and updates their HP according to the damage dealt
     */
    private void doDamage()
    {
        for(int i = 0; i < round.getPetList().size(); i++)
        {
            Pet pet = (Pet) round.getPetList().get(i);
            if(i == 0) // checks for wrap around on first pet
            {
                round.getPetList().get(i).setCurrentHp(round.getPetList().get(i).getCurrentHp() - round.getRoundDamage().get(round.getPetList().size() - 1).calculateTotalDamage());
                pet.setRandomDamageDifference(pet.getRandomDamageDifference() + (round.getRoundDamage().get(i).getRandomDamage() - round.getRoundDamage().get(round.getPetList().size() - 1).getRandomDamage()));
            }
            else
            {
                round.getPetList().get(i).setCurrentHp(round.getPetList().get(i).getCurrentHp() - round.getRoundDamage().get(i - 1).calculateTotalDamage());
                pet.setRandomDamageDifference(pet.getRandomDamageDifference() + (round.getRoundDamage().get(i).getRandomDamage() - round.getRoundDamage().get(i - 1).getRandomDamage()));
            }
        }
    }


    /**
     * Prints the output for the start of the round
     */
    private void startOfRoundPrints()
    {
        double highestHP = -Double.MAX_VALUE;
        List<Playable> orderedPetList = new ArrayList<>();
        List<Playable> tempPetList = new ArrayList<>();
        for(Playable pet: round.getPetList())
        {
            tempPetList.add(pet);
        }
        while(! tempPetList.isEmpty())
        {
            for(Playable pet: tempPetList)
            {
                if(pet.getCurrentHp() > highestHP)
                {
                    highestHP = pet.getCurrentHp();
                }
            }
            Iterator<Playable> iterator = tempPetList.iterator();
            while (iterator.hasNext())
            {
                Playable pet = iterator.next();
                if (pet.getCurrentHp() == highestHP) {
                    orderedPetList.add(pet);
                    iterator.remove();
                    highestHP = -Double.MAX_VALUE;
                    for(Playable pet2: tempPetList)
                    {
                        if(pet2.getCurrentHp() > highestHP)
                        {
                            highestHP = pet2.getCurrentHp();
                        }
                    }
                }
            }
        }
        for(int j = 0; j < orderedPetList.size(); j++)
            {
                outputManger.printOutput(orderedPetList.get(j).getPetName());
                outputManger.printOutput("\nCurrent HP: ");
                outputManger.printOutput(String.valueOf(orderedPetList.get(j).getCurrentHp()));
                outputManger.printOutput("\nSkill recharge times:\nRock Throw: ");
                outputManger.printOutput(String.valueOf(orderedPetList.get(j).getSkillRechargeTime(Skills.ROCK_THROW)));
                outputManger.printOutput("\nScissors Poke: ");
                outputManger.printOutput(String.valueOf(orderedPetList.get(j).getSkillRechargeTime(Skills.SCISSORS_POKE)));
                outputManger.printOutput("\nPaper Cut: ");
                outputManger.printOutput(String.valueOf(orderedPetList.get(j).getSkillRechargeTime(Skills.PAPER_CUT)));
                outputManger.printOutput("\nShoot the Moon: ");
                outputManger.printOutput(String.valueOf(orderedPetList.get(j).getSkillRechargeTime(Skills.SHOOT_THE_MOON)));
                outputManger.printOutput("\nReversal of Fortune: ");
                outputManger.printOutput(String.valueOf(orderedPetList.get(j).getSkillRechargeTime(Skills.REVERSAL_OF_FORTUNE)));
                outputManger.printOutput("\n\n");
            }
    }

    /**
     * Prints the output for the end of the round
     * @param skillChoices - list of skills chosem during the round
     */
    private void endOfRoundPrints(List<Skills> skillChoices)
    {
        outputManger.printOutput("Skill choices:\n");
        for(int k=0; k < round.getPetList().size(); k++)
        {
            Pet pet = (Pet) round.getPetList().get(k);
            outputManger.printOutput(round.getPetList().get(k).getPetName());
            outputManger.printOutput(" chose ");
            outputManger.printOutput(String.valueOf(skillChoices.get(k)));
            outputManger.printOutput(" which did ");
            outputManger.printOutput(String.valueOf(damageManager.getDamage().get(k).getRandomDamage()));
            outputManger.printOutput(" random damage, ");
            outputManger.printOutput(String.valueOf(damageManager.getDamage().get(k).getConditionalDamage()));
            outputManger.printOutput(" conditional damage, and ");
            outputManger.printOutput(String.valueOf(damageManager.getDamage().get(k).calculateTotalDamage()));
            outputManger.printOutput(" total damage. Current random damage difference: ");
            outputManger.printOutput(String.valueOf(pet.getRandomDamageDifference()));
            outputManger.printOutput(".\n");
        }
        outputManger.printOutput("\n");
    }

    /**
     * iterates through the pet list and gives a turn to each pet
     */
    public void runTurns() // make a new petList each round that is only the awake pets!
    {
        startOfRoundPrints();
        List<Skills> skillChoices = new ArrayList<>();
        for(int i = 0; i < round.getPetList().size(); i++)
        {
            outputManger.printOutput(round.getPetList().get(i).getPetName());
            outputManger.printOutput("'s turn.\n");
            skillChoices.add(i, round.getPetList().get(i).chooseSkill());
        }
        damageManager.calculateDamage(round.getPetList(), skillChoices);
        round.setRoundDamage(this.damageManager.getDamage());
        doDamage();
        for(int j = 0; j < round.getPetList().size();j++)
        {
            if(skillChoices.get(j) == Skills.SHOOT_THE_MOON || skillChoices.get(j) == Skills.REVERSAL_OF_FORTUNE)
            {
                round.getPetList().get(j).setRechargeTime(skillChoices.get(j), 7);
            }
            else if(skillChoices.get(j) == Skills.ROCK_THROW || skillChoices.get(j) == Skills.SCISSORS_POKE || skillChoices.get(j) == Skills.PAPER_CUT)
            {
                round.getPetList().get(j).setRechargeTime(skillChoices.get(j),2 );
            }
        }
        endOfRoundPrints(skillChoices);
        for(Playable pet: round.getPetList())
        {
            pet.decrementRechargeTimes();
        }
    }
}