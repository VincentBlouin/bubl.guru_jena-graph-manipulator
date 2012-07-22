package graph.scenarios;

import graph.mock.JenaGraphManipulatorMock;
import org.triple_brain.graphmanipulator.jena.graph.JenaGraphManipulator;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.Edge;
import org.triple_brain.module.model.graph.Vertex;

import java.util.UUID;

/*
* Copyright Mozilla Public License 1.1
*/
public class TestScenarios {

    protected User user;
    protected JenaGraphManipulatorMock graphManipulator;

    public static TestScenarios withUserManipulators(User user, JenaGraphManipulatorMock graphManipulator){
        return new TestScenarios(
                user,
                graphManipulator
        );
    }

    protected TestScenarios(User user, JenaGraphManipulatorMock graphManipulator){
        this.user = user;
        this.graphManipulator = graphManipulator;
    }

    public VerticesCalledABAndC makeGraphHave3VerticesABCWhereAIsDefaultCenterVertexAndAPointsToBAndBPointsToC() throws Exception{
        graphManipulator.graph().removeAll();
        JenaGraphManipulator.createUserGraph(user);
        Vertex vertexA = graphManipulator.defaultVertex();
        vertexA.label("vertex A");
        Vertex vertexB = vertexA.addVertexAndRelation().destinationVertex();
        vertexB.label("vertex B");
        Vertex vertexC = vertexB.addVertexAndRelation().destinationVertex();
        vertexC.label("vertex C");
        Edge betweenAAndB = vertexA.edgeThatLinksToDestinationVertex(vertexB);
        betweenAAndB.label("between vertex A and vertex B");
        Edge betweenBAndC = vertexB.edgeThatLinksToDestinationVertex(vertexC);
        betweenBAndC.label("between vertex B and vertex C");
        return new VerticesCalledABAndC(
                vertexA,
                vertexB,
                vertexC
        );
    }

    public VerticesCalledABAndC makeGraphHave3SerialVerticesWithLongLabels()throws Exception{
        VerticesCalledABAndC verticesCalledABAndC = makeGraphHave3VerticesABCWhereAIsDefaultCenterVertexAndAPointsToBAndBPointsToC();
        verticesCalledABAndC.vertexA().label("vertex Azure");
        verticesCalledABAndC.vertexB().label("vertex Bareau");
        verticesCalledABAndC.vertexC().label("vertex Cadeau");
        return verticesCalledABAndC;
    }

    public Vertex addPineAppleVertexToVertex(Vertex vertex){
        Edge newEdge = vertex.addVertexAndRelation();
        Vertex pineApple = newEdge.destinationVertex();
        pineApple.label("pine Apple");
        return pineApple;
    }

    public User randomUser(){
        return User.withUsernameAndEmail(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString() + "@example.org"
        );
    }

}
