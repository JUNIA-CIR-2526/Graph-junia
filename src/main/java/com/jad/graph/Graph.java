package com.jad.graph;

import com.google.gson.Gson;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

public class Graph {
    private final List<Node<String>> nodes = new ArrayList<>();

    public void loadFromJson(final String json) {
        for (final JSonLink link : new Gson().fromJson(json, JSonLink[].class)) {
            Node<String> node1 = this.getNodeByValue(link.nodes().getFirst());
            Node<String> node2 = this.getNodeByValue(link.nodes().get(1));
            if (node1 == null) {
                node1 = new Node<>(link.nodes().getFirst());
                this.nodes.add(node1);
            }
            if (node2 == null) {
                node2 = new Node<>(link.nodes().get(1));
                this.nodes.add(node2);
            }
            node1.connectTo(node2, link.weight());
        }
    }

    private Node<String> getNodeByValue(final String value) {
        for (final Node<String> node : this.nodes) {
            if (node.getValue().equals(value)) return node;
        }
        return null;
    }

    public List<Link<String>> getBoruvkaTree() {

        List<BoruvkaNode> boruvkaNodes = new ArrayList<>();
        for (final Node<String> node : this.nodes) {
            boruvkaNodes.add(new BoruvkaNode(boruvkaNodes.size(), new ArrayList<>(List.of(node))));
        }
        final List<Link<String>> result = new ArrayList<>();
        final List<Link<String>> links = this.getAllLinks();
        while (links.size() > 1) {
            Link<String> minLink = this.popMinLink(links, boruvkaNodes);
            if (minLink == null) throw new IllegalStateException("Graph is not fully connected");
            BoruvkaNode firstNode = this.getBoruvkaNodeByNode(minLink.getFirst(), boruvkaNodes);
            BoruvkaNode secondNode = this.getBoruvkaNodeByNode(minLink.getSecond(), boruvkaNodes);
            if (firstNode.getId() != secondNode.getId()) {
                this.setIdToAllNodesWithId(firstNode.getId(), secondNode.getId(), boruvkaNodes);
                result.add(minLink);
            }
        }
        return result;
    }

    private List<Link<String>> getAllLinks() {
        final List<Link<String>> links = new ArrayList<>();
        for (final Node<String> node : this.nodes) {
            for (final Link<String> link : node.getLinks()) {
                if (!links.contains(link)) links.add(link);
            }
        }
        return links;
    }

    private Link<String> popMinLink(final List<Link<String>> links, final List<BoruvkaNode> boruvkaNodes) {
        Link<String> minLink = null;
        for (final Link<String> link : links) {
            if (minLink == null || link.getWeight() < minLink.getWeight()) minLink = link;
        }
        links.remove(minLink);
        return minLink;
    }

    private BoruvkaNode getBoruvkaNodeByNode(final Node<String> first, final List<BoruvkaNode> boruvkaNodes) {
        for (final BoruvkaNode boruvkaNode : boruvkaNodes) {
            if (boruvkaNode.getNodes().contains(first)) return boruvkaNode;
        }
        throw new IllegalStateException("Node not found in any Boruvka node");
    }

    private void setIdToAllNodesWithId(final int idToSet, final int idToReplace, final List<BoruvkaNode> boruvkaNodes) {
        for (BoruvkaNode boruvkaNode : boruvkaNodes) {
            if (boruvkaNode.getId() == idToReplace) {
                boruvkaNode.setId(idToSet);
            }
        }
    }

    public Hashtable<String, Pair<Integer, String>> getDijkstraArrayFrom(final String start) {
        final Hashtable<String, Pair<Integer, String>> dijkstraArray = new Hashtable<>();
        final Node<String> startNode = this.getNodeByValue(start);
        if (startNode == null) throw new IllegalArgumentException("Start node not found in graph");
        dijkstraArray.put(start, Pair.of(0, null));
        List<Link<String>> links = new ArrayList<>(startNode.getLinks());
        while (dijkstraArray.size() < this.nodes.size()) {
            final Link<String> minLink = this.popMinLink(links, new ArrayList<>());
            if (minLink == null) throw new IllegalStateException("Graph is not fully connected");
            final Node<String> nextNode = this.getNextNode(minLink, dijkstraArray);
            final Node<String> fromNode = this.getFromNode(minLink, dijkstraArray);
            dijkstraArray.put(nextNode.getValue(), Pair.of(minLink.getWeight(), fromNode.getValue()));
            links.addAll(this.addWeightToAllLinks(nextNode.getLinks(), minLink.getWeight()));
            this.cleanLinks(links, dijkstraArray.keySet());
        }
        return dijkstraArray;
    }

    private Node<String> getNextNode(final Link<String> minLink,
                                     final Hashtable<String, Pair<Integer, String>> dijkstraArray) {
        boolean firstInDijkstra = dijkstraArray.containsKey(minLink.getFirst().getValue());
        boolean secondInDijkstra = dijkstraArray.containsKey(minLink.getSecond().getValue());
        if (firstInDijkstra && !secondInDijkstra) return minLink.getSecond();
        if (!firstInDijkstra && secondInDijkstra) return minLink.getFirst();
        throw new IllegalStateException("Both nodes of the link are already in the Dijkstra array");
    }

    private Node<String> getFromNode(final Link<String> minLink,
                                     final Hashtable<String, Pair<Integer, String>> dijkstraArray) {
        boolean firstInDijkstra = dijkstraArray.containsKey(minLink.getFirst().getValue());
        boolean secondInDijkstra = dijkstraArray.containsKey(minLink.getSecond().getValue());
        if (firstInDijkstra && !secondInDijkstra) return minLink.getFirst();
        if (!firstInDijkstra && secondInDijkstra) return minLink.getSecond();
        throw new IllegalStateException("Both nodes of the link are already in the Dijkstra array");
    }

    private List<Link<String>> addWeightToAllLinks(final List<Link<String>> links, final int weightToAdd) {
        final List<Link<String>> result = new ArrayList<>();
        for (final Link<String> link : links) {
            result.add(new Link<>(link.getFirst(), link.getSecond(), link.getWeight() + weightToAdd));
        }
        return result;
    }

    private void cleanLinks(final List<Link<String>> links, final Set<String> keys) {
        links.removeIf(link -> keys.contains(link.getFirst().getValue())
                && keys.contains(link.getSecond().getValue()));
    }

    private record JSonLink(int weight, List<String> nodes) {
    }

    private static class BoruvkaNode {
        private final ArrayList<Node<String>> nodes;
        private int id;

        public BoruvkaNode(final int id, final ArrayList<Node<String>> nodes) {
            this.nodes = nodes;
            this.id = id;
        }

        public int getId() {
            return this.id;
        }

        public void setId(final int id) {
            this.id = id;
        }

        public ArrayList<Node<String>> getNodes() {
            return this.nodes;
        }

    }

}
