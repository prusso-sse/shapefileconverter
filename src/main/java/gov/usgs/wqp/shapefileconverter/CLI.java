package gov.usgs.wqp.shapefileconverter;

import gov.usgs.wqp.shapefileconverter.model.sources.DataInputType;
import gov.usgs.wqp.shapefileconverter.utils.ShapeFileUtils;

import java.io.File;
import java.io.IOException;

public class CLI {
	// Arguments
	private static String fileArg = "-f"; // XML File Argument
	private static String theFile = null; // XML File Argument Value
	private static String pathArg = "-p"; // Path Argument
	private static String thePath = null; // Path Argument Value
	private static String nameArg = "-s"; // Shapefile Name Argument
	private static String theName = null; // Shapefile Name Argument Value
	private static String archiveArg = "-a"; // zip up the archive
	private static boolean theArchive = false; // zip up the archive Value
	private static String helpArg = "-h"; // Help Argument

	public static void main(String[] args) throws IOException {
		// Retrieve all CLI arguments
		if (!getArgs(args)) {
			showHelp();
			System.exit(0);
		}

		// Perform the Shapefile Conversion
		ShapeFileConverter spc = new ShapeFileConverter();
		spc.parseInput(theFile, DataInputType.WQX_OB_XML);
		spc.createShapeFile(thePath, theName, true, theArchive);
	}

	/**
	 * Parse CLI Arguments
	 * 
	 * @param args
	 * @return
	 */
	private static boolean getArgs(String[] args) {
		boolean result = false;

		for (int i = 0; i < args.length; i++) {
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
						result = true;
					} else {
						System.out.println("\n\nError: The file [" + file + "] does not exist.\n\n");
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
						result = true;
					} else {
						System.out.println("\n\nError: The path [" + file + "] does not exist.\n\n");
					}
				}
			}
			
			if (args[i].equals(nameArg)) {
				if ((i + 1) >= args.length) {
					return false;
				}

				if (args[i + 1] != null) {
					// Stat the file
					String name = args[i + 1];
					if(ShapeFileUtils.filenameIsValid(name)) {
						theName = name;
						result = true;
					} else {
						System.out.println("\n\nError: The name [" + name + "] contains illegal filename characters.\n\n");
					}
				}
			}
			
			if (args[i].equals(archiveArg)) {
				theArchive = true;
			}

			if (args[i].equals(helpArg)) {
				return false;
			}
		}

		return result;
	}

	private static void showHelp() {
		String help = "\n   ShapefileConverter v0.0.1\n"
				+ "   Copyright (c) 2014 USGS\n\n"
				+ "     This program will convert a set of XML GeoSpatial\n"
				+ "     points to the shapefile format.\n"
				+ "              \n\n"
				+ "   Usage:\n\n"
				+ "      java -jar shapefileconverter-0.0.1-SNAPSHOT.jar [-f FILE] [-v] [-h]\n"
				+ "      -f  file    :   Raw XML File\n"
				+ "      -p  path    :   Directory to write to\n"
				+ "      -s  name    :   Name of shapefile to write to\n"
				+ "      -v          :   Verbose.\n"
				+ "      -h          :   This help document.\n\n"
				+ "       --------------------------------------\n"
				+ "\n\n   --------------------------------------------------------------------------\n\n"
				+ "   Example:\n\n"
				+ "   >java -jar shapefileconverter-0.0.1-SNAPSHOT.jar -f file.xml -p ./ -s MyShapeFile -v\n\n";

		System.out.println(help);

		return;
	}
}
