package db;

import db.jpa.entities.AgentRecord;
import db.jpa.entities.PluginRecord;
import db.jpa.entities.RegionRecord;
import io.cresco.library.utilities.CLogger;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
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
            throw new ReflectiveOperationException(msg,ex);
        }
        try {
            return (PersistenceProvider) providerConstructor.newInstance();
        } catch(ReflectiveOperationException ex){
            String msg = String.format("Could not get instance of persistence provider %s: %s",persistenceClassName,ex.getMessage());
            logger.error(msg);
            throw new ReflectiveOperationException(msg,ex);
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

    /*No way around casting the results, but doing it explicitly should help troubleshoot later
     As it's a runtime error and directly touches stuff outside of my code, I will need to make sure
     the casts work as expected in the integration tests.
     */
    protected ClassCastException handleStreamClassCastEx(ClassCastException ex, String targetType){
        if(ex == null) throw new IllegalArgumentException(
                "Parameter 'ex' for handleStreamClassCastEx() cannot be null");
        if(targetType == null) targetType="";
        logger.error(ex.getMessage());
        logger.error(ExceptionUtils.getStackTrace(ex));
        return new ClassCastException("Could not cast untyped Stream to " + targetType +
                ". Does the return type of the method match the types in the JPQL query?");
    }

    protected static final String REGION_QUERY_STRING = "SELECT r FROM RegionRecord r";
    public Stream<RegionRecord> getRegions(){
            return entityManager.createQuery(REGION_QUERY_STRING,RegionRecord.class).getResultStream();
    }

    protected static final String AGENT_QUERY_STRING = "SELECT a FROM AgentRecord a";
    public Stream<AgentRecord> getAgents(){
            return entityManager.createQuery(AGENT_QUERY_STRING,AgentRecord.class).getResultStream();

    }

    protected static final String PLUGIN_QUERY_STRING = "SELECT p FROM PluginRecord p";
    public Stream<PluginRecord> getPlugins(){
        return entityManager.createQuery(PLUGIN_QUERY_STRING,PluginRecord.class).getResultStream();
    }

    /*NMS I wonder if it's faster to run more very specific DB queries that return exactly what we want or
    to run fewer DB queries and manipulate the resultsets? Either way I can provide the methods and decide
    which ones to use later*/
    protected static final String QRY_AGENTS_IN_REGION = "SELECT a FROM AgentRecord a JOIN a.region r WHERE r.name = :rName";
    public Stream<AgentRecord> getAgentsInRegion(String regionName){
        if(regionName == null){
            throw new IllegalArgumentException("Argument to getAgentsInRegion(String) cannot be null");
        }
            return entityManager.createQuery(QRY_AGENTS_IN_REGION,AgentRecord.class).setParameter("rName", regionName).getResultStream();
    }

    protected static final String QRY_PLUGINS_IN_AGENT = "SELECT p FROM PluginRecord p JOIN p.agent a WHERE a.name = :aName";
    public Stream<PluginRecord> getPluginsInAgent(String agentName){
        if(agentName == null){
            throw new IllegalArgumentException("Argument to getPluginsInAgent(String) cannot be null");
        }
            return (Stream<PluginRecord>) entityManager
                    .createQuery(QRY_PLUGINS_IN_AGENT,PluginRecord.class)
                    .setParameter("aName",agentName)
                    .getResultStream();
    }

    protected static final String QRY_DBINTERFACE_GETREGIONLIST = "SELECT r.name,COUNT(a) FROM AgentRecord a JOIN a.region r GROUP BY r";
    public Stream<Object[]> getRegionList(){
        //NMS it may not actually return a tuple. If so, try Stream<Object[]>
            return entityManager.createQuery(QRY_DBINTERFACE_GETREGIONLIST, Object[].class).getResultStream();
    }

    protected static final String QRY_DBINTERFACE_GETAGENTLIST = "SELECT a, COUNT(p) FROM PluginRecord p JOIN p.agent a GROUP BY a";
    public Stream<Object[]>getAgentList(){
        return entityManager.createQuery(QRY_DBINTERFACE_GETAGENTLIST, Object[].class).getResultStream();
    }

    /*protected static final String QRY_DBINTERFACE_GETPLUGINLIST = "SELECT p FROM PluginRecord p";
    public Stream<Object[]>getPluginList(){
        return entityManager.createQuery(QRY_DBINTERFACE_GETPLUGINLIST, Object[].class).getResultStream();
    }*/


}
