package team8.SeasonPackage;

import team8.BattlePackage.Battle;
import team8.BattlePackage.BattleManager;
import team8.InputOutputPackage.OutputManager;
import team8.PlayablePackage.Playable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SeasonManager
{
    private OutputManager outputManager = OutputManager.OUTPUT;
    private Season season;
    private List<Playable> petList;
    private int numFights;
    private List<BattleManager> battleList = new ArrayList<>();
    private List<Integer> battleWins = new ArrayList<>();
    private List<Integer> battleWinsCopy = new ArrayList<>();
    private List<Playable> winners = new ArrayList<>();
    private List<Integer> fightsWon = new ArrayList<>();
    private List<Integer> endOfSeasonPrintList = new ArrayList<>();
    private int totalBattles;

    /**
     * Basic constructor
     * @param season - the entity class for the season
     * @param petList - a list of Playables representing the pets in the season
     */
    public SeasonManager(Season season, List<Playable> petList, int numFights)
    {
        this.season = season;
        this.petList = petList;
        this.numFights = numFights;
        totalBattles = petList.size() - 1;
        for(int i = 0; i < petList.size();i++)
        {
            battleWins.add(0);
            fightsWon.add(0);
        }
    }

    /**
     * Creates a list of BattleManagers so that each pet battles the others exactly once in the correct order
     */
    public void roundRobin()
    {
        List<Integer> petListCopy = new ArrayList<>();
        for (int i = 0; i < petList.size(); i++)
        {
            petListCopy.add(i);
        }
        if(petList.size() % 2 != 0)
        {
            petListCopy.add(-1);
        }
        List<Integer> petListCopyCopy = new ArrayList<>();
        petListCopyCopy.addAll(petListCopy);
        List<Playable> battlesTemp = new ArrayList<>();
        int count = 0;
        while(!(petListCopyCopy.equals(petListCopy)) || count == 0)
        {
            List<Integer> flipped = flipSecondHalf(petListCopyCopy);
            for(int i = 0; i < (flipped.size() / 2);i++)
            {
                if(!(flipped.get(i) == -1 || flipped.get(((i + (flipped.size() / 2)) % flipped.size())) == -1))
                {
                    battlesTemp.add(petList.get(flipped.get(i)));
                    battlesTemp.add(petList.get(flipped.get(((i + (flipped.size() / 2)) % flipped.size()))));
                    List<Playable> usable = new ArrayList<>();
                    usable.addAll(battlesTemp);
                    Battle b = new Battle(usable, numFights);
                    BattleManager bm = new BattleManager(b);
                    battleList.add(bm);
                    battlesTemp.clear();
                }
            }
            petListCopyCopy.add(1, petListCopyCopy.remove(petListCopyCopy.size() - 1));
            count++;
        }
        season.setBattleList(battleList);
    }

    /**
     * Method used to flip the second half of a list
     * @param notFlipped - the unflipped list
     * @return a list of Integer that is flipped
     */
    public List<Integer> flipSecondHalf(List<Integer> notFlipped)
    {
        List<Integer> flipped = new ArrayList<>();
        flipped.addAll(notFlipped);
        for(int i = (notFlipped.size() / 2); i < (notFlipped.size() - 1); i++)
        {
            flipped.add(i, flipped.get(flipped.size() - 1));
            flipped.remove(flipped.size() - 1);
        }
        return flipped;
    }

    /**
     * Uses the entity's iterator to iterate over the battles and run them
     */
    public void runBattles()
    {
        Iterator<BattleManager> iterator = season.iterator();
        BattleManager manager;
        while(iterator.hasNext())
        {
            manager = iterator.next();
            manager.runFights();
            List<Playable> winner = manager.getBattle().getWinners();
            for(List<Playable> pets: manager.getBattle().getFightWinners())
            {
                for(Playable petWon:pets)
                {
                    for(int i = 0; i < petList.size();i++)
                    {
                        if(petList.get(i).equals(petWon))
                        {
                            fightsWon.set(i, fightsWon.get(i) + 1);
                        }
                    }
                }
            }
            for(int i = 0; i < petList.size();i++)
            {
                for(Playable won:winner)
                {
                    if(petList.get(i).equals(won))
                    {
                        battleWins.set(i, battleWins.get(i) + 1);
                    }
                }
            }

        }
        calculateSeasonWinners();
        endOfSeasonPrint();
        //orderEndOfSeasonPrints();
        //endOfSeasonPrints();
    }

    /**
     * Calculates the winner of a season after running all battles
     */
    public void calculateSeasonWinners()
    {
        int max = 0;
        for(Integer num: battleWins)
        {
            if(num > max)
            {
                max = num;
            }
        }
        for(int i = 0; i < battleWins.size(); i++)
        {
            if(battleWins.get(i) == max)
            {
                winners.add(petList.get(i));
            }
        }
    }

    /**
     * Iterates through all pets in the season and prints out the necessary end of season prints
     */
    public void endOfSeasonPrints()
    {
//        for(Playable pet: endOfSeasonPrintList)
        for(Playable pet: petList)
        {
            printOnePet(pet);
        }
    }

    /**
     * Prints out the necessary end of season prints for one pet
     * @param pet - the pet for which the prints will represent
     */
    public void printOnePet(Playable pet)
    {
        int battlesWon = 0;
        for(int i = 0; i < petList.size();i++)
        {
            if(pet.equals(petList.get(i)))
            {
                battlesWon = battleWins.get(i);
            }
        }
        int battlesLost = totalBattles - battlesWon;
        outputManager.printOutput(pet.getPetName() + " won " + battlesWon + " battles :)\tAnd lost " + battlesLost + " battles :(\n");
    }

    /**
     * Iterates through season data to correctly order the prints at the end of a season
     */

    public void endOfSeasonPrint(){
        endOfSeasonPrintList.clear();
        endOfSeasonPrintList.add(0);
        for (int i = 1; i < petList.size(); i++){
           for (int j = 0; j < endOfSeasonPrintList.size(); j++){
               if (compareWins(i, endOfSeasonPrintList.get(j)) == 1 || compareWins(i, endOfSeasonPrintList.get(j)) == 0){
                   endOfSeasonPrintList.add(j, i);
                   break;
               }
            }
           endOfSeasonPrintList.add(i);
        }

        for (int i = 0; i < petList.size(); i++){
            outputManager.printOutput(petList.get(endOfSeasonPrintList.get(i)).getPetName() + " won " + battleWins.get(endOfSeasonPrintList.get(i)) + " battles :)\tAnd lost " + (totalBattles - battleWins.get(endOfSeasonPrintList.get(i))) + " battles :(\n");
        }
    }

    private int compareWins(int index1, int index2){
        if (battleWins.get(index1) > battleWins.get(index2)){
            return 1;
        } else if (battleWins.get(index1) < battleWins.get(index2)) {
            return -1;
        }else if (fightsWon.get(index1) > fightsWon.get(index2)){
            return 1;
        }else if (fightsWon.get(index1) < fightsWon.get(index2)){
            return -1;
        }else{
            return 0;
        }
    }

    /*
    public void orderEndOfSeasonPrints()
    {
        battleWinsCopy.addAll(battleWins);
        List<Playable> petListCopy = new ArrayList<>();
        petListCopy.addAll(petList);
        while(!(petListCopy.isEmpty()))
        {
            List<Playable> checkMultiple = new ArrayList<>();
            int max = 0;
            for(Integer num: battleWinsCopy)
            {
                if(num > max)
                {
                    max = num;
                }
            }
            List<Playable> tracker = new ArrayList<>(petList.size());
            for(int i = 0; i < battleWinsCopy.size();i++)
            {
                if(battleWinsCopy.get(i) == max)
                {
                    checkMultiple.add(petListCopy.get(i));
                    tracker.add(petListCopy.get(i));
                }
            }
            if(checkMultiple.size() == 1)
            {
                endOfSeasonPrintList.add(checkMultiple.get(0));
                for(int i = 0; i < petListCopy.size();i++)
                {
                    if(petListCopy.get(i).equals(checkMultiple.get(0)))
                    {
                        petListCopy.remove(petListCopy.get(i));
                        battleWinsCopy.remove(battleWinsCopy.get(i));
                    }
                }
            }
            else if(checkMultiple.size() > 1)
            {
                List<Playable> ordered = orderBattleTie(checkMultiple);
                for(Playable pet: ordered)
                {
                    endOfSeasonPrintList.add(pet);
                }
                List<Integer> trackDelete = new ArrayList<>();
                trackDelete.add(1);
                while(!(trackDelete.isEmpty()))
                {
                    trackDelete.clear();
                    for(int i = 0; i < petListCopy.size();i++)
                    {
                        for(Playable pet2: ordered)
                        {
                            if(petListCopy.get(i).equals(pet2))
                            {
                                trackDelete.add(i);
                            }
                        }
                    }
                    if(!(trackDelete.isEmpty()))//changed to !
                    {
                        petListCopy.remove(trackDelete.get(0));
                        battleWinsCopy.remove(trackDelete.get(0));
                        //battleWinsCopy.remove(battleWinsCopy.get(trackDelete.get(0))); was this one
                    }
                }
            }
        }
    }
     */

    /**
     * Used when ordering the end of season prints in the case of a tie in battle wins
     * @param pets - a list of Playables representing the pets who tied
     * @return a list of the tied Playables inthe correct order
     */
    /*
    public List<Playable> orderBattleTie(List<Playable> pets)
    {
        List<Playable> order = new ArrayList<>();
        List<Playable> tempOrder = new ArrayList<>();
        tempOrder.addAll(pets);
        List<Integer> nums = new ArrayList<>();
        for(int i = 0; i < petList.size();i++)
        {
            for(Playable pet: pets)
            {
                if(petList.get(i).equals(pet))
                {
                    nums.add(fightsWon.get(i));
                }
            }
        }
        while(!(tempOrder.isEmpty()))
        {
            int max = 0;
            for(Integer number: nums)
            {
                if(number > max)
                {
                    max = number;
                }
            }
            int removed = 0;
            for(int i = 0; i < tempOrder.size();i++)
            {
                if(nums.get(i) == max)
                {
                    order.add(tempOrder.get(i));
                    removed = i;
                }
            }
            tempOrder.remove(removed);
            nums.remove(removed);
        }
        return order;
    }

     */

}
