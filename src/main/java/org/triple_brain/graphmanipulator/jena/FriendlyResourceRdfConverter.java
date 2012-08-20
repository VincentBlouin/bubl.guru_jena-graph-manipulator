package org.triple_brain.graphmanipulator.jena;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.triple_brain.module.model.FriendlyResource;

import java.net.URI;
import java.net.URISyntaxException;

/*
* Copyright Mozilla Public License 1.1
*/
public class FriendlyResourceRdfConverter {

    public static Resource createInModel(Model model, FriendlyResource friendlyResource) {
        Resource friendlyResourceAsJenaResource = model.createResource(
                friendlyResource.uri().toString()
        );
        friendlyResourceAsJenaResource.addProperty(
                RDFS.label,
                model.createTypedLiteral(
                        friendlyResource.label()
                )
        );
        return friendlyResourceAsJenaResource;
    }

    public static FriendlyResource fromRdf(Resource resource) {
        try {
            return FriendlyResource.withUriAndLabel(
                    new URI(resource.getURI()),
                    resource.getProperty(RDFS.label).getString()
            );
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
