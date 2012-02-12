package graph;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import org.junit.Before;
import org.junit.Test;
import org.triple_brain.graphmanipulator.jena.FOAFModel;
import org.triple_brain.graphmanipulator.jena.graph.JenaGraphManipulator;
import org.triple_brain.graphmanipulator.jena.graph.JenaVertexManipulator;
import org.triple_brain.graphmanipulator.jena.graph.exceptions.NonExistingResourceException;

import static com.hp.hpl.jena.vocabulary.RDFS.label;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.triple_brain.graphmanipulator.jena.graph.JenaGraphManipulator.jenaGraphManipulatorWithDefaultUser;
import static org.triple_brain.graphmanipulator.jena.graph.JenaVertexManipulator.jenaVertexManipulatorWithJenaGraphManipulator;
import static com.hp.hpl.jena.vocabulary.RDF.*;

/**
 * @author Vincent Blouin
 */
public class JenaVertexManipulatorTest {

    private JenaVertexManipulator jenaVertexManipulator;
    private Resource defaultCenterVertex;
    private Resource middleVertex;
    private Resource endVertex;

    @Before
    public void setUp() {
        JenaGraphManipulator jenaGraphManipulator = JenaGraphManipulator.jenaGraphManipulatorWithDefaultUser();
        jenaVertexManipulator = jenaVertexManipulatorWithJenaGraphManipulator(jenaGraphManipulator);
        //creating graph defaultCenterVertexId -> secondVertexId -> thirdVertexId
        defaultCenterVertex = jenaVertexManipulator.graph().listSubjects().nextResource();

        middleVertex = jenaVertexManipulator.addVertexAndRelation(defaultCenterVertex.getLocalName()).getObject().asResource();
        endVertex = jenaVertexManipulator.addVertexAndRelation(middleVertex.getLocalName()).getObject().asResource();
    }

    @Test
    public void can_add_vertex_and_relation() {
        Integer numberOfEdgesAndVertices = jenaVertexManipulator.defaultUser().model().listSubjects().toList().size();
        Statement newStatement = jenaVertexManipulator.addVertexAndRelation(defaultCenterVertex.getLocalName());

        Integer newNumberOfEdgesAndVertices = jenaVertexManipulator.defaultUser().model().listSubjects().toList().size();
        assertThat(newNumberOfEdgesAndVertices, is(numberOfEdgesAndVertices + 2));
        assertTrue(defaultCenterVertex.hasProperty(newStatement.getPredicate()));

        assertThat(newStatement.getSubject().getURI(), is(defaultCenterVertex.getURI()));

        assertThat(newStatement.getPredicate(), is(not(nullValue())));
        assertThat(newStatement.getPredicate().listProperties().toList().size(), is(1));
        assertTrue(newStatement.getPredicate().hasProperty(label));

        assertThat(newStatement.getObject(), is(not(nullValue())));
        assertTrue(newStatement.getObject().isResource());
        Resource newObject = (Resource) newStatement.getObject();
        assertThat(newObject.listProperties().toList().size(), is(1));
        assertTrue(newObject.hasProperty(label));
    }

    @Test
    public void add_vertex_and_relation_with_non_existent_source_vertex_throws_an_error() {
        Integer numberOfEdgesAndVertices = jenaVertexManipulator.graph().listSubjects().toList().size();
        try {
            jenaVertexManipulator.addVertexAndRelation("invalid_URI");
            fail();
        } catch (NonExistingResourceException e) {
            assertThat(e.getMessage(), is("Resource with URI :" + jenaVertexManipulator.defaultUser().URI() + "invalid_URI not found"));
        }
        assertThat(jenaVertexManipulator.graph().listSubjects().toList().size(), is(numberOfEdgesAndVertices));
    }

    @Test
    public void can_remove_a_vertex() {
        Integer numberOfEdgesAndVertices = jenaVertexManipulator.graph().listSubjects().toList().size();

        assertTrue(jenaVertexManipulator.graph().containsResource(middleVertex));
        jenaVertexManipulator.removeVertex(middleVertex.getLocalName());
        assertFalse(jenaVertexManipulator.graph().containsResource(middleVertex));

        Integer updatedNumberOfEdgesAndVertices = jenaVertexManipulator.graph().listSubjects().toList().size();
        assertThat(updatedNumberOfEdgesAndVertices, is(numberOfEdgesAndVertices - 3));
    }

    @Test
    public void remove_vertex_with_non_existent_edge_throws_an_error() {
        Integer numberOfEdgesAndVertices = jenaVertexManipulator.graph().listSubjects().toList().size();
        try {
            jenaVertexManipulator.removeVertex("invalid_URI");
            fail();
        } catch (NonExistingResourceException e) {
            assertThat(e.getMessage(), is("Resource with URI :" + jenaVertexManipulator.defaultUser().URI() + "invalid_URI not found"));
        }
        assertThat(jenaVertexManipulator.graph().listSubjects().toList().size(), is(numberOfEdgesAndVertices));
    }

    @Test
    public void can_update_label() {
        Statement newStatement = jenaVertexManipulator.addVertexAndRelation(defaultCenterVertex.getLocalName());
        Resource vertex = newStatement.getObject().asResource();
        jenaVertexManipulator.updateLabel(vertex.getLocalName(), "Ju-Ji-Tsu");
        vertex = jenaVertexManipulator.graph().getResource(vertex.getURI());
        String vertexLabel = vertex.getProperty(label).getLiteral().toString();
        assertThat(vertexLabel, is("Ju-Ji-Tsu"));
    }

    @Test
    public void can_set_type_of_vertex(){
        assertFalse(defaultCenterVertex.hasProperty(type));
        String personClassURI = "http://xmlns.com/foaf/0.1/Person";
        jenaVertexManipulator.semanticType(defaultCenterVertex.getLocalName(), personClassURI);
        assertThat(defaultCenterVertex.getProperty(type).getObject().asResource().getURI(), is("http://xmlns.com/foaf/0.1/Person"));
    }
}

