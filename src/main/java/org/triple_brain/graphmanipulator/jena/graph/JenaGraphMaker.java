package org.triple_brain.graphmanipulator.jena.graph;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDBFactory;
import org.triple_brain.graphmanipulator.jena.JenaConnection;
import org.triple_brain.module.model.TripleBrainUris;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.GraphMaker;
import org.triple_brain.module.model.graph.UserGraph;
import org.triple_brain.module.model.graph.Vertex;

/*
* Copyright Mozilla Public License 1.1
*/
public class JenaGraphMaker implements GraphMaker {

    @Override
    public UserGraph createForUser(User user) {
        Model model = TDBFactory.createNamedModel(
                user.mindMapURIFromSiteURI(TripleBrainUris.BASE),
                JenaConnection.ModelsDirectory
        );
        Vertex vertex = createDefaultVertexForUserAndModel(user, model);
        vertex.label("me");
        return JenaUserGraph.withUser(user);
    }

    private Vertex createDefaultVertexForUserAndModel(User user, Model model) {
        String newVertexURI = user.URIFromSiteURI(TripleBrainUris.BASE) + "default";
        return JenaVertex.createUsingModelUriAndOwner(model, newVertexURI, user);
    }
}
