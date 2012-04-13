package org.triple_brain.graphmanipulator.jena;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Copyright Mozilla Public License 1.1
 */
public class FOAFModel {

   private static FOAFModel FOAFModel;
   private Model model;

   public static FOAFModel FOAFModel() throws IOException{
    if(FOAFModel == null){
        FOAFModel = new FOAFModel();
    }
    return FOAFModel;
   }

   private FOAFModel() throws IOException{
        model = ModelFactory.createDefaultModel();
        FileReader fileReader = new FileReader(new File("resources/foaf.rdf"));
        model.read(fileReader, null);
   }

   public Property name(){
       return (Property) model.getResource("http://xmlns.com/foaf/0.1/name");
   }

}
