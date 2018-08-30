import io.cresco.library.utilities.CLogger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Map;

public class CoreDBImpl {

    //The following should be the same as what's specified in persistence.xml
    public static final String DEFAULT_PERSISTENCE_UNIT_NAME = "io.cresco.controller";

    protected EntityManagerFactory entityManagerFactory;
    protected EntityManager entityManager;
    protected CLogger logger;
    protected String persistenceUnitName;
    protected Map persistenceUnitProperties;

    protected CoreDBImpl(){}

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
    }

    public CoreDBImpl (String persistenceUnitName, Map persistenceUnitProperties){
        this.persistenceUnitName = persistenceUnitName;
        this.persistenceUnitProperties = persistenceUnitProperties;
        this.entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName, persistenceUnitProperties);
        this.entityManager = entityManagerFactory.createEntityManager();

    }



}
