package org.triple_brain.graphmanipulator.jena.graph;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
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
    private Graph subGraph = JenaGraph.withVerticesAndEdges(
            new HashSet<Vertex>(),
            new HashSet<Edge>()
            );;
    private int currentDepth = 0;

    public static JenaSubGraphExtractor2 withMaximumDepthWholeModelAndCenterVertex(
            int maximumDepth, Model wholeModel, JenaVertex centerVertex){
        return new JenaSubGraphExtractor2(maximumDepth, wholeModel, centerVertex);
    }

    protected JenaSubGraphExtractor2(
            int maximumDepth, Model wholeModel, JenaVertex centerVertex){
        this.maximumDepth = maximumDepth;
        this.wholeModel = wholeModel;
        this.centerVertex = centerVertex;
    }

    public Graph extract(){
        subGraphBuiltRecursively(centerVertex, currentDepth);
        addMinDistanceFromCenterVertex();
        addHiddenLabelsToFrontierVertices();
        return subGraph;
    }

    private void subGraphBuiltRecursively(JenaVertex currentVertex, int currentDepth) {
        subGraph.vertices().add(currentVertex.buildVertexInModel(subModel));
        updateMinDepthFromCenterIfNecessary(currentVertex, currentDepth);
        if (currentDepth == maximumDepth) {
            return ;
        }
        for (Edge connectedEdge : currentVertex.connectedEdges()) {
            JenaEdge edge = (JenaEdge) connectedEdge;
            subGraph.edges().add(edge.buildEdgeInModel(subModel));
            JenaVertex sourceVertex = (JenaVertex) edge.sourceVertex();
            JenaVertex destinationVertex = (JenaVertex)  edge.destinationVertex();
            subGraph.vertices().add(sourceVertex.buildVertexInModel(subModel));
            updateMinDepthFromCenterIfNecessary(sourceVertex, currentDepth + 1);
            if (isCurrentDepthSmallerThanVisitedDepth(sourceVertex, currentDepth + 1)) {
                subGraphBuiltRecursively(sourceVertex, currentDepth);
            }
            subGraph.vertices().add(destinationVertex.buildVertexInModel(subModel));
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
