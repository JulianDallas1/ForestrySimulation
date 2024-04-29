package CanadianForestService;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


/*
We need to make a Forest class:
    1) Print the current forest
    2) Add a new randomly generated tree
    3) Cut down a tree by index
    4) Reap the trees over a specified height
    5) Save the forest to a .db file
    6) Load a different forest from it's .db file
    7) Move to the next forest in the tree simulation
    8) Exit the program
 */

class Forest implements Serializable {
    private static final int MIN_HEIGHT_TO_PLANT = 10;
    private static final int MIN_GROWTH_RATE = 10;
    private static final long serialVersionUID = 1L;
    String name;
    List<Tree> trees;

    public Forest(String name) {
        this.name = name;
        this.trees = new ArrayList<>();
    }

    // This method is going to print the forest
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Forest name: " + name + "\n");
        for (int i = 0; i < trees.size(); i++) {
            sb.append("     ").append(i).append(" ").append(trees.get(i).toString()).append("\n");
        }
        sb.append("There are ").append(trees.size()).append(" trees, with an average height of ")
                .append(String.format("%.2f", averageHeight())).append("\n");
        return sb.toString();
    }

    // This method will add a tree to the list (List<Tree> trees)
    public void addTree(Tree tree) {
        trees.add(tree);
    }

    // Method to cut down a tree by its index
    public void cutDownTree(int index) {
        if (index >= 0 && index < trees.size()) {
            trees.remove(index);
        } else {
            System.out.println("Tree number " + index + " does not exist.");
        }
    }

    // Method to grow the trees
    public void grow() {
        for (Tree tree : trees) {
            tree.grow();
        }
    }

    // Method that will reap trees over a certain height
    public void reap(double maxHeight) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < trees.size(); i++) {
            Tree tree = trees.get(i);
            if (tree.height > maxHeight) {
                System.out.printf("Reaping the tall tree  %s   %d   %.2f'  %.1f%%\n",
                        tree.species, tree.plantingYear, tree.height, tree.growthRate);

                // Replacing old tree with new one
                TreeSpecies newSpecies = TreeSpecies.values()[random.nextInt(TreeSpecies.values().length)];
                int newYear = Calendar.getInstance().get(Calendar.YEAR);
                double newHeight = MIN_HEIGHT_TO_PLANT + random.nextDouble() * MIN_HEIGHT_TO_PLANT;
                double newGrowthRate = MIN_GROWTH_RATE + random.nextDouble() * MIN_GROWTH_RATE;

                // Replace the old tree with the new tree
                trees.set(i, new Tree(newSpecies.toString(), newYear, newHeight, newGrowthRate));
                System.out.printf("Replaced with new tree %s   %d   %.2f'  %.1f%%\n",
                        newSpecies, newYear, newHeight, newGrowthRate);
            }
        }
    }

    // Method to save serialized forest to a .db file
    public void saveToFile(String fileName) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(this);
        } catch (IOException e) {
            throw new IOException("Failed to save forest: " + e.getMessage(), e);
        }
    }

    public static Forest loadFromFile(String fileName) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            return (Forest) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IOException("Failed to load forest: " + e.getMessage(), e);
        }
    }

    // Method for finding the average height to then use in the print method
    public double averageHeight() {
        if (trees.isEmpty()) {
            return 0;
        }

        double totalHeight = 0;
        for (Tree tree : trees) {
            totalHeight += tree.height;
        }

        return totalHeight / trees.size();
    }

} // End of the Forest class
