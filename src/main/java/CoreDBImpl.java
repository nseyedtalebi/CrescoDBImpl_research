import io.cresco.library.utilities.CLogger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import java.util.Map;
import java.util.Properties;

public class CoreDBImpl {

    //The following should be the same as what's specified in persistence.xml
    public static final String DEFAULT_PERSISTENCE_UNIT_NAME = "io.cresco.controller";

    protected EntityManagerFactory entityManagerFactory;
    protected EntityManager entityManager;
    protected CLogger logger;
    protected String persistenceUnitName;
    protected Properties persistenceUnitProperties;
    protected PersistenceProvider provider;

    /*protected CoreDBImpl(){}

    protected CoreDBImpl entityManager(EntityManager toAdd){
        this.entityManager = toAdd;
        return this;
    }

    protected CoreDBImpl logger(CLogger toAdd){
        this.logger = toAdd;
        return this;
    }

    public CoreDBImpl(Map persistenceUnitProperties){
        this(DEFAULT_PERSISTENCE_UNIT_NAME,persistenceUnitProperties);
    }*/

    public CoreDBImpl (PersistenceUnitInfo persistenceUnitInfo) throws PersistenceProviderClassNotFound {
        this.persistenceUnitName = persistenceUnitInfo.getPersistenceUnitName();
        this.persistenceUnitProperties = persistenceUnitInfo.getProperties();
        try {
            this.provider = (PersistenceProvider) Class.forName(persistenceUnitInfo.getPersistenceProviderClassName()).newInstance();
        }//TODO:Keep working on this
        catch(ClassNotFoundException ex){
            throw new PersistenceProviderClassNotFound("Could not find provider class %s".format(this.provider));
        }
        //this.entityManagerFactory =
        //this.entityManager = entityManagerFactory.createEntityManager();

    }




}
