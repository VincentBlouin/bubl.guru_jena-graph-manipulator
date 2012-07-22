package org.triple_brain.graphmanipulator.jena.graph;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.*;
import org.triple_brain.graphmanipulator.jena.SuggestionRdfConverter;
import org.triple_brain.module.model.Suggestion;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.Edge;
import org.triple_brain.module.model.graph.Vertex;

import java.util.*;

import static com.hp.hpl.jena.vocabulary.OWL2.sameAs;
import static com.hp.hpl.jena.vocabulary.RDF.type;
import static com.hp.hpl.jena.vocabulary.RDFS.label;
import static org.triple_brain.graphmanipulator.jena.QueryUtils.*;
import static org.triple_brain.graphmanipulator.jena.TripleBrainModel.*;

/**
 * Copyright Mozilla Public License 1.1
 */
public class JenaVertex extends Vertex {
    protected Resource resource;
    private User owner;
    JenaGraphElement graphElement;

    public static JenaVertex loadUsingResourceOfOwner(Resource resource, User owner) {
        return new JenaVertex(resource, owner);
    }

    public static JenaVertex createUsingModelUriAndOwner(Model model, String URI, User owner) {
        Resource resource = model.createResource(URI);
        resource.addLiteral(label, "");
        resource.addProperty(type, TRIPLE_BRAIN_VERTEX());
        return new JenaVertex(resource, owner);
    }



    protected JenaVertex(Resource resource, User owner) {
        graphElement = JenaGraphElement.withResource(resource);
        this.resource = resource;
        this.owner = owner;
    }

    public JenaVertex buildVertexInModelWithOwner(Model model, User owner) {
        Resource resourceInModel = model.createResource(id());
        model.add(resource.listProperties());
        addSuggestionsInModel(model);
        return loadUsingResourceOfOwner(resourceInModel, owner);
    }

    @Override
    public boolean hasEdge(Edge edge) {
        return resource.hasProperty(
                HAS_OUTGOING_EDGE(),
                graphElement.resourceFromGraphElement(edge)
        );
    }

    @Override
    public void addOutgoingEdge(Edge edge) {
        Resource edgeAsResource = graphElement.resourceFromGraphElement(edge);
        resource.addProperty(HAS_OUTGOING_EDGE(), edgeAsResource);
    }

    @Override
    public void removeOutgoingEdge(Edge edge) {
        resource.getModel().listStatements(new SimpleSelector(
                resource,
                HAS_OUTGOING_EDGE(),
                graphElement.resourceFromGraphElement(edge)
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
        return JenaEdge.loadWithResourceOfOwner(
                rs.next().getResource("edge"),
                owner
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
    public Edge addVertexAndRelation() {
        String newVertexURI = owner.URIFromSiteURI(SITE_URI) + UUID.randomUUID().toString();
        JenaVertex newVertex = JenaVertex.createUsingModelUriAndOwner(model(), newVertexURI, owner);

        String edgeURI = owner.URIFromSiteURI(SITE_URI) + UUID.randomUUID().toString();
        JenaEdge edge = JenaEdge.createWithModelUriDestinationVertexAndOwner(
                model(),
                edgeURI,
                newVertex,
                owner
        );

        addOutgoingEdge(edge);

        newVertex.addNeighbor(this);
        addNeighbor(newVertex);
        return edge;
    }

    @Override
    public Edge addRelationToVertex(Vertex destinationVertex) {
        JenaEdge edge = JenaEdge.createWithModelUriDestinationVertexAndOwner(
                model(),
                owner.URIFromSiteURI(SITE_URI) + UUID.randomUUID().toString(),
                destinationVertex,
                owner
        );
        addOutgoingEdge(edge);
        addNeighbor(destinationVertex);
        destinationVertex.addNeighbor(this);
        return edge;
    }

    @Override
    public void remove() {
        for(Edge edge : connectedEdges()){
            edge.remove();
        }
        removeSuggestions();
        resource.removeProperties();
    }

    @Override
    public void removeNeighbor(Vertex neighbor) {
        resource.getModel().listStatements(new SimpleSelector(
                resource,
                HAS_NEIGHBOR(),
                graphElement.resourceFromGraphElement(neighbor)
        )).nextStatement()
                .remove();
    }

    @Override
    public Set<Edge> outGoingEdges() {
        Set<Edge> outGoingEdges = new HashSet<Edge>();
        List<Statement> statements = resource.listProperties(HAS_OUTGOING_EDGE()).toList();
        for (Statement statement : statements) {
            outGoingEdges.add(
                    JenaEdge.loadWithResourceOfOwner(
                            statement.getObject().asResource(),
                            owner
                    )
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
                    JenaEdge.loadWithResourceOfOwner(
                            rs.next().getResource("edges"),
                            owner
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
    public void suggestions(Set<Suggestion> suggestions) {
        removeSuggestions();
        SuggestionRdfConverter suggestionToRdf = SuggestionRdfConverter.withModel(resource.getModel());
        for(Suggestion suggestion : suggestions){
            resource.addProperty(
                    HAS_SUGGESTION(),
                    suggestionToRdf.suggestionToRdf(suggestion)
                    );
        }
    }

    @Override
    public Set<Suggestion> suggestions() {
        Set<Suggestion> suggestions = new HashSet<Suggestion>();
        SuggestionRdfConverter suggestionToRdf = SuggestionRdfConverter.withModel(resource.getModel());
        for(Statement statement : resource.listProperties(HAS_SUGGESTION()).toList()){
            suggestions.add(
                    suggestionToRdf.rdfToSuggestion(statement.getObject().asResource())
            );
        }
        return suggestions;
    }

    @Override
    public void addSemanticType(String typeUri) {
        Resource typeAsResource = model().createResource(typeUri);
        resource.addProperty(type, typeAsResource);
    }

    @Override
    public void setSameAsUsingUri(String sameAsUri) {
        Resource sameAsResource = model().createResource(sameAsUri);
        resource.addProperty(sameAs, sameAsResource);
    }

    private void removeSuggestions(){
        for(Statement statement : resource.listProperties(HAS_SUGGESTION()).toList()){
            statement.getObject().asResource().removeProperties();
        }
        resource.removeAll(HAS_SUGGESTION());
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

    private void addSuggestionsInModel(Model model){
        for(Statement statement : resource.listProperties(HAS_SUGGESTION()).toList()){
            Resource suggestion = statement.getObject().asResource();
            model.add(suggestion.listProperties());
        }
    }

    protected JenaGraphElement jenaGraphElement() {
        return graphElement;
    }

    private Model model(){
        return graphElement.model();
    }
}
