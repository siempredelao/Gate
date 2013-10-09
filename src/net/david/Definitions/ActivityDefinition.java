package net.david.Definitions;

import net.david.Definitions.Auto.RecordCoordinatesProperty;
import net.david.Definitions.Auto.RecordImageProperty;
import net.david.Definitions.NonAuto.EditFormProperty;
import net.david.Definitions.NonAuto.GetCoordinatesProperty;
import net.david.Definitions.NonAuto.IncidentTypeProperty;
import net.david.Definitions.NonAuto.MakePhotosProperty;
import net.david.Definitions.NonAuto.SelectCoordinatesProperty;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ActivityDefinition {
	private int order;
	private String type;
	private QueryProperty queryDefinition;
	

	public void setOrder(int order) {
		this.order = order;
	}

	public int getOrder() {
		return order;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public QueryProperty getQueryDefinition() {
		return queryDefinition;
	}

	public void setQueryDefinition(QueryProperty queryDefinition) {
		this.queryDefinition = queryDefinition;
	}
	
	/** Deserializa un trozo de fichero XML correspondiente a un elemento < activity >
	 *  @author David
	 *  @param activity Nodo XML conteniendo la información de la Activity
	 *  @throws Exception
	 */
	public void deserializeFromXML(Element activity) throws Exception {
		// Obtenemos el atributo "type"
		this.setType(activity.getAttribute("type"));

		NodeList items = activity.getElementsByTagName("order");	// <order>
		if (items.getLength() != 0)
			this.setOrder(Integer.valueOf(items.item(0).getTextContent()));

		Node query = activity.getElementsByTagName("query").item(0); // Localizamos el elemento <query>
		
		if (getType().equals("make_photos"))
			setQueryDefinition(new MakePhotosProperty((Element) query));
		else if (getType().equals("select_coordinates"))
			setQueryDefinition(new SelectCoordinatesProperty((Element) query));
		else if (getType().equals("get_coordinates"))
			setQueryDefinition(new GetCoordinatesProperty((Element) query));
		else if (getType().equals("edit_form"))
			setQueryDefinition(new EditFormProperty((Element) query));
		else if (getType().equals("incident_type"))
			setQueryDefinition(new IncidentTypeProperty((Element) query));
		
		else if (getType().equals("record_image"))
			setQueryDefinition(new RecordImageProperty((Element) query));
		else if (getType().equals("record_coordinates"))
			setQueryDefinition(new RecordCoordinatesProperty((Element) query));
		
		else
			throw new Exception("No se ha encontrado la actividad \"" + activity.getAttribute("type") + "\".");
	}

}
