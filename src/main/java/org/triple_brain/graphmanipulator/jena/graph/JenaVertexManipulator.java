package org.triple_brain.graphmanipulator.jena.graph;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import org.triple_brain.graphmanipulator.jena.User;
import org.triple_brain.module.graph_manipulator.VertexManipulator;
import org.triple_brain.module.graph_manipulator.exceptions.NonExistingResourceException;
import org.triple_brain.module.model.graph.Edge;
import org.triple_brain.module.model.graph.Vertex;

import static com.hp.hpl.jena.vocabulary.OWL2.sameAs;
import static com.hp.hpl.jena.vocabulary.RDF.type;
import static org.triple_brain.graphmanipulator.jena.graph.JenaGraphElementManipulator.jenaGraphElementManipulatorWithJenaGraphManipulator;

/**
 * Copyright Mozilla Public License 1.1
 */
public class JenaVertexManipulator implements VertexManipulator{

    private JenaGraphManipulator jenaGraphManipulator;
    private JenaEdgeManipulator jenaEdgeManipulator;
    private JenaGraphElementManipulator jenaGraphElementManipulator;

    public static JenaVertexManipulator withJenaGraphManipulator(JenaGraphManipulator jenaGraphManipulator){
        return new JenaVertexManipulator(jenaGraphManipulator);
    }

    protected JenaVertexManipulator(JenaGraphManipulator jenaGraphManipulator){
        this.jenaGraphManipulator = jenaGraphManipulator;
        jenaGraphElementManipulator = jenaGraphElementManipulatorWithJenaGraphManipulator(jenaGraphManipulator);
        jenaEdgeManipulator = JenaEdgeManipulator.withJenaGraphManipulator(jenaGraphManipulator);
    }

    public Edge addVertexAndRelation(String sourceVertexURI) throws NonExistingResourceException {
        Resource subjectResource = graph().getResource(sourceVertexURI);
        Vertex sourceVertex = JenaVertex.withResource(subjectResource);
        if (!graph().containsResource(subjectResource)) {
            throw new NonExistingResourceException(sourceVertexURI);
        }

        String newVertexURI = defaultUser().URI() + defaultUser().nextId();
        JenaVertex newVertex = JenaVertex.withModelAndURI(defaultUser().model(), newVertexURI);

        String edgeURI = defaultUser().URI() + defaultUser().nextId();
        JenaEdge edge = JenaEdge.withModelURIAndDestinationVertex(defaultUser().model(), edgeURI, newVertex);

        sourceVertex.addOutgoingEdge(edge);

        newVertex.addNeighbor(sourceVertex);
        sourceVertex.addNeighbor(newVertex);

        return edge;
    }

    public JenaVertexManipulator removeVertex(String vertexURI) {
        Resource vertexResource = defaultUser().model().getResource(vertexURI);
        if (!graph().containsResource(vertexResource)) {
            throw new NonExistingResourceException(vertexURI);
        }
        JenaVertex vertex = JenaVertex.withResource(vertexResource);
        for(Edge edge : vertex.connectedEdges()){
            jenaEdgeManipulator.removeEdge(edge.id());
        }
        vertexResource.removeProperties();
        return this;
    }

    public JenaVertexManipulator updateLabel(String vertexURI, String newLabel) throws NonExistingResourceException{
        jenaGraphElementManipulator.updateLabel(vertexURI, newLabel);
        return this;
    }

    public JenaVertexManipulator semanticType(String vertexURI, String typeUri){
        Resource vertex = defaultUser().model().getResource(vertexURI);
        if (!graph().containsResource(vertex)) {
            throw new NonExistingResourceException(vertexURI);
        }
        Resource typeAsResource = graph().createResource(typeUri);
        vertex.addProperty(type, typeAsResource);
        return this;
    }

    public JenaVertexManipulator sameAsResourceWithUri(String vertexLocalName, String sameAsUri){
        Resource vertex = defaultUser().model().getResource(defaultUser().URI() + vertexLocalName);
        if (!graph().containsResource(vertex)) {
            throw new NonExistingResourceException(defaultUser().URI() + vertexLocalName);
        }
        Resource sameAsResource = graph().createResource(sameAsUri);
        vertex.addProperty(sameAs, sameAsResource);
        return this;
    }

    public Model graph(){
        return jenaGraphManipulator.graph();
    }

    public User defaultUser(){
        return jenaGraphManipulator.defaultUser();
    }
}
