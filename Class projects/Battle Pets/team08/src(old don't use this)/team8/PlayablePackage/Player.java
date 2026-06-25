package team8.PlayablePackage;

import team8.BattlePackage.*;
import team8.RandomPackage.*;
import team8.FightPackage.*;
import team8.DamagePackage.*;
import team8.InputOutputPackage.*;
import team8.PlayablePackage.*;
import team8.RoundPackage.*;

/**
 * Entity class to hold the necessary player data
 */
public class Player{
    private String name;
    private PlayerTypes type;

    public Player(String name, PlayerTypes type)
    {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PlayerTypes getType() {
        return type;
    }

    public void setType(PlayerTypes type) {
        this.type = type;
    }


}