package org.triple_brain.graphmanipulator.jena.graph.exceptions;


public class NonExistingResourceException extends JenaGraphManipulatorException {

    public NonExistingResourceException(String resourceId){
        super("Resource with URI :" + resourceId + " not found");
    }
}
