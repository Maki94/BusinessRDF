package test.com.virtuona.os.rdfstore;

import com.virtuona.os.rdfstore.SPARQLstore;
import com.virtuona.os.rdfstore.SPARQLstoreClient;
import com.virtuona.os.sparql.TripletUpdateRequestBuffer;
import com.virtuona.os.sparql.TripletUpdateRequestBuffer.UpdateAction;

public class TestTripletsRW {

	public static final String TEST_GRAPH = "http://www.example.com/testgraph";
	public static final String TEST_GRAPH_NS = TEST_GRAPH+"/";
	
	public static void main(String[] args) throws Exception {

		String serviceEP = "http://infosys1.elfak.ni.ac.rs/scor/rdfstore";
		SPARQLstore store = new SPARQLstoreClient(serviceEP);
		 
		//CHECK
		String testQuery = "SELECT * { GRAPH ?g { ?resource <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <"+TEST_GRAPH_NS+"DummyClass> . ?resource ?p ?o . } }";
		store.executeDatasetSPARQL(testQuery, System.out, "json", null);
		
		//INSERT
		System.out.println("Inserting triplets");
		testTripletInsert(store);
		store.executeDatasetSPARQL(testQuery, System.out, "json", null);
		
		//DELETE 
		System.out.println("Deleting triplets for resource 1");
		testTripletDelete(store);
		store.executeDatasetSPARQL(testQuery, System.out, "json", null);
		
		//DELETE ALL
		System.out.println("Deleting all inserted triplets");
		testTripletDeleteAll(store);
		store.executeDatasetSPARQL(testQuery, System.out, "json", null);
	}
	
	/**
	 * Insert two resources. Each resource have 2 triplets.
	 * @param store
	 * @throws Exception
	 */
	public static void testTripletInsert(SPARQLstore store) throws Exception{
		TripletUpdateRequestBuffer turb = new TripletUpdateRequestBuffer(1000, TEST_GRAPH, store, UpdateAction.INSERT);
		
		turb.addDataPropTriplet(TEST_GRAPH_NS+"Resource1", TEST_GRAPH_NS+"Property1", "Dummy literal value", null);
		turb.addObjectPropTriplet(TEST_GRAPH_NS+"Resource1", "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", TEST_GRAPH_NS+"DummyClass");
		turb.addDataPropTriplet(TEST_GRAPH_NS+"Resource2", TEST_GRAPH_NS+"Property1", "Dummy literal value", null);
		turb.addObjectPropTriplet(TEST_GRAPH_NS+"Resource2", "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", TEST_GRAPH_NS+"DummyClass");
		
		turb.flush();
	}

	/**
	 * Delete one resource by deleting two of its triplets
	 * @param store
	 * @throws Exception
	 */
	public static void testTripletDelete(SPARQLstore store) throws Exception{
		TripletUpdateRequestBuffer turb = new TripletUpdateRequestBuffer(1000, TEST_GRAPH, store, UpdateAction.DELETE);
		
		turb.addDataPropTriplet(TEST_GRAPH_NS+"Resource1", TEST_GRAPH_NS+"Property1", "Dummy literal value", null);
		turb.addObjectPropTriplet(TEST_GRAPH_NS+"Resource1", "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", TEST_GRAPH_NS+"DummyClass");
		
		turb.flush();
	}
	
	/**
	 * Delete all resources that are of type Dummy class
	 * @param store
	 * @throws Exception
	 */
	public static void testTripletDeleteAll(SPARQLstore store) throws Exception{
		String query = "DELETE { GRAPH ?g { ?s ?p ?o } } WHERE { GRAPH ?g {  ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <"+TEST_GRAPH_NS+"DummyClass> . ?s ?p ?o } }";
		store.executeDatasetUpdateSPARQL(query);
	}
}
