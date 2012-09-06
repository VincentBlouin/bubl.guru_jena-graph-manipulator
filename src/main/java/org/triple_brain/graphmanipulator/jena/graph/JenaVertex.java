package org.triple_brain.graphmanipulator.jena.graph;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.vocabulary.OWL2;
import com.hp.hpl.jena.vocabulary.RDF;
import org.triple_brain.graphmanipulator.jena.FriendlyResourceRdfConverter;
import org.triple_brain.graphmanipulator.jena.SuggestionRdfConverter;
import org.triple_brain.graphmanipulator.jena.TripleBrainModel;
import org.triple_brain.module.model.FriendlyResource;
import org.triple_brain.module.model.Suggestion;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.Edge;
import org.triple_brain.module.model.graph.Vertex;

import java.net.URI;
import java.util.*;

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
        resource.addProperty(
                type,
                TripleBrainModel.withEnglobingModel(model).TRIPLE_BRAIN_VERTEX()
        );
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
        copySuggestionsInModel(model);
        copyAdditionalTypesInModel(model);
        copyAllSameAsInModel(model);
        return loadUsingResourceOfOwner(resourceInModel, owner);
    }

    @Override
    public boolean hasEdge(Edge edge) {
        return resource.hasProperty(
                graphElement.tripleBrainModel().HAS_OUTGOING_EDGE(),
                graphElement.resourceFromGraphElement(edge)
        );
    }

    @Override
    public void addOutgoingEdge(Edge edge) {
        Resource edgeAsResource = graphElement.resourceFromGraphElement(edge);
        resource.addProperty(
                graphElement.tripleBrainModel().HAS_OUTGOING_EDGE(),
                edgeAsResource
        );
    }

    @Override
    public void removeOutgoingEdge(Edge edge) {
        model().listStatements(new SimpleSelector(
                resource,
                graphElement.tripleBrainModel().HAS_OUTGOING_EDGE(),
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
        QueryExecution qe = QueryExecutionFactory.create(query, model());
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
                query, model()
        );
        ResultSet rs = qe.execSelect();
        return rs.hasNext();
    }

    @Override
    public void addNeighbor(Vertex neighbor) {
        Resource neighborAsResource = graphElement().resourceFromGraphElement(neighbor);
        resource.addProperty(
                graphElement.tripleBrainModel().HAS_NEIGHBOR(),
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
        TDB.sync(resource.getModel());
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
        removeAdditionalTypes();
        removeAllSameAs();
        resource.removeProperties();
    }

    @Override
    public void removeNeighbor(Vertex neighbor) {
        model().listStatements(new SimpleSelector(
                resource,
                graphElement.tripleBrainModel().HAS_NEIGHBOR(),
                graphElement.resourceFromGraphElement(neighbor)
        )).nextStatement()
                .remove();
    }

    @Override
    public Set<Edge> outGoingEdges() {
        Set<Edge> outGoingEdges = new HashSet<Edge>();
        List<Statement> statements = resource.listProperties(
                graphElement.tripleBrainModel().HAS_OUTGOING_EDGE()
        ).toList();
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
                query, model()
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
        if(!resource.hasProperty(
                graphElement.tripleBrainModel().LABEL_OF_HIDDEN_EDGES()
        )){
            return hiddenEdgesLabel;
        }
        Seq labelSequence = resource.getProperty(
                graphElement.tripleBrainModel().LABEL_OF_HIDDEN_EDGES()
        ).getSeq();
        for (int i = 1; i <= labelSequence.size(); i++) {
            hiddenEdgesLabel.add(
                    labelSequence.getString(i)
            );
        }
        return hiddenEdgesLabel;
    }

    @Override
    public boolean hasMinNumberOfEdgesFromCenterVertex() {
        return resource.hasProperty(
                graphElement.tripleBrainModel().LABEL_OF_HIDDEN_EDGES()
        );
    }

    @Override
    public void hiddenConnectedEdgesLabel(List<String> hiddenEdgeLabel) {
        resource.removeAll(
                graphElement.tripleBrainModel().LABEL_OF_HIDDEN_EDGES()
        );
        Seq labelSequence = model().createSeq();
        for (String label : hiddenEdgeLabel) {
            labelSequence.add(label);
        }
        resource.addProperty(
                graphElement.tripleBrainModel().LABEL_OF_HIDDEN_EDGES(),
                labelSequence
        );
    }

    @Override
    public void suggestions(Set<Suggestion> suggestions) {
        removeSuggestions();
        SuggestionRdfConverter suggestionToRdf = SuggestionRdfConverter.withModel(model());
        for(Suggestion suggestion : suggestions){
            resource.addProperty(
                    graphElement.tripleBrainModel().HAS_SUGGESTION(),
                    suggestionToRdf.suggestionToRdf(suggestion)
                    );
        }
    }

    @Override
    public Set<Suggestion> suggestions() {
        Set<Suggestion> suggestions = new HashSet<Suggestion>();
        SuggestionRdfConverter suggestionToRdf = SuggestionRdfConverter.withModel(model());
        for(Statement statement : resource.listProperties(
                graphElement.tripleBrainModel().HAS_SUGGESTION()
        ).toList()){
            suggestions.add(
                    suggestionToRdf.rdfToSuggestion(statement.getObject().asResource())
            );
        }
        return suggestions;
    }

    @Override
    public FriendlyResource friendlyResourceWithUri(URI uri) {
        return FriendlyResourceRdfConverter.fromRdf(
                model().getResource(
                        uri.toString()
                )
        );
    }

    @Override
    public void addType(FriendlyResource type) {
        resource.addProperty(
                RDF.type,
                FriendlyResourceRdfConverter.createInModel(
                        model(),
                        type
                )
        );
    }

    @Override
    public void removeFriendlyResource(FriendlyResource friendlyResource) {
        Resource friendlyResourceAsJenaResource = model().getResource(
                friendlyResource.uri().toString()
        );
        friendlyResourceAsJenaResource.removeProperties();
        model().removeAll(
                resource,
                null,
                friendlyResourceAsJenaResource
        );
    }

    @Override
    public Set<FriendlyResource> getAdditionalTypes() {
        Set<FriendlyResource> additionalTypes = new HashSet<FriendlyResource>();
        String query = TRIPLE_BRAIN_PREFIX + RDF_PREFIX +
                "SELECT ?additional_type " +
                "WHERE { " +
                URIForQuery(id()) + " rdf:type ?additional_type . " +
                "FILTER (" +
                "?additional_type != " +
                URIForQuery(TripleBrainModel.TRIPLE_BRAIN_VERTEX_URI) +
                ")" +
                "}";
        QueryExecution qe = QueryExecutionFactory.create(query, model());
        ResultSet rs = qe.execSelect();
        while (rs.hasNext()) {
            QuerySolution querySolution = rs.next();
            additionalTypes.add(FriendlyResourceRdfConverter.fromRdf(
                    querySolution.getResource("?additional_type")
            ));
        }
        return additionalTypes;
    }

    @Override
    public void addSameAs(FriendlyResource friendlyResource) {
        resource.addProperty(
                OWL2.sameAs,
                FriendlyResourceRdfConverter.createInModel(
                        model(),
                        friendlyResource
                )
        );
    }

    @Override
    public Set<FriendlyResource> getSameAs() {
        Set<FriendlyResource> sameAs = new HashSet<FriendlyResource>();
        for(Statement statement : resource.listProperties(OWL2.sameAs).toList()){
            sameAs.add(
                    FriendlyResourceRdfConverter.fromRdf(
                            statement.getObject().asResource()
                    )
            );
        }
        return sameAs;
    }

    private void removeSuggestions(){
        for(Statement statement : resource.listProperties(
                graphElement.tripleBrainModel().HAS_SUGGESTION()
        ).toList()){
            statement.getObject().asResource().removeProperties();
        }
        resource.removeAll(
                graphElement.tripleBrainModel().HAS_SUGGESTION()
        );
    }
    private void removeAdditionalTypes(){
        removeFriendlyResources(
                getAdditionalTypes()
        );
    }

    private void removeFriendlyResources(Set<FriendlyResource> friendlyResources){
        for(FriendlyResource type : friendlyResources){
            removeFriendlyResource(type);
        }
    }
    private void removeAllSameAs(){
        removeFriendlyResources(
                getSameAs()
        );
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

    private void copySuggestionsInModel(Model model){
        for(Statement statement : resource.listProperties(
                graphElement.tripleBrainModel().HAS_SUGGESTION()
        ).toList()){
            Resource suggestion = statement.getObject().asResource();
            model.add(suggestion.listProperties());
        }
    }
    private void copyAdditionalTypesInModel(Model model){
        copyFriendlyResourcesInModel(
                getAdditionalTypes(),
                model
        );
    }

    private void copyAllSameAsInModel(Model model){
        copyFriendlyResourcesInModel(
                getSameAs(),
                model
        );
    }

    private void copyFriendlyResourcesInModel(Set<FriendlyResource> friendlyResources, Model model){
        for(FriendlyResource friendlyResource: friendlyResources){
            Resource friendlyResourceAsJenaResource = model().getResource(
                    friendlyResource.uri().toString()
            );
            model.add(
                    friendlyResourceAsJenaResource.listProperties()
            );
        }
    }

    protected JenaGraphElement graphElement() {
        return graphElement;
    }

    private Model model(){
        return graphElement.model();
    }
}
