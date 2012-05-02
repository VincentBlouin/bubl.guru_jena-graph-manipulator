package graph;

import graph.mock.JenaGraphManipulatorMock;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.triple_brain.graphmanipulator.jena.graph.JenaEdgeManipulator;
import org.triple_brain.graphmanipulator.jena.graph.JenaGraphManipulator;
import org.triple_brain.graphmanipulator.jena.graph.JenaVertexManipulator;
import org.triple_brain.module.graph_manipulator.exceptions.NonExistingResourceException;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.Edge;
import org.triple_brain.module.model.graph.Vertex;

import static junit.framework.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.triple_brain.graphmanipulator.jena.JenaConnection.closeConnection;


/**
 * Copyright Mozilla Public License 1.1
 */
public class JenaVertexManipulatorTest {

    JenaGraphManipulatorMock graphManipulator;
    private JenaVertexManipulator vertexManipulator;
    private JenaEdgeManipulator edgeManipulator;
    private Vertex vertexA;
    private Vertex vertexB;
    private Vertex vertexC;
    private static User user;

    @Before
    public void setUp() throws Exception{
        user = User.withUsernameAndEmail(
                "roger_lamothe",
                "roger.lamothe@example.org"
        );
        resetManipulators();
        makeGraphHave3VerticesABCWhereAIsDefaultCenterVertexAndAPointsToBAndBPointsToC();
    }

    @AfterClass
    public static void after()throws Exception{
        closeConnection();
    }

    private void resetManipulators() throws Exception{
        graphManipulator = JenaGraphManipulatorMock.mockWithUser(user);
        vertexManipulator = JenaVertexManipulator.withUser(user);
        edgeManipulator = JenaEdgeManipulator.withUser(user);
    }

    private void makeGraphHave3VerticesABCWhereAIsDefaultCenterVertexAndAPointsToBAndBPointsToC() throws Exception{
        graphManipulator.graph().removeAll();
        JenaGraphManipulator.createUserGraph(user);
        vertexA = vertexManipulator.defaultVertex();
        vertexA.label("vertex A");
        vertexB = vertexManipulator.addVertexAndRelation(vertexA.id()).destinationVertex();
        vertexB.label("vertex B");
        vertexC = vertexManipulator.addVertexAndRelation(vertexB.id()).destinationVertex();
        vertexC.label("vertex C");
        Edge betweenAAndB = vertexA.edgeThatLinksToDestinationVertex(vertexB);
        betweenAAndB.label("between vertex A and vertex B");
        Edge betweenBAndC = vertexB.edgeThatLinksToDestinationVertex(vertexC);
        betweenBAndC.label("between vertex B and vertex C");
    }

    @Test
    public void can_add_vertex_and_relation() {
        Integer numberOfEdgesAndVertices = graphManipulator.numberOfEdgesAndVertices();
        Edge edge = vertexManipulator.addVertexAndRelation(
                vertexA.id());

        assertThat(edge, is(not(nullValue())));
        assertTrue(edge.hasLabel());

        Integer newNumberOfEdgesAndVertices = graphManipulator.numberOfEdgesAndVertices();
        assertThat(newNumberOfEdgesAndVertices, is(numberOfEdgesAndVertices + 2));
        assertTrue(vertexA.hasEdge(edge));

        assertThat(edge.sourceVertex().id(), is(vertexA.id()));

        Vertex destinationVertex = edge.destinationVertex();
        assertThat(destinationVertex, is(not(nullValue())));
        assertTrue(destinationVertex.hasLabel());
    }

    @Test
    public void add_vertex_and_relation_with_non_existent_source_vertex_throws_an_error() {
        Integer numberOfEdgesAndVertices = graphManipulator.numberOfEdgesAndVertices();
        try {
            vertexManipulator.addVertexAndRelation("invalid_URI");
            fail();
        } catch (NonExistingResourceException e) {
            assertThat(e.getMessage(), is("Resource with URI: invalid_URI not found"));
        }
        assertThat(graphManipulator.numberOfEdgesAndVertices(), is(numberOfEdgesAndVertices));
    }

    @Test
    public void can_remove_a_vertex() {
        Integer numberOfEdgesAndVertices = graphManipulator.numberOfEdgesAndVertices();

        assertTrue(graphManipulator.containsElement(vertexB));
        vertexManipulator.removeVertex(vertexB.id());
        assertFalse(graphManipulator.containsElement(vertexB));

        Integer updatedNumberOfEdgesAndVertices = graphManipulator.numberOfEdgesAndVertices();
        assertThat(updatedNumberOfEdgesAndVertices, is(numberOfEdgesAndVertices - 3));
    }

    @Test
    public void remove_vertex_with_non_existent_edge_throws_an_error() {
        Integer numberOfEdgesAndVertices = graphManipulator.numberOfEdgesAndVertices();
        try {
            vertexManipulator.removeVertex("invalid_URI");
            fail();
        } catch (NonExistingResourceException e) {
            assertThat(e.getMessage(), is("Resource with URI: invalid_URI not found"));
        }
        assertThat(graphManipulator.numberOfEdgesAndVertices(), is(numberOfEdgesAndVertices));
    }

    @Test
    public void can_update_label() {
        Edge newEdge = vertexManipulator.addVertexAndRelation(vertexA.id());
        Vertex vertex = newEdge.destinationVertex();
        vertexManipulator.updateLabel(vertex.id(), "Ju-Ji-Tsu");
        assertThat(vertex.label(), is("Ju-Ji-Tsu"));
    }

    @Test
    public void can_set_type_of_vertex(){
        String personClassURI = "http://xmlns.com/foaf/0.1/Person";
        assertFalse(vertexA.types().contains(personClassURI));
        vertexManipulator.semanticType(vertexA.id(), personClassURI);
        assertTrue(vertexA.types().contains(personClassURI));
    }
}

