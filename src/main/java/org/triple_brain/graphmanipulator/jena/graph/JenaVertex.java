package org.triple_brain.graphmanipulator.jena.graph;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.*;
import org.triple_brain.module.model.graph.Edge;
import org.triple_brain.module.model.graph.Vertex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.hp.hpl.jena.vocabulary.RDF.type;
import static com.hp.hpl.jena.vocabulary.RDFS.label;
import static org.triple_brain.graphmanipulator.jena.QueryUtils.*;
import static org.triple_brain.graphmanipulator.jena.TripleBrainModel.*;
import static org.triple_brain.graphmanipulator.jena.graph.JenaGraphElement.jenaGraphElementWithResource;

/**
 * Copyright Mozilla Public License 1.1
 */
public class JenaVertex extends Vertex {
    protected Resource resource;
    JenaGraphElement jenaGraphElement;

    public static JenaVertex withResource(Resource resource) {
        return new JenaVertex(resource);
    }

    public static JenaVertex withModelAndURI(Model model, String URI) {
        Resource resource = model.createResource(URI);
        resource.addLiteral(label, "");
        resource.addProperty(type, TRIPLE_BRAIN_VERTEX());
        return new JenaVertex(resource);
    }

    protected JenaVertex(Resource resource) {
        jenaGraphElement = jenaGraphElementWithResource(resource);
        this.resource = resource;
    }

    @Override
    public boolean hasEdge(Edge edge) {
        return resource.hasProperty(
                HAS_OUTGOING_EDGE(),
                jenaGraphElement.resourceFromGraphElement(edge)
        );
    }

    @Override
    public void addOutgoingEdge(Edge edge) {
        Resource edgeAsResource = jenaGraphElement.resourceFromGraphElement(edge);
        resource.addProperty(HAS_OUTGOING_EDGE(), edgeAsResource);
    }

    @Override
    public void removeOutgoingEdge(Edge edge) {
        resource.getModel().listStatements(new SimpleSelector(
                resource,
                HAS_OUTGOING_EDGE(),
                jenaGraphElement.resourceFromGraphElement(edge)
        )).nextStatement()
                .remove();

    }

    @Override
    public Edge edgeThatLinksToDestinationVertex(Vertex destinationVertex) {
        String query = TRIPLE_BRAIN_PREFIX + RDF_PREFIX +
                "SELECT ?edge " +
                "WHERE { " +
                URIForQuery(id()) + " tb:has_outgoing_edge ?edge . " +
                "?edge tb:destination_vertex " + URIForQuery(destinationVertex.id()) + " . " +
                "}";
        QueryExecution qe = QueryExecutionFactory.create(query, resource.getModel());
        ResultSet rs = qe.execSelect();
        return JenaEdge.withResource(
                rs.next().getResource("edge")
        );
    }

    @Override
    public boolean hasDestinationVertex(Vertex destinationVertex) {
        String query =  TRIPLE_BRAIN_PREFIX +
                "SELECT ?edge " +
                "WHERE { " +
                    URIForQuery(id()) + " tb:has_outgoing_edge ?edge . " +
                    "{ ?edge tb:destination_vertex " + URIForQuery(destinationVertex.id()) + "} " +
                "}";
        QueryExecution qe = QueryExecutionFactory.create(
                query, resource.getModel()
        );
        ResultSet rs = qe.execSelect();
        return rs.hasNext();
    }

    @Override
    public void addNeighbor(Vertex neighbor) {
        Resource neighborAsResource = jenaGraphElement().resourceFromGraphElement(neighbor);
        resource.addProperty(
                HAS_NEIGHBOR(),
                neighborAsResource
        );
    }

    @Override
    public void removeNeighbor(Vertex neighbor) {
        resource.getModel().listStatements(new SimpleSelector(
                resource,
                HAS_NEIGHBOR(),
                jenaGraphElement.resourceFromGraphElement(neighbor)
        )).nextStatement()
                .remove();
    }

    @Override
    public Set<Edge> outGoingEdges() {
        Set<Edge> outGoingEdges = new HashSet<Edge>();
        List<Statement> statements = resource.listProperties(HAS_OUTGOING_EDGE()).toList();
        for (Statement statement : statements) {
            outGoingEdges.add(
                    JenaEdge.withResource(statement.getObject().asResource())
            );
        }
        return outGoingEdges;
    }

    @Override
    public Set<Edge> connectedEdges() {
        Set<Edge> connectedEdges = new HashSet<Edge>();
        String query = RDFS_PREFIX + TRIPLE_BRAIN_PREFIX +
                "SELECT DISTINCT ?edges " +
                "WHERE { " +
                "{" + URIForQuery(id()) + " tb:has_outgoing_edge ?edges. } " +
                "UNION " +
                "{ ?edges tb:destination_vertex " + URIForQuery(id()) + "} " +
                "}";
        QueryExecution qe = QueryExecutionFactory.create(
                query, resource.getModel()
        );
        ResultSet rs = qe.execSelect();
        while (rs.hasNext()) {
            connectedEdges.add(
                    JenaEdge.withResource(
                            rs.next().getResource("edges")
                    )
            );
        }
        return connectedEdges;
    }

    @Override
    public List<String> hiddenConnectedEdgesLabel() {
        List<String> hiddenEdgesLabel = new ArrayList<String>();
        if(!resource.hasProperty(LABEL_OF_HIDDEN_EDGES())){
            return hiddenEdgesLabel;
        }
        Seq labelSequence = resource.getProperty(LABEL_OF_HIDDEN_EDGES()).getSeq();
        for (int i = 1; i <= labelSequence.size(); i++) {
            hiddenEdgesLabel.add(
                    labelSequence.getString(i)
            );
        }
        return hiddenEdgesLabel;
    }

    @Override
    public boolean hasMinNumberOfEdgesFromCenterVertex() {
        return resource.hasProperty(LABEL_OF_HIDDEN_EDGES());
    }

    @Override
    public void hiddenConnectedEdgesLabel(List<String> hiddenEdgeLabel) {
        resource.removeAll(LABEL_OF_HIDDEN_EDGES());
        Seq labelSequence = resource.getModel().createSeq();
        for (String label : hiddenEdgeLabel) {
            labelSequence.add(label);
        }
        resource.addProperty(LABEL_OF_HIDDEN_EDGES(), labelSequence);
    }

    @Override
    public void minNumberOfEdgesFromCenterVertex(int number) {
        resource.removeAll(MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX());
        resource.getModel().addLiteral(resource, MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX(), number);
    }

    @Override
    public int minNumberOfEdgesFromCenterVertex() {
        return resource.getProperty(MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX()).getInt();
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

    public JenaVertex buildVertexInModel(Model model) {
        Resource resourceInModel = model.createResource(id());
        model.add(resource.listProperties());
        return withResource(resourceInModel);
    }

    protected JenaGraphElement jenaGraphElement() {
        return jenaGraphElement;
    }
}
