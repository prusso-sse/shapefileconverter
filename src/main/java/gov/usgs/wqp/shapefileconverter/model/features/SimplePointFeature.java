package gov.usgs.wqp.shapefileconverter.model.features;

import gov.usgs.wqp.shapefileconverter.model.FeatureDAO;
import gov.usgs.wqp.shapefileconverter.model.attributes.BaseAttributeType;
import gov.usgs.wqp.shapefileconverter.model.attributes.FeatureAttributeType;
import gov.usgs.wqp.shapefileconverter.model.providers.SourceProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class SimplePointFeature implements FeatureDAO {
	private SourceProvider provider;
	private String name;
	private String type;
	private double longitude;
	private double latitude;
	private Point point;
	
	private Map<BaseAttributeType, String> baseAttribs;
	private Map<FeatureAttributeType, Object> featureAttribs;
	private SimpleFeature simpleFeature;
	
	private boolean featureIsDirty = true;
	private GeometryFactory geometryFactory;
	private SimpleFeatureBuilder featureBuilder;
	
	private static SimpleFeatureType FEATURETYPE;
	
	@SuppressWarnings("serial")
	public SimplePointFeature(SimpleFeatureBuilder featureBuilder, SourceProvider srcProvider) {
		this.featureBuilder = featureBuilder;
		
		this.provider = srcProvider;
		this.name = "";
		this.type = "";
		this.longitude = 0.0;
		this.latitude = 0.0;
		
		this.geometryFactory = JTSFactoryFinder.getGeometryFactory();
		this.point = this.geometryFactory.createPoint(new Coordinate(this.longitude, this.latitude));
		
		this.baseAttribs = new HashMap<BaseAttributeType, String>() {{
			put(BaseAttributeType.Provider, SourceProvider.getStringFromType(provider));
			put(BaseAttributeType.LocationIdentifier, type);
			put(BaseAttributeType.LocationType, type);
			put(BaseAttributeType.Longitude, "" + longitude);
			put(BaseAttributeType.Latitude, "" + latitude);
		}};
		
		this.featureAttribs = new HashMap<FeatureAttributeType, Object>() {{
			put(FeatureAttributeType.provider, SourceProvider.getStringFromType(provider));
			put(FeatureAttributeType.name, name);
			put(FeatureAttributeType.type, type);
			put(FeatureAttributeType.point, point);
		}};
		
		this.featureIsDirty = true;
	}
	
	@SuppressWarnings("serial")
	public SimplePointFeature(SimpleFeatureBuilder featureBuilder, SourceProvider srcProvider, String featureName, String featureType, double lng, double lat) {
		this.provider = srcProvider;
		this.name = featureName;
		this.type = featureType;
		this.longitude = lng;
		this.latitude = lat;
		
		this.geometryFactory = JTSFactoryFinder.getGeometryFactory();
		this.point = this.geometryFactory.createPoint(new Coordinate(this.longitude, this.latitude));
		
		baseAttribs = new HashMap<BaseAttributeType, String>() {{
			put(BaseAttributeType.Provider, SourceProvider.getStringFromType(provider));
			put(BaseAttributeType.LocationIdentifier, name);
			put(BaseAttributeType.LocationType, type);
			put(BaseAttributeType.Longitude, "" + longitude);
			put(BaseAttributeType.Latitude, "" + latitude);
		}};
		
		featureAttribs = new HashMap<FeatureAttributeType, Object>() {{
			put(FeatureAttributeType.provider, SourceProvider.getStringFromType(provider));
			put(FeatureAttributeType.name, name);
			put(FeatureAttributeType.type, type);
			put(FeatureAttributeType.point, point);
		}};
				
		this.featureIsDirty = true;
	}

	public SourceProvider getProvider() {
		return provider;
	}

	public void setProvider(SourceProvider provider) {
		this.provider = provider;
		baseAttribs.put(BaseAttributeType.Provider, SourceProvider.getStringFromType(provider));
		this.featureIsDirty = true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		baseAttribs.put(BaseAttributeType.LocationIdentifier, name);
		featureAttribs.put(FeatureAttributeType.name, name);
		this.featureIsDirty = true;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
		baseAttribs.put(BaseAttributeType.LocationType, type);
		featureAttribs.put(FeatureAttributeType.type, type);
		this.featureIsDirty = true;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
		baseAttribs.put(BaseAttributeType.Longitude, "" + longitude);
		
		this.point = this.geometryFactory.createPoint(new Coordinate(this.longitude, this.latitude));
		
		featureAttribs.put(FeatureAttributeType.point, point);
		this.featureIsDirty = true;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
		baseAttribs.put(BaseAttributeType.Latitude, "" + latitude);
		
		this.point = this.geometryFactory.createPoint(new Coordinate(this.longitude, this.latitude));
		
		featureAttribs.put(FeatureAttributeType.point, point);
		this.featureIsDirty = true;
	}

	public Point getPoint() {
		return point;
	}
	
	public void setPoint(double longitude, double latitude) {
		this.longitude = longitude;
		baseAttribs.put(BaseAttributeType.Longitude, "" + longitude);
		this.latitude = latitude;
		baseAttribs.put(BaseAttributeType.Latitude, "" + latitude);
		
		this.point = this.geometryFactory.createPoint(new Coordinate(this.longitude, this.latitude));
		
		featureAttribs.put(FeatureAttributeType.point, point);
		this.featureIsDirty = true;
	}

	public List<BaseAttributeType> listBaseAttributes() {
		return new ArrayList<BaseAttributeType>(baseAttribs.keySet());
	}

	public String getBaseAttribute(BaseAttributeType baseType) {
		return baseAttribs.get(baseType);
	}

	public List<FeatureAttributeType> listFeatureAttributes() {
		return new ArrayList<FeatureAttributeType>(featureAttribs.keySet());
	}

	public Object getFeatureAttribute(FeatureAttributeType featureType) {
		return featureAttribs.get(featureType);
	}
	
	public SimpleFeature getSimpleFeature() {
		if(this.featureIsDirty) {
			this.simpleFeature = null;
		}
		
		if(this.simpleFeature == null) {
			/**
			 * Must be in order of the SimpleFeatureType defined below in getFeatureType()
			 */
			this.featureBuilder.add(this.point);
			this.featureBuilder.add(this.name);
			this.featureBuilder.add(this.type);
			this.featureBuilder.add(SourceProvider.getStringFromType(this.provider));
			this.simpleFeature = featureBuilder.buildFeature(null);
			
			this.featureIsDirty = false;
		}
		
		return this.simpleFeature;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("SimplePointFeature Instance:");
		sb.append("\tProvider:\t" + SourceProvider.getStringFromType(this.provider) + "\n");
		sb.append("\tName:\t\t" + this.name + "\n");
		sb.append("\tType:\t\t" + this.type + "\n");
		sb.append("\tLongitude:\t" + this.longitude + "\n");
		sb.append("\tLatitude:\t" + this.latitude + "\n");
		sb.append("\tPOINT: " + this.point + "\n");
		
		return sb.toString();
	}
	
	public static SimpleFeatureType getFeatureType() throws SchemaException, NoSuchAuthorityCodeException, FactoryException {
		if(SimplePointFeature.FEATURETYPE == null) {
			synchronized(SimplePointFeature.class) {
				if(SimplePointFeature.FEATURETYPE == null) {
					SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
			        builder.setName("Location");
			        builder.setCRS(CRS.decode("EPSG:4326")); // <- Coordinate reference system

			        // add attributes in order
			        builder.add("the_geom", Point.class);
			        builder.length(32).add("name", String.class);
			        builder.length(32).add("type", String.class);
			        builder.length(32).add("provider", String.class);
			        
			        // build the type
			        SimplePointFeature.FEATURETYPE = builder.buildFeatureType();
				}
			}
		}
		
		return SimplePointFeature.FEATURETYPE;
	}

}
