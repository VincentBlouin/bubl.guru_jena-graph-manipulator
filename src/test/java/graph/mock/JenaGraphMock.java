package graph.mock;

import com.hp.hpl.jena.rdf.model.Model;
import org.triple_brain.graphmanipulator.jena.graph.JenaSubGraph;

/**
 * Copyright Mozilla Public License 1.1
 */
public class JenaGraphMock extends JenaSubGraph {

//    public static JenaGraphMock jenaGraphWithModel(Model model){
//        return new JenaGraphMock(model);
//    }
//
    protected JenaGraphMock(Model model){
        super(null, null);
    }
//
//    public List<Vertex> vertices(){
//        return null;
//    }

}
