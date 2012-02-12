package org.triple_brain.graphmanipulator.jena;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;

/**
 * @author Vincent Blouin
 */
public class TripleBrainModel {
    public static final String SITE_URI = "http://triple_brain.org/";
    public static final String EMPTY_EDGE_LABEL = "a property";
    public static final String EMPTY_VERTEX_LABEL = "a concept";
    public static final String IS_FRONTIER_VERTEX_WITH_HIDDEN_VERTICES_URI = SITE_URI + "is_frontier_vertex_with_hidden_vertices";
    private static Model model;
    private static Property IS_FRONTIER_VERTEX_WITH_HIDDEN_VERTICES;
    private static final String MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX_URI = SITE_URI + "min_number_of_edges_from_center_vertex";
    private static Property MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX;
    private static final String NUMBER_OF_HIDDEN_CONNECTED_VERTICES_URI = SITE_URI + "number_of_hidden_connected_vertices";
    private static Property NUMBER_OF_HIDDEN_CONNECTED_VERTICES;
    private static final String NAME_OF_HIDDEN_PROPERTIES_URI = SITE_URI + "name_of_hidden_properties";
    private static Property NAME_OF_HIDDEN_PROPERTIES;



    private static Model tripleBrainModel() {
        if (model == null) {
            model = ModelFactory.createDefaultModel();
            IS_FRONTIER_VERTEX_WITH_HIDDEN_VERTICES = model.createProperty(IS_FRONTIER_VERTEX_WITH_HIDDEN_VERTICES_URI);
            MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX = model.createProperty(MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX_URI);
            NUMBER_OF_HIDDEN_CONNECTED_VERTICES = model.createProperty(NUMBER_OF_HIDDEN_CONNECTED_VERTICES_URI);
            NAME_OF_HIDDEN_PROPERTIES = model.createProperty(NAME_OF_HIDDEN_PROPERTIES_URI);
        }
        return model;
    }

    public static Property IS_FRONTIER_VERTEX_WITH_HIDDEN_VERTICES() {
        if (IS_FRONTIER_VERTEX_WITH_HIDDEN_VERTICES == null) {
            IS_FRONTIER_VERTEX_WITH_HIDDEN_VERTICES = tripleBrainModel().getProperty(IS_FRONTIER_VERTEX_WITH_HIDDEN_VERTICES_URI);
        }
        return IS_FRONTIER_VERTEX_WITH_HIDDEN_VERTICES;
    }

    public static Property MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX() {
        if (MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX == null) {
            MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX = tripleBrainModel().getProperty(MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX_URI);
        }
        return MIN_NUMBER_OF_EDGES_FROM_CENTER_VERTEX;
    }

    public static Property NUMBER_OF_HIDDEN_CONNECTED_VERTICES() {
        if (NUMBER_OF_HIDDEN_CONNECTED_VERTICES == null) {
            NUMBER_OF_HIDDEN_CONNECTED_VERTICES = tripleBrainModel().getProperty(NUMBER_OF_HIDDEN_CONNECTED_VERTICES_URI);
        }
        return NUMBER_OF_HIDDEN_CONNECTED_VERTICES;
    }

    public static Property NAME_OF_HIDDEN_PROPERTIES() {
        if (NAME_OF_HIDDEN_PROPERTIES == null) {
            NAME_OF_HIDDEN_PROPERTIES = tripleBrainModel().getProperty(NAME_OF_HIDDEN_PROPERTIES_URI);
        }
        return NAME_OF_HIDDEN_PROPERTIES;
    }




}
