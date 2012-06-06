package org.triple_brain.graphmanipulator.jena.graph;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.triple_brain.module.graph_manipulator.exceptions.NonExistingResourceException;
import org.triple_brain.module.model.User;

import static org.triple_brain.graphmanipulator.jena.JenaConnection.modelMaker;
import static org.triple_brain.graphmanipulator.jena.TripleBrainModel.*;

/**
 * Copyright Mozilla Public License 1.1
 */
public class JenaGraphElementManipulator {

    private Model userModel;

    public static JenaGraphElementManipulator withUser(User user){
        Model userModel = modelMaker().openModel(user.mindMapURIFromSiteURI(SITE_URI));
        return new JenaGraphElementManipulator(userModel);
    }

    public static JenaGraphElementManipulator withUserModel(Model userModel){
        return new JenaGraphElementManipulator(userModel);
    }

    private JenaGraphElementManipulator(Model userModel){
        this.userModel = userModel;
    }

    public JenaGraphElementManipulator updateLabel(String graphElementURI, String label) throws NonExistingResourceException{

        Resource graphElement = userModel.getResource(graphElementURI);
        if (!graph().containsResource(graphElement)) {
            throw new NonExistingResourceException(graphElementURI);
        }

        graphElement.getProperty(RDFS.label).changeObject(label);
        return this;
    }

    public Model graph(){
        return userModel;
    }
}
