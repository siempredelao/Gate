package net.david.Gates;

import java.util.ArrayList;

import net.david.Definitions.GateDefinition;

public class GatePresenter {
	private android.app.Activity act;
	private GateDefinition definition;
	private Gate gate;
	
	public GatePresenter(android.app.Activity activity, GateDefinition definition){
		this.act = activity;
		this.definition = definition;
	}
	
	/** Procedimiento que crea la Gate.
	 * @author David
	 * @param glisteners Los listeners asociados
	 * @return La Gate creada
	 */
	public Gate create(ArrayList<GateFinishedListener> glisteners){
		System.gc();
		GateFactory gateFactory = new GateFactory();
		gate = gateFactory.load(definition, this.act, glisteners);
		return gate;
	}
	
	/** Procedimiento que ejecuta la Gate.
	 * @author David
	 * @param gate La Gate a ejecutar
	 */
	public void run(Gate gate){
		gate.execute(definition, this.act, null);
	}
}
