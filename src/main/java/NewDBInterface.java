import javax.persistence.spi.PersistenceUnitInfo;

import io.cresco.library.utilities.CLogger;
import jpa.*;
import jpa.entities.AgentRecord;
import queryresults.AgentListResult;
import queryresults.RegionListResult;

import java.util.*;
import java.util.stream.Collectors;
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
    RegionListResult getRegionList(){
        Stream<Object[]> results = db.getRegionList();
        return new RegionListResult(results.map(object -> new RegionListResult.ListEntry((String)object[0],(long)object[1]))
                .collect(Collectors.toList()));
        /*
        Map<String,List<Map<String,String>>> toReturn = new HashMap<>();
        List<Map<String,String>> regionList = results.map(object -> {
            Map<String,String> region = new HashMap<>();
            region.put("name",(String)object[0]);
            return region;
        }).collect(Collectors.toList());
        toReturn.put("regions",regionList);
        return toReturn;
        */
    }

    AgentListResult getAgentList(){
        Stream<Object[]>results = db.getAgentList();
        return new AgentListResult(results.map(object -> {
            AgentRecord agent = (AgentRecord) object[0];
            return new AgentListResult.ListEntry(agent.getName(),agent.getRegion().getName(),
                    (long)object[1]/*plugin count*/,agent.getRecordParams().get("location"),
                    agent.getRecordParams().get("platform"),
                    agent.getRecordParams().get("environment"));
        }).collect(Collectors.toList()));
    }

}

/*Things to remember:
    -Only implement the stuff for the "internal db" part, not application or global-only stuff
    -Move health info to properties on record types (jpa.entities.AgentRecord, jpa.entities.PluginRecord, jpa.entities.RegionRecord)
    -Add stuff to call sites to handle streams instead of strings. That is, move the serialization
    out of DBInterface to somplace else

 */
