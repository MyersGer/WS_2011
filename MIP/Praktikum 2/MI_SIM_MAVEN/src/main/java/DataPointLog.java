import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class DataPointLog {

	private List<Set<Map<String, Double>>> dataSetList;
	
	public DataPointLog(){
		dataSetList = new ArrayList<Set<Map<String, Double>>>();
	}
	
	public void addEntry(Set<Map<String, Double>> entry){
		dataSetList.add(entry);
	}
	
	

}
