package org.ihtsdo.otf.snomed;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IndexCreatorTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testMain() {
		
		String args[] = new String[2];
		
		args[0] = "/Users/gouri/git/snomed-content-service/refset-query-interface/src/main/resources/titan-graph-es-dev.properties";//"/home/pnema/dev/titan-0.5.0-hadoop2/conf/titan-cassandra-es.properties";
    	args[1] = "/home/pnema/dev/titan-0.5.0-hadoop2/temp/out_Snapshot-en_INT_20140131.sorted.nq.gz";
    	
    	CopyOfSnomedIndexCreator.main(args);

	}

}
