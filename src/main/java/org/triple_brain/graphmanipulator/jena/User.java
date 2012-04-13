package org.triple_brain.graphmanipulator.jena;

import com.hp.hpl.jena.rdf.model.*;
import static com.hp.hpl.jena.vocabulary.RDFS.*;
import static com.hp.hpl.jena.vocabulary.RDF.type;
import static org.triple_brain.graphmanipulator.jena.TripleBrainModel.*;
/**
 * Copyright Mozilla Public License 1.1
 */
public class User {

    private Integer nextId = 1;
    private String userName;
    private Model model;
    public static final String ABSOLUTE_CENTRAL_VERTEX_LOCAL_NAME = "element_1";
    private Resource userNameResource;
    public static User withUserName(String userName) {
        return new User(userName);
    }

    private User(String userName) {
        model = ModelFactory.createDefaultModel();
        this.userName = userName;
        Resource firstResource = model.createResource(URI() + ABSOLUTE_CENTRAL_VERTEX_LOCAL_NAME);
        firstResource.addLiteral(label, model.createTypedLiteral("me"));
        firstResource.addProperty(type, TRIPLE_BRAIN_VERTEX());

        Resource nameRelation = model.createProperty(URI() + nextId());
        nameRelation.addProperty(label, "name");
        nameRelation.addProperty(type, TripleBrainModel.TRIPLE_BRAIN_EDGE());

        firstResource.addProperty(HAS_OUTGOING_EDGE(), nameRelation);

        userNameResource = model.createResource(URI() + nextId());
        userNameResource.addProperty(label, userName);
        userNameResource.addProperty(type, TRIPLE_BRAIN_VERTEX());
        nameRelation.addProperty(DESTINATION_VERTEX(), userNameResource);

        firstResource.addProperty(HAS_NEIGHBOR(), userNameResource);
        userNameResource.addProperty(HAS_NEIGHBOR(), firstResource);
    }

    public synchronized String nextId() {
        nextId++;
        return "element_" + nextId.toString();
    }

    public String URI() {
        return TripleBrainModel.SITE_URI + userName.replace(" ", "_").toLowerCase() + "/";
    }

    public Model model(){
        return model;
    }

    public Resource absoluteCentralVertex() {
        String URI = URI() + ABSOLUTE_CENTRAL_VERTEX_LOCAL_NAME;
        return model.getResource(URI);
    }

    public Resource usernameResource() {
        return userNameResource;
    }

}
