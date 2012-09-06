package graph.scenarios;

import org.triple_brain.module.model.FriendlyResource;
import org.triple_brain.module.model.Suggestion;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.Edge;
import org.triple_brain.module.model.graph.GraphMaker;
import org.triple_brain.module.model.graph.UserGraph;
import org.triple_brain.module.model.graph.Vertex;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/*
* Copyright Mozilla Public License 1.1
*/
public class TestScenarios {

    @Inject
    protected GraphMaker graphMaker;

    public FriendlyResource personType(){
        try{
            return FriendlyResource.withUriAndLabel(
                    new URI("http://xmlns.com/foaf/0.1/Person"),
                    "Person"
            );
        }catch(URISyntaxException e){
            throw new RuntimeException(e);
        }
    }

    public FriendlyResource computerScientistType(){
        try{
            return FriendlyResource.withUriAndLabel(
                    new URI("http://rdf.freebase.com/rdf/computer.computer_scientist"),
                    "Computer Scientist"
            );
        }catch(URISyntaxException e){
            throw new RuntimeException(e);
        }
    }

    public FriendlyResource timBernersLee(){
        try{
            return FriendlyResource.withUriAndLabel(
                    new URI("http://www.w3.org/People/Berners-Lee/card#i"),
                    "Tim Berners-Lee"
            );
        }catch(URISyntaxException e){
            throw new RuntimeException(e);
        }
    }

    public FriendlyResource timBernersLeeInFreebase(){
        try{
            return FriendlyResource.withUriAndLabel(
                    new URI("http://rdf.freebase.com/rdf/en.tim_berners-lee"),
                    "Tim Berners-Lee"
            );
        }catch(URISyntaxException e){
            throw new RuntimeException(e);
        }
    }

    public Suggestion startDateSuggestion(){
        try{
            return Suggestion.withTypeDomainAndLabel(
                    new URI("http://rdf.freebase.com/rdf/time/event/start_date"),
                    new URI("http://rdf.freebase.com/rdf/type/datetime"),
                    "Start date"
            );
        }catch(URISyntaxException e){
            throw new RuntimeException(e);
        }
    }

    public VerticesCalledABAndC makeGraphHave3VerticesABCWhereAIsDefaultCenterVertexAndAPointsToBAndBPointsToC(UserGraph userGraph) throws Exception{
        userGraph.remove();
        graphMaker.createForUser(userGraph.user());
        Vertex vertexA = userGraph.defaultVertex();
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

    public VerticesCalledABAndC makeGraphHave3SerialVerticesWithLongLabels(UserGraph userGraph)throws Exception{
        VerticesCalledABAndC verticesCalledABAndC = makeGraphHave3VerticesABCWhereAIsDefaultCenterVertexAndAPointsToBAndBPointsToC(userGraph);
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
