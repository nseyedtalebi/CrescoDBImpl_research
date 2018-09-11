import io.cresco.library.db.AgentRecord;
import io.cresco.library.db.PluginRecord;
import io.cresco.library.db.RegionRecord;
import io.cresco.library.utilities.CLogger;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

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
        CoreDBImpl newInstance = new CoreDBImpl(puInfo);
        newInstance.provider = newInstance.getProviderFromPUInfo();
        newInstance.entityManagerFactory = newInstance.provider
                .createContainerEntityManagerFactory(
                        newInstance.persistenceUnitInfo,persistenceUnitProps
                );
        newInstance.entityManager =  newInstance.entityManagerFactory
                .createEntityManager(entityManagerProps);
        return newInstance;
    }

    protected PersistenceProvider getProviderFromPUInfo()
            throws ReflectiveOperationException{
        String persistenceClassName = this.persistenceUnitInfo.getPersistenceProviderClassName();
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

    protected CoreDBImpl(PersistenceUnitInfo persistenceUnitInfo){
        this.persistenceUnitName = persistenceUnitInfo.getPersistenceUnitName();
        this.persistenceUnitProperties = persistenceUnitInfo.getProperties();
        this.persistenceUnitInfo = persistenceUnitInfo;
    }

    protected static final String REGION_QUERY_STRING = "SELECT r FROM RegionRecord r";
    public Stream<RegionRecord> getRegions(){
        try {
            return entityManager.createQuery(REGION_QUERY_STRING).getResultStream();
        } catch(ClassCastException ex){
            logger.error(ex.getMessage());
            logger.error(ExceptionUtils.getStackTrace(ex));
            throw new ClassCastException("Could not cast untyped Stream to Stream<RegionRecord>. " +
                    "Perhaps something related to persistence is misconfigured?");
        }
    }

    protected static final String AGENT_QUERY_STRING = "SELECT a FROM AgentRecord a";
    public Stream<AgentRecord> getAgents(){
        try {
            return (Stream<AgentRecord>) entityManager.createQuery(AGENT_QUERY_STRING).getResultStream();
        }
        catch(ClassCastException ex){
            logger.error(ex.getMessage());
            logger.error(ExceptionUtils.getStackTrace(ex));
            throw new ClassCastException("Could not cast untyped Stream to Stream<AgentRecord>. " +
                    "Perhaps something related to persistence is misconfigured?");
        }
    }

    protected static final String PLUGIN_QUERY_STRING = "SELECT p FROM PluginRecord p";
    public Stream<PluginRecord> getPlugins(){
        try {
            return (Stream<PluginRecord>) entityManager.createQuery(PLUGIN_QUERY_STRING).getResultStream();
        }catch(ClassCastException ex){
            logger.error(ex.getMessage());
            logger.error(ExceptionUtils.getStackTrace(ex));
            throw new ClassCastException("Could not cast untyped Stream to Stream<PluginRecord>". " +
                    "Perhaps something related to persistence is misconfigured?");
        }

    }

    /*NMS I wonder if it's faster to run more very specific DB queries that return exactly what we want or
    to run fewer DB queries and manipulate the resultsets? Either way I can provide the methods and decide
    which ones to use later*/
    protected static final String AGENTS_IN_REGION_QUERY_STRING = "SELECT a FROM AgentRecord a JOIN a.region r WHERE r.name = :rName";
    public Stream<AgentRecord> getAgentsInRegion(String regionName){
        try {
            return entityManager.createQuery(AGENTS_IN_REGION_QUERY_STRING).setParameter("rName", regionName).getResultStream();
        }catch(ClassCastException ex){
            logger.error(ex.getMessage());
            logger.error(ExceptionUtils.getStackTrace(ex));
            throw new ClassCastException("Could not cast untyped Stream to . " +
                    "Perhaps something related to persistence is misconfigured?");
        }
    }


}
