package db.jpa.entities;

import db.NodeStatusType;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;


@Entity
@Table(name="plugin"
        ,uniqueConstraints = {@UniqueConstraint(columnNames={"plugin_name"})})
public class PluginRecord {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name="agent_id")
    private AgentRecord agent;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="region_id")
    private RegionRecord region;

    @Column(name="pluginname")
    private String pluginname;

    @Column(name="agentcontroller")
    private String agentcontroller;

    @Column(name="jarfile")
    private String jarfile;

    @Column(name="md5")
    private String md5;

    @Column(name="plugin_version")
    private String version;

    @ElementCollection(fetch = FetchType.LAZY)
    private Map<String,String> recordParams = new HashMap<>();


    @Column(name="plugin_status")
    private NodeStatusType status;


    protected PluginRecord(){}

    public Long getId() {
        return id;
    }

    public AgentRecord getAgent() {
        return agent;
    }

    public void setAgent(AgentRecord agent) {
        this.agent = agent;
    }

    public RegionRecord getRegion() {
        return region;
    }

    public void setRegion(RegionRecord region) {
        this.region = region;
    }

    public String getPluginname() {
        return pluginname;
    }

    public void setPluginname(String name) {
        this.pluginname = name;
    }

    public Map<String, String> getRecordParams() {
        return recordParams;
    }

    public void setRecordParams(Map<String, String> recordParams) {
        this.recordParams = recordParams;
    }

    public String getAgentcontroller() {
        return agentcontroller;
    }


    public void setAgentcontroller(String agentcontroller) {
        this.agentcontroller = agentcontroller;
    }

    public NodeStatusType getStatus() {
        return status;
    }

    public void setStatus(NodeStatusType status) {
        this.status = status;
    }

}
