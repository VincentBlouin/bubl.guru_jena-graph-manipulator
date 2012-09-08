package graph;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.triple_brain.graphmanipulator.jena.JenaConnection;
import org.triple_brain.graphmanipulator.jena.graph.JenaGraphMaker;
import org.triple_brain.module.model.graph.GraphMaker;
import org.triple_brain.module.repository_sql.JenaFriendlyDataSource;
import org.triple_brain.module.repository_sql.JenaH2DataSource;

import javax.sql.DataSource;

/*
* Copyright Mozilla Public License 1.1
*/
public class JenaTestModule extends AbstractModule{

    @Override
    protected void configure() {
        requestStaticInjection(JenaConnection.class);
        JenaFriendlyDataSource jenaFriendlyDataSource = new JenaH2DataSource();

        bind(DataSource.class)
                .annotatedWith(Names.named("jenaDB"))
                .toInstance(jenaFriendlyDataSource);

        bind(String.class)
                .annotatedWith(Names.named("jenaDatabaseTypeName"))
                .toInstance(jenaFriendlyDataSource.getDatabaseTypeName());

        bind(GraphMaker.class).to(JenaGraphMaker.class);

    }

}
