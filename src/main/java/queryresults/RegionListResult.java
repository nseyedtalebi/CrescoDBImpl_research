package queryresults;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class RegionListResult {
    private List<ListEntry> regions;

    public RegionListResult(List<ListEntry> regions){
        this.regions = regions;
    }

    public List<ListEntry> getRegions() {
        return regions;
    }

    public static class ListEntry{
        String name;
        long agents;

        public ListEntry(String name,long agents){
            this.name = name;
            this.agents = agents;
        }
    }
}
