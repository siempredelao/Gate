package net.david.Facts.Auto;

import net.david.Activities.Activity;
import net.david.Activities.Auto.RecordCoordinatesActivity;
import net.david.Definitions.ActivityDefinition;
import net.david.Facts.Measure;

public final class RecordCoordinatesMeasure extends Measure {

	@Override
	public String getMeasureToHTML(android.app.Activity act, Activity activity,
			ActivityDefinition activityDefinition) {
		return null;
	}

	@Override
	public String serializeData(Activity activity, android.app.Activity act) {
		String serialization = "";
		RecordCoordinatesActivity aga = (RecordCoordinatesActivity) activity;
		if (aga.getLugar() != null){
			serialization += "<position>";
			serialization += "\n    <latitude>" + aga.getLugar().getLatitude() + "</latitude>";
			serialization += "\n    <longitude>" + aga.getLugar().getLongitude() + "</longitude>";
			serialization += "\n</position>";
		} else {
			serialization = "<position>" + "" + "</position>";
		}
		return serialization;
	}

	@Override
	public String generateFinalMeasure(){
		return "  <measure type=\"gps\">\n" + this.getId_Data() + "\n  </measure>";
	}
}
