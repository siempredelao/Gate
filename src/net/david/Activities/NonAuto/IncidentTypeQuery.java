package net.david.Activities.NonAuto;

import android.app.Activity;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import net.david.Constantes;
import net.david.Activities.Query;
import net.david.Definitions.QueryProperty;
import net.david.Definitions.NonAuto.IncidentTypeProperty;

public class IncidentTypeQuery extends Query {

	@Override
	protected void insertaElementos(QueryProperty queryProperty, Activity act,
			float densidad, RelativeLayout contenedor) {
		
		IncidentTypeProperty incidentTypeProperty = (IncidentTypeProperty) queryProperty;
		
		// Con lastId tendremos el último componente introducido en pantalla
		int lastId = -1;
		String title = incidentTypeProperty.getTitle();
		if (title != null){
			LayoutParams tvlp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			tvlp.width = LayoutParams.WRAP_CONTENT;
			tvlp.height = LayoutParams.WRAP_CONTENT;
			tvlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
			
			TextView tv = new TextView(act, null, android.R.attr.textAppearanceMedium);
			tv.setId(Constantes.TITULO_LISTA);
			lastId = Constantes.TITULO_LISTA;
			tv.setLayoutParams(tvlp);
			tv.setText(title);
			contenedor.addView(tv);
		}
		
		LayoutParams lvlp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		lvlp.height = LayoutParams.FILL_PARENT;
		lvlp.width = LayoutParams.FILL_PARENT;
		if (lastId == -1) lvlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
		if (lastId != -1) lvlp.addRule(RelativeLayout.BELOW, lastId);
		if (lastId != -1) lvlp.topMargin = (int) (5 * densidad);
		lastId = Constantes.CONTENIDO_LISTA;
		
		ListView lv = new ListView(act);
		lv.setLayoutParams(lvlp);
		lv.setId(Constantes.CONTENIDO_LISTA);
		contenedor.addView(lv);
	}
}
