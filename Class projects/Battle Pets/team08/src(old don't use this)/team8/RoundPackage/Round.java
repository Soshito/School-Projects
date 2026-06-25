package team8.RoundPackage;

import team8.BattlePackage.*;
import team8.RandomPackage.*;
import team8.FightPackage.*;
import team8.DamagePackage.*;
import team8.InputOutputPackage.*;
import team8.PlayablePackage.*;
import team8.RoundPackage.*;

import java.util.List;

/**
 * Entity class to hold the necessary round data
 */
public class Round
{
    private List<Playable> petList;

    private List<Damage> roundDamage;
    public Round(List<Playable> petList)
    {
        this.petList = petList;
    }

    public List<Playable> getPetList() {
        return this.petList;
    }

    public void setPetList(List<Playable> petList) {
        this.petList = petList;
    }

    public List<Damage> getRoundDamage() {
        return roundDamage;
    }

    public void setRoundDamage(List<Damage> roundDamage) {
        this.roundDamage = roundDamage;
    }
}