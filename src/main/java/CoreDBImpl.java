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
    protected PersistenceUnitInfo persistenceUnitInfo;

    public static CoreDBImpl getInstance(PersistenceUnitInfo puInfo
            ,Map persistenceUnitProps, Map entityManagerProps)
            throws ClassNotFoundException
            ,InstantiationException
            ,IllegalAccessException{
        String persistenceClassName = puInfo.getPersistenceProviderClassName();
        PersistenceProvider persistenceProvider = getProviderFromClassName(persistenceClassName);
        EntityManagerFactory emf = getEMFactoryFromProvider(puInfo,persistenceProvider,persistenceUnitProps);
        EntityManager em = emf.createEntityManager(entityManagerProps);
        return new CoreDBImpl(puInfo,em,emf,persistenceProvider);
    }

    private static PersistenceProvider getProviderFromClassName(String persistenceClassName)
            throws ClassNotFoundException
            ,InstantiationException
            ,IllegalAccessException {

        try {
            return (PersistenceProvider) Class.forName(persistenceClassName)
                    .newInstance();
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
    }

    private static EntityManagerFactory getEMFactoryFromProvider(
            PersistenceUnitInfo persistenceUnitInfo
            ,PersistenceProvider persistenceProvider
            ,Map persistenceUnitProps){
        return persistenceProvider.createContainerEntityManagerFactory(
                persistenceUnitInfo, persistenceUnitProps);
    }

    private CoreDBImpl(PersistenceUnitInfo persistenceUnitInfo
    ,EntityManager entityManager
    ,EntityManagerFactory entityManagerFactory
    ,PersistenceProvider provider){
        this.entityManagerFactory = entityManagerFactory;
        this.entityManager = entityManager;
        this.logger = new CLogger("CoreDBImpl", CLogger.Level.Error);
        this.persistenceUnitName = persistenceUnitInfo.getPersistenceUnitName();
        this.persistenceUnitProperties = persistenceUnitInfo.getProperties();
        this.provider = provider;
        this.persistenceUnitInfo = persistenceUnitInfo;
    }


}
