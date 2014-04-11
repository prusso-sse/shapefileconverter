package gov.usgs.wqp.shapefileconverter.parser.wqx.handler;

import gov.usgs.wqp.shapefileconverter.model.FeatureDAO;
import gov.usgs.wqp.shapefileconverter.model.features.SimplePointFeature;
import gov.usgs.wqp.shapefileconverter.model.providers.SourceProvider;
import gov.usgs.wqp.shapefileconverter.utils.ShapeFileUtils;

import java.io.CharArrayWriter;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.xerces.parsers.SAXParser;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SimplePointLocationHandler parses the WQX_Outbound XML format for <MonitoringLocation> elements.
 * 
 * The XML format is as follows:
 * 
 * 		<WQX-Outbound xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
 * 		    <Provider>
 * 		        <ProviderName>STEWARDS</ProviderName>
 * 
 * 		        <Organization>
 * 		        	<MonitoringLocation>
 * 		                <MonitoringLocationIdentity>
 * 		                    <MonitoringLocationIdentifier>ARS-IAWC-IAWC225</MonitoringLocationIdentifier>
 * 		                    <ResolvedMonitoringLocationTypeName>Land</ResolvedMonitoringLocationTypeName>
 * 		                </MonitoringLocationIdentity>
 * 		                <MonitoringLocationGeospatial>
 * 		                    <LatitudeMeasure>41.9607224179</LatitudeMeasure>
 * 		                    <LongitudeMeasure>-93.698220503</LongitudeMeasure>
 * 		                </MonitoringLocationGeospatial>
 * 		            </MonitoringLocation>
 * 		            <MonitoringLocation>
 * 		                <MonitoringLocationIdentity>
 * 		                    <MonitoringLocationIdentifier>ARS-IAWC-IAWC410</MonitoringLocationIdentifier>
 * 		                    <ResolvedMonitoringLocationTypeName>Stream</ResolvedMonitoringLocationTypeName>
 * 		                </MonitoringLocationIdentity>
 * 		                <MonitoringLocationGeospatial>
 * 		                    <LatitudeMeasure>41.9505493342</LatitudeMeasure>
 * 		                    <LongitudeMeasure>-93.759072857</LongitudeMeasure>
 * 		                </MonitoringLocationGeospatial>
 * 		            </MonitoringLocation>
 * 		            <MonitoringLocation>
 * 		                <MonitoringLocationIdentity>
 * 		                    <MonitoringLocationIdentifier>ARS-IAWC-IAWC450</MonitoringLocationIdentifier>
 * 		                    <ResolvedMonitoringLocationTypeName>Stream</ResolvedMonitoringLocationTypeName>
 * 		                </MonitoringLocationIdentity>
 * 		                <MonitoringLocationGeospatial>
 * 		                    <LatitudeMeasure>41.9216043545</LatitudeMeasure>
 * 		                    <LongitudeMeasure>-93.756546312</LongitudeMeasure>
 * 		                </MonitoringLocationGeospatial>
 * 		            </MonitoringLocation>
 * 		        </Organization>
 * 
 * 		    </Provider>
 * 		</WQX-Outbound>
 * 
 * 
 * 
 * 
 * @author prusso
 *
 */

public class SimplePointLocationHandler extends DefaultHandler {
	static Logger log = ShapeFileUtils.getLogger(SimplePointLocationHandler.class);
	private List<FeatureDAO> simplePointFeatures;
	private SourceProvider currentSourceProvider;
	private SimplePointFeature currentPointFeature;
	
	private static final String LOCATION_START = "MonitoringLocation";
	private static final String LOCATION_IDENTIFIER = "MonitoringLocationIdentifier";
	private static final String LOCATION_TYPE = "ResolvedMonitoringLocationTypeName";
	private static final String LATTITUDE = "LatitudeMeasure";
	private static final String LONGITUDE = "LongitudeMeasure";
	
	private SimplePointProviderHandler parentHandler;
	private SAXParser xmlReader;
	private CharArrayWriter contents = new CharArrayWriter();
	
	private SimpleFeatureBuilder featureBuilder;
	
	public SimplePointLocationHandler(SimplePointProviderHandler parentHandler, SAXParser xmlReader, List<FeatureDAO> featureList, SourceProvider sourceProvider, SimpleFeatureBuilder featureBuilder) throws SchemaException, NoSuchAuthorityCodeException, FactoryException {
		this.parentHandler = parentHandler;
		this.xmlReader = xmlReader;
		this.simplePointFeatures = featureList;
		this.currentSourceProvider = sourceProvider;
		
		this.featureBuilder = featureBuilder;
	}
	
	public void startDocument() throws SAXException {
		//String msg = "========== SimplePointLocationHandler.startDocument() ==========";
		//System.out.println(msg);
		//log.debug(msg);
	}
	
	public void endDocument() throws SAXException {
		//String msg = "========== SimplePointLocationHandler.endDocument() ==========";
		//System.out.println(msg);
		//log.debug(msg);
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		//String msg = "========== SimplePointLocationHandler.startElement() [" + qName + "] ==========";
		//System.out.println(msg);
		//log.debug(msg);
		contents.reset();
		
		if(SimplePointLocationHandler.LOCATION_START.equals(qName)) {
			this.currentPointFeature = new SimplePointFeature(this.featureBuilder, this.currentSourceProvider);
		}
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		//String msg = "========== SimplePointLocationHandler.endElement() [" + qName + "] ==========";
		//System.out.println(msg);
		//log.debug(msg);
		
		/**
		 * SimplePointFeature name element
		 */
		if(SimplePointLocationHandler.LOCATION_IDENTIFIER.equals(qName)) {
			if(this.currentPointFeature != null) {
				this.currentPointFeature.setName(contents.toString());
			} else {
				String error = "SimplePointLocationHandler.endElement() ERROR: Element name [" +
						  localName + "] found but no SimplePointFeature object created!";
				System.out.println(error);
				log.debug(error);
			}
		}
		
		/**
		 * SimplePointFeature type element
		 */
		if(SimplePointLocationHandler.LOCATION_TYPE.equals(qName)) {
			if(this.currentPointFeature != null) {
				this.currentPointFeature.setType(contents.toString());
			} else {
				String error = "SimplePointLocationHandler.endElement() ERROR: Element name [" +
						  localName + "] found but no SimplePointFeature object created!";
				System.out.println(error);
				log.debug(error);
			}
		}
		
		/**
		 * SimplePointFeature latitude element
		 */
		if(SimplePointLocationHandler.LATTITUDE.equals(qName)) {
			if(this.currentPointFeature != null) {
				double value = 0.0;
				String stringValue = contents.toString();
				try {
					value = Double.parseDouble(stringValue);
				} catch (NumberFormatException e) {
					String error = "SimplePointLocationHandler.endElement() ERROR: Latitude value [" + stringValue +
							  "] could not be parsed as a double value.  Setting latitude to 0.0";
					System.out.println(error);
					log.debug(error);
				} catch (NullPointerException e) {
					String error = "SimplePointLocationHandler.endElement() ERROR: Latitude value is null and " +
							  "could not be parsed as a double value.  Setting latitude to 0.0";
					System.out.println(error);
					log.debug(error);
				}				
				
				this.currentPointFeature.setLatitude(value);
			} else {
				String error = "SimplePointLocationHandler.endElement() ERROR: Element name [" +
						  localName + "] found but no SimplePointFeature object created!";
				System.out.println(error);
				log.debug(error);
			}
		}
		
		/**
		 * SimplePointFeature longitude element
		 */
		if(SimplePointLocationHandler.LONGITUDE.equals(qName)) {
			if(this.currentPointFeature != null) {
				double value = 0.0;
				String stringValue = contents.toString();
				try {
					value = Double.parseDouble(stringValue);
				} catch (NumberFormatException e) {
					String error = "SimplePointLocationHandler.endElement() ERROR: Longitude value [" + stringValue +
							  "] could not be parsed as a double value.  Setting longitude to 0.0";
					System.out.println(error);
					log.debug(error);
				} catch (NullPointerException e) {
					String error = "SimplePointLocationHandler.endElement() ERROR: Longitude value is null and " +
							  "could not be parsed as a double value.  Setting longitude to 0.0";
					System.out.println(error);
					log.debug(error);
				}				
				
				this.currentPointFeature.setLongitude(value);
			} else {
				String error = "SimplePointLocationHandler.endElement() ERROR: Element name [" +
						  localName + "] found but no SimplePointFeature object created!";
				System.out.println(error);
				log.debug(error);
			}
		}
		
		/**
		 * SimplePointFeature name element
		 */
		if(SimplePointLocationHandler.LOCATION_IDENTIFIER.equals(qName)) {
			if(this.currentPointFeature != null) {
				this.currentPointFeature.setName(contents.toString());
			} else {
				String error = "SimplePointLocationHandler.endElement() ERROR: Element name [" +
						  localName + "] found but no SimplePointFeature object created!";
				System.out.println(error);
				log.debug(error);
			}
		}
		
		/**
		 * The ending tag for this feature
		 */
		if(SimplePointLocationHandler.LOCATION_START.equals(qName)) {
			this.simplePointFeatures.add(this.currentPointFeature);
		}
		
		/**
		 * Tag flag to return handling to the parent
		 */
		if(SimplePointProviderHandler.SUBHANDLER_ELEMENT.equals(qName)) {
			this.xmlReader.setContentHandler(this.parentHandler);
			return;
		}
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException {
		contents.write(ch, start, length);
	}
}
