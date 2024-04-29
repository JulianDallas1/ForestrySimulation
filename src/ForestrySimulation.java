import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

// @author Julian Dallas

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
} // End of Tree class


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

                // This part creates the new tree with its random specifications
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

    // Method for finding the average height
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

} // End of Forest class

/*
We need to make a simulation class that is going to use the tree and forest classes and provide output
1) We need to output the prompt: (P)rint, (A)dd, (C)ut, (G)row, (R)eap, (S)ave, (L)oad, (N)ext, e(X)it :
2) Use a switch-case statement inside a loop

 */

public class ForestrySimulation {
    private static Map<String, Forest> forests = new HashMap<>();
    private static Forest currentForest;
    private static Scanner keyboard = new Scanner(System.in);
    private static final int MIN_HEIGHT_TO_PLANT = 10;
    private static final int MIN_GROWTH_RATE = 10;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("No forest names provided on the command line.");
            return;
        }

        System.out.println("Welcome to the Forestry Simulation");
        System.out.println("----------------------------------");
        loadForests(args);


        String option;
        do {
            System.out.print("(P)rint, (A)dd, (C)ut, (G)row, (R)eap, (S)ave, (L)oad, (N)ext, e(X)it : ");
            option = keyboard.nextLine().trim().toUpperCase();
            switch (option) {
                case "P":
                    System.out.println(currentForest);
                    break;
                case "A":
                    planter();
                    break;
                case "C":
                    lumberjack();
                    break;
                case "G":
                    currentForest.grow();
                    break;
                case "R":
                    reapTrees();
                    break;
                case "S":
                    saveForest();
                    break;
                case "L":
                    loadForest();
                    break;
                case "N":
                    switchToNextForest(args);
                    break;
                case "X":
                    System.out.println("Exiting the Forestry Simulation");
                    break;
                default:
                    System.out.println("Invalid menu option, try again");
                    break;
            }
        } while (!"X".equals(option)); // X will stop the loop
    } // End of main


    // This will load the CSV files for the forests into the program
    private static void loadForests(String[] forestNames) {
        boolean montaneInitialized = false;
        for (String name : forestNames) {
            try {
                Scanner fileScanner = new Scanner(new File(name + ".csv"));
                Forest forest = new Forest(name);
                while (fileScanner.hasNextLine()) {
                    String[] data = fileScanner.nextLine().split(",");
                    Tree tree = new Tree(data[0], Integer.parseInt(data[1].trim()),
                            Double.parseDouble(data[2].trim()), Double.parseDouble(data[3].trim()));
                    forest.addTree(tree);
                }
                forests.put(name, forest);
                if (name.equals("Montane") && !montaneInitialized) {
                    currentForest = forest;
                    System.out.println("Initializing from " + name);
                    montaneInitialized = true;
                }
            } catch (FileNotFoundException e) {
                System.out.println("Error opening/reading " + name + ".csv");
            }
        }
        if (!montaneInitialized) {
            System.out.println("Montane forest not found among provided names.");
        }
    }


    /* This is the planter method that adds a new tree
       We will use the addTree method from before
     */
    private static void planter() {
        TreeSpecies species = TreeSpecies.values()[new Random().nextInt(TreeSpecies.values().length)];
        int year = Calendar.getInstance().get(Calendar.YEAR);
        double height = MIN_HEIGHT_TO_PLANT + new Random().nextDouble() * MIN_HEIGHT_TO_PLANT;
        double growthRate = MIN_GROWTH_RATE + new Random().nextDouble() * MIN_GROWTH_RATE;
        Tree tree = new Tree(species.toString(), year, height, growthRate);
        currentForest.addTree(tree);
    }


   /*
    This is the lumberjack method responsible for the removal of trees
    It uses the cutDownTree method from before
    We will also handle for invalid input using a try-catch block
    */
    private static void lumberjack() {
        while (true) {
            System.out.print("Tree number to cut down: ");
            String input = keyboard.nextLine().trim();

            try {
                int index = Integer.parseInt(input);
                if (index >= 0 && index < currentForest.trees.size()) {
                    currentForest.cutDownTree(index);
                    break;
                } else {
                    System.out.println("Tree number " + index + " does not exist");
                    System.out.println();
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("That is not an integer");
            }
        }
    }




    /*
        This is the method for reaping trees above a certain height
        It uses the reap method from before
        This also handles invalid input from the user using a try-catch block
     */
    private static void reapTrees() {
        while (true) {
            System.out.print("Height to reap from: ");
            String input = keyboard.nextLine().trim();
            try {
                double height = Double.parseDouble(input);
                currentForest.reap(height);
                break;
            } catch (NumberFormatException e) {
                System.out.println("That is not an integer");
            }
        }
    }


   /*
    This is the saveForest method where we use the saveToFile method to save the file to a .db file
    It will throw an error if there is an issue saving the file
    */
    private static void saveForest() {
        try {
            currentForest.saveToFile(currentForest.name + ".db");
        } catch (IOException e) {
            System.out.println("Error saving forest to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

   /*
   Here is the loadForest method that prompts the user to enter the name of the forest they wish to save
   This will also throw an error if there is an issue loading the forest (The user types the name of a forest that does not exist)
    */
    private static void loadForest() {
        System.out.print("Enter forest name: ");
        String name = keyboard.nextLine().trim();
        try {
            currentForest = Forest.loadFromFile(name + ".db");
        } catch (Exception e) { // Catching all other exceptions
            System.out.println("Error opening/reading " + name + ".db");
            System.out.println("Old forest retained");
        }
    }



   /*
   This is the switchToNextForest method that moves to the next forest
   This will use a for loop to iterate over forestNames and sets it to the currentForest.name)
   It will also handle when there are no more forests to move to
    */
    private static void switchToNextForest(String[] forestNames) {
        boolean found = false;
        for (int i = 0; i < forestNames.length; i++) {
            if (forestNames[i].equals(currentForest.name) && i + 1 < forestNames.length) {
                currentForest = forests.get(forestNames[i + 1]);
                System.out.println("Moving to the next forest");
                System.out.println("Initializing from " + currentForest.name);
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println("No more forests to move to.");
        }
    }
} // End of ForestrySimulation class
// Testing to see if my .zip is correct


