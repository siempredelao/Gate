package net.david.Facts.NonAuto;

import net.david.Constantes;
import net.david.Activities.Activity;
import net.david.Activities.NonAuto.SelectCoordinatesActivity;
import net.david.Definitions.ActivityDefinition;
import net.david.Facts.Measure;
import android.util.Log;

import com.google.android.maps.GeoPoint;

public final class SelectCoordinatesMeasure extends Measure {
	
	@Override
	public String serializeData(Activity activity, android.app.Activity act) {
		String serialization = "";
		
		SelectCoordinatesActivity nama = (SelectCoordinatesActivity) activity;
		if (nama.getMapas().getMarker() != null){
			serialization += "<latitude>" + nama.getMapas().getMarker().getPoint().getLatitudeE6()/1E6 + "</latitude>\n";
			serialization += "<longitude>" + nama.getMapas().getMarker().getPoint().getLongitudeE6()/1E6 + "</longitude>";
		}
		return serialization;
	}

	@Override
	public String getMeasureToHTML(android.app.Activity act, Activity activity, ActivityDefinition activityDefinition) {
		SelectCoordinatesActivity ma = (SelectCoordinatesActivity) activity;

		String result = "    <h2>" + activityDefinition.getQueryDefinition().getMessage() + "<h2>\n";

		try {
			GeoPoint punto = ma.getMapas().getMarker().getPoint();
			double lat = punto.getLatitudeE6()/1E6; double lon = punto.getLongitudeE6()/1E6;
			String direccion = "http://maps.google.com/maps/api/staticmap?" +
								"center=" + String.valueOf(lat) + "," + String.valueOf(lon) +
								"&zoom=20&maptype=hybrid&size=" + Constantes.TAMANIO_PREVISUALIZA +
								"x" + Constantes.TAMANIO_PREVISUALIZA + "&sensor=true";
			result += "      <img src=\"" + direccion + "\" alt=\"Posición mapa\" width=" + Constantes.TAMANIO_PREVISUALIZA + " height=" + Constantes.TAMANIO_PREVISUALIZA + " />\n";
		} catch (NullPointerException e){
			Log.w("getMeasureToHTML", "Posición GPS no obtenida");
			result += "      <h4>Posición GPS no obtenida</h4>\n";
		}
		result += "      <br>\n";
		
		return result;
	}

	@Override
	public String generateFinalMeasure() {
		return "  <measure type=\"map\">\n" + this.getId_Data() + "\n  </measure>";
	}
}
