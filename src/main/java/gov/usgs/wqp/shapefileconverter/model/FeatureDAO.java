package gov.usgs.wqp.shapefileconverter.model;

import gov.usgs.wqp.shapefileconverter.model.attributes.BaseAttributeType;
import gov.usgs.wqp.shapefileconverter.model.attributes.FeatureAttributeType;

import java.util.List;

import org.opengis.feature.simple.SimpleFeature;

public interface FeatureDAO {
	public List<BaseAttributeType> listBaseAttributes();
	public String getBaseAttribute(BaseAttributeType baseType);
	
	public List<FeatureAttributeType> listFeatureAttributes();
	public Object getFeatureAttribute(FeatureAttributeType featureType);
	
	public SimpleFeature getSimpleFeature();
}
