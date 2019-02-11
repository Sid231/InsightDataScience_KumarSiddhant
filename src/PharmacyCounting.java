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
	
	//MAIN CLASS
	public static void main(String s[]){
		
		//FILE INPUT AND OUTPUT WRITER INSTANCES DEFINITION
		FileInputStream fis = null;
		BufferedReader reader = null;
		PrintWriter writer = null;
		
		//FILE PATHS
		//String directoryPath = System.getProperty("user.dir");
		String relativeInputFilePath = "../input/itcont.txt";
		String relativeOutputFilePath = "../output/top_cost_drug.txt";
		
		
		/**
		* DATA STRUCTURE INIT TO STORE THE INPUT
		*/
		
		//STORES KEY AS THE INDEX NAME AND VALUE AS THE INDEX
		//id:0, prescriber_last_name:1, prescriber_first_name:2, drug_name:3, drug_cost:4
		Map<String,Integer> indexMap = null;
		//STORES KEY AS THE DRUG NAME AND VALUE AS THE SET OF CUSTOMERS (FIRST + LAST NAME)
		Map<String,HashSet<String>> drugPrescriberMap = null;
		//STORES KEY AS THE DRUG NAME AND VALUE AS THE DRUG COST
		Map<String,Integer> drugCostMap = null;
		
		//BOOLEAN FLAG SET TO TRUE FOR ANY FAULTY CASE. ELSE REMAINS AS FALSE
		boolean faultFlag = false;
		
		try {
			//INIT AND READ INPUT FROM THE GIVEN FILE
			fis = new FileInputStream(relativeInputFilePath);
			reader = new BufferedReader(new InputStreamReader(fis));
			String line = reader.readLine();
			if(line != null || line != ""){
				indexMap = new HashMap<String,Integer>();
				//SETTING UP THE INDEX MAP
				fillIndexMap(indexMap,line);
				line = reader.readLine();
			}
			if(!indexMap.isEmpty() && line != null){
				drugPrescriberMap = new HashMap<String,HashSet<String>>();
				drugCostMap = new TreeMap<String,Integer>();
				//GETTING THE DATA FROM THE FILE LINE BY LINE IN A LOOP
				while(line != null){
					String[] lineArray = line.split(",");
					String drugName = lineArray[indexMap.get("drug_name")];
					String firstName = lineArray[indexMap.get("prescriber_first_name")];
					String lastName = lineArray[indexMap.get("prescriber_last_name")];
					String cost = lineArray[indexMap.get("drug_cost")];
					
					if(!drugName.isEmpty() && !cost.isEmpty()){
						if(firstName.isEmpty() && lastName.isEmpty()){
							//FAULTY CASE: BOTH THE FIRST AND LAST NAME OF THE PRESCRIBER IS EMPTY IN THE DATASET
							//System.out.println("First and Last name of the prescriber is empty in the dataset!");
							faultFlag = true;
							break;
						}else{
							String name = firstName+" "+lastName;
							
							//ENTER THE DRUG AND THE PERSON DETAILS IN THE drugPrescriberMap
							if(drugPrescriberMap.isEmpty() || !drugPrescriberMap.containsKey(drugName)){
								HashSet<String> nameSet = new HashSet<String>();
								nameSet.add(name);
								drugPrescriberMap.put(drugName,nameSet);
							}else{
								HashSet<String> nameSet = drugPrescriberMap.get(drugName);
								nameSet.add(name);
								drugPrescriberMap.put(drugName,nameSet);
							}
							
							//ENTER THE DRUG AND THE COST OF THE DRUG DETAILS IN THE drugCostMap
							if(drugCostMap.isEmpty() || !drugCostMap.containsKey(drugName)){
								drugCostMap.put(drugName, Integer.parseInt(cost));
							}else{
								drugCostMap.put(drugName, drugCostMap.get(drugName)+Integer.parseInt(cost));
							}
							
						}
					}else{
						//FAULTY CASE: EITHER THE DRUG NAME OR THE COST OF THE DRUG IS EMPTY IN THE DATASET!
						//System.out.println("Drug name or the cost is empty in the dataset!");
						faultFlag = true;
						break;
					}
					
					line = reader.readLine();
				}
				reader.close();
				
				//IF NEITHER OF THE FILES ARE EMPTY THEN GO FOR THE NEXT STEP
				if(!drugPrescriberMap.isEmpty() && !drugCostMap.isEmpty()){
					
					//SORT THE drugCostMap BY MAP VALUES
					Set<Entry<String, Integer>> set = drugCostMap.entrySet();
			        List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(set);
			        Collections.sort( list, new Comparator<Map.Entry<String, Integer>>()
			        {
			            public int compare( Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2 )
			            {
			                return (o2.getValue()).compareTo( o1.getValue() );
			            }
			        } );
			        
			        //INIT THE OUTPUT WRITER TO WRITE ON A FILE
			        writer = new PrintWriter(relativeOutputFilePath, "UTF-8");
			    	writer.println("drug_name,num_prescriber,total_cost");
			        
			    	//WRITE DATA ON THE FILE BY ITERATING THRU THE MAP SORTED BY VALUE (THE COST OF THE DRUGS)
			        for(Map.Entry<String, Integer> entry:list){
			        	String result = "";
			        	result += entry.getKey()+",";
			        	int size = drugPrescriberMap.get(entry.getKey()).size();
			        	result += Integer.toString(size)+",";
			        	result += Integer.toString(entry.getValue());
			        	writer.println(result);
			        }
			        writer.close();
			        //DATA PRINT IS SUCCESSFUL
			        //System.out.println("Data printed in the output file");
			        
				}else{
					//FAULTY CASE: EITHER THE drugPrescriberMap OR THE drugCostMap IS EMPTY!
					faultFlag = true;
					//System.out.println("drugPrescriberMap or drugCostMap is empty");
					//System.out.println("Dataset is erroneous!");
				}
			}else{
				//FAULTY CASE: ERROR GETTING DATA LINE BY LINE FROM THE GIVEN TXT FILE
				faultFlag = true;
				//System.out.println("Error getting lines from the txt file");
			}
			
			if(faultFlag){
				//FAULTY CASE: ERROR ENTRIES IN THE DATASET
				//System.out.println("Error entries in the dataset!");
			}
			
		} catch (FileNotFoundException e) {
			faultFlag = true;
			//FAULTY CASE: FILE NOT FOUND EXCEPTION
			//System.out.println("File not found exception caught.");
			//System.out.println("Input File path = "+relativeInputFilePath);
			//System.out.println("Output File path = "+relativeOutputFilePath);
			//System.out.println("File not found exception = "+e);
			e.printStackTrace();
		} catch (IOException e) {
			faultFlag = true;
			//FAULTY CASE: IO EXCEPTION
			//System.out.println("IO exception caught.");
			//System.out.println("Input File path = "+relativeInputFilePath);
			//System.out.println("Output File path = "+relativeOutputFilePath);
			//System.out.println("IO exception = "+e);
			e.printStackTrace();
		}
		if(faultFlag){
			//FAULTY CASE
			//System.out.println("Fail");
		}else{
			//SUCCESSFUL CASE
			//System.out.println("Pass");
		}
		
	}

	/**
	 * THIS FUNCTION FILLS THE INDEXMAP WITH THE COLUMN NAMES AND THEIR RESPECTIVE INDEX
	 * 
	 * @param indexMap: INDEX MAP TO BE FILLED
	 * @param line: INPUT COLUMN DATA FROM THE FILE AS A STRING
	 * 
	 */
	private static void fillIndexMap(Map<String, Integer> indexMap, String line) {
		String[] lineArray = line.split(",");
		for(int i=0; i<lineArray.length; i++){
			indexMap.put(lineArray[i], i);
		}
	}
}
