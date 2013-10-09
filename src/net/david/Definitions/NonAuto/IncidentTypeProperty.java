package net.david.Definitions.NonAuto;

import net.david.Definitions.QueryProperty;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class IncidentTypeProperty extends QueryProperty {
	private String title;
	private String[] incidents;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setIncidents(String[] incidents) {
		this.incidents = incidents;
	}

	public String[] getIncidents() {
		return incidents;
	}
	
	public IncidentTypeProperty(Element xmlview) {
		super(xmlview);
	}

	@Override
	protected void completaFields(Element xmlview) {
		NodeList elementos = xmlview.getElementsByTagName("title"); // <title>
		if (elementos != null){
			if (elementos.getLength() > 0){
				if (elementos.item(0).getTextContent().length() != 0)
					this.setTitle(elementos.item(0).getTextContent());
				else
					this.setTitle(null); // No se introduce nada en la etiqueta
			} else
				this.setTitle(null); // 
		} else
			this.setTitle(null); // No se explicita la etiqueta
		
		elementos = xmlview.getElementsByTagName("type"); // <type>
		if (elementos != null)
			if (elementos.getLength() > 0)
				if (elementos.item(0).getTextContent().length() != 0)
					this.setIncidents(elementos.item(0).getTextContent().split(" "));
				else
					this.setIncidents(null);
			else
				this.setIncidents(null);
		else
			this.setIncidents(null);
	}
}