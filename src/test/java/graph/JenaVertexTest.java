package graph;

import graph.scenarios.TestScenarios;
import org.junit.Test;
import org.triple_brain.module.model.FriendlyResource;
import org.triple_brain.module.model.Suggestion;
import org.triple_brain.module.model.graph.Edge;
import org.triple_brain.module.model.graph.Vertex;

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
    public void on_vertex_delete_its_additional_type_label_is_removed_from_the_model(){
        FriendlyResource personType = TestScenarios.personType();
        vertexA.addType(
                personType
        );
        assertTrue(
                modelContainsLabel(
                        personType.label()
                )
        );
        vertexA.remove();
        assertFalse(
                modelContainsLabel(
                        personType.label()
                )
        );
    }


    @Test
    public void on_vertex_remove_suggestions_properties_are_also_delete_from_the_model(){
        Set<Suggestion> suggestions = new HashSet<Suggestion>();
        Suggestion startDateSuggestion = TestScenarios.startDateSuggestion();
        suggestions.add(
                startDateSuggestion
        );
        vertexA.suggestions(
                suggestions
        );
        assertTrue(
                modelContainsLabel(
                        startDateSuggestion.label()
                )
        );
        assertTrue(
                model().containsResource(
                        model().getResource(
                                startDateSuggestion.typeUri().toString()
                        )
                )
        );
        assertTrue(
                model().containsResource(
                        model().getResource(
                                startDateSuggestion.domainUri().toString()
                        )
                )
        );
        vertexA.remove();
        assertFalse(
                modelContainsLabel(
                        startDateSuggestion.label()
                )
        );
        assertFalse(
                model().containsResource(
                        model().getResource(
                                startDateSuggestion.typeUri().toString()
                        )
                )
        );
        assertFalse(
                model().containsResource(
                        model().getResource(
                                startDateSuggestion.domainUri().toString()
                        )
                )
        );
    }

    @Test
    public void can_update_label() {
        Edge newEdge = vertexA.addVertexAndRelation();
        Vertex vertex = newEdge.destinationVertex();
        vertex.label("Ju-Ji-Tsu");
        assertThat(vertex.label(), is("Ju-Ji-Tsu"));
    }

    @Test
    public void can_add_an_additional_type_to_vertex() throws Exception{
        assertTrue(
                vertexA.getAdditionalTypes().isEmpty()
        );
        vertexA.addType(
                TestScenarios.personType()
        );
        assertFalse(
                vertexA.getAdditionalTypes().isEmpty()
        );
    }

    @Test
    public void can_add_multiple_additional_types_to_a_vertex() throws Exception{
        assertTrue(
                vertexA.getAdditionalTypes().isEmpty()
        );
        vertexA.addType(
                TestScenarios.personType()
        );
        vertexA.addType(
                TestScenarios.computerScientistType()
        );
        assertThat(
                vertexA.getAdditionalTypes().size(),
                is(2)
        );
    }

    @Test
    public void can_remove_an_additional_type_to_vertex() throws Exception{
        vertexA.addType(
                TestScenarios.personType()
        );
        FriendlyResource computerScientistType = TestScenarios.computerScientistType();
        vertexA.addType(
                computerScientistType
        );
        assertThat(
                vertexA.getAdditionalTypes().size(),
                is(2)
        );
        vertexA.removeFriendlyResource(TestScenarios.personType());
        assertThat(
                vertexA.getAdditionalTypes().size(),
                is(1)
        );
        FriendlyResource remainingType = vertexA.getAdditionalTypes().iterator().next();
        assertThat(
                remainingType.label(),
                is(computerScientistType.label())
        );
    }

    @Test
    public void can_set_suggestions_of_vertex()throws Exception{
        assertTrue(vertexA.suggestions().isEmpty());
        Set<Suggestion> suggestions = new HashSet<Suggestion>();
        suggestions.add(
            TestScenarios.startDateSuggestion()
        );
        vertexA.suggestions(suggestions);
        assertFalse(vertexA.suggestions().isEmpty());
        Suggestion getSuggestion = vertexA.suggestions().iterator().next();
        assertThat(getSuggestion.label(), is("Start date"));
    }
}

