package net.david.Definitions;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public abstract class QueryProperty {
	private String message		= null;
	private boolean slidable	= false;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isSlidable() {
		return slidable;
	}

	public void setSlidable(boolean slidable) {
		this.slidable = slidable;
	}

	public QueryProperty(Element xmlview) {
		// Obtenemos el título de la ventana
		NodeList elementos = xmlview.getElementsByTagName("message"); // <message>
		if (elementos.getLength() != 0)
			setMessage(elementos.item(0).getTextContent());
		
		// Rellenamos los campos
		completaFields(xmlview);
		
		// Marcamos si se pide que la información contenida esté dentro de
		// una vista deslizable o no
		elementos = xmlview.getElementsByTagName("slidable"); // <slidable>
		if (elementos.getLength() != 0){
			if (elementos.item(0).getTextContent().equals("yes")) // Deslizable
				setSlidable(true);
			else if (elementos.item(0).getTextContent().equals("no"))
				setSlidable(false);
		}
	}
	
	public QueryProperty(){}

	/** Método para rellenar los campos particulares de las diferentes Activity's
	 *  @author David
	 *  @param xmlview Nodo XML conteniendo las propiedades de la Activity
	 */
	protected abstract void completaFields(Element xmlview);

}
