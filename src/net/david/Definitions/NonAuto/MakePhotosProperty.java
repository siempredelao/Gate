package net.david.Definitions.NonAuto;

import net.david.Definitions.QueryProperty;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public final class MakePhotosProperty extends QueryProperty {
	private String title;
	private int maxPhotos;
	
	public MakePhotosProperty(Element xmlview){
		super(xmlview);
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public int getMaxPhotos() {
		return maxPhotos;
	}

	public void setMaxPhotos(int maxPhotos) {
		this.maxPhotos = maxPhotos;
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
				setTitle(null); // 
		} else
			setTitle(null); // No se explicita la etiqueta
		
		elementos = xmlview.getElementsByTagName("maxphotos"); // <maxphotos>
		if (elementos != null)
			setMaxPhotos(Integer.valueOf(elementos.item(0).getTextContent()));
		else
			setMaxPhotos(Integer.MAX_VALUE); // Si no hay máximo de fotos, a cero
	}
}
