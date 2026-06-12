package AppSystem;

import java.util.*;

/*
 * Graph.java
 * ----------
 * Represents the delivery map as a weighted undirected graph.
 * Locations are vertices; roads between them are edges with a
 * weight equal to distance in km.
 *
 * Storage: Adjacency Matrix — a 2D array where
 *   adjMatrix[i][j] = distance between location i and location j
 *   adjMatrix[i][j] = 0 means no direct road between i and j
 *
 * Why adjacency matrix?
 *   Simple to implement and fast to check if a road exists: O(1).
 *   Works well when the number of locations is small and fixed.
 *
 * Dijkstra's Algorithm (implemented in dijkstra()):
 *   Finds the shortest path from a source to a destination by
 *   always expanding the unvisited location with the lowest
 *   accumulated distance. Uses a min-heap (PriorityQueue) to
 *   efficiently pick the next location — O((V + E) log V).
 */
public class Graph {

    // The total number of locations (restaurants + customers) in the map
    private final int      vertices;

    // 2D array storing distances between locations
    // adjMatrix[i][j] = distance in km from location i to location j
    // A value of 0 means there is no direct road between those two locations
    private final int[][]  adjMatrix;

    // Human-readable names for each location index, e.g. "[Rest] Burger King"
    private final String[] locationNames;

    // Constructor: sets up the graph with the given number of locations and their names
    public Graph(int vertices, String[] names) {
        this.vertices      = vertices;
        this.locationNames = names;
        // Create the 2D array — all values start at 0 (no roads yet)
        this.adjMatrix     = new int[vertices][vertices];
    }

    /*
     * Adds a two-way road between two locations with the given distance.
     * Because roads go both ways, we set the distance in both directions:
     *   adjMatrix[src][dest] = weight
     *   adjMatrix[dest][src] = weight
     */
    public void addEdge(int src, int dest, int weight) {
        adjMatrix[src][dest] = weight; // road from src to dest
        adjMatrix[dest][src] = weight; // road from dest back to src (same distance)
    }

    /*
     * Dijkstra's Shortest Path Algorithm
     * ------------------------------------
     * Finds the shortest route from 'source' to 'destination' on the map.
     *
     * Step-by-step:
     *   1. Set the distance to the source as 0 and all other distances as infinity.
     *   2. Use a min-heap to always visit the unvisited location with the smallest known distance.
     *   3. For each neighbour of the current location, check if travelling through
     *      the current location gives a shorter distance than what we already know.
     *   4. If yes, update that neighbour's distance and add it to the heap.
     *   5. Repeat until we've processed the destination.
     *   6. Trace back the path using the 'prev' array (which stores where we came from).
     */
    public void dijkstra(int source, int destination) {
        // dist[i] = the shortest known distance from the source to location i
        int[]     dist    = new int[vertices];

        // visited[i] = true once we've confirmed the shortest path to location i
        boolean[] visited = new boolean[vertices];

        // prev[i] = the location we came from to reach location i on the shortest path
        // Used at the end to reconstruct the full route
        int[]     prev    = new int[vertices];

        // Start by assuming we can't reach anywhere (infinity distance)
        Arrays.fill(dist, Integer.MAX_VALUE);
        // No previous location is known yet
        Arrays.fill(prev, -1);
        // The distance from the source to itself is always 0
        dist[source] = 0;

        // Min-heap: stores pairs of [distance, locationIndex]
        // The location with the smallest accumulated distance is always processed next
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        pq.offer(new int[]{0, source}); // start by processing the source location

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();  // get the unvisited location with the smallest distance
            int   u    = curr[1];    // u is the index of that location

            // If we've already confirmed the shortest path to u, skip it
            if (visited[u]) continue;
            visited[u] = true;

            // Check every possible neighbour of the current location u
            for (int v = 0; v < vertices; v++) {
                // Only consider locations that have a direct road and haven't been confirmed yet
                if (adjMatrix[u][v] != 0 && !visited[v]) {
                    // Calculate the distance if we travel through u to reach v
                    int newDist = dist[u] + adjMatrix[u][v];

                    if (newDist < dist[v]) {
                        // We found a shorter way to reach v — update it
                        dist[v] = newDist;
                        prev[v] = u; // remember that the best path to v goes through u
                        pq.offer(new int[]{newDist, v}); // add v to the heap for processing
                    }
                }
            }
        }

        // Print the result
        System.out.println("[Route] Shortest path from "
                + locationNames[source] + " to " + locationNames[destination] + ":");

        // If the destination's distance is still infinity, there's no path
        if (dist[destination] == Integer.MAX_VALUE) {
            System.out.println("  No path found.");
            return;
        }

        // Trace back from the destination to the source using the 'prev' array
        // Each step goes one location backward along the shortest route
        List<String> path = new ArrayList<>();
        for (int at = destination; at != -1; at = prev[at]) {
            path.add(locationNames[at]);
        }
        // The path was built backwards (destination → source), so we reverse it
        Collections.reverse(path);

        System.out.println("  Path : " + String.join(" -> ", path));
        System.out.println("  Total distance: " + dist[destination] + " km");
    }

    /*
     * Prints all roads that exist in the map.
     * We only print each pair once (when i < j) to avoid printing the same
     * road twice (e.g. "A <-> B" and "B <-> A").
     */
    public void displayEdges() {
        System.out.println("[Map] All road connections:");
        for (int i = 0; i < vertices; i++) {
            for (int j = i + 1; j < vertices; j++) {
                // Only print if there's actually a road between location i and j
                if (adjMatrix[i][j] != 0) {
                    System.out.println("  " + locationNames[i]
                            + " <-> " + locationNames[j]
                            + " : "   + adjMatrix[i][j] + " km");
                }
            }
        }
    }
}
