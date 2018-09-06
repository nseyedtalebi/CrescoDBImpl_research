import io.cresco.library.utilities.CLogger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import java.util.Map;
import java.util.Properties;

public class CoreDBImplFactory {

    public static CoreDBImpl getInstance(PersistenceUnitInfo puInfo
    ,Map persistenceUnitProps, Map entityManagerProps)
            throws ClassNotFoundException
            ,InstantiationException
            ,IllegalAccessException{
        String persistenceUnitName = puInfo.getPersistenceUnitName();
        String persistenceClassName = puInfo.getPersistenceProviderClassName();
        PersistenceProvider persistenceProvider = getProviderFromClassName(persistenceClassName);
        EntityManagerFactory emf = getEMFactoryFromProvider(puInfo,persistenceProvider,persistenceUnitProps);
        EntityManager em = emf.createEntityManager(entityManagerProps);
        return new CoreDBImpl();
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




}
