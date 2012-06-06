package graph;

import com.google.inject.Guice;
import graph.mock.JenaGraphManipulatorMock;
import graph.scenarios.GraphScenariosGenerator;
import graph.scenarios.VertexABAndC;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.triple_brain.graphmanipulator.jena.graph.JenaEdgeManipulator;
import org.triple_brain.graphmanipulator.jena.graph.JenaGraphElementManipulator;
import org.triple_brain.graphmanipulator.jena.graph.JenaVertexManipulator;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.Vertex;

import static org.triple_brain.graphmanipulator.jena.JenaConnection.closeConnection;

/*
* Copyright Mozilla Public License 1.1
*/
public class JenaGeneralGraphManipulatorTest {

    protected static JenaGraphManipulatorMock graphManipulator;
    protected static JenaVertexManipulator vertexManipulator;
    protected static JenaEdgeManipulator edgeManipulator;
    protected static JenaGraphElementManipulator graphElementManipulator;
    protected Vertex vertexA;
    protected Vertex vertexB;
    protected Vertex vertexC;

    protected static User user;

    @BeforeClass
    public static void beforeClass() throws Exception {
        Guice.createInjector(new JenaSQLTestModule());
        user = User.withUsernameAndEmail(
                "roger_lamothe",
                "roger.lamothe@example.org"
        );
        resetManipulators();
    }

    @Before
    public void setUp() throws Exception {
        makeGraphHave3VerticesABCWhereAIsDefaultCenterVertexAndAPointsToBAndBPointsToC();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        closeConnection();
    }

    protected static void resetManipulators() throws Exception {
        graphManipulator = JenaGraphManipulatorMock.mockWithUser(user);
        vertexManipulator = JenaVertexManipulator.withUser(user);
        edgeManipulator = JenaEdgeManipulator.withUser(user);
        graphElementManipulator = JenaGraphElementManipulator.withUser(user);
    }

    protected void makeGraphHave3VerticesABCWhereAIsDefaultCenterVertexAndAPointsToBAndBPointsToC() throws Exception {
        GraphScenariosGenerator graphScenariosGenerator = GraphScenariosGenerator.withUserManipulators(
                user,
                graphManipulator,
                vertexManipulator,
                edgeManipulator
        );
        VertexABAndC vertexABAndC = graphScenariosGenerator.makeGraphHave3VerticesABCWhereAIsDefaultCenterVertexAndAPointsToBAndBPointsToC();
        vertexA = vertexABAndC.vertexA();
        vertexB = vertexABAndC.vertexB();
        vertexC = vertexABAndC.vertexC();
    }
}
