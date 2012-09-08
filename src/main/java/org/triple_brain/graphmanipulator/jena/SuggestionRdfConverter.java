package org.triple_brain.graphmanipulator.jena;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.triple_brain.module.model.Suggestion;
import org.triple_brain.module.model.TripleBrainUris;

import java.util.UUID;

import static org.triple_brain.graphmanipulator.jena.JenaUtils.objectUriOfPropertyOfResource;

/*
* Copyright Mozilla Public License 1.1
*/
public class SuggestionRdfConverter {

    private Model model;
    private Resource resource;
    private Suggestion suggestion;
    public static SuggestionRdfConverter withModel(Model model){
        return new SuggestionRdfConverter(model);
    }

    private SuggestionRdfConverter(Model model){
        this.model = model;
    }
    public Resource suggestionToRdf(Suggestion suggestion){
        resource = model.createResource(TripleBrainUris.BASE+ "suggestion/" + UUID.randomUUID().toString());
        this.suggestion = suggestion;
        addType();
        addDomain();
        addLabel();
        return resource;
    }

    public Suggestion rdfToSuggestion(Resource resource){
        return Suggestion.withTypeDomainAndLabel(
                objectUriOfPropertyOfResource(RDF.type, resource),
                objectUriOfPropertyOfResource(RDFS.domain, resource),
                resource.getProperty(RDFS.label).getString()
        );
    }

    private void addType(){
        resource.addProperty(
                RDF.type,
                model.createResource(
                        suggestion.typeUri().toString()
                )
        );
    }
    private void addDomain(){
        resource.addProperty(
                RDFS.domain,
                model.createResource(
                        suggestion.domainUri().toString()
                )
        );
    }
    private void addLabel(){
        resource.addProperty(
                RDFS.label,
                model.createTypedLiteral(
                        suggestion.label().toString()
                )
        );
    }


}
