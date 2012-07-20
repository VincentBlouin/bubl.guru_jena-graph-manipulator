package org.triple_brain.graphmanipulator.jena;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

import java.net.URI;
import java.net.URISyntaxException;

/*
* Copyright Mozilla Public License 1.1
*/
public class JenaUtils {

    public static URI objectUriOfPropertyOfResource(Property property, Resource resource){
        try{
            return new URI(resource.getProperty(property).getObject().asResource().getURI());
        }catch (URISyntaxException e){
            throw new RuntimeException(e);
        }
    }
}
