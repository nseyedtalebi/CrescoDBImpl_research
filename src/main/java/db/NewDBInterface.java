package db;

import javax.persistence.spi.PersistenceUnitInfo;

import db.jpa.entities.AgentRecord;
import db.jpa.PersistenceUnitInfoBuilder;
import db.jpa.queryresults.*;
import io.cresco.agent.controller.db.DBInterface;
import io.cresco.library.messaging.MsgEvent;
import io.cresco.library.utilities.CLogger;
import io.cresco.library.plugin.PluginBuilder;
import io.cresco.agent.controller.core.ControllerEngine;
import db.jpa.entities.PluginRecord;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NewDBInterface {
    private CoreDBImpl db;
    private PersistenceUnitInfo puInfo;
    private PluginBuilder plugin;
    private ControllerEngine ce;

    public class Builder {
        private CoreDBImpl db;
        private PersistenceUnitInfo puInfo;
        private PluginBuilder plugin;
        private ControllerEngine ce;
        private Map persistenceUnitProps;
        private Map entityManagerProps;

        public Builder (){
        }

        public Builder setDb(CoreDBImpl toSet){
            this.db = toSet;
            return this;
        }

        public Builder setPuInfo(PersistenceUnitInfo toSet){
            return this.setPuInfo(toSet,null,null);
        }

        public Builder setPuInfo(PersistenceUnitInfo toSet,Map persistenceUnitProps, Map entityManagerProps){
            this.puInfo = toSet;
            this.persistenceUnitProps = persistenceUnitProps;
            this.entityManagerProps = entityManagerProps;
            return this;
        }

        public Builder setPluginBuilder(PluginBuilder toSet){
            this.plugin = toSet;
            return this;
        }

        public Builder setControllerEngine(ControllerEngine toSet){
            this.ce = toSet;
            return this;
        }

        public NewDBInterface build() throws ReflectiveOperationException {
            return new NewDBInterface(this.db,
                    this.plugin,
                    this.ce);
        }
    }
    protected static final CLogger logger = new CLogger("NewDBInterface"
            , CLogger.Level.None);

    private NewDBInterface(CoreDBImpl db,
                          PluginBuilder plugin,
                          ControllerEngine ce){
        this.db = db;
        this.plugin = plugin;
        this.ce = ce;
    }

    //getRegionList()
    public RegionListResult getRegionList(){
        Stream<Object[]> results = db.getRegionList();
        return new RegionListResult(results.map(object -> new RegionListResult.ListEntry((String)object[0],(long)object[1]))
                .collect(Collectors.toList()));
    }

    public AgentListResult getAgentList(){
        Stream<Object[]>results = db.getAgentList();
        return new AgentListResult(results.map(object -> {
            AgentRecord agent = (AgentRecord) object[0];
            return new AgentListResult.ListEntry(agent.getName(),agent.getRegion().getName(),
                    (long)object[1]/*plugin count*/,agent.getRecordParams().get("location"),
                    agent.getRecordParams().get("platform"),
                    agent.getRecordParams().get("environment"));
        }).collect(Collectors.toList()));
    }

    public PluginListResult getPluginList(){
        Stream<Object[]>results = db.getPluginList();
        return new PluginListResult(results.map(object -> {
            PluginRecord plugin = (PluginRecord) object[0];
            return new PluginListResult.ListEntry(plugin.getPluginname(),plugin.getRegion().getName(),plugin.getAgent().getName());
        }).collect(Collectors.toList()));
    }

    public PluginListByTypeResult getPluginListByType() {
        Stream<Object[]> results = db.getPluginList();
        return new PluginListByTypeResult(results.map(object -> {
            PluginRecord plugin = (PluginRecord) object[0];
            return new PluginListByTypeResult.ListEntry(plugin.getAgentcontroller(), plugin.getRegion().getName(), plugin.getAgent().getName(), plugin.getRecordParams());
        }).collect(Collectors.toList()));
    }

    public PluginListRepoInventoryResult getPluginListRepoInventory(){
        return new PluginListRepoInventoryResult(
                getPluginListByType().getPlugins().stream().map(listEntry -> {
                   MsgEvent request = plugin.getGlobalPluginMsgEvent(MsgEvent.Type.EXEC,
                           listEntry.getRegion(),
                           listEntry.getAgent(),
                           listEntry.getNodeParams().getOrDefault("pluginid",
                                   "MISSING_ID_DBINTERFACE"));
                   MsgEvent response = plugin.sendRPC(request);
                   return response.getCompressedParam("repolist");
                }).collect(Collectors.toList())
        );
    }

   /*NMS
   I'm not implementing the following (BUT WILL HAVE TO FIX UP OTHER STUFF TO ACCOUNT FOR IT)
        getNodeStatus(String,String,String) because it was not really used
        getEdgeHealthStatus because the data will be read directly from Agent/Region/Plugin records
        getPluginInfo because it will be read from PluginRecord

    */




}

/*Things to remember:
    -Only implement the stuff for the "internal db" part, not application or global-only stuff
    -Move health info to properties on record types (AgentRecord, PluginRecord, RegionRecord)
    -Add stuff to call sites to handle streams instead of strings. That is, move the serialization
    out of DBInterface to somplace else

 */
