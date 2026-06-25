package team8.PlayablePackage;

import team8.Utils;

public enum PlayerTypes
{
	HUMAN,
	COMPUTER;
	
	@Override
	public String toString()
	{		
		return Utils.convertEnumString(this.name());
	}
}
