package gov.usgs.wqp.shapefileconverter.model.providers;

public enum SourceProvider {
	NWIS, STEWARDS, STORET, UNKNOWN;

	public static SourceProvider getTypeFromString(String string) {
		if (string.equals("NWIS")) {
			return NWIS;
		}
		
		if (string.equals("STEWARDS")) {
			return STEWARDS;
		}
		
		if (string.equals("STORET")) {
			return STORET;
		}
		
		return UNKNOWN;
	}

	public static String getStringFromType(SourceProvider type) {
		switch (type) {
			case NWIS: {
				return "NWIS";
			}
			
			case STEWARDS: {
				return "STEWARDS";
			}
			
			case STORET: {
				return "STORET";
			}
			
			default: {
				return "UNKNOWN";
			}
		}
	}
}
