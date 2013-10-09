package net.david.Activities.NonAuto;

import android.content.Context;

import com.google.android.maps.MapView;

/** Clase patrón Singleton para generar y obtener un único MapView. Es necesario
 *  que sea Singleton porque en Android 2.2 solo podemos tener una instancia de
 *  un MapView en la ejecución de una aplicación.
 *  @author David
 */
public class MapViewGenerator {
	private static MapView mapView = null;
	
	private MapViewGenerator(){ }
	
	public synchronized static final MapView getInstance(Context c, String key){
		if (mapView == null)
			mapView = new MapView(c, key);
		return mapView;
	}
	
	public synchronized static final void deleteInstance(){
		if (mapView != null){
			mapView.postInvalidate();
			mapView = null;
		}
	}
	
	
	// Sobreescribimos el método "clone" para asegurar la instancia única
	public Object clone() throws CloneNotSupportedException{
		throw new CloneNotSupportedException();
	}
}
