package net.david.Facts;

import java.util.ArrayList;
import java.util.Date;

import net.david.Constantes;
import net.david.Activities.Activity;

/** Clase que representa un hecho asociado a una Gate
 *  @author David
 */
public class Fact {
	private Date timestamp;
	
	public Fact(){
		timestamp = new java.util.Date();
	}
	
	// Genera un hecho con su timestamp y cada measure
	/** Método que genera un hecho con su marcha de fecha y hora y con cada measure.
	 *  @author David
	 *  @param activities El conjunto de actividades
	 *  @param act Activity de Android para poder realizar diversas operaciones
	 *  @return El contenido del fichero en forma de ristra de caracteres
	 */
	public String serialize(ArrayList<Activity> activities, android.app.Activity act){
		StringBuilder fact = new StringBuilder("<fact ");
		fact.append(generateTimestamp());
		fact.append(generateGuid());
		fact.append(">");
		
		for (Activity activity: activities)
			fact = fact.append("\n" + activity.getMeasure().generateFinalMeasure());
		System.gc();
		
		// Cerrar el hecho
		fact = fact.append("\n</fact>\n");
		return fact.toString();
	}
	
	private String generateTimestamp(){
		return "timestamp=\"" + timestamp.toString() + "\" ";
	}
	
	private String generateGuid(){
		return "guid=\"" + Constantes.guid + "\" ";
	}
}
