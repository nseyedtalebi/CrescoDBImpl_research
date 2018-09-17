package db.jpa.entities;

import db.NodeStatusType;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

public class AgentRecord {

    @Id
    @GeneratedValue
    private long id;

    @Column(name="agent_name")
    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="region_id")
    private RegionRecord region;

    @ElementCollection(fetch = FetchType.LAZY)
    private Map<String,String> recordParams = new HashMap<>();

    @Column(name="agent_status")
    private NodeStatusType status;

    protected AgentRecord() {
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RegionRecord getRegion() {
        return region;
    }

    public void setRegion(RegionRecord region) {
        this.region = region;
    }

    public Map<String, String> getRecordParams() {
        return recordParams;
    }

    public void setRecordParams(Map<String, String> recordParams) {
        this.recordParams = recordParams;
    }
}
