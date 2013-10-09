package net.david.Activities.NonAuto;

import java.util.ArrayList;

import net.david.Constantes;
import net.david.Activities.Activity;
import net.david.Activities.ActivityListener;
import net.david.Definitions.ActivityDefinition;
import net.david.Definitions.NonAuto.EditFormProperty;
import net.david.Facts.NonAuto.EditFormMeasure;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public final class EditFormActivity extends Activity {
	
	private ArrayList<String> contenido;

	public ArrayList<String> getContenido() {
		return contenido;
	}
	
	@Override
	public void createMeasure(){
		this.setMeasure(new EditFormMeasure());
	}
	
	public EditFormActivity(ActivityDefinition definition, android.app.Activity act,
							ArrayList<ActivityListener> aListeners, View view) {
		super(definition, act, aListeners, view);
		
		// No hace falta pero BAH!
		super.setListeners(aListeners);

		this.contenido = new ArrayList<String>();
	}

	@Override
	public void anadeContenedorInformacion(ActivityDefinition definition, android.app.Activity act,
			View view, final float densidad){
		LayoutParams rlilp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		rlilp.height = LayoutParams.WRAP_CONTENT;
		rlilp.width = LayoutParams.FILL_PARENT;
		
		RelativeLayout rli = new RelativeLayout(act);
		rli.setLayoutParams(rlilp);
		
		// Este procedimiento será característico de cada sensor
		// Cada elemento lo añadiremos al RelativeLayout "rli" contenedor de la
		// información. Posteriormente, añadiremos dicho RelativeLayout a la vista
		EditFormQuery formQuery = new EditFormQuery();
		formQuery.insertaElementos((EditFormProperty) definition.getQueryDefinition(), act, densidad, rli);
		
		// A continuación diferenciamos entre una vista deslizable o no
		// Si es deslizable, metemos el relativeLayout dentro de un scrollview
		// y lo agregamos a la vista
		// Si no es deslizable, configuramos sus parámetros y lo agregamos a la
		// vista directamente
		agregaInformacionVista(definition, act, view, densidad, rlilp, rli, Constantes.ACTIVIDAD_NO_AUTOMATICA);
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public void copiarActivity(Activity activity, android.app.Activity act){
		this.contenido = (ArrayList<String>) ((EditFormActivity) activity).contenido.clone();
		
		for (int indice=0; indice<this.contenido.size(); indice++){
			EditText et = (EditText) act.findViewById(Constantes.CONTENIDO_ITEM_FORMULARIO+indice);
			et.setText(this.contenido.get(indice));
		}
	}
	
	@Override
	public void guardarEstado(android.app.Activity act) {
		int indice = 0;
		EditText et = (EditText) act.findViewById(Constantes.CONTENIDO_ITEM_FORMULARIO+indice);
		if (this.contenido.size() == 0){
			while (et != null){
				this.contenido.add(indice, et.getText().toString());
				indice++;
				et = (EditText) act.findViewById(Constantes.CONTENIDO_ITEM_FORMULARIO+indice);
			}
		} else {
			while (et != null){
				this.contenido.set(indice, et.getText().toString());
				indice++;
				et = (EditText) act.findViewById(Constantes.CONTENIDO_ITEM_FORMULARIO+indice);
			}
		}
		
	}	
	
	@Override
	public void run(android.app.Activity act) {
		// No hace nada
		ImageView opciones = (ImageView) act.findViewById(Constantes.BOTON_OPCIONES);
		opciones.setEnabled(false);
		opciones.setVisibility(ImageView.VISIBLE);
	}

	@Override
	public Object saveData(android.app.Activity act) {
		guardarEstado(act);
		// No hace nada en una activity no-automática
		return null;
	}

	@Override
	public void stop() {
		// Nada que hacer
	}
}
