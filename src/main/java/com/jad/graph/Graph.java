package com.jad.graph;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

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

    private record JSonLink(int weight, List<String> nodes) {
    }

    private class BoruvkaNode {
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
