package org.triple_brain.graphmanipulator.jena.graph;

/*
Copyright Mozilla Public License 1.1
*/

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.Edge;
import org.triple_brain.module.model.graph.Graph;
import org.triple_brain.module.model.graph.Vertex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.triple_brain.graphmanipulator.jena.QueryUtils.*;

public class JenaSubGraphExtractor {

    private int maximumDepth = 0;

    private Model wholeModel = ModelFactory.createDefaultModel();
    private Model subModel = ModelFactory.createDefaultModel();
    private JenaVertex centerVertex ;
    private Graph subGraph;
    private User user;
    private int currentDepth = 0;

    public static JenaSubGraphExtractor withMaximumDepthWholeModelCenterVertexAndUser(
            int maximumDepth, Model wholeModel, JenaVertex centerVertex, User user){
        return new JenaSubGraphExtractor(maximumDepth, wholeModel, centerVertex, user);
    }

    protected JenaSubGraphExtractor(
            int maximumDepth, Model wholeModel, JenaVertex centerVertex, User user){
        this.maximumDepth = maximumDepth;
        this.wholeModel = wholeModel;
        this.centerVertex = centerVertex;
        this.user = user;
    }

    public Graph extract(){
        subGraph = JenaGraph.withVerticesAndEdges(
                new HashSet<Vertex>(),
                new HashSet<Edge>()
        );
        for(currentDepth = 0 ; currentDepth <= maximumDepth; currentDepth++){
            String query = queryToGetVerticesAtDepth(
                    currentDepth
            );
            Set<JenaVertex> verticesInQuery = verticesFromQueryInModel(query);
            if(verticesInQuery.isEmpty()){
                return subGraph;
            }
            verticesInQuery.removeAll(subGraph.vertices());
            addVerticesAndTheirEdges(
                    verticesInQuery
            );
        }
        return subGraph;
    }

    private String queryToGetVerticesAtDepth(int depth) {
        return TRIPLE_BRAIN_PREFIX + RDF_PREFIX +
                "SELECT DISTINCT ?vertices " +
                "WHERE { " +
                URIForQuery(centerVertex.id()) +
                " tb:has_neighbor{"+depth+"} " +
                "?vertices . " +
                "} ";
    }

    private Set<JenaVertex> verticesFromQueryInModel(String query) {
        Set<JenaVertex> verticesInQuery = new HashSet<JenaVertex>();
        QueryExecution qe = QueryExecutionFactory.create(query, wholeModel);
        ResultSet rs = qe.execSelect();
        while(rs.hasNext()){
            QuerySolution querySolution = rs.next();
            verticesInQuery.add(JenaVertex.loadUsingResourceOfOwner(
                    querySolution.getResource("vertices"),
                    user
            ));
        }
        return verticesInQuery;
    }

    private void addVerticesAndTheirEdges(Set<JenaVertex> vertices) {
        for(JenaVertex vertex : vertices){
            if(currentDepth == maximumDepth){
                List<String> hiddenEdgesLabelOfVertex = hiddenEdgesLabelOfVertex(vertex);
                vertex = vertex.buildVertexInModelWithOwner(subModel, user);
                vertex.hiddenConnectedEdgesLabel(hiddenEdgesLabelOfVertex);
            }else{
                addEdgesOfVertex(vertex);
                vertex = vertex.buildVertexInModelWithOwner(subModel, user);
            }
            vertex.minNumberOfEdgesFromCenterVertex(currentDepth);
            subGraph.vertices().add(vertex);
        }
    }


    private List<String> hiddenEdgesLabelOfVertex(Vertex vertex){
        List<String> hiddenEdgesLabel = new ArrayList<String>();
        Set<Edge> connectedEdges = vertex.connectedEdges();
        connectedEdges.removeAll(subGraph.edges());
        for(Edge edge : connectedEdges){
            hiddenEdgesLabel.add(edge.label());
        }
        return hiddenEdgesLabel;
    }

    private void addEdgesOfVertex(JenaVertex vertex){
        for(Edge edge : vertex.connectedEdges()){
            subGraph.edges().add(((JenaEdge) edge).buildEdgeInModelOfUser(
                    subModel,
                    user
            ));
        }
    }
}
