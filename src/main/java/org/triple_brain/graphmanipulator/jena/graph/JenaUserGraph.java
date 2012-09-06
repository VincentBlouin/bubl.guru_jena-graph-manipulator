package org.triple_brain.graphmanipulator.jena.graph;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import org.triple_brain.graphmanipulator.jena.TripleBrainModel;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.*;
import org.triple_brain.module.model.graph.exceptions.InvalidDepthOfSubVerticesException;
import org.triple_brain.module.model.graph.exceptions.NonExistingResourceException;

import java.io.StringWriter;

import static org.triple_brain.graphmanipulator.jena.JenaConnection.modelMaker;
import static org.triple_brain.graphmanipulator.jena.TripleBrainModel.SITE_URI;
/**
 * Copyright Mozilla Public License 1.1
 */
public class JenaUserGraph implements UserGraph {

    private Model userModel;
    private User user;

    public static JenaUserGraph withUser(User user){
        return new JenaUserGraph(user);
    }

    protected JenaUserGraph(User user){
        userModel = modelMaker().getNamedModel(user.mindMapURIFromSiteURI(SITE_URI));
        TripleBrainModel.withEnglobingModel(userModel).incorporate();
        this.user = user;
    }


    @Override
    public SubGraph graphWithDefaultVertexAndDepth(Integer depthOfSubVertices) throws InvalidDepthOfSubVerticesException {
        Vertex defaultCenterVertex = defaultVertex();
        return graphWithDepthAndCenterVertexId(depthOfSubVertices, defaultCenterVertex.id());

    }

    @Override
    public SubGraph graphWithDepthAndCenterVertexId(Integer depthOfSubVertices, String centerVertexURI) throws NonExistingResourceException {
        Resource centralVertex = model().getResource(centerVertexURI);
        if (!model().containsResource(centralVertex)) {
            throw new NonExistingResourceException(centerVertexURI);
        }
        if (depthOfSubVertices < 0) {
            throw new InvalidDepthOfSubVerticesException(depthOfSubVertices, centerVertexURI);
        }
        SubGraph subGraph = subGraphWithCenterVertexAndDepth(
                JenaVertex.loadUsingResourceOfOwner(centralVertex, user),
                depthOfSubVertices
        );
        return subGraph;
    }

    public String toRDFXML() {
        StringWriter rdfXML = new StringWriter();
        model().write(rdfXML);
        return rdfXML.toString();
    }

    private SubGraph subGraphWithCenterVertexAndDepth(Vertex centerVertex, int maximumDepth) {
        return JenaSubGraphExtractor.withMaximumDepthWholeModelCentralVertexAndUser(
                maximumDepth,
                model(),
                (JenaVertex) centerVertex,
                user
        ).extract();
    }

    public Model model() {
        return userModel;
    }

    public boolean containsElement(GraphElement graphElement) {
        Resource resource = model().getResource(graphElement.id());
        return model().containsResource(resource);
    }

    public Vertex vertexWithURI(String uri){
        Resource vertex = model().getResource(uri);
        if (!model().containsResource(vertex)) {
            throw new NonExistingResourceException(uri);
        }
        return JenaVertex.loadUsingResourceOfOwner(vertex, user);
    }

    public Edge edgeWithUri(String uri){
        Resource edge = model().getResource(uri);
        if (!model().containsResource(edge)) {
            throw new NonExistingResourceException(uri);
        }
        return JenaEdge.loadWithResourceOfOwner(edge, user);
    }

    @Override
    public Vertex defaultVertex(){
        return JenaVertex.loadUsingResourceOfOwner(
                userModel.getResource(user.URIFromSiteURI(SITE_URI) + "default"),
                user
        );
    }

    @Override
    public User user() {
        return user;
    }

    @Override
    public void remove() {
        model().removeAll();
    }
}
