package org.triple_brain.graphmanipulator.jena.graph;

/**
 * Copyright Mozilla Public License 1.1
 */
public class JenaGraphElementManipulator {

//    private Model userModel;
//
//    public static JenaGraphElementManipulator withUser(User user){
//        Model userModel = JenaConnection.modelMaker().getNamedModel(user.mindMapURIFromSiteURI(SITE_URI));
//        return new JenaGraphElementManipulator(userModel);
//    }
//
//    public static JenaGraphElementManipulator withUserModel(Model userModel){
//        return new JenaGraphElementManipulator(userModel);
//    }
//
//    private JenaGraphElementManipulator(Model userModel){
//        this.userModel = userModel;
//    }
//
//    public JenaGraphElementManipulator updateLabel(String graphElementURI, String label) throws NonExistingResourceException{
//        Resource graphElementAsResource = userModel.getResource(graphElementURI);
//        if (!graph().containsResource(graphElementAsResource)) {
//            throw new NonExistingResourceException(graphElementURI);
//        }
//        GraphElement graphElement = JenaGraphElement.withResource(
//                graphElementAsResource
//        );
//        graphElement.label(label);
//        return this;
//    }
//
//    public Model graph(){
//        return userModel;
//    }
}
