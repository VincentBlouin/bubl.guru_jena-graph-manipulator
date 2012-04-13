package graph.mock;

import org.triple_brain.graphmanipulator.jena.graph.JenaGraphManipulator;
import org.triple_brain.graphmanipulator.jena.graph.JenaVertex;
import org.triple_brain.module.model.graph.Graph;

import static com.hp.hpl.jena.vocabulary.RDF.type;
import static org.triple_brain.graphmanipulator.jena.TripleBrainModel.TRIPLE_BRAIN_EDGE;
import static org.triple_brain.graphmanipulator.jena.TripleBrainModel.TRIPLE_BRAIN_VERTEX;

/**
 * Copyright Mozilla Public License 1.1
 */
public class JenaGraphManipulatorMock extends JenaGraphManipulator {

    public static final Integer DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES = 10;

    public static JenaGraphManipulatorMock jenaGraphManipulatorWithDefaultUser() {
        return new JenaGraphManipulatorMock();
    }

    private JenaGraphManipulatorMock(){
        super();
    }

    public int numberOfEdgesAndVertices(){
        return graph().listSubjectsWithProperty(
                type, TRIPLE_BRAIN_VERTEX()).toList()
                .size() +
                graph().listSubjectsWithProperty(
                        type, TRIPLE_BRAIN_EDGE()).toList()
                        .size();
    }

    public JenaVertex defaultCenterVertex(){
        return JenaVertex.withResource(defaultUser().absoluteCentralVertex());
    }

    public JenaVertex userNameVertex(){
        return JenaVertex.withResource(defaultUser().usernameResource());
    }


    public Graph wholeGraph(){
        return graphWithDefaultVertexAndDepth(DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES);
    }
}
