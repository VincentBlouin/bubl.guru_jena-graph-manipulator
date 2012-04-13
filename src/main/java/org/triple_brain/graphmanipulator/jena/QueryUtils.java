package org.triple_brain.graphmanipulator.jena;

/*
Copyright Mozilla Public License 1.1
*/
public class QueryUtils {

    public static String TRIPLE_BRAIN_PREFIX = "PREFIX tb:<http://www.triple_brain.org/> \n";
    public static String RDF_PREFIX = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n ";
    public static String RDFS_PREFIX = "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> \n ";
    public static String URIForQuery(String URI){
        return "<" + URI + ">";
    }
}
