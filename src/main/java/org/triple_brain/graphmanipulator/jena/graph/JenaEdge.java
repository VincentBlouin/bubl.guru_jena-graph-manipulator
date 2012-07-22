package org.triple_brain.graphmanipulator.jena.graph;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.Edge;
import org.triple_brain.module.model.graph.Vertex;

import java.util.Set;

import static com.hp.hpl.jena.vocabulary.RDF.type;
import static com.hp.hpl.jena.vocabulary.RDFS.label;
import static org.triple_brain.graphmanipulator.jena.TripleBrainModel.*;

/**
 * Copyright Mozilla Public License 1.1
 */
public class JenaEdge extends Edge {
    private Resource resource;
    private JenaGraphElement graphElement;
    private User owner;
    public static JenaEdge createWithModelUriDestinationVertexAndOwner(Model model, String URI, Vertex destinationVertex, User owner) {
        Resource resource = model.createProperty(URI);
        resource.addLiteral(label, "");
        resource.addProperty(type, TRIPLE_BRAIN_EDGE());
        return new JenaEdge(resource, destinationVertex, owner);
    }

    public static JenaEdge loadWithResourceOfOwner(Resource resource, User owner) {
        return new JenaEdge(resource, owner);
    }

    protected JenaEdge(Resource resource, User owner) {
        graphElement = JenaGraphElement.withResource(resource);
        this.resource = resource;
        this.owner = owner;
    }

    protected JenaEdge(Resource resource, Vertex destinationVertex, User owner) {
        graphElement = JenaGraphElement.withResource(resource);
        this.resource = resource;
        Resource destinationVertexAsResource = graphElement.resourceFromGraphElement(destinationVertex);
        resource.addProperty(DESTINATION_VERTEX(), destinationVertexAsResource);
        this.owner = owner;
    }

    public JenaEdge buildEdgeInModelOfUser(Model model, User owner) {
        Resource resourceInModel = model.createResource(id());
        model.add(resource.listProperties());
        return loadWithResourceOfOwner(resourceInModel, owner);
    }

    @Override
    public String id() {
        return graphElement.id();
    }

    @Override
    public String label() {
        return graphElement.label();
    }

    @Override
    public void label(String label) {
        graphElement.label(label);
    }

    @Override
    public boolean hasLabel() {
        return graphElement.hasLabel();
    }

    @Override
    public Set<String> types() {
        return graphElement.types();
    }

    @Override
    public Vertex sourceVertex() {
        return JenaVertex.loadUsingResourceOfOwner(
                resource.getModel().
                        listStatements(
                                new SimpleSelector(
                                        null,
                                        HAS_OUTGOING_EDGE(),
                                        resource
                                )).toList().get(0)
                        .getSubject().asResource(),
                owner
        );
    }

    @Override
    public Vertex destinationVertex() {
        return JenaVertex.loadUsingResourceOfOwner(
                resource.getProperty(
                        DESTINATION_VERTEX()
                ).getObject().asResource(),
                owner
        );
    }

    @Override
    public void remove() {
        Vertex sourceVertex = sourceVertex();
        Vertex destinationVertex = destinationVertex();
        sourceVertex.removeOutgoingEdge(this);
        if(!areVerticesConnectedInAnyWay(
                sourceVertex, destinationVertex
        )){
            sourceVertex.removeNeighbor(destinationVertex);
            destinationVertex.removeNeighbor(sourceVertex);
        }
        resource.removeProperties();
    }

    private boolean areVerticesConnectedInAnyWay(Vertex vertexA, Vertex vertexB){
        return  vertexA.hasDestinationVertex(vertexB) ||
                vertexB.hasDestinationVertex(vertexA);
    }

    private Model model(){
        return graphElement.model();
    }
}
