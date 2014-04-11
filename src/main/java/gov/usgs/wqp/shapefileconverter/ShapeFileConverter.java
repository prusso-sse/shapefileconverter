package gov.usgs.wqp.shapefileconverter;

import gov.usgs.wqp.shapefileconverter.model.FeatureDAO;
import gov.usgs.wqp.shapefileconverter.model.features.SimplePointFeature;
import gov.usgs.wqp.shapefileconverter.model.sources.DataInputType;
import gov.usgs.wqp.shapefileconverter.parser.wqx.SimplePointParser;
import gov.usgs.wqp.shapefileconverter.utils.ShapeFileUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.xml.sax.SAXException;

public class ShapeFileConverter {
	static Logger log = ShapeFileUtils.getLogger(ShapeFileConverter.class);
	private List<FeatureDAO> featureList = null;
	
	private SimpleFeatureBuilder featureBuilder;
	private SimpleFeatureType featureType;
	
	public ShapeFileConverter() {
		this.featureList = new ArrayList<FeatureDAO>();
	}
	
	public boolean parseInput(String filename, DataInputType type) {
		boolean result = false;
		
		switch(type) {
			case WQX_OB_XML: {
				try {
					this.featureType = SimplePointFeature.getFeatureType();
					this.featureBuilder = new SimpleFeatureBuilder(this.featureType);
					
					long startTime = System.nanoTime();
					SimplePointParser spp = new SimplePointParser(filename, this.featureBuilder);
					this.featureList = spp.parseSimplePointSource();
					long endTime = System.nanoTime();
					
					NumberFormat formatter = new DecimalFormat("#0.000000000");
					System.out.println("\nParse Execution Time [" + formatter.format((endTime - startTime) / 1000000000d) + "] seconds");
					
					result = true;
				} catch (ParserConfigurationException e) {
					System.out.println(e.getMessage());
					log.error(e.getMessage());
				} catch (SAXException e) {
					System.out.println(e.getMessage());
					log.error(e.getMessage());
				} catch (IOException e) {
					System.out.println(e.getMessage());
					log.error(e.getMessage());
				} catch (NoSuchAuthorityCodeException e) {
					System.out.println(e.getMessage());
					log.error(e.getMessage());
				} catch (SchemaException e) {
					System.out.println(e.getMessage());
					log.error(e.getMessage());
				} catch (FactoryException e) {
					System.out.println(e.getMessage());
					log.error(e.getMessage());
				}				
				break;
			}
			default: {
				break;
			}
		}
		
		return result;
	}
	
	public boolean createShapeFile(String path, String filename, boolean createIndex, boolean archive) {
		File newFile = new File(path + "/" + filename + ".shp");

        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();

        Map<String, Serializable> params = new HashMap<String, Serializable>();
        try {
			params.put("url", newFile.toURI().toURL());
		} catch (MalformedURLException e) {
			System.out.println(e.getMessage());
			log.error(e.getMessage());
			return false;
		}
        params.put("create spatial index", createIndex);

        ShapefileDataStore newDataStore;
		try {
			newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			log.error(e.getMessage());
			return false;
		}

        /*
         * TYPE is used as a template to describe the file contents
         */
        try {
			newDataStore.createSchema(this.featureType);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			log.error(e.getMessage());
			return false;
		}
        
        /**
		 * Get our features
		 */
        long startTime = System.nanoTime();
        List<SimpleFeature> features = new ArrayList<SimpleFeature>();
		for(FeatureDAO feature : this.featureList) {
			features.add(feature.getSimpleFeature());
		}
		long endTime = System.nanoTime();		
		NumberFormat formatter = new DecimalFormat("#0.000000000");
		System.out.println("\nFeature Creation Time [" + formatter.format((endTime - startTime) / 1000000000d) + "] seconds");
        
        /*
         * Write the features to the shapefile
         */
		startTime = System.nanoTime();
        if(!ShapeFileUtils.writeToShapeFile(newDataStore, featureType, features, archive, path, filename)) {
        	String error = "Unable to write shape file";
        	System.out.println(error);
			log.error(error);
			return false;
        }
        endTime = System.nanoTime();
		System.out.println("\nShapeFile Creation Time [" + formatter.format((endTime - startTime) / 1000000000d) + "] seconds");
		
		return true;
	}
	
	
}
