import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

class City {
    int node;
    double x;
    double y;
    
    //contructor node x and y

    public City(int node, double x, double y) {
        this.node = node;
        this.x = x;
        this.y = y;
    }
}
  
public class TSPGreedy {

    public static City[] readCitiesFromFile(String fileName) throws IOException {
        City[] cities = new City[107]; // Taking 107 cities from the file
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            int index = 0;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" "); //read line of files spilt by space
                int node = Integer.parseInt(parts[0]); //parse value into node x and y 
                double x = Double.parseDouble(parts[1]);
                double y = Double.parseDouble(parts[2]);
                cities[index++] = new City(node, x, y); //create a city object added to array
            }
        }
        return cities;
    }
    
    // (a.x, a.y) and (b.x, b.y) are the coordinates of the two points
    // a.x - b.x gives us the difference between the x coordinates of the two points.
    // a.y - b.y gives us the difference between the y coordinates of the two points.
    // Math.pow(a.x - b.x, 2) squares the x coordinate difference.
    // Math.pow(a.y - b.y, 2) squares the y coordinate difference.
    // Math.sqrt is applied to the sum of squares to find the square root of the sum, which gives us the Euclidean distance
    public static double euclideanDistance(City a, City b) {
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }
    
    public static double totalDistance(City[] path) {
        double distance = 0; //initialise distance to 0 and iterate over the path array
        for (int i = 0; i < path.length - 1; i++) {
            distance += euclideanDistance(path[i], path[i + 1]);
        }
        // Add the distance from the last city back to the first city
        distance += euclideanDistance(path[path.length - 1], path[0]);
        return distance;
    }

    public static City[] greedyTSP(City[] cities) { //method greedytsp take city array as input and return it
        City[] path = new City[cities.length]; //create path array that is same length as cities array
        boolean[] unvisited = new boolean[cities.length]; //create a unvisited city array 

        for (int i = 0; i < cities.length; i++) {
            unvisited[i] = true;
        }

        City currentCity = cities[0]; //initialise the starting city 
        unvisited[currentCity.node - 1] = false; //set false to indicate the city have been visited
        path[0] = currentCity; //set the frist elemnt of path array to currentcity
        int pathIndex = 1; // path index set to 1 indicate first city have been added to path

        while (pathIndex < cities.length) {   // loop will continue until every city has been visited
            double minDistance = Double.MAX_VALUE; // to ensure that the first distance checked will be smaller largest value
            City closestCity = null; // any city that is found will become the closestCity 
            //track of the closest unvisited city to the current city
            
            for (City city : cities) {
                if (unvisited[city.node - 1]) { 
                	//calculates the distance between the current city and each unvisited city
                    double distance = euclideanDistance(currentCity, city);
                    if (distance < minDistance) { // if distance is smaller than current min distance
                        minDistance = distance; //min distance is set to distance
                        closestCity = city; // update closest city set to current city 
                    }
                }
            }

            currentCity = closestCity; // update current city 
            unvisited[currentCity.node - 1] = false; // unvisited array set to false to indicate the city have been visited
            path[pathIndex++] = currentCity; // path index ++
            
        }
       // Add the initial city at the end of the path
        path[cities.length - 1] = cities[0];

        return path;

        
    }

    public static void main(String[] args) {
        String fileName = "C:/Users/zheny/Desktop/AIM Coursework/TSP_107.txt";

        try {
            City[] cities = readCitiesFromFile(fileName);
            City[] path = greedyTSP(cities);

            System.out.println("Path:");
            for (City city : path) {
                System.out.println(city.node);
            }
            
            double totalDist = totalDistance(path);
            System.out.println("Total distance: " + totalDist);

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
}