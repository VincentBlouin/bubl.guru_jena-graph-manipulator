package org.triple_brain.graphmanipulator.jena.graph;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import org.triple_brain.module.graph_manipulator.EdgeManipulator;
import org.triple_brain.module.graph_manipulator.exceptions.NonExistingResourceException;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.Edge;
import org.triple_brain.module.model.graph.Vertex;
import java.util.UUID;

import static org.triple_brain.graphmanipulator.jena.JenaConnection.modelMaker;
import static org.triple_brain.graphmanipulator.jena.TripleBrainModel.*;
/**
 * Copyright Mozilla Public License 1.1
 */
public class JenaEdgeManipulator implements EdgeManipulator {

    private JenaGraphElementManipulator jenaGraphElementManipulator;

    private Model userModel;
    private User user;


    public static JenaEdgeManipulator withUserAndItsModel(User user, Model userModel){
        return new JenaEdgeManipulator(user, userModel);
    }

    public static JenaEdgeManipulator withUser(User user){
        return new JenaEdgeManipulator(user);
    }

    private JenaEdgeManipulator(User user, Model userModel){
        this.userModel = userModel;
        this.user = user;
        jenaGraphElementManipulator = JenaGraphElementManipulator.withUserModel(userModel);
    }

    protected JenaEdgeManipulator(User user){
        this.user = user;
        userModel = modelMaker().openModel(user.username());
        jenaGraphElementManipulator = JenaGraphElementManipulator.withUserModel(userModel);
    }

    public Edge addRelationBetweenVertices(String sourceVertexURI, String destinationVertexURI) {
        Resource sourceVertexResource = userModel.getResource(sourceVertexURI);
        Resource destinationVertexResource = userModel.getResource(destinationVertexURI);

        if (!graph().containsResource(sourceVertexResource)) {
            throw new NonExistingResourceException(sourceVertexURI);
        }

        if (!graph().containsResource(destinationVertexResource)) {
            throw new NonExistingResourceException(destinationVertexURI);
        }

        JenaVertex sourceVertex = JenaVertex.withResource(sourceVertexResource);
        JenaVertex destinationVertex = JenaVertex.withResource(destinationVertexResource);

        JenaEdge edge = JenaEdge.withModelURIAndDestinationVertex(
                userModel,
                user.URIFromSiteURI(SITE_URI)+ UUID.randomUUID().toString(),
                destinationVertex
        );
        sourceVertex.addOutgoingEdge(edge);
        sourceVertex.addNeighbor(destinationVertex);
        destinationVertex.addNeighbor(sourceVertex);
        return edge;
    }

    public void removeEdge(String edgeId) {
        Resource edgeAsResource = userModel.getResource(edgeId);
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
        return userModel;
    }
}
