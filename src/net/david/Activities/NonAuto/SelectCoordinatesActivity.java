package net.david.Activities.NonAuto;

import java.util.ArrayList;

import net.david.Constantes;
import net.david.R;
import net.david.Activities.Activity;
import net.david.Activities.ActivityListener;
import net.david.Definitions.ActivityDefinition;
import net.david.Definitions.NonAuto.SelectCoordinatesProperty;
import net.david.Facts.NonAuto.SelectCoordinatesMeasure;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public final class SelectCoordinatesActivity extends Activity implements LocationListener {
	
	private boolean posicionarMarcador;
	private LocationManager locManager;
	private GeoPoint point;
	private String altaPrecision; // Proveedor de alta precisión
	private Location usted_esta_aqui;
	private MyOverlay marker;
	private Mapas mapas;
	private android.app.Activity activity;
	
	private ImageView opciones				= null;

	public Mapas getMapas() {
		return mapas;
	}
	
	@Override
	public void createMeasure(){
		this.setMeasure(new SelectCoordinatesMeasure());
	}
	
	public SelectCoordinatesActivity(ActivityDefinition definition, android.app.Activity act,
			ArrayList<ActivityListener> aListeners, View view) {
		super(definition, act, aListeners, view);
		
		// No hace falta pero BAH!
		super.setListeners(aListeners);

		this.usted_esta_aqui = null;
		this.posicionarMarcador = true;
		this.locManager = null;
		this.point = null;
		this.altaPrecision = null;
		this.marker = null;
		this.mapas = null;
		this.activity = act;
	}
	
	@Override
	public void anadeContenedorInformacion(ActivityDefinition definition, android.app.Activity act,
			View view, final float densidad){
		LayoutParams rlilp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		rlilp.height = LayoutParams.WRAP_CONTENT;
		rlilp.width = LayoutParams.FILL_PARENT;
		
		RelativeLayout rli = new RelativeLayout(act);
		rli.setLayoutParams(rlilp);
		
		// Este procedimiento será característico de cada sensor
		// Cada elemento lo añadiremos al RelativeLayout "rli" contenedor de la
		// información. Posteriormente, añadiremos dicho RelativeLayout a la vista
		SelectCoordinatesQuery gpsQuery = new SelectCoordinatesQuery();
		gpsQuery.insertaElementos((SelectCoordinatesProperty) definition.getQueryDefinition(), act, densidad, rli);
		
		// A continuación diferenciamos entre una vista deslizable o no
		// Si es deslizable, metemos el relativeLayout dentro de un scrollview
		// y lo agregamos a la vista
		// Si no es deslizable, configuramos sus parámetros y lo agregamos a la
		// vista directamente
		agregaInformacionVista(definition, act, view, densidad, rlilp, rli, Constantes.ACTIVIDAD_NO_AUTOMATICA);
	}
	
	
	@Override
	public void copiarActivity(Activity activity, android.app.Activity act){
		this.usted_esta_aqui = ((SelectCoordinatesActivity) activity).usted_esta_aqui;
		this.posicionarMarcador = ((SelectCoordinatesActivity) activity).posicionarMarcador;
		this.locManager = ((SelectCoordinatesActivity) activity).locManager;
		this.point = ((SelectCoordinatesActivity) activity).point;
		this.altaPrecision = ((SelectCoordinatesActivity) activity).altaPrecision;
		this.mapas = ((SelectCoordinatesActivity) activity).mapas;
	}
	
	@Override
	public void guardarEstado(android.app.Activity act) {
		// Las operaciones de modificación de los atributos se hacen directamente
		// sobre los mismos, así que este procedimiento no hace nada en este caso
	}
	
	@Override
	public void run(final android.app.Activity act) {
		// Obtenemos una referencia a los controles
	    final MapView mapa = (MapView) act.findViewById(Constantes.CONTENIDO_MAPA);
        // Mostramos los controles de zoom sobre el mapa
        mapa.setBuiltInZoomControls(true);
        // Seleccionamos la capa satélite
        mapa.setSatellite(true);
        // Establecemos el zoom
        mapa.getController().setZoom(Constantes.ZOOM);
        
        locManager = (LocationManager) act.getSystemService(Context.LOCATION_SERVICE);
        
        Criteria c = new Criteria();
        
        altaPrecision = locManager.getBestProvider(c, true);
        
        locManager.requestLocationUpdates(altaPrecision,
											Constantes.GPS_INTERVALO,
											Constantes.GPS_DISTANCIA_METROS,
											this);
        
        if (mapas == null)
        mapas = new Mapas(point, posicionarMarcador, marker, mapa, act,
        				act.getText(net.david.R.string.ubicacion).toString(), altaPrecision);
        
        if (mapas.getMarker() != null){
        	Location aux = new Location(this.altaPrecision);
        	aux.setLatitude(mapas.getMarker().getPoint().getLatitudeE6()/1E6);
        	aux.setLongitude(mapas.getMarker().getPoint().getLongitudeE6()/1E6);
        	mapas.updateLocation(aux);
        } else
        	mapas.updateLocation(locManager.getLastKnownLocation(altaPrecision));
        
        opciones = (ImageView) act.findViewById(Constantes.BOTON_OPCIONES);
        opciones.setEnabled(true);
		opciones.setVisibility(ImageView.VISIBLE);
        opciones.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(Constantes.DIALOGO_MAPA, act);
			}
		});
	}

	protected Dialog showDialog(int id, android.app.Activity activity) {
    	Dialog dialogo = null;

    	switch(id){
    		case Constantes.DIALOGO_BUSQUEDA:
    			dialogo = crearDialogoBusqueda(activity);
    			break;
    		case Constantes.DIALOGO_MAPA:
    			dialogo = crearDialogoSeleccionM(activity);
    			break;
    		default:
    			dialogo = null;
    			break;
    	}
    	dialogo.show();
    	return dialogo;
    }
	
	// Tratamiento del diálogo de MAPA
	private Dialog crearDialogoSeleccionM(final android.app.Activity activity){
    	final ArrayList<Item> items = new ArrayList<Item>();
    	items.add(new Item(activity.getText(R.string.move_to_current_position).toString(), android.R.drawable.ic_menu_mylocation));
    	items.add(new Item(activity.getText(R.string.search_position).toString(), android.R.drawable.ic_menu_search));

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
	        	case 0:	// Ir a posición actual, sin posicionar el marcador
	        			mapas.setPosicionarMarcador(false);
	        			usted_esta_aqui = locManager.getLastKnownLocation(altaPrecision);
	        			mapas.updateLocation(usted_esta_aqui);
	        			mapas.setPosicionarMarcador(true);
	        			break;
	        	case 1:	// Buscar una posición
	        			showDialog(Constantes.DIALOGO_BUSQUEDA, activity);
	        			// Esto se hace con getFromLocationName
    					break;
	        	}
	        }
	    });
    	
    	return builder.create();
    }
	
	
	// Tratamiento del diálogo de BUSCAR POSICIÓN
    public Dialog crearDialogoBusqueda(android.app.Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        
        final EditText textoBusqueda = new EditText(activity);
        builder.setTitle(net.david.R.string.buscar);	// Título
        builder.setView(textoBusqueda);
        builder.setPositiveButton(net.david.R.string.aceptar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	// Tomar lo que escribió el usuario y dar direcciones posibles
            	if (textoBusqueda != null){
	            	mapas.setPosicionarMarcador(false);
	            	mapas.updateLocation(mapas.dameDirecciones(textoBusqueda.getText().toString()));
	            	mapas.setPosicionarMarcador(true);
            	}
            }
        });
        return builder.create();
    }
    
    

	@Override
	public void onLocationChanged(Location location) {
		// Si descomentamos esta línea siempre vuelve al punto actual y no nos
		// interesa en esta activity
//		mapas.updateLocation(location);
	}

	@Override
	public void onProviderDisabled(String provider) { }	

	@Override
	public void onProviderEnabled(String provider) { }


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) { }

	@Override
	public Object saveData(android.app.Activity act) {
		// No hace nada en una activity no-automática
		return null;
	}

	@Override
	public void stop() {
		// Destruimos el mapa, si existe
		MapView mv = (MapView) this.activity.findViewById(Constantes.CONTENIDO_MAPA);
		if (mv != null){
			// Hay que hacer que no apunte al padre porque una View
			// no puede tener dos padres
			ViewGroup vp = (ViewGroup) mv.getParent();
			vp.removeView(mv);
		}
		System.gc();
		
		locManager.removeUpdates(this);
	}
}
