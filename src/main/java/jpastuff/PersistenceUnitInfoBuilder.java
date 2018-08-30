package jpastuff;

import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;
import java.net.URL;
import java.util.List;
import java.util.Properties;

public class PersistenceUnitInfoBuilder {
    private String persistenceUnitName
            ,persistenceProviderClassName;
    private PersistenceUnitTransactionType persistenceUnitTransactionType = PersistenceUnitTransactionType.RESOURCE_LOCAL;
    DataSource jtaDataSource
            ,nonJtaDataSource;
    private List<String> mappingFileNames
            ,managedClassNames;
    private List<URL> jarFileUrls;
    private SharedCacheMode sharedCacheMode = SharedCacheMode.UNSPECIFIED;
    private ValidationMode validationMode = ValidationMode.AUTO;
    private Properties properties;
    private boolean excludeUnlistedClasses = false;
    private URL persistenceUnitRootUrl;

    public PersistenceUnitInfoBuilder setPersistenceUnitName(String persistenceUnitName) {
        this.persistenceUnitName = persistenceUnitName;
        return this;
    }

    public PersistenceUnitInfoBuilder setPersistenceProviderClassName(String persistenceProviderClassName) {
        this.persistenceProviderClassName = persistenceProviderClassName;
        return this;
    }

    public PersistenceUnitInfoBuilder setPersistenceUnitTransactionType(PersistenceUnitTransactionType persistenceUnitTransactionType) {
        this.persistenceUnitTransactionType = persistenceUnitTransactionType;
        return this;
    }

    public PersistenceUnitInfoBuilder setJtaDataSource(DataSource jtaDataSource) {
        this.jtaDataSource = jtaDataSource;
        return this;
    }

    public PersistenceUnitInfoBuilder setNonJtaDataSource(DataSource nonJtaDataSource) {
        this.nonJtaDataSource = nonJtaDataSource;
        return this;
    }

    public PersistenceUnitInfoBuilder setMappingFileNames(List<String> mappingFileNames) {
        this.mappingFileNames = mappingFileNames;
        return this;
    }

    public PersistenceUnitInfoBuilder setManagedClassNames(List<String> managedClassNames) {
        this.managedClassNames = managedClassNames;
        return this;
    }

    public PersistenceUnitInfoBuilder setJarFileUrls(List<URL> jarFileUrls) {
        this.jarFileUrls = jarFileUrls;
        return this;
    }

    public PersistenceUnitInfoBuilder setSharedCacheMode(SharedCacheMode sharedCacheMode) {
        this.sharedCacheMode = sharedCacheMode;
        return this;
    }

    public PersistenceUnitInfoBuilder setValidationMode(ValidationMode validationMode) {
        this.validationMode = validationMode;
        return this;
    }

    public PersistenceUnitInfoBuilder setProperties(Properties properties) {
        this.properties = properties;
        return this;
    }

    public PersistenceUnitInfoBuilder setExcludeUnlistedClasses(boolean excludeUnlistedClasses){
        this.excludeUnlistedClasses = excludeUnlistedClasses;
        return this;
    }

    public PersistenceUnitInfoImpl build() {
        return new PersistenceUnitInfoImpl(persistenceUnitName
                ,persistenceProviderClassName
                ,persistenceUnitTransactionType
                ,jtaDataSource
                ,nonJtaDataSource
                ,mappingFileNames
                ,managedClassNames
                ,jarFileUrls
                ,sharedCacheMode
                ,validationMode
                ,properties
                ,excludeUnlistedClasses
                ,persistenceUnitRootUrl
                 );
    }
    public class NotEnoughOptionsConfiguredException extends Exception{
        public NotEnoughOptionsConfiguredException(String message) {
            super(message);
        }
    }
}