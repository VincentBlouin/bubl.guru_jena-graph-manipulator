package org.triple_brain.graphmanipulator.jena.graph;

import org.triple_brain.module.model.graph.Edge;
import org.triple_brain.module.model.graph.SubGraph;
import org.triple_brain.module.model.graph.Vertex;

import java.util.HashSet;
import java.util.Set;

/**
 * Copyright Mozilla Public License 1.1
 */
public class JenaSubGraph implements SubGraph {

    private Set<Vertex> vertices = new HashSet<Vertex>();
    private Set<Edge> edges = new HashSet<Edge>();

    public static JenaSubGraph withVerticesAndEdges(Set<Vertex> vertices, Set<Edge> edges){
        return new JenaSubGraph(vertices, edges);
    }

    protected JenaSubGraph(Set<Vertex> vertices, Set<Edge> edges){
        this.vertices = vertices;
        this.edges = edges;
    }

    @Override
    public Vertex vertexWithIdentifier(String identifier) {
        for(Vertex vertex : vertices){
            if(vertex.id().equals(identifier)){
                return vertex;
            }
        }
        return null;
    }

    @Override
    public Edge edgeWithIdentifier(String identifier) {
        for(Edge edge : edges){
            if(edge.id().equals(identifier)){
                return edge;
            }
        }
        return null;
    }

    @Override
    public int numberOfEdgesAndVertices() {
        return numberOfEdges() + numberOfVertices();
    }

    @Override
    public boolean containsVertex(Vertex vertex) {
        return vertices.contains(vertex);
    }

    @Override
    public Set<Vertex> vertices() {
        return vertices;
    }

    @Override
    public Set<Edge> edges() {
        return edges;
    }

    @Override
    public int numberOfEdges(){
        return edges.size();
    }

    @Override
    public int numberOfVertices(){
        return vertices.size();
    }
}
