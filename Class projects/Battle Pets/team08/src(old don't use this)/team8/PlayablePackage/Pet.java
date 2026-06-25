package team8.PlayablePackage;

import team8.InputOutputPackage.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

/**
 * Entity class to hold the necessary data for pets
 */
public class Pet implements Playable {
    private final String name;
    private final PetTypes type;
    private double currentHP;
    private final double startingHP;
    private double randomDamageDifference;
    private final Player player;
    private final Random random = new Random();
    private int UID;
    private Skills currentPrediction;
    private final List<Skills> skillList;
    private final Map<Skills, Integer> skillMap;
    private final static InputManager inputmanager = InputManager.INPUT;
    private final static OutputManager outputmanager = OutputManager.OUTPUT;
    /**
     * Constructs a PetPackage.Pet using the builder pattern
     * @param builder The builder for a put
     */
    private Pet(PetBuilder builder){
        //Sate validity checks
        if (builder.name == null){
            throw new IllegalStateException("Name must not be null");
        }if (builder.type == null){
            throw new IllegalStateException("Type must not be null");
        }if (builder.player == null) {
            throw new IllegalStateException("Player must not be null");
        }if (builder.startingHP <= 0){
            throw new IllegalStateException("Starting HP must greater than 0");
        }if (builder.UID < 0){
            throw new IllegalStateException("UID must be greater than 0");
        }
        this.name = builder.name;
        this.type = builder.type;
        this.startingHP = builder.startingHP;
        this.currentHP = builder.startingHP;
        this.player = builder.player;
        this.UID = builder.UID;
        this.skillList = new ArrayList<>(5);
        this.skillList.add(Skills.ROCK_THROW);
        this.skillList.add(Skills.SCISSORS_POKE);
        this.skillList.add(Skills.PAPER_CUT);
        this.skillList.add(Skills.SHOOT_THE_MOON);
        this.skillList.add(Skills.REVERSAL_OF_FORTUNE);
        this.skillMap = new HashMap<>(5);
        this.skillMap.put(Skills.ROCK_THROW, 0);
        this.skillMap.put(Skills.SCISSORS_POKE, 0);
        this.skillMap.put(Skills.PAPER_CUT, 0);
        this.skillMap.put(Skills.SHOOT_THE_MOON, 0);
        this.skillMap.put(Skills.REVERSAL_OF_FORTUNE, 0);
        this.randomDamageDifference = 0.0;
    }


    //team8.PlayablePackage.Playable Functionality
    /**
     * @return Returns the unique id for this playable.  The id is set during construction (in the constructor of the playable).
     */
    public int getPlayableUid(){
        return UID;
    }

    /**
     * Sets the playableUid to the id provided.  This should be implemented but not called.
     * It has special use for Doug's own code.
     * @param playableUid - The unique id to assign to the current playable object
     */
    public void setPlayableUid(int playableUid){ UID = playableUid;}

    /**
     * @return Returns the player's name
     */
    public String getPlayerName() {
        return player.getName();
    }
    /**
     * @return Returns the pets name
     */
    public String getPetName() {
        return name;
    }

    /**
     * @return Returns the player's type
     */
    public PlayerTypes getPlayerType() {
        return player.getType();
    }
    /**
     * @return Returns the pet's type
     */
    public PetTypes getPetType() {
        return type;
    }

    /**
     * @return Returns the pet's current HP
     */
    public double getCurrentHp() {
        return currentHP;
    }

    /**
     * gets the random damage difference for the pet
     * @return a double representing the difference in random damage dealt by this pet - random damage dealt by other pets to this one
     */

    public double getRandomDamageDifference() {
        return randomDamageDifference;
    }

    /**
     * takes a double and sets the pet's randomDamageDifference to the double
     * @param randomDamageDifference - a double to represent the new random damage difference for the pet
     */

    public void setRandomDamageDifference(double randomDamageDifference) {
        this.randomDamageDifference = randomDamageDifference;
    }

    /**
     * Prompt the user for a skill choice or the system chooses an option for computer players
     * @return Returns the chosen skill
     */
    public Skills chooseSkill(){
        int option;
        while (true) {
            outputmanager.printOutput("Enter Skill Choice\n[1]--> ROCK THROW\n[2]--> SCISSOR POKE\n[3]--> PAPER CUT\n[4]--> SHOOT THE MOON\n[5]--> REVERSAL OF FORTUNE\n");
            if (player.getType().equals(PlayerTypes.HUMAN)) {
                option = inputmanager.getInt();
            }else{
                option = random.nextInt(1, 6);
            }
            if (option < 1 || option > skillList.size()) {
                outputmanager.printOutput("Please enter a valid choice");
                continue;
            }
            if (skillMap.get(skillList.get(option - 1)) != 0 ) //changed to -1
            {
                if (player.getType().equals(PlayerTypes.HUMAN)) {
                    outputmanager.printOutput("Skill is recharging for " + skillMap.get(skillList.get(option)) + " turn(s)\n");
                }
                continue;
            }
            break;
        }
        return switch (option) {
            case 2 -> Skills.SCISSORS_POKE;
            case 3 -> Skills.PAPER_CUT;
            case 4 -> {
                getSkillPredictionChoice();
                yield Skills.SHOOT_THE_MOON;
            }
            case 5 -> Skills.REVERSAL_OF_FORTUNE;
            default -> Skills.ROCK_THROW;
        };

    }

    /**
     * This method is called by the game controlling classes to update the pet's hp based on the damage inflicted
     * @param hp - The hp which will be subtracted from the current hp
     */
    public void updateHp(double hp){
        this.currentHP -= hp;
    }

    /**
     * Resets the pet's current hp to its starting hp
     */
    public void resetHp(){
        currentHP = startingHP;
    }

    /**
     * Sets the pet's hp
     * @param currentHp - the value to set the pets currentHP to
     */
    public void setCurrentHp(double currentHp){
        this.currentHP = currentHp;
    }

    /**
     * @return Returns true if the pet's hp > 0, false otherwise
     */
    public boolean isAwake(){
        return currentHP > 0;
    }

    /**
     * This is a mystery for now...
     */
    public void getSkillPredictionChoice(){
        int option;
        outputmanager.printOutput(player.getName() + " what skill do you think the next player will use?\n[1]--> ROCK THROW\n[2]--> SCISSOR POKE\n[3]--> PAPER CUT\n[4]--> SHOOT THE MOON\n[5]--> REVERSAL OF FORTUNE\n");
        if (player.getType().equals(PlayerTypes.HUMAN)) {
            option = inputmanager.getInt();
        }else{
            option = random.nextInt(1, 6);
        }
        while (option < 1 || option > skillList.size()) {
            outputmanager.printOutput("Please enter a valid choice");
            option = inputmanager.getInt();
        }
        currentPrediction =  skillList.get(option - 1);
    }

    public Skills getSkillPrediction(){
        return currentPrediction;
    }

    /**
     * @param skill - the skill to get the recharge time of
     * @return Returns the current recharge time for the provided skill enumeration
     */
    public int getSkillRechargeTime(Skills skill){
        return switch (skill) {
            case ROCK_THROW -> skillMap.get(Skills.ROCK_THROW);
            case PAPER_CUT -> skillMap.get(Skills.PAPER_CUT);
            case SCISSORS_POKE -> skillMap.get(Skills.SCISSORS_POKE);
            case SHOOT_THE_MOON -> skillMap.get(Skills.SHOOT_THE_MOON);
            case REVERSAL_OF_FORTUNE -> skillMap.get(Skills.REVERSAL_OF_FORTUNE);
        };

    }
    /**
     * This is somewhat of a convenience method, since there are methods to get the starting hp and current hp
     * @return Returns the pet's current percent of hp
     */
    public double calculateHpPercent(){
        return (currentHP / startingHP);
    }

    /**
     * @return Returns the pet's starting hp
     */
    public double getStartingHp(){
        return startingHP;
    }


    /**
     * Called by the game controlling classes.
     * Resets the pet's hp to its starting hp
     * Resets all skills to what they were at the start of the fight
     */
    public void reset() {
        currentHP = startingHP;
        randomDamageDifference = 0; // wasn't here before
        skillMap.replaceAll((k, v) -> 0);
    }


    /**
     * Decrements the recharge times for all recharging skills
     */
    public void decrementRechargeTimes(){
        for(Map.Entry<Skills, Integer> skill: skillMap.entrySet()){
            if (skill.getValue() != 0){
                skillMap.put(skill.getKey(), skill.getValue() - 1);
            }
        }
    }

    /**
     * Sets the recharge time for the given skill
     * @param skill - the skill to change the recharge time of
     * @param rechargeTime - the time to set the skill to
     */
    public void setRechargeTime(Skills skill, int rechargeTime){
        switch(skill){
            case ROCK_THROW:
                skillMap.put(Skills.ROCK_THROW, rechargeTime);
                break;
            case PAPER_CUT:
                skillMap.put(Skills.PAPER_CUT, rechargeTime);
                break;
            case SCISSORS_POKE:
                skillMap.put(Skills.SCISSORS_POKE, rechargeTime);
                break;
            case SHOOT_THE_MOON:
                skillMap.put(Skills.SHOOT_THE_MOON, rechargeTime);
                break;
            case REVERSAL_OF_FORTUNE:
                skillMap.put(Skills.REVERSAL_OF_FORTUNE, rechargeTime);
                break;
        }

    }
    /**
     * A Builder for the PetPackage.Pet class
     */
    public static class PetBuilder{

        private String name;
        private PetTypes type;
        private double startingHP;
        private Player player;
        private int UID;

        public PetBuilder(){

        }


        public PetBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public PetBuilder withType(PetTypes type) {
            this.type = type;
            return this;
        }


        public PetBuilder withStartingHP(double startingHp) {
            this.startingHP = startingHp;
            return this;
        }

        public PetBuilder withPlayer(Player player) {
            this.player = player;
            return this;
        }

        public PetBuilder withUID(int UID) {
            this.UID = UID;
            return this;
        }

        /**
         * @return a new PetPackage.Pet object
         */
        public Pet build()
        {
            return new Pet(this);
        }
    }

}