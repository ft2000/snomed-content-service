package org.ihtsdo.otf.refset.domain;

public enum RefsetType {

	simple("Simple Type"), complex("Complex Type");
	
	private final String display;

	private RefsetType(String display) {
		
		this.display = display;
		
	}
	
	@Override
	public String toString() {
		return display;
	}
	
	public String getName() {
		
		return this.name();
	}
	
}
