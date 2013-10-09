package net.david.Facts;

import net.david.Activities.Activity;
import net.david.Definitions.ActivityDefinition;

/** Clase que representa una medida asociada a una Activity
 *  @author David
 */
public abstract class Measure {
	private String id_Data;
	// Aquí podría haber otro campo que guardara los datos verdaderos
	// pero haría muy pesada a la aplicación y petaría con más facilidad
	
	public String getId_Data() {
		return id_Data;
	}

	public void setId_Data(String data) {
		this.id_Data = data;
	}

	public Measure(){
		this.id_Data = "";
	}
	
	/** Método que serializa los datos asociados a una Activity en formato XML.
	 *  @author David
	 *  @param activity Activity a serializar
	 *  @param act Activity de Android para poder realizar diversas operaciones
	 *  @return Los datos serializados en forma de ristra de caracteres.
	 */
	public abstract String serializeData(Activity activity, android.app.Activity act);
	
	/** Método que devuelve la respresentación de los datos asociados a una Activity
	 *  en formato HTML.
	 *  @author David
	 *  @param act Activity de Android para poder realizar diversas operaciones
	 *  @param activity Activity a serializar
	 *  @param activityDefinition Definición de la Activity para obtener algunos datos
	 *  @return Los datos en formato HTML en una ristra de caracteres
	 */
	public abstract String getMeasureToHTML(android.app.Activity act, Activity activity, ActivityDefinition activityDefinition);
	
	/** Método que introduce los datos de una Measure en su correspondiente etiqueta.
	 *  @author David
	 *  @retun Una ristra de caracteres conteniendo la etiqueta del Measure con
	 *  con el puntero al fichero con sus datos
	 */
	protected abstract String generateFinalMeasure();
}
