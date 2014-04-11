package gov.usgs.wqp.shapefileconverter.parser.wqx.handler;

import gov.usgs.wqp.shapefileconverter.model.FeatureDAO;
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
 * SimplePointProviderHandler parses the WQX_Outbound XML format for <Provider> elements.
 * 
 * The XML format is as follows:
 * 
 * 		<WQX-Outbound xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
 * 		    <Metadata>
 * 		        <WebServiceURL>http://wqp-test.er.usgs.gov/Station/search?statecode=US%3A19&amp;countycode=US%3A19%3A015&amp;sampleMedia=Water&amp;startDateLo=01-01-2009&amp;providers=NWIS&amp;providers=STEWARDS&amp;mimeType=xml&amp;zip=yes</WebServiceURL>
 * 		        <Time>2014-04-04T13:07:37+00:00</Time>
 * 		    </Metadata>
 * 		    <Provider>
 * 		        <ProviderName>NWIS</ProviderName>
 * 		        <Organization>
 * 		            <MonitoringLocation>
 * 		                <MonitoringLocationIdentity>
 * 		                    <MonitoringLocationIdentifier>USGS-420158093562001</MonitoringLocationIdentifier>
 * 		                    <ResolvedMonitoringLocationTypeName>Well</ResolvedMonitoringLocationTypeName>
 * 		                </MonitoringLocationIdentity>
 * 		                <MonitoringLocationGeospatial>
 * 		                    <LatitudeMeasure>42.0327602</LatitudeMeasure>
 * 		                    <LongitudeMeasure>-93.9391185</LongitudeMeasure>
 * 		                </MonitoringLocationGeospatial>
 * 		            </MonitoringLocation>
 * 		        </Organization>
 * 		    </Provider>
 * 		    <Provider>
 * 		        <ProviderName>STEWARDS</ProviderName>
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
 * 		    </Provider>
 * 		    <Provider>
 * 		        <ProviderName>STORET</ProviderName>
 * 		        <Organization>
 * 		            <MonitoringLocation>
 * 		                <MonitoringLocationIdentity>
 * 		                    <MonitoringLocationIdentifier>USGS-420158093562001</MonitoringLocationIdentifier>
 * 		                    <ResolvedMonitoringLocationTypeName>Well</ResolvedMonitoringLocationTypeName>
 * 		                </MonitoringLocationIdentity>
 * 		                <MonitoringLocationGeospatial>
 * 		                    <LatitudeMeasure>42.0327602</LatitudeMeasure>
 * 		                    <LongitudeMeasure>-93.9391185</LongitudeMeasure>
 * 		                </MonitoringLocationGeospatial>
 * 		            </MonitoringLocation>
 * 		        </Organization>
 * 		    </Provider>
 * 		</WQX-Outbound>
 * 
 * 
 * 
 * 
 * @author prusso
 *
 */

public class SimplePointProviderHandler extends DefaultHandler {
	static Logger log = ShapeFileUtils.getLogger(SimplePointProviderHandler.class);
	private List<FeatureDAO> simplePointFeatures;
	
	public static final String SUBHANDLER_ELEMENT = "Organization";
	private static final String PROVIDER_NAME_ELEMENT = "ProviderName";
	
	private SourceProvider currentProvider;
	
	private SimpleFeatureBuilder featureBuilder;
	
	private SAXParser xmlReader;
	private CharArrayWriter contents = new CharArrayWriter();
	
	public SimplePointProviderHandler(SAXParser xmlReader, List<FeatureDAO> featureList, SimpleFeatureBuilder featureBuilder) {
		this.xmlReader = xmlReader;
		this.simplePointFeatures = featureList;
		this.featureBuilder = featureBuilder;
		this.currentProvider = SourceProvider.UNKNOWN;
	}
	
	public void startDocument() throws SAXException {
		//String msg = "========== SimplePointProviderHandler.startDocument() ==========";
		//System.out.println(msg);
		//log.debug(msg);
	}
	
	public void endDocument() throws SAXException {
		//String msg = "========== SimplePointProviderHandler.endDocument() ==========";
		//System.out.println(msg);
		//log.debug(msg);
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		//String msg = "========== SimplePointProviderHandler.startElement() [" + qName + "] ==========";
		//System.out.println(msg);
		//log.debug(msg);
		contents.reset();
		
		if(SimplePointProviderHandler.SUBHANDLER_ELEMENT.equals(qName)) {
			try {
				xmlReader.setContentHandler(new SimplePointLocationHandler(this, this.xmlReader, this.simplePointFeatures, this.currentProvider, this.featureBuilder));
			} catch (SchemaException e) {
				String error = "SimplePointProviderHandler.startElement() Exception: " + e.getMessage();
				log.error(error);
				System.out.println(error);
				
				throw new SAXException(error);
			} catch (NoSuchAuthorityCodeException e) {
				String error = "SimplePointProviderHandler.startElement() Exception: " + e.getMessage();
				log.error(error);
				System.out.println(error);
				
				throw new SAXException(error);
			} catch (FactoryException e) {
				String error = "SimplePointProviderHandler.startElement() Exception: " + e.getMessage();
				log.error(error);
				System.out.println(error);
				
				throw new SAXException(error);
			}
		}
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		//String msg = "========== SimplePointProviderHandler.startElement() [" + qName + "] ==========";
		//System.out.println(msg);
		//log.debug(msg);
		
		if(SimplePointProviderHandler.PROVIDER_NAME_ELEMENT.equals(qName)) {
			this.currentProvider = SourceProvider.getTypeFromString(contents.toString());
		}
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException {
		contents.write(ch, start, length);
	}
}
