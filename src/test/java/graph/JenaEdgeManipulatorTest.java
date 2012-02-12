package graph;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import org.junit.Before;
import org.junit.Test;
import org.triple_brain.graphmanipulator.jena.graph.JenaEdgeManipulator;
import org.triple_brain.graphmanipulator.jena.graph.JenaGraphManipulator;
import org.triple_brain.graphmanipulator.jena.graph.JenaVertexManipulator;
import org.triple_brain.graphmanipulator.jena.graph.exceptions.NonExistingResourceException;

import java.util.List;

import static com.hp.hpl.jena.vocabulary.RDFS.label;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.triple_brain.graphmanipulator.jena.graph.JenaVertexManipulator.jenaVertexManipulatorWithJenaGraphManipulator;
import static org.triple_brain.graphmanipulator.jena.graph.JenaEdgeManipulator.*;

/**
 * @author Vincent Blouin
 */
public class JenaEdgeManipulatorTest {

    private JenaEdgeManipulator jenaEdgeManipulator;
    private JenaVertexManipulator jenaVertexManipulator;
    private Resource defaultCenterVertex;
    private Resource middleVertex;
    private Resource endVertex;

    @Before
    public void setUp() {
        JenaGraphManipulator jenaGraphManipulator = JenaGraphManipulator.jenaGraphManipulatorWithDefaultUser();
        jenaVertexManipulator = jenaVertexManipulatorWithJenaGraphManipulator(jenaGraphManipulator);
        jenaEdgeManipulator = jenaEdgeManipulatorWithJenaGraphManipulator(jenaGraphManipulator);
        //creating graph defaultCenterVertexId -> secondVertexId -> thirdVertexId
        defaultCenterVertex = jenaVertexManipulator.graph().listSubjects().nextResource();

        middleVertex = jenaVertexManipulator.addVertexAndRelation(defaultCenterVertex.getLocalName()).getObject().asResource();
        endVertex = jenaVertexManipulator.addVertexAndRelation(middleVertex.getLocalName()).getObject().asResource();
    }

    @Test
    public void can_add_relation() {
        Resource secondVertex = jenaVertexManipulator.addVertexAndRelation(defaultCenterVertex.getLocalName()).getObject().asResource();
        Resource thirdVertex = jenaVertexManipulator.addVertexAndRelation(secondVertex.getLocalName()).getObject().asResource();

        Integer numberOfEdgesAndVertices = jenaEdgeManipulator.graph().listSubjects().toList().size();
        Statement newRelation = jenaEdgeManipulator.addRelationBetweenVertices(thirdVertex.getLocalName(), defaultCenterVertex.getLocalName());

        assertThat(newRelation.getSubject().getURI(), is(thirdVertex.getURI()));
        assertThat(newRelation.getObject().asResource().getURI(), is(defaultCenterVertex.getURI()));
        assertTrue(jenaEdgeManipulator.graph().contains(newRelation));
        String edgeName = newRelation.getPredicate().asResource().getProperty(label).getLiteral().getString();
        assertThat(edgeName, is(""));
        assertThat(jenaEdgeManipulator.graph().listSubjects().toList().size(), is(numberOfEdgesAndVertices + 1));
    }

    @Test
    public void add_relation_with_non_existent_source_or_destination_throws_an_error() {
        Integer numberOfEdgesAndVertices = jenaEdgeManipulator.graph().listSubjects().toList().size();
        try {
            jenaEdgeManipulator.addRelationBetweenVertices(defaultCenterVertex.getLocalName(), "invalid_URI");
            fail();
        } catch (NonExistingResourceException e) {
            assertThat(e.getMessage(), is("Resource with URI :" + jenaEdgeManipulator.defaultUser().URI() + "invalid_URI not found"));
        }
        try {
            jenaEdgeManipulator.addRelationBetweenVertices("invalid_URI", defaultCenterVertex.getLocalName());
            fail();
        } catch (NonExistingResourceException e) {
            assertThat(e.getMessage(), is("Resource with URI :" + jenaEdgeManipulator.defaultUser().URI() + "invalid_URI not found"));
        }
        assertThat(jenaEdgeManipulator.graph().listSubjects().toList().size(), is(numberOfEdgesAndVertices));
    }

    @Test
    public void can_remove_an_edge() {
        Integer numberOfEdgesAndVertices = jenaEdgeManipulator.graph().listSubjects().toList().size();

        Statement statementWhereSubjectIsCenterVertexAndObjectIsMiddleVertex = jenaEdgeManipulator.graph().listStatements(new SimpleSelector(defaultCenterVertex, null, middleVertex)).toList().get(0);
        Resource edge = statementWhereSubjectIsCenterVertexAndObjectIsMiddleVertex.getPredicate();
        assertTrue(jenaEdgeManipulator.graph().containsResource(edge));
        jenaEdgeManipulator.removeEdge(edge.getLocalName());
        assertFalse(jenaEdgeManipulator.graph().containsResource(edge));
        List<Statement> statementsWhereSubjectIsCenterVertexAndObjectIsMiddleVertex = jenaEdgeManipulator.graph().listStatements(new SimpleSelector(defaultCenterVertex, null, middleVertex)).toList();
        assertThat(statementsWhereSubjectIsCenterVertexAndObjectIsMiddleVertex.size(), is(0));

        Integer updatedNumberOfEdgesAndVertices = jenaEdgeManipulator.graph().listSubjects().toList().size();
        assertThat(updatedNumberOfEdgesAndVertices, is(numberOfEdgesAndVertices - 1));
    }

    @Test
    public void remove_relation_with_non_existent_edge_throws_an_error() {
        Integer numberOfEdgesAndVertices = jenaEdgeManipulator.graph().listSubjects().toList().size();
        try {
            jenaEdgeManipulator.removeEdge("invalid_URI");
            fail();
        } catch (NonExistingResourceException e) {
            assertThat(e.getMessage(), is("Resource with URI :" + jenaEdgeManipulator.defaultUser().URI() + "invalid_URI not found"));
        }

        assertThat(jenaEdgeManipulator.graph().listSubjects().toList().size(), is(numberOfEdgesAndVertices));
    }

    @Test
    public void can_update_label() {
        Statement newStatement = jenaVertexManipulator.addVertexAndRelation(defaultCenterVertex.getLocalName());
        Resource edge = newStatement.getPredicate();
        jenaEdgeManipulator.updateLabel(edge.getLocalName(), "likes");
        edge = jenaEdgeManipulator.graph().getResource(edge.getURI());
        String edgeLabel = edge.getProperty(label).getLiteral().toString();
        assertThat(edgeLabel, is("likes"));
    }
}
