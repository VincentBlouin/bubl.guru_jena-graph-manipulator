package org.triple_brain.graphmanipulator.jena.graph;

import com.hp.hpl.jena.rdf.model.*;
import org.triple_brain.graphmanipulator.jena.User;
import org.triple_brain.graphmanipulator.jena.graph.exceptions.NonExistingResourceException;

import static com.hp.hpl.jena.vocabulary.RDF.*;
import static com.hp.hpl.jena.vocabulary.OWL2.*;
import static com.hp.hpl.jena.vocabulary.RDFS.*;
import static org.triple_brain.graphmanipulator.jena.graph.JenaGraphElementManipulator.*;

/**
 * @author Vincent Blouin
 */
public class JenaVertexManipulator{

    private JenaGraphManipulator jenaGraphManipulator;
    private JenaGraphElementManipulator jenaGraphElementManipulator;

    public static JenaVertexManipulator jenaVertexManipulatorWithJenaGraphManipulator(JenaGraphManipulator jenaGraphManipulator){
        return new JenaVertexManipulator(jenaGraphManipulator);
    }

    private JenaVertexManipulator(JenaGraphManipulator jenaGraphManipulator){
        this.jenaGraphManipulator = jenaGraphManipulator;
        jenaGraphElementManipulator = jenaGraphElementManipulatorWithJenaGraphManipulator(jenaGraphManipulator);
    }

    public Statement addVertexAndRelation(String sourceVertexLocalName) throws NonExistingResourceException {
        Resource subjectResource = graph().getResource(defaultUser().URI() + sourceVertexLocalName);
        if (!graph().containsResource(subjectResource)) {
            throw new NonExistingResourceException(defaultUser().URI() + sourceVertexLocalName);
        }

        String edgeURI = defaultUser().URI() + defaultUser().nextId();
        Resource newEdge = defaultUser().model().createProperty(edgeURI);
        newEdge.addLiteral(label, "");

        String objectVertexURI = defaultUser().URI() + defaultUser().nextId();
        Resource newVertex = defaultUser().model().createResource(objectVertexURI);
        newVertex.addLiteral(label, "");

        subjectResource.addProperty((Property) newEdge, (RDFNode) newVertex);
        return defaultUser().model().listStatements(new SimpleSelector(subjectResource, (Property) newEdge, (RDFNode) newVertex)).nextStatement();
    }

    public JenaVertexManipulator removeVertex(String vertexLocalName) {
        Resource vertex = defaultUser().model().getResource(defaultUser().URI() + vertexLocalName);
        if (!graph().containsResource(vertex)) {
            throw new NonExistingResourceException(defaultUser().URI() + vertexLocalName);
        }

        for (Statement statement : graph().listStatements(new SimpleSelector(null, null, vertex)).toList()) {
            graph().remove(statement);
            statement.getPredicate().removeProperties();
        }
        for (Statement statement : vertex.listProperties().toList()) {
            graph().remove(statement);
            statement.getPredicate().removeProperties();
        }
        return this;
    }

    public JenaVertexManipulator updateLabel(String vertexLocalName, String newLabel) throws NonExistingResourceException{
        jenaGraphElementManipulator.updateLabel(vertexLocalName, newLabel);
        return this;
    }

    public JenaVertexManipulator semanticType(String vertexLocalName, String typeUri){
        Resource vertex = defaultUser().model().getResource(defaultUser().URI() + vertexLocalName);
        if (!graph().containsResource(vertex)) {
            throw new NonExistingResourceException(defaultUser().URI() + vertexLocalName);
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
