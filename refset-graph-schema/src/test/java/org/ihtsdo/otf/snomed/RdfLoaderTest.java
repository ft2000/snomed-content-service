/**
 * 
 */
package org.ihtsdo.otf.snomed;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author gouri
 *
 */
public class RdfLoaderTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.ihtsdo.otf.snomed.RdfLoader#main(java.lang.String[])}.
	 */
	@Test
	public void testMain() {
		String args[] = new String[2];
		
		//args[0] = "/Users/gouri/dev/titan-0.5.0-hadoop2/conf/titan-cassandra-es.properties";
    	args[1] = "/Users/gouri/dev/titan-0.5.0-hadoop2/examples/out_Snapshot-en_INT_20140131.sorted.nq.gz";
		
		args[0] = "/Users/gouri/git/snomed-content-service/refset-query-interface/src/main/resources/titan-graph-es-dev.properties";//"/home/pnema/dev/titan-0.5.0-hadoop2/conf/titan-cassandra-es.properties";

    	RdfLoader.main(args);
		
	}

}
