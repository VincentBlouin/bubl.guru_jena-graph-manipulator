package graph;

import org.junit.Test;
import org.triple_brain.module.graph_manipulator.exceptions.InvalidDepthOfSubVerticesException;
import org.triple_brain.module.graph_manipulator.exceptions.NonExistingResourceException;
import org.triple_brain.module.model.graph.Edge;
import org.triple_brain.module.model.graph.Graph;
import org.triple_brain.module.model.graph.Vertex;

import java.util.List;

import static graph.mock.JenaGraphManipulatorMock.DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.*;

/**
 * Copyright Mozilla Public License 1.1
 */
public class JenaGraphManipulatorTest extends JenaGeneralGraphManipulatorTest{

    @Test
    public void can_get_graph_with_default_center_vertex() {
        Graph graph = graphManipulator.graphWithDefaultVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
        );
        assertThat(graph, is(not(nullValue())));
        assertThat(graph.numberOfVertices(), is(3));
        assertThat(graph.numberOfEdges(), is(2));
        assertTrue(graph.containsVertex(vertexA));
    }

    @Test
    public void can_get_graph_with_custom_center_vertex() {
        Graph graph = graphManipulator.graphWithDepthAndCenterVertexId(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES,
                vertexB.id());
        assertThat(graph, is(not(nullValue())));
        Vertex centerVertex = graph.vertexWithIdentifier(vertexB.id());
        assertThat(graph.numberOfEdgesAndVertices(), is(graphManipulator.numberOfEdgesAndVertices()));
        assertThat(centerVertex.label(), is("vertex B"));
    }

    @Test
    
    public void can_get_circular_graph_with_default_center_vertex() {
        edgeManipulator.addRelationBetweenVertices(
                vertexC.id(),
                vertexA.id()
        );
        Graph graph = graphManipulator.graphWithDefaultVertexAndDepth(DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES);
        assertThat(graph, is(not(nullValue())));
        assertThat(graph.numberOfEdgesAndVertices(), is(graphManipulator.numberOfEdgesAndVertices()));
        Vertex centerVertex = graph.vertexWithIdentifier(vertexA.id());
        assertThat(centerVertex.label(), is("vertex A"));
    }

    @Test
    
    public void can_get_a_limited_graph_with_default_center_vertex() throws Exception {
        Graph subGraph = graphManipulator.graphWithDefaultVertexAndDepth(2);
        assertThat(subGraph.numberOfEdges(), is(2));
        assertThat(subGraph.numberOfVertices(), is(3));
        assertTrue(subGraph.containsVertex(vertexA));

        subGraph = graphManipulator.graphWithDefaultVertexAndDepth(1);
        assertThat(subGraph.numberOfEdges(), is(1));
        assertThat(subGraph.numberOfVertices(), is(2));
        assertFalse(subGraph.containsVertex(vertexC));
        assertTrue(subGraph.containsVertex(vertexA));
    }

    @Test
    
    public void can_get_a_limited_graph_with_a_custom_center_vertex() {
        Graph subGraph = graphManipulator.graphWithDepthAndCenterVertexId(
                1,
                vertexC.id()
        );
        assertThat(subGraph.numberOfVertices(), is(2));
        assertThat(subGraph.numberOfEdges(), is(1));
        assertFalse(subGraph.containsVertex(vertexA));
        assertTrue(subGraph.containsVertex(vertexC));
    }

    @Test
    
    public void can_get_sub_graph_of_destination_vertex_of_center_vertex() {
       Edge newEdge = vertexManipulator.addVertexAndRelation(
               vertexC.id()
       );
        Graph subGraph = graphManipulator.graphWithDepthAndCenterVertexId(
                2, vertexB.id()
        );
        assertThat(subGraph.numberOfEdges(), is(3));
        assertThat(subGraph.numberOfVertices(), is(4));

        assertTrue(subGraph.containsVertex(vertexA));
        assertTrue(subGraph.containsVertex(vertexB));
        assertTrue(subGraph.containsVertex(vertexC));
        assertTrue(subGraph.containsVertex(newEdge.destinationVertex()));
    }

    @Test
    
    public void can_get_sub_graph_of_source_vertex_of_center_vertex() {
        Graph subGraph;
        Edge newEdge = vertexManipulator.addVertexAndRelation(
                vertexA.id()
        );
        subGraph = graphManipulator.graphWithDepthAndCenterVertexId(
                2, vertexB.id()
        );
        assertThat(subGraph.numberOfVertices(), is(4));
        assertThat(subGraph.numberOfEdges(), is(3));

        assertTrue(subGraph.containsVertex(newEdge.destinationVertex()));
        assertTrue(subGraph.containsVertex(vertexA));
        assertTrue(subGraph.containsVertex(vertexB));
        assertTrue(subGraph.containsVertex(vertexC));
    }

    @Test
    
    public void can_get_sub_graph_of_source_vertex_of_center_vertex_having_also_a_circular_relation() {
         edgeManipulator.addRelationBetweenVertices(
                 vertexC.id(),
                 vertexA.id()
         );
         Edge edgeGoingOutOfC = vertexManipulator.addVertexAndRelation(vertexC.id());

         Graph subGraph = graphManipulator.graphWithDepthAndCenterVertexId(
                 2,
                 vertexA.id()
         );
         assertTrue(subGraph.containsVertex(edgeGoingOutOfC.destinationVertex()));
    }

    @Test
    
    public void with_a_depth_of_sub_vertices_of_zero_only_central_vertex_is_returned() {
        Graph subGraph = graphManipulator.graphWithDefaultVertexAndDepth(0);
        assertThat(subGraph.numberOfVertices(), is(1));
        assertThat(subGraph.numberOfEdges(), is(0));
        assertTrue(subGraph.containsVertex(vertexA));

        subGraph = graphManipulator.graphWithDepthAndCenterVertexId(
                0, vertexB.id()
        );
        assertThat(subGraph.numberOfVertices(), is(1));
        assertThat(subGraph.numberOfEdges(), is(0));
        assertTrue(subGraph.containsVertex(vertexB));
    }

    @Test
    public void an_exception_is_thrown_when_getting_graph_with_default_center_vertex_with_negative_depth() {
        try {
            graphManipulator.graphWithDefaultVertexAndDepth(-1);
            fail();
        } catch (InvalidDepthOfSubVerticesException e) {
            assertThat(e.getMessage(), is("Invalid depth of sub vertices. Depth was:-1 and center vertex id was:" + vertexA.id()));
        }
    }

    @Test
    
    public void an_exception_is_thrown_when_getting_graph_with_custom_center_vertex_with_negative_depth() {
        try {
            graphManipulator.graphWithDepthAndCenterVertexId(-1, vertexB.id());
            fail();
        } catch (InvalidDepthOfSubVerticesException e) {
            assertThat(e.getMessage(), is("Invalid depth of sub vertices. Depth was:-1 and center vertex id was:" + vertexB.id()));
        }
    }

    @Test
    
    public void an_exception_is_thrown_when_getting_graph_with_non_existing_center_vertex() {
        Integer numberOfEdgesAndVertices = graphManipulator.numberOfEdgesAndVertices();
        try {
            graphManipulator.graphWithDepthAndCenterVertexId(1, "invalid_URI");
            fail();
        } catch (NonExistingResourceException e) {
            assertThat(e.getMessage(), is("Resource with URI: invalid_URI not found"));
        }
        assertThat(graphManipulator.numberOfEdgesAndVertices(), is(numberOfEdgesAndVertices));
    }

    @Test
    
    public void frontier_vertices_with_hidden_vertices_have_a_list_of_their_hidden_properties_name() {
        Edge newEdge = vertexManipulator.addVertexAndRelation(vertexB.id());
        newEdge.label("new edge");
        Graph subGraph = graphManipulator.graphWithDepthAndCenterVertexId(
                1,
                vertexA.id()
        );
        List<String> vertexBConnectedEdgesLabel = subGraph.vertexWithIdentifier(vertexB.id())
                .hiddenConnectedEdgesLabel();
        assertFalse(vertexBConnectedEdgesLabel.isEmpty());
        assertThat(vertexBConnectedEdgesLabel.size(), is(2));
        assertTrue(vertexBConnectedEdgesLabel.contains("between vertex B and vertex C"));
        assertTrue(vertexBConnectedEdgesLabel.contains("new edge"));
    }

    @Test
    
    public void vertices_have_their_minimum_number_of_edges_from_center_vertex() {
        Graph subGraph = graphManipulator.graphWithDepthAndCenterVertexId(
                2, vertexA.id()
        );
        Vertex vertexAInSubGraph = subGraph.vertexWithIdentifier(vertexA.id());
        Vertex vertexBInSubGraph = subGraph.vertexWithIdentifier(vertexB.id());
        Vertex vertexCInSubGraph = subGraph.vertexWithIdentifier(vertexC.id());

        assertThat(vertexAInSubGraph.minNumberOfEdgesFromCenterVertex(), is(0));
        assertThat(vertexBInSubGraph.minNumberOfEdgesFromCenterVertex(), is(1));
        assertThat(vertexCInSubGraph.minNumberOfEdgesFromCenterVertex(), is(2));
    }

    @Test
    
    public void with_multiple_edges_pointing_at_a_vertex_the_minimum_number_of_edges_to_get_to_the_vertex_from_the_center_vertex_is_set() {
        edgeManipulator.addRelationBetweenVertices(
                vertexA.id(), vertexC.id()
        );
        Graph subGraph = graphManipulator.graphWithDepthAndCenterVertexId(
                2, vertexA.id()
        );
        Vertex vertexCInSubGraph = subGraph.vertexWithIdentifier(vertexC.id());
        assertThat(vertexCInSubGraph.minNumberOfEdgesFromCenterVertex(), is(1));
    }

    @Test
    
    public void edges_direction_does_not_have_a_relevance_to_the_minimum_number_of_edges() {
        Edge edgeBetweenCAndA = edgeManipulator.addRelationBetweenVertices(
                vertexA.id(), vertexC.id()
        );
        Graph subGraph = graphManipulator.graphWithDepthAndCenterVertexId(
                2, vertexA.id()
        );
        Vertex vertexCInSubGraph = subGraph.vertexWithIdentifier(vertexC.id());
        assertThat(vertexCInSubGraph.minNumberOfEdgesFromCenterVertex(), is(1));

        edgeManipulator.removeEdge(edgeBetweenCAndA.id());
        edgeManipulator.addRelationBetweenVertices(
                vertexC.id(), vertexA.id());

        subGraph = graphManipulator.graphWithDepthAndCenterVertexId(
                2, vertexA.id());
        vertexCInSubGraph = subGraph.vertexWithIdentifier(vertexC.id());
        assertThat(vertexCInSubGraph.minNumberOfEdgesFromCenterVertex(), is(1));
    }

    @Test
    
    public void can_get_rdf_xml_representation_of_graph(){
        assertThat(graphManipulator.toRDFXML(), is(not(nullValue())));
    }
}