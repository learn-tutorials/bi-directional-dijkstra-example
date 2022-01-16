package com.riteshk.bidirectional.dijisktra.example;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.riteshk.bidirectional.dijisktra.example.BiDirectionalDijkstra.Edge;
import com.riteshk.bidirectional.dijisktra.example.BiDirectionalDijkstra.Graph;
import com.riteshk.bidirectional.dijisktra.example.BiDirectionalDijkstra.ShortestPath;
import org.junit.jupiter.api.Test;

public class BiDirectionalDijkstraTest {

    @Test
    public void testSimpleSearch() {
        Graph graph = new Graph();
        graph.addEdge(new Edge("0", "1", 4));
        graph.addEdge(new Edge("0", "7", 8));
        graph.addEdge(new Edge("1", "7", 11));
        graph.addEdge(new Edge("1", "2", 8));
        graph.addEdge(new Edge("7", "6", 1));
        graph.addEdge(new Edge("7", "8", 7));
        graph.addEdge(new Edge("2", "8", 2));
        graph.addEdge(new Edge("2", "5", 4));
        graph.addEdge(new Edge("2", "3", 7));
        graph.addEdge(new Edge("8", "6", 6));
        graph.addEdge(new Edge("6", "5", 2));
        graph.addEdge(new Edge("3", "5", 14));
        graph.addEdge(new Edge("3", "4", 9));
        graph.addEdge(new Edge("5", "4", 10));
        ShortestPath path = new BiDirectionalDijkstra().bidirectionalDijkstra(graph, "0", "4");
        System.out.println(path.path);
        assertEquals(path.path, asList("0", "7", "6", "5", "4"));
        assertEquals(path.distance, 21);

    }

}
