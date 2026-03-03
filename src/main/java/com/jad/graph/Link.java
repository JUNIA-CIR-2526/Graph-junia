package com.jad.graph;

public class Link<E> {
    private final Node<E> first;
    private final Node<E> second;
    private final int weight;

    public Link(final Node<E> first, final Node<E> second, final int weight) {
        this.first = first;
        this.second = second;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Link{" +
                "first=" + getFirst() +
                ", second=" + getSecond() +
                ", weight=" + getWeight() +
                '}';
    }

    public Node<E> getFirst() {
        return this.first;
    }

    public Node<E> getSecond() {
        return this.second;
    }

    public int getWeight() {
        return this.weight;
    }
}
