package gov.usgs.wqp.shapefileconverter;

import gov.usgs.wqp.shapefileconverter.model.sources.DataInputType;
import gov.usgs.wqp.shapefileconverter.utils.RESTClient;
import gov.usgs.wqp.shapefileconverter.utils.ShapeFileUtils;
import gov.usgs.wqp.shapefileconverter.utils.ShapeFileUtils.CLIMode;
import gov.usgs.wqp.shapefileconverter.utils.TimeProfiler;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

public class CLI {
	static Logger log = ShapeFileUtils.getLogger(CLI.class);
	
	// Arguments
	private static String modeArg = "-m"; // mode Argument (create file or push file or send geoserver request)
	private static CLIMode modeValue = null; // zip up the archive Value
	private static String fileArg = "-f"; // XML File Argument
	private static String theFile = null; // XML File Argument Value
	private static String pathArg = "-d"; // Path Argument
	private static String thePath = null; // Path Argument Value
	private static String nameArg = "-s"; // Shapefile Name Argument
	private static String theName = null; // Shapefile Name Argument Value
	private static String archiveArg = "-a"; // zip up the archive
	private static boolean theArchive = false; // zip up the archive Value
	private static String restArg = "-r"; // REST URI
	private static String restValue = null; // REST URI value
	private static String restUserArg = "-u"; // REST User
	private static String restUserValue = null; // REST User value
	private static String restPassArg = "-p"; // REST Password
	private static String restPassValue = null; // REST password value
	private static String restPostDataArg = "-D"; // REST Post Data
	private static String restPostDataValue = null; // REST Post Data value
	private static String restMediaTypeArg = "-X"; // REST Media Type Data
	private static String restMediaTypeValue = null; // REST Media Type value
	private static String helpArg = "-h"; // Help Argument

	public static void main(String[] args) throws IOException {
		// Retrieve all CLI arguments
		if (!getArgs(args)) {
			showHelp();
			System.exit(0);
		}
		
		switch(modeValue) {
			case createAndUploadShapefile: {
				// Perform the Shapefile Conversion
				TimeProfiler.startTimer("ShapeFileConverter Overall Time");
				ShapeFileConverter spc = new ShapeFileConverter();
				
				/**
				 * Parse the input
				 */
				System.out.println("o ----- Parsing input (" + theFile + ")");
				if(!spc.parseInput(theFile, DataInputType.WQX_OB_XML)) {
					System.out.println("\nParsing the input failed.  Exiting...\n");
					System.exit(0);
					break;
				}
				System.out.println("o ----- Parsing Complete");
				
				/**
				 * Create the shapefile
				 */
				System.out.println("o ----- Creating Shapefile (" + theName + ")");
				if(!spc.createShapeFile(thePath, theName, true, true)) {		// we force zip file as GeoServer requires it
					System.out.println("\nCreating the shapefile failed.  Exiting...\n");
					System.exit(0);
					break;
				}
				System.out.println("o ----- Creating Shapefile Complete");
				
				/**
				 * Upload the shapefile
				 */
				System.out.println("o ----- Uploading Shapefile (" + theName + ") to GeoServer");
				String zipFilename = thePath + File.separator + theName + ".zip";
				String response = RESTClient.putDataFile(restValue, restUserValue, restPassValue, ShapeFileUtils.MEDIATYPE_APPLICATION_ZIP, zipFilename);				
				System.out.println("\nGeoServer response for request [" + restValue + "] is: \n[" + response + "]");
				System.out.println("o ----- Uploading Shapefile Complete");				
				
				TimeProfiler.endTimer("ShapeFileConverter Overall Time", log);
				
				break;
			}
			case createShapefile: {
				// Perform the Shapefile Conversion
				TimeProfiler.startTimer("ShapeFileConverter Overall Time");
				ShapeFileConverter spc = new ShapeFileConverter();
				
				/**
				 * Parse the input
				 */
				if(!spc.parseInput(theFile, DataInputType.WQX_OB_XML)) {
					System.out.println("\nParsing the input failed.  Exiting...\n");
					System.exit(0);
					break;
				}
				
				/**
				 * Create the shapefile
				 */
				if(!spc.createShapeFile(thePath, theName, true, theArchive)) {
					System.out.println("\nCreating the shapefile failed.  Exiting...\n");
					System.exit(0);
					break;
				}
				
				TimeProfiler.endTimer("ShapeFileConverter Overall Time", log);
				
				break;
			}
			case geoserverGet: {
				String response = RESTClient.get(restValue, restUserValue, restPassValue, restMediaTypeValue);				
				System.out.println("\nGeoServer response for request [" + restValue + "] is: \n[" + response + "]\n\n\nExiting...\n");				
				break;
			}
			case geoserverDelete: {
				String response = RESTClient.delete(restValue, restUserValue, restPassValue, restMediaTypeValue);				
				System.out.println("\nGeoServer response for request [" + restValue + "] is: \n[" + response + "]\n\n\nExiting...\n");				
				break;
			}
			case geoserverPost: {
				String response = RESTClient.post(restValue, restPostDataValue, restUserValue, restPassValue, restMediaTypeValue);				
				System.out.println("\nGeoServer response for request [" + restValue + "] is: \n[" + response + "]\n\n\nExiting...\n");
				break;
			}
			case uploadShapefile: {
				String response = RESTClient.putDataFile(restValue, restUserValue, restPassValue, restMediaTypeValue, theFile);				
				System.out.println("\nGeoServer response for request [" + restValue + "] is: \n[" + response + "]\n\n\nExiting...\n");
				break;
			}
			default: {
				showHelp();
				System.exit(0);
				break;
			}
		}
		
		System.out.println("\nExiting ShapeFileConverter\n");
	}

	/**
	 * Parse CLI Arguments
	 * 
	 * @param args
	 * @return
	 */
	private static boolean getArgs(String[] args) {
		boolean gotMode = false;
		boolean gotFile = false;
		boolean gotPath = false;
		boolean gotName = false;
		boolean gotRestURI = false;
		boolean gotRestUser = false;
		boolean gotRestPass = false;
		boolean gotRestPostData = false;
		boolean gotRestMediaType = false;

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals(modeArg)) {
				if ((i + 1) >= args.length) {
					return false;
				}

				if (args[i + 1] != null) {
					// Stat the file
					String mode = args[i + 1];
					
					modeValue = CLIMode.getTypeFromString(mode);
					gotMode = true;
					
					if(CLIMode.UNKNOWN != modeValue) {
					} else {
						System.out.println("\n\nError: The requested mode [" + mode + "] is not a valid mode.\nValid modes are: [" +
										   CLIMode.validModes() + "\n");
						return false;
					}
				}
			}
			
			if (args[i].equals(fileArg)) {
				if ((i + 1) >= args.length) {
					return false;
				}

				if (args[i + 1] != null) {
					// Stat the file
					String file = args[i + 1];
					File f = new File(file);
					if (f.exists()) {
						theFile = file;
						gotFile = true;
					} else {
						System.out.println("\n\nError: The file [" + file + "] does not exist.\n\n");
						return false;
					}
				}
			}
			
			if (args[i].equals(pathArg)) {
				if ((i + 1) >= args.length) {
					return false;
				}

				if (args[i + 1] != null) {
					// Stat the file
					String file = args[i + 1];
					File f = new File(file);
					if (f.exists()) {
						try {
							thePath = f.getCanonicalPath();
						} catch (IOException e) {
							e.printStackTrace();
							thePath = file;
						}
						gotPath = true;
					} else {
						System.out.println("\n\nError: The path [" + file + "] does not exist.\n\n");
						return false;
					}
				}
			}
			
			if (args[i].equals(nameArg)) {
				if ((i + 1) >= args.length) {
					return false;
				}

				if (args[i + 1] != null) {
					String name = args[i + 1];
					if(ShapeFileUtils.filenameIsValid(name)) {
						theName = name;
						gotName = true;
					} else {
						System.out.println("\n\nError: The name [" + name + "] contains illegal filename characters.\n\n");
						return false;
					}
				}
			}
			
			if (args[i].equals(restUserArg)) {
				if ((i + 1) >= args.length) {
					return false;
				}

				if (args[i + 1] != null) {
					restUserValue = args[i + 1];
					gotRestUser = true;
				}
			}
			
			if (args[i].equals(restPassArg)) {
				if ((i + 1) >= args.length) {
					return false;
				}

				if (args[i + 1] != null) {
					restPassValue = args[i + 1];
					gotRestPass = true;
				}
			}
			
			if (args[i].equals(restArg)) {
				if ((i + 1) >= args.length) {
					return false;
				}

				if (args[i + 1] != null) {
					restValue = args[i + 1];
					gotRestURI = true;
				}
			}
			
			if (args[i].equals(restPostDataArg)) {
				if ((i + 1) >= args.length) {
					return false;
				}

				if (args[i + 1] != null) {
					restPostDataValue = args[i + 1];
					gotRestPostData = true;
				}
			}
			
			if (args[i].equals(restMediaTypeArg)) {
				if ((i + 1) >= args.length) {
					return false;
				}

				if (args[i + 1] != null) {
					restMediaTypeValue = args[i + 1];
					gotRestMediaType = true;
				}
			}
			
			if (args[i].equals(archiveArg)) {
				theArchive = true;
			}

			if (args[i].equals(helpArg)) {
				return false;
			}
		}
		
		if(!gotMode) {
			return false;
		}
		
		switch(modeValue) {
			case createAndUploadShapefile: {
				if(gotFile && gotPath && gotName && gotRestURI) {
					if(gotRestUser && !gotRestPass) {
						System.out.println("\n\nWARNING: Rest username described without a password.  Using empty string as password.\n\n");
						restPassValue = "";
					} else if(!gotRestUser && gotRestPass) {
						System.out.println("\n\nERROR: Rest password described without a username.  Exiting...\n\n");
						return false;
					}
					
					if(!gotRestMediaType) {
						System.out.println("\n\nWARNING: Rest media type has not been established.  Using [" + ShapeFileUtils.MEDIATYPE_APPLICATION_ZIP + "] as default.\n\n");
						restMediaTypeValue = ShapeFileUtils.MEDIATYPE_APPLICATION_ZIP;
					}
					return true;
				} else {
					System.out.println("\n\nError: When using mode [" + CLIMode.getStringFromType(modeValue) + "] you must declare the " + fileArg + ", " +
									   pathArg + ", " + nameArg + ", " + restArg + " arguments.\n\n");					
					return false;
				}
			}
			case createShapefile: {
				if(gotFile && gotPath && gotName) {
					return true;
				} else {
					System.out.println("\n\nError: When using mode [" + CLIMode.getStringFromType(modeValue) + "] you must declare the " + fileArg + ", " +
									   pathArg + " and " + nameArg + " arguments.\n\n");					
					return false;
				}
			}
			case geoserverGet: {
				if(gotRestURI) {
					if(gotRestUser && !gotRestPass) {
						System.out.println("\n\nWARNING: Rest username described without a password.  Using empty string as password.\n\n");
						restPassValue = "";
					} else if(!gotRestUser && gotRestPass) {
						System.out.println("\n\nERROR: Rest password described without a username.  Exiting...\n\n");
						return false;
					}
					
					if(!gotRestMediaType) {
						System.out.println("\n\nWARNING: Rest media type has not been established.  Using [" + MediaType.TEXT_XML + "] as default.\n\n");
						restMediaTypeValue = MediaType.TEXT_XML;
					}
					
					return true;
				} else {
					System.out.println("\n\nError: When using mode [" + CLIMode.getStringFromType(modeValue) + "] you must declare the " + restArg + 
							   		   " argument.\n\n");
					return false;
				}
			}
			case geoserverDelete: {
				if(gotRestURI) {
					if(gotRestUser && !gotRestPass) {
						System.out.println("\n\nWARNING: Rest username described without a password.  Using empty string as password.\n\n");
						restPassValue = "";
					} else if(!gotRestUser && gotRestPass) {
						System.out.println("\n\nERROR: Rest password described without a username.  Exiting...\n\n");
						return false;
					}
					
					if(!gotRestMediaType) {
						System.out.println("\n\nWARNING: Rest media type has not been established.  Using [" + MediaType.TEXT_XML + "] as default.\n\n");
						restMediaTypeValue = MediaType.TEXT_XML;
					}
					
					return true;
				} else {
					System.out.println("\n\nError: When using mode [" + CLIMode.getStringFromType(modeValue) + "] you must declare the " + restArg + 
							   		   " argument.\n\n");
					return false;
				}
			}
			case geoserverPost: {
				if(gotRestURI && gotRestPostData) {
					if(gotRestUser && !gotRestPass) {
						System.out.println("\n\nWARNING: Rest username described without a password.  Using empty string as password.\n\n");
						restPassValue = "";
					} else if(!gotRestUser && gotRestPass) {
						System.out.println("\n\nERROR: Rest password described without a username.  Exiting...\n\n");
						return false;
					}
					
					if(!gotRestMediaType) {
						System.out.println("\n\nWARNING: Rest media type has not been established.  Using [" + MediaType.TEXT_XML + "] as default.\n\n");
						restMediaTypeValue = MediaType.TEXT_XML;
					}
					
					return true;
				} else {
					System.out.println("\n\nError: When using mode [" + CLIMode.getStringFromType(modeValue) + "] you must declare the " + restArg + 
							   		   " and " + restPostDataArg + " arguments.\n\n");
					return false;
				}
			}
			case uploadShapefile: {
				if(gotFile && gotRestURI) {
					if(gotRestUser && !gotRestPass) {
						System.out.println("\n\nWARNING: Rest username described without a password.  Using empty string as password.\n\n");
						restPassValue = "";
					} else if(!gotRestUser && gotRestPass) {
						System.out.println("\n\nERROR: Rest password described without a username.  Exiting...\n\n");
						return false;
					}
					
					if(!gotRestMediaType) {
						System.out.println("\n\nWARNING: Rest media type has not been established.  Using [" + ShapeFileUtils.MEDIATYPE_APPLICATION_ZIP + "] as default.\n\n");
						restMediaTypeValue = ShapeFileUtils.MEDIATYPE_APPLICATION_ZIP;
					}
					
					return true;
				} else {
					System.out.println("\n\nError: When using mode [" + CLIMode.getStringFromType(modeValue) + "] you must declare the " + fileArg + ", " +
									   restArg + " arguments.\n\n");					
					return false;
				}
			}
			default: {
				break;
			}
		}
		

		return false;
	}

	private static void showHelp() {
		String help = "\n   ShapefileConverter v0.0.1\n"
				+ "   Copyright (c) 2014 USGS\n\n"
				+ "     This program will convert a set of XML GeoSpatial\n"
				+ "     points to the shapefile format.\n"
				+ "              \n\n"
				+ "   Usage:\n\n"
				+ "      java -jar shapefileconverter-0.0.1-SNAPSHOT.jar [-f FILE] [-v] [-h]\n"
				+ "      -m  mode       :   Mode this application runs in.  Valid modes are:\n"
				+ "                         " + CLIMode.validModes() + "\n"
				+ "      -f  file       :   File used for MODE (raw xml or shapefile)\n"
				+ "      -d  path       :   Directory to write to\n"
				+ "      -s  name       :   Name of shapefile to write to\n"
				+ "      -r  URI        :   REST URI (Mainly for GeoServer but can be for any REST server).\n"
				+ "      -u  username   :   REST Username.\n"
				+ "      -p  password   :   REST Password.\n"
				+ "      -D  data       :   REST Data in string form.\n"
				+ "      -X  media type :   REST Media Type in string form.\n"
				+ "      -a             :   If argument is present will archive created shapefiles\n"
				+ "      -v             :   Verbose.\n"
				+ "      -h             :   This help document.\n\n"
				+ "       --------------------------------------\n"
				+ "\n\n   --------------------------------------------------------------------------\n\n"
				+ "   Example:\n\n"
				+ "   >java -jar shapefileconverter-0.0.1-SNAPSHOT.jar -f file.xml -d ./ -s MyShapeFile -v\n\n";

		System.out.println(help);

		return;
	}
}
