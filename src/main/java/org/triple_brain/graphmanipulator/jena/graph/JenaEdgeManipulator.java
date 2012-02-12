package org.triple_brain.graphmanipulator.jena.graph;

import com.hp.hpl.jena.rdf.model.*;
import org.triple_brain.graphmanipulator.jena.User;
import org.triple_brain.graphmanipulator.jena.graph.exceptions.NonExistingResourceException;

import static com.hp.hpl.jena.vocabulary.RDFS.label;
import static org.triple_brain.graphmanipulator.jena.graph.JenaGraphElementManipulator.jenaGraphElementManipulatorWithJenaGraphManipulator;

/**
 * @author Vincent Blouin
 */
public class JenaEdgeManipulator {

    private JenaGraphManipulator jenaGraphManipulator;
    private JenaGraphElementManipulator jenaGraphElementManipulator;

    public static JenaEdgeManipulator jenaEdgeManipulatorWithJenaGraphManipulator(JenaGraphManipulator jenaGraphManipulator){
        return new JenaEdgeManipulator(jenaGraphManipulator);
    }

    private JenaEdgeManipulator(JenaGraphManipulator jenaGraphManipulator){
        this.jenaGraphManipulator = jenaGraphManipulator;
        jenaGraphElementManipulator = jenaGraphElementManipulatorWithJenaGraphManipulator(jenaGraphManipulator);
    }

    public Statement addRelationBetweenVertices(String sourceVertexLocalName, String destinationVertexLocalName) {

        Resource sourceVertex = defaultUser().model().getResource(defaultUser().URI() + sourceVertexLocalName);
        Resource destinationVertex = defaultUser().model().getResource(defaultUser().URI() + destinationVertexLocalName);
        if (!graph().containsResource(sourceVertex)) {
            throw new NonExistingResourceException(defaultUser().URI() + sourceVertexLocalName);
        }

        if (!graph().containsResource(destinationVertex)) {
            throw new NonExistingResourceException(defaultUser().URI() + destinationVertexLocalName);
        }

        String edgeURI = defaultUser().URI() + defaultUser().nextId();
        Resource newEdge = defaultUser().model().createProperty(edgeURI);
        newEdge.addLiteral(label, "");

        sourceVertex.addProperty((Property) newEdge, (RDFNode) destinationVertex);
        return defaultUser().model().listStatements(new SimpleSelector(sourceVertex, (Property) newEdge, (RDFNode) destinationVertex)).nextStatement();
    }

    public void removeEdge(String edgeLocalName) {
        Resource edge = defaultUser().model().getResource(defaultUser().URI() + edgeLocalName);
        if (!graph().containsResource(edge)) {
            throw new NonExistingResourceException(defaultUser().URI() + edgeLocalName);
        }
        RDFNode objectOfEdgeToDelete = graph().listObjectsOfProperty((Property) edge).toList().get(0);
        Statement statementWhereEdgeIsProperty = graph().listStatements(new SimpleSelector(null, (Property) edge, objectOfEdgeToDelete)).toList().get(0);
        graph().remove(statementWhereEdgeIsProperty);
        edge.removeProperties();
    }

    public JenaEdgeManipulator updateLabel(String edgeLocalName, String newLabel) throws NonExistingResourceException{
        jenaGraphElementManipulator.updateLabel(edgeLocalName, newLabel);
        return this;
    }

    public Model graph(){
        return jenaGraphManipulator.graph();
    }

    public User defaultUser(){
        return jenaGraphManipulator.defaultUser();
    }
}
