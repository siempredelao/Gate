package net.david.Activities.NonAuto;

import java.io.IOException;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

import net.david.Constantes;
import net.david.R;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.widget.Toast;

/** Clase para representar y generar datos asociados a un mapa.
 * @author David
 */
public class Mapas  {

	private GeoPoint point;
	private boolean posicionarMarcador;
	private MyOverlay marker;
	private MapView mapa;
	private Context contexto;
	private String posicion;

	private String altaPrecision;
	
	public Mapas(GeoPoint p, boolean hayQuePosicionar, MyOverlay overlay,
							MapView miMapa, Context c, String ubicacion,
							String precision){
		point = p;
		posicionarMarcador = hayQuePosicionar;
		marker = overlay;
		mapa = miMapa;
		contexto = c;
		posicion = ubicacion;
		altaPrecision = precision;
	}
	
	public MyOverlay getMarker() {
		return marker;
	}

	public void setMarker(MyOverlay marker) {
		this.marker = marker;
	}

	public boolean isPosicionarMarcador() {
		return posicionarMarcador;
	}

	public void setPosicionarMarcador(boolean posicionarMarcador) {
		this.posicionarMarcador = posicionarMarcador;
	}
	
	// Actualiza la posición
	public void updateLocation(Location location){
		
		if (location == null){
			Toast.makeText(contexto, contexto.getText(R.string.current_position_not_found), Toast.LENGTH_SHORT).show();
			return;
		}
		
        MapController mapController = mapa.getController();
        point = new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6));
        mapController.animateTo(point);
        
        // Esto puede hacer que tarde mucho en situar el mapa en la posición
        Toast.makeText(contexto, posicion + "\n" + obtenerDireccion(point), Toast.LENGTH_SHORT).show();
		
        // Si queremos que además de pulsar, se posicione el marcador
        // Útil cuando movemos a la posición actual sin posicionar el marcador
        if (isPosicionarMarcador()){
	        List<Overlay> mapOverlays = mapa.getOverlays();
	        mapOverlays.clear();
	        setMarker(new MyOverlay(point, null));
	        mapOverlays.add(marker);
	        mapa.invalidate();
        }
	}
	
	// Obtiene una dirección asociada a una coordenada GPS
	public String obtenerDireccion(GeoPoint p) {
		String address = "";
		Geocoder geoC = new Geocoder(contexto, Locale.getDefault());
		try {
            List<Address> addresses = geoC.getFromLocation(
            									p.getLatitudeE6() / 1E6,
            									p.getLongitudeE6() / 1E6, 1);

            if (addresses.size() > 0) {
                for (int i=0; i<addresses.get(0).getMaxAddressLineIndex(); i++)
                   address += addresses.get(0).getAddressLine(i) + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		return address;
	}
	
	// Devuelve la coordenada GPS asociada a una dirección
	public Location dameDirecciones(String address){
    	Geocoder gC = new Geocoder(contexto);
    	Location direccion = null;
    	try {
			List<Address> lista = gC.getFromLocationName(address, 10);
			// Si nos devuelve elementos, actuamos
			if (lista.size() != 0){
				Address aux = lista.get(0);
				if (aux != null){
					direccion = new Location(altaPrecision);
					direccion.setLatitude(aux.getLatitude());
					direccion.setLongitude(aux.getLongitude());
				}
			} else {
				Toast.makeText(contexto, contexto.getText(net.david.R.string.sindireccion), Toast.LENGTH_SHORT).show();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchElementException e){
			e.printStackTrace();
		}
		return direccion;
    }
	
	// Devuelve la imagen asociada a unas coordenadas
	public Bitmap obtenerImagenDeCoordenadas(GeoPoint p) throws MalformedURLException, IOException {
		double lat = p.getLatitudeE6()/1E6; double lon = p.getLongitudeE6()/1E6;
		String direccion = "http://maps.google.com/maps/api/staticmap?" +
							"center=" + String.valueOf(lat) + "," + String.valueOf(lon) +
							"&zoom=20&maptype=hybrid&size=" + Constantes.TAMANIO_PREVISUALIZA +
							"x" + Constantes.TAMANIO_PREVISUALIZA + "&sensor=true";
		
		URL url = new URL(direccion);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.connect();
		return BitmapFactory.decodeStream(conn.getInputStream());
	}

}
