package com.company;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static com.company.SimulatedAnnealing.numberOfCities;

class City {
    int index;
    float x, y;

    City(int index, float x, float y) {
        this.index = index;
        this.x = x;
        this.y = y;
    }
    float distanceTo(City city) {
        return (float)Math.sqrt(Math.pow(city.x - this.x, 2) + Math.pow(city.y - this.y, 2));
    }
}
public class SimulatedAnnealing {

    static int numberOfCities;
    static double temp;
    static double coolingRate;
    static Random r = new Random();
    private static ArrayList<City> cities = new ArrayList<>();

    public static void main(String[] args) {

        setInitialTemperature(100000);

        setCoolingRate(0.003);

        cities = (ArrayList<City>) loadCities(); // Load the city coordinates here

        numberOfCities = cities.size();

        Solution currentSolution = new Solution();
        currentSolution.generateIndividual();

        System.out.println("Total distance of initial solution: " + currentSolution.getTotalDistance());
        System.out.println("Solution: " + currentSolution);

        Solution best = new Solution(currentSolution.getSolution());

        // Loop until system has cooled
        while (temp > 1) {
            // Create new neighbour solution
            Solution newSolution = new Solution(currentSolution.getSolution());

            // Get random positions in the solution
            int solutionPos1 = randomInt(0 , newSolution.solutionSize());
            int solutionPos2 = randomInt(0 , newSolution.solutionSize());

            //to make sure that solutionPos1 and solutionPos2 are different
            while(solutionPos1 == solutionPos2) {solutionPos2 = randomInt(0 , newSolution.solutionSize());}

            // Get the cities at selected positions in the solution
            City citySwap1 = newSolution.getCity(solutionPos1);
            City citySwap2 = newSolution.getCity(solutionPos2);

            // Swap them
            newSolution.setCity(solutionPos2, citySwap1);
            newSolution.setCity(solutionPos1, citySwap2);

            // Get energy of solutions
            int currentDistance   = currentSolution.getTotalDistance();
            int neighbourDistance = newSolution.getTotalDistance();

            // Decide if we should accept the neighbour
            double rand = r.nextInt(1000) / 1000.0;
            if (acceptanceProbability(currentDistance, neighbourDistance) > rand) {
                currentSolution = new Solution(newSolution.getSolution());
            }

            // Keep track of the best solution found
            if (currentSolution.getTotalDistance() < best.getTotalDistance()) {
                best = new Solution(currentSolution.getSolution());
            }

            // Cool system
            temp *= 1 - coolingRate;
        }

        System.out.println("Final solution distance: " + best.getTotalDistance());
        System.out.println("Solution: " + best);
    }

    private static List<City> loadCities() {
        List<City> cities = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(Paths.get("C:\\Users\\User\\OneDrive - University of Nottingham Malaysia\\Documents\\School\\Nottingham\\CLASSES\\Degree\\Year 2\\COMP2024 - Artificial Intelligence Methods\\coursework\\TSP_107.txt"));
            for (String line : lines) {
                String[] parts = line.trim().split("\\s+"); // Assumes city index, x, and y coordinates are separated by whitespace
                int index = Integer.parseInt(parts[0]) - 1; // Subtract 1 from the index
                float x = Float.parseFloat(parts[1]);
                float y = Float.parseFloat(parts[2]);
                cities.add(new City(index, x, y));
            }
        } catch (IOException e) {
            System.err.println("Error reading city coordinates from file: " + e.getMessage());
        }

        return cities;
    }

    private static void setInitialTemperature(double initialTemp) {
        temp = initialTemp;
    }
    private static void setCoolingRate(double newCoolingRate) {
        coolingRate = newCoolingRate;
    }

    public static double acceptanceProbability(int currentDistance, int newDistance) {
        // If the new solution is better, accept it
        if (newDistance < currentDistance) {
            return 1.0;
        }
        // If the new solution is worse, calculate an acceptance probability
        return Math.exp((currentDistance - newDistance) / temp);
    }

    public static int randomInt(int min , int max) {
        Random r = new Random();
        double d = min + r.nextDouble() * (max - min);
        return (int)d;
    }

    public static City getCity(int index){
        return cities.get(index);
    }
}

class Solution{

    //to hold a solution of cities
    private ArrayList<City> solution = new ArrayList<>();

    //we assume initial value of distance is 0
    private int distance = 0;

    //Constructor
    //starts an empty solution
    public Solution(){
        for (int i = 0; i < numberOfCities; i++) {
            solution.add(null);
        }
    }

    //another Constructor
    //starts a solution from another solution
    @SuppressWarnings("unchecked")
    public Solution(ArrayList<City> solution){
        this.solution = (ArrayList<City>) solution.clone();
    }

    /**
     Returns solution information
     @return currentSolution
     */
    public ArrayList<City> getSolution(){
        return solution;
    }

    /**
     * Creates a random solution (i.e. individual or candidate solution)
     */
    public void generateIndividual() {
        // Loop through all our destination cities and add them to our solution
        for (int cityIndex = 0; cityIndex < numberOfCities; cityIndex++) {
            setCity(cityIndex, SimulatedAnnealing.getCity(cityIndex));
        }
        // Randomly reorder the solution
        Collections.shuffle(solution);
    }

    /**
     * Returns a city from the solution given the city's index
     * @param index index of city
     * @return City at that index
     */
    public City getCity(int index) {
        return solution.get(index);
    }

    /**
     * Sets a city in a certain position within a solution
     * @param index index of city
     * @param city city
     */
    public void setCity(int index, City city) {
        solution.set(index, city);
        // If the solution has been altered we need to reset the fitness and distance
        distance = 0;
    }

    /**
     * Computes and returns the total distance of the solution
     * @return distance total distance of the solution
     */
    public int getTotalDistance(){
        if (distance == 0) {
            int solutionDistance = 0;
            // Loop through our solution's cities
            for (int cityIndex=0; cityIndex < solutionSize(); cityIndex++) {
                // Get city we're traveling from
                City fromCity = getCity(cityIndex);
                // City we're traveling to
                City destinationCity;
                // Check we're not on our solution's last city, if we are set our
                // solution's final destination city to our starting city
                if(cityIndex+1 < solutionSize()){
                    destinationCity = getCity(cityIndex+1);
                }
                else{
                    destinationCity = getCity(0);
                }
                // Get the distance between the two cities
                solutionDistance += fromCity.distanceTo(destinationCity);
            }
            distance = solutionDistance;
        }
        return distance;
    }

    /**
     * Get number of cities on our solution
     * @return number how many cities there are in the solution!
     */
    public int solutionSize() {
        return solution.size();
    }

    public String toString() {
        StringBuilder s = Optional.ofNullable(getCity(0).toString()).map(StringBuilder::new).orElse(null);
        for (int i = 1; i < solutionSize(); i++) {
            s = (s == null ? new StringBuilder("null") : s).append(" -> ").append(getCity(i).toString());
        }
        return s == null ? null : s.toString();
    }
}
