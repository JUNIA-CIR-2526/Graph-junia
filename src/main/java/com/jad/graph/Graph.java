package com.jad.graph;

import com.google.gson.Gson;
import com.jad.treenode.NaryTreeNode;

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

    public NaryTreeNode<String> getBoruvkaTree() {

        List<BoruvkaNode> boruvkaNodes = new ArrayList<>();
        for (final Node<String> node : this.nodes) {
            boruvkaNodes.add(new BoruvkaNode(boruvkaNodes.size(), new ArrayList<>(List.of(node))));
        }
        final List<Link<String>> result = new ArrayList<>();
        final List<Link<String>> links = this.getAllLinks();
        while (boruvkaNodes.size() > 1) {
            Link<String> minLink = this.popMinLink(links, boruvkaNodes);
            if (minLink == null) {
                throw new IllegalStateException("Graph is not fully connected");
            }
            BoruvkaNode firstNode = this.getBoruvkaNodeByNode(minLink.getFirst(), boruvkaNodes);
            BoruvkaNode secondNode = this.getBoruvkaNodeByNode(minLink.getSecond(), boruvkaNodes);
            if (firstNode.id() != secondNode.id()) {
                // à finir semaine prochaine
            }
        }
        return null;
    }

    private List<Link<String>> getAllLinks() {
        final List<Link<String>> links = new ArrayList<>();
        for (final Node<String> node : this.nodes) {
            for (final Link<String> link : node.getLinks()) {
                if (!links.contains(link)) {
                    links.add(link);
                }
            }
        }
        return links;
    }

    private Link<String> popMinLink(final List<Link<String>> links, final List<BoruvkaNode> boruvkaNodes) {
        Link<String> minLink = null;
        for (final Link<String> link : links) {
            if (minLink == null || link.getWeight() < minLink.getWeight()) {
                boolean isValid = false;
                for (final BoruvkaNode boruvkaNode : boruvkaNodes) {
                    if (boruvkaNode.nodes().contains(link.getFirst()) && boruvkaNode.nodes().contains(
                            link.getSecond())) {
                        isValid = true;
                        break;
                    }
                }
                if (isValid) {
                    minLink = link;
                }
            }
        }
        links.remove(minLink);
        return minLink;
    }

    private BoruvkaNode getBoruvkaNodeByNode(final Node<String> first, final List<BoruvkaNode> boruvkaNodes) {
        for (final BoruvkaNode boruvkaNode : boruvkaNodes) {
            if (boruvkaNode.nodes().contains(first)) return boruvkaNode;
        }
        throw new IllegalStateException("Node not found in any Boruvka node");
    }

    private record BoruvkaNode(int id, ArrayList<Node<String>> nodes) {
    }

    private record JSonLink(int weight, List<String> nodes) {
    }
}
