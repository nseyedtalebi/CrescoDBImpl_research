import io.cresco.library.utilities.CLogger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import java.util.HashMap;
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

    public CoreDBImpl (PersistenceUnitInfo persistenceUnitInfo
            ,Map entityManagerProperties
            ,Map persistenceProviderProperties
    )
            throws ClassNotFoundException
            ,InstantiationException
            ,IllegalAccessException
    {
        this.logger = new CLogger("CoreDBImpl", CLogger.Level.Error);

        Map emPropsNoNull = entityManagerProperties == null ? new HashMap()
                : entityManagerProperties;
        Map ppNoNull = persistenceProviderProperties == null ? new HashMap()
                : persistenceProviderProperties;

        this.persistenceUnitName = persistenceUnitInfo.getPersistenceUnitName();
        this.persistenceUnitProperties = persistenceUnitInfo.getProperties();
        String persistenceClassName = persistenceUnitInfo.getPersistenceProviderClassName();
        try {
            this.provider = (PersistenceProvider) Class.forName(persistenceClassName).newInstance();
        }
        catch(ClassNotFoundException ex) {
            throw new ClassNotFoundException(
                    "Could not find provider class %s".format(persistenceClassName), ex.getCause());
        }
        catch(InstantiationException ex){
            throw new InstantiationException(
                    "Could not create instance of provider class %s because of %s".format(
                            persistenceClassName,ex.getMessage()));
        }
        catch(IllegalAccessException ex){
            throw new IllegalAccessException(
                    "Could not access instance of provider class %s because of %s".format(
                            persistenceClassName,ex.getMessage()));
        }
        //try{
        this.entityManagerFactory = provider.createContainerEntityManagerFactory(
                persistenceUnitInfo,ppNoNull);
        this.entityManager = this.entityManagerFactory.createEntityManager(emPropsNoNull);

        //}

        //this.entityManagerFactory =
        //this.entityManager = entityManagerFactory.createEntityManager();

    }




}
