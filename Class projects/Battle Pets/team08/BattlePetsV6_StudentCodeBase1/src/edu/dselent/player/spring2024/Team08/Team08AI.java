package edu.dselent.player.spring2024.Team08;

import edu.dselent.event.*;
import edu.dselent.player.PetTypes;
import edu.dselent.player.PlayerTypes;
import edu.dselent.player.defaultintelligence.*;
import edu.dselent.settings.PlayerSettings;
import edu.dselent.skill.Skills;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Team08AI extends Pet
{
    private Random skillGenerator; // new random generator for AI
    private final long SEED = 42069; //hard-coded seed (funny)
    private int myIndex;
    private int myVictimIndex;
    private int myAttackerIndex;
    private double damageDifference;
    private ArrayList<Team08AI.ImportantPetInfo> petInfoList;
    private int playableUid;
    private int crackedAI = 0;

    public Team08AI(int playableUid, PlayerSettings playerSettings)
    {
        super(playerSettings);
        this.playableUid = playableUid;
        this.skillGenerator = new Random(SEED);
        setPlayerType(PlayerTypes.ReginaldRegiment);
        petInfoList = new ArrayList<Team08AI.ImportantPetInfo>();
    }

    @Override
    public int getPlayableUid()
    {
        return playableUid;
    }

    /**
     * Used to get the current benefit of using reversal of fortune
     * @return a double representing our difference in random damage done - random damage taken
     */
    public double getDamageDifference() {
        return damageDifference;
    }

    /**
     * gets input from the AI and returns a valid skill
     */
    public Skills chooseSkill()
    {
        Skills currentPetSkill = null;
        Team08AI.ImportantPetInfo victim = petInfoList.get(myVictimIndex);
        List<Skills> victimAvailableSkills = new ArrayList<>();
        List<Skills> skillList = new ArrayList<>();
        skillList.add(Skills.ROCK_THROW);
        skillList.add(Skills.SCISSORS_POKE);
        skillList.add(Skills.PAPER_CUT);
        skillList.add(Skills.SHOOT_THE_MOON);
        skillList.add(Skills.REVERSAL_OF_FORTUNE);
        for(Skills skill: skillList)
        {
            if(victim.isRecharged(skill))
            {
                victimAvailableSkills.add(skill);
            }
        }
        double victimCurrentHp = victim.getHp() / victim.getMaxHp();
        PetTypes victimType = victim.getPetType();
        //Team08Intelligence intelligence = new Team08Intelligence(this, victimCurrentHp, victimAvailableSkills, victimType);
        Team08Power power = new Team08Power(this, victimCurrentHp, victimAvailableSkills, victimType);
        if(this.getPetType() == PetTypes.INTELLIGENCE)
        {
            crackedAI = 1;
            //currentPetSkill = intelligence.decideSkill();
        }
        else if(this.getPetType() == PetTypes.POWER)
        {
            crackedAI = 2;
            currentPetSkill = power.decideSkill();
        }
        if(currentPetSkill == Skills.SHOOT_THE_MOON)
        {
            crackedAI = 3;
            predictSkill();
        }


        return currentPetSkill;
    }

    @Override
    public void setPlayableUid(int playableUid)
    {
        this.playableUid = playableUid;
    }

    /**
     * used to predict the enemy's skill choice in the event that our pet chooses Shoot the Moon
     */
    private void predictSkill()
    {
        Skills predictedSkill = null;
        int skillNum = 0;
        boolean isRecharged = false;
        while (!isRecharged)
        {
            skillNum = getSkillNum();
            if(skillNum < 0)
                skillNum += Skills.values().length;
            predictedSkill = Skills.values()[skillNum];
            isRecharged = petInfoList.get(myVictimIndex).isRecharged(predictedSkill);
        }
        setCurrentSkillPrediction(predictedSkill);
        if (crackedAI == 0) {
            //choose speed
            crackedAI = -1;
        }
        else if (crackedAI == 1) {
            //choose intelligence
            crackedAI = -1;
        }
        else if (crackedAI == 2) {
            //choose power
            crackedAI = -1;
        }
        else {
            //shoot the moon with informed final state
            crackedAI = -1;
        }
    }

    /**
     * returns a number 0 to Skills.values().length
     *                  0 to 4
     * this number is the index of Skills.values() that we want
     * @return
     */
    private int getSkillNum()
    {
        int skillNum = ((skillGenerator.nextInt() % Skills.values().length));
        if(skillNum < 0) // % can be negative
            skillNum += Skills.values().length;
        return skillNum;
    }
    /**
     * Takes Events from the Referee calls the appropriate method
     */
    @Override
    public void update(Object event)
    {
        if(event instanceof BaseEvent)
        {
            switch(((BaseEvent) event).getEventType())
            {
                case ATTACK:
                    handleAttackEvent((AttackEvent)event);
                    break;
                case FIGHT_START:
                    handleFightStartEvent((FightStartEvent)event);
                    break;
                case ROUND_START:
                    handleRoundStartEvent((RoundStartEvent)event);
                    break;
            }
        }
    }

    /**
     * decrements recharge times
     * @param RoundStartEvent Isn't used now, but might be in the future
     */
    private void handleRoundStartEvent(RoundStartEvent RoundStartEvent)
    {
        for(int i = 0; i < petInfoList.size(); i++)
        {
            petInfoList.get(i).decrementRechargeTimes();
        }
    }

    /**
     * Clear knowledge of previous fight
     * Create a new list of ImportantPetInfo
     * @param fightStartEvent
     */
    private void handleFightStartEvent(FightStartEvent fightStartEvent)
    {
        damageDifference = 0;
        if(petInfoList != null)
        {
            petInfoList.clear();
        }

        ArrayList<PlayerEventInfo> playerEventInfoList
                = (ArrayList<PlayerEventInfo>) fightStartEvent.getPlayerEventInfoList();

        for(int i = 0; i < playerEventInfoList.size(); i++)
        {
            PlayerEventInfo playerInfo = playerEventInfoList.get(i);
            if(getPetName().equals(playerInfo.getPetName()))
            {
                this.myIndex = i;
            }
            petInfoList.add(new Team08AI.ImportantPetInfo(playerInfo.getPetType(), playerInfo.getStartingHp()));
        }
    }

    /**
     * takes the AttackEvent and uses it to update ImportantPetInfoList
     */
    private void handleAttackEvent(AttackEvent attackEvent)
    {
        int attackingIndex = attackEvent.getAttackingPlayableUid();
        int victimIndex = attackEvent.getVictimPlayableUid();
        Team08AI.ImportantPetInfo attackingPetInfo = petInfoList.get(attackingIndex);
        Team08AI.ImportantPetInfo victimPetInfo = petInfoList.get(victimIndex);
        double randomDamage = attackEvent.getDamage().getRandomDamage();

        attackingPetInfo.useSkill(attackEvent.getAttackingSkillChoice());
        victimPetInfo.updateHp(randomDamage + attackEvent.getDamage().getConditionalDamage());

        if(attackingIndex == myIndex)
        {
            damageDifference -= randomDamage;
            myVictimIndex = victimIndex;
        }
        if(victimIndex == myIndex)
        {
            damageDifference += randomDamage;
            myAttackerIndex = attackingIndex;
        }
    }

    /**
     * An inner class that represents Pets.  Only the information
     * important to our algorithms is stored.
     */
    public static class ImportantPetInfo
    {
        private PetTypes petType;
        private double hp;
        private double MAX_HP = 100;
        private RockThrow rock = new RockThrow();
        private ScissorsPoke scissors = new ScissorsPoke();
        private PaperCut paper = new PaperCut();
        private ShootTheMoon moon = new ShootTheMoon();
        private ReversalOfFortune reversal = new ReversalOfFortune();

        public ImportantPetInfo(PetTypes petType, double maxHp)
        {
            this.petType = petType;
            this.hp = maxHp;
            this.MAX_HP = maxHp;
        }

        /**
         * Takes a skills enum and uses the skill, updating the recharge times
         * @param skill
         */
        public void useSkill(Skills skill)
        {
            switch (skill)
            {
                case ROCK_THROW:
                    rock.useSkill();
                    break;
                case PAPER_CUT:
                    paper.useSkill();
                    break;
                case SCISSORS_POKE:
                    scissors.useSkill();
                    break;
                case SHOOT_THE_MOON:
                    moon.useSkill();
                    break;
                case REVERSAL_OF_FORTUNE:
                    reversal.useSkill();
                    break;
            }
        }

        public PetTypes getPetType()
        {
            return petType;
        }

        public double getHp()
        {
            return hp;
        }

        public double getMaxHp()
        {
            return MAX_HP;
        }

        /**
         * updates this.hp to take damage
         * @param hp
         */
        public void updateHp(double hp)
        {
            this.hp -= hp;
        }


        /**
         * takes a skills enum and returns
         * the recharge time associated with it
         * @return
         */
        public boolean isRecharged(Skills skill)
        {
            Skill skillInstance = null;
            switch (skill)
            {
                case ROCK_THROW:
                    skillInstance = rock;
                    break;
                case PAPER_CUT:
                    skillInstance = paper;
                    break;
                case SCISSORS_POKE:
                    skillInstance = scissors;
                    break;
                case SHOOT_THE_MOON:
                    skillInstance = moon;
                    break;
                case REVERSAL_OF_FORTUNE:
                    skillInstance = reversal;
                    break;
            }
            return skillInstance != null && skillInstance.isCharged();
        }

        /**
         * recharges each skill by 1
         */
        public void decrementRechargeTimes()
        {
            rock.recharge();
            scissors.recharge();
            paper.recharge();
            moon.recharge();
            reversal.recharge();
        }
    }
}
