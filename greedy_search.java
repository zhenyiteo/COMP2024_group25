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

    public static City[] greedyTSPFromStart(City[] cities, int startIdx) {
        City[] path = new City[cities.length];
        boolean[] unvisited = new boolean[cities.length];

        for (int i = 0; i < cities.length; i++) {
            unvisited[i] = true;
        }

        City currentCity = cities[startIdx];
        unvisited[currentCity.node - 1] = false;
        path[0] = currentCity;
        int pathIndex = 1;

        while (pathIndex < cities.length) {
            double minDistance = Double.MAX_VALUE;
            City closestCity = null;

            for (City city : cities) {
                if (unvisited[city.node - 1]) {
                    double distance = euclideanDistance(currentCity, city);
                    if (distance < minDistance) {
                        minDistance = distance;
                        closestCity = city;
                    }
                }
            }

            currentCity = closestCity;
            unvisited[currentCity.node - 1] = false;
            path[pathIndex++] = currentCity;
        }

        path[cities.length - 1] = cities[startIdx];

        return path;


    }

    public static void main(String[] args) {
        String fileName = "C:/Users/zheny/Desktop/AIM Coursework/TSP_107.txt";
        int numStartingPoints = 1000000; // number of random starting points to try
        double shortestDist = Double.MAX_VALUE;
        City[] shortestPath = null;

        try {
            City[] cities = readCitiesFromFile(fileName);

            for (int i = 0; i < numStartingPoints; i++) {
                // Randomly select a starting point
                int startIdx = (int) (Math.random() * cities.length);
                City[] path = greedyTSPFromStart(cities, startIdx);

                // Calculate total distance of the path
                double totalDist = totalDistance(path);

                // Update shortest path and distance if necessary
                if (totalDist < shortestDist) {
                    shortestDist = totalDist;
                    shortestPath = path;
                }
            }

            System.out.println("Shortest path:");
            for (City city : shortestPath) {
                System.out.println(city.node);
            }
            System.out.println("Total distance: " + shortestDist);

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

    }
}