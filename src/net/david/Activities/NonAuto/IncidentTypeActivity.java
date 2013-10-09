package net.david.Activities.NonAuto;

import java.util.ArrayList;

import net.david.Constantes;
import net.david.Activities.Activity;
import net.david.Activities.ActivityListener;
import net.david.Definitions.ActivityDefinition;
import net.david.Definitions.NonAuto.IncidentTypeProperty;
import net.david.Facts.NonAuto.IncidentTypeMeasure;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public final class IncidentTypeActivity extends Activity {
	
	private String[] incidencias = null;
	private int position = -1;

	public String[] getIncidencias() {
		return incidencias;
	}

	public void setIncidencias(String[] incidencias) {
		this.incidencias = incidencias;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public IncidentTypeActivity(ActivityDefinition definition,
			android.app.Activity act, ArrayList<ActivityListener> aListeners,
			View view) {
		super(definition, act, aListeners, view);
		
		super.setListeners(aListeners);
		
		this.incidencias = ((IncidentTypeProperty) definition.getQueryDefinition()).getIncidents();
	}

	@Override
	public void createMeasure() {
		this.setMeasure(new IncidentTypeMeasure());
	}

	@Override
	public void copiarActivity(Activity activity, android.app.Activity act) {
		this.position = ((IncidentTypeActivity) activity).position;
	}

	@Override
	public void guardarEstado(android.app.Activity act) {
	}

	@Override
	protected void anadeContenedorInformacion(ActivityDefinition definition,
			android.app.Activity act, View view, float densidad){
		LayoutParams rlilp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		rlilp.height = LayoutParams.WRAP_CONTENT;
		rlilp.width = LayoutParams.FILL_PARENT;
		
		RelativeLayout rli = new RelativeLayout(act);
		rli.setLayoutParams(rlilp);
		
		// Este procedimiento será característico de cada sensor
		// Cada elemento lo añadiremos al RelativeLayout "rli" contenedor de la
		// información. Posteriormente, añadiremos dicho RelativeLayout a la vista
		IncidentTypeQuery incidentTypeQuery = new IncidentTypeQuery();
		incidentTypeQuery.insertaElementos((IncidentTypeProperty) definition.getQueryDefinition(), act, densidad, rli);
		
		// A continuación diferenciamos entre una vista deslizable o no
		// Si es deslizable, metemos el relativeLayout dentro de un scrollview
		// y lo agregamos a la vista
		// Si no es deslizable, configuramos sus parámetros y lo agregamos a la
		// vista directamente
		agregaInformacionVista(definition, act, view, densidad, rlilp, rli, Constantes.ACTIVIDAD_NO_AUTOMATICA);
	}

	@Override
	public Object saveData(android.app.Activity act) {
		return null;
	}

	@Override
	public void run(final android.app.Activity act) {
		ArrayAdapter<String> adaptador = new ArrayAdapter<String>(act, android.R.layout.simple_list_item_1, incidencias);
			 
		ListView lstIncidencias = (ListView) act.findViewById(Constantes.CONTENIDO_LISTA);
		lstIncidencias.setAdapter(adaptador);
		
		lstIncidencias.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int pos, long id) {
				position = pos;
				new Wait().execute();
			}
			
			// Simplemente para que el usuario vea el resultado
			class Wait extends AsyncTask<Void, Void, Void>{

				@Override
				protected Void doInBackground(Void... arg0) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					return null;
				}
				@Override
				protected void onPostExecute(Void result){
					aListenersNotify();
				}
			}
		});
		
		if (this.position != -1){
			lstIncidencias.setSelection(position);
		}
	}

	@Override
	public void stop() {
	}

}
