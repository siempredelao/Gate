package net.david.Definitions;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class GateDefinition {
	private String label;
	private String type;
	private int rate;
	private String server;
	private int port;
	private String web_service;
	// Lista de "definición activity"
	private ArrayList<ActivityDefinition> activities;
	
	
	public GateDefinition() {
		this.label = "";
		this.type = "";
		this.rate = 1; // 1 por defecto
		this.server = "";
		// this.port a nada
		this.web_service = "";
		this.activities = new ArrayList<ActivityDefinition>();
	}

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

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public String getWeb_service() {
		return web_service;
	}

	public void setWeb_service(String web_service) {
		this.web_service = web_service;
	}
	
	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}
	
	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}
	
	public ArrayList<ActivityDefinition> getActivities() {
		return activities;
	}

	public void setActivities(ArrayList<ActivityDefinition> activities) {
		this.activities = activities;
	}
	
	/** Deserializa un trozo de fichero XML correspondiente a un elemento < gate >
	 *  @author David
	 *  @param gate Nodo XML conteniendo la información de la Gate
	 *  @param activity Activity de Android necesaria para hacer diversas operaciones
	 *  @throws Exception
	 */
	public void deserializeFromXML(Element gate, android.app.Activity activity)
										throws 	ParserConfigurationException,
												SAXException,
												IOException,
												Exception {
		getActivities().clear();
		
		if (gate.getAttribute("type").equals("auto")) 			this.setType("auto");
        else if (gate.getAttribute("type").equals("non-auto"))	this.setType("non-auto");
        else throw new Exception("El tipo " + gate.getAttribute("type") + " no es una Gate válida");
		
		NodeList items = gate.getElementsByTagName("label");	// <label>
		if ((items.getLength() != 0) && (items.item(0).getTextContent() != ""))
			this.setLabel(items.item(0).getTextContent());
		
		items = gate.getElementsByTagName("rate");				// <rate>
		if ((items.getLength() != 0) && (items.item(0).getTextContent() != ""))
			this.setRate(Integer.valueOf(items.item(0).getTextContent()));
		
		items = gate.getElementsByTagName("server");			// <server>
		if ((items.getLength() != 0) && (items.item(0).getTextContent() != ""))
			this.setServer(items.item(0).getTextContent());
		
		items = gate.getElementsByTagName("port");				// <port>
		if ((items.getLength() != 0) && (items.item(0).getTextContent() != ""))
			this.setPort(Integer.valueOf(items.item(0).getTextContent()));
		
		items = gate.getElementsByTagName("web_service");		// <web_service>
		if ((items.getLength() != 0) && (items.item(0).getTextContent() != ""))
			this.setWeb_service(items.item(0).getTextContent());
		
        items = gate.getElementsByTagName("activity");			// Localizamos todos los elementos <activity>
        int i=1;
        ActivityDefinition activityDefinition = null;
        while (i<items.getLength()+1){
        	int j=0;
        	activityDefinition = new ActivityDefinition();
        	activityDefinition.deserializeFromXML((Element) items.item(j));
        	if (i != activityDefinition.getOrder()){
	        	while (i != activityDefinition.getOrder() && (j < items.getLength())){
	            	activityDefinition.deserializeFromXML((Element) items.item(j));
	            	j++;
	        	}
        	}
        	if (i == activityDefinition.getOrder()){
        		getActivities().add(activityDefinition); // La añadimos por orden
        		i++;
        	} else throw new Exception("Ha ocurrido un error. Revise su fichero xml de definición");
        }      
	}
}
