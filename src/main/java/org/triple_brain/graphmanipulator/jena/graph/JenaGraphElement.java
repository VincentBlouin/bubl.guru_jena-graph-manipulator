package org.triple_brain.graphmanipulator.jena.graph;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.triple_brain.graphmanipulator.jena.TripleBrainModel;
import org.triple_brain.module.model.graph.GraphElement;

import static com.hp.hpl.jena.vocabulary.RDFS.label;

/**
 * Copyright Mozilla Public License 1.1
 */
public class JenaGraphElement implements GraphElement{
    private Resource resource;

    public static JenaGraphElement withResource(Resource resource){
        return new JenaGraphElement(resource);
    }

    protected JenaGraphElement(Resource resource){
        this.resource = resource;
    }

    @Override
    public String id() {
        return resource.getURI();
    }

    @Override
    public String label() {
        return resource.getProperty(label).getString();
    }

    @Override
    public void label(String label) {
        resource.removeAll(RDFS.label);
        resource.addProperty(
                RDFS.label,
                resource.getModel().createTypedLiteral(label)
        );
    }

    @Override
    public boolean hasLabel() {
        return resource.hasProperty(label);
    }

    public boolean containsAnExternalType(){
        return externalTypeInTypes() != null;
    }

    public Resource externalTypeInTypes(){
        for(Statement statement : resource.listProperties(RDF.type).toList()){
            Resource type = statement.getObject().asResource();
            if(isExternalResource(type)){
                return type;
            }
        }
        return null;
    }

    private boolean isExternalResource(Resource resource){
        return !resource.getURI().toString().contains(
                TripleBrainModel.SITE_URI
        );
    }

    public Resource resourceFromGraphElement(GraphElement graphElement){
       return resource.getModel().getResource(graphElement.id());
    }

    public Model model(){
        return resource.getModel();
    }

}
