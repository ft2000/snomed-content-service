/**
 * 
 */
package org.ihtsdo.otf.snomed.domain;

import java.io.Serializable;

import javax.xml.datatype.XMLGregorianCalendar;

import org.ihtsdo.otf.snomed.service.RdfEnums;
import org.joda.time.DateTime;
import org.openrdf.model.Value;
import org.openrdf.model.util.Literals;
import org.springframework.util.StringUtils;

/**
 * @author Episteme Partners
 *
 */
public class Concept implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String BASE_URI = "http://sct.snomed.info/";
	private static final String SCT_NS = "http://sct.snomed.info/#";
	//private static final String RDF_NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	private static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";

	private String id;

	private String module;
	
	private DateTime effectiveTime;
	
	private String label;
	
	private String casesignificance;
	
	private String type;
	
	private boolean active;
	
	private String modifier;
	
	private String characteristictype;
	
	private String group;

	/**
	 * @return the module
	 */
	public String getModule() {
		return module;
	}

	/**
	 * @param module the module to set
	 */
	public void setModule(String module) {
		this.module = module;
	}

	/**
	 * @return the effectiveTime
	 */
	public DateTime getEffectiveTime() {
		return effectiveTime;
	}

	/**
	 * @param effectiveTime the effectiveTime to set
	 */
	public void setEffectiveTime(DateTime effectiveTime) {
		this.effectiveTime = effectiveTime;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the casesignificance
	 */
	public String getCasesignificance() {
		return casesignificance;
	}

	/**
	 * @param casesignificance the casesignificance to set
	 */
	public void setCasesignificance(String casesignificance) {
		this.casesignificance = casesignificance;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @return the modifier
	 */
	public String getModifier() {
		return modifier;
	}

	/**
	 * @param modifier the modifier to set
	 */
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	/**
	 * @return the characteristictype
	 */
	public String getCharacteristictype() {
		return characteristictype;
	}

	/**
	 * @param characteristictype the characteristictype to set
	 */
	public void setCharacteristictype(String characteristictype) {
		this.characteristictype = characteristictype;
	}

	/**
	 * @return the group
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	public void addProperties(Value bName, Value bValue) {
		
		if (bName != null && !(StringUtils.isEmpty(bName.stringValue()) && StringUtils.isEmpty(bValue))) {
			
			String name = bName.stringValue();
			
			if (name.equalsIgnoreCase(SCT_NS + RdfEnums.effectiveTime)) {
				
				XMLGregorianCalendar cal = Literals.getCalendarValue(bValue, null);
				
				/*
				 * int year,
            int monthOfYear,
            int dayOfMonth,
            int hourOfDay,
            int minuteOfHour, there is no time component in data so pass 0 0
				 */
				this.effectiveTime = new DateTime(cal.getYear(), cal.getMonth(), cal.getDay(), 0, 0);
				
			} else if (name.equalsIgnoreCase(SCT_NS + RdfEnums.active)) {
				
				this.active = Literals.getBooleanValue(bValue, false);
				
			} else if (name.equalsIgnoreCase(SCT_NS + RdfEnums.group)) {
				
				this.group = StringUtils.delete(bValue.stringValue(), BASE_URI);
				
			} else if (name.equalsIgnoreCase(SCT_NS + RdfEnums.module)) {
				
				this.module = StringUtils.delete(bValue.stringValue(), BASE_URI);
				
			} else if (name.equalsIgnoreCase(SCT_NS + RdfEnums.casesignificance)) {
				
				this.casesignificance = StringUtils.delete(bValue.stringValue(), BASE_URI);
				
			} else if (name.equalsIgnoreCase(SCT_NS + RdfEnums.characteristictype)) {
				
				this.characteristictype = StringUtils.delete(bValue.stringValue(), BASE_URI);
				
			} else if (name.equalsIgnoreCase(RDFS_NS + RdfEnums.label)) {
				
				this.label = Literals.getLabel(bValue, bValue.stringValue());
				
			} else if (name.equalsIgnoreCase(SCT_NS + RdfEnums.modifier)) {
				
				this.modifier = StringUtils.delete(bValue.stringValue(), BASE_URI);
				
			} else if (name.equalsIgnoreCase(SCT_NS + RdfEnums.type)) {
				
				this.type = StringUtils.delete(bValue.stringValue(), BASE_URI);
				
			} 
		}
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.format("Concept [effectiveTime = %s, active = %s, casesignificance = %s"
				+ "characteristictype = %s, group = %s, id = %s, label = %s, modifer = %s"
				+ "module = %s, type = %s",  this.effectiveTime, this.active, this.casesignificance
				, this.characteristictype, this.group, this.id, this.label, this.modifier, this.module
				, this.type);
	}
	

}