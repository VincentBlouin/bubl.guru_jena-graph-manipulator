package org.triple_brain.graphmanipulator.jena.graph;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import org.triple_brain.graphmanipulator.jena.User;
import org.triple_brain.module.graph_manipulator.GraphManipulator;
import org.triple_brain.module.graph_manipulator.exceptions.InvalidDepthOfSubVerticesException;
import org.triple_brain.module.graph_manipulator.exceptions.NonExistingResourceException;
import org.triple_brain.module.model.graph.Graph;
import org.triple_brain.module.model.graph.GraphElement;
import org.triple_brain.module.model.graph.Vertex;

import java.io.StringWriter;

import static org.triple_brain.graphmanipulator.jena.TripleBrainModel.SITE_URI;

/**
 * Copyright Mozilla Public License 1.1
 */
public class JenaGraphManipulator implements GraphManipulator {

    protected User defaultUser = User.withUserName("Roger Lamothe");

    private Model graphTraversalProperties = ModelFactory.createDefaultModel();
    private Property VISITED_AT_DEPTH = graphTraversalProperties.createProperty(SITE_URI + "visited_at_depth");

    public static JenaGraphManipulator withDefaultUser() {
        return new JenaGraphManipulator();
    }

    protected JenaGraphManipulator() {
    }

    public Graph graphWithDefaultVertexAndDepth(Integer depthOfSubVertices) throws InvalidDepthOfSubVerticesException {
        Vertex defaultCenterVertex = JenaVertex.withResource(defaultUser.absoluteCentralVertex());
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
        return subGraphWithCenterVertexAndDepth(
                JenaVertex.withResource(centralVertex),
                depthOfSubVertices
        );
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

    public User defaultUser() {
        return defaultUser;
    }

    public Model graph() {
        return defaultUser.model();
    }

    public boolean containsElement(GraphElement graphElement) {
        Resource resource = graph().getResource(graphElement.id());
        return graph().containsResource(resource);
    }

}
