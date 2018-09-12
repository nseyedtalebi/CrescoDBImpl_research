import javax.persistence.spi.PersistenceUnitInfo;
import javax.swing.plaf.synth.Region;

import io.cresco.library.utilities.CLogger;
import jpastuff.*;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

public class NewDBInterface {
    private CoreDBImpl db;
    private PersistenceUnitInfo puInfo;
    protected static final CLogger logger = new CLogger("NewDBInterface"
            , CLogger.Level.None);

    public NewDBInterface(String puName, String providerClassName)
            throws ReflectiveOperationException{
        this(puName, providerClassName, null, null);
    }
    public NewDBInterface(String puName, String providerClassName, Map persistenceUnitProps
    ,Map entityManagerProps) throws ReflectiveOperationException {
        this.puInfo = new PersistenceUnitInfoBuilder(puName, providerClassName).build();
        this.db = CoreDBImpl.getInstance(puInfo,persistenceUnitProps,entityManagerProps);
    }

    //getRegionList()
    Stream<RegionRecord> getRegionList(){
        /*TODO: Implement lower-level method to get plugin count at agent, agent count in region.
        Then use that to finish implementing this one.
         */
        return db.getRegions();
    }

}

/*Things to remember:
    -Only implement the stuff for the "internal db" part, not application or global-only stuff
    -Move health info to properties on record types (AgentRecord, PluginRecord, RegionRecord)
    -Add stuff to call sites to handle streams instead of strings. That is, move the serialization
    out of DBInterface to somplace else

 */
