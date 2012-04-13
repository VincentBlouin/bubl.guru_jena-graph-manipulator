package graph;

import graph.mock.JenaGraphManipulatorMock;
import org.junit.Before;
import org.junit.Test;
import org.triple_brain.graphmanipulator.jena.graph.JenaGraphElementManipulator;
import org.triple_brain.graphmanipulator.jena.graph.JenaVertexManipulator;
import org.triple_brain.module.graph_manipulator.exceptions.NonExistingResourceException;
import org.triple_brain.module.model.graph.Edge;
import org.triple_brain.module.model.graph.Vertex;

import static junit.framework.Assert.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.triple_brain.graphmanipulator.jena.graph.JenaGraphElementManipulator.jenaGraphElementManipulatorWithJenaGraphManipulator;
import static org.triple_brain.graphmanipulator.jena.graph.JenaVertexManipulator.withJenaGraphManipulator;

/**
 * Copyright Mozilla Public License 1.1
 */
public class JenaGraphElementManipulatorTest {

    private JenaGraphElementManipulator jenaGraphElementManipulator;
    private JenaVertexManipulator jenaVertexManipulator;
    private Vertex defaultCenterVertex;

    @Before
    public void setUp() {
        JenaGraphManipulatorMock graphManipulator = JenaGraphManipulatorMock.jenaGraphManipulatorWithDefaultUser();
        jenaVertexManipulator = withJenaGraphManipulator(graphManipulator);
        jenaGraphElementManipulator = jenaGraphElementManipulatorWithJenaGraphManipulator(graphManipulator);
        defaultCenterVertex = graphManipulator.defaultCenterVertex();
    }

    @Test
    public void can_update_label() {
        Edge edge = jenaVertexManipulator.addVertexAndRelation(defaultCenterVertex.id());
        jenaGraphElementManipulator.updateLabel(edge.id(), "likes");
        assertThat(edge.label(), is("likes"));

        Vertex vertex = edge.destinationVertex();
        jenaGraphElementManipulator.updateLabel(vertex.id(), "Ju-Ji-Tsu");
        assertThat(vertex.label(), is("Ju-Ji-Tsu"));
    }

    @Test
    public void update_label_with_non_existent_resource_throws_an_error() {
        try {
            jenaGraphElementManipulator.updateLabel("invalid_URI", "new label");
            fail();
        } catch (NonExistingResourceException e) {
            assertThat(e.getMessage(), is("Resource with URI: invalid_URI not found"));
        }
    }
}
