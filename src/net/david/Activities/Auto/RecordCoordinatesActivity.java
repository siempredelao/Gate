package net.david.Activities.Auto;

import net.david.Constantes;
import net.david.R;
import net.david.Activities.Activity;
import net.david.Definitions.ActivityDefinition;
import net.david.Definitions.Auto.RecordCoordinatesProperty;
import net.david.Facts.Auto.RecordCoordinatesMeasure;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public final class RecordCoordinatesActivity extends Activity{
	
	private TextView posicion					= null;
	private Location location					= null;
	private android.app.Activity a				= null;
	private Location lugar						= null;

	public Location getLugar() {
		return lugar;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public RecordCoordinatesActivity(ActivityDefinition definition, android.app.Activity act, View view)  {
		super(definition, act, null, view);
		
		this.a = act;
	}

	@Override
	public void createMeasure() {
		this.setMeasure(new RecordCoordinatesMeasure());
	}

	@Override
	public void copiarActivity(Activity activity, android.app.Activity act) {
	}

	@Override
	public void guardarEstado(android.app.Activity act) {
	}

	@Override
	protected void anadeContenedorInformacion(ActivityDefinition definition,
			android.app.Activity act, View view, float densidad) {
		LayoutParams rlilp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		rlilp.height = LayoutParams.WRAP_CONTENT;
		rlilp.width = LayoutParams.FILL_PARENT;
		
		RelativeLayout rli = new RelativeLayout(act);
		rli.setLayoutParams(rlilp);
		
		// Este procedimiento será característico de cada sensor
		// Cada elemento lo añadiremos al RelativeLayout "rli" contenedor de la
		// información. Posteriormente, añadiremos dicho RelativeLayout a la vista
		RecordCoordinatesQuery gpsQuery = new RecordCoordinatesQuery();
		gpsQuery.insertaElementos((RecordCoordinatesProperty) definition.getQueryDefinition(), act, densidad, rli);
		
		// A continuación diferenciamos entre una vista deslizable o no
		// Si es deslizable, metemos el relativeLayout dentro de un scrollview
		// y lo agregamos a la vista
		// Si no es deslizable, configuramos sus parámetros y lo agregamos a la
		// vista directamente
		agregaInformacionVista(definition, act, view, densidad, rlilp, rli, Constantes.ACTIVIDAD_AUTOMATICA);
	}

	@Override
	public Object saveData(android.app.Activity act) {
		lugar = location;
		return null;
	}

	public double lati = 0.0;
	public double longi = 0.0;
	private boolean primeravez = true;
	private LocationManager locManager;
	private VeggsterLocationListener mVeggsterLocationListener;
	
	@Override
	public void run(android.app.Activity act) {
		posicion = (TextView) act.findViewById(Constantes.CONTENIDO_GPS);
		Toast.makeText(a, a.getText(R.string.connecting_gps).toString(), Toast.LENGTH_LONG).show();
		new SearchPosition().execute();
	}
	
	/** Clase que implementa la búsqueda de la ubicación GPS */
	public class VeggsterLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
			if ((lati != 0.0) && (primeravez)){
				Toast.makeText(a, a.getText(R.string.gps_ok).toString(), Toast.LENGTH_LONG).show();
				primeravez = false;
			}
            try {
                lati = location.getLatitude();
                longi = location.getLongitude();
                setLocation(location);
                posicion.setText(a.getText(R.string.current_position).toString() + lati + ", " + longi + ")");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            a.startActivity(intent);
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        	switch (status){
            case LocationProvider.AVAILABLE:
            	posicion.setText("Network location available again");
                break;
            case LocationProvider.OUT_OF_SERVICE:
            	posicion.setText("Network location out of service");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
            	posicion.setText("Network location temporarily unavailable");
                break;
            }
        }
    }
	
	/** Clase que implementa la búsqueda de la ubicación GPS de la clase
	 *  "VeggsterLocationListener" en un hilo separado */
	private class SearchPosition extends AsyncTask<Void, Void, Void>{
		public double lati = 0.0;
		
		@Override
		protected void onPreExecute(){
			mVeggsterLocationListener = new VeggsterLocationListener();
			locManager = (LocationManager) a.getSystemService(Context.LOCATION_SERVICE);
			locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					Constantes.GPS_INTERVALO,
					Constantes.GPS_DISTANCIA_METROS,
					mVeggsterLocationListener);
		}
		
		@Override
		protected void onProgressUpdate(Void... values){
		}
		
		// SOLAMENTE EL MÉTODO doInBackground(...) SE EJECUTA EN EL HILO SECUNDARIO
		// ¡EL RESTO LO HACE EN EL HILO PRINCIPAL!
		@Override
		protected Void doInBackground(Void... params) {
			Thread.currentThread().setName("RecordCoordinatesActivity");
			while (this.lati == 0.0){}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result){
		}
	}

	@Override
	public void stop() {
		locManager.removeUpdates(mVeggsterLocationListener);
	}
}
