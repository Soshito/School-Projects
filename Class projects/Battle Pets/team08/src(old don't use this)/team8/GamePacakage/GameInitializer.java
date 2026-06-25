package team8.GamePacakage;

import team8.InputOutputPackage.InputManager;
import team8.InputOutputPackage.OutputManager;
import team8.PlayablePackage.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameInitializer
{
    private InputManager inputManager = InputManager.INPUT;
    private OutputManager outputManager = OutputManager.OUTPUT;
    private List<Playable> petList;
    private List<Player> players;
    private List<Pet.PetBuilder> petBuilders;
    private List<String> playerNames;
    private List<PlayerTypes> playerTypes;
    private Map<Integer, PlayerTypes> playerTypesMap = new HashMap<>(2){{
        put(1, PlayerTypes.HUMAN);
        put(2, PlayerTypes.COMPUTER);
    }};
    private Map<Integer, PetTypes> petTypesMap = new HashMap<>(3) {{
        put(1, PetTypes.POWER);
        put(2, PetTypes.SPEED);
        put(3, PetTypes.INTELLIGENCE);
    }};

    /**
     * basic constructor
     */
    public GameInitializer()
    {
    }

    public List<Playable> getPetList() {
        return petList;
    }

    public void setPetList(List<Playable> petList) {
        this.petList = petList;
    }

    /**
     * calls the necessary methods to instantiate a game
     */
    public void setParams()
    {
        int numPlayers = numPlayers();
        playerCreation(numPlayers);
        petCreation(numPlayers);
        for (int i = 0; i < numPlayers; i++) {
            players.add(i, new Player(playerNames.get(i), playerTypes.get(i)));
            petList.add(i, petBuilders.get(i).withPlayer(players.get(i)).withUID(i).build());
        }

    }
    /**
     * used when initializing the n players
     * @return - an int representing the number of players
     */
    private int numPlayers() {
        outputManager.printOutput("How many players are there?\n");
        int numberOfPlayers = inputManager.getInt();
        while(numberOfPlayers < 2){
            outputManager.printOutput("Your number must be 2 or larger\n");
            numberOfPlayers = inputManager.getInt();
        }
        players = new ArrayList<>(numberOfPlayers);
        petList = new ArrayList<>(numberOfPlayers);
        petBuilders = new ArrayList<>(numberOfPlayers);
        for (int i = 0; i < numberOfPlayers; i++){
            petBuilders.add(i, new Pet.PetBuilder());
        }
        playerNames = new ArrayList<>(numberOfPlayers);
        playerTypes = new ArrayList<>(numberOfPlayers);
        return numberOfPlayers;
    }

    /**
     * used when initializing the n players
     * @param numPlaying - an int representing the number of players
     */
    private void playerCreation(int numPlaying) {
        for(int i = 0; i < numPlaying; i++){
            outputManager.printOutput("Player " + (i + 1) + " type\n[1]--> HUMAN\n[2]--> COMPUTER\n");
            int choice1 = inputManager.getInt();
            while (choice1 != 1 && choice1 != 2) {
                outputManager.printOutput("You must enter 1 or 2.\n");
                choice1 = inputManager.getInt();
            }
            playerTypes.add(i, playerTypesMap.get(choice1));
        }
        for(int i = 0; i < numPlaying; i++){
            outputManager.printOutput("Player " + (i + 1) + " enter your name--> ");
            String playerName = inputManager.getInput();
            while (playerName.isEmpty()) {
                outputManager.printOutput("You must input a player name.\n");
                playerName = inputManager.getInput();
            }
            playerNames.add(i, playerName);
        }
    }

    /**
     * Used when initializing the pets
     * @param numPlaying - an int representing the number of players
     */
    private void petCreation(int numPlaying) {
        for(int i = 0; i < numPlaying; i++){
            outputManager.printOutput("Player " + (i + 1) + " enter your pet's name--> ");
            String petName = inputManager.getInput();
            while (petName.isEmpty()) {
                outputManager.printOutput("You must input a pet name.\n");
                petName = inputManager.getInput();
            }
            petBuilders.get(i).withName(petName);
            outputManager.printOutput("Player " + (i + 1) + " enter your pet's type\n[1]--> POWER\n[2]--> SPEED\n[3]--> INTELLIGENCE\n");
            int petType = inputManager.getInt();
            while (petType != 1 && petType != 2 && petType != 3) {
                outputManager.printOutput("You must enter 1, 2, or 3.\n");
                petType = inputManager.getInt();
            }
            petBuilders.get(i).withType(petTypesMap.get(petType));
            outputManager.printOutput("Player " + (i + 1) + " enter your pet's starting HP--> ");
            double petHp = inputManager.getDouble();
            while (petHp <= 0.0) {
                outputManager.printOutput("You must enter a HP above 0.0.\n");
                petHp = inputManager.getDouble();
            }
            petBuilders.get(i).withStartingHP(petHp);
        }
    }

}
