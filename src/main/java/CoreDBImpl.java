import io.cresco.library.utilities.CLogger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Properties;

public class CoreDBImpl {

    //The following should be the same as what's specified in persistence.xml
    public static final String DEFAULT_PERSISTENCE_UNIT_NAME = "io.cresco.controller";
    protected static final CLogger logger = new CLogger("CoreDBImpl", CLogger.Level.None);

    protected EntityManagerFactory entityManagerFactory;
    protected EntityManager entityManager;

    protected String persistenceUnitName;
    protected Properties persistenceUnitProperties;
    protected PersistenceProvider provider;
    protected PersistenceUnitInfo persistenceUnitInfo;

    public static CoreDBImpl getInstance(PersistenceUnitInfo puInfo
            ,Map persistenceUnitProps, Map entityManagerProps)
            throws ReflectiveOperationException{
        String persistenceClassName = puInfo.getPersistenceProviderClassName();
        PersistenceProvider persistenceProvider = getProviderFromClassName(persistenceClassName);
        EntityManagerFactory emf = getEMFactoryFromProvider(puInfo
                ,persistenceProvider,persistenceUnitProps);
        EntityManager em = emf.createEntityManager(entityManagerProps);
        return new CoreDBImpl(puInfo,em,emf,persistenceProvider);
    }

    protected static PersistenceProvider getProviderFromClassName(String persistenceClassName)
            throws ReflectiveOperationException{
        Class providerClass;
        Constructor providerConstructor;
        try {
             providerClass =  Class.forName(persistenceClassName);
        } catch(ClassNotFoundException ex){
            logger.error(String.format("Class %s not found",ex.getMessage()));
            throw new ClassNotFoundException(String.format("Could not find JPA provider class %s",persistenceClassName),ex);
        }
        try {
            providerConstructor = providerClass.getConstructor(null);
        } catch(ReflectiveOperationException ex){
            String msg = String.format("Could not get constructor for persistence provider %s: %s",persistenceClassName,ex.getMessage());
            logger.error(msg);
            throw new ReflectiveOperationException(msg,ex.getCause());
        }
        try {
            return (PersistenceProvider) providerConstructor.newInstance();
        } catch(ReflectiveOperationException ex){
            String msg = String.format("Could not get instance of persistence provider %s: %s",persistenceClassName,ex.getMessage());
            logger.error(msg);
            throw new ReflectiveOperationException(msg,ex.getCause());
        }
    }

    protected static EntityManagerFactory getEMFactoryFromProvider(
            PersistenceUnitInfo persistenceUnitInfo
            ,PersistenceProvider persistenceProvider
            ,Map persistenceUnitProps){
        return persistenceProvider.createContainerEntityManagerFactory(
                persistenceUnitInfo, persistenceUnitProps);
    }

    protected CoreDBImpl(PersistenceUnitInfo persistenceUnitInfo
    ,EntityManager entityManager
    ,EntityManagerFactory entityManagerFactory
    ,PersistenceProvider provider){
        this.entityManagerFactory = entityManagerFactory;
        this.entityManager = entityManager;
        this.persistenceUnitName = persistenceUnitInfo.getPersistenceUnitName();
        this.persistenceUnitProperties = persistenceUnitInfo.getProperties();
        this.provider = provider;
        this.persistenceUnitInfo = persistenceUnitInfo;
    }


}
