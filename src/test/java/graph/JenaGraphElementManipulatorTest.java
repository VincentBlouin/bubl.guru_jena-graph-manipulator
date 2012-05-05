package graph;

import org.junit.Test;
import org.triple_brain.module.graph_manipulator.exceptions.NonExistingResourceException;
import org.triple_brain.module.model.graph.Edge;
import org.triple_brain.module.model.graph.Vertex;

import static junit.framework.Assert.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
/**
 * Copyright Mozilla Public License 1.1
 */
public class JenaGraphElementManipulatorTest extends JenaGeneralGraphManipulatorTest {

    @Test
    public void can_update_label() {
        Edge edge = vertexManipulator.addVertexAndRelation(vertexA.id());
        graphElementManipulator.updateLabel(edge.id(), "likes");
        assertThat(edge.label(), is("likes"));

        Vertex vertex = edge.destinationVertex();
        graphElementManipulator.updateLabel(vertex.id(), "Ju-Ji-Tsu");
        assertThat(vertex.label(), is("Ju-Ji-Tsu"));
    }

    @Test
    public void update_label_with_non_existent_resource_throws_an_error() {
        try {
            graphElementManipulator.updateLabel("invalid_URI", "new label");
            fail();
        } catch (NonExistingResourceException e) {
            assertThat(e.getMessage(), is("Resource with URI: invalid_URI not found"));
        }
    }
}
