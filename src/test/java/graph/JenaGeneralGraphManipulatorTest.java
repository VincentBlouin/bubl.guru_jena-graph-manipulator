package graph;

import com.google.inject.Guice;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.vocabulary.RDFS;
import graph.mock.JenaGraphManipulatorMock;
import graph.scenarios.TestScenarios;
import graph.scenarios.VerticesCalledABAndC;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.triple_brain.graphmanipulator.jena.graph.JenaGraphElementManipulator;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.Vertex;

import java.util.List;

import static org.triple_brain.graphmanipulator.jena.JenaConnection.closeConnection;

/*
* Copyright Mozilla Public License 1.1
*/
public class JenaGeneralGraphManipulatorTest {

    protected static JenaGraphManipulatorMock graphManipulator;
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
        graphElementManipulator = JenaGraphElementManipulator.withUser(user);
    }

    protected void makeGraphHave3VerticesABCWhereAIsDefaultCenterVertexAndAPointsToBAndBPointsToC() throws Exception {
        TestScenarios graphScenariosGenerator = TestScenarios.withUserManipulators(
                user,
                graphManipulator
        );
        VerticesCalledABAndC vertexABAndC = graphScenariosGenerator.makeGraphHave3VerticesABCWhereAIsDefaultCenterVertexAndAPointsToBAndBPointsToC();
        vertexA = vertexABAndC.vertexA();
        vertexB = vertexABAndC.vertexB();
        vertexC = vertexABAndC.vertexC();
    }

    protected boolean modelContainsLabel(String label){
        List<RDFNode> allLabelsInModel = graphManipulator.model().listObjectsOfProperty(RDFS.label).toList();
        for(RDFNode rdfNode : allLabelsInModel){
            if(rdfNode.asLiteral().getString().equals(label)){
                return true;
            }
        }
        return false;
    }
}
