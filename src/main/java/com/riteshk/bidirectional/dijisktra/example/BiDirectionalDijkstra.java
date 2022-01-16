package com.riteshk.bidirectional.dijisktra.example;

import static com.google.common.collect.ImmutableMap.of;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class BiDirectionalDijkstra {

    public ShortestPath bidirectionalDijkstra(Graph graph, String source, String destination) {
        // Data structures with 'F' Suffix are required for forward search and 'B' suffix are required for backward search

        // If source and destination are same then return
        if (source.equals(destination)) {
            return new ShortestPath(ImmutableList.of(source), 0);
        }

        // Initialize our estimate to infinity and join vertex/node wen searching from backward and forward
        double estimate = Double.POSITIVE_INFINITY;
        String joinNode = null;

        Map<String, Double> distanceF = new HashMap<>(of(source, 0D)), distanceB = new HashMap<>(of(destination, 0D));
        Map<String, String> parentF = new HashMap<>(), parentB = new HashMap<>();

        PriorityQueue<PathComputation> queueF = new PriorityQueue<>(), queueB = new PriorityQueue<>();
        queueF.add(new PathComputation(0, source));
        queueB.add(new PathComputation(0, destination));

        while (!queueF.isEmpty() && !queueB.isEmpty()) {
            PathComputation pathF = queueF.peek(), pathB = queueB.peek();
            if (pathF.distance + pathB.distance >= estimate) {
                break;
            }
            Pair<Double, String> result;

            //Discover new nodes from edges and add them to queue depending on if its forward or backward search
            if (pathF.distance < pathB.distance) {
                queueF.poll();
                Collection<Edge> edges = graph.getEdges(pathF.node);
                result = discoverNodes(edges, pathF, distanceF, distanceB, queueF,
                        parentF, estimate, joinNode);

            } else {
                queueB.poll();

                // Just reverse the search parameters to discover nodes from backward
                Collection<Edge> edges = graph.getReverseEdges(pathB.node);
                result = discoverNodes(edges, pathB, distanceB, distanceF, queueB,
                        parentB, estimate, joinNode);
            }
            estimate = result.getLeft();
            joinNode = result.getRight();
        }
        // If no node is found then there is no shortest path
        if (joinNode == null) {
            return new ShortestPath(Collections.emptyList(), Double.POSITIVE_INFINITY);
        }
        //Construct the shortest path from forward and backward queues
        List<String> nodesF = traverseShortestNodes(parentF.get(joinNode), parentF);
        List<String> nodesB = traverseShortestNodes(parentB.get(joinNode), parentB);
        Collections.reverse(nodesF);
        nodesF.add(joinNode);
        nodesF.addAll(nodesB);
        return new ShortestPath(nodesF, estimate);
    }

    private Pair<Double, String> discoverNodes(Collection<Edge> edges, PathComputation pathF,
            Map<String, Double> distanceF, Map<String, Double> distanceB, PriorityQueue<PathComputation> queueF,
            Map<String, String> parentF, double estimate, String joinNode) {
        for (Edge edge : edges) {
            double w = pathF.distance + edge.weight;
            if (w < distanceF.getOrDefault(edge.destination, Double.POSITIVE_INFINITY)) {
                queueF.add(new PathComputation(w, edge.destination));
                parentF.put(edge.destination, pathF.node);
                distanceF.put(edge.destination, w);
            }
            if (distanceB.containsKey(edge.destination)) {
                double newEstimate = w + distanceB.get(edge.destination);
                if (newEstimate < estimate) {
                    estimate = newEstimate;
                    joinNode = edge.destination;
                }
            }
        }
        return new ImmutablePair<>(estimate, joinNode);
    }

    /**
     * Iterate parents to construct path
     */
    private List<String> traverseShortestNodes(String parent, Map<String, String> parentF) {
        List<String> nodes = new ArrayList<>();
        while (parent != null) {
            nodes.add(parent);
            parent = parentF.get(parent);
        }
        return nodes;
    }

    public static class Graph {

        private Map<String, Map<String, Edge>> edges = new HashMap<>();
        private Map<String, Map<String, Edge>> reverseEdges = new HashMap<>();

        public Collection<Edge> getEdges(String node) {
            return this.edges.getOrDefault(node, Collections.emptyMap()).values();
        }

        public Collection<Edge> getReverseEdges(String node) {
            return this.reverseEdges.getOrDefault(node, Collections.emptyMap())
                    .values().stream().map(e -> new Edge(e.destination, e.source, e.weight))
                    .collect(Collectors.toList());
        }

        public void addEdge(Edge edge) {
            edges.putIfAbsent(edge.source, new HashMap<>());
            edges.get(edge.source).put(edge.destination, edge);
            reverseEdges.putIfAbsent(edge.destination, new HashMap<>());
            reverseEdges.get(edge.destination).put(edge.source, edge);
        }
    }

    public static class Edge {

        private String source;
        private String destination;
        private double weight;

        public Edge(String source, String destination, double weight) {
            this.source = source;
            this.destination = destination;
            this.weight = weight;
        }
    }

    public static class ShortestPath {

        protected List<String> path;
        protected double distance;

        public ShortestPath(List<String> path, double distance) {
            this.path = path;
            this.distance = distance;
        }
    }

    public static class PathComputation implements Comparable<PathComputation> {

        private double distance;
        private String node;

        public PathComputation(double distance, String node) {
            this.distance = distance;
            this.node = node;
        }

        @Override
        public int compareTo(PathComputation o) {
            return Double.compare(distance, o.distance);
        }

    }

}
