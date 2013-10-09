package net.david.Activities.NonAuto;

import java.util.HashMap;
import java.util.List;

import net.david.Constantes;
import net.david.Activities.Query;
import net.david.Definitions.QueryProperty;
import net.david.Definitions.NonAuto.EditFormProperty;
import net.david.Definitions.NonAuto.Field;

import org.w3c.dom.DOMException;

import android.app.Activity;
import android.text.InputType;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public final class EditFormQuery extends Query {
	
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
									throws DOMException {
		
		EditFormProperty formProperty = (EditFormProperty) queryProperty;
		
		// Con lastId tendremos el último componente introducido en pantalla
		int lastId = -1;
		String title = formProperty.getTitle();
		if (title != null){
			LayoutParams tvlp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			tvlp.width = LayoutParams.WRAP_CONTENT;
			tvlp.height = LayoutParams.WRAP_CONTENT;
			tvlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
			
			TextView tv = new TextView(act, null, android.R.attr.textAppearanceMedium);
			tv.setId(Constantes.TITULO_FORMULARIO);
			lastId = Constantes.TITULO_FORMULARIO;
			tv.setLayoutParams(tvlp);
			tv.setText(title);
			contenedor.addView(tv);
		}
		
		List<Field> fields = formProperty.getFields();
		if (fields != null){
			for (int i=0; i<fields.size(); i++){
				Field campo = fields.get(i);
				if (campo.getLabel() != null){
					LayoutParams tvlp2 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					tvlp2.width = LayoutParams.WRAP_CONTENT;
					tvlp2.height = LayoutParams.WRAP_CONTENT;
					if (lastId == -1) tvlp2.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
					if (lastId != -1) tvlp2.addRule(RelativeLayout.BELOW, lastId);
					if (lastId != -1) tvlp2.topMargin = (int) (5 * densidad);
					
					TextView tv2 = new TextView(act, null, android.R.attr.textAppearanceMedium);
					tv2.setId(Constantes.TITULO_ITEM_FORMULARIO+i);
					lastId = Constantes.TITULO_ITEM_FORMULARIO+i;
					tv2.setLayoutParams(tvlp2);
					tv2.setText(campo.getLabel());
					contenedor.addView(tv2);
				}
				if (campo.getType().equals("text")){
					LayoutParams etlp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
					etlp.height = LayoutParams.WRAP_CONTENT;
					etlp.width = LayoutParams.FILL_PARENT;
					etlp.bottomMargin = (int) (5 * densidad);

					HashMap<String, String> opciones = campo.getOptions(); // <options>
					EditText et = new EditText(act);
					et.setId(Constantes.CONTENIDO_ITEM_FORMULARIO+i);
					if (lastId != -1) etlp.addRule(RelativeLayout.BELOW, lastId);
					lastId = Constantes.CONTENIDO_ITEM_FORMULARIO+i;
					et.setLayoutParams(etlp);
					et.setLines(1);
					
					if (opciones != null){
						if (opciones.get("initial_text") != null){
							et.setHint(opciones.get("initial_text"));
						}
						if (opciones.get("size") != null){
							if (opciones.get("size").equals("big")){
								et.setInputType(et.getInputType() | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
								et.setGravity(Gravity.TOP);
								et.setHeight((int) (300*densidad));
							}
						}
					}
					contenedor.addView(et);
				}
				else if (campo.getType().equals("email")){
					LayoutParams etlp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
					etlp.height = LayoutParams.WRAP_CONTENT;
					etlp.width = LayoutParams.FILL_PARENT;
					etlp.bottomMargin = (int) (5 * densidad);

					EditText et = new EditText(act);
					et.setId(Constantes.CONTENIDO_ITEM_FORMULARIO+i);
					if (lastId != -1) etlp.addRule(RelativeLayout.BELOW, lastId);
					lastId = Constantes.CONTENIDO_ITEM_FORMULARIO+i;
					et.setLayoutParams(etlp);
					et.setLines(1);
					et.setInputType(et.getInputType() | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
					contenedor.addView(et);
				}
			}
		}
	}
}
