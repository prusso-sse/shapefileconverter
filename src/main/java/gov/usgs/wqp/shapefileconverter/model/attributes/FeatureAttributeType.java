package gov.usgs.wqp.shapefileconverter.model.attributes;

public enum FeatureAttributeType {
	provider, name, type, point, UNKNOWN;

	public static FeatureAttributeType getTypeFromString(String string) {
		if (string.equals("Provider")) {
			return provider;
		}
		
		if (string.equals("Name")) {
			return name;
		}
		
		if (string.equals("Type")) {
			return type;
		}
		
		if (string.equals("Point")) {
			return point;
		}

		return UNKNOWN;
	}

	public static String getStringFromType(FeatureAttributeType type) {
		switch (type) {
			case provider: {
				return "Provider";
			}
			
			case name: {
				return "Name";
			}
			
			case type: {
				return "Type";
			}
			
			case point: {
				return "Point";
			}
			
			default: {
				return "UNKNOWN";
			}
		}
	}
}
