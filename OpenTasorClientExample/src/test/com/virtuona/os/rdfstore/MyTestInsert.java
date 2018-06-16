package test.com.virtuona.os.rdfstore;

import com.virtuona.os.rdfstore.SPARQLstore;
import com.virtuona.os.rdfstore.SPARQLstoreClient;
import com.virtuona.os.sparql.TripletUpdateRequestBuffer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class MyTestInsert {
    public static final String SEP = File.separator;
    public static final String TEST_GRAPH = "http://www.tasorone.com/tsc/resources/BusinessModels";
    public static final String TEST_GRAPH_NS = TEST_GRAPH + "/";

    public static final String DS0_DIR = System.getProperty("user.dir") + SEP + "data" + SEP + "ds0" + SEP;
    public static final String spendonRandD = "spendonR&D";
    public static final String administration = "administration";
    public static final String City = "City";
    public static final String spendOnMarketing = "spendOnMarketing";
    public static final String profit = "profit";
    public static final String SUBJECT = "Startupcompany";

    public static void main(String[] args) throws Exception {
        String serviceEP = "http://infosys1.elfak.ni.ac.rs/scor/rdfstore";
        SPARQLstore store = new SPARQLstoreClient(serviceEP);

        List<String> data = MyTestInsert.read(DS0_DIR + '/' + profit);

        testTripletInsertLiteral(store, SUBJECT, profit, data.get(0), null);

        System.out.println(data);

        //CHECK
        String testQuery = "SELECT * { GRAPH ?g { ?resource <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <"+TEST_GRAPH_NS+ SUBJECT+"> . ?resource ?p ?o . } }";
        store.executeDatasetSPARQL(testQuery, System.out, "json", null);

    }

    public static List<String> read(String csvFile) throws IOException {
        String line = "";
        List<String> toRet = new ArrayList<String>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                toRet.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return toRet;
    }

    public static void testTripletInsertLiteral(SPARQLstore store, String subject, String predicate, String value, String objectType) throws Exception {
        TripletUpdateRequestBuffer turb = new TripletUpdateRequestBuffer(1000, TEST_GRAPH, store, TripletUpdateRequestBuffer.UpdateAction.INSERT);
        turb.addDataPropTriplet(TEST_GRAPH_NS + subject, TEST_GRAPH_NS + predicate, value, objectType);
        turb.flush();
    }
}
