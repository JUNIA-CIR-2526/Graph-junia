package com.jad;

import com.jad.graph.Graph;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        final Graph graph = new Graph();
        String json = new String(Main.class.getResourceAsStream("/AventuriersDuRailEurope-links.json").readAllBytes());
        graph.loadFromJson(json);
        System.out.println(graph);
        System.out.println(graph.getBoruvkaTree());
    }
}