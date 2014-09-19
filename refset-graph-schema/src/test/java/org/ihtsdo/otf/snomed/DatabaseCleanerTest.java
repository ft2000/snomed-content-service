package org.ihtsdo.otf.snomed;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DatabaseCleanerTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		String args[] = new String[2];
		
		args[0] = "/home/pnema/dev/titan-0.5.0-hadoop2/conf/titan-cassandra-es.properties";
    	args[1] = "/home/pnema/dev/titan-0.5.0-hadoop2/temp/out_Snapshot-en_INT_20140131.sorted.nq.gz";
    	
    	//DatabaseCleaner.main(args);

	}

}
