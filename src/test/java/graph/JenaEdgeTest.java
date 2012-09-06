package graph;

import org.junit.Test;
import org.triple_brain.module.model.graph.Edge;
import org.triple_brain.module.model.graph.Vertex;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;

/**
 * Copyright Mozilla Public License 1.1
 */
public class JenaEdgeTest extends JenaGeneralGraphManipulatorTest{

    @Test
    public void can_add_relation() {
        Vertex vertexD = vertexA.addVertexAndRelation().destinationVertex();
        Vertex vertexE = vertexD.addVertexAndRelation().destinationVertex();

        Integer numberOfEdgesAndVertices = graphManipulator.wholeGraph().numberOfEdgesAndVertices();
        Edge newEdge = vertexE.addRelationToVertex(vertexA);

        assertThat(newEdge.sourceVertex(), is(vertexE));
        assertThat(newEdge.destinationVertex(), is(vertexA));
        assertTrue(graphManipulator.containsElement(newEdge));
        assertThat(newEdge.label(), is(""));
        assertThat(graphManipulator.numberOfEdgesAndVertices(), is(numberOfEdgesAndVertices + 1));
    }

    @Test
    public void can_remove_an_edge() {
        Integer numberOfEdgesAndVertices = graphManipulator.numberOfEdgesAndVertices();
        Edge edge = vertexA.edgeThatLinksToDestinationVertex(vertexB);
        assertTrue(graphManipulator.containsElement(edge));
        edge.remove();
        assertFalse(graphManipulator.containsElement(edge));
        assertFalse(vertexA.hasDestinationVertex(vertexB));

        Integer updatedNumberOfEdgesAndVertices = graphManipulator.numberOfEdgesAndVertices();
        assertThat(updatedNumberOfEdgesAndVertices, is(numberOfEdgesAndVertices - 1));
    }

    @Test
    public void can_update_label() {
        Edge edge = vertexA.addVertexAndRelation();
        edge.label("likes");
        assertThat(edge.label(), is("likes"));
    }
}
