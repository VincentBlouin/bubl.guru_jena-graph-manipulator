package graph.mock;

import com.hp.hpl.jena.vocabulary.RDF;
import org.triple_brain.graphmanipulator.jena.TripleBrainModel;
import org.triple_brain.graphmanipulator.jena.graph.JenaUserGraph;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.SubGraph;

/**
 * Copyright Mozilla Public License 1.1
 */
public class JenaUserGraphMock extends JenaUserGraph {

    public static final Integer DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES = 10;

    public static JenaUserGraphMock mockWithUser(User user) throws Exception{
        return new JenaUserGraphMock(user);
    }

    private JenaUserGraphMock(User user) throws Exception{
        super(user);

    }

    public int numberOfEdgesAndVertices(){
        return model().listSubjectsWithProperty(
                RDF.type,
                TripleBrainModel.withEnglobingModel(model()).TRIPLE_BRAIN_VERTEX()
        ).toList()
                .size() +
                model().listSubjectsWithProperty(
                        RDF.type,
                        TripleBrainModel.withEnglobingModel(model()).TRIPLE_BRAIN_EDGE()
                ).toList()
                        .size();
    }

    public SubGraph wholeGraph(){
        return graphWithDefaultVertexAndDepth(DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES);
    }
}
