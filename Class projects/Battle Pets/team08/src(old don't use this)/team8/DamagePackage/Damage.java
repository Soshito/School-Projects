package team8.DamagePackage;

import team8.BattlePackage.*;
import team8.RandomPackage.*;
import team8.FightPackage.*;
import team8.DamagePackage.*;
import team8.InputOutputPackage.*;
import team8.PlayablePackage.*;
import team8.RoundPackage.*;

/**
* Class to store damage once it has been calculated
* Can be used to store damage for all pets for all rounds by game controlling classes.
*/
public class Damage
{
	private double randomDamage;
	private double conditionalDamage;
	
	public Damage(double randomDamage, double conditionalDamage)
	{
		this.randomDamage = randomDamage;
		this.conditionalDamage = conditionalDamage;
	}

	public double getRandomDamage()
	{
		return randomDamage;
	}

	public void setRandomDamage(double randomDamage)
	{
		this.randomDamage = randomDamage;
	}
	
	public double getConditionalDamage()
	{
		return conditionalDamage;
	}
	
	public void setConditionalDamage(double conditionalDamage)
	{
		this.conditionalDamage = conditionalDamage;
	}

	public double calculateTotalDamage()
	{
		return randomDamage + conditionalDamage;
	}

}
