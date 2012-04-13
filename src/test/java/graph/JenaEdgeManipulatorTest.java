package graph;

import com.hp.hpl.jena.rdf.model.ModelFactory;
import graph.mock.JenaGraphManipulatorMock;
import org.junit.Before;
import org.junit.Test;
import org.triple_brain.graphmanipulator.jena.graph.JenaEdge;
import org.triple_brain.graphmanipulator.jena.graph.JenaEdgeManipulator;
import org.triple_brain.graphmanipulator.jena.graph.JenaVertexManipulator;
import org.triple_brain.module.graph_manipulator.exceptions.NonExistingResourceException;
import org.triple_brain.module.model.graph.Edge;
import org.triple_brain.module.model.graph.Vertex;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;

/**
 * Copyright Mozilla Public License 1.1
 */
public class JenaEdgeManipulatorTest {

    private JenaEdgeManipulator edgeManipulator;
    JenaGraphManipulatorMock graphManipulator;
    private JenaVertexManipulator jenaVertexManipulator;
    private Vertex defaultCenterVertex;
    private Vertex middleVertex;
    private Vertex endVertex;

    @Before
    public void setUp() {
        graphManipulator = JenaGraphManipulatorMock.jenaGraphManipulatorWithDefaultUser();
        jenaVertexManipulator = JenaVertexManipulator.withJenaGraphManipulator(graphManipulator);
        edgeManipulator = JenaEdgeManipulator.withJenaGraphManipulator(graphManipulator);
        //creating graph defaultCenterVertexId -> secondVertexId -> thirdVertexId
        defaultCenterVertex = graphManipulator.defaultCenterVertex();

        middleVertex = jenaVertexManipulator.addVertexAndRelation(defaultCenterVertex.id()).destinationVertex();
        endVertex = jenaVertexManipulator.addVertexAndRelation(middleVertex.id()).destinationVertex();
    }

    @Test
    public void can_add_relation() {
        Vertex secondVertex = jenaVertexManipulator.addVertexAndRelation(defaultCenterVertex.id()).destinationVertex();
        Vertex thirdVertex = jenaVertexManipulator.addVertexAndRelation(secondVertex.id()).destinationVertex();

        Integer numberOfEdgesAndVertices = edgeManipulator.graph().listSubjects().toList().size();
        Edge newEdge = edgeManipulator.addRelationBetweenVertices(
                thirdVertex.id(),
                defaultCenterVertex.id()
        );

        assertThat(newEdge.sourceVertex(), is(thirdVertex));
        assertThat(newEdge.destinationVertex(), is(defaultCenterVertex));
        assertTrue(graphManipulator.containsElement(newEdge));
        assertThat(newEdge.label(), is(""));
        assertThat(graphManipulator.numberOfEdgesAndVertices(), is(numberOfEdgesAndVertices + 1));
    }

    @Test
    public void add_relation_with_non_existent_source_or_destination_throws_an_error() {
        Integer numberOfEdgesAndVertices = edgeManipulator.graph().listSubjects().toList().size();
        try {
            edgeManipulator.addRelationBetweenVertices(defaultCenterVertex.id(), "invalid_URI");
            fail();
        } catch (NonExistingResourceException e) {
            assertThat(e.getMessage(), is("Resource with URI: invalid_URI not found"));
        }
        try {
            edgeManipulator.addRelationBetweenVertices("invalid_URI", defaultCenterVertex.id());
            fail();
        } catch (NonExistingResourceException e) {
            assertThat(e.getMessage(), is("Resource with URI: invalid_URI not found"));
        }
        assertThat(edgeManipulator.graph().listSubjects().toList().size(), is(numberOfEdgesAndVertices));
    }

    @Test
    public void can_remove_an_edge() {
        Integer numberOfEdgesAndVertices = graphManipulator.numberOfEdgesAndVertices();
        Edge edge = defaultCenterVertex.edgeThatLinksToDestinationVertex(middleVertex);
        assertTrue(graphManipulator.containsElement(edge));
        edgeManipulator.removeEdge(edge.id());
        assertFalse(graphManipulator.containsElement(edge));
        assertFalse(defaultCenterVertex.hasDestinationVertex(middleVertex));

        Integer updatedNumberOfEdgesAndVertices = graphManipulator.numberOfEdgesAndVertices();
        assertThat(updatedNumberOfEdgesAndVertices, is(numberOfEdgesAndVertices - 1));
    }

    @Test
    public void remove_relation_with_non_existent_edge_throws_an_error() {
        Integer numberOfEdgesAndVertices = edgeManipulator.graph().listSubjects().toList().size();
        try {
            edgeManipulator.removeEdge(JenaEdge.withResource(
                    ModelFactory.createDefaultModel().createResource("http://example.org/other")
            ).id());
            fail();
        } catch (NonExistingResourceException e) {
            assertThat(e.getMessage(), is("Resource with URI: http://example.org/other not found"));
        }
        assertThat(edgeManipulator.graph().listSubjects().toList().size(), is(numberOfEdgesAndVertices));
    }

    @Test
    public void can_update_label() {
        Edge edge = jenaVertexManipulator.addVertexAndRelation(defaultCenterVertex.id());
        edgeManipulator.updateLabel(edge.id(), "likes");
        assertThat(edge.label(), is("likes"));
    }
}
