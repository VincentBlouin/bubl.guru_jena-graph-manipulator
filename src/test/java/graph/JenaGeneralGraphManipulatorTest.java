package graph;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.vocabulary.RDFS;
import graph.mock.JenaUserGraphMock;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.triple_brain.graphmanipulator.jena.graph.JenaGraphMaker;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.Vertex;
import org.triple_brain.module.model.graph.scenarios.TestScenarios;
import org.triple_brain.module.model.graph.scenarios.VerticesCalledABAndC;

import javax.inject.Inject;
import java.util.List;

import static org.triple_brain.graphmanipulator.jena.JenaConnection.closeConnection;

/*
* Copyright Mozilla Public License 1.1
*/
@Ignore
public class JenaGeneralGraphManipulatorTest {

    @Inject
    protected TestScenarios testScenarios;

    protected static JenaUserGraphMock userGraph;
    protected Vertex vertexA;
    protected Vertex vertexB;
    protected Vertex vertexC;

    protected static User user;

    private JenaGraphMaker jenaGraphMaker = new JenaGraphMaker();

    private static Injector injector;

    @BeforeClass
    public static void beforeClass() throws Exception {
        injector = Guice.createInjector(new JenaTestModule());
        user = User.withUsernameAndEmail(
                "roger_lamothe",
                "roger.lamothe@example.org"
        );
        resetManipulators();
    }

    @Before
    public void setUp() throws Exception {
        injector.injectMembers(this);
        makeGraphHave3VerticesABCWhereAIsDefaultCenterVertexAndAPointsToBAndBPointsToC();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        closeConnection();
    }

    protected static void resetManipulators() throws Exception {
        userGraph = JenaUserGraphMock.mockWithUser(user);
    }

    protected void makeGraphHave3VerticesABCWhereAIsDefaultCenterVertexAndAPointsToBAndBPointsToC() throws Exception {
        VerticesCalledABAndC vertexABAndC = testScenarios.makeGraphHave3VerticesABCWhereAIsDefaultCenterVertexAndAPointsToBAndBPointsToC(
                jenaGraphMaker.createForUser(user)
        );
        vertexA = vertexABAndC.vertexA();
        vertexB = vertexABAndC.vertexB();
        vertexC = vertexABAndC.vertexC();
    }

    protected boolean modelContainsLabel(String label){
        List<RDFNode> allLabelsInModel = userGraph.model().listObjectsOfProperty(RDFS.label).toList();
        for(RDFNode rdfNode : allLabelsInModel){
            if(rdfNode.asLiteral().getString().equals(label)){
                return true;
            }
        }
        return false;
    }

    protected Model model(){
        return userGraph.model();
    }
}
