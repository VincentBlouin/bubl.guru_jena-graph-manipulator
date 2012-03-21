package graph.mock;

import org.triple_brain.graphmanipulator.jena.graph.JenaGraphManipulator;

/**
 * @author Vincent Blouin
 */
public class JenaGraphManipulatorMock extends JenaGraphManipulator {

    public static JenaGraphManipulatorMock jenaGraphManipulatorWithDefaultUser() {
        return new JenaGraphManipulatorMock();
    }

    private JenaGraphManipulatorMock(){
        super();
    }

    public int numberOfEdgesAndVertices(){
        return graph().listSubjects().toList().size();
    }
}
