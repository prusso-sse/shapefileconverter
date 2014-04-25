package gov.usgs.wqp.shapefileconverter.model.features;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.usgs.wqp.shapefileconverter.model.attributes.BaseAttributeType;
import gov.usgs.wqp.shapefileconverter.model.attributes.FeatureAttributeType;
import gov.usgs.wqp.shapefileconverter.model.providers.SourceProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class SimplePointFeatureTest {
	/**
	 * Used Classes
	 */
	private SimpleFeatureBuilder usedFeatureBuilder;
	private SimpleFeatureType usedFeatureType;
	private GeometryFactory geometryFactory;
	
	/**
	 * Default Values
	 */
	private List<BaseAttributeType> defaultBaseAttribs;
	private List<FeatureAttributeType> defaultFeatureAttribs;
	
	@Before
	public void init() throws Exception {		
		/**
		 * Create necessary support classes
		 */
		this.geometryFactory = JTSFactoryFinder.getGeometryFactory();
		
		/**
		 * Create testable values
		 */
		this.usedFeatureType = SimplePointFeature.getFeatureType();
		this.usedFeatureBuilder = new SimpleFeatureBuilder(this.usedFeatureType);
		this.defaultBaseAttribs = new ArrayList<BaseAttributeType>(Arrays.asList(BaseAttributeType.Provider, BaseAttributeType.LocationIdentifier,
																				BaseAttributeType.LocationType, BaseAttributeType.Longitude,
																				BaseAttributeType.Latitude));
		this.defaultFeatureAttribs = new ArrayList<FeatureAttributeType>(Arrays.asList(FeatureAttributeType.provider, FeatureAttributeType.name,
																					   FeatureAttributeType.type, FeatureAttributeType.point));
	}
	
	@After
	public void destroy() throws Exception {
		
	}
	
	@Test
	public void testSimplePointFeatureSimpleConstructor() {
		/**
		 * Instantiate used values
		 */
		Point defaultPoint = this.geometryFactory.createPoint(new Coordinate(0, 0));
		
		/**
		 * Start our tests
		 * 
		 * Create our SimplePointFeature
		 */
		SimplePointFeature currentPointFeature = new SimplePointFeature(this.usedFeatureBuilder, SourceProvider.UNKNOWN);
		
		/**
		 * Check typical feature values
		 */
		assertEquals(0, currentPointFeature.getLatitude(), 0);		
		assertEquals(0, currentPointFeature.getLongitude(), 0);
		assertEquals("", currentPointFeature.getName());
		assertEquals(defaultPoint, currentPointFeature.getPoint());
		assertEquals(SourceProvider.UNKNOWN, currentPointFeature.getProvider());
		assertEquals("", currentPointFeature.getType());
		
		/**
		 * Check complex feature BaseAttributeType datastructure
		 */
		List<BaseAttributeType> baseTypes = currentPointFeature.listBaseAttributes();
		for(BaseAttributeType baseType : this.defaultBaseAttribs) {
			assertTrue(baseTypes.contains(baseType));
		}
		assertEquals(SourceProvider.getStringFromType(SourceProvider.UNKNOWN), currentPointFeature.getBaseAttribute(BaseAttributeType.Provider));
		assertEquals("", currentPointFeature.getBaseAttribute(BaseAttributeType.LocationType));
		assertEquals("", currentPointFeature.getBaseAttribute(BaseAttributeType.LocationIdentifier));
		assertEquals(Double.toString(0), currentPointFeature.getBaseAttribute(BaseAttributeType.Longitude));
		assertEquals(Double.toString(0), currentPointFeature.getBaseAttribute(BaseAttributeType.Latitude));
		
		
		List<FeatureAttributeType> featureTypes = currentPointFeature.listFeatureAttributes();
		for(FeatureAttributeType featureType : this.defaultFeatureAttribs) {
			assertTrue(featureTypes.contains(featureType));
		}
		assertEquals("", currentPointFeature.getFeatureAttribute(FeatureAttributeType.name));
		assertEquals("", currentPointFeature.getFeatureAttribute(FeatureAttributeType.type));
		assertEquals(SourceProvider.getStringFromType(SourceProvider.UNKNOWN), currentPointFeature.getFeatureAttribute(FeatureAttributeType.provider));
		assertEquals(defaultPoint, currentPointFeature.getFeatureAttribute(FeatureAttributeType.point));
		
		/**
		 * Get the feature type
		 */
		SimpleFeatureType featureType = null;
		try {
			featureType = SimplePointFeature.getFeatureType();
		} catch (Exception e) {
			fail("Failed requesting featureType: " + e.getMessage());
		}
		
		/**
		 * Check that feature type is basic one we know needs to be set up
		 */
		assertEquals("Location", featureType.getName().getLocalPart());
		assertEquals("the_geom", featureType.getGeometryDescriptor().getName().getLocalPart());
		
		/**
		 * Check that the feature is dirty (logic says its dirty whenever a value
		 * changes in the feature OR before any call to getSimpleFeature();)
		 */
		assertTrue(currentPointFeature.featureIsDirty());
		
		/**
		 * Get the simpleFeature
		 */
		SimpleFeature generatedFeature = currentPointFeature.getSimpleFeature();	
		
		/**
		 * Check that our isDirty flag has gone away.
		 */
		assertFalse(currentPointFeature.featureIsDirty());
		
		/**
		 * Now check the actual SimpleFeature created
		 * 
		 * 		Attribute Name 		= 	"name"
		 * 		Attribute Point 	= 	"the_geom"
		 * 		Attribute Provider 	= 	"provider"
		 * 		Attribute Type 		= 	"type"
		 * 
		 */
		Object theName = generatedFeature.getAttribute("name");
		assertEquals(theName.getClass(), String.class);
		assertEquals("", (String)theName);
		
		Object theGeom = generatedFeature.getAttribute("the_geom");
		assertEquals(theGeom.getClass(), Point.class);
		assertEquals(defaultPoint, theGeom);
		
		Object theProvider = generatedFeature.getAttribute("provider");
		assertEquals(theProvider.getClass(), String.class);
		assertEquals(SourceProvider.getStringFromType(SourceProvider.UNKNOWN), (String)theProvider);
		
		Object theType = generatedFeature.getAttribute("type");
		assertEquals(theType.getClass(), String.class);
		assertEquals("", (String)theType);
	}
	
	@Test
	public void testSimplePointFeatureComplexConstructor() {
		/**
		 * Instantiate used values
		 */
		SourceProvider defaultProvider = SourceProvider.NWIS;
		String defaultName = "Feature Name";
		String defaultType =  "My Type";
		double defaultLong = -91;
		double defaultLat = 85;
		Point defaultPoint = this.geometryFactory.createPoint(new Coordinate(defaultLong, defaultLat));
		
		/**
		 * Start our tests
		 * 
		 * Create our SimplePointFeature
		 */
		SimplePointFeature currentPointFeature = new SimplePointFeature(this.usedFeatureBuilder, defaultProvider, defaultName, defaultType, defaultLong, defaultLat);
		
		/**
		 * Check typical feature values
		 */
		assertEquals(defaultLat, currentPointFeature.getLatitude(), 0);
		assertEquals(defaultLong, currentPointFeature.getLongitude(), 0);
		assertEquals(defaultName, currentPointFeature.getName());
		assertEquals(defaultPoint, currentPointFeature.getPoint());
		assertEquals(defaultProvider, currentPointFeature.getProvider());
		assertEquals(defaultType, currentPointFeature.getType());
		
		/**
		 * Check complex feature BaseAttributeType datastructure
		 */
		List<BaseAttributeType> baseTypes = currentPointFeature.listBaseAttributes();
		for(BaseAttributeType baseType : this.defaultBaseAttribs) {
			assertTrue(baseTypes.contains(baseType));
		}
		assertEquals(SourceProvider.getStringFromType(defaultProvider), currentPointFeature.getBaseAttribute(BaseAttributeType.Provider));
		assertEquals(defaultType, currentPointFeature.getBaseAttribute(BaseAttributeType.LocationType));
		assertEquals(defaultName, currentPointFeature.getBaseAttribute(BaseAttributeType.LocationIdentifier));
		assertEquals(Double.toString(defaultLong), currentPointFeature.getBaseAttribute(BaseAttributeType.Longitude));
		assertEquals(Double.toString(defaultLat), currentPointFeature.getBaseAttribute(BaseAttributeType.Latitude));
		
		
		List<FeatureAttributeType> featureTypes = currentPointFeature.listFeatureAttributes();
		for(FeatureAttributeType featureType : this.defaultFeatureAttribs) {
			assertTrue(featureTypes.contains(featureType));
		}
		assertEquals(defaultName, currentPointFeature.getFeatureAttribute(FeatureAttributeType.name));
		assertEquals(defaultType, currentPointFeature.getFeatureAttribute(FeatureAttributeType.type));
		assertEquals(SourceProvider.getStringFromType(defaultProvider), currentPointFeature.getFeatureAttribute(FeatureAttributeType.provider));
		assertEquals(defaultPoint, currentPointFeature.getFeatureAttribute(FeatureAttributeType.point));
		
		/**
		 * Get the feature type
		 */
		SimpleFeatureType featureType = null;
		try {
			featureType = SimplePointFeature.getFeatureType();
		} catch (Exception e) {
			fail("Failed requesting featureType: " + e.getMessage());
		}
		
		/**
		 * Check that feature type is basic one we know needs to be set up
		 */
		assertEquals("Location", featureType.getName().getLocalPart());
		assertEquals("the_geom", featureType.getGeometryDescriptor().getName().getLocalPart());
		
		/**
		 * Check that the feature is dirty (logic says its dirty whenever a value
		 * changes in the feature OR before any call to getSimpleFeature();)
		 */
		assertTrue(currentPointFeature.featureIsDirty());
		
		/**
		 * Get the simpleFeature
		 */
		SimpleFeature generatedFeature = currentPointFeature.getSimpleFeature();
		
		/**
		 * Check that our isDirty flag has gone away.
		 */
		assertFalse(currentPointFeature.featureIsDirty());
		
		/**
		 * Now check the actual SimpleFeature created
		 * 
		 * 		Attribute Name 		= 	"name"
		 * 		Attribute Point 	= 	"the_geom"
		 * 		Attribute Provider 	= 	"provider"
		 * 		Attribute Type 		= 	"type"
		 * 
		 */
		Object theName = generatedFeature.getAttribute("name");
		assertEquals(theName.getClass(), String.class);
		assertEquals(defaultName, (String)theName);
		
		Object theGeom = generatedFeature.getAttribute("the_geom");
		assertEquals(theGeom.getClass(), Point.class);
		assertEquals(defaultPoint, theGeom);
		
		Object theProvider = generatedFeature.getAttribute("provider");
		assertEquals(theProvider.getClass(), String.class);
		assertEquals(SourceProvider.getStringFromType(defaultProvider), (String)theProvider);
		
		Object theType = generatedFeature.getAttribute("type");
		assertEquals(theType.getClass(), String.class);
		assertEquals(defaultType, (String)theType);
	}
	
	@Test
	public void testSimplePointFeatureChangeName() {
		/**
		 * Instantiate used values
		 */
		String provider = SourceProvider.getStringFromType(SourceProvider.NWIS);
		String testName = "TestName";
		String testType = "TestType";
		double testLong = -81;
		double testLat = 55;
		Point defaultPoint = this.geometryFactory.createPoint(new Coordinate(testLong, testLat));
		
		/**
		 * Create our SimplePointFeature
		 */
		SimplePointFeature testableFeature = new SimplePointFeature(this.usedFeatureBuilder, SourceProvider.NWIS, testName, testType, testLong, testLat);
		
		/**
		 * Test our name
		 */
		assertEquals(testName, testableFeature.getName());
		assertEquals(testName, testableFeature.getBaseAttribute(BaseAttributeType.LocationIdentifier));
		assertEquals(testName, testableFeature.getFeatureAttribute(FeatureAttributeType.name));
		
		/**
		 * Test that its dirty
		 */
		assertTrue(testableFeature.featureIsDirty());
		
		/**
		 * Get the simpleFeature
		 */
		SimpleFeature initialFeature = testableFeature.getSimpleFeature();
		
		/**
		 * Check that our initialFeature is the right one
		 */
		Object initialName = initialFeature.getAttribute("name");
		assertEquals(initialName.getClass(), String.class);
		assertEquals(testName, (String)initialName);
		
		/**
		 * Check that our isDirty flag has gone away.
		 */
		assertFalse(testableFeature.featureIsDirty());
		
		/**
		 * Change our feature's name
		 */
		String newName = "New Name";
		testableFeature.setName(newName);
		
		/**
		 * Test we have the new name
		 */
		assertEquals(newName, testableFeature.getName());
		assertEquals(newName, testableFeature.getBaseAttribute(BaseAttributeType.LocationIdentifier));
		assertEquals(newName, testableFeature.getFeatureAttribute(FeatureAttributeType.name));
		
		/**
		 * Make sure the feature is now dirty
		 */
		assertTrue(testableFeature.featureIsDirty());
		
		/**
		 * Now check the actual SimpleFeature created
		 * 
		 * 		Attribute Name 		= 	"name"
		 * 		Attribute Point 	= 	"the_geom"
		 * 		Attribute Provider 	= 	"provider"
		 * 		Attribute Type 		= 	"type"
		 * 
		 */
		SimpleFeature generatedFeature = testableFeature.getSimpleFeature();
		
		Object theName = generatedFeature.getAttribute("name");
		assertEquals(theName.getClass(), String.class);
		assertEquals(newName, (String)theName);
		
		Object theGeom = generatedFeature.getAttribute("the_geom");
		assertEquals(theGeom.getClass(), Point.class);
		assertEquals(defaultPoint, theGeom);
		
		Object theProvider = generatedFeature.getAttribute("provider");
		assertEquals(theProvider.getClass(), String.class);
		assertEquals(provider, (String)theProvider);
		
		Object theType = generatedFeature.getAttribute("type");
		assertEquals(theType.getClass(), String.class);
		assertEquals(testType, (String)theType);
	}
	
	@Test
	public void testSimplePointFeatureChangeType() {
		/**
		 * Instantiate used values
		 */
		String provider = SourceProvider.getStringFromType(SourceProvider.NWIS);
		String testName = "TestName";
		String testType = "TestType";
		double testLong = -81;
		double testLat = 55;
		Point defaultPoint = this.geometryFactory.createPoint(new Coordinate(testLong, testLat));
		
		/**
		 * Create our SimplePointFeature
		 */
		SimplePointFeature testableFeature = new SimplePointFeature(this.usedFeatureBuilder, SourceProvider.NWIS, testName, testType, testLong, testLat);
		
		/**
		 * Test our type
		 */
		assertEquals(testType, testableFeature.getType());
		assertEquals(testType, testableFeature.getBaseAttribute(BaseAttributeType.LocationType));
		assertEquals(testType, testableFeature.getFeatureAttribute(FeatureAttributeType.type));
		
		/**
		 * Test that its dirty
		 */
		assertTrue(testableFeature.featureIsDirty());
		
		/**
		 * Get the simpleFeature
		 */
		SimpleFeature initialFeature = testableFeature.getSimpleFeature();
		
		/**
		 * Check that our initialFeature is the right one
		 */
		Object initialName = initialFeature.getAttribute("name");
		assertEquals(initialName.getClass(), String.class);
		assertEquals(testName, (String)initialName);	
		
		/**
		 * Check that our isDirty flag has gone away.
		 */
		assertFalse(testableFeature.featureIsDirty());
		
		/**
		 * Change our feature's type
		 */
		String newType = "New Type";
		testableFeature.setType(newType);
		
		/**
		 * Test we have the new type
		 */
		assertEquals(newType, testableFeature.getType());
		assertEquals(newType, testableFeature.getBaseAttribute(BaseAttributeType.LocationType));
		assertEquals(newType, testableFeature.getFeatureAttribute(FeatureAttributeType.type));
		
		/**
		 * Make sure the feature is now dirty
		 */
		assertTrue(testableFeature.featureIsDirty());
		
		/**
		 * Now check the actual SimpleFeature created
		 * 
		 * 		Attribute Name 		= 	"name"
		 * 		Attribute Point 	= 	"the_geom"
		 * 		Attribute Provider 	= 	"provider"
		 * 		Attribute Type 		= 	"type"
		 * 
		 */
		SimpleFeature generatedFeature = testableFeature.getSimpleFeature();
		
		Object theName = generatedFeature.getAttribute("name");
		assertEquals(theName.getClass(), String.class);
		assertEquals(testName, (String)theName);
		
		Object theGeom = generatedFeature.getAttribute("the_geom");
		assertEquals(theGeom.getClass(), Point.class);
		assertEquals(defaultPoint, theGeom);
		
		Object theProvider = generatedFeature.getAttribute("provider");
		assertEquals(theProvider.getClass(), String.class);
		assertEquals(provider, (String)theProvider);
		
		Object theType = generatedFeature.getAttribute("type");
		assertEquals(theType.getClass(), String.class);
		assertEquals(newType, (String)theType);
	}
	
	@Test
	public void testSimplePointFeatureChangeLong() {
		/**
		 * Instantiate used values
		 */
		String provider = SourceProvider.getStringFromType(SourceProvider.NWIS);
		String testName = "TestName";
		String testType = "TestType";
		double testLong = -81;
		double testLat = 55;
		Point defaultPoint = this.geometryFactory.createPoint(new Coordinate(testLong, testLat));
		
		/**
		 * Create our SimplePointFeature
		 */
		SimplePointFeature testableFeature = new SimplePointFeature(this.usedFeatureBuilder, SourceProvider.NWIS, testName, testType, testLong, testLat);
		
		/**
		 * Test our long
		 */
		assertEquals(testLong, testableFeature.getLongitude(), 0);
		assertEquals(Double.toString(testLong), testableFeature.getBaseAttribute(BaseAttributeType.Longitude));
		
		/**
		 * Test our Point
		 */
		assertEquals(defaultPoint, testableFeature.getPoint());
		assertEquals(defaultPoint, testableFeature.getFeatureAttribute(FeatureAttributeType.point));
		
		/**
		 * Test that its dirty
		 */
		assertTrue(testableFeature.featureIsDirty());
		
		/**
		 * Get the simpleFeature
		 */
		SimpleFeature initialFeature = testableFeature.getSimpleFeature();
		
		/**
		 * Check that our initialFeature is the right one
		 */
		Object initialName = initialFeature.getAttribute("name");
		assertEquals(initialName.getClass(), String.class);
		assertEquals(testName, (String)initialName);	
		
		/**
		 * Check that our isDirty flag has gone away.
		 */
		assertFalse(testableFeature.featureIsDirty());
		
		/**
		 * Change our feature's longitue
		 */
		double newLong = -33;
		testableFeature.setLongitude(newLong);
		
		/**
		 * Test we have the new longitude
		 */
		assertEquals(newLong, testableFeature.getLongitude(), 0);
		assertEquals(Double.toString(newLong), testableFeature.getBaseAttribute(BaseAttributeType.Longitude));
		
		/**
		 * Test our new Point
		 */
		Point newPoint = this.geometryFactory.createPoint(new Coordinate(newLong, testLat));
		assertEquals(newPoint, testableFeature.getPoint());	
		assertEquals(newPoint, testableFeature.getFeatureAttribute(FeatureAttributeType.point));
		
		/**
		 * Make sure the feature is now dirty
		 */
		assertTrue(testableFeature.featureIsDirty());
		
		/**
		 * Make sure the feature is now dirty
		 */
		assertTrue(testableFeature.featureIsDirty());
		
		/**
		 * Now check the actual SimpleFeature created
		 * 
		 * 		Attribute Name 		= 	"name"
		 * 		Attribute Point 	= 	"the_geom"
		 * 		Attribute Provider 	= 	"provider"
		 * 		Attribute Type 		= 	"type"
		 * 
		 */
		SimpleFeature generatedFeature = testableFeature.getSimpleFeature();
		
		Object theName = generatedFeature.getAttribute("name");
		assertEquals(theName.getClass(), String.class);
		assertEquals(testName, (String)theName);
		
		Object theGeom = generatedFeature.getAttribute("the_geom");
		assertEquals(theGeom.getClass(), Point.class);
		assertEquals(newPoint, theGeom);
		
		Object theProvider = generatedFeature.getAttribute("provider");
		assertEquals(theProvider.getClass(), String.class);
		assertEquals(provider, (String)theProvider);
		
		Object theType = generatedFeature.getAttribute("type");
		assertEquals(theType.getClass(), String.class);
		assertEquals(testType, (String)theType);
	}
	
	@Test
	public void testSimplePointFeatureChangeLat() {
		/**
		 * Instantiate used values
		 */
		String provider = SourceProvider.getStringFromType(SourceProvider.NWIS);
		String testName = "TestName";
		String testType = "TestType";
		double testLong = -81;
		double testLat = 55;
		Point defaultPoint = this.geometryFactory.createPoint(new Coordinate(testLong, testLat));
			
		/**
		 * Create our SimplePointFeature
		 */
		SimplePointFeature testableFeature = new SimplePointFeature(this.usedFeatureBuilder, SourceProvider.NWIS, testName, testType, testLong, testLat);
		
		/**
		 * Test our lat
		 */
		assertEquals(testLat, testableFeature.getLatitude(), 0);
		assertEquals(Double.toString(testLat), testableFeature.getBaseAttribute(BaseAttributeType.Latitude));
		
		/**
		 * Test our Point
		 */
		assertEquals(defaultPoint, testableFeature.getPoint());	
		assertEquals(defaultPoint, testableFeature.getFeatureAttribute(FeatureAttributeType.point));	
		
		/**
		 * Test that its dirty
		 */
		assertTrue(testableFeature.featureIsDirty());
		
		/**
		 * Get the simpleFeature
		 */
		SimpleFeature initialFeature = testableFeature.getSimpleFeature();
		
		/**
		 * Check that our initialFeature is the right one
		 */
		Object initialName = initialFeature.getAttribute("name");
		assertEquals(initialName.getClass(), String.class);
		assertEquals(testName, (String)initialName);		
		
		/**
		 * Check that our isDirty flag has gone away.
		 */
		assertFalse(testableFeature.featureIsDirty());
		
		/**
		 * Change our feature's latitude
		 */
		double newLat = 67;
		testableFeature.setLatitude(newLat);
		
		/**
		 * Test we have the new latitude
		 */
		assertEquals(newLat, testableFeature.getLatitude(), 0);
		assertEquals(Double.toString(newLat), testableFeature.getBaseAttribute(BaseAttributeType.Latitude));
		
		/**
		 * Test our new Point
		 */
		Point newPoint = this.geometryFactory.createPoint(new Coordinate(testLong, newLat));
		assertEquals(newPoint, testableFeature.getPoint());	
		assertEquals(newPoint, testableFeature.getFeatureAttribute(FeatureAttributeType.point));
		
		/**
		 * Make sure the feature is now dirty
		 */
		assertTrue(testableFeature.featureIsDirty());
		
		/**
		 * Now check the actual SimpleFeature created
		 * 
		 * 		Attribute Name 		= 	"name"
		 * 		Attribute Point 	= 	"the_geom"
		 * 		Attribute Provider 	= 	"provider"
		 * 		Attribute Type 		= 	"type"
		 * 
		 */
		SimpleFeature generatedFeature = testableFeature.getSimpleFeature();
		
		Object theName = generatedFeature.getAttribute("name");
		assertEquals(theName.getClass(), String.class);
		assertEquals(testName, (String)theName);
		
		Object theGeom = generatedFeature.getAttribute("the_geom");
		assertEquals(theGeom.getClass(), Point.class);
		assertEquals(newPoint, theGeom);
		
		Object theProvider = generatedFeature.getAttribute("provider");
		assertEquals(theProvider.getClass(), String.class);
		assertEquals(provider, (String)theProvider);
		
		Object theType = generatedFeature.getAttribute("type");
		assertEquals(theType.getClass(), String.class);
		assertEquals(testType, (String)theType);
	}
	
	@Test
	public void testSimplePointFeatureChangePoint() {
		/**
		 * Instantiate used values
		 */
		String provider = SourceProvider.getStringFromType(SourceProvider.NWIS);
		String testName = "TestName";
		String testType = "TestType";
		double testLong = -81;
		double testLat = 55;
		Point defaultPoint = this.geometryFactory.createPoint(new Coordinate(testLong, testLat));
		
		/**
		 * Create our SimplePointFeature
		 */
		SimplePointFeature testableFeature = new SimplePointFeature(this.usedFeatureBuilder, SourceProvider.NWIS, testName, testType, testLong, testLat);
				
		/**
		 * Test our Point
		 */
		assertEquals(defaultPoint, testableFeature.getPoint());
		assertEquals(defaultPoint, testableFeature.getFeatureAttribute(FeatureAttributeType.point));		
		
		/**
		 * Test that its dirty
		 */
		assertTrue(testableFeature.featureIsDirty());
		
		/**
		 * Get the simpleFeature
		 */
		SimpleFeature initialFeature = testableFeature.getSimpleFeature();
		
		/**
		 * Check that our initialFeature is the right one
		 */
		Object initialName = initialFeature.getAttribute("name");
		assertEquals(initialName.getClass(), String.class);
		assertEquals(testName, (String)initialName);	
		
		/**
		 * Check that our isDirty flag has gone away.
		 */
		assertFalse(testableFeature.featureIsDirty());
		
		/**
		 * Change our feature's longitude and latitude
		 * by setting the point
		 */
		double newLong = -33;
		double newLat = 67;
		Point newPoint = this.geometryFactory.createPoint(new Coordinate(newLong, newLat));
		testableFeature.setPoint(newLong, newLat);
		
		/**
		 * Test we have the new longitude
		 */
		assertEquals(newLong, testableFeature.getLongitude(), 0);
		assertEquals(Double.toString(newLong), testableFeature.getBaseAttribute(BaseAttributeType.Longitude));
		
		/**
		 * Test we have the new latitude
		 */
		assertEquals(newLat, testableFeature.getLatitude(), 0);
		assertEquals(Double.toString(newLat), testableFeature.getBaseAttribute(BaseAttributeType.Latitude));
		
		/**
		 * Test our new Point
		 */
		assertEquals(newPoint, testableFeature.getPoint());	
		assertEquals(newPoint, testableFeature.getFeatureAttribute(FeatureAttributeType.point));
		
		/**
		 * Make sure the feature is now dirty
		 */
		assertTrue(testableFeature.featureIsDirty());
		
		/**
		 * Now check the actual SimpleFeature created
		 * 
		 * 		Attribute Name 		= 	"name"
		 * 		Attribute Point 	= 	"the_geom"
		 * 		Attribute Provider 	= 	"provider"
		 * 		Attribute Type 		= 	"type"
		 * 
		 */
		SimpleFeature generatedFeature = testableFeature.getSimpleFeature();
		
		Object theName = generatedFeature.getAttribute("name");
		assertEquals(theName.getClass(), String.class);
		assertEquals(testName, (String)theName);
		
		Object theGeom = generatedFeature.getAttribute("the_geom");
		assertEquals(theGeom.getClass(), Point.class);
		assertEquals(newPoint, theGeom);
		
		Object theProvider = generatedFeature.getAttribute("provider");
		assertEquals(theProvider.getClass(), String.class);
		assertEquals(provider, (String)theProvider);
		
		Object theType = generatedFeature.getAttribute("type");
		assertEquals(theType.getClass(), String.class);
		assertEquals(testType, (String)theType);
	}
	
	@Test
	public void testSimplePointFeatureChangeProvider() {
		/**
		 * Instantiate used values
		 */
		String provider = SourceProvider.getStringFromType(SourceProvider.NWIS);
		String testName = "TestName";
		String testType = "TestType";
		double testLong = -81;
		double testLat = 55;
		Point defaultPoint = this.geometryFactory.createPoint(new Coordinate(testLong, testLat));
		
		/**
		 * Create our SimplePointFeature
		 */
		SimplePointFeature testableFeature = new SimplePointFeature(this.usedFeatureBuilder, SourceProvider.NWIS, testName, testType, testLong, testLat);
		
		/**
		 * Test our provider
		 */
		assertEquals(provider, SourceProvider.getStringFromType(testableFeature.getProvider()));
		assertEquals(provider, testableFeature.getBaseAttribute(BaseAttributeType.Provider));
		assertEquals(provider, testableFeature.getFeatureAttribute(FeatureAttributeType.provider));
		
		/**
		 * Test that its dirty
		 */
		assertTrue(testableFeature.featureIsDirty());
		
		/**
		 * Get the simpleFeature
		 */
		SimpleFeature initialFeature = testableFeature.getSimpleFeature();
		
		/**
		 * Check that our initialFeature is the right one
		 */
		Object initialName = initialFeature.getAttribute("name");
		assertEquals(initialName.getClass(), String.class);
		assertEquals(testName, (String)initialName);	
		
		/**
		 * Check that our isDirty flag has gone away.
		 */
		assertFalse(testableFeature.featureIsDirty());
		
		/**
		 * Change our feature's type
		 */
		String newProvider = SourceProvider.getStringFromType(SourceProvider.STEWARDS);
		testableFeature.setProvider(SourceProvider.STEWARDS);
		
		/**
		 * Test we have the new type
		 */
		assertEquals(newProvider, SourceProvider.getStringFromType(testableFeature.getProvider()));
		assertEquals(newProvider, testableFeature.getBaseAttribute(BaseAttributeType.Provider));
		assertEquals(newProvider, testableFeature.getFeatureAttribute(FeatureAttributeType.provider));
		
		/**
		 * Make sure the feature is now dirty
		 */
		assertTrue(testableFeature.featureIsDirty());
		
		/**
		 * Now check the actual SimpleFeature created
		 * 
		 * 		Attribute Name 		= 	"name"
		 * 		Attribute Point 	= 	"the_geom"
		 * 		Attribute Provider 	= 	"provider"
		 * 		Attribute Type 		= 	"type"
		 * 
		 */
		SimpleFeature generatedFeature = testableFeature.getSimpleFeature();
		
		Object theName = generatedFeature.getAttribute("name");
		assertEquals(theName.getClass(), String.class);
		assertEquals(testName, (String)theName);
		
		Object theGeom = generatedFeature.getAttribute("the_geom");
		assertEquals(theGeom.getClass(), Point.class);
		assertEquals(defaultPoint, theGeom);
		
		Object theProvider = generatedFeature.getAttribute("provider");
		assertEquals(theProvider.getClass(), String.class);
		assertEquals(newProvider, (String)theProvider);
		
		Object theType = generatedFeature.getAttribute("type");
		assertEquals(theType.getClass(), String.class);
		assertEquals(testType, (String)theType);
	}
}
