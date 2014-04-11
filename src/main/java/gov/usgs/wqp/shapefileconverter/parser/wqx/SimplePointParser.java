package gov.usgs.wqp.shapefileconverter.parser.wqx;

import gov.usgs.wqp.shapefileconverter.model.FeatureDAO;
import gov.usgs.wqp.shapefileconverter.parser.wqx.handler.SimplePointProviderHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.xerces.parsers.SAXParser;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.xml.sax.SAXException;

/**
 * Java Parser 		- 	Parse Execution Time [0.806693000] seconds for 28,185
 * Xerces Parser 	-	Parse Execution Time [0.726449000] seconds for 28,185
 * 
 * @author prusso
 *
 */


public class SimplePointParser {
	private SAXParser saxParser;
	private SimplePointProviderHandler spHander;
	
	private SimpleFeatureBuilder featureBuilder;
	
	private String inputFile;
	List<FeatureDAO> simplePointFeatures;
	
	public SimplePointParser(String xmlFile, SimpleFeatureBuilder featureBuilder) throws ParserConfigurationException, SAXException, NoSuchAuthorityCodeException, SchemaException, FactoryException {
		this.inputFile = xmlFile;
		this.saxParser = new SAXParser();		
		this.simplePointFeatures = new ArrayList<FeatureDAO>();
		
		this.featureBuilder = featureBuilder;
		
		this.spHander = new SimplePointProviderHandler(this.saxParser, this.simplePointFeatures, this.featureBuilder);
	}
	
	public List<FeatureDAO> parseSimplePointSource() throws SAXException, IOException {
		this.saxParser.setContentHandler(this.spHander);
		this.saxParser.parse(this.inputFile);
		
		return this.simplePointFeatures;
	}
}
