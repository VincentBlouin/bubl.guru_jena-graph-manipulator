package org.triple_brain.graphmanipulator.jena;

import com.hp.hpl.jena.rdf.model.*;
import static com.hp.hpl.jena.vocabulary.RDFS.*;
import static org.triple_brain.graphmanipulator.jena.FOAFModel.*;
import static org.triple_brain.graphmanipulator.jena.TripleBrainModel.*;
/**
 * @author Vincent Blouin
 */
public class User {

    private Integer nextId = 1;
    private String userName;
    private Model model;
    public static final String ABSOLUTE_CENTRAL_VERTEX_LOCAL_NAME = "element_1";

    public static User withUserName(String userName) {
        return new User(userName);
    }

    private User(String userName) {
        model = ModelFactory.createDefaultModel();
        this.userName = userName;
        Resource firstResource = model.createResource(URI() + ABSOLUTE_CENTRAL_VERTEX_LOCAL_NAME);
        firstResource.addLiteral(label, model.createTypedLiteral("me"));
        Resource nameRelation = model.createProperty(URI() + nextId());
        nameRelation.addProperty(label, "name");
        Resource userNameResource = model.createResource(URI() + nextId());
        userNameResource.addProperty(label, userName);
        firstResource.addProperty((Property) nameRelation, userNameResource);
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

}
