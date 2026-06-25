package team8.RandomPackage;

import java.util.Random;

/**
 * Singleton Random object for random damage calculation
 */
public enum RandomSingleton
{
    INSTANCE;
    private Random random;
    RandomSingleton()
    {
    }

    public Random getRandom() {
        return random;
    }

    /**
     * Used to create the Random object with a specific seed
     * @param random - Random object instatiated with input seed
     */
    public void setRandom(Random random) {
        this.random = random;
    }
}
