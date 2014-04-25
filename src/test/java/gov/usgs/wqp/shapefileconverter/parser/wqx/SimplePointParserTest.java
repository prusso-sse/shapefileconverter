package gov.usgs.wqp.shapefileconverter.parser.wqx;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import gov.usgs.wqp.shapefileconverter.parser.wqx.handler.SimplePointProviderHandler;

import java.util.List;

import org.apache.xerces.parsers.SAXParser;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SimplePointParserTest {
	private SimpleFeatureBuilder featureBuilder;
	
	@Before
	public void init() throws Exception {
		featureBuilder = mock(SimpleFeatureBuilder.class);
	}
	
	@After
	public void destroy() throws Exception {
		
	}
	
	@Test
	public void testSimplePointParserConstructor() {
		SimplePointParser spp = null;
		
		try {
			spp = new SimplePointParser("testFileName", this.featureBuilder);
		} catch (Exception e) {
			fail("Failed creating SimplePointParser: " + e.getMessage());
		}
		
		assertNotNull(spp.getSaxParser());
		assertThat(spp.getSaxParser(), instanceOf(SAXParser.class));
		
		assertNotNull(spp.getSimplePointFeatures());
		assertThat(spp.getSimplePointFeatures(), instanceOf(List.class));
		
		assertNotNull(spp.getSpHander());
		assertThat(spp.getSpHander(), instanceOf(SimplePointProviderHandler.class));		
	}
}
