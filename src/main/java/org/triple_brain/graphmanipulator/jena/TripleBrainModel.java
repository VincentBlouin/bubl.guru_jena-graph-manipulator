package org.triple_brain.graphmanipulator.jena;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import static org.triple_brain.module.model.TripleBrainUris.*;
/**
 * Copyright Mozilla Public License 1.1
 */
public class TripleBrainModel {

    private Model model;

    public static TripleBrainModel withEnglobingModel(Model model){
        return new TripleBrainModel(model);
    }

    protected TripleBrainModel(Model model) {
        this.model = model;
    }

    public void incorporate(){
        model.createProperty(MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX);
        model.createProperty(LABEL_OF_HIDDEN_EDGES);
        model.createResource(TRIPLE_BRAIN_VERTEX);
        model.createProperty(HAS_OUTGOING_EDGE);
        model.createResource(TRIPLE_BRAIN_EDGE);
        model.createProperty(DESTINATION_VERTEX);
        model.createProperty(HAS_NEIGHBOR);
        model.createProperty(HAS_SUGGESTION);
    }

    public Property MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX() {
        return model.getProperty(MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX);
    }

    public Property LABEL_OF_HIDDEN_EDGES() {
        return model.getProperty(LABEL_OF_HIDDEN_EDGES);
    }

    public Resource TRIPLE_BRAIN_VERTEX() {
        return model.getProperty(TRIPLE_BRAIN_VERTEX);
    }

    public Property HAS_OUTGOING_EDGE() {
        return model.getProperty(HAS_OUTGOING_EDGE);
    }

    public Resource TRIPLE_BRAIN_EDGE() {
        return model.getProperty(TRIPLE_BRAIN_EDGE);
    }

    public Property DESTINATION_VERTEX() {
        return model.getProperty(DESTINATION_VERTEX);
    }

    public Property HAS_NEIGHBOR() {
        return model.getProperty(HAS_NEIGHBOR);
    }

    public Property HAS_SUGGESTION() {
        return model.getProperty(HAS_SUGGESTION);
    }
}