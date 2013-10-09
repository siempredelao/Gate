package net.david.Definitions.NonAuto;

import net.david.Definitions.QueryProperty;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public final class SelectCoordinatesProperty extends QueryProperty {
	private String title;
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public SelectCoordinatesProperty(Element xmlview){
		super(xmlview);
	}
	
	public void completaFields(Element xmlview) {
		NodeList elementos = xmlview.getElementsByTagName("title"); // <title>
		if (elementos != null){
			if (elementos.getLength() > 0){
				if (elementos.item(0).getTextContent().length() != 0)
					setTitle(elementos.item(0).getTextContent());
				else
					setTitle(null); // No se introduce nada en la etiqueta
			} else
				setTitle(null);
		} else
			setTitle(null); // No se explicita la etiqueta
	}
}
