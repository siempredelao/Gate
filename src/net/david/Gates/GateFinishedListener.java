package net.david.Gates;

import java.util.EventListener;

public interface GateFinishedListener extends EventListener {
	/** M�todo para notificar el evento que indica que la Gate ha finalizado.
	 *  @author David
	 */
	public void gateFinishedNotify();
}
