package net.david.Activities.Auto;

import net.david.Constantes;
import net.david.Activities.Query;
import net.david.Definitions.QueryProperty;
import net.david.Definitions.Auto.RecordCoordinatesProperty;

import org.w3c.dom.DOMException;

import android.app.Activity;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public final class RecordCoordinatesQuery extends Query {
	
	/** Procedimiento que añade los elementos que se explicitan en el documento
	 *  XML al RelativeLayout.
	 * @param xmlview
	 * @param act
	 * @param densidad
	 * @param contenedor
	 * @throws DOMException
	 */
	@Override
	public void insertaElementos(	QueryProperty queryProperty,
									Activity act,
									final float densidad,
									RelativeLayout contenedor)
									throws DOMException, IllegalStateException {
		
		RecordCoordinatesProperty gpsProperty = (RecordCoordinatesProperty) queryProperty;
		
		int lastId = -1;
		String title = gpsProperty.getTitle();
		if (title != null){
			LayoutParams tvlp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			tvlp.width = LayoutParams.WRAP_CONTENT;
			tvlp.height = LayoutParams.WRAP_CONTENT;
			tvlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
			
			TextView tv = new TextView(act, null, android.R.attr.textAppearanceMedium);
			tv.setId(Constantes.TITULO_GPS);
			lastId = Constantes.TITULO_GPS;
			tv.setLayoutParams(tvlp);
			tv.setText(title);
			contenedor.addView(tv);
		}
		
		LayoutParams glp = new LayoutParams(LayoutParams.FILL_PARENT, (int) (300 * densidad));
		glp.height = (int) (300 * densidad);
		glp.width = LayoutParams.FILL_PARENT;
		if (lastId == -1) glp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
		if (lastId != -1) glp.addRule(RelativeLayout.BELOW, lastId);
		if (lastId != -1) glp.topMargin = (int) (5 * densidad);
		lastId = Constantes.CONTENIDO_GPS;
		
		TextView tv = new TextView(act, null, android.R.attr.textAppearanceSmall);
		tv.setId(Constantes.CONTENIDO_GPS);
		lastId = Constantes.CONTENIDO_GPS;
		tv.setLayoutParams(glp);
		tv.setText("");
		contenedor.addView(tv);
	}
}
