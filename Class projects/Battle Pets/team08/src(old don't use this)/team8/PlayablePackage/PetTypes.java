package team8.PlayablePackage;

import team8.BattlePackage.*;
import team8.RandomPackage.*;
import team8.FightPackage.*;
import team8.DamagePackage.*;
import team8.InputOutputPackage.*;
import team8.PlayablePackage.*;
import team8.RoundPackage.*;
import team8.Utils;

public enum PetTypes
{
	POWER,
	SPEED,
	INTELLIGENCE;
	
	@Override
	public String toString()
	{		
		return Utils.convertEnumString(this.name());
	}
}
