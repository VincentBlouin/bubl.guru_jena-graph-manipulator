package learning;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.junit.Test;
import org.triple_brain.graphmanipulator.jena.TripleBrainModel;

import static com.hp.hpl.jena.vocabulary.RDF.type;
import static com.hp.hpl.jena.vocabulary.RDFS.label;
import static junit.framework.Assert.assertFalse;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.triple_brain.graphmanipulator.jena.QueryUtils.*;
import static org.triple_brain.graphmanipulator.jena.TripleBrainModel.TRIPLE_BRAIN_EDGE;
import static org.triple_brain.graphmanipulator.jena.TripleBrainModel.TRIPLE_BRAIN_VERTEX;

public class JenaTest {

    public static String EXAMPLE_PREFIX = "PREFIX ex:<http://www.example.org/> \n ";

    @Test 
    public void can_make_sparql_query(){
        Model model = createModelWithAResourceCalledBobby();
        String query= "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
        "SELECT ?bobby_name "+
        "WHERE {" +
            "      ?z rdfs:label ?bobby_name. " +
            "      }";
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet rs = qe.execSelect();
        assertThat(rs.next().getLiteral("bobby_name").getString(), is("bobby"));
        qe.close();
    }

    @Test
    public void can_add_a_type_to_a_property(){
        Model model = ModelFactory.createDefaultModel();
        Resource property = model.createResource();
        property.addProperty(type, TRIPLE_BRAIN_EDGE());
        assertFalse(model.listSubjectsWithProperty(
                type,
                TRIPLE_BRAIN_EDGE()
        ).toList().isEmpty());
    }

    @Test
    public void can_make_query_filtered_on_custom_type(){
        Model model = createModelWithAResourceCalledBobbyHavingType(
                TripleBrainModel.TRIPLE_BRAIN_VERTEX()
        );
        String query= RDF_PREFIX +
                "SELECT ?resource_with_property "+
                "WHERE { " +
                    "?resource_with_property rdf:type " + URIForQuery(TRIPLE_BRAIN_VERTEX().getURI()) + " . " +
                "}";
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet rs = qe.execSelect();
        assertTrue(rs.hasNext());
        Resource bobby = rs.next().getResource("?resource_with_property");
        assertThat(bobby.getURI(), is(bobbyInModel(model).getURI()));
    }


    @Test
    public void can_use_degree_of_separation_in_query(){
        Model model = createModelWithAResourceCalledBobby();
        Resource firstResource = resourceWithLabelAndModel("first", model);
        Resource bobby = bobbyInModel(model);

        bobby.addProperty(
                RDFS.seeAlso,
                firstResource
                );

        firstResource.addProperty(
                RDFS.seeAlso,
                resourceWithLabelAndModel("second", model)
        );
        String query= RDFS_PREFIX+
                "SELECT ?secondSeeAlso "+
                "WHERE { " +
                URIForQuery(bobby.getURI()) + " rdfs:seeAlso{2} ?secondSeeAlso . " +
                "}";
        QueryExecution qe = QueryExecutionFactory.create(query, bobby.getModel());
        ResultSet rs = qe.execSelect();
        assertTrue(rs.hasNext());
        Resource secondSeeAlsoFromQuery = rs.next().getResource("?secondSeeAlso");
        assertThat(
                secondSeeAlsoFromQuery.getProperty(label).getString(),
                is("second")
        );
    }

    @Test
    public void can_use_specific_degree_of_separation_on_custom_property(){
        Model model = createModelWithAResourceCalledBobby();
        Resource bobby = bobbyInModel(model);
        Property hasFriend = model.createProperty("http://www.example.org/hasFriend");
        Resource jimmy = model.createResource("http://www.example.org/jimmy");
        jimmy.addProperty(label, "Jimmy");
        Resource juliette = model.createResource("http://www.example.org/juliette");
        juliette.addProperty(label, "Juliette");

        bobby.addProperty(hasFriend, jimmy);
        jimmy.addProperty(hasFriend, juliette);

        String query = EXAMPLE_PREFIX + RDFS_PREFIX+
                "SELECT ?juliette "+
                "WHERE { " +
                "ex:bobby ex:hasFriend{2} ?juliette . " +
                "}";
        QueryExecution qe = QueryExecutionFactory.create(query, bobby.getModel());
        ResultSet rs = qe.execSelect();
        assertTrue(rs.hasNext());
        Resource julietteFromQuery = rs.next().getResource("?juliette");
        assertThat(
                julietteFromQuery.getProperty(label).getString(),
                is("Juliette")
        );
    }



    private Resource resourceWithLabelAndModel(String label, Model model){
        Resource resource = model.createResource();
        resource.addProperty(RDFS.label, label);
        return resource;
    }

    private Model createModelWithAResourceCalledBobbyHavingType(Resource resourceType){
        Model model = createModelWithAResourceCalledBobby();
        Resource bobby = bobbyInModel(model);
        bobby.addProperty(type, resourceType);
        return bobby.getModel();
    }

    private Resource bobbyInModel(Model model){
        return model.listSubjectsWithProperty(label, "bobby").toList().get(0);
    }

    private Model createModelWithAResourceCalledBobby(){
        Model model = ModelFactory.createDefaultModel();
        Resource bobby = model.createResource("http://www.example.org/bobby");
        bobby.addLiteral(label, "bobby");
        return model;
    }

}
