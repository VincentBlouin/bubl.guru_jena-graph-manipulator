package graph.scenarios;

import graph.mock.JenaGraphManipulatorMock;
import org.triple_brain.graphmanipulator.jena.graph.JenaEdgeManipulator;
import org.triple_brain.graphmanipulator.jena.graph.JenaGraphManipulator;
import org.triple_brain.graphmanipulator.jena.graph.JenaVertexManipulator;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.Edge;
import org.triple_brain.module.model.graph.Vertex;

/*
* Copyright Mozilla Public License 1.1
*/
public class GraphScenariosGenerator {

    protected User user;
    protected JenaGraphManipulatorMock graphManipulator;
    protected JenaVertexManipulator vertexManipulator;
    protected JenaEdgeManipulator edgeManipulator;

    public static GraphScenariosGenerator withUserManipulators(User user, JenaGraphManipulatorMock graphManipulator, JenaVertexManipulator vertexManipulator, JenaEdgeManipulator edgeManipulator){
        return new GraphScenariosGenerator(
                user,
                graphManipulator,
                vertexManipulator,
                edgeManipulator
        );
    }

    protected GraphScenariosGenerator(User user, JenaGraphManipulatorMock graphManipulator, JenaVertexManipulator vertexManipulator, JenaEdgeManipulator edgeManipulator){
        this.user = user;
        this.graphManipulator = graphManipulator;
        this.vertexManipulator = vertexManipulator;
        this.edgeManipulator = edgeManipulator;
    }

    public VertexABAndC makeGraphHave3VerticesABCWhereAIsDefaultCenterVertexAndAPointsToBAndBPointsToC() throws Exception{
        graphManipulator.graph().removeAll();
        JenaGraphManipulator.createUserGraph(user);
        Vertex vertexA = vertexManipulator.defaultVertex();
        vertexA.label("vertex A");
        Vertex vertexB = vertexManipulator.addVertexAndRelation(vertexA.id()).destinationVertex();
        vertexB.label("vertex B");
        Vertex vertexC = vertexManipulator.addVertexAndRelation(vertexB.id()).destinationVertex();
        vertexC.label("vertex C");
        Edge betweenAAndB = vertexA.edgeThatLinksToDestinationVertex(vertexB);
        betweenAAndB.label("between vertex A and vertex B");
        Edge betweenBAndC = vertexB.edgeThatLinksToDestinationVertex(vertexC);
        betweenBAndC.label("between vertex B and vertex C");
        return new VertexABAndC(
                vertexA,
                vertexB,
                vertexC
        );
    }

}
