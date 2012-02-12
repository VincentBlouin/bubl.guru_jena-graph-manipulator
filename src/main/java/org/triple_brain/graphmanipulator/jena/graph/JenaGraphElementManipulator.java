package org.triple_brain.graphmanipulator.jena.graph;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.triple_brain.graphmanipulator.jena.User;
import org.triple_brain.graphmanipulator.jena.graph.exceptions.NonExistingResourceException;

/**
 * @author Vincent Blouin
 */
public class JenaGraphElementManipulator {

    private JenaGraphManipulator jenaGraphManipulator;

    public static JenaGraphElementManipulator jenaGraphElementManipulatorWithJenaGraphManipulator (JenaGraphManipulator jenaGraphManipulator){
        return new JenaGraphElementManipulator(jenaGraphManipulator);
    }

    private JenaGraphElementManipulator(JenaGraphManipulator jenaGraphManipulator){
        this.jenaGraphManipulator = jenaGraphManipulator;
    }

    public JenaGraphElementManipulator updateLabel(String graphElementLocalName, String label) throws NonExistingResourceException{

        Resource graphElement = defaultUser().model().getResource(defaultUser().URI() + graphElementLocalName);
        if (!graph().containsResource(graphElement)) {
            throw new NonExistingResourceException(defaultUser().URI() + graphElementLocalName);
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
