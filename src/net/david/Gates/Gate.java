package net.david.Gates;

import java.util.ArrayList;

import net.david.Definitions.GateDefinition;
import net.david.Display.MyView;
import android.app.Activity;
import android.view.View;

/** Clase que representa una Gate
 *  @author David
 */
public abstract class Gate {
	private ArrayList<GateFinishedListener> glisteners;
	
	private net.david.Activities.Activity actividad;
	private ArrayList<net.david.Activities.Activity> actividades;
	private MyView gView;
	private android.app.Activity mActivity;
	

	protected ArrayList<GateFinishedListener> getGlisteners() {
		return glisteners;
	}

	protected void setGlisteners(ArrayList<GateFinishedListener> glisteners) {
		this.glisteners = glisteners;
	}

	protected net.david.Activities.Activity getActividad() {
		return actividad;
	}

	protected void setActividad(net.david.Activities.Activity actividad) {
		this.actividad = actividad;
	}

	protected MyView getgView() {
		return gView;
	}

	protected void setgView(MyView gView) {
		this.gView = gView;
	}

	protected android.app.Activity getmActivity() {
		return mActivity;
	}

	protected void setmActivity(android.app.Activity mActivity) {
		this.mActivity = mActivity;
	}
	
	protected ArrayList<net.david.Activities.Activity> getActividades() {
		return actividades;
	}

	protected void setActividades(ArrayList<net.david.Activities.Activity> actividades) {
		this.actividades = actividades;
	}
	
	protected void gListenersNotify(){
		for (GateFinishedListener glistener: glisteners) glistener.gateFinishedNotify();
	}

	/** M�todo que ejecuta una Gate
	 *  @author David
	 *  @param definition Definici�n de la Gate
	 *  @param activity Activity de Android para poder hacer diversas operaciones
	 *  @param copia Activity que se usar� cuando durante la ejecuci�n de una
	 *  Gate No-Autom�tica para restaurar la Activity anterior cuando se vuelva atr�s
	 */
	protected abstract void execute(GateDefinition definition, Activity activity, net.david.Activities.Activity copia);

	/** M�todo que construye el layout asociado a una Gate
	 *  @author David
	 *  @param activity Activity de Android para poder hacer diversas operaciones
	 *  @param view Vista sobre la que se construir� la Gate
	 *  @param densidad Densidad de la pantalla del terminal m�vil
	 */
	protected abstract void buildGate(android.app.Activity activity, View view, final float densidad);
}
