package net.david.Activities;

import java.util.ArrayList;

import net.david.Constantes;
import net.david.Definitions.ActivityDefinition;
import net.david.Facts.Measure;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;

public abstract class Activity {
	private Measure measure;
	private ArrayList<ActivityListener> listeners; // Para las activity's pseudo-automáticas

	public void setMeasure(Measure measure) {
		this.measure = measure;
	}
	
	public Measure getMeasure() {
		return measure;
	}
	
	public ArrayList<ActivityListener> getListeners() {
		return listeners;
	}
	
	public void setListeners(ArrayList<ActivityListener> listeners) {
		this.listeners = listeners;
	}
	
	public Activity(ActivityDefinition definition, android.app.Activity act,
			ArrayList<ActivityListener> aListeners, View view){
		final float DENSIDAD = act.getResources().getDisplayMetrics().density;
		
		// y cuarto, el contenedor de la información
		anadeContenedorInformacion(definition, act, view, DENSIDAD);
	}
	
	/** Método para la notificación del evento que indica que la Activity ha finalizado.
	 *  @author David
	 */
	public void aListenersNotify(){ // Para las activity's que obtienen el dato y avanzan solas
		for (ActivityListener listener: listeners) listener.activityNotify();
	}

	/** Método para la creación de la Measure asociada a una Activity
	 *  @author David
	 */
	public abstract void createMeasure();
	
	/** Método para copiar dos Activity's.
	 *  @author David
	 *  @param activity Activity desde la que se copian los datos
	 *  @param act Activity de Android necesaria para hacer diferentes operaciones
	 */
	public abstract void copiarActivity(Activity activity, android.app.Activity act);
	
	/** Método para conservar el estado de una Activity.
	 *  @author David
	 *  @param act Activity de Android necesaria para hacer diferentes operaciones
	 */
	public abstract void guardarEstado(android.app.Activity act);
	
	/** Método para insertar los elementos de la Activity en la vista.
	 *  @author David
	 *  @param definition Definición de la Activity
	 *  @param act Activity de Android necesaria para hacer diferentes operaciones
	 *  @param view Vista en la que se insertarán los elementos
	 *  @param densidad Densidad de pantalla del terminal móvil
	 */
	protected abstract void anadeContenedorInformacion(ActivityDefinition definition, android.app.Activity act,
			View view, final float densidad);
	
	/** Método auxiliar para agregar el RelativeLayout con todos los elementos
	 *  a la vista. Si se indica, podrá ser dentro de un ScrollView o 
	 *  directamente el propio RelativeLayout.
	 *  @author David
	 *  @param definition Definición de la Activity
	 *  @param act Activity de Android
	 *  @param view Pantalla sobre la que se van a insertar los diferentes elementos
	 *  @param densidad Densidad de la pantalla
	 *  @param rlilp LayoutParams del RelativeLayout rli
	 *  @param rli RelativeLayout contenedor de la información
	 *  @param tipo_actividad Indica si se trata de una actividad automática o no-automática
	 */
	protected void agregaInformacionVista(ActivityDefinition definition, android.app.Activity act,
										View view, float densidad,
										LayoutParams rlilp, RelativeLayout rli,
										boolean tipo_actividad) {
		
		if (tipo_actividad == Constantes.ACTIVIDAD_NO_AUTOMATICA){
			// Si está el antiguo contenedor de información, nos lo cargamos
			View aux = view.findViewWithTag("Info");
			if ((aux != null) && (tipo_actividad == Constantes.ACTIVIDAD_NO_AUTOMATICA)){
				((ViewGroup) view).removeView(aux);
				aux.invalidate();
				aux = null;
			}
			
			// Comprobamos si se pide que la información contenida esté dentro de
			// una vista deslizable o no
			if (definition.getQueryDefinition().isSlidable()){ // Deslizable
				LayoutParams svlp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				svlp.height = LayoutParams.WRAP_CONTENT;
				svlp.width = LayoutParams.FILL_PARENT;
				svlp.leftMargin = (int) (5 * densidad);
				svlp.rightMargin = (int) (5 * densidad);
				svlp.topMargin = (int) (5 * densidad);
				svlp.bottomMargin = (int) (5 * densidad);
				svlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
				svlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
				svlp.addRule(RelativeLayout.ABOVE, Constantes.BOTON_OPCIONES);	// Caso no automático
				svlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
				
				rlilp.height = LayoutParams.FILL_PARENT;
				rlilp.width = LayoutParams.FILL_PARENT;
				rli.setLayoutParams(rlilp);
				
				ScrollView sv = new ScrollView(act);
				sv.setLayoutParams(svlp);
				sv.addView(rli); // Hacemos el RelativeLayout deslizable y lo agregamos a la vista
				sv.setId(Constantes.INFO_S);
				sv.setTag("Info");
				((ViewGroup) view).addView(sv);
			} else {
				rlilp.leftMargin = (int) (5 * densidad);
				rlilp.rightMargin = (int) (5 * densidad);
				rlilp.topMargin = (int) (5 * densidad);
				rlilp.bottomMargin = (int) (5 * densidad);
				rlilp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
				rlilp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
				rlilp.addRule(RelativeLayout.ABOVE, Constantes.BOTON_OPCIONES);
				rlilp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
				
				rli.setLayoutParams(rlilp);
				rli.setId(Constantes.INFO_R);
				rli.setTag("Info");
				((ViewGroup) view).addView(rli);
			}
		} else {
			if (((ViewGroup) view).getChildCount() > 1){ // Existe algo a parte de los botones
				RelativeLayout rltotal = (RelativeLayout) view.findViewById(100000);
				
				rlilp.addRule(RelativeLayout.BELOW, 100000+rltotal.getChildCount());
				rlilp.height = LayoutParams.WRAP_CONTENT;
				rlilp.width = LayoutParams.FILL_PARENT;
				rlilp.topMargin = (int) (5 * densidad);
				rli.setLayoutParams(rlilp);
				rli.setId(100000+rltotal.getChildCount()+1);
				
				rltotal.addView(rli);
			} else if (((ViewGroup) view).getChildCount() == 1){ // Solo existen los botones
				LayoutParams svlp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				svlp.height = LayoutParams.WRAP_CONTENT;
				svlp.width = LayoutParams.FILL_PARENT;
				svlp.leftMargin = (int) (5 * densidad);
				svlp.rightMargin = (int) (5 * densidad);
				svlp.topMargin = (int) (5 * densidad);
				svlp.bottomMargin = (int) (5 * densidad);
				svlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
				svlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
				svlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
				svlp.addRule(RelativeLayout.ABOVE, Constantes.BOTONES);

				rlilp.height = LayoutParams.WRAP_CONTENT;
				rlilp.width = LayoutParams.FILL_PARENT;
				rli.setLayoutParams(rlilp);
				rli.setId(100001);
				
				RelativeLayout rltotal = new RelativeLayout(act);
				rltotal.setId(100000);
				rltotal.addView(rli);
				
				
				ScrollView sv = new ScrollView(act);
				sv.setLayoutParams(svlp);
				sv.addView(rltotal); // Hacemos el RelativeLayout deslizable y lo agregamos a la vista
				sv.setId(Constantes.INFO_S);
				sv.setTag("Info");
				((ViewGroup) view).addView(sv);
			} 
		}
	}

	/** Método para obtener los datos a la hora de generar un hecho. Solo útil
	 *  para Activity's automáticas.
	 *  @author David
	 *  @param act Activity de Android necesaria para hacer diferentes operaciones
	 */
	public abstract Object saveData(android.app.Activity act);
	
	/** Método para iniciar la ejecución de la Activity.
	 *  @author David
	 *  @param act Activity de Android necesaria para hacer diferentes operaciones
	 */
	public abstract void run(android.app.Activity act);

	/** Método para detener solamente la ejecución de la Activity, pero no la vista.
	 *  @author David
	 *  @param act Activity de Android necesaria para hacer diferentes operaciones
	 */
	public abstract void stop();
}
