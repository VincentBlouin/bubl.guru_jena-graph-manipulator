package org.triple_brain.graphmanipulator.jena.graph;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.Edge;
import org.triple_brain.module.model.graph.Graph;
import org.triple_brain.module.model.graph.Vertex;

import java.util.*;

/*
* Copyright Mozilla Public License 1.1
*/
public class JenaSubGraphExtractor2 {

    private int maximumDepth = 0;

    private Model wholeModel = ModelFactory.createDefaultModel();
    private Model subModel = ModelFactory.createDefaultModel();
    private Map<Vertex, Integer> minDistanceFromCenterVertexMap = new HashMap<Vertex, Integer>();
    private JenaVertex centerVertex ;
    private User user;
    private Graph subGraph = JenaGraph.withVerticesAndEdges(
            new HashSet<Vertex>(),
            new HashSet<Edge>()
            );;
    private int currentDepth = 0;

    public static JenaSubGraphExtractor2 withMaximumDepthWholeModelCenterVertexAndUser(
            int maximumDepth, Model wholeModel, JenaVertex centerVertex, User user){
        return new JenaSubGraphExtractor2(maximumDepth, wholeModel, centerVertex, user);
    }

    protected JenaSubGraphExtractor2(
            int maximumDepth, Model wholeModel, JenaVertex centerVertex, User user){
        this.maximumDepth = maximumDepth;
        this.wholeModel = wholeModel;
        this.centerVertex = centerVertex;
        this.user = user;
    }

    public Graph extract(){
        subGraphBuiltRecursively(centerVertex, currentDepth);
        addMinDistanceFromCenterVertex();
        addHiddenLabelsToFrontierVertices();
        return subGraph;
    }

    private void subGraphBuiltRecursively(JenaVertex currentVertex, int currentDepth) {
        subGraph.vertices().add(currentVertex.buildVertexInModelWithOwner(subModel, user));
        updateMinDepthFromCenterIfNecessary(currentVertex, currentDepth);
        if (currentDepth == maximumDepth) {
            return ;
        }
        for (Edge connectedEdge : currentVertex.connectedEdges()) {
            JenaEdge edge = (JenaEdge) connectedEdge;
            subGraph.edges().add(edge.buildEdgeInModelOfUser(subModel, user));
            JenaVertex sourceVertex = (JenaVertex) edge.sourceVertex();
            JenaVertex destinationVertex = (JenaVertex)  edge.destinationVertex();
            subGraph.vertices().add(sourceVertex.buildVertexInModelWithOwner(subModel, user));
            updateMinDepthFromCenterIfNecessary(sourceVertex, currentDepth + 1);
            if (isCurrentDepthSmallerThanVisitedDepth(sourceVertex, currentDepth + 1)) {
                subGraphBuiltRecursively(sourceVertex, currentDepth);
            }
            subGraph.vertices().add(destinationVertex.buildVertexInModelWithOwner(subModel, user));
            updateMinDepthFromCenterIfNecessary(destinationVertex, currentDepth + 1);
            if (isCurrentDepthSmallerThanVisitedDepth(destinationVertex, currentDepth + 1)) {
                subGraphBuiltRecursively(destinationVertex, currentDepth + 1);
            }
        }
    }

    private boolean isResourceVisited(Vertex vertex) {
        return minDistanceFromCenterVertexMap.containsKey(vertex);
    }

    private boolean isCurrentDepthSmallerThanVisitedDepth(Vertex vertex, Integer currentDepth) {
        if (!isResourceVisited(vertex)) {
            return true;
        } else {
            if (depthFromCenterVertex(vertex) > currentDepth) {
                return true;
            }
        }
        return false;

    }

    private int depthFromCenterVertex(Vertex vertex) {
        return minDistanceFromCenterVertexMap.get(vertex);
    }

    private void updateMinDepthFromCenterIfNecessary(Vertex vertex, Integer currentDepth) {
        if (
            !minDistanceFromCenterVertexMap.containsKey(vertex)
                    ||
            minDistanceFromCenterVertexMap.get(vertex) > currentDepth
            )
        {
            minDistanceFromCenterVertexMap.put(vertex, currentDepth);
        }
    }

    private void addHiddenLabelsToFrontierVertices() {
        for(Vertex vertex : subGraph.vertices()){
            if(vertex.minNumberOfEdgesFromCenterVertex() == maximumDepth){
                vertex.hiddenConnectedEdgesLabel(
                        hiddenEdgesLabelOfVertex(vertex)
                );
            }
        }
    }
    private void addMinDistanceFromCenterVertex(){
        for(Vertex vertex : subGraph.vertices()){
            vertex.minNumberOfEdgesFromCenterVertex(
                    minDistanceFromCenterVertexMap.get(vertex)
            );
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
}
