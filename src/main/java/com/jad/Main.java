package com.jad;

import com.jad.graph.Graph;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.Hashtable;

public class Main {
    public static void main(String[] args) throws IOException {
        final Graph graph = new Graph();
        String json = new String(Main.class.getResourceAsStream("/AventuriersDuRailEurope-links.json").readAllBytes());
        graph.loadFromJson(json);
        System.out.println(graph);
        System.out.println(graph.getBoruvkaTree());
        Hashtable<String, Pair<Integer, String>> dijkstraFromParis = graph.getDijkstraArrayFrom("Paris");
        for (String city : dijkstraFromParis.keySet()) {
            System.out.println(city + "\t" + dijkstraFromParis.get(city).getLeft() + "\t" + dijkstraFromParis.get(
                    city).getRight());
        }
    }
}