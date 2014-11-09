/**
 * 
 */
package org.ihtsdo.otf.refset.common;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.EnumMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Episteme Partners
 *
 */
@RunWith( PowerMockRunner.class )
@PrepareForTest( EnumMap.class )
public class UriFormatterTest {
	
	private static final String BASE_URI = "http://sct.snomed.info/";
	
	private static final String NAMED_GRAPH_URI_FMT = "http://sct.snomed.info/module/%s/release/%s";
	
	private static final String MODULE_URI_FMT = "http://sct.snomed.info/module/%s";
	
	private static final String CONCEPT_URI_FMT = "http://sct.snomed.info/%s";
	
	
	private static final String NAMED_GRAPH_URI = "http://sct.snomed.info/module/123456777/release/20140131";
	
	private static final String MODULE_URI = "http://sct.snomed.info/module/123456777";
	
	private static final String CONCEPT_URI = "http://sct.snomed.info/12345";

	
	@Mock
	private EnumMap<URIFormats, String> formats;
	
	private UriFormatter formatter;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		formatter = new UriFormatter();
		formatter.setFormats(formats);

		when(formats.get(URIFormats.baseUri)).thenReturn(BASE_URI);
		
		when(formats.get(URIFormats.namedGraphUriFmt)).thenReturn(NAMED_GRAPH_URI_FMT);

		when(formats.get(URIFormats.conceptUriFmt)).thenReturn(CONCEPT_URI_FMT);

		when(formats.get(URIFormats.releaseUriFmt)).thenReturn(NAMED_GRAPH_URI_FMT);
		
		when(formats.get(URIFormats.moduleUriFmt)).thenReturn(MODULE_URI_FMT);



	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.ihtsdo.otf.refset.common.UriFormatter#getNamedGraphUri(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetNamedGraphUri() {
		
		String result = UriFormatter.getNamedGraphUri("123456777", "20140131");
		
		assertEquals(NAMED_GRAPH_URI, result);
		
	}

	/**
	 * Test method for {@link org.ihtsdo.otf.refset.common.UriFormatter#getReleaseUri(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetReleaseUri() {
		
		String result = UriFormatter.getReleaseUri("123456777", "20140131");
		
		assertEquals(NAMED_GRAPH_URI, result);
		
	}

	/**
	 * Test method for {@link org.ihtsdo.otf.refset.common.UriFormatter#getBaseUri(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetBaseUri() {
		
		String result = UriFormatter.getBaseUri();
		
		assertEquals(BASE_URI, result);
		
	}

	/**
	 * Test method for {@link org.ihtsdo.otf.refset.common.UriFormatter#getConceptUri(java.lang.String)}.
	 */
	@Test
	public void testGetConceptUri() {
		
		String result = UriFormatter.getConceptUri("12345");
		
		assertEquals(CONCEPT_URI, result);
		
	}

	/**
	 * Test method for {@link org.ihtsdo.otf.refset.common.UriFormatter#getModuletUri(java.lang.String)}.
	 */
	@Test
	public void testGetModuletUri() {
		
		String result = UriFormatter.getModuletUri("123456777");
		
		assertEquals(MODULE_URI, result);
		
	}

}
