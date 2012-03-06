package graph;

import com.hp.hpl.jena.rdf.model.*;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.triple_brain.graphmanipulator.jena.graph.JenaEdgeManipulator;
import org.triple_brain.graphmanipulator.jena.graph.JenaGraphManipulator;
import org.triple_brain.graphmanipulator.jena.graph.JenaVertexManipulator;
import org.triple_brain.graphmanipulator.jena.graph.exceptions.InvalidDepthOfSubVerticesException;
import org.triple_brain.graphmanipulator.jena.graph.exceptions.NonExistingResourceException;

import static com.hp.hpl.jena.vocabulary.RDFS.label;
import static junit.framework.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.triple_brain.graphmanipulator.jena.TripleBrainModel.*;
import static org.triple_brain.graphmanipulator.jena.graph.JenaEdgeManipulator.jenaEdgeManipulatorWithJenaGraphManipulator;
import static org.triple_brain.graphmanipulator.jena.graph.JenaGraphManipulator.jenaGraphManipulatorWithDefaultUser;
import static org.triple_brain.graphmanipulator.jena.graph.JenaVertexManipulator.jenaVertexManipulatorWithJenaGraphManipulator;

/**
 * @author Vincent Blouin
 */
public class JenaGraphManipulatorTest {

    private JenaGraphManipulator jenaGraphManipulator;
    private JenaVertexManipulator jenaVertexManipulator;
    private JenaEdgeManipulator jenaEdgeManipulator;
    private Resource defaultCenterVertex;
    private Resource middleVertex;
    private Resource endVertex;

    private final Integer DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES = 10;

    @Before
    public void setUp() {
        jenaGraphManipulator = JenaGraphManipulator.jenaGraphManipulatorWithDefaultUser();
        jenaVertexManipulator = jenaVertexManipulatorWithJenaGraphManipulator(jenaGraphManipulator);
        jenaEdgeManipulator = jenaEdgeManipulatorWithJenaGraphManipulator(jenaGraphManipulator);
        //creating graph defaultCenterVertexId -> secondVertexId -> thirdVertexId
        defaultCenterVertex = jenaGraphManipulator.defaultUser().absoluteCentralVertex();
        middleVertex = jenaVertexManipulator.addVertexAndRelation(defaultCenterVertex.getLocalName()).getObject().asResource();
        endVertex = jenaVertexManipulator.addVertexAndRelation(middleVertex.getLocalName()).getObject().asResource();

        Property betweenDefaultAndMiddleVertex = relationBetweenVerticesInGraph(defaultCenterVertex, middleVertex, jenaGraphManipulator.graph());
        jenaEdgeManipulator.updateLabel(betweenDefaultAndMiddleVertex.getLocalName(), "between default and middle vertex");
        Property betweenMiddleAndEndVertex = relationBetweenVerticesInGraph(middleVertex, endVertex, jenaGraphManipulator.graph());
        jenaEdgeManipulator.updateLabel(betweenMiddleAndEndVertex.getLocalName(), "between middle and end vertex");
        jenaVertexManipulator.updateLabel(middleVertex.getLocalName(), "middle vertex");
        jenaVertexManipulator.updateLabel(endVertex.getLocalName(), "end vertex");
    }

    @Test
    public void can_get_graph_with_default_center_vertex() {
        jenaGraphManipulator = jenaGraphManipulatorWithDefaultUser();
        Model jenaGraph = jenaGraphManipulator.graphWithDefaultVertexAndDepth(DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES);
        Resource centerVertex = jenaGraph.getResource(defaultCenterVertex.getURI());
        assertThat(jenaGraph, is(not(nullValue())));
        assertThat(jenaGraph.listSubjects().toList().size(), Is.is(jenaGraphManipulator.graph().listSubjects().toList().size()));
        assertTrue(centerVertex.hasProperty(label));
        assertThat(centerVertex.getProperty(label).getString(), is("me"));
    }

    @Test
    public void can_get_graph_with_custom_center_vertex() {
        Model jenaGraph = jenaGraphManipulator.graphWithDepthAndCenterVertexId(DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES, middleVertex.getLocalName());
        Resource centerVertex = jenaGraph.getResource(middleVertex.getURI());
        assertThat(jenaGraph, is(not(nullValue())));
        assertThat(jenaGraph.listSubjects().toList().size(), Is.is(jenaGraphManipulator.graph().listSubjects().toList().size()));
        assertTrue(centerVertex.hasProperty(label));
        assertThat(centerVertex.getProperty(label).getString(), is("middle vertex"));
    }

    @Test
    public void can_get_circular_graph_with_default_center_vertex() {
        jenaEdgeManipulator.addRelationBetweenVertices(endVertex.getLocalName(), defaultCenterVertex.getLocalName());
        Model jenaGraph = jenaGraphManipulator.graphWithDefaultVertexAndDepth(DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES);
        assertThat(jenaGraph, is(not(nullValue())));
        assertThat(jenaGraph.listSubjects().toList().size(), Is.is(jenaGraphManipulator.graph().listSubjects().toList().size()));
        Resource centerVertex = jenaGraph.getResource(defaultCenterVertex.getURI());
        assertTrue(centerVertex.hasProperty(label));
        assertThat(centerVertex.getProperty(label).getString(), is("me"));
    }

    @Test
    public void can_get_circular_graph_with_custom_center_vertex() {
        jenaEdgeManipulator.addRelationBetweenVertices(endVertex.getLocalName(), defaultCenterVertex.getLocalName());
        Model jenaGraph = jenaGraphManipulator.graphWithDepthAndCenterVertexId(DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES, middleVertex.getLocalName());
        assertThat(jenaGraph, is(not(nullValue())));
        assertThat(jenaGraph.listSubjects().toList().size(), Is.is(jenaGraphManipulator.graph().listSubjects().toList().size()));
        Resource centerVertex = jenaGraph.getResource(middleVertex.getURI());
        assertTrue(centerVertex.hasProperty(label));
        assertThat(centerVertex.getProperty(label).getString(), is("middle vertex"));
    }

    @Test
    public void can_get_a_limited_graph_with_default_center_vertex() throws Exception {
        Integer numberOfExpectedVertices;
        Integer numberOfExpectedEdges;
        Model subGraph;
        Resource centerVertex;

        subGraph = jenaGraphManipulator.graphWithDefaultVertexAndDepth(2);
        numberOfExpectedVertices = 4;
        numberOfExpectedEdges = 3;
        assertThat(numberOfVerticesAndEdgesInGraph(subGraph), is(numberOfExpectedVertices + numberOfExpectedEdges));
        centerVertex = subGraph.getResource(defaultCenterVertex.getURI());
        assertTrue(centerVertex.hasProperty(label));
        assertThat(centerVertex.getProperty(label).getString(), is("me"));

        subGraph = jenaGraphManipulator.graphWithDefaultVertexAndDepth(1);
        numberOfExpectedVertices = 3;
        numberOfExpectedEdges = 2;
        assertThat(numberOfVerticesAndEdgesInGraph(subGraph), is(numberOfExpectedVertices + numberOfExpectedEdges));
        assertFalse(subGraph.containsResource(endVertex));
        centerVertex = subGraph.getResource(defaultCenterVertex.getURI());
        assertTrue(centerVertex.hasProperty(label));
        assertThat(centerVertex.getProperty(label).getString(), is("me"));
    }

    @Test
    public void can_get_a_limited_graph_with_a_custom_center_vertex() {
        Integer numberOfExpectedVertices;
        Integer numberOfExpectedEdges;
        Model subGraph;
        Resource centerVertex;

        subGraph = jenaGraphManipulator.graphWithDepthAndCenterVertexId(1, endVertex.getLocalName());
        numberOfExpectedVertices = 2;
        numberOfExpectedEdges = 1;
        assertThat(numberOfVerticesAndEdgesInGraph(subGraph), is(numberOfExpectedVertices + numberOfExpectedEdges));
        assertFalse(subGraph.containsResource(defaultCenterVertex));
        centerVertex = subGraph.getResource(endVertex.getURI());
        assertTrue(centerVertex.hasProperty(label));
        assertThat(centerVertex.getProperty(label).getString(), is("end vertex"));
    }

    @Test
    public void can_get_sub_graph_elements_of_a_vertex_that_is_pointed_by_center_vertex() {
        Integer numberOfExpectedVertices;
        Integer numberOfExpectedEdges;
        Model subGraph;
        Resource centerVertex;

        jenaVertexManipulator.addVertexAndRelation(endVertex.getLocalName());

        subGraph = jenaGraphManipulator.graphWithDepthAndCenterVertexId(2, middleVertex.getLocalName());
        numberOfExpectedVertices = 5;
        numberOfExpectedEdges = 4;
        assertThat(numberOfVerticesAndEdgesInGraph(subGraph), is(numberOfExpectedVertices + numberOfExpectedEdges));
        centerVertex = subGraph.getResource(middleVertex.getURI());
        assertTrue(centerVertex.hasProperty(label));
        assertThat(centerVertex.getProperty(label).getString(), is("middle vertex"));
    }

    @Test
    public void can_get_sub_graph_elements_of_a_vertex_that_points_to_center_vertex() {
        Integer numberOfExpectedVertices;
        Integer numberOfExpectedEdges;
        Model subGraph;
        Resource centerVertex;

        jenaVertexManipulator.addVertexAndRelation(defaultCenterVertex.getLocalName());

        subGraph = jenaGraphManipulator.graphWithDepthAndCenterVertexId(2, middleVertex.getLocalName());
        numberOfExpectedVertices = 5;
        numberOfExpectedEdges = 4;
        assertThat(numberOfVerticesAndEdgesInGraph(subGraph), is(numberOfExpectedVertices + numberOfExpectedEdges));
        centerVertex = subGraph.getResource(middleVertex.getURI());
        assertTrue(centerVertex.hasProperty(label));
        assertThat(centerVertex.getProperty(label).getString(), is("middle vertex"));

    }

    @Test
    public void can_get_sub_graph_of_vertex_that_points_to_center_vertex_and_has_a_circular_relation_with_it() {
         jenaEdgeManipulator.addRelationBetweenVertices(endVertex.getLocalName(), defaultCenterVertex.getLocalName());
         Resource vertexThatShouldBeOnDepthTwo = jenaVertexManipulator.addVertexAndRelation(endVertex.getLocalName()).getObject().asResource();
         Model subGraph = jenaGraphManipulator.graphWithDepthAndCenterVertexId(2, defaultCenterVertex.getLocalName());
         assertTrue(subGraph.containsResource(vertexThatShouldBeOnDepthTwo));
    }

    @Test
    public void with_a_depth_of_sub_vertices_of_zero_only_central_vertex_is_returned() {
        Model subGraph = jenaGraphManipulator.graphWithDefaultVertexAndDepth(0);
        assertThat(numberOfVerticesAndEdgesInGraph(subGraph), is(1));
        Resource centerVertex = subGraph.getResource(defaultCenterVertex.getURI());
        assertTrue(subGraph.containsResource(centerVertex));

        subGraph = jenaGraphManipulator.graphWithDepthAndCenterVertexId(0, middleVertex.getLocalName());
        assertThat(numberOfVerticesAndEdgesInGraph(subGraph), is(1));
        centerVertex = subGraph.getResource(middleVertex.getURI());
        assertTrue(subGraph.containsResource(centerVertex));
    }

    @Test
    public void get_graph_with_default_center_vertex_throws_exception_with_negative_depth_of_sub_vertices() {
        try {
            jenaGraphManipulator.graphWithDefaultVertexAndDepth(-1);
            fail();
        } catch (InvalidDepthOfSubVerticesException e) {
            assertThat(e.getMessage(), is("Invalid depth of sub vertices. Depth was:-1 and center vertex id was:" + defaultCenterVertex.getLocalName()));
        }
    }

    @Test
    public void get_graph_with_custom_center_vertex_throws_exception_with_negative_depth_of_sub_vertices() {
        try {
            jenaGraphManipulator.graphWithDepthAndCenterVertexId(-1, middleVertex.getLocalName());
            fail();
        } catch (InvalidDepthOfSubVerticesException e) {
            assertThat(e.getMessage(), is("Invalid depth of sub vertices. Depth was:-1 and center vertex id was:" + middleVertex.getLocalName()));
        }
    }

    @Test
    public void get_graph_with_non_existent_center_vertex_throws_an_error() {
        Integer numberOfEdgesAndVertices = jenaGraphManipulator.graph().listSubjects().toList().size();
        try {
            jenaGraphManipulator.graphWithDepthAndCenterVertexId(1, "invalid_URI");
            fail();
        } catch (NonExistingResourceException e) {
            assertThat(e.getMessage(), is("Resource with URI :" + jenaGraphManipulator.defaultUser().URI() + "invalid_URI not found"));
        }
        assertThat(jenaGraphManipulator.graph().listSubjects().toList().size(), is(numberOfEdgesAndVertices));
    }

    @Test
    public void frontier_vertices_that_have_hidden_vertices_have_a_special_property() {
        Model jenaGraph = jenaGraphManipulator.graphWithDepthAndCenterVertexId(2, defaultCenterVertex.getLocalName());
        Resource middleVertexOfSubGraph = jenaGraph.getResource(middleVertex.getURI());
        assertFalse(middleVertexOfSubGraph.hasProperty(IS_FRONTIER_VERTEX_WITH_HIDDEN_VERTICES()));

        jenaGraph = jenaGraphManipulator.graphWithDepthAndCenterVertexId(1, defaultCenterVertex.getLocalName());
        middleVertexOfSubGraph = jenaGraph.getResource(middleVertex.getURI());
        assertTrue(middleVertexOfSubGraph.hasProperty((IS_FRONTIER_VERTEX_WITH_HIDDEN_VERTICES())));
        assertFalse(jenaGraphManipulator.graph().containsResource(IS_FRONTIER_VERTEX_WITH_HIDDEN_VERTICES()));
    }

    @Test
    public void frontier_vertices_that_dont_have_hidden_vertices_dont_have_a_special_property() {
        Model jenaGraph = jenaGraphManipulator.graphWithDepthAndCenterVertexId(2, defaultCenterVertex.getLocalName());
        Resource endVertexOfSubGraph = jenaGraph.getResource(endVertex.getURI());
        assertFalse(endVertexOfSubGraph.hasProperty(IS_FRONTIER_VERTEX_WITH_HIDDEN_VERTICES()));
    }

    @Test
    public void frontier_vertices_with_hidden_vertices_hold_their_number_of_hidden_vertices() {
        Model jenaGraph = jenaGraphManipulator.graphWithDepthAndCenterVertexId(1, middleVertex.getLocalName());
        Resource endVertexOfSubGraph = jenaGraph.getResource(endVertex.getURI());
        assertFalse(endVertexOfSubGraph.hasProperty(NUMBER_OF_HIDDEN_CONNECTED_VERTICES()));

        jenaGraph = jenaGraphManipulator.graphWithDepthAndCenterVertexId(1, defaultCenterVertex.getLocalName());
        Resource middleVertexOfSubGraph = jenaGraph.getResource(middleVertex.getURI());
        assertThat(middleVertexOfSubGraph.getProperty(NUMBER_OF_HIDDEN_CONNECTED_VERTICES()).getInt(), is(1));
        assertFalse(jenaGraphManipulator.graph().containsResource(NUMBER_OF_HIDDEN_CONNECTED_VERTICES()));

        jenaVertexManipulator.addVertexAndRelation(middleVertex.getLocalName());
        jenaGraph = jenaGraphManipulator.graphWithDepthAndCenterVertexId(1, defaultCenterVertex.getLocalName());
        middleVertexOfSubGraph = jenaGraph.getResource(middleVertex.getURI());
        assertThat(middleVertexOfSubGraph.getProperty(NUMBER_OF_HIDDEN_CONNECTED_VERTICES()).getInt(), is(2));
    }

    @Test
    public void frontier_vertices_with_hidden_vertices_have_a_list_of_their_hidden_properties_name() {
        Model subGraph = jenaGraphManipulator.graphWithDepthAndCenterVertexId(1, middleVertex.getLocalName());
        Resource endVertexOfSubGraph = subGraph.getResource(endVertex.getURI());
        assertFalse(endVertexOfSubGraph.hasProperty(NAME_OF_HIDDEN_PROPERTIES()));


        subGraph = jenaGraphManipulator.graphWithDepthAndCenterVertexId(1, defaultCenterVertex.getLocalName());
        Resource middleVertexOfSubGraph = subGraph.getResource(middleVertex.getURI());
        Seq hiddenProperties =  middleVertexOfSubGraph.getProperty(NAME_OF_HIDDEN_PROPERTIES()).getObject().as(Seq.class);
        assertThat(hiddenProperties.size(), is(1));
        assertThat(hiddenProperties.getString(1), is("between middle and end vertex"));
        assertFalse(jenaGraphManipulator.graph().containsResource(NAME_OF_HIDDEN_PROPERTIES()));

        Statement statement = jenaVertexManipulator.addVertexAndRelation(middleVertex.getLocalName());
        Property addedRelation = statement.getPredicate();
        jenaEdgeManipulator.updateLabel(addedRelation.getLocalName(), "new edge name");
        subGraph = jenaGraphManipulator.graphWithDepthAndCenterVertexId(1, defaultCenterVertex.getLocalName());
        middleVertexOfSubGraph = subGraph.getResource(middleVertex.getURI());
        hiddenProperties =  middleVertexOfSubGraph.getProperty(NAME_OF_HIDDEN_PROPERTIES()).getObject().as(Seq.class);
        assertThat(hiddenProperties.size(), is(2));
        assertThat(hiddenProperties.getString(1), is("between middle and end vertex"));
        assertThat(hiddenProperties.getString(2), is("new edge name"));
    }

    @Test
    public void vertices_have_their_minimum_number_of_edges_from_center_vertex() {
        Model subGraph = jenaGraphManipulator.graphWithDepthAndCenterVertexId(2, defaultCenterVertex.getLocalName());
        Resource defaultCenterVertexInSubGraph = subGraph.getResource(defaultCenterVertex.getURI());
        Resource middleVertexInSubGraph = subGraph.getResource(middleVertex.getURI());
        Resource endVertexInSubGraph = subGraph.getResource(endVertex.getURI());

        assertThat(defaultCenterVertexInSubGraph.getProperty(MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX()).getInt(), is(0));
        assertThat(middleVertexInSubGraph.getProperty(MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX()).getInt(), is(1));
        assertThat(endVertexInSubGraph.getProperty(MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX()).getInt(), is(2));
        assertFalse(jenaGraphManipulator.graph().containsResource(MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX()));
    }

    @Test
    public void with_multiple_edges_pointing_at_a_vertex_the_minimum_number_of_edges_to_get_to_the_vertex_from_the_center_vertex_is_set() {
        jenaEdgeManipulator.addRelationBetweenVertices(defaultCenterVertex.getLocalName(), endVertex.getLocalName());
        Model subGraph = jenaGraphManipulator.graphWithDepthAndCenterVertexId(2, defaultCenterVertex.getLocalName());
        Resource endVertexInSubGraph = subGraph.getResource(endVertex.getURI());
        assertThat(endVertexInSubGraph.getProperty(MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX()).getInt(), is(1));
    }

    @Test
    public void edges_direction_does_not_have_a_relevance_to_the_minimum_number_of_edges() {
        Statement statement = jenaEdgeManipulator.addRelationBetweenVertices(defaultCenterVertex.getLocalName(), endVertex.getLocalName());
        Model subGraph = jenaGraphManipulator.graphWithDepthAndCenterVertexId(2, defaultCenterVertex.getLocalName());
        Resource endVertexInSubGraph = subGraph.getResource(endVertex.getURI());
        assertThat(endVertexInSubGraph.getProperty(MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX()).getInt(), is(1));

        jenaEdgeManipulator.removeEdge(statement.getPredicate().getLocalName());
        jenaEdgeManipulator.addRelationBetweenVertices(endVertex.getLocalName(), defaultCenterVertex.getLocalName());

        jenaEdgeManipulator.addRelationBetweenVertices(defaultCenterVertex.getLocalName(), endVertex.getLocalName());
        subGraph = jenaGraphManipulator.graphWithDepthAndCenterVertexId(2, defaultCenterVertex.getLocalName());
        endVertexInSubGraph = subGraph.getResource(endVertex.getURI());
        assertThat(endVertexInSubGraph.getProperty(MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX()).getInt(), is(1));
    }

    @Test
    public void can_get_rdf_xml_representation_of_graph(){
        assertThat(jenaGraphManipulator.toRDFXML(), is(not(nullValue())));
    }

    private int numberOfVerticesAndEdgesInGraph(Model graph){
        return graph.listSubjectsWithProperty(label).toList().size();
    }

    private Property relationBetweenVerticesInGraph(Resource a, Resource b, Model graph){
        return graph.listStatements(new SimpleSelector(a, null , b)).nextStatement().getPredicate();
    }
}
