package graph.mock;

import org.triple_brain.graphmanipulator.jena.graph.JenaGraphManipulator;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.Graph;

import static com.hp.hpl.jena.vocabulary.RDF.type;
import static org.triple_brain.graphmanipulator.jena.TripleBrainModel.TRIPLE_BRAIN_EDGE;
import static org.triple_brain.graphmanipulator.jena.TripleBrainModel.TRIPLE_BRAIN_VERTEX;

/**
 * Copyright Mozilla Public License 1.1
 */
public class JenaGraphManipulatorMock extends JenaGraphManipulator {

    public static final Integer DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES = 10;

    public static JenaGraphManipulatorMock mockWithUser(User user) throws Exception{
        return new JenaGraphManipulatorMock(user);
    }

    private JenaGraphManipulatorMock(User user) throws Exception{
        super(user);

    }

    public int numberOfEdgesAndVertices(){
        return model().listSubjectsWithProperty(
                type, TRIPLE_BRAIN_VERTEX()).toList()
                .size() +
                model().listSubjectsWithProperty(
                        type, TRIPLE_BRAIN_EDGE()).toList()
                        .size();
    }

    public Graph wholeGraph(){
        return graphWithDefaultVertexAndDepth(DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES);
    }
}
