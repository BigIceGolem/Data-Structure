package AppSystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class Graph {
    private final int vertices;
    private final int[][] adjMatrix;
    private final String[] locationNames;

    public Graph(int vertices, String[] names) {
        this.vertices = vertices;
        this.locationNames = names;
        this.adjMatrix = new int[vertices][vertices];
    }

    public void addEdge(int src, int dest, int weight) {
        adjMatrix[src][dest] = weight;
        adjMatrix[dest][src] = weight;
    }

    public void displayEdges() {
        System.out.println("[Map] All edges:");
        for (int i = 0; i < vertices; i++)
            for (int j = i + 1; j < vertices; j++)
                if (adjMatrix[i][j] != 0)
                    System.out.println("  " + locationNames[i] + " <-> " + locationNames[j]
                            + " : " + adjMatrix[i][j] + " km");
    }

    public void dijkstra(int source, int destination) {
        int[] dist = new int[vertices];
        boolean[] visited = new boolean[vertices];
        int[] prev = new int[vertices];

        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(prev, -1);
        dist[source] = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        pq.offer(new int[]{0, source});

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int u = curr[1];
            if (visited[u]) continue;
            visited[u] = true;

            for (int v = 0; v < vertices; v++) {
                if (adjMatrix[u][v] != 0 && !visited[v]) {
                    int newDist = dist[u] + adjMatrix[u][v];
                    if (newDist < dist[v]) {
                        dist[v] = newDist;
                        prev[v] = u;
                        pq.offer(new int[]{newDist, v});
                    }
                }
            }
        }

        System.out.println("[Route] Shortest path from " + locationNames[source]
                + " to " + locationNames[destination] + ":");
        if (dist[destination] == Integer.MAX_VALUE) {
            System.out.println("  No path found.");
            return;
        }

        List<String> path = new ArrayList<>();
        for (int at = destination; at != -1; at = prev[at]) {
            path.add(locationNames[at]);
        }
        Collections.reverse(path);
        System.out.println("  Path : " + String.join(" -> ", path));
        System.out.println("  Total distance: " + dist[destination] + " km");
    }
}
