package graph;

import org.junit.Test;
import org.triple_brain.module.model.FriendlyResource;
import org.triple_brain.module.model.Suggestion;
import org.triple_brain.module.model.graph.Edge;
import org.triple_brain.module.model.graph.SubGraph;
import org.triple_brain.module.model.graph.Vertex;
import org.triple_brain.module.model.graph.exceptions.InvalidDepthOfSubVerticesException;
import org.triple_brain.module.model.graph.exceptions.NonExistingResourceException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static graph.mock.JenaGraphManipulatorMock.DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.*;

/**
 * Copyright Mozilla Public License 1.1
 */
public class JenaGraphManipulatorTest extends JenaGeneralGraphManipulatorTest {

    @Test
    public void can_get_graph_with_default_center_vertex() {
        SubGraph graph = graphManipulator.graphWithDefaultVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
        );
        assertThat(graph, is(not(nullValue())));
        assertThat(graph.numberOfVertices(), is(3));
        assertThat(graph.numberOfEdges(), is(2));
        assertTrue(graph.containsVertex(vertexA));
    }

    @Test
    public void can_get_graph_with_custom_center_vertex() {
        SubGraph graph = graphManipulator.graphWithDepthAndCenterVertexId(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES,
                vertexB.id());
        assertThat(graph, is(not(nullValue())));
        Vertex centerVertex = graph.vertexWithIdentifier(vertexB.id());
        assertThat(graph.numberOfEdgesAndVertices(), is(graphManipulator.numberOfEdgesAndVertices()));
        assertThat(centerVertex.label(), is("vertex B"));
    }

    @Test
    public void can_get_circular_graph_with_default_center_vertex() {
        vertexC.addRelationToVertex(vertexA);
        SubGraph graph = graphManipulator.graphWithDefaultVertexAndDepth(DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES);
        assertThat(graph, is(not(nullValue())));
        assertThat(graph.numberOfEdgesAndVertices(), is(graphManipulator.numberOfEdgesAndVertices()));
        Vertex centerVertex = graph.vertexWithIdentifier(vertexA.id());
        assertThat(centerVertex.label(), is("vertex A"));
    }

    @Test
    public void can_get_a_limited_graph_with_default_center_vertex() throws Exception {
        SubGraph subGraph = graphManipulator.graphWithDefaultVertexAndDepth(2);
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
        SubGraph subGraph = graphManipulator.graphWithDepthAndCenterVertexId(
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
        Edge newEdge = vertexC.addVertexAndRelation();
        SubGraph subGraph = graphManipulator.graphWithDepthAndCenterVertexId(
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
        SubGraph subGraph;
        Edge newEdge = vertexA.addVertexAndRelation();
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
        vertexC.addRelationToVertex(vertexA);
        Edge edgeGoingOutOfC = vertexC.addVertexAndRelation();

        SubGraph subGraph = graphManipulator.graphWithDepthAndCenterVertexId(
                2,
                vertexA.id()
        );
        assertTrue(subGraph.containsVertex(edgeGoingOutOfC.destinationVertex()));
    }

    @Test
    public void with_a_depth_of_sub_vertices_of_zero_only_central_vertex_is_returned() {
        SubGraph subGraph = graphManipulator.graphWithDefaultVertexAndDepth(0);
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
        Edge newEdge = vertexB.addVertexAndRelation();
        newEdge.label("new edge");
        SubGraph subGraph = graphManipulator.graphWithDepthAndCenterVertexId(
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
    public void connected_frontier_vertices_have_their_edge() {
        Edge newEdge = vertexC.addRelationToVertex(vertexA);
        newEdge.label("edge between frontier vertices");
        SubGraph subGraph = graphManipulator.graphWithDepthAndCenterVertexId(
                1,
                vertexB.id()
        );
        List<String> vertexCConnectedEdgesLabel = subGraph.vertexWithIdentifier(
                vertexC.id()
        ).hiddenConnectedEdgesLabel();
        assertThat(vertexCConnectedEdgesLabel.size(), is(0));
        Vertex vertexCFromSubGraph = subGraph.vertexWithIdentifier(
                vertexC.id()
        );
        assertThat(vertexCFromSubGraph.connectedEdges().size(), is(2));
    }

    @Test
    public void can_get_rdf_xml_representation_of_graph() {
        assertThat(graphManipulator.toRDFXML(), is(not(nullValue())));
    }

    @Test
    public void vertex_additional_type_label_is_in_sub_graph(){
        assertTrue(
                vertexA.getAdditionalTypes().isEmpty()
        );
        vertexA.addType(
                testScenarios.personType()
        );
        SubGraph subGraph = graphManipulator.wholeGraph();
        vertexA = subGraph.vertexWithIdentifier(vertexA.id());
        FriendlyResource additionalType = vertexA.getAdditionalTypes().iterator().next();
        assertThat(additionalType.label(), is("Person"));
    }

    @Test
    public void vertex_suggestions_have_their_properties_sub_graph(){
        Set<Suggestion> suggestions = new HashSet<>();
        suggestions.add(
                testScenarios.startDateSuggestion()
        );
        vertexA.suggestions(
            suggestions
        );
        SubGraph subGraph = graphManipulator.wholeGraph();
        vertexA = subGraph.vertexWithIdentifier(vertexA.id());
        Suggestion suggestion = vertexA.suggestions().iterator().next();
        assertThat(suggestion.label(), is("Start date"));
    }
}