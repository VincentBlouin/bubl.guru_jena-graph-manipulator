package org.triple_brain.graphmanipulator.jena.graph;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import org.triple_brain.module.graph_manipulator.GraphManipulator;
import org.triple_brain.module.graph_manipulator.exceptions.InvalidDepthOfSubVerticesException;
import org.triple_brain.module.graph_manipulator.exceptions.NonExistingResourceException;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.Graph;
import org.triple_brain.module.model.graph.GraphElement;
import org.triple_brain.module.model.graph.Vertex;

import java.io.StringWriter;

import static org.triple_brain.graphmanipulator.jena.JenaConnection.modelMaker;

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
        userModel = modelMaker().openModel(user.username());
        this.user = user;
    }

    public static void createUserGraph(User user){
        Model model = modelMaker().createModel(user.username());
        JenaVertexManipulator jenaVertexManipulator = JenaVertexManipulator.withUser(user);
        Vertex vertex = jenaVertexManipulator.createDefaultVertex();
        vertex.label("me");
    }

    public Graph graphWithDefaultVertexAndDepth(Integer depthOfSubVertices) throws InvalidDepthOfSubVerticesException {
        JenaVertexManipulator jenaVertexManipulator = JenaVertexManipulator.withUser(user);
        Vertex defaultCenterVertex = jenaVertexManipulator.defaultVertex();
        return graphWithDepthAndCenterVertexId(depthOfSubVertices, defaultCenterVertex.id());

    }

    public Graph graphWithDepthAndCenterVertexId(Integer depthOfSubVertices, String centerVertexURI) throws NonExistingResourceException {
        Resource centralVertex = graph().getResource(centerVertexURI);
        if (!graph().containsResource(centralVertex)) {
            throw new NonExistingResourceException(centerVertexURI);
        }
        if (depthOfSubVertices < 0) {
            throw new InvalidDepthOfSubVerticesException(depthOfSubVertices, centerVertexURI);
        }
        Graph subGraph = subGraphWithCenterVertexAndDepth(
                JenaVertex.withResource(centralVertex),
                depthOfSubVertices
        );
        return subGraph;
    }

    public String toRDFXML() {
        StringWriter rdfXML = new StringWriter();
        graph().write(rdfXML);
        return rdfXML.toString();
    }

    private Graph subGraphWithCenterVertexAndDepth(JenaVertex centerVertex, int maximumDepth) {
        return JenaSubGraphExtractor.withMaximumDepthWholeModelAndCenterVertex(
                maximumDepth,
                graph(),
                centerVertex
        ).extract();
    }

    public Model graph() {
        return userModel;
    }

    public boolean containsElement(GraphElement graphElement) {
        Resource resource = graph().getResource(graphElement.id());
        return graph().containsResource(resource);
    }

}
