package net.david.Display;

import net.david.Constantes;
import android.app.Activity;
import android.widget.RelativeLayout;

/** Clase que controla la vista en pantalla de la aplicación
 *  @author David
 */
public class MyView{
	
	private RelativeLayout view;
	
	public MyView(Activity context){
		view = new RelativeLayout(context);
		view.setId(Constantes.VISTA);
	}

	public void setView(RelativeLayout view) {
		this.view = view;
	}

	public RelativeLayout getView() {
		return view;
	}
}
