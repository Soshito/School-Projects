package team8.FightPackage;

import team8.InputOutputPackage.InputManager;
import team8.InputOutputPackage.OutputManager;
import team8.PlayablePackage.Playable;

import java.util.ArrayList;
import java.util.List;

public class FightInitializer
{
    private InputManager inputManager = InputManager.INPUT;
    private OutputManager outputManager = OutputManager.OUTPUT;
    private List<Playable> petList;

    /**
     * Basic constructor
     * @param petList - a list of Playables representing the pets in the fight
     */
    public FightInitializer(List<Playable> petList)
    {
        this.petList = petList;
    }

    /**
     * prints all necessary outputs and runs a fight
     * @return a list of Playables representing the winner(s)
     */
    public List<Playable> runFight()
    {
        outputManager.printOutput("\n\nPets:\n");
        outputManager.printOutput(petList.get(0).getPetName());
        outputManager.printOutput(" (");
        outputManager.printOutput(String.valueOf(petList.get(0).getPetType()));
        outputManager.printOutput(")\n");
        outputManager.printOutput(petList.get(1).getPetName());
        outputManager.printOutput(" (");
        outputManager.printOutput(String.valueOf(petList.get(1).getPetType()));
        outputManager.printOutput(")\n\n");
        Fight fight = new Fight(petList);
        FightManager fightManager = new FightManager(fight);
        while(!fight.getIsOver())
        {
            fightManager.runRounds();
        }
        if(fight.getWinner().size() > 1)
        {
            for(int i = 0; i < fight.getWinner().size(); i++)
            {
                if(i == fight.getWinner().size() - 1)
                {
                    outputManager.printOutput(fight.getWinner().get(1).getPetName());
                }
                else
                {
                    outputManager.printOutput(fight.getWinner().get(i).getPetName() + " and ");
                }
            }
            outputManager.printOutput(" are the winners of the fight!\n\n");
        }
        else
        {
            outputManager.printOutput(fight.getWinner().get(0).getPetName());
            outputManager.printOutput(" is the winner of the fight!\n\n");
        }
        return fight.getWinner();
    }


    public List<Playable> getPetList() {
        return petList;
    }

    public void setPetList(List<Playable> petList) {
        this.petList = petList;
    }
}
