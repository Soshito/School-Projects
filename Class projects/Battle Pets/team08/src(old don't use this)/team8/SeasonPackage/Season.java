package team8.SeasonPackage;

import team8.BattlePackage.BattleManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Season implements Iterable<BattleManager> {
    private List<BattleManager> battleList = new ArrayList<>();

    /**
     * Basic constructor
     */
    public Season() {
    }

    public List<BattleManager> getBattleList() {
        return battleList;
    }

    public void setBattleList(List<BattleManager> battleList) {
        this.battleList = battleList;
    }

    @Override
    public Iterator<BattleManager> iterator() {
        return new BattleListIterator();
    }

    private class BattleListIterator implements Iterator<BattleManager>
    {
        private int currentIndex = 0;

        /**
         * Iterator method to determine if there is a next element in the Iterable
         * @return boolean representing whether there is another element in the Iterable
         */

        @Override
        public boolean hasNext() {
            return currentIndex < battleList.size();
        }

        /**
         * Iterator method to get the next element in the Iterable
         * @return the next element in the Iterable
         */

        @Override
        public BattleManager next() {
            return battleList.get(currentIndex++);
        }

        /**
         * Iterator method to remove an element from the Iterable
         */
        @Override
        public void remove() {
            battleList.remove(--currentIndex);
        }
    }
}
