package org.triple_brain.graphmanipulator.jena.graph;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.triple_brain.graphmanipulator.jena.User;
import org.triple_brain.module.graph_manipulator.exceptions.NonExistingResourceException;

/**
 * Copyright Mozilla Public License 1.1
 */
public class JenaGraphElementManipulator {

    private JenaGraphManipulator jenaGraphManipulator;

    public static JenaGraphElementManipulator jenaGraphElementManipulatorWithJenaGraphManipulator (JenaGraphManipulator jenaGraphManipulator){
        return new JenaGraphElementManipulator(jenaGraphManipulator);
    }

    private JenaGraphElementManipulator(JenaGraphManipulator jenaGraphManipulator){
        this.jenaGraphManipulator = jenaGraphManipulator;
    }

    public JenaGraphElementManipulator updateLabel(String graphElementURI, String label) throws NonExistingResourceException{

        Resource graphElement = defaultUser().model().getResource(graphElementURI);
        if (!graph().containsResource(graphElement)) {
            throw new NonExistingResourceException(graphElementURI);
        }

        graphElement.getProperty(RDFS.label).changeObject(label);
        return this;
    }

    public Model graph(){
        return jenaGraphManipulator.graph();
    }

    public User defaultUser(){
        return jenaGraphManipulator.defaultUser();
    }
}
