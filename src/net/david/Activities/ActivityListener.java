package net.david.Activities;

import java.util.EventListener;

public interface ActivityListener extends EventListener {
	/** Método para notificar el evento que indica que la Activity ha finalizado.
	 *  @author David
	 */
	public void activityNotify();
}
