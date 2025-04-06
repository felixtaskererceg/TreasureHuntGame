//Name: Felix Tasker-Erceg
//UPI: ftas019
//Pragram Description: A treasure hunt game where treasures are randomly generated on a map.
//There can be 1-5 players, and the game cycles each "hunter" to the closest treasure until a winner is decided.



//Gets the users specifications for the game.
import java.util.*;
public class A1 {
    public static void main(String[] args) {
		
		System.out.println("Welcome to the Treasure Hunt Game!");
		
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter the initial score (1-100): ");
		int initialScore = scanner.nextInt();
		System.out.println("Enter the number of Treasures: ");
		int numberOfTreasures = scanner.nextInt();
		

		TreasureMap game = new TreasureMap(initialScore, numberOfTreasures, 50, 50, 30);
		
		System.out.println("Enter the number of hunters (1-5): ");
		int numberOfHunters = scanner.nextInt();
		scanner.nextLine();
		for (int i = 0; i < numberOfHunters; i++){
		    System.out.println("Enter the name of the " + (i + 1) + " hunter: ");
		    String name = scanner.nextLine();
		    game.addHunter(name);
		}
		
		game.start();
		game.announce();
    }
}



//Name: Felix Tasker-Erceg
//Represents generic treasure on map with a location.
class Treasure{
    public int x = 0;
    public int y= 0 ;
    protected int value = 20;
    
    public Treasure(){
    }
    
    public Treasure(int value){
        this.x = 0;
        this.y = 0;
        this.value = value;
    }
    
    public Treasure(int x, int y, int value){
        this.x = x;
        this.y = y;
        this.value = value;
    }
    
    public int getValue(){
        return value;
    }
    
    //Calculates the distance of a hunter using the x and y coordiates of both, casts to int to return.
    public int distance(int hunter_x, int hunter_y){
        return (int) Math.round(Math.sqrt(Math.pow((hunter_x - x), 2.0) + Math.pow((hunter_y-y), 2.0)));
    }
    
    public String toString(){
        return String.format("Treasure at (%d, %d) worth %s points", x, y, value);
    }
}



//Name: Felix Tasker-Erceg
//Creates a Treasure with double the value of regular treasure.
class DoubleBonusTreasure extends Treasure{
    
    public DoubleBonusTreasure(){
        super(0, 0, 20*2);
    }
    
    public DoubleBonusTreasure(int value){
        super(value*2);
    }
    
    public DoubleBonusTreasure(int x, int y, int value){
        super(x, y, value*2);
    }
}



//Name: Felix Tasker-Erceg
//Creates a trapped treasure to penalise player through score subtraction over points.
class TrapTreasure extends Treasure{
    private int penalty = 50;
    
    public TrapTreasure(){
        super();
    }
    
    public TrapTreasure(int penalty){
        super();
        this.penalty = penalty;
    }
    
    public TrapTreasure(int value, int penalty){
        super(value);
        this.penalty = penalty;
    }
    
    public TrapTreasure(int x, int y, int value, int penalty){
        super(x, y, value);
        this.penalty = penalty;
    }
    
    public int getPenalty(){
        return penalty;
    }
    
    public int getValue(){
        return value - penalty;
    }
    
    public String toString(){
        return String.format("Treasure at (%d, %d) worth %s points but has a penalty of %d", x, y, value, penalty);
    }
}



//Name: Felix Tasker-Erceg
//Creates Hunter in the game who moves accross map collecting treasures.
class Hunter {
    private String name = "Unknown";
    private int x = 0;
    private int y = 0;
    private int score = 20;
    private ArrayList<Treasure> collected = new ArrayList<>();

    public Hunter() {
    }
    
    public Hunter(String name, int initialScore) {
        this.name = name;
        this.score = initialScore;
    }
    
    public Hunter(int x, int y, String name, int initialScore) {
        this.x = x;
        this.y = y;
        this.name = name;
        this.score = initialScore;
    }

    public Treasure findClosest(ArrayList<Treasure> treasures) {
        if (treasures == null || treasures.isEmpty()) {
            return null;
        }

        //Sets closest to first in list, cycles through comparing distance to current closest and saves closest.
        Treasure closest = treasures.get(0);
        for (Treasure t : treasures) {
            if (t.distance(x, y) < closest.distance(x, y)) {
                closest = t;
            }
        }
        return closest;
    }
    
    //Attempts to collect nearest treasure.
    public boolean collect(ArrayList<Treasure> treasures) {
        Treasure closest = findClosest(treasures);
        if (closest == null) {
            return false;
        }
        
        int distance = closest.distance(x, y);
        int oldx = x;
        int oldy = y;
        
        if (score >= distance) {
            score = (score - distance) + closest.getValue();
            collected.add(closest);
            treasures.remove(closest);
            x = closest.x;
            y = closest.y;
            
            System.out.println(String.format("%s started at (%d, %d), spent %d points, and collected %s. New score: %d.",
                    name, oldx, oldy, distance, closest.toString(), score));
            return true;
            
        } else {
            System.out.println(String.format("%s started at (%d, %d), but does not have enough points to reach the treasure.",
                    name, x, y));
            return false;
        }
    }

    public String toString() {
        return String.format("Hunter %s: %d points. Treasures collected: %d", name, score, collected.size());
    }
}



//Name: Felix Tasker-Erceg
//Manages game by creating map, generating treasures, and hunters.
class TreasureMap {
    private ArrayList<Hunter> hunters;
    private ArrayList<Treasure> treasures;
    private int initialScore;

    //Initializes map with a random distribution of treasures, with double and trap treasures.
    public TreasureMap(int initialScore, int numberOfTreasures, int maxX, int maxY, int maxValue) {
        this.initialScore = initialScore;
        hunters = new ArrayList<>();
        treasures = new ArrayList<>();
        Random rand = new Random(30);
        
        for (int i = 0; i < numberOfTreasures; i++) {
            int x = rand.nextInt(maxX);
            int y = rand.nextInt(maxY);
            int value = rand.nextInt(maxValue);
            treasures.add(new Treasure(x, y, value));
        }
        
        {
            int x = rand.nextInt(maxX);
            int y = rand.nextInt(maxY);
            int value = rand.nextInt(maxValue);
            treasures.add(new DoubleBonusTreasure(x, y, value));
        }
        
        {
            int x = rand.nextInt(maxX);
            int y = rand.nextInt(maxY);
            int value = rand.nextInt(maxValue);
            int penalty = rand.nextInt(maxValue);
            treasures.add(new TrapTreasure(x, y, value, penalty));
        }
        
        for (Treasure t : treasures) {
            System.out.println(t);
        }
    }
    
    public Hunter addHunter(String name) {
        Hunter h = new Hunter(name, initialScore);
        hunters.add(h);
        return h;
    }
    
    //Each hunter attempts to collect closest treasure, if any hunter fails the game ends.
    public void start() {
        boolean gameOver = false;
        while (!gameOver && !treasures.isEmpty()) {
            for (Hunter h : hunters) {
                if (!h.collect(treasures)) {
                    gameOver = true;
                    break;
                }
            }
        }
    }
    
    public void announce() {
        System.out.println("\n--- Final Scores ---");
        for (Hunter h : hunters) {
            System.out.println(h);
        }
    }
}