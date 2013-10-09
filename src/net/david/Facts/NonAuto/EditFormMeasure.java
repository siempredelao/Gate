package net.david.Facts.NonAuto;

import java.util.ArrayList;
import java.util.Iterator;

import net.david.Activities.Activity;
import net.david.Activities.NonAuto.EditFormActivity;
import net.david.Definitions.ActivityDefinition;
import net.david.Definitions.NonAuto.EditFormProperty;
import net.david.Facts.Measure;
import android.util.Log;

public final class EditFormMeasure extends Measure {
	@Override
	public String serializeData(Activity activity, android.app.Activity act) {
		String serialization = "";
		
		EditFormActivity nafa = (EditFormActivity) activity;
		ArrayList<String> datos_form = nafa.getContenido();
		if (hayDatos(datos_form))
			for (String field: datos_form) serialization += "<field>" + field + "</field>\n";
		return serialization;
	}

	private boolean hayDatos(ArrayList<String> lista_datos) {
		for (String field: lista_datos)
			if (field != null) return true;
		return false;
	}

	@Override
	public String getMeasureToHTML(android.app.Activity act, Activity activity, ActivityDefinition activityDefinition) {
		EditFormActivity fa = (EditFormActivity) activity;
		ArrayList<String> datos_form = fa.getContenido();
		
		Iterator<String> i = datos_form.iterator();
		
		String result = "    <h2>" + activityDefinition.getQueryDefinition().getMessage() + "<h2>\n";
		EditFormProperty fp = (EditFormProperty) activityDefinition.getQueryDefinition();
		
		String datos = ""; String aux="";
		int j=0;
		if (i != null){
			do {
				String s = i.next();
				aux += s;
				datos += "      <p><h4>" + fp.getFields().get(j).getLabel() + ": " + s + "</h4></p>\n";
				j++;
			} while (i.hasNext());
		}
		if (aux.equals("")){
			Log.w("getMeasureToHTML", "Formulario vacío");
			result += "      <h4>Formulario vacío</h4>\n";
		} else
			result += datos;
		
		result += "      <br>\n";
		
		return result;
	}

	@Override
	public String generateFinalMeasure() {
		return "  <measure type=\"form\">\n" + this.getId_Data() + "\n  </measure>";
	}
}
