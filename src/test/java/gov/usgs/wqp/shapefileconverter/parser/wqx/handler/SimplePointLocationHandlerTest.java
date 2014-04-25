package gov.usgs.wqp.shapefileconverter.parser.wqx.handler;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import gov.usgs.wqp.shapefileconverter.model.FeatureDAO;
import gov.usgs.wqp.shapefileconverter.model.attributes.BaseAttributeType;
import gov.usgs.wqp.shapefileconverter.model.attributes.FeatureAttributeType;
import gov.usgs.wqp.shapefileconverter.model.features.SimplePointFeature;
import gov.usgs.wqp.shapefileconverter.model.providers.SourceProvider;

import java.util.ArrayList;
import java.util.List;

import org.apache.xerces.parsers.SAXParser;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vividsolutions.jts.geom.Point;

public class SimplePointLocationHandlerTest {
	private SAXParser xmlReader;
	SimpleFeatureBuilder featureBuilder;
	SimplePointProviderHandler parentHandler;
	SourceProvider provider;
	private List<FeatureDAO> featureList;
	
	@Before
	public void init() throws Exception {
		xmlReader = mock(SAXParser.class);
		featureBuilder = mock(SimpleFeatureBuilder.class);
		parentHandler = mock(SimplePointProviderHandler.class);
		provider = SourceProvider.NWIS;
		featureList = new ArrayList<FeatureDAO>();
	}
	
	@After
	public void destroy() throws Exception {
		
	}
	
	@Test
	public void testStartElementNotInteresting() {
		SimplePointLocationHandler locationHandler = null;
		try {
			locationHandler = new SimplePointLocationHandler(this.parentHandler, this.xmlReader, this.featureList, this.provider, this.featureBuilder);
		} catch (Exception e) {
			fail("Error creating SimplePointLocationHandler: " + e.getMessage());
		}
		
		try {
			locationHandler.startElement("uri", "localName", "qName", null);
		} catch (Exception e) {
			fail("Error calling startElement(): " + e.getMessage());
		}
		
		assertNull(locationHandler.getCurrentPointFeature());		
	}
	
	@Test
	public void testStartElementInteresting() {
		SimplePointLocationHandler locationHandler = null;
		try {
			locationHandler = new SimplePointLocationHandler(this.parentHandler, this.xmlReader, this.featureList, this.provider, this.featureBuilder);
		} catch (Exception e) {
			fail("Error creating SimplePointLocationHandler: " + e.getMessage());
		}
		
		try {
			locationHandler.startElement("uri", "localName", SimplePointLocationHandler.LOCATION_START, null);
		} catch (Exception e) {
			fail("Error calling startElement(): " + e.getMessage());
		}
		
		assertNotNull(locationHandler.getCurrentPointFeature());
		assertThat(locationHandler.getCurrentPointFeature(), instanceOf(SimplePointFeature.class));
	}
	
	@Test
	public void testEndElementNoStart() {
		SimplePointLocationHandler locationHandler = null;
		try {
			locationHandler = new SimplePointLocationHandler(this.parentHandler, this.xmlReader, this.featureList, this.provider, this.featureBuilder);
		} catch (Exception e) {
			fail("Error creating SimplePointLocationHandler: " + e.getMessage());
		}
		
		try {
			locationHandler.endElement("uri", "localName", "qName");
		} catch (Exception e) {
			fail("Error calling startElement(): " + e.getMessage());
		}
		
		assertNull(locationHandler.getCurrentPointFeature());
	}
	
	@Test
	public void testEndElementIdentifier() {
		SimplePointLocationHandler locationHandler = null;
		try {
			locationHandler = new SimplePointLocationHandler(this.parentHandler, this.xmlReader, this.featureList, this.provider, this.featureBuilder);
		} catch (Exception e) {
			fail("Error creating SimplePointLocationHandler: " + e.getMessage());
		}
		
		/**
		 * We need to start the parsing with the SimplePointLocationHandler.LOCATION_START flag
		 */
		try {
			locationHandler.startElement("uri", "localName", SimplePointLocationHandler.LOCATION_START, null);
		} catch (Exception e) {
			fail("Error calling startElement(): " + e.getMessage());
		}
		
		assertNotNull(locationHandler.getCurrentPointFeature());
		assertThat(locationHandler.getCurrentPointFeature(), instanceOf(SimplePointFeature.class));
		
		/**
		 * Now populate the contents as if it was parsed with a "name" and send
		 * the handler the SimplePointLocationHandler.LOCATION_IDENTIFIER flag
		 */
		char[] testContents = {'N', 'a', 'm', 'e'};
		try {
			locationHandler.characters(testContents, 0, testContents.length);
		} catch (Exception e) {
			fail("Error calling characters(): " + e.getMessage());
		}
		
		/**
		 * Now send the endElement() call with our ID flag
		 */
		try {
			locationHandler.endElement("uri", "localName", SimplePointLocationHandler.LOCATION_IDENTIFIER);
		} catch (Exception e) {
			fail("Error calling startElement(): " + e.getMessage());
		}
		
		/**
		 * Now lets see if the SimplePointFeature name was populated with our character array
		 */
		assertEquals("Name", locationHandler.getCurrentPointFeature().getName());
		assertEquals("Name", locationHandler.getCurrentPointFeature().getBaseAttribute(BaseAttributeType.LocationIdentifier));
		assertEquals("Name", locationHandler.getCurrentPointFeature().getFeatureAttribute(FeatureAttributeType.name));
	}
	
	@Test
	public void testEndElementType() {
		SimplePointLocationHandler locationHandler = null;
		try {
			locationHandler = new SimplePointLocationHandler(this.parentHandler, this.xmlReader, this.featureList, this.provider, this.featureBuilder);
		} catch (Exception e) {
			fail("Error creating SimplePointLocationHandler: " + e.getMessage());
		}
		
		/**
		 * We need to start the parsing with the SimplePointLocationHandler.LOCATION_START flag
		 */
		try {
			locationHandler.startElement("uri", "localName", SimplePointLocationHandler.LOCATION_START, null);
		} catch (Exception e) {
			fail("Error calling startElement(): " + e.getMessage());
		}
		
		assertNotNull(locationHandler.getCurrentPointFeature());
		assertThat(locationHandler.getCurrentPointFeature(), instanceOf(SimplePointFeature.class));
		
		/**
		 * Now populate the contents as if it was parsed with a "type" and send
		 * the handler the SimplePointLocationHandler.LOCATION_TYPE flag
		 */
		char[] testContents = {'m', 'Y', 't', 'Y', 'p', 'E'};
		try {
			locationHandler.characters(testContents, 0, testContents.length);
		} catch (Exception e) {
			fail("Error calling characters(): " + e.getMessage());
		}
		
		/**
		 * Now send the endElement() call with our TYPE flag
		 */
		try {
			locationHandler.endElement("uri", "localName", SimplePointLocationHandler.LOCATION_TYPE);
		} catch (Exception e) {
			fail("Error calling startElement(): " + e.getMessage());
		}
		
		/**
		 * Now lets see if the SimplePointFeature name was populated with our character array
		 */
		assertEquals("mYtYpE", locationHandler.getCurrentPointFeature().getType());
		assertEquals("mYtYpE", locationHandler.getCurrentPointFeature().getBaseAttribute(BaseAttributeType.LocationType));
		assertEquals("mYtYpE", locationHandler.getCurrentPointFeature().getFeatureAttribute(FeatureAttributeType.type));
	}
	
	@Test
	public void testEndElementLongitude() {
		SimplePointLocationHandler locationHandler = null;
		try {
			locationHandler = new SimplePointLocationHandler(this.parentHandler, this.xmlReader, this.featureList, this.provider, this.featureBuilder);
		} catch (Exception e) {
			fail("Error creating SimplePointLocationHandler: " + e.getMessage());
		}
		
		/**
		 * We need to start the parsing with the SimplePointLocationHandler.LOCATION_START flag
		 */
		try {
			locationHandler.startElement("uri", "localName", SimplePointLocationHandler.LOCATION_START, null);
		} catch (Exception e) {
			fail("Error calling startElement(): " + e.getMessage());
		}
		
		assertNotNull(locationHandler.getCurrentPointFeature());
		assertThat(locationHandler.getCurrentPointFeature(), instanceOf(SimplePointFeature.class));
		
		/**
		 * Now populate the contents as if it was parsed with a "longitude" and send
		 * the handler the SimplePointLocationHandler.LONGITUDE flag
		 */
		char[] testContents = {'5', '3', '.', '1', '7'};
		try {
			locationHandler.characters(testContents, 0, testContents.length);
		} catch (Exception e) {
			fail("Error calling characters(): " + e.getMessage());
		}
		
		/**
		 * Now send the endElement() call with our LONGITUTDE flag
		 */
		try {
			locationHandler.endElement("uri", "localName", SimplePointLocationHandler.LONGITUDE);
		} catch (Exception e) {
			fail("Error calling startElement(): " + e.getMessage());
		}
		
		/**
		 * Now lets see if the SimplePointFeature name was populated with our character array
		 */
		assertEquals(53.17, locationHandler.getCurrentPointFeature().getLongitude(), 0);
		assertEquals("53.17", locationHandler.getCurrentPointFeature().getBaseAttribute(BaseAttributeType.Longitude));
		
		
		assertThat(locationHandler.getCurrentPointFeature().getFeatureAttribute(FeatureAttributeType.point), instanceOf(Point.class));
		Point thePoint = (Point) locationHandler.getCurrentPointFeature().getFeatureAttribute(FeatureAttributeType.point);
		assertEquals(53.17, thePoint.getCoordinate().x, 0);
	}
	
	@Test
	public void testEndElementLattitude() {
		SimplePointLocationHandler locationHandler = null;
		try {
			locationHandler = new SimplePointLocationHandler(this.parentHandler, this.xmlReader, this.featureList, this.provider, this.featureBuilder);
		} catch (Exception e) {
			fail("Error creating SimplePointLocationHandler: " + e.getMessage());
		}
		
		/**
		 * We need to start the parsing with the SimplePointLocationHandler.LOCATION_START flag
		 */
		try {
			locationHandler.startElement("uri", "localName", SimplePointLocationHandler.LOCATION_START, null);
		} catch (Exception e) {
			fail("Error calling startElement(): " + e.getMessage());
		}
		
		assertNotNull(locationHandler.getCurrentPointFeature());
		assertThat(locationHandler.getCurrentPointFeature(), instanceOf(SimplePointFeature.class));
		
		/**
		 * Now populate the contents as if it was parsed with a "lattitude" and send
		 * the handler the SimplePointLocationHandler.LATTITUDE flag
		 */
		char[] testContents = {'-', '9', '2', '.', '3', '8'};
		try {
			locationHandler.characters(testContents, 0, testContents.length);
		} catch (Exception e) {
			fail("Error calling characters(): " + e.getMessage());
		}
		
		/**
		 * Now send the endElement() call with our LATTITUDE flag
		 */
		try {
			locationHandler.endElement("uri", "localName", SimplePointLocationHandler.LATTITUDE);
		} catch (Exception e) {
			fail("Error calling startElement(): " + e.getMessage());
		}
		
		/**
		 * Now lets see if the SimplePointFeature name was populated with our character array
		 */
		assertEquals(-92.38, locationHandler.getCurrentPointFeature().getLatitude(), 0);
		assertEquals("-92.38", locationHandler.getCurrentPointFeature().getBaseAttribute(BaseAttributeType.Latitude));
		
		
		assertThat(locationHandler.getCurrentPointFeature().getFeatureAttribute(FeatureAttributeType.point), instanceOf(Point.class));
		Point thePoint = (Point) locationHandler.getCurrentPointFeature().getFeatureAttribute(FeatureAttributeType.point);
		assertEquals(-92.38, thePoint.getCoordinate().y, 0);
	}
	
	@Test
	public void testEndElementStart() {
		SimplePointLocationHandler locationHandler = null;
		try {
			locationHandler = new SimplePointLocationHandler(this.parentHandler, this.xmlReader, this.featureList, this.provider, this.featureBuilder);
		} catch (Exception e) {
			fail("Error creating SimplePointLocationHandler: " + e.getMessage());
		}
		
		/**
		 * We need to start the parsing with the SimplePointLocationHandler.LOCATION_START flag
		 */
		try {
			locationHandler.startElement("uri", "localName", SimplePointLocationHandler.LOCATION_START, null);
		} catch (Exception e) {
			fail("Error calling startElement(): " + e.getMessage());
		}
		
		assertNotNull(locationHandler.getCurrentPointFeature());
		assertThat(locationHandler.getCurrentPointFeature(), instanceOf(SimplePointFeature.class));
		
		/**
		 * Now populate the contents as if it was parsed with a "name" and send
		 * the handler the SimplePointLocationHandler.LOCATION_IDENTIFIER flag
		 */
		char[] testContents = {'T', 'e', 'S', 't', 'P', 'o', 'I', 'n', 'T'};
		try {
			locationHandler.characters(testContents, 0, testContents.length);
		} catch (Exception e) {
			fail("Error calling characters(): " + e.getMessage());
		}
		
		/**
		 * Now send the endElement() call with our ID flag
		 */
		try {
			locationHandler.endElement("uri", "localName", SimplePointLocationHandler.LOCATION_IDENTIFIER);
		} catch (Exception e) {
			fail("Error calling startElement(): " + e.getMessage());
		}
		
		/**
		 * Now send the endElement() call with our START flag again so we can finish
		 * this element
		 */
		try {
			locationHandler.endElement("uri", "localName", SimplePointLocationHandler.LOCATION_START);
		} catch (Exception e) {
			fail("Error calling startElement(): " + e.getMessage());
		}
		
		/**
		 * Since we sent the START flag again as an end element the logic should have
		 * put the SimplePointFeature into the List we passed into the handler 
		 */
		assertEquals(1, this.featureList.size());
		
		FeatureDAO featureDao = this.featureList.get(0);
		assertNotNull(featureDao);
		
		assertEquals("TeStPoInT", featureDao.getBaseAttribute(BaseAttributeType.LocationIdentifier));
		assertEquals("TeStPoInT", featureDao.getFeatureAttribute(FeatureAttributeType.name));
	}
}
