import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class DataPointLog {

	private List<Map<String, Double>> dataSetList;
	
	public DataPointLog(){
		dataSetList = new ArrayList<Map<String, Double>>();
	}
	
	public void addEntry(Map<String, Double> entry){
		dataSetList.add(entry);
	}
	
	

}
