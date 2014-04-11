package gov.usgs.wqp.shapefileconverter.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public class ShapeFileUtils {
	private static Logger log = ShapeFileUtils.getLogger(ShapeFileUtils.class);
	
	private static final char[] ILLEGAL_CHARACTERS = { '/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':' };
	
	public static Logger getLogger(Class<?> T) {
		URL logFile = T.getResource("/log4j.properties");
		if(logFile == null) {
			logFile = T.getResource("./log4j.properties");
		}
		if(logFile == null) {
			logFile = T.getResource("conf/log4j.properties");
		}

		Logger log = Logger.getLogger(T.getName());	
		
		if(logFile != null) {
			PropertyConfigurator.configure(logFile);
		}

		return log;
	}
	
	public static boolean filenameIsValid(String name) {
		for(char character : ShapeFileUtils.ILLEGAL_CHARACTERS) {
			if(name.indexOf(character) != -1) {
				return false;
			}
		}
		
		return true;
	}
	
	public static boolean writeToShapeFile(ShapefileDataStore newDataStore, SimpleFeatureType featureType, List<SimpleFeature> features, boolean archive, String path, String filename) {
		/*
         * Write the features to the shapefile
         */
        Transaction transaction = new DefaultTransaction("create");

        String typeName;
		try {
			typeName = newDataStore.getTypeNames()[0];
		} catch (IOException e) {
			System.out.println(e.getMessage());
			log.error(e.getMessage());
			return false;
		}
		
        SimpleFeatureSource featureSource;
		try {
			featureSource = newDataStore.getFeatureSource(typeName);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			log.error(e.getMessage());
			return false;
		}

        if (featureSource instanceof SimpleFeatureStore) {
            SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
            /*
             * SimpleFeatureStore has a method to add features from a
             * SimpleFeatureCollection object, so we use the ListFeatureCollection
             * class to wrap our list of features.
             */
            SimpleFeatureCollection collection = new ListFeatureCollection(featureType, features);
            featureStore.setTransaction(transaction);
            try {
                featureStore.addFeatures(collection);
                transaction.commit();
            } catch (Exception e) {
            	System.out.println(e.getMessage());
    			log.error(e.getMessage());
                try {
					transaction.rollback();
				} catch (IOException e1) {
					System.out.println(e.getMessage());
					log.error(e.getMessage());
				}
            } finally {
                try {
					transaction.close();
				} catch (IOException e) {
					System.out.println(e.getMessage());
					log.error(e.getMessage());
				}
            }
        } else {
            String msg = typeName + " does not support read/write access";
            System.out.println(msg);
			log.error(msg);
			return false;
        }
        
        if(archive) {
        	ShapeFileUtils.createZipFromFilematch(path, filename);
        }
		
		return true;
	}
	
	public static boolean createZipFromFilematch(String path, final String filename) {
		File directory = new File(path);
		
		if(!directory.exists()) {
			String msg = "Directory " + path + " does not exist";
            System.out.println(msg);
			log.error(msg);
			return false;
		}
		
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if(name.indexOf(filename) != -1) {
					return true;
				}
				return false;
			}
		};			
		
		String[] directoryFiles = directory.list(filter);
			
		try {
			byte[] buffer = new byte[1024]; 
	    	FileOutputStream fos = new FileOutputStream(path + File.separator + filename + ".zip");
	    	ZipOutputStream zos = new ZipOutputStream(fos);
	 	 
	    	for(String file : directoryFiles){
	    		ZipEntry ze= new ZipEntry(file);
	        	zos.putNextEntry(ze);
	        	
	        	String fileToZip = path + File.separator + file;
	        	FileInputStream in = new FileInputStream(fileToZip);
	 
	        	int len;
	        	while ((len = in.read(buffer)) > 0) {
	        		zos.write(buffer, 0, len);
	        	}
	 
	        	in.close();
	        	
	        	File deleteFile = new File(fileToZip);
	        	deleteFile.delete();
	    	}
	 
	    	zos.closeEntry();
	    	zos.close();
	    } catch(IOException e) {
	    	System.out.println(e.getMessage());
			log.error(e.getMessage());
			return false;
	    }
		
		return true;
	}
}
