package gov.usgs.wqp.shapefileconverter.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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
	
	public static final String MEDIATYPE_APPLICATION_ZIP = "application/zip";
	
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
		// ==============
		TimeProfiler.startTimer("GeoTools - Create Transaction time");
        Transaction transaction = new DefaultTransaction("create");
        TimeProfiler.endTimer("GeoTools - Create Transaction time", log);
		// ==============
        
        String typeName;
		try {
			typeName = newDataStore.getTypeNames()[0];
		} catch (IOException e) {
			System.out.println(e.getMessage());
			log.error(e.getMessage());
			return false;
		}
		
		// ==============
		TimeProfiler.startTimer("GeoTools - Create SimpleFeatureSource time");
        SimpleFeatureSource featureSource;
		try {
			featureSource = newDataStore.getFeatureSource(typeName);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			log.error(e.getMessage());
			return false;
		}
		TimeProfiler.endTimer("GeoTools - Create SimpleFeatureSource time", log);
		// ==============
		
        if (featureSource instanceof SimpleFeatureStore) {
            SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
            /*
             * SimpleFeatureStore has a method to add features from a
             * SimpleFeatureCollection object, so we use the ListFeatureCollection
             * class to wrap our list of features.
             */
            // ==============
            TimeProfiler.startTimer("GeoTools - SimpleFeatureCollection Creation time");
            SimpleFeatureCollection collection = new ListFeatureCollection(featureType, features);
            TimeProfiler.endTimer("GeoTools - SimpleFeatureCollection Creation time", log);
    		// ==============
    		
            featureStore.setTransaction(transaction);
            try {
            	// ==============
            	TimeProfiler.startTimer("GeoTools - SimpleFeatureCollection Population time");
                featureStore.addFeatures(collection);
                TimeProfiler.endTimer("GeoTools - SimpleFeatureCollection Population time", log);
        		// ==============
        		
        		// ==============
                TimeProfiler.startTimer("GeoTools - Transaction Commit time");
                transaction.commit();
                TimeProfiler.endTimer("GeoTools - Transaction Commit time", log);
        		// ==============
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
        	// ==============
        	TimeProfiler.startTimer("ZIP Archive - Overall Archive time");
        	ShapeFileUtils.createZipFromFilematch(path, filename);
        	TimeProfiler.endTimer("ZIP Archive - Overall Archive time", log);
    		// ==============
        }
		
		return true;
	}
	
	public static boolean createZipFromFilematch(String path, final String filename) {
		String zipFileName = path + File.separator + filename + ".zip";
		
		File directory = new File(path);
		
		if(!directory.exists()) {
			String msg = "Directory " + path + " does not exist";
            System.out.println(msg);
			log.error(msg);
			return false;
		}
		
		File existingFile = new File(zipFileName);
		if(existingFile.exists()) {
			String msg = "Zip file " + zipFileName + " exists.  Deleting prior to new shapefile creation.";
            System.out.println(msg);
			log.info(msg);
			existingFile.delete();
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
			final int BUFFER = 2048;
			BufferedInputStream origin = null;
			FileOutputStream dest = new FileOutputStream(zipFileName);
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

			byte data[] = new byte[BUFFER];

			for (String file : directoryFiles) {
				FileInputStream fi = new FileInputStream(file);
				origin = new BufferedInputStream(fi, BUFFER);

				ZipEntry entry = new ZipEntry(file);
				out.putNextEntry(entry);
				int count;

				while ((count = origin.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
				}

				origin.close();
				
				File deleteFile = new File(file);
	        	deleteFile.delete();
			}
			out.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			log.error(e.getMessage());
			return false;
		}
		
		return true;
	}
	
	/**
	 * GLOBAL ENUMS
	 */
	public enum CLIMode {
		createAndUploadShapefile, createShapefile, uploadShapefile, geoserverPost, geoserverPostFile, geoserverGet, geoserverDelete, UNKNOWN;

		public static CLIMode getTypeFromString(String string) {
			if (string.equals("createAndUploadShapefile")) {
				return createAndUploadShapefile;
			}
			
			if (string.equals("createShapefile")) {
				return createShapefile;
			}
			
			if (string.equals("uploadShapefile")) {
				return uploadShapefile;
			}
			
			if (string.equals("geoserverPost")) {
				return geoserverPost;
			}
			
			if (string.equals("geoserverPostFile")) {
				return geoserverPostFile;
			}
			
			if (string.equals("geoserverGet")) {
				return geoserverGet;
			}
			
			if (string.equals("geoserverDelete")) {
				return geoserverDelete;
			}
			
			if (string.equals("UNKNOWN")) {
				return UNKNOWN;
			}

			return UNKNOWN;
		}

		public static String getStringFromType(CLIMode type) {
			switch (type) {
				case createAndUploadShapefile: {
					return "createAndUploadShapefile";
				}
				
				case createShapefile: {
					return "createShapefile";
				}
				
				case uploadShapefile: {
					return "uploadShapefile";
				}
				
				case geoserverPost: {
					return "geoserverPost";
				}
				
				case geoserverPostFile: {
					return "geoserverPostFile";
				}
				
				case geoserverGet: {
					return "geoserverGet";
				}
				
				case geoserverDelete: {
					return "geoserverDelete";
				}
				
				case UNKNOWN: {
					return "UNKNOWN";
				}
				
				default: {
					return "UNKNOWN";
				}
			}
		}
		
		public static String validModes() {
			CLIMode[] values = CLIMode.values();
			
			StringBuffer result = new StringBuffer();
			
			for(int i = 0; i < values.length; i++) {
				CLIMode mode = values[i];
				if(CLIMode.UNKNOWN == mode) {
					continue;
				}
				
				result.append(mode);
				
				if(i < (values.length - 1)) {
					if(values[i+1] != CLIMode.UNKNOWN) {
						result.append(", ");
					}
				}
			}
			
			return result.toString();
		}
	}
}
