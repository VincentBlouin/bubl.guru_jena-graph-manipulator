package org.triple_brain.graphmanipulator.jena.graph;
import com.hp.hpl.jena.rdf.model.Statement;
/**
 * @author Vincent Blouin
 */
public class JenaRelation {

    private Statement statement;

    public static JenaRelation jenaRelationWithStatement(Statement statement){
        return new JenaRelation(statement);
    }
    
    private JenaRelation(Statement statement){
        this.statement = statement;
    }
}
