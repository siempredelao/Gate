package net.david.Activities.NonAuto;

import java.util.ArrayList;

import net.david.Constantes;
import net.david.Activities.Activity;
import net.david.Activities.ActivityListener;
import net.david.Definitions.ActivityDefinition;
import net.david.Definitions.QueryProperty;

import org.w3c.dom.Element;

import android.graphics.Color;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public final class SummaryActivity extends Activity {

	private ImageView opciones = null;

	@Override
	public void createMeasure() {
		// No hace nada
	}
	
	public SummaryActivity(ActivityDefinition definition, android.app.Activity act,
							ArrayList<ActivityListener> aListeners, View view) {
		super(definition, act, aListeners, view);
		
		// No hace falta pero BAH!
		super.setListeners(aListeners);
	}
	
	@Override
	protected void anadeContenedorInformacion(ActivityDefinition definition,
			android.app.Activity act, View view, float densidad) {
		LayoutParams rlilp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		rlilp.height = LayoutParams.WRAP_CONTENT;
		rlilp.width = LayoutParams.FILL_PARENT;
		
		RelativeLayout rli = new RelativeLayout(act);
		rli.setLayoutParams(rlilp);
		
		ActivityDefinition definition2 = new ActivityDefinition();
		definition2.setType("summary");
		
		QueryProperty qp = new QueryProperty() {
			@Override
			protected void completaFields(Element xmlview) {
			}
		};
		qp.setSlidable(false);
		definition2.setQueryDefinition(qp);
		
		// Este procedimiento será característico de cada sensor
		// Cada elemento lo añadiremos al RelativeLayout "rli" contenedor de la
		// información. Posteriormente, añadiremos dicho RelativeLayout a la vista
		LayoutParams vhtmllp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		vhtmllp.width = LayoutParams.WRAP_CONTENT;
		vhtmllp.height = LayoutParams.WRAP_CONTENT;
		vhtmllp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
		
		WebView wv = new WebView(act);
		wv.setBackgroundColor(Color.BLACK);
		wv.setVerticalFadingEdgeEnabled(true);
		wv.setId(Constantes.VISOR_HTML);
		wv.setLayoutParams(vhtmllp);
		rli.addView(wv);
		
		// A continuación diferenciamos entre una vista deslizable o no
		// Si es deslizable, metemos el relativeLayout dentro de un scrollview
		// y lo agregamos a la vista
		// Si no es deslizable, configuramos sus parámetros y lo agregamos a la
		// vista directamente
		agregaInformacionVista(definition2, act, view, densidad, rlilp, rli, Constantes.ACTIVIDAD_NO_AUTOMATICA);
	}
	

	@Override
	public void copiarActivity(Activity activity, android.app.Activity act) {
		// No hace nada
	}

	@Override
	public void guardarEstado(android.app.Activity act) {
		// No hace nada
	}

	@Override
	public void run(android.app.Activity act) {
		opciones = (ImageView) act.findViewById(Constantes.BOTON_OPCIONES);
		opciones.setEnabled(false);
		opciones.setVisibility(ImageView.INVISIBLE);
	}

	@Override
	public Object saveData(android.app.Activity act) {
		// No hace nada en una activity no-automática
		return null;
	}

	@Override
	public void stop() {
		// Nada que hacer
	}
}