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

    private final int      vertices;     // total number of locations
    private final int[][]  adjMatrix;    // stores road distances
    private final String[] locationNames;// human-readable names for each index

    public Graph(int vertices, String[] names) {
        this.vertices      = vertices;
        this.locationNames = names;
        this.adjMatrix     = new int[vertices][vertices];
        // all entries default to 0 (no road)
    }

    /*
     * Adds a two-way road between two locations with the given distance.
     * We set both [src][dest] and [dest][src] because roads go both ways.
     */
    public void addEdge(int src, int dest, int weight) {
        adjMatrix[src][dest] = weight;
        adjMatrix[dest][src] = weight;
    }

    /*
     * Dijkstra's Shortest Path Algorithm
     * ------------------------------------
     * Step-by-step:
     *   1. Set distance to source = 0, all others = infinity.
     *   2. Use a min-heap to always process the nearest unvisited location.
     *   3. For each neighbour of the current location, check if going
     *      through the current location gives a shorter path.
     *   4. If yes, update that neighbour's distance and add it to the heap.
     *   5. Repeat until the destination is reached.
     *   6. Reconstruct the path by backtracking through the 'prev' array.
     */
    public void dijkstra(int source, int destination) {
        int[]     dist    = new int[vertices];     // shortest known distance to each location
        boolean[] visited = new boolean[vertices]; // true once a location's shortest path is confirmed
        int[]     prev    = new int[vertices];     // tracks which location we came from

        Arrays.fill(dist, Integer.MAX_VALUE); // start with "infinity" for all
        Arrays.fill(prev, -1);                // no previous location yet
        dist[source] = 0;                     // distance to ourselves is 0

        // min-heap stores [distance, vertex] pairs; smallest distance is processed first
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        pq.offer(new int[]{0, source});

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int   u    = curr[1]; // current location index

            if (visited[u]) continue; // already found the shortest path to u
            visited[u] = true;

            // check every possible neighbour of u
            for (int v = 0; v < vertices; v++) {
                // skip if no road, or already visited
                if (adjMatrix[u][v] != 0 && !visited[v]) {
                    int newDist = dist[u] + adjMatrix[u][v];
                    if (newDist < dist[v]) {
                        // found a shorter path to v — update it
                        dist[v] = newDist;
                        prev[v] = u; // remember we reached v through u
                        pq.offer(new int[]{newDist, v});
                    }
                }
            }
        }

        // -- print the result --
        System.out.println("[Route] Shortest path from "
                + locationNames[source] + " to " + locationNames[destination] + ":");

        if (dist[destination] == Integer.MAX_VALUE) {
            System.out.println("  No path found.");
            return;
        }

        // backtrack from destination to source using the prev array, then reverse
        List<String> path = new ArrayList<>();
        for (int at = destination; at != -1; at = prev[at]) {
            path.add(locationNames[at]);
        }
        Collections.reverse(path);

        System.out.println("  Path : " + String.join(" -> ", path));
        System.out.println("  Total distance: " + dist[destination] + " km");
    }

    /*
     * Prints all existing roads (edges) in the graph.
     * We only print each pair once (i < j) to avoid duplicates.
     */
    public void displayEdges() {
        System.out.println("[Map] All road connections:");
        for (int i = 0; i < vertices; i++) {
            for (int j = i + 1; j < vertices; j++) {
                if (adjMatrix[i][j] != 0) {
                    System.out.println("  " + locationNames[i]
                            + " <-> " + locationNames[j]
                            + " : "   + adjMatrix[i][j] + " km");
                }
            }
        }
    }
}
