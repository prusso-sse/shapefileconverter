package gov.usgs.wqp.shapefileconverter.parser.wqx.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import gov.usgs.wqp.shapefileconverter.model.FeatureDAO;
import gov.usgs.wqp.shapefileconverter.model.providers.SourceProvider;

import java.util.ArrayList;
import java.util.List;

import org.apache.xerces.parsers.SAXParser;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SimplePointProviderHandlerTest {
	private SAXParser xmlReader;
	SimpleFeatureBuilder featureBuilder;
	private List<FeatureDAO> featureList;
	
	@Before
	public void init() throws Exception {		
		xmlReader = mock(SAXParser.class);
		featureBuilder = mock(SimpleFeatureBuilder.class);
		featureList = new ArrayList<FeatureDAO>();
	}
	
	@After
	public void destroy() throws Exception {
		
	}
	
	@Test
	public void testStartElementNotInteresting() {
		SimplePointProviderHandler providerHandler = new SimplePointProviderHandler(this.xmlReader, this.featureList, this.featureBuilder);
		
		assertEquals(SourceProvider.UNKNOWN, providerHandler.getCurrentProvider());		
		
		try {
			providerHandler.startElement("uri", "localName", "qName", null);
		} catch (Exception e) {
			fail("Error calling startElement(): " + e.getMessage());
		}
		
		assertEquals(SourceProvider.UNKNOWN, providerHandler.getCurrentProvider());		
	}
	
	@Test
	public void testStartElementInteresting() {
		SimplePointProviderHandler providerHandler = new SimplePointProviderHandler(this.xmlReader, this.featureList, this.featureBuilder);
		
		assertEquals(SourceProvider.UNKNOWN, providerHandler.getCurrentProvider());		
		
		try {
			providerHandler.startElement("uri", "localName", "Organization", null);
		} catch (Exception e) {
			fail("Error calling startElement(): " + e.getMessage());
		}
		
		verify(this.xmlReader).setContentHandler(any(SimplePointLocationHandler.class));
		
		assertEquals(SourceProvider.UNKNOWN, providerHandler.getCurrentProvider());	
	}
	
	@Test
	public void testEndElementNotInteresting() {
		SimplePointProviderHandler providerHandler = new SimplePointProviderHandler(this.xmlReader, this.featureList, this.featureBuilder);
		
		assertEquals(SourceProvider.UNKNOWN, providerHandler.getCurrentProvider());		
		
		try {
			providerHandler.endElement("uri", "localName", "qName");
		} catch (Exception e) {
			fail("Error calling endElement(): " + e.getMessage());
		}
		
		assertEquals(SourceProvider.UNKNOWN, providerHandler.getCurrentProvider());		
	}
	
	@Test
	public void testEndElementInteresting() {
		SimplePointProviderHandler providerHandler = new SimplePointProviderHandler(this.xmlReader, this.featureList, this.featureBuilder);
		
		assertEquals(SourceProvider.UNKNOWN, providerHandler.getCurrentProvider());		
		
		/**
		 * Set up the contents character array to simulate parsing xml
		 */
		char[] testContents = {'N', 'W', 'I', 'S'};
		try {
			providerHandler.characters(testContents, 0, testContents.length);
		} catch (Exception e) {
			fail("Error calling characters(): " + e.getMessage());
		}
		
		try {
			providerHandler.endElement("uri", "localName", "ProviderName");
		} catch (Exception e) {
			fail("Error calling startElement(): " + e.getMessage());
		}
		
		assertEquals(SourceProvider.NWIS, providerHandler.getCurrentProvider());	
	}
}
