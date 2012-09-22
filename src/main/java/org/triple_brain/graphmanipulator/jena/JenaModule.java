package org.triple_brain.graphmanipulator.jena;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.triple_brain.graphmanipulator.jena.graph.JenaGraphFactory;
import org.triple_brain.module.model.graph.GraphFactory;

import javax.sql.DataSource;

import static com.google.inject.jndi.JndiIntegration.fromJndi;

/*
* Copyright Mozilla Public License 1.1
*/
public class JenaModule extends AbstractModule {

    @Override
    protected void configure() {

        requestStaticInjection(JenaConnection.class);
        bind(GraphFactory.class).to(JenaGraphFactory.class);
        bind(DataSource.class)
                .annotatedWith(Names.named("jenaDB"))
                .toProvider(fromJndi(DataSource.class, "jdbc/jenaTripleBrainDB"));

        bind(String.class)
                .annotatedWith(Names.named("jenaDatabaseTypeName"))
                .toProvider(fromJndi(String.class, "jdbc/jenaTripleBrainDBTypeName"));
        bind(String.class)
                .annotatedWith(Names.named("tdb_directory_path"))
                .toInstance("src/resources/tdb");
    }
}
