package org.triple_brain.graphmanipulator.jena.graph;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import org.triple_brain.module.model.graph.Edge;
import org.triple_brain.module.model.graph.Vertex;

import java.util.Set;

import static com.hp.hpl.jena.vocabulary.RDF.type;
import static com.hp.hpl.jena.vocabulary.RDFS.label;
import static org.triple_brain.graphmanipulator.jena.TripleBrainModel.*;
import static org.triple_brain.graphmanipulator.jena.graph.JenaGraphElement.jenaGraphElementWithResource;

/**
 * Copyright Mozilla Public License 1.1
 */
public class JenaEdge extends Edge {
    Resource resource;
    JenaGraphElement jenaGraphElement;

    public static JenaEdge withModelURIAndDestinationVertex(Model model, String URI, JenaVertex destinationVertex) {
        Resource resource = model.createProperty(URI);
        resource.addLiteral(label, "");
        resource.addProperty(type, TRIPLE_BRAIN_EDGE());
        return new JenaEdge(resource, destinationVertex);
    }

    public static JenaEdge withResource(Resource resource) {
        return new JenaEdge(resource);
    }

    protected JenaEdge(Resource resource) {
        jenaGraphElement = jenaGraphElementWithResource(resource);
        this.resource = resource;
    }

    protected JenaEdge(Resource resource, JenaVertex destinationVertex) {
        jenaGraphElement = jenaGraphElementWithResource(resource);
        this.resource = resource;
        Resource destinationVertexAsResource = jenaGraphElement.resourceFromGraphElement(destinationVertex);
        resource.addProperty(DESTINATION_VERTEX(), destinationVertexAsResource);
    }

    public JenaEdge buildEdgeInModel(Model model) {
        Resource resourceInModel = model.createResource(id());
        model.add(resource.listProperties());
        return withResource(resourceInModel);
    }

    @Override
    public String id() {
        return jenaGraphElement.id();
    }

    @Override
    public String label() {
        return jenaGraphElement.label();
    }

    @Override
    public void label(String label) {
        jenaGraphElement.label(label);
    }

    @Override
    public boolean hasLabel() {
        return jenaGraphElement.hasLabel();
    }

    @Override
    public Set<String> types() {
        return jenaGraphElement.types();
    }

    @Override
    public Vertex sourceVertex() {
        return JenaVertex.withResource(
                resource.getModel().
                        listStatements(
                                new SimpleSelector(
                                        null,
                                        HAS_OUTGOING_EDGE(),
                                        resource
                                )).toList().get(0)
                        .getSubject().asResource());
    }

    @Override
    public Vertex destinationVertex() {
        return JenaVertex.withResource(
                resource.getProperty(
                        DESTINATION_VERTEX()
                ).getObject().asResource()
        );
    }
}
