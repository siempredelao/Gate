package net.david.Activities.NonAuto;

import java.util.ArrayList;

import net.david.Constantes;
import net.david.R;
import net.david.Activities.Activity;
import net.david.Activities.ActivityListener;
import net.david.Definitions.ActivityDefinition;
import net.david.Definitions.NonAuto.GetCoordinatesProperty;
import net.david.Facts.NonAuto.GetCoordinatesMeasure;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public final class GetCoordinatesActivity extends Activity {

	private TextView posicion				= null;
	private Location location				= null;

	private android.app.Activity a			= null;
	
	private ImageView opciones				= null;

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public GetCoordinatesActivity(ActivityDefinition definition, android.app.Activity act,
							ArrayList<ActivityListener> aListeners, View view) {
		super(definition, act, aListeners, view);
		
		super.setListeners(aListeners);
		this.a = act;
	}

	@Override
	public void createMeasure() {
		this.setMeasure(new GetCoordinatesMeasure());
	}

	@Override
	public void copiarActivity(Activity activity, android.app.Activity act) {
		this.location = ((GetCoordinatesActivity) activity).location;
	}

	@Override
	public void guardarEstado(android.app.Activity act) {
		// No hace nada
	}

	@Override
	protected void anadeContenedorInformacion(ActivityDefinition definition,
			android.app.Activity act, View view, float densidad){

		LayoutParams rlilp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		rlilp.height = LayoutParams.WRAP_CONTENT;
		rlilp.width = LayoutParams.FILL_PARENT;
		
		RelativeLayout rli = new RelativeLayout(act);
		rli.setLayoutParams(rlilp);
		
		// Este procedimiento será característico de cada sensor
		// Cada elemento lo añadiremos al RelativeLayout "rli" contenedor de la
		// información. Posteriormente, añadiremos dicho RelativeLayout a la vista
		GetCoordinatesQuery gpsQuery = new GetCoordinatesQuery();
		gpsQuery.insertaElementos((GetCoordinatesProperty) definition.getQueryDefinition(), act, densidad, rli);
		
		// A continuación diferenciamos entre una vista deslizable o no
		// Si es deslizable, metemos el relativeLayout dentro de un scrollview
		// y lo agregamos a la vista
		// Si no es deslizable, configuramos sus parámetros y lo agregamos a la
		// vista directamente
		agregaInformacionVista(definition, act, view, densidad, rlilp, rli, Constantes.ACTIVIDAD_NO_AUTOMATICA);
	}

	@Override
	public Object saveData(android.app.Activity act) {
		return null;
	}

	@Override
	public void run(final android.app.Activity act) {
		posicion = (TextView) act.findViewById(Constantes.CONTENIDO_GPS);
		
		new SearchPosition().execute();
        
        opciones = (ImageView) act.findViewById(Constantes.BOTON_OPCIONES);
        opciones.setEnabled(true);
		opciones.setVisibility(ImageView.VISIBLE);
        opciones.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(Constantes.DIALOGO_GPS, act);
			}
		});
	}
	
	private LocationManager locManager;
	private VeggsterLocationListener mVeggsterLocationListener;
	public double lati = 0.0;
	public double longi = 0.0;
	
	/** Clase que implementa la búsqueda de la ubicación GPS */
	public class VeggsterLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            try {
                lati = location.getLatitude();
                longi = location.getLongitude();
                setLocation(location);
            } catch (Exception e) {
            }

        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(a, a.getText(R.string.gps_off).toString(), Toast.LENGTH_LONG).show();
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
		private ProgressDialog progressDialog = null;
		
		@Override
		protected void onPreExecute(){
			progressDialog = new ProgressDialog(a);
			progressDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					SearchPosition.this.cancel(true);
				}
			});
			progressDialog.setCancelable(true);
			progressDialog.setMessage(a.getText(R.string.get_position).toString());
			progressDialog.show();
			mVeggsterLocationListener = new VeggsterLocationListener();
			locManager = (LocationManager) a.getSystemService(Context.LOCATION_SERVICE);
			locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					Constantes.GPS_INTERVALO,
					Constantes.GPS_DISTANCIA_METROS,
					mVeggsterLocationListener);
			
		}
		
		@Override
		protected void onProgressUpdate(Void... values){
			// No hace nada
		}
		
		// SOLAMENTE EL MÉTODO doInBackground(...) SE EJECUTA EN EL HILO SECUNDARIO
		// ¡EL RESTO LO HACE EN EL HILO PRINCIPAL!
		@Override
		protected Void doInBackground(Void... params) {
			Thread.currentThread().setName("GetCoordinatesActivity");
			while (lati == 0.0){}
			return null;
		}
		
		@Override
    	protected void onCancelled(){
			posicion.setText(a.getText(R.string.search_cancelled).toString());
			progressDialog.dismiss();
			locManager.removeUpdates(mVeggsterLocationListener);
		}
		
		@Override
		protected void onPostExecute(Void result){
			progressDialog.dismiss();
			locManager.removeUpdates(mVeggsterLocationListener);
			if (lati != 0.0){
		    	posicion.setText(a.getText(R.string.position_found_lati).toString() + lati +
		    						a.getText(R.string.position_found_longi).toString() + longi);
		    	System.out.println("Posición encontrada - Latitud: " + lati + "\nLongitud: " + longi);
				new Wait().execute();
			} else 
				Toast.makeText(a, a.getText(R.string.no_found_position).toString(), Toast.LENGTH_LONG).show();
		}
		
		// Simplemente para que el usuario vea el resultado
		class Wait extends AsyncTask<Void, Void, Void>{

			@Override
			protected Void doInBackground(Void... arg0) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return null;
			}
			@Override
			protected void onPostExecute(Void result){
				aListenersNotify();
			}
		}
	}
	
	
	protected Dialog showDialog(int id, android.app.Activity activity) {
    	Dialog dialogo = null;

    	switch(id){
    		case Constantes.DIALOGO_GPS:
    			dialogo = crearDialogoGPS(activity);
    			break;
    		default:
    			dialogo = null;
    			break;
    	}
    	dialogo.show();
    	return dialogo;
    }
	
	// Tratamiento del diálogo de BUSCAR POSICIÓN
    public Dialog crearDialogoGPS(final android.app.Activity activity){
    	final ArrayList<Item> items = new ArrayList<Item>();
    	items.add(new Item(a.getText(R.string.buscar_gps).toString(), android.R.drawable.ic_menu_myplaces));

		ListAdapter adapter = new ArrayAdapter<Item>(
		    activity,
		    android.R.layout.select_dialog_item,
		    android.R.id.text1,
		    items){
		        public View getView(int position, View convertView, ViewGroup parent) {
		            // User super class to create the View
		            View v = super.getView(position, convertView, parent);
		            TextView tv = (TextView)v.findViewById(android.R.id.text1);

		            // Put the image on the TextView
		            tv.setCompoundDrawablesWithIntrinsicBounds(items.get(position).getIcon(), 0, 0, 0);

		            // Add margin between image and text (support various screen densities)
		            int dp5 = (int) (5 * activity.getResources().getDisplayMetrics().density + 0.5f);
		            tv.setCompoundDrawablePadding(dp5);

		            return v;
		        }
		    };

	    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    	
	    builder.setTitle(net.david.R.string.selecciona);
    	builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int item) {
	        	switch(item){
		        	case 0:	// Volver a buscar la posición
		        		System.out.println("Volviendo a buscar la posición");
		        		new SearchPosition().execute();
		        		break;
	        	}
	        }
	    });
    	
    	return builder.create();
    }

	@Override
	public void stop() {
		locManager.removeUpdates(mVeggsterLocationListener);
	}

}
