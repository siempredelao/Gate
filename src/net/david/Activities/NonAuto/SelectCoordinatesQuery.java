package net.david.Activities.NonAuto;

import net.david.Constantes;
import net.david.Activities.Query;
import net.david.Definitions.QueryProperty;
import net.david.Definitions.NonAuto.SelectCoordinatesProperty;

import org.w3c.dom.DOMException;

import android.app.Activity;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

import com.google.android.maps.MapView;

public final class SelectCoordinatesQuery extends Query {
	
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
		
		SelectCoordinatesProperty mapProperty = (SelectCoordinatesProperty) queryProperty;
		
		int lastId = -1;
		String title = mapProperty.getTitle();
		if (title != null){
			LayoutParams tvlp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			tvlp.width = LayoutParams.WRAP_CONTENT;
			tvlp.height = LayoutParams.WRAP_CONTENT;
			tvlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
			
			TextView tv = new TextView(act, null, android.R.attr.textAppearanceMedium);
			tv.setId(Constantes.TITULO_MAPA);
			lastId = Constantes.TITULO_MAPA;
			tv.setLayoutParams(tvlp);
			tv.setText(title);
			contenedor.addView(tv);
		}
		
		LayoutParams mvlp = new LayoutParams(LayoutParams.FILL_PARENT, (int) (300 * densidad));
		mvlp.height = (int) (300 * densidad);
		mvlp.width = LayoutParams.FILL_PARENT;
		if (lastId == -1) mvlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
		if (lastId != -1) mvlp.addRule(RelativeLayout.BELOW, lastId);
		if (lastId != -1) mvlp.topMargin = (int) (5 * densidad);
		lastId = Constantes.CONTENIDO_MAPA;
		
		try {
			MapView mv = MapViewGenerator.getInstance(act, "0qOb9cBw7Lv-xHNR_LeIaioMhRyX-bV1exD07ew");
			mv.setLayoutParams(mvlp);
			mv.setClickable(true);
			mv.setId(Constantes.CONTENIDO_MAPA);
			contenedor.addView(mv);
		} catch (IllegalStateException e){
			e.printStackTrace();
		}
	}
}
