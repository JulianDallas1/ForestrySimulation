package CanadianForestService;

import java.io.Serializable;

// Enumerate the types of trees
enum TreeSpecies {
    BIRCH, MAPLE, FIR;
}

/* We are going to make a tree class that holds:
    1) species
    2) planting year
    3) tree height
    4) growth rate
    5) A method to grow the trees by 10-20 %
    6) A method to return the trees' information
 */

class Tree implements Serializable {
    private static final long serialVersionUID = 1L;

    TreeSpecies species;
    int plantingYear;
    double height;
    double growthRate;

    public Tree(String species, int plantingYear, double height, double growthRate) {
        this.species = TreeSpecies.valueOf(species.toUpperCase());
        this.plantingYear = plantingYear;
        this.height = height;
        this.growthRate = growthRate;
    }

    public void grow() {
        height += height * (growthRate / 100.0);
    }

    @Override
    public String toString() {
        return String.format("%s %d %.2f' %.1f%%", species, plantingYear, height, growthRate);
    }
} // End of Tree Class
