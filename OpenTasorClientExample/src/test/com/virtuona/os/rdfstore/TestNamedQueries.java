package test.com.virtuona.os.rdfstore;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.virtuona.os.rdfstore.SPARQLstore;
import com.virtuona.os.rdfstore.SPARQLstoreClient;

public class TestNamedQueries {

	public static final String TEST_GRAPH = "http://www.example.com/testgraph";
	public static final String TEST_GRAPH_NS = TEST_GRAPH+"/";
	
	public static void main(String[] args) throws Exception {

		String serviceEP = "http://infosys1.elfak.ni.ac.rs/scor/rdfstore";
		SPARQLstore store = new SPARQLstoreClient(serviceEP);

		//---execute tests
		System.out.println("Listing first 100 triplets");
		executeExampleNamedQuery("ListAllTriplets", store);
		
		System.out.println("Creating new class");
		String classURI = executeUpdateNamedQuery1("Test Class 28", store);
		
		System.out.println("Printing resource triplets");
		executeExampleNamedQuery2(classURI, store);
		
		System.out.println("Deleting resource triplets");
		executeUpdateNamedQuery2(classURI, store);
		
		System.out.println("Printing resource triplets");
		executeExampleNamedQuery2(classURI, store);
		
		System.out.println("Listing first 100 triplets");
		executeExampleNamedQuery("ListAllTriplets", store);
		
		//---OVER
		System.out.println("OVER");
	}

	/*
	public static void listAllTriplets(SPARQLstore store) throws Exception{
		PrintStream ps = new PrintStream(System.out);
		String queryName = "ListAllTriplets";
		Map<String, String> qargs = new HashMap<String, String>();
		qargs.put("OFFSET", "0");
		qargs.put("LIMIT", "10");
		ResultSet rez = store.executeQuery(queryName, qargs);
		while(rez.hasNext()){
			QuerySolution qs = rez.next();
			ps.println("S="+qs.get("s").toString()+
					   "P="+qs.get("p").toString()+
					   "O="+qs.get("o").toString());
		}
	}
	*/


	/**
	 * 
	 * This query will create new RDF class in a given project.
	 * @param className Name of the class to create
	 * @param store
	 * @return URI of the newly created class
	 * @throws TagleenServicesException
	 */
	public static String executeUpdateNamedQuery1(String className, SPARQLstore store) throws Exception{
		
		//query arguments
		String safeClassName = className.trim().replaceAll(" ", "");
		String classURI = TEST_GRAPH + "/"+safeClassName;
		Map<String, String> qargs = new HashMap<String, String>();
		qargs.put("G", "<"+TEST_GRAPH+">");
		qargs.put("CLASS_URI", "<"+classURI+">");
		qargs.put("CLASS_LABEL", "'''"+className+"'''");
		
		//URI of the query
		String queryName = "NewClass";
		
		store.executeUpdateQuery(queryName, qargs);
		
		return classURI;
	}

	
	/**
	 * 
	 * This query will delete all triplets for given subject.
	 * @param resourceURI URI of the resource
	 * @param store
	 * @throws TagleenServicesException
	 */
	public static void executeUpdateNamedQuery2(String resourceURI, SPARQLstore store) throws Exception{
		
		//query arguments
		Map<String, String> qargs = new HashMap<String, String>();
		qargs.put("RESOURCE_URI", "<"+resourceURI+">");
		
		//URI of the query
		String queryURI = "DeleteResourceTriplets";
		
		store.executeUpdateQuery(queryURI, qargs);
		
	}
	
	/**
	 * This query will print all triplets where givern resource is subject
	 * @param resourceURI URI of the resource
	 * @param store
	 * @throws TagleenServicesException
	 */
	public static void executeExampleNamedQuery2(String resourceURI, SPARQLstore store) throws Exception{
		//output
		PrintStream ps = new PrintStream(System.out);
		
		//query arguments
		Map<String, String> qargs = new HashMap<String, String>();
		qargs.put("RESOURCE_URI", "<"+resourceURI+">");
		qargs.put("OFFSET", "0");
		qargs.put("LIMIT", "100");
		
		//URI of the query
		String queryName = "ListTripletsForResource";
		
		ResultSet rs = store.executeQuery(queryName, qargs);
		long count = 0;
		while(rs.hasNext()){
			QuerySolution qs = rs.next();
			if(qs.contains("s") && qs.contains("p") && qs.contains("o")){
				String subject = qs.get("s").asResource().toString();
				String predicate = qs.get("p").asResource().toString();
				String object = null;
				if(qs.get("o").isLiteral()){
					object = qs.get("o").asLiteral().getLexicalForm();
				}else{
					object = qs.get("o").asResource().toString();
				}
				count++;
				ps.println(count+"\tS: "+subject+"\tP: "+predicate+"\tO: "+object);
			}
		}
	}
	
	/**
	 * This query will print first 100 triplets in the given store
	 * @param queryName
	 * @param store
	 * @throws TagleenServicesException
	 */
	public static void executeExampleNamedQuery(String queryName, SPARQLstore store) throws Exception{
		//output
		PrintStream ps = new PrintStream(System.out);
		
		//query arguments
		Map<String, String> qargs = new HashMap<String, String>();
		qargs.put("OFFSET", "0");
		qargs.put("LIMIT", "100");
		
		ResultSet rs = store.executeQuery(queryName, qargs);
		long count = 0;
		while(rs.hasNext()){
			QuerySolution qs = rs.next();
			if(qs.contains("s") && qs.contains("p") && qs.contains("o")){
				String subject = qs.get("s").asResource().toString();
				String predicate = qs.get("p").asResource().toString();
				String object = null;
				if(qs.get("o").isLiteral()){
					object = qs.get("o").asLiteral().getLexicalForm();
				}else{
					object = qs.get("o").asResource().toString();
				}
				count++;
				ps.println(count+"\tS: "+subject+"\tP: "+predicate+"\tO: "+object);
			}
		}
	}
}
