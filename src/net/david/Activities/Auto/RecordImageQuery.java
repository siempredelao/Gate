package net.david.Activities.Auto;

import net.david.Constantes;
import net.david.Activities.Query;
import net.david.Definitions.QueryProperty;
import net.david.Definitions.Auto.RecordImageProperty;

import org.w3c.dom.DOMException;

import android.app.Activity;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public final class RecordImageQuery extends Query {

	/** Procedimiento que añade los elementos que se explicitan en el documento
	 *  XML al RelativeLayout.
	 * @param xmlview
	 * @param act
	 * @param densidad
	 * @param contenedor
	 * @throws DOMException
	 * @throws NumberFormatException
	 */
	@Override
	public void insertaElementos(	QueryProperty queryProperty,
									Activity act,
									final float densidad,
									RelativeLayout contenedor)
									throws DOMException, NumberFormatException {
		
		RecordImageProperty cameraProperty = (RecordImageProperty) queryProperty;
		
		// Con lastId tendremos el último componente introducido en pantalla
		int lastId = -1;
		String title = cameraProperty.getTitle();
		if (title != null){
			LayoutParams tvlp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			tvlp.width = LayoutParams.WRAP_CONTENT;
			tvlp.height = LayoutParams.WRAP_CONTENT;
			tvlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
			
			TextView tv = new TextView(act, null, android.R.attr.textAppearanceMedium);
			tv.setId(Constantes.TITULO_CAMARA);
			lastId = Constantes.TITULO_CAMARA;
			tv.setLayoutParams(tvlp);
			tv.setText(title);
			contenedor.addView(tv);
		}
		
		LayoutParams sfvlp = new LayoutParams(LayoutParams.FILL_PARENT, (int) (300 * densidad));
		sfvlp.height = (int) (300 * densidad);
		sfvlp.width = LayoutParams.FILL_PARENT;
		if (lastId == -1) sfvlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
		if (lastId != -1) sfvlp.addRule(RelativeLayout.BELOW, lastId);
		if (lastId != -1) sfvlp.topMargin = (int) (5 * densidad);
		lastId = Constantes.CANVAS_CAMARA;
		
		SurfaceView sfv = new SurfaceView(act);
		sfv.setLayoutParams(sfvlp);
		sfv.setId(Constantes.CANVAS_CAMARA);
		contenedor.addView(sfv);
		
		LayoutParams ivlp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		ivlp.height = LayoutParams.WRAP_CONTENT;
		ivlp.width = LayoutParams.WRAP_CONTENT;
		ivlp.addRule(RelativeLayout.ALIGN_LEFT, sfv.getId());
		ivlp.addRule(RelativeLayout.ALIGN_RIGHT, sfv.getId());
		ivlp.addRule(RelativeLayout.ALIGN_TOP, sfv.getId());
		ivlp.addRule(RelativeLayout.ALIGN_BOTTOM, sfv.getId());
		
		ImageView iv = new ImageView(act);
		iv.setId(Constantes.PREVISUALIZA_CAMARA);
		iv.setLayoutParams(ivlp);
		iv.setVisibility(ImageView.INVISIBLE);
		contenedor.addView(iv);
	}
	
}
