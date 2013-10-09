package net.david.Definitions.NonAuto;

import java.util.HashMap;

/** Clase que representa los campos de un formulario EditFormProperty.
 *  @author David
 */
public class Field {
	private String label 					= null;
	private String type 					= null;
	private HashMap<String, String> options = null;
	
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public HashMap<String, String> getOptions() {
		return options;
	}
	public void setOptions(HashMap<String, String> options) {
		this.options = options;
	}
}
