package graph;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import graph.mock.JenaGraphManipulatorMock;
import org.junit.Before;
import org.junit.Test;
import org.triple_brain.graphmanipulator.jena.graph.JenaVertexManipulator;
import org.triple_brain.graphmanipulator.jena.graph.exceptions.NonExistingResourceException;

import static com.hp.hpl.jena.vocabulary.RDF.type;
import static com.hp.hpl.jena.vocabulary.RDFS.label;
import static junit.framework.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.triple_brain.graphmanipulator.jena.graph.JenaVertexManipulator.jenaVertexManipulatorWithJenaGraphManipulator;

/**
 * @author Vincent Blouin
 */
public class JenaVertexManipulatorTest {

    JenaGraphManipulatorMock graphManipulator;
    private JenaVertexManipulator vertexManipulator;
    private Resource defaultCenterVertex;
    private Resource middleVertex;
    private Resource endVertex;

    @Before
    public void setUp() {
        graphManipulator = JenaGraphManipulatorMock.jenaGraphManipulatorWithDefaultUser();
        vertexManipulator = jenaVertexManipulatorWithJenaGraphManipulator(graphManipulator);
        //creating graph defaultCenterVertexId -> secondVertexId -> thirdVertexId
        defaultCenterVertex = vertexManipulator.graph().listSubjects().nextResource();

        middleVertex = vertexManipulator.addVertexAndRelation(defaultCenterVertex.getLocalName()).getObject().asResource();
        endVertex = vertexManipulator.addVertexAndRelation(middleVertex.getLocalName()).getObject().asResource();
    }

    @Test
    public void can_add_vertex_and_relation() {
        Integer numberOfEdgesAndVertices = graphManipulator.numberOfEdgesAndVertices();
        Statement newStatement = vertexManipulator.addVertexAndRelation(defaultCenterVertex.getLocalName());

        Integer newNumberOfEdgesAndVertices = graphManipulator.numberOfEdgesAndVertices();
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
        Integer numberOfEdgesAndVertices = graphManipulator.numberOfEdgesAndVertices();
        try {
            vertexManipulator.addVertexAndRelation("invalid_URI");
            fail();
        } catch (NonExistingResourceException e) {
            assertThat(e.getMessage(), is("Resource with URI :" + vertexManipulator.defaultUser().URI() + "invalid_URI not found"));
        }
        assertThat(graphManipulator.numberOfEdgesAndVertices(), is(numberOfEdgesAndVertices));
    }

    @Test
    public void can_remove_a_vertex() {
        Integer numberOfEdgesAndVertices = vertexManipulator.graph().listSubjects().toList().size();

        assertTrue(vertexManipulator.graph().containsResource(middleVertex));
        vertexManipulator.removeVertex(middleVertex.getLocalName());
        assertFalse(vertexManipulator.graph().containsResource(middleVertex));

        Integer updatedNumberOfEdgesAndVertices = vertexManipulator.graph().listSubjects().toList().size();
        assertThat(updatedNumberOfEdgesAndVertices, is(numberOfEdgesAndVertices - 3));
    }

    @Test
    public void remove_vertex_with_non_existent_edge_throws_an_error() {
        Integer numberOfEdgesAndVertices = vertexManipulator.graph().listSubjects().toList().size();
        try {
            vertexManipulator.removeVertex("invalid_URI");
            fail();
        } catch (NonExistingResourceException e) {
            assertThat(e.getMessage(), is("Resource with URI :" + vertexManipulator.defaultUser().URI() + "invalid_URI not found"));
        }
        assertThat(vertexManipulator.graph().listSubjects().toList().size(), is(numberOfEdgesAndVertices));
    }

    @Test
    public void can_update_label() {
        Statement newStatement = vertexManipulator.addVertexAndRelation(defaultCenterVertex.getLocalName());
        Resource vertex = newStatement.getObject().asResource();
        vertexManipulator.updateLabel(vertex.getLocalName(), "Ju-Ji-Tsu");
        vertex = vertexManipulator.graph().getResource(vertex.getURI());
        String vertexLabel = vertex.getProperty(label).getLiteral().toString();
        assertThat(vertexLabel, is("Ju-Ji-Tsu"));
    }

    @Test
    public void can_set_type_of_vertex(){
        assertFalse(defaultCenterVertex.hasProperty(type));
        String personClassURI = "http://xmlns.com/foaf/0.1/Person";
        vertexManipulator.semanticType(defaultCenterVertex.getLocalName(), personClassURI);
        assertThat(defaultCenterVertex.getProperty(type).getObject().asResource().getURI(), is("http://xmlns.com/foaf/0.1/Person"));
    }
}

