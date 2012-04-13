package org.triple_brain.graphmanipulator.jena.graph;

import com.hp.hpl.jena.rdf.model.*;
import org.triple_brain.graphmanipulator.jena.User;
import org.triple_brain.module.graph_manipulator.EdgeManipulator;
import org.triple_brain.module.graph_manipulator.exceptions.NonExistingResourceException;
import org.triple_brain.module.model.graph.Edge;
import org.triple_brain.module.model.graph.Vertex;

import static org.triple_brain.graphmanipulator.jena.graph.JenaGraphElementManipulator.jenaGraphElementManipulatorWithJenaGraphManipulator;
/**
 * Copyright Mozilla Public License 1.1
 */
public class JenaEdgeManipulator implements EdgeManipulator {

    private JenaGraphManipulator jenaGraphManipulator;
    private JenaGraphElementManipulator jenaGraphElementManipulator;

    public static JenaEdgeManipulator withJenaGraphManipulator(JenaGraphManipulator jenaGraphManipulator){
        return new JenaEdgeManipulator(jenaGraphManipulator);
    }

    private JenaEdgeManipulator(JenaGraphManipulator jenaGraphManipulator){
        this.jenaGraphManipulator = jenaGraphManipulator;
        jenaGraphElementManipulator = jenaGraphElementManipulatorWithJenaGraphManipulator(jenaGraphManipulator);
    }

    public Edge addRelationBetweenVertices(String sourceVertexURI, String destinationVertexURI) {
        Resource sourceVertexResource = defaultUser().model().getResource(sourceVertexURI);
        Resource destinationVertexResource = defaultUser().model().getResource(destinationVertexURI);

        if (!graph().containsResource(sourceVertexResource)) {
            throw new NonExistingResourceException(sourceVertexURI);
        }

        if (!graph().containsResource(destinationVertexResource)) {
            throw new NonExistingResourceException(destinationVertexURI);
        }

        JenaVertex sourceVertex = JenaVertex.withResource(sourceVertexResource);
        JenaVertex destinationVertex = JenaVertex.withResource(destinationVertexResource);

        JenaEdge edge = JenaEdge.withModelURIAndDestinationVertex(
                defaultUser().model(),
                defaultUser().URI() + defaultUser().nextId(),
                destinationVertex
        );
        sourceVertex.addOutgoingEdge(edge);
        sourceVertex.addNeighbor(destinationVertex);
        destinationVertex.addNeighbor(sourceVertex);
        return edge;
    }

    public void removeEdge(String edgeId) {
        Resource edgeAsResource = defaultUser().model().getResource(edgeId);
        if (!graph().containsResource(edgeAsResource)) {
            throw new NonExistingResourceException(edgeId);
        }
        Edge edge = JenaEdge.withResource(edgeAsResource);
        Vertex sourceVertex = edge.sourceVertex();
        Vertex destinationVertex = edge.destinationVertex();
        sourceVertex.removeOutgoingEdge(edge);
        if(!areVerticesConnectedInAnyWay(
                sourceVertex, destinationVertex
        )){
            sourceVertex.removeNeighbor(destinationVertex);
            destinationVertex.removeNeighbor(sourceVertex);
        }
        edgeAsResource.removeProperties();
    }

    private boolean areVerticesConnectedInAnyWay(Vertex vertexA, Vertex vertexB){
        return  vertexA.hasDestinationVertex(vertexB) ||
                vertexB.hasDestinationVertex(vertexA);
    }

    public JenaEdgeManipulator updateLabel(String edgeURI, String newLabel) throws NonExistingResourceException {
        jenaGraphElementManipulator.updateLabel(edgeURI, newLabel);
        return this;
    }

    public Model graph(){
        return jenaGraphManipulator.graph();
    }

    public User defaultUser(){
        return jenaGraphManipulator.defaultUser();
    }
}
