package org.triple_brain.graphmanipulator.jena.graph;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import org.triple_brain.module.graph_manipulator.GraphManipulator;
import org.triple_brain.module.graph_manipulator.exceptions.InvalidDepthOfSubVerticesException;
import org.triple_brain.module.graph_manipulator.exceptions.NonExistingResourceException;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.Edge;
import org.triple_brain.module.model.graph.Graph;
import org.triple_brain.module.model.graph.GraphElement;
import org.triple_brain.module.model.graph.Vertex;

import java.io.StringWriter;

import static org.triple_brain.graphmanipulator.jena.JenaConnection.modelMaker;
import static org.triple_brain.graphmanipulator.jena.TripleBrainModel.*;
/**
 * Copyright Mozilla Public License 1.1
 */
public class JenaGraphManipulator implements GraphManipulator {

    private Model userModel;
    private User user;

    public static JenaGraphManipulator withUser(User user){
        return new JenaGraphManipulator(user);
    }

    protected JenaGraphManipulator(User user){
        userModel = modelMaker().openModel(user.mindMapURIFromSiteURI(SITE_URI));
        this.user = user;
    }

    public static void createUserGraph(User user){
        Model model = modelMaker().createModel(user.mindMapURIFromSiteURI(SITE_URI));
        Vertex vertex = createDefaultVertexForUserAndModel(user, model);
        vertex.label("me");
    }

    public static Vertex createDefaultVertexForUserAndModel(User user, Model model){
        String newVertexURI = user.URIFromSiteURI(SITE_URI) + "default";
        return JenaVertex.createUsingModelUriAndOwner(model, newVertexURI, user);
    }

    public Graph graphWithDefaultVertexAndDepth(Integer depthOfSubVertices) throws InvalidDepthOfSubVerticesException {
        Vertex defaultCenterVertex = defaultVertex();
        return graphWithDepthAndCenterVertexId(depthOfSubVertices, defaultCenterVertex.id());

    }

    public Graph graphWithDepthAndCenterVertexId(Integer depthOfSubVertices, String centerVertexURI) throws NonExistingResourceException {
        Resource centralVertex = model().getResource(centerVertexURI);
        if (!model().containsResource(centralVertex)) {
            throw new NonExistingResourceException(centerVertexURI);
        }
        if (depthOfSubVertices < 0) {
            throw new InvalidDepthOfSubVerticesException(depthOfSubVertices, centerVertexURI);
        }
        Graph subGraph = subGraphWithCenterVertexAndDepth(
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

    private Graph subGraphWithCenterVertexAndDepth(JenaVertex centerVertex, int maximumDepth) {
        return JenaSubGraphExtractor.withMaximumDepthWholeModelCentralVertexAndUser(
                maximumDepth,
                model(),
                centerVertex,
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

    public Vertex defaultVertex(){
        return JenaVertex.loadUsingResourceOfOwner(
                userModel.getResource(user.URIFromSiteURI(SITE_URI) + "default"),
                user
        );
    }
}
