package com.jad.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Node<E> {
    private final E value;
    private final List<Link<E>> links = new ArrayList<>();

    public Node(E value) {
        this.value = value;

    }

    public void connectTo(Node<E> node, final int weight) {
        if (!this.isAlreadyConnectedTo(node)) {
            final Link<E> link = new Link<>(this, node, weight);
            this.links.add(link);
            node.links.add(link);
        }
    }

    private boolean isAlreadyConnectedTo(final Node<E> node) {
        for (final Link<E> link : this.links) {
            if (link.getFirst() == node || link.getSecond() == node) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Node{" +
                "value=" + getValue() +
                '}';
    }

    public E getValue() {
        return this.value;
    }

    public List<Link<E>> getLinks() {
        return Collections.unmodifiableList(this.links);
    }
}