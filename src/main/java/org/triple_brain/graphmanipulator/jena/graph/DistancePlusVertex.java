package org.triple_brain.graphmanipulator.jena.graph;

/*
* Copyright Mozilla Public License 1.1
*/
public class DistancePlusVertex {

    private JenaVertex vertex;
    private int distance;

    public DistancePlusVertex(JenaVertex vertex, int distance){
        this.distance = distance;
        this.vertex = vertex;
    }

    public JenaVertex vertex(){
        return vertex;
    }

    public int distance(){
        return distance;
    }
}
