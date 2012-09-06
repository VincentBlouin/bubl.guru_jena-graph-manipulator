package org.triple_brain.graphmanipulator.jena;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Copyright Mozilla Public License 1.1
 */
public class TripleBrainModel {

    private Model model;
    public static final String SITE_URI = "http://www.triple_brain.org/";

    private static final String MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX_URI = SITE_URI + "min_number_of_edges_from_center_vertex";
    private Property MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX;

    private static final String LABEL_OF_HIDDEN_EDGES_URI = SITE_URI + "label_of_hidden_edges";
    private Property LABEL_OF_HIDDEN_EDGES;

    public static String TRIPLE_BRAIN_VERTEX_URI = SITE_URI + "vertex";
    private Resource TRIPLE_BRAIN_VERTEX;

    private static final String DESTINATION_VERTEX_URI = SITE_URI + "destination_vertex";
    private Property DESTINATION_VERTEX;

    private static final String HAS_OUTGOING_EDGE_URI = SITE_URI + "has_outgoing_edge";
    private Property HAS_OUTGOING_EDGE;

    private static String TRIPLE_BRAIN_EDGE_URI = SITE_URI + "edge";
    private Resource TRIPLE_BRAIN_EDGE;

    private static final String HAS_NEIGHBOR_URI = SITE_URI + "has_neighbor";
    private Property HAS_NEIGHBOR;

    private static final String HAS_SUGGESTION_URI = SITE_URI + "has_suggestion";
    private Property HAS_SUGGESTION;

    public static TripleBrainModel withEnglobingModel(Model model){
        return new TripleBrainModel(model);
    }

    protected TripleBrainModel(Model model) {
        this.model = model;
    }

    public void incorporate(){
        model.createProperty(MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX_URI);
        model.createProperty(LABEL_OF_HIDDEN_EDGES_URI);
        model.createResource(TRIPLE_BRAIN_VERTEX_URI);
        model.createProperty(HAS_OUTGOING_EDGE_URI);
        model.createResource(TRIPLE_BRAIN_EDGE_URI);
        model.createProperty(DESTINATION_VERTEX_URI);
        model.createProperty(HAS_NEIGHBOR_URI);
        model.createProperty(HAS_SUGGESTION_URI);
    }

    public Property MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX() {
        return model.getProperty(MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX_URI);
    }

    public Property LABEL_OF_HIDDEN_EDGES() {
        return model.getProperty(LABEL_OF_HIDDEN_EDGES_URI);
    }

    public Resource TRIPLE_BRAIN_VERTEX() {
        return model.getProperty(TRIPLE_BRAIN_VERTEX_URI);
    }

    public Property HAS_OUTGOING_EDGE() {
        return model.getProperty(HAS_OUTGOING_EDGE_URI);
    }

    public Resource TRIPLE_BRAIN_EDGE() {
        return model.getProperty(TRIPLE_BRAIN_EDGE_URI);
    }

    public Property DESTINATION_VERTEX() {
        return model.getProperty(DESTINATION_VERTEX_URI);
    }

    public Property HAS_NEIGHBOR() {
        return model.getProperty(HAS_NEIGHBOR_URI);
    }

    public Property HAS_SUGGESTION() {
        return model.getProperty(HAS_SUGGESTION_URI);
    }
}