package graph;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import org.junit.Before;
import org.junit.Test;
import org.triple_brain.graphmanipulator.jena.graph.JenaGraphElementManipulator;
import org.triple_brain.graphmanipulator.jena.graph.JenaGraphManipulator;
import org.triple_brain.graphmanipulator.jena.graph.JenaVertexManipulator;
import org.triple_brain.graphmanipulator.jena.graph.exceptions.NonExistingResourceException;

import static com.hp.hpl.jena.vocabulary.RDFS.label;
import static junit.framework.Assert.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.triple_brain.graphmanipulator.jena.graph.JenaGraphElementManipulator.*;
import static org.triple_brain.graphmanipulator.jena.graph.JenaVertexManipulator.jenaVertexManipulatorWithJenaGraphManipulator;

/**
 * @author Vincent Blouin
 */
public class JenaGraphElementManipulatorTest {

    private JenaGraphElementManipulator jenaGraphElementManipulator;
    private JenaVertexManipulator jenaVertexManipulator;
    private Resource defaultCenterVertex;

    @Before
    public void setUp() {
        JenaGraphManipulator jenaGraphManipulator = JenaGraphManipulator.jenaGraphManipulatorWithDefaultUser();
        jenaVertexManipulator = jenaVertexManipulatorWithJenaGraphManipulator(jenaGraphManipulator);
        jenaGraphElementManipulator = jenaGraphElementManipulatorWithJenaGraphManipulator(jenaGraphManipulator);
        defaultCenterVertex = jenaGraphElementManipulator.defaultUser().absoluteCentralVertex();
    }

    @Test
    public void can_update_label() {
        Statement newStatement = jenaVertexManipulator.addVertexAndRelation(defaultCenterVertex.getLocalName());
        Resource edge = newStatement.getPredicate();
        jenaGraphElementManipulator.updateLabel(edge.getLocalName(), "likes");
        edge = jenaGraphElementManipulator.graph().getResource(edge.getURI());
        String edgeLabel = edge.getProperty(label).getLiteral().toString();
        assertThat(edgeLabel, is("likes"));

        Resource vertex = newStatement.getObject().asResource();
        jenaGraphElementManipulator.updateLabel(vertex.getLocalName(), "Ju-Ji-Tsu");
        vertex = jenaGraphElementManipulator.graph().getResource(vertex.getURI());
        String vertexLabel = vertex.getProperty(label).getLiteral().toString();
        assertThat(vertexLabel, is("Ju-Ji-Tsu"));
    }

    @Test
    public void update_label_with_non_existent_resource_throws_an_error() {
        try {
            jenaGraphElementManipulator.updateLabel("invalid_URI", "new label");
            fail();
        } catch (NonExistingResourceException e) {
            assertThat(e.getMessage(), is("Resource with URI :" + jenaGraphElementManipulator.defaultUser().URI() + "invalid_URI not found"));
        }
    }
}
