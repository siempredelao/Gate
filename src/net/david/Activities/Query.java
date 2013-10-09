package net.david.Activities;

import net.david.Definitions.QueryProperty;
import android.app.Activity;
import android.widget.RelativeLayout;

public abstract class Query {
	/** Método para insertar los diferentes elementos de una Activity en una vista especificada.
	 *  @author David
	 *  @param queryProperty Propiedades de la Activity a insertar
	 *  @param act Activity de Android necesaria para hacer diversas operaciones
	 *  @param densidad Densidad de pantalla del terminal móvil
	 *  @param contenedor Contenedor en el que se van a insertar los elementos
	 */
	protected abstract void insertaElementos(QueryProperty queryProperty,
									Activity act,
									final float densidad,
									RelativeLayout contenedor);
}
