package team8.DamagePackage;

import java.util.ArrayList;
import java.util.List;

import team8.RandomPackage.*;
import team8.PlayablePackage.*;


/**
 * Control class to handle business logic for damage
 */
public class DamageManager
{
    private List<Damage> damage = new ArrayList<>();
    private RandomSingleton random = RandomSingleton.INSTANCE;

    private final int RANDOM_DAMAGE_CAP = 5;
    private final double INTELLIGENCE_COUNTER_CONDITIONAL_DAMAGE = 3.0;
    private final double INTELLIGENCE_SAME_CONDITIONAL_DAMAGE = 2.0;
    private final double SPEED_CONDITIONAL_DAMAGE = 12.5;
    private final double POWER_CONDITIONAL_DAMAGE_MULTIPLIER = 5.0;
    private final double SPEED_UPPER_HP_THRESHOLD = 0.75;
    private final double SPEED_LOWER_HP_THRESHOLD = 0.25;
    private final double SHOOT_THE_MOON_CONDITIONAL_DAMAGE = 20;


    public DamageManager()
    {
    }

    /**
     * returns the damage object of the damage manager
     * @return team8.DamagePackage.Damage object
     */
    public List<Damage> getDamage()
    {
        return this.damage;
    }

    /**
     * calculates random and conditional damage for all pets in the petList and makes new damage objects for each
     * @param petList - the list of pets attacking this turn
     * @param skillChoices - the skills chose by attacking pets this turn
     */
    public void calculateDamage(List<Playable> petList, List<Skills> skillChoices)
    {
        for(int i = 0; i < petList.size();i++)
        {
            double tempConditionalDamage = 0.0;
            double tempRandomDamage = calculateRandomDamage();
            if(skillChoices.get(i) == Skills.SHOOT_THE_MOON)
            {
                if(i == petList.size() - 1) //checks for wrap around in list
                {
                    if(skillChoices.get(0) == petList.get(i).getSkillPrediction())
                    {
                        tempConditionalDamage += SHOOT_THE_MOON_CONDITIONAL_DAMAGE;
                    }
                }
                else
                {
                    if(skillChoices.get(i + 1) == petList.get(i).getSkillPrediction())
                    {
                        tempConditionalDamage += SHOOT_THE_MOON_CONDITIONAL_DAMAGE;
                    }
                }
            }
            else if(skillChoices.get(i) == Skills.REVERSAL_OF_FORTUNE)
            {
                Pet pet = (Pet) petList.get(i);
                tempConditionalDamage -= pet.getRandomDamageDifference();
                tempRandomDamage -= pet.getRandomDamageDifference();
            }
            else
            {
                if(petList.get(i).getPetType() == PetTypes.POWER)
                {
                    if(i == petList.size() - 1)//checks for wrap around in list
                    {
                        tempConditionalDamage = powerDamage(tempRandomDamage, skillChoices.get(i), skillChoices.get(0));
                    }
                    else
                    {
                        tempConditionalDamage = powerDamage(tempRandomDamage, skillChoices.get(i), skillChoices.get(i + 1));
                    }
                }
                else if (petList.get(i).getPetType() == PetTypes.SPEED)
                {
                    if(i == petList.size() - 1)//checks for wrap around in list
                    {
                        double hpPercent = petList.get(0).calculateHpPercent();
                        tempConditionalDamage = speedDamage(hpPercent, skillChoices.get(i), skillChoices.get(0));
                    }
                    else
                    {
                        double hpPercent = petList.get(i + 1).calculateHpPercent();
                        tempConditionalDamage = speedDamage(hpPercent, skillChoices.get(i), skillChoices.get(i + 1));
                    }
                }
                else if (petList.get(i).getPetType() == PetTypes.INTELLIGENCE)
                {
                    if(i == petList.size() - 1) //checks for wrap around in list
                    {
                        tempConditionalDamage = intelligenceDamage(skillChoices.get(i), petList.get(0));
                    }
                    else
                    {
                        tempConditionalDamage = intelligenceDamage(skillChoices.get(i), petList.get(i + 1));
                    }
                }
            }
            this.damage.add(new Damage(tempRandomDamage, tempConditionalDamage)); // this used to be this.damage.add(i, new Damage(tempRandomDamage, tempConditionalDamage));
        }
    }

    /**
     * called when the attacking pet is the INTELLIGENCE type
     * @param pet1Skill - skill chosen by the attacker
     * @param pet2 - the defending pet
     * @return a double representing the conditional damage dealt by the attacker
     */
    public double intelligenceDamage(Skills pet1Skill, Playable pet2)
    {
        double tempConditionalDamage = 0.0;
        if(pet1Skill == Skills.ROCK_THROW)
        {
            if(pet2.getSkillRechargeTime(Skills.SCISSORS_POKE) > 0)
            {
                tempConditionalDamage += INTELLIGENCE_COUNTER_CONDITIONAL_DAMAGE;
            }
            else if(pet2.getSkillRechargeTime(Skills.ROCK_THROW) > 0)
            {
                tempConditionalDamage += INTELLIGENCE_SAME_CONDITIONAL_DAMAGE;
            }
            if(pet2.getSkillRechargeTime(Skills.SHOOT_THE_MOON) > 0)
            {
                tempConditionalDamage += INTELLIGENCE_SAME_CONDITIONAL_DAMAGE; // change this if they don't add (if only one condition applies)
            }
        }
        else if(pet1Skill == Skills.SCISSORS_POKE)
        {
            if(pet2.getSkillRechargeTime(Skills.PAPER_CUT) > 0)
            {
                tempConditionalDamage += INTELLIGENCE_COUNTER_CONDITIONAL_DAMAGE;
            }
            else if(pet2.getSkillRechargeTime(Skills.SCISSORS_POKE) > 0)
            {
                tempConditionalDamage += INTELLIGENCE_SAME_CONDITIONAL_DAMAGE;
            }
            if(pet2.getSkillRechargeTime(Skills.SHOOT_THE_MOON) > 0)
            {
                tempConditionalDamage += INTELLIGENCE_SAME_CONDITIONAL_DAMAGE; // change this if they don't add (if only one condition applies)
            }
        }
        else if(pet1Skill == Skills.PAPER_CUT)
        {
            if(pet2.getSkillRechargeTime(Skills.ROCK_THROW) > 0)
            {
                tempConditionalDamage += INTELLIGENCE_COUNTER_CONDITIONAL_DAMAGE;
            }
            else if(pet2.getSkillRechargeTime(Skills.PAPER_CUT) > 0)
            {
                tempConditionalDamage += INTELLIGENCE_SAME_CONDITIONAL_DAMAGE;
            }
            if(pet2.getSkillRechargeTime(Skills.SHOOT_THE_MOON) > 0)
            {
                tempConditionalDamage += INTELLIGENCE_SAME_CONDITIONAL_DAMAGE; // change this if they don't add (if only one condition applies)
            }
        }
        return tempConditionalDamage;
    }

    /**
     * called when the attacking pet is the POWER type
     * @param tempRandomDamage - the random damage dealt by the attacking pet this turn
     * @param pet1Skill - the skill chosen by the attacking pet
     * @param pet2Skill - the skill chosen by the defending pet
     * @return a double representing the conditional damage dealt by the attacker
     */
    public double powerDamage(double tempRandomDamage, Skills pet1Skill, Skills pet2Skill)
    {
        double tempConditionalDamage = 0.0;
        if(pet1Skill == Skills.ROCK_THROW && pet2Skill == Skills.SCISSORS_POKE)
        {
            tempConditionalDamage += POWER_CONDITIONAL_DAMAGE_MULTIPLIER * tempRandomDamage;
        }
        else if(pet1Skill == Skills.SCISSORS_POKE && pet2Skill == Skills.PAPER_CUT)
        {
            tempConditionalDamage += POWER_CONDITIONAL_DAMAGE_MULTIPLIER * tempRandomDamage;
        }
        else if(pet1Skill == Skills.PAPER_CUT && pet2Skill == Skills.ROCK_THROW)
        {
            tempConditionalDamage += POWER_CONDITIONAL_DAMAGE_MULTIPLIER * tempRandomDamage;
        }
        return tempConditionalDamage;
    }

    /**
     * called when the attacking pet is the SPEED type
     * @param hpPercent - percent HP remaining for the defending pet
     * @param pet1Skill - skill chosen by the attacking pet
     * @param pet2Skill - skill chosen by the defending pet
     * @return a double representing the conditional damage dealt by the attacker
     */
    public double speedDamage(double hpPercent, Skills pet1Skill, Skills pet2Skill)
    {
        double tempConditionalDamage = 0.0;
        if(hpPercent >= SPEED_UPPER_HP_THRESHOLD && (pet1Skill == Skills.ROCK_THROW && pet2Skill != Skills.ROCK_THROW))
        {
            tempConditionalDamage += SPEED_CONDITIONAL_DAMAGE;
        }
        else if((hpPercent <= SPEED_UPPER_HP_THRESHOLD) && (hpPercent >= SPEED_LOWER_HP_THRESHOLD) && (pet1Skill == Skills.SCISSORS_POKE && pet2Skill != Skills.SCISSORS_POKE))
        {
            tempConditionalDamage += SPEED_CONDITIONAL_DAMAGE;
        }
        else if((hpPercent < SPEED_LOWER_HP_THRESHOLD) && (hpPercent >= 0.0) && (pet1Skill == Skills.PAPER_CUT && pet2Skill != Skills.PAPER_CUT))
        {
            tempConditionalDamage += SPEED_CONDITIONAL_DAMAGE;
        }
        return tempConditionalDamage;
    }


    /**
     * Gets the next random double from the random object
     * @return the next random double
     */
    public double calculateRandomDamage()
    {
        double randomDamage = random.getRandom().nextDouble() * (RANDOM_DAMAGE_CAP + Double.MIN_NORMAL);
        if(randomDamage == 0.0)
        {
            randomDamage = random.getRandom().nextDouble() * (RANDOM_DAMAGE_CAP + Double.MIN_VALUE);
        }
        return randomDamage;
    }
}