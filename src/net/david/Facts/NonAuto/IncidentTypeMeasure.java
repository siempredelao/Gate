package net.david.Facts.NonAuto;

import android.util.Log;
import net.david.Activities.Activity;
import net.david.Activities.NonAuto.IncidentTypeActivity;
import net.david.Definitions.ActivityDefinition;
import net.david.Facts.Measure;

public class IncidentTypeMeasure extends Measure {

	@Override
	public String serializeData(Activity activity, android.app.Activity act) {
		String serialization = "";
		
		IncidentTypeActivity ita = (IncidentTypeActivity) activity;
		if (ita.getPosition() != -1)
			serialization += "<type>" + ita.getIncidencias()[ita.getPosition()] + "</type>\n";
		return serialization;
	}

	@Override
	public String getMeasureToHTML(android.app.Activity act, Activity activity,
			ActivityDefinition activityDefinition) {
		IncidentTypeActivity ita = (IncidentTypeActivity) activity;

		String result = "    <h2>" + activityDefinition.getQueryDefinition().getMessage() + "<h2>\n";

		if (ita.getPosition() != -1)
			result += "      <h4>" + ita.getIncidencias()[ita.getPosition()] + "</h4>\n";
		else {
			Log.w("getMeasureToHTML", "Tipo no elegido");
			result += "      <h4>Tipo no elegido</h4>\n";
		}
		
		result += "      <br>\n";
		
		return result;
	}

	@Override
	protected String generateFinalMeasure() {
		return "  <measure type=\"type\">\n" + this.getId_Data() + "\n  </measure>";
	}

}
