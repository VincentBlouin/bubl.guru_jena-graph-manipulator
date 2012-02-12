package org.triple_brain.graphmanipulator.jena.graph;

import com.hp.hpl.jena.rdf.arp.NTriple;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.triple_brain.graphmanipulator.jena.graph.exceptions.InvalidDepthOfSubVerticesException;
import org.triple_brain.graphmanipulator.jena.graph.exceptions.NonExistingResourceException;
import org.triple_brain.graphmanipulator.jena.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

import static com.hp.hpl.jena.vocabulary.RDFS.*;

import static org.triple_brain.graphmanipulator.jena.TripleBrainModel.*;

/**
 * @author Vincent Blouin
 */
public class JenaGraphManipulator {

    protected User defaultUser = User.withUserName("Roger Lamothe");

    private Model graphTraversalProperties = ModelFactory.createDefaultModel();
    private Property VISITED_AT_DEPTH = graphTraversalProperties.createProperty(SITE_URI + "visited_at_depth");

    public static JenaGraphManipulator jenaGraphManipulatorWithDefaultUser() {
        return new JenaGraphManipulator();
    }

    protected JenaGraphManipulator() {
    }

    public Model graphWithDefaultVertexAndDepth(Integer depthOfSubVertices) throws InvalidDepthOfSubVerticesException {
        Resource defaultCenterVertex = defaultUser.absoluteCentralVertex();
        return graphWithDepthAndCenterVertexId(depthOfSubVertices, defaultCenterVertex.getLocalName());
    }

    public Model graphWithDepthAndCenterVertexId(Integer depthOfSubVertices, String centerVertexLocalName) throws NonExistingResourceException {
        Resource centralVertex = graph().getResource(defaultUser.URI() + centerVertexLocalName);
        if (!graph().containsResource(centralVertex)) {
            throw new NonExistingResourceException(defaultUser.URI() + centerVertexLocalName);
        }
        if (depthOfSubVertices < 0) {
            throw new InvalidDepthOfSubVerticesException(depthOfSubVertices, centralVertex.getLocalName());
        }
        Integer startingDeptLevel = 0;
        Model subGraph = subGraphBuiltRecursively(centralVertex, startingDeptLevel, depthOfSubVertices);
        subGraph.removeAll(null, VISITED_AT_DEPTH, (RDFNode) null);
        graph().removeAll(null, VISITED_AT_DEPTH, (RDFNode) null);
        flagFrontierVerticesWithHiddenVertices(subGraph, depthOfSubVertices);
        graph().removeAll(null, MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX(), (RDFNode) null);
        return subGraph;
    }

    public String toRDFXML() {
        StringWriter rdfXML = new StringWriter();
        graph().write(rdfXML);
        return rdfXML.toString();
    }

    /*
    * @return each call returns a sub graph with the statements matching (@param currentResource, predicate, object) or (subject, predicate, @param currentResource)
    */
    private Model subGraphBuiltRecursively(Resource currentResource, int currentDepth, int maximumDepth) {

        Model partOfSubGraph = ModelFactory.createDefaultModel();

        partOfSubGraph.add(currentResource.getProperty(label));

        updateVisitAtDepth(currentResource, currentDepth);
        updateMinDepthFromCenterIfNecessary(currentResource, currentDepth);

        if (currentDepth == maximumDepth) {
            return partOfSubGraph;
        }

        List<Statement> statementsInvolvingCurrentVertex = new ArrayList<Statement>();
        List<Statement> statementsWhereCurrentVertexIsSubject = currentResource.listProperties().toList();
        List<Statement> statementsWhereCurrentVertexIsObject = graph().listStatements(null, null, currentResource).toList();
        statementsInvolvingCurrentVertex.addAll(statementsWhereCurrentVertexIsSubject);
        statementsInvolvingCurrentVertex.addAll(statementsWhereCurrentVertexIsObject);

        for (Statement statement : statementsInvolvingCurrentVertex) {

            partOfSubGraph.add(statement);

            if (doesPropertyLeadsToAVertex(statement.getPredicate())) {
                Resource subject = statement.getSubject().asResource();
                partOfSubGraph.add(subject.getProperty(label));

                updateMinDepthFromCenterIfNecessary(subject, currentDepth + 1);

                if (isCurrentDepthSmallerThanVisitedDepth(subject, currentDepth + 1)) {
                    partOfSubGraph = partOfSubGraph.union(subGraphBuiltRecursively(subject, currentDepth + 1, maximumDepth));
                }

                Property predicate = statement.getPredicate();
                partOfSubGraph.add(predicate.getProperty(label));

                Resource object = statement.getObject().asResource();
                partOfSubGraph.add(object.getProperty(label));

                updateMinDepthFromCenterIfNecessary(object, currentDepth + 1);

                if (isCurrentDepthSmallerThanVisitedDepth(object, currentDepth + 1)) {
                    partOfSubGraph = partOfSubGraph.union(subGraphBuiltRecursively(object, currentDepth + 1, maximumDepth));
                }
            }
        }
        return partOfSubGraph;
    }

    private boolean doesPropertyLeadsToAVertex(Property property) {
        return property.hasProperty(label);
    }

    private void updateVisitAtDepth(Resource vertex, int depth) {
        vertex.removeAll(VISITED_AT_DEPTH);
        vertex.addLiteral(VISITED_AT_DEPTH, depth);
    }

    private boolean isResourceVisited(Resource vertex) {
        return vertex.hasProperty(VISITED_AT_DEPTH);
    }

    private boolean isCurrentDepthSmallerThanVisitedDepth(Resource vertex, int currentDepth) {
        if (!isResourceVisited(vertex)) {
            return true;
        } else {
            if (depthFromCenterVertex(vertex) > currentDepth) {
                return true;
            }
        }
        return false;

    }

    private int depthFromCenterVertex(Resource vertex) {
        return vertex.getProperty(VISITED_AT_DEPTH).getInt();
    }

    private void updateMinDepthFromCenterIfNecessary(Resource vertex, Integer currentDepth) {
        if (!vertex.hasProperty(MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX())) {
            vertex.addLiteral(MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX(), currentDepth.intValue());
        } else if (vertex.getProperty(MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX()).getInt() > currentDepth) {
            vertex.removeAll(MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX());
            vertex.addLiteral(MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX(), currentDepth.intValue());
        }
    }

    private void flagFrontierVerticesWithHiddenVertices(Model subGraph, Integer maximumDepth) {
        List<Resource> vertices = graph().listSubjectsWithProperty(MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX()).toList();

        for (Resource vertexInWholeGraph : vertices) {
            Resource vertexInSubGraph = subGraph.getResource(vertexInWholeGraph.getURI());
            int distanceFromCenterVertex = vertexInWholeGraph.getProperty(MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX()).getInt();
            vertexInSubGraph.addLiteral(MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX(), distanceFromCenterVertex);

            if (distanceFromCenterVertex == maximumDepth) {

                Model graphMadeOfStatementsInvolvingVertexAsSubjectInWholeGraph = ModelFactory.createDefaultModel();
                graphMadeOfStatementsInvolvingVertexAsSubjectInWholeGraph.add(vertexInWholeGraph.listProperties());

                Model graphContainingStatementsOfVertexAsSubjectInWholeGraphAbsentInSubGraph = graphMadeOfStatementsInvolvingVertexAsSubjectInWholeGraph.difference(subGraph);

                Integer numberOfHiddenNeighborVertices = 0;
                numberOfHiddenNeighborVertices += graphContainingStatementsOfVertexAsSubjectInWholeGraphAbsentInSubGraph.listStatements().toList().size();

                Model graphMadeOfStatementsInvolvingVertexAsObjectInWholeGraph = ModelFactory.createDefaultModel();
                graphMadeOfStatementsInvolvingVertexAsObjectInWholeGraph.add(graph().listStatements(new SimpleSelector(null, null, vertexInWholeGraph)));
                Model graphContainingStatementsOfVertexAsObjectInWholeGraphAbsentInSubGraph = graphMadeOfStatementsInvolvingVertexAsObjectInWholeGraph.difference(subGraph);

                numberOfHiddenNeighborVertices += graphContainingStatementsOfVertexAsObjectInWholeGraphAbsentInSubGraph.listStatements().toList().size();
                Model graphWithHiddenStatements = graphContainingStatementsOfVertexAsSubjectInWholeGraphAbsentInSubGraph.union(graphContainingStatementsOfVertexAsObjectInWholeGraphAbsentInSubGraph);

                Seq hiddenProperties = subGraph.createSeq();

                if (numberOfHiddenNeighborVertices > 0){
                    for (Statement statementsWhereFrontierResourceIsSubject : graphWithHiddenStatements.listStatements().toList()) {
                        Property property = statementsWhereFrontierResourceIsSubject.getPredicate();
                        Property propertyInWholeGraph = graph().getProperty(property.getURI());
                        if(propertyInWholeGraph.hasProperty(label)){
                            String propertyName = propertyInWholeGraph.getProperty(label).getString();
                            hiddenProperties.add(propertyName);
                        }
                    }
                    vertexInSubGraph.addProperty(NAME_OF_HIDDEN_PROPERTIES(), hiddenProperties);
                    if (!vertexInSubGraph.hasProperty(IS_FRONTIER_VERTEX_WITH_HIDDEN_VERTICES())) {
                        vertexInSubGraph.addProperty(IS_FRONTIER_VERTEX_WITH_HIDDEN_VERTICES(), "true");
                    }
                    vertexInSubGraph.addLiteral(NUMBER_OF_HIDDEN_CONNECTED_VERTICES(), numberOfHiddenNeighborVertices);
                }

            }
        }
    }

    public User defaultUser() {
        return defaultUser;
    }

    public Model graph() {
        return defaultUser.model();
    }

}
