import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class PharmacyCounting {
	
	public static void main(String s[]){
		
		FileInputStream fis = null;
		BufferedReader reader = null;
		PrintWriter writer = null;
		
		//String directoryPath = System.getProperty("user.dir");
		String relativeInputFilePath = "../input/itcont.txt";
		String relativeOutputFilePath = "../output/top_cost_drug.txt";
		
		Map<String,Integer> indexMap = null;
		Map<String,HashSet<String>> drugPrescriberMap = null;
		Map<String,Integer> drugCostMap = null;
		
		boolean faultFlag = false;
		
		try {
			fis = new FileInputStream(relativeInputFilePath);
			reader = new BufferedReader(new InputStreamReader(fis));
			String line = reader.readLine();
			if(line != null || line != ""){
				indexMap = new HashMap<String,Integer>();
				fillIndexMap(indexMap,line);
				line = reader.readLine();
			}
			if(!indexMap.isEmpty() && line != null){
				drugPrescriberMap = new HashMap<String,HashSet<String>>();
				drugCostMap = new TreeMap<String,Integer>();
				while(line != null){
					String[] lineArray = line.split(",");
					String drugName = lineArray[indexMap.get("drug_name")];
					String firstName = lineArray[indexMap.get("prescriber_first_name")];
					String lastName = lineArray[indexMap.get("prescriber_last_name")];
					String cost = lineArray[indexMap.get("drug_cost")];
					
					if(!drugName.isEmpty() && !cost.isEmpty()){
						if(firstName.isEmpty() && lastName.isEmpty()){
							//System.out.println("First and Last name of the prescriber is empty in the dataset!");
							faultFlag = true;
							break;
						}else{
							String name = firstName+" "+lastName;
							
							if(drugPrescriberMap.isEmpty() || !drugPrescriberMap.containsKey(drugName)){
								HashSet<String> nameSet = new HashSet<String>();
								nameSet.add(name);
								drugPrescriberMap.put(drugName,nameSet);
							}else{
								HashSet<String> nameSet = drugPrescriberMap.get(drugName);
								nameSet.add(name);
								drugPrescriberMap.put(drugName,nameSet);
							}
							
							if(drugCostMap.isEmpty() || !drugCostMap.containsKey(drugName)){
								drugCostMap.put(drugName, Integer.parseInt(cost));
							}else{
								drugCostMap.put(drugName, drugCostMap.get(drugName)+Integer.parseInt(cost));
							}
							
						}
					}else{
						//System.out.println("Drug name or the cost is empty in the dataset!");
						faultFlag = true;
						break;
					}
					
					line = reader.readLine();
				}
				reader.close();
				if(!drugPrescriberMap.isEmpty() && !drugCostMap.isEmpty()){
					Set<Entry<String, Integer>> set = drugCostMap.entrySet();
			        List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(set);
			        Collections.sort( list, new Comparator<Map.Entry<String, Integer>>()
			        {
			            public int compare( Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2 )
			            {
			                return (o2.getValue()).compareTo( o1.getValue() );
			            }
			        } );
			        
			        writer = new PrintWriter(relativeOutputFilePath, "UTF-8");
			    	writer.println("drug_name,num_prescriber,total_cost");
			        
			        for(Map.Entry<String, Integer> entry:list){
			        	String result = "";
			        	result += entry.getKey()+",";
			        	int size = drugPrescriberMap.get(entry.getKey()).size();
			        	result += Integer.toString(size)+",";
			        	result += Integer.toString(entry.getValue());
			        	writer.println(result);
			        }
			        writer.close();
			        //System.out.println("Data printed in the output file");
			        
				}else{
					faultFlag = true;
					//System.out.println("drugPrescriberMap or drugCostMap is empty");
					//System.out.println("Dataset is erroneous!");
				}
			}else{
				faultFlag = true;
				//System.out.println("Error getting lines from the txt file");
			}
			
			if(faultFlag){
				System.out.println("Error entries in the dataset!");
			}
			
		} catch (FileNotFoundException e) {
			faultFlag = true;
			//System.out.println("File not found exception caught.");
			//System.out.println("Input File path = "+relativeInputFilePath);
			//System.out.println("Output File path = "+relativeOutputFilePath);
			//System.out.println("File not found exception = "+e);
			e.printStackTrace();
		} catch (IOException e) {
			faultFlag = true;
			//System.out.println("IO exception caught.");
			//System.out.println("Input File path = "+relativeInputFilePath);
			//System.out.println("Output File path = "+relativeOutputFilePath);
			//System.out.println("IO exception = "+e);
			e.printStackTrace();
		}
		if(faultFlag){
			//System.out.println("Fail");
		}else{
			//System.out.println("Pass");
		}
		
	}

	private static void fillIndexMap(Map<String, Integer> indexMap, String line) {
		String[] lineArray = line.split(",");
		for(int i=0; i<lineArray.length; i++){
			indexMap.put(lineArray[i], i);
		}
	}
}
