package net.david.Facts.NonAuto;

import net.david.Constantes;
import net.david.Activities.Activity;
import net.david.Activities.NonAuto.GetCoordinatesActivity;
import net.david.Definitions.ActivityDefinition;
import net.david.Facts.Measure;
import android.util.Log;

public final class GetCoordinatesMeasure extends Measure {
	
	@Override
	public String serializeData(Activity activity, android.app.Activity act) {
		String serialization = "";
		
		GetCoordinatesActivity naga = (GetCoordinatesActivity) activity;
		if (naga.getLocation() != null){
			serialization += "<latitude>" + naga.getLocation().getLatitude() + "</latitude>\n";
			serialization += "<longitude>" + naga.getLocation().getLongitude() + "</longitude>";
		}
		return serialization;
	}

	@Override
	public String getMeasureToHTML(android.app.Activity act,
			net.david.Activities.Activity activity,
			ActivityDefinition activityDefinition) {
		GetCoordinatesActivity ga = (GetCoordinatesActivity) activity;

		String result = "    <h2>" + activityDefinition.getQueryDefinition().getMessage() + "<h2>\n";

		try {
			double lat = ga.getLocation().getLatitude(); double lon = ga.getLocation().getLongitude();
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
		return "  <measure type=\"gps\">\n" + this.getId_Data() + "\n  </measure>";
	}
}
