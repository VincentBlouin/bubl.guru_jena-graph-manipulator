package org.triple_brain.graphmanipulator.jena.graph.exceptions;

/**
 * @author Vincent Blouin
 */
public class InvalidDepthOfSubVerticesException extends JenaGraphManipulatorException {

    public InvalidDepthOfSubVerticesException(Integer depthOfSubvertices, String centerVertexId){
        super("Invalid depth of sub vertices. Depth was:" + depthOfSubvertices + " and center vertex id was:" + centerVertexId);
    }
}
