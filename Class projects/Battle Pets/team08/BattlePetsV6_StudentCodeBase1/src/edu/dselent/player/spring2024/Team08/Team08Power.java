package edu.dselent.player.spring2024.Team08;


import edu.dselent.player.PetTypes;
import edu.dselent.player.Playable;
import edu.dselent.player.defaultintelligence.*;
import edu.dselent.skill.Skills;

import java.util.ArrayList;
import java.util.List;

public class Team08Power
{
    private List<Skills> enemyChoosableSkills;
    private List<Skills> ourChoosableSkills = new ArrayList<>();
    private List<Skills> skillList = new ArrayList<>();
    private Playable ourPet;
    private double enemyCurrentHp;
    private PetTypes enemyType;
    private double reversalOfFortuneDamage;

    /**
     * Basic constructor
     * @param ourPet - our AI pet
     * @param enemyCurrentHp - the current HP of the pet we are attacking
     * @param enemyChoosableSkills - a list of Skills representing the skills available to the pet we are attacking
     * @param enemyType - the PetTypes type of the pet we are attacking
     */
    public Team08Power(Playable ourPet, double enemyCurrentHp, List<Skills> enemyChoosableSkills, PetTypes enemyType)
    {
        this.ourPet = ourPet;
        this.enemyCurrentHp = enemyCurrentHp;
        this.enemyChoosableSkills = enemyChoosableSkills;
        this.enemyType = enemyType;
    }

    /**
     * Method called to update the values of all lists used to make logical decisions
     */
    public void setSkillLists()
    {
        Team08AI forDamage = (Team08AI) ourPet;
        reversalOfFortuneDamage = forDamage.getDamageDifference();
        Pet inCase = (Pet) ourPet;
        this.skillList.add(Skills.ROCK_THROW);
        this.skillList.add(Skills.SCISSORS_POKE);
        this.skillList.add(Skills.PAPER_CUT);
        this.skillList.add(Skills.SHOOT_THE_MOON);
        this.skillList.add(Skills.REVERSAL_OF_FORTUNE);
        for(Skills skill: skillList)
        {
            if(ourPet.getSkillRechargeTime(skill) < 1)
            {
                ourChoosableSkills.add(skill);
            }
        }
        if(enemyType == PetTypes.INTELLIGENCE && ourPet.calculateHpPercent() > 0.1)
        {
            boolean isItThere = false;
            for(Skills skill : ourChoosableSkills)
            {
                if(skill == Skills.SHOOT_THE_MOON)
                {
                    isItThere = true;
                }
            }
            if(isItThere)
            {
                ourChoosableSkills.remove(Skills.SHOOT_THE_MOON);
            }
        }
        if(reversalOfFortuneDamage < 0)
        {
            boolean isItThere = false;
            for(Skills skill : ourChoosableSkills)
            {
                if(skill == Skills.REVERSAL_OF_FORTUNE)
                {
                    isItThere = true;
                }
            }
            if(isItThere)
            {
                ourChoosableSkills.remove(Skills.REVERSAL_OF_FORTUNE);
            }
        }
    }


    /**
     * Method called when the pet we are attacking is either a POWER or INTELLIGENCE type
     * @return the skill we will choose
     */
    public Skills decideSkillVsPowerOrIntelligence()
    {
        Skills skillChoice = null;
        setSkillLists();
        boolean isROF = false;
        boolean isSTM = false;
        List<Skills> checkLastPlay = new ArrayList<>();
        for(Skills skill : ourChoosableSkills)
        {
            if(skill == Skills.SHOOT_THE_MOON)
            {
                isSTM = true;
            }
            else if(skill == Skills.REVERSAL_OF_FORTUNE)
            {
                isROF = true;
            }
            else
            {
                checkLastPlay.add(skill);
            }
        }
        if(checkLastPlay.size() == 2)
        {
            boolean isRock = true;
            boolean isScissors = true;
            boolean isPaper = true;
            for(Skills skill : checkLastPlay)
            {
                if(skill == Skills.ROCK_THROW)
                {
                    isRock = false;
                }
                else if(skill == Skills.SCISSORS_POKE)
                {
                    isScissors = false;
                }
                else if(skill == Skills.PAPER_CUT)
                {
                    isPaper = false;
                }
            }
            if(isRock)
            {
                skillChoice = Skills.SCISSORS_POKE;
            }
            else if(isScissors)
            {
                skillChoice = Skills.PAPER_CUT;
            }
            else if(isPaper)
            {
                skillChoice = Skills.ROCK_THROW;
            }
        }
        else
        {
            skillChoice = Skills.ROCK_THROW; // this can be anything it's just a "random" throw
        }
        if(isROF && reversalOfFortuneDamage >= 10)
        {
            skillChoice = Skills.REVERSAL_OF_FORTUNE;
        }
        if(isSTM && ourPet.calculateHpPercent() < 0.05 && (enemyCurrentHp <= 0.2 && enemyCurrentHp > reversalOfFortuneDamage / 100))
        {
            skillChoice = Skills.SHOOT_THE_MOON;
        }
        return skillChoice;
    }

    /**
     * Method called when the pet we are attacking is a SPEED type
     * @return the skill we will choose
     */
    public Skills decideSkillVsSpeed()
    {
        Skills skillChoice = null;
        setSkillLists();
        boolean canDoSmartOne = false;
        boolean isROF = false;
        boolean isSTM = false;
        List<Skills> fallbacks = new ArrayList<>();
        for(Skills skill : ourChoosableSkills)
        {
            if(skill == Skills.SHOOT_THE_MOON)
            {
                fallbacks.add(skill);
                isSTM = true;
            }
            else if(skill == Skills.REVERSAL_OF_FORTUNE)
            {
                fallbacks.add(skill);
                isROF = true;
            }
        }
        if(ourPet.calculateHpPercent() >= 0.75)
        {
            for(Skills skill : ourChoosableSkills)
            {
                if(skill == Skills.ROCK_THROW)
                {
                    canDoSmartOne = true;
                }
            }
        }
        else if(ourPet.calculateHpPercent() >= 0.25)
        {
            for(Skills skill : ourChoosableSkills)
            {
                if(skill == Skills.SCISSORS_POKE)
                {
                    canDoSmartOne = true;
                }
            }
        }
        else if(ourPet.calculateHpPercent() <= 0.25)
        {
            for(Skills skill : ourChoosableSkills)
            {
                if(skill == Skills.PAPER_CUT)
                {
                    canDoSmartOne = true;
                }
            }
        }
        if(canDoSmartOne)
        {
            if(ourPet.calculateHpPercent() >= 0.75)
            {
                skillChoice = Skills.ROCK_THROW;
            }
            else if(ourPet.calculateHpPercent() >= 0.25)
            {
                skillChoice = Skills.SCISSORS_POKE;
            }
            else if(ourPet.calculateHpPercent() <= 0.25)
            {
                skillChoice = Skills.PAPER_CUT;
            }
        }
        else
        {
            if(fallbacks.size() == 2)
            {
                if(reversalOfFortuneDamage >= 4.9)
                {
                    skillChoice = Skills.REVERSAL_OF_FORTUNE;
                }
                else
                {
                    skillChoice = Skills.SHOOT_THE_MOON;
                }
            }
            else if(fallbacks.size() == 1)
            {
                skillChoice = fallbacks.get(0);
            }
            else if(fallbacks.size() == 0)
            {
                if(ourPet.calculateHpPercent() >= 0.75)
                {
                    skillChoice = Skills.PAPER_CUT;
                }
                else if(ourPet.calculateHpPercent() >= 0.25)
                {
                    skillChoice = Skills.ROCK_THROW;
                }
                else if(ourPet.calculateHpPercent() <= 0.25)
                {
                    skillChoice = Skills.SCISSORS_POKE;
                }
            }
        }
        if(isROF && reversalOfFortuneDamage >= 10)
        {
            skillChoice = Skills.REVERSAL_OF_FORTUNE;
        }
        if(isSTM && ourPet.calculateHpPercent() < 0.05 && (enemyCurrentHp <= 0.2 && enemyCurrentHp > reversalOfFortuneDamage / 100))
        {
            skillChoice = Skills.SHOOT_THE_MOON;
        }
        return skillChoice;
    }

    /**
     * called by our Team08AI pet class. Calls the correct logic depending on what PetTypes the pet we are attacking is
     * @return the skill we will choose
     */
    public Skills decideSkill()
    {
        Skills skillChoice;
        if(enemyType == PetTypes.SPEED)
        {
            skillChoice = decideSkillVsSpeed();
        }
        else
        {
            skillChoice = decideSkillVsPowerOrIntelligence();
        }
        return skillChoice;
    }
}
