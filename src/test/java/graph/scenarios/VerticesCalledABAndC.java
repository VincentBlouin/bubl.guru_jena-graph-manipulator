package graph.scenarios;

import org.triple_brain.module.model.graph.Vertex;

/*
* Copyright Mozilla Public License 1.1
*/
public class VerticesCalledABAndC {

    private Vertex vertexA;
    private Vertex vertexB;
    private Vertex vertexC;

    public VerticesCalledABAndC(Vertex vertexA, Vertex vertexB, Vertex vertexC){
        this.vertexA = vertexA;
        this.vertexB = vertexB;
        this.vertexC = vertexC;
    }

    public Vertex vertexA(){
        return vertexA;
    }

    public Vertex vertexB(){
        return vertexB;
    }

    public Vertex vertexC(){
        return vertexC;
    }
}
