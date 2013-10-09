package net.david.Facts;

import net.david.Activities.Activity;
import net.david.Definitions.ActivityDefinition;

/** Clase que representa una medida asociada a una Activity
 *  @author David
 */
public abstract class Measure {
	private String id_Data;
	// Aqu� podr�a haber otro campo que guardara los datos verdaderos
	// pero har�a muy pesada a la aplicaci�n y petar�a con m�s facilidad
	
	public String getId_Data() {
		return id_Data;
	}

	public void setId_Data(String data) {
		this.id_Data = data;
	}

	public Measure(){
		this.id_Data = "";
	}
	
	/** M�todo que serializa los datos asociados a una Activity en formato XML.
	 *  @author David
	 *  @param activity Activity a serializar
	 *  @param act Activity de Android para poder realizar diversas operaciones
	 *  @return Los datos serializados en forma de ristra de caracteres.
	 */
	public abstract String serializeData(Activity activity, android.app.Activity act);
	
	/** M�todo que devuelve la respresentaci�n de los datos asociados a una Activity
	 *  en formato HTML.
	 *  @author David
	 *  @param act Activity de Android para poder realizar diversas operaciones
	 *  @param activity Activity a serializar
	 *  @param activityDefinition Definici�n de la Activity para obtener algunos datos
	 *  @return Los datos en formato HTML en una ristra de caracteres
	 */
	public abstract String getMeasureToHTML(android.app.Activity act, Activity activity, ActivityDefinition activityDefinition);
	
	/** M�todo que introduce los datos de una Measure en su correspondiente etiqueta.
	 *  @author David
	 *  @retun Una ristra de caracteres conteniendo la etiqueta del Measure con
	 *  con el puntero al fichero con sus datos
	 */
	protected abstract String generateFinalMeasure();
}
