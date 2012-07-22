package graph;

import org.junit.Test;
import org.triple_brain.module.model.Suggestion;
import org.triple_brain.module.model.graph.Edge;
import org.triple_brain.module.model.graph.Vertex;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;


/**
 * Copyright Mozilla Public License 1.1
 */
public class JenaVertexTest extends JenaGeneralGraphManipulatorTest{

    @Test
    public void can_add_vertex_and_relation() {
        Integer numberOfEdgesAndVertices = graphManipulator.numberOfEdgesAndVertices();
        Edge edge = vertexA.addVertexAndRelation();

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
    public void can_remove_a_vertex() {
        Integer numberOfEdgesAndVertices = graphManipulator.numberOfEdgesAndVertices();

        assertTrue(graphManipulator.containsElement(vertexB));
        vertexB.remove();
        assertFalse(graphManipulator.containsElement(vertexB));

        Integer updatedNumberOfEdgesAndVertices = graphManipulator.numberOfEdgesAndVertices();
        assertThat(updatedNumberOfEdgesAndVertices, is(numberOfEdgesAndVertices - 3));
    }

    @Test
    public void can_update_label() {
        Edge newEdge = vertexA.addVertexAndRelation();
        Vertex vertex = newEdge.destinationVertex();
        vertex.label("Ju-Ji-Tsu");
        assertThat(vertex.label(), is("Ju-Ji-Tsu"));
    }

    @Test
    public void can_set_type_of_vertex(){
        String personClassURI = "http://xmlns.com/foaf/0.1/Person";
        assertFalse(vertexA.types().contains(personClassURI));
        vertexA.addSemanticType(personClassURI);
        assertTrue(vertexA.types().contains(personClassURI));
    }

    @Test
    public void can_set_suggestions_of_vertex()throws Exception{
        assertTrue(vertexA.suggestions().isEmpty());
        Set<Suggestion> suggestions = new HashSet<Suggestion>();
        suggestions.add(
                Suggestion.withTypeDomainAndLabel(
                new URI("http://rdf.freebase.com/rdf/time/event/start_date"),
                new URI("http://rdf.freebase.com/rdf/type/datetime"),
                "Start date"
        ));
        vertexA.suggestions(suggestions);
        assertFalse(vertexA.suggestions().isEmpty());
        Suggestion getSuggestion = vertexA.suggestions().iterator().next();
        assertThat(getSuggestion.label(), is("Start date"));
    }
}

