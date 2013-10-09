package net.david.Definitions.NonAuto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.david.Definitions.QueryProperty;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

public final class EditFormProperty extends QueryProperty {
	private String title;
	private List<Field> fields;
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public EditFormProperty(Element xmlview){
		super(xmlview);
	}
	
	public List<Field> getFields() {
		return fields;
	}
	public void setFields(List<Field> fields) {
		this.fields = fields;
	}
	
	public void completaFields(Element xmlview) {
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
		
		elementos = xmlview.getElementsByTagName("fields"); // <fields>
		if (elementos.getLength() != 0){
			setFields(new ArrayList<Field>());
			Element nodo1 = (Element) elementos.item(0);
			NodeList campos = nodo1.getElementsByTagName("field"); // <field>, <field>, <field>, <field>,  
			
			if (campos != null){
				for (int i=0; i<campos.getLength(); i++){
					Field field = new Field();
					NamedNodeMap nnm = campos.item(i).getAttributes();
					// Darle la posibilidad de que no tenga título el formulario
					if (nnm.getNamedItem("label") != null){
						field.setLabel(nnm.getNamedItem("label").getNodeValue());
					} else field.setLabel(null);
					
					String tipo = nnm.getNamedItem("type").getNodeValue();
					if (tipo.equals("text")){
						field.setType(tipo);

						Element nodo2 = (Element) campos.item(i);					
						NodeList opciones = nodo2.getElementsByTagName("options"); // <options>
						
						if (opciones != null){
							HashMap<String, String> hashmap = new HashMap<String, String>();
							hashmap.clear();
							// El for está pa' las risas...
							for (int j=0; j<opciones.getLength(); j++){						
								Element nodo3 = (Element) opciones.item(j);
								NodeList campos3 = nodo3.getElementsByTagName("initial_text");
								if (campos3 != null && campos3.getLength() > 0){
									Element opcion = (Element) campos3.item(0);
									hashmap.put("initial_text", opcion.getFirstChild().getNodeValue());
								}
								
								campos3 = nodo3.getElementsByTagName("email");
								if (campos3 != null && campos3.getLength() > 0){
									Element opcion = (Element) campos3.item(0);
									hashmap.put("email", opcion.getFirstChild().getNodeValue());
								}
								
								campos3 = nodo3.getElementsByTagName("size");
								if (campos3 != null && campos3.getLength() > 0){
									Element opcion = (Element) campos3.item(0);
									hashmap.put("size", opcion.getFirstChild().getNodeValue());
								}
							}
							field.setOptions(hashmap);
						}
					} else if (tipo.equals("email"))
						field.setType(tipo);
					getFields().add(field);
				}
			}
		}
	}
}
