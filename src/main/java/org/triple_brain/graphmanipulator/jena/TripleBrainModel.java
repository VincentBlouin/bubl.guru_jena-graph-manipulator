package org.triple_brain.graphmanipulator.jena;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Copyright Mozilla Public License 1.1
 */
public class TripleBrainModel {
    private static Model model;
    public static final String SITE_URI = "http://www.triple_brain.org/";
    public static final String EMPTY_EDGE_LABEL = "a property";
    public static final String EMPTY_VERTEX_LABEL = "a concept";

    private static final String MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX_URI = SITE_URI + "min_number_of_edges_from_center_vertex";
    private static Property MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX;

    private static final String LABEL_OF_HIDDEN_EDGES_URI = SITE_URI + "label_of_hidden_edges";
    private static Property LABEL_OF_HIDDEN_EDGES;

    private static String TRIPLE_BRAIN_VERTEX_URI = SITE_URI + "vertex";
    private static Resource TRIPLE_BRAIN_VERTEX;

    private static final String DESTINATION_VERTEX_URI = SITE_URI + "destination_vertex";
    private static Property DESTINATION_VERTEX;

    private static final String HAS_OUTGOING_EDGE_URI = SITE_URI + "has_outgoing_edge";
    private static Property HAS_OUTGOING_EDGE;

    private static String TRIPLE_BRAIN_EDGE_URI = SITE_URI + "edge";
    private static Resource TRIPLE_BRAIN_EDGE;

    private static final String HAS_NEIGHBOR_URI = SITE_URI + "has_neighbor";
    private static Property HAS_NEIGHBOR;

    private static final String HAS_SUGGESTION_URI = SITE_URI + "has_suggestion";
    private static Property HAS_SUGGESTION;


    private static Model tripleBrainModel() {
        if (model == null) {
            model = ModelFactory.createDefaultModel();
            MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX = model.createProperty(MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX_URI);
            LABEL_OF_HIDDEN_EDGES = model.createProperty(LABEL_OF_HIDDEN_EDGES_URI);
            TRIPLE_BRAIN_VERTEX = model.createResource(TRIPLE_BRAIN_VERTEX_URI);
            HAS_OUTGOING_EDGE = model.createProperty(HAS_OUTGOING_EDGE_URI);
            TRIPLE_BRAIN_EDGE = model.createResource(TRIPLE_BRAIN_EDGE_URI);
            DESTINATION_VERTEX = model.createProperty(DESTINATION_VERTEX_URI);
            HAS_NEIGHBOR = model.createProperty(HAS_NEIGHBOR_URI);
            HAS_SUGGESTION = model.createProperty(HAS_SUGGESTION_URI);
        }
        return model;
    }

    public static Property MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX() {
        if (MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX == null) {
            MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX = tripleBrainModel().getProperty(MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX_URI);
        }
        return MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX;
    }

    public static Property LABEL_OF_HIDDEN_EDGES() {
        if (LABEL_OF_HIDDEN_EDGES == null) {
            LABEL_OF_HIDDEN_EDGES = tripleBrainModel().getProperty(LABEL_OF_HIDDEN_EDGES_URI);
        }
        return LABEL_OF_HIDDEN_EDGES;
    }

    public static Resource TRIPLE_BRAIN_VERTEX() {
        if (TRIPLE_BRAIN_VERTEX == null) {
            TRIPLE_BRAIN_VERTEX = tripleBrainModel().getResource(TRIPLE_BRAIN_VERTEX_URI);
        }
        return TRIPLE_BRAIN_VERTEX;
    }

    public static Property HAS_OUTGOING_EDGE() {
        if (HAS_OUTGOING_EDGE == null) {
            HAS_OUTGOING_EDGE = tripleBrainModel().getProperty(HAS_OUTGOING_EDGE_URI);
        }
        return HAS_OUTGOING_EDGE;
    }

    public static Resource TRIPLE_BRAIN_EDGE() {
        if (TRIPLE_BRAIN_EDGE == null) {
            TRIPLE_BRAIN_EDGE = tripleBrainModel().getResource(TRIPLE_BRAIN_EDGE_URI);
        }
        return TRIPLE_BRAIN_EDGE;
    }

    public static Property DESTINATION_VERTEX() {
        if (DESTINATION_VERTEX == null) {
            DESTINATION_VERTEX = tripleBrainModel().getProperty(DESTINATION_VERTEX_URI);
        }
        return DESTINATION_VERTEX;
    }

    public static Property HAS_NEIGHBOR() {
        if (HAS_NEIGHBOR == null) {
            HAS_NEIGHBOR = tripleBrainModel().getProperty(HAS_NEIGHBOR_URI);
        }
        return HAS_NEIGHBOR;
    }

    public static Property HAS_SUGGESTION() {
        if (HAS_SUGGESTION == null) {
            HAS_SUGGESTION = tripleBrainModel().getProperty(HAS_SUGGESTION_URI);
        }
        return HAS_SUGGESTION;
    }
}
