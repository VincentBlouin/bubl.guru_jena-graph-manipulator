package org.triple_brain.graphmanipulator.jena;

import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.sdb.store.DatabaseType;
import org.triple_brain.module.repository_sql.SQLConnection;

import java.sql.SQLException;

/*
* Copyright Mozilla Public License 1.1
*/
public class JenaConnection {

    private static final String JENA_DATABASE_NAME = "jena_database";
    private static ModelMaker modelMaker;
    private static DBConnection dbConnection;

    public static ModelMaker modelMaker(){
        if(modelMaker == null){
            modelMaker = ModelFactory.createModelRDBMaker(connection());
        }
        return modelMaker;
    }

    public static void closeConnection() throws SQLException {
        dbConnection.close();
        modelMaker = null;
    }

    private static DBConnection connection(){
        try{
            Class.forName(SQLConnection.DRIVER_CLASS_PATH).newInstance();
            dbConnection = new DBConnection(
                    SQLConnection.DATABASES_PATH + JENA_DATABASE_NAME,
                    SQLConnection.USERNAME,
                    SQLConnection.PASSWORD,
                    DatabaseType.MySQL.getName()
            );
            return dbConnection;
        }
        catch(Exception e){
                e.printStackTrace();
            }
        return null;
    }
}
