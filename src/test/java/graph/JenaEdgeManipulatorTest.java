package graph;

import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.junit.Test;
import org.triple_brain.graphmanipulator.jena.graph.JenaEdge;
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
public class JenaEdgeManipulatorTest extends JenaGeneralGraphManipulatorTest{

    @Test
    public void can_add_relation() {
        Vertex secondVertex = vertexManipulator.addVertexAndRelation(vertexA.id()).destinationVertex();
        Vertex thirdVertex = vertexManipulator.addVertexAndRelation(secondVertex.id()).destinationVertex();

        Integer numberOfEdgesAndVertices = edgeManipulator.graph().listSubjects().toList().size();
        Edge newEdge = edgeManipulator.addRelationBetweenVertices(
                thirdVertex.id(),
                vertexA.id()
        );

        assertThat(newEdge.sourceVertex(), is(thirdVertex));
        assertThat(newEdge.destinationVertex(), is(vertexA));
        assertTrue(graphManipulator.containsElement(newEdge));
        assertThat(newEdge.label(), is(""));
        assertThat(graphManipulator.numberOfEdgesAndVertices(), is(numberOfEdgesAndVertices + 1));
    }

    @Test
    public void add_relation_with_non_existent_source_or_destination_throws_an_error() {
        Integer numberOfEdgesAndVertices = edgeManipulator.graph().listSubjects().toList().size();
        try {
            edgeManipulator.addRelationBetweenVertices(vertexA.id(), "invalid_URI");
            fail();
        } catch (NonExistingResourceException e) {
            assertThat(e.getMessage(), is("Resource with URI: invalid_URI not found"));
        }
        try {
            edgeManipulator.addRelationBetweenVertices("invalid_URI", vertexA.id());
            fail();
        } catch (NonExistingResourceException e) {
            assertThat(e.getMessage(), is("Resource with URI: invalid_URI not found"));
        }
        assertThat(edgeManipulator.graph().listSubjects().toList().size(), is(numberOfEdgesAndVertices));
    }

    @Test
    public void can_remove_an_edge() {
        Integer numberOfEdgesAndVertices = graphManipulator.numberOfEdgesAndVertices();
        Edge edge = vertexA.edgeThatLinksToDestinationVertex(vertexB);
        assertTrue(graphManipulator.containsElement(edge));
        edgeManipulator.removeEdge(edge.id());
        assertFalse(graphManipulator.containsElement(edge));
        assertFalse(vertexA.hasDestinationVertex(vertexB));

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
        Edge edge = vertexManipulator.addVertexAndRelation(vertexA.id());
        edgeManipulator.updateLabel(edge.id(), "likes");
        assertThat(edge.label(), is("likes"));
    }
}
