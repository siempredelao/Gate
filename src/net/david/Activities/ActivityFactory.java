package net.david.Activities;

import java.util.ArrayList;

import net.david.Constantes;
import net.david.Activities.Auto.RecordCoordinatesActivity;
import net.david.Activities.Auto.RecordImageActivity;
import net.david.Activities.NonAuto.EditFormActivity;
import net.david.Activities.NonAuto.GetCoordinatesActivity;
import net.david.Activities.NonAuto.IncidentTypeActivity;
import net.david.Activities.NonAuto.MakePhotosActivity;
import net.david.Activities.NonAuto.SelectCoordinatesActivity;
import net.david.Activities.NonAuto.SummaryActivity;
import net.david.Definitions.ActivityDefinition;
import android.view.View;

public class ActivityFactory {
	/** M�todo para cargar una Activity atendiendo a su definici�n.
	 *  @author David
	 *  @param definition Definici�n de la Activity
	 *  @param activity Activity de Android necesaria para hacer diversas operaciones
	 *  @param view Vista en la que se insertar� los diferentes elementos de la Activity
	 *  @param listeners Listeners que informar�n cuando una Activity pseudo-no-autom�tica haya acabado
	 *  @param tipo_actividad Booleano que indica qu� tipo de actividad se est� tratando: autom�tica o no-autom�tica
	 *  @return Una Activity creada conforme a la definici�n
	 */
	public Activity load(ActivityDefinition definition,
						android.app.Activity activity,
						View view,
						ArrayList<ActivityListener> listeners,
						boolean tipo_actividad) {
		if (definition != null){
			if (tipo_actividad == Constantes.ACTIVIDAD_NO_AUTOMATICA){
				if (definition.getType().equals("make_photos"))
					return new MakePhotosActivity(definition, activity, null, view);
				else if (definition.getType().equals("select_coordinates"))
					return new SelectCoordinatesActivity(definition, activity, null, view);
				else if (definition.getType().equals("get_coordinates"))
					return new GetCoordinatesActivity(definition, activity, listeners, view); // Parcialmente autom�tica
				else if (definition.getType().equals("edit_form"))
					return new EditFormActivity(definition, activity, null, view);
				else if (definition.getType().equals("incident_type"))
					return new IncidentTypeActivity(definition, activity, listeners, view); // Parcialmente autom�tica
				else if (definition.getType().equals("summary"))
					return new SummaryActivity(definition, activity, null, view);
			}
			else if (tipo_actividad == Constantes.ACTIVIDAD_AUTOMATICA){
				if (definition.getType().equals("record_image"))
					return new RecordImageActivity(definition, activity, view);
				else if (definition.getType().equals("record_coordinates"))
					return new RecordCoordinatesActivity(definition, activity, view);
			}
		}
		return null;
	}
}
