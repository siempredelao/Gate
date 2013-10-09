package net.david.Gates;

import java.util.ArrayList;

import net.david.Definitions.GateDefinition;
import android.app.Activity;

public class GateFactory {
	/** M�todo para cargar una Gate atendiendo a su definici�n.
	 *  @author David
	 *  @param definition Definici�n de la Gate
	 *  @param activity Activity de Android necesaria para hacer diversas operaciones
	 *  @param glisteners Listeners que informar�n cuando una Gate haya acabado
	 *  @return Una Gate creada conforme a la definici�n
	 */
	public Gate load(GateDefinition definition, Activity activity, ArrayList<GateFinishedListener> glisteners){
		if (definition != null){
			if (definition.getType().equals("auto"))
				return new AutomaticGate(definition, activity, glisteners);
			else if (definition.getType().equals("non-auto"))
				return new NonAutomaticGate(definition, activity, glisteners);
		}
		return null;
	}

}
