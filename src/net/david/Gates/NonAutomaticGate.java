package net.david.Gates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.ExecutionException;

import net.david.Constantes;
import net.david.R;
import net.david.Activities.ActivityFactory;
import net.david.Activities.ActivityListener;
import net.david.Definitions.ActivityDefinition;
import net.david.Definitions.GateDefinition;
import net.david.Display.MyView;
import net.david.Facts.Fact;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.DOMException;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public final class NonAutomaticGate extends Gate {
	private int activityPosition;
	private ProgressDialog progressDialog = null;
	private static Stack<net.david.Activities.Activity> pila;
	private ArrayList<ActivityListener> listeners;
	
	protected static Stack<net.david.Activities.Activity> getPila() {
		return pila;
	}

	protected static void setPila(Stack<net.david.Activities.Activity> pila) {
		NonAutomaticGate.pila = pila;
	}

	protected void setListeners(ArrayList<ActivityListener> listeners) {
		this.listeners = listeners;
	}
	
	protected ArrayList<ActivityListener> getListeners() {
		return listeners;
	}
	
	protected synchronized void addActivityListener(ActivityListener actListener){
		this.listeners.add(actListener);
	}
	
	protected synchronized void removeActivityListener(ActivityListener actListener){
		this.listeners.remove(actListener);
	}
	
	
	public NonAutomaticGate(GateDefinition definition, android.app.Activity activity, ArrayList<GateFinishedListener> glisteners) {
		final float DENSIDAD = activity.getResources().getDisplayMetrics().density;
		
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		lp.width = LayoutParams.MATCH_PARENT;
		lp.height = LayoutParams.MATCH_PARENT;
		
		this.activityPosition = 0; // 0 porque el ArrayList comienza en cero
		NonAutomaticGate.setPila(new Stack<net.david.Activities.Activity>());
		super.setActividades(null);
		super.setgView(new MyView(activity));
		super.getgView().getView().setLayoutParams(lp);
		super.setGlisteners(glisteners);
		super.setmActivity(activity);
		this.setListeners(new ArrayList<ActivityListener>());
		
		buildGate(activity, super.getgView().getView(), DENSIDAD);
		
	}

	@Override
	protected void buildGate(android.app.Activity activity, View view, float densidad){
		// Comenzamos creando los diferentes elementos de la pantalla de abajo
		// hacia arriba: primero, la barra inferior
		RelativeLayout belowBar = anadeBarraInferior(activity, view, densidad);
		
		// segundo, la barra de progreso
		ProgressBar pb = anadeBarraProgreso(activity, view, densidad, belowBar);
		
		// tercero, el botón de opciones
		anadeBotonOpciones(activity, view, densidad, pb);
	}

	/** Procedimiento auxiliar que añade la barra inferior a la vista de la Gate
	 *  @author David
	 * @param xmlview
	 * @param act
	 * @param densidad
	 * @return El RelativeLayout con la barra inferior
	 * @throws DOMException
	 */
	private RelativeLayout anadeBarraInferior(android.app.Activity act, View view,
			final float densidad){
		
        RelativeLayout belowBar = new RelativeLayout(act);
		ImageView leftButton = null;
		ImageView rightButton = null;
		TextView barText = null;
		TextView progressText = null;
		createBelowBar(	act, densidad, view, belowBar,
				leftButton,	rightButton, barText, progressText);
		return belowBar;
	}
	
	/** Procedimiento auxiliar que crea la barra inferior del asistente de la incidencia.
	 * @author David
	 * @param act Actividad
	 * @param densidad Densidad de la pantalla
	 * @param tituloVentana Título de la ventana actual del asistente
	 * @param principal RelativeLayout base
	 * @param barraBaja	RelativeLayout específico con la barra inferior
	 * @param botonIzquierda ImageView representativo del botón "atrás"
	 * @param botonDerecha ImageView representativo del botón "siguiente"
	 * @param textoBarra Texto del asistente
	 * @param textoProgreso Texto del paso actual del asistente
	 */
	private void createBelowBar(android.app.Activity act,
								float densidad,
								View principal,
								RelativeLayout barraBaja,
								ImageView botonIzquierda,
								ImageView botonDerecha,
								TextView textoBarra,
								TextView textoProgreso) {
		LayoutParams bblp = new LayoutParams(LayoutParams.FILL_PARENT, (int) (60 * densidad));
		bblp.height = (int) (60 * densidad);
		bblp.width = LayoutParams.FILL_PARENT;
		bblp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		
		barraBaja.setLayoutParams(bblp);
		barraBaja.setId(Constantes.BARRA_BAJA); // Hay que darle un ID para poder enganchar desde él
		barraBaja.setBackgroundResource(R.drawable.degradado);
		
		
		LayoutParams bilp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		bilp.width = LayoutParams.WRAP_CONTENT;
		bilp.height = LayoutParams.WRAP_CONTENT;
		bilp.leftMargin = (int) (5 * densidad);
		bilp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		bilp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		
		botonIzquierda = new ImageView(act);
		botonIzquierda.setId(Constantes.BOTON_ATRAS);
		botonIzquierda.setLayoutParams(bilp);
		botonIzquierda.setImageResource(R.drawable.ic_menu_close_clear_cancel);
		barraBaja.addView(botonIzquierda);
		
		
		LayoutParams bdlp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		bdlp.width = LayoutParams.WRAP_CONTENT;
		bdlp.height = LayoutParams.WRAP_CONTENT;
		bdlp.rightMargin = (int) (5 * densidad);
		bdlp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		bdlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		
		botonDerecha = new ImageView(act);
		botonDerecha.setId(Constantes.BOTON_SIGUIENTE);
		botonDerecha.setLayoutParams(bdlp);
		botonDerecha.setImageResource(R.drawable.arrow_right);
		barraBaja.addView(botonDerecha);
		
		
		LayoutParams tblp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		tblp.width = LayoutParams.WRAP_CONTENT;
		tblp.height = LayoutParams.WRAP_CONTENT;
		tblp.bottomMargin = (int) (5 * densidad);
		tblp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		tblp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		
		textoBarra = new TextView(act);
		textoBarra.setLayoutParams(tblp);
		textoBarra.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
		textoBarra.setTypeface(Typeface.DEFAULT_BOLD);
		textoBarra.setId(Constantes.GATE_MESSAGE);
		barraBaja.addView(textoBarra);
		
		
		LayoutParams tplp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		tplp.width = LayoutParams.WRAP_CONTENT;
		tplp.height = LayoutParams.WRAP_CONTENT;
		tplp.topMargin = (int) (5 * densidad);
		tplp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		tplp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
		
		textoProgreso = new TextView(act);
		textoProgreso.setLayoutParams(tplp);
		textoProgreso.setId(Constantes.ACTIVITY_MESSAGE);
		barraBaja.addView(textoProgreso);
		
		((ViewGroup) principal).addView(barraBaja);
    }
	
	/** Procedimiento auxiliar que añade la barra de progreso a la vista de la Gate
	 *  @author David
	 * @param act
	 * @param densidad
	 * @param belowBar
	 * @return La barra de progreso creada
	 */
	private ProgressBar anadeBarraProgreso(android.app.Activity act, View view,
								final float densidad, RelativeLayout belowBar) {
		LayoutParams pblp = new LayoutParams(LayoutParams.FILL_PARENT, (int) (20 * densidad));
		pblp.height = (int) (20 * densidad);
		pblp.width = LayoutParams.FILL_PARENT;
		pblp.bottomMargin = (int) (5 * densidad);
		pblp.addRule(RelativeLayout.ABOVE, belowBar.getId());
		
		ProgressBar pb = new ProgressBar(act, null, android.R.attr.progressBarStyleHorizontal);
		pb.setId(Constantes.PROGRESSBAR);
		pb.setLayoutParams(pblp);
		((ViewGroup) view).addView(pb);
		return pb;
	}

	/** Procedimiento auxiliar que añade el botón de opciones a la vista de la Gate
	 *  @author David
	 * @param act
	 * @param densidad
	 * @param pb
	 */
	private void anadeBotonOpciones(android.app.Activity act, View view,
											final float densidad, ProgressBar pb){
		LayoutParams oflp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		oflp.height = LayoutParams.WRAP_CONTENT;
		oflp.width = LayoutParams.WRAP_CONTENT;
		oflp.bottomMargin = (int) (5 * densidad);
		oflp.addRule(RelativeLayout.ABOVE, pb.getId());
		oflp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		
		ImageView opcionesForm = new ImageView(act);
		opcionesForm.setId(Constantes.BOTON_OPCIONES);
		opcionesForm.setLayoutParams(oflp);
		opcionesForm.setBackgroundResource(R.drawable.menu_arriba);
		((ViewGroup) view).addView(opcionesForm);
	}


	@Override
	protected void execute(final GateDefinition definition, final android.app.Activity act, net.david.Activities.Activity copia) {

		ActivityFactory activityFactory = new ActivityFactory();
		// Invocar a la actividad indicada por la posición del gate
		super.setActividad(activityFactory.load(definition.getActivities().get(this.activityPosition),
							act,
							super.getgView().getView(),
							this.getListeners(),
							Constantes.ACTIVIDAD_NO_AUTOMATICA));
		
		act.setContentView(super.getgView().getView());

		TextView tv = (TextView) act.findViewById(Constantes.ACTIVITY_MESSAGE);
		if (tv != null) tv.setText(definition.getActivities().get(this.activityPosition).getQueryDefinition().getMessage());
		
		TextView tvg = (TextView) act.findViewById(Constantes.GATE_MESSAGE);
		if (tvg != null) tvg.setText(definition.getLabel());
		
		ProgressBar pb = (ProgressBar) act.findViewById(Constantes.PROGRESSBAR);
		pb.setProgress((int) ((float)this.activityPosition/(float)definition.getActivities().size()*100));
		
		// Copiamos los datos de la activity
		// ¿y la destruimos?
		if (copia != null)
			super.getActividad().copiarActivity(copia, act);

		super.getActividad().run(act);
		
		this.addActivityListener(new ActivityListener(){
			@Override
			public void activityNotify() {
				removeActivityListener(getListeners().get(getListeners().size()-1));
				getActividad().stop();
				activityContinue(definition, act, getgView().getView(), getActividad());
			}
		});
		
		ImageView botonAtras = (ImageView) act.findViewById(Constantes.BOTON_ATRAS);
		if (botonAtras != null){
			botonAtras.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					removeActivityListener(getListeners().get(getListeners().size()-1));
					getActividad().stop();
					activityBack(definition, act, getgView().getView(), null);
				}
			});
		}
		
		ImageView botonSiguiente = (ImageView) act.findViewById(Constantes.BOTON_SIGUIENTE);
		if (botonSiguiente != null){
			botonSiguiente.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					removeActivityListener(getListeners().get(getListeners().size()-1));
					getActividad().stop();
					activityContinue(definition, act, getgView().getView(), getActividad());
				}
			});
		}
	}
	
	private void executeSummary(final GateDefinition definition, final android.app.Activity act, net.david.Activities.Activity copia){
		ActivityDefinition def = new ActivityDefinition();
		def.setType("summary");
		
		ActivityFactory activityFactory = new ActivityFactory();
		// Invocar a la actividad indicada por la posición del gate
		super.setActividad(activityFactory.load(def,
							act,
							super.getgView().getView(),
							this.getListeners(),
							Constantes.ACTIVIDAD_NO_AUTOMATICA));
		
		act.setContentView(super.getgView().getView());

		TextView tv = (TextView) act.findViewById(Constantes.ACTIVITY_MESSAGE);
		if (tv != null) tv.setText(net.david.R.string.summary);
		ProgressBar pb = (ProgressBar) act.findViewById(Constantes.PROGRESSBAR);
		pb.setProgress(100);
		
		// Copiamos los datos de la activity
		// ¿y la destruimos?
		if (copia != null)
			super.getActividad().copiarActivity(copia, act);

		super.getActividad().run(act);
		
		new LoadResume().execute(definition);
		
		ImageView botonAtras = (ImageView) act.findViewById(Constantes.BOTON_ATRAS);
		if (botonAtras != null){
			botonAtras.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					getActividad().stop();
					activityBack(definition, act, getgView().getView(), null);
				}
			});
		}
		
		ImageView botonSiguiente = (ImageView) act.findViewById(Constantes.BOTON_SIGUIENTE);
		if (botonSiguiente != null){
			botonSiguiente.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					activityContinue(definition, act, getgView().getView(), getActividad());
				}
			});
		}
	}
	
	/** Clase auxiliar para cargar el resumen en un visor HTML mediante un hilo
	 *  @author David
	 */
	private class LoadResume extends AsyncTask<GateDefinition, Void, Void>{
		private WebView wv;
		private ProgressDialog progressDialogLR;

		
		@Override
		protected void onPreExecute(){
			wv = (WebView) getmActivity().findViewById(Constantes.VISOR_HTML);
			if (progressDialogLR == null)
				progressDialogLR = ProgressDialog.show(getmActivity(), "", getmActivity().getText(R.string.generating_summary).toString());
		}
		@Override
		protected void onProgressUpdate(Void... values){
			// No hace nada
		}
		@Override
		protected Void doInBackground(GateDefinition... params) {
			wv.loadDataWithBaseURL("", generateSummary((GateDefinition) params[0], getmActivity()), "text/html", "UTF-8", "");
			return null;
		}
		@Override
		protected void onPostExecute(Void result){
			progressDialogLR.dismiss();
		}
	}
	
	/** Procedimiento auxiliar para generar el resumen en HTML.
	 * @author David
	 * @param definition
	 * @param act
	 * @return Una ristra de caracteres conteniendo el resumen de la Gate en formato HTML
	 */
	@SuppressWarnings("unchecked")
	private String generateSummary(GateDefinition definition, android.app.Activity act) {
		super.setActividades(new ArrayList<net.david.Activities.Activity>());
		copyStackInArrayList((Stack<net.david.Activities.Activity>) getPila().clone(), super.getActividades());

		String html = "<html>\n  <head>\n    <meta charset=utf-8>\n  </head>\n  <body>\n    <font color=\"white\">\n";
		for (int i=0; i<super.getActividades().size(); i++){
			super.getActividades().get(i).createMeasure();
			html += super.getActividades().get(i).getMeasure().getMeasureToHTML(act, super.getActividades().get(i), definition.getActivities().get(i));
		}
		html += "    </font>\n  </body>\n</html>";
		return html;
	}
	
	/** Procedimiento auxiliar para volver atrás en el asistente.
	 * @author David
	 * @param definition
	 * @param act
	 * @param gateView
	 * @param actual
	 */
	private void activityBack(GateDefinition definition, android.app.Activity act,
								View gateView, net.david.Activities.Activity actual) {
		System.gc();
		System.out.println("Hacia atrás");
		this.activityPosition--;
		
		if (this.activityPosition < 0){
			super.gListenersNotify();
			actual = null;
		} else {
			ImageView botonSalir = (ImageView) act.findViewById(Constantes.BOTON_ATRAS);
			if (this.activityPosition == 0)
				botonSalir.setImageResource(net.david.R.drawable.ic_menu_close_clear_cancel);
			else
				botonSalir.setImageResource(net.david.R.drawable.arrow_left);
			
			ImageView botonEnviar = (ImageView) act.findViewById(Constantes.BOTON_SIGUIENTE);
			botonEnviar.setImageResource(net.david.R.drawable.arrow_right);


			actual = null;
			// Sacar la última actividad de la pila
			this.execute(definition, act, NonAutomaticGate.getPila().pop());
		}
	}

	/** Procedimiento auxiliar para avanzar en el asistente.
	 * @author David
	 * @param definition
	 * @param act
	 * @param gateView
	 * @param actual
	 */
	private void activityContinue(GateDefinition definition, final android.app.Activity act,
									View gateView, net.david.Activities.Activity actual) {
		System.gc();
		System.out.println("Hacia delante");
		this.activityPosition++;
		
		if (this.activityPosition == definition.getActivities().size()){
			// Construir pantalla resumen hechos
			// Cambiamos el icono
			ImageView botonEnviar = (ImageView) act.findViewById(Constantes.BOTON_SIGUIENTE);
			botonEnviar.setImageResource(net.david.R.drawable.ic_menu_send);
			
			actual.guardarEstado(act);
			NonAutomaticGate.getPila().push(actual);
			actual = null;
			
			// Sacar la última actividad de la pila
			this.executeSummary(definition, act, null);
		} else if (this.activityPosition > definition.getActivities().size()){
			// AQUÍ SE ENVÍAN LOS DATOS
			new SendMeasure().execute(definition, act, 0);
		} else {
			ImageView botonSalir = (ImageView) act.findViewById(Constantes.BOTON_ATRAS);
			botonSalir.setImageResource(net.david.R.drawable.arrow_left);
			
			actual.guardarEstado(act);
			// Meter la actividad actual en la pila
			NonAutomaticGate.getPila().push(actual);
			actual = null;
			
			this.execute(definition, act, null);
		}
	}

	
	/** Clase auxiliar que envía un único Measure mediante la creación de un hilo
	 *  @author David
	 */
	private class SendMeasure extends AsyncTask<Object, Void, Void>{
		private HttpResponse response;
		private int kkk;
		private HttpClient httpClient;
		private Object[] parametros;
		
		@Override
		protected void onPreExecute(){
			httpClient = new DefaultHttpClient();
			// Evitamos que se piense que vamos a continuar con más envíos
			httpClient.getParams().setBooleanParameter("http.protocol.expect-continue", false);
			if (progressDialog == null)
				progressDialog = ProgressDialog.show(getmActivity(), "", getmActivity().getText(R.string.sending_data));
		}
		@Override
		protected void onProgressUpdate(Void... values){
		}
		@Override
		protected Void doInBackground(Object... params) {
			kkk = ((Integer) params[2]);
			
			parametros = params;
			
			Thread.currentThread().setName("NA_SendOnlyOneFact-" + kkk);
			// Guardar los datos creados
			getActividades().get(kkk).saveData((android.app.Activity) params[1]);
			// Serializar los datos creados
			String cosa_serializada = getActividades().get(kkk).getMeasure().serializeData(getActividades().get(kkk), (android.app.Activity) params[1]);
			
			// Obtener el XML a partir de ese arraylist
			// Enviarlo mediante REST
			HttpHost targetHost = new HttpHost(((GateDefinition) params[0]).getServer(), ((GateDefinition) params[0]).getPort(), "http");

			HttpPost httpPost = new HttpPost("http://" + ((GateDefinition) params[0]).getServer() + ":" + ((GateDefinition) params[0]).getPort() + "/" + ((GateDefinition) params[0]).getWeb_service());
			// Also be sure to tell the server what kind of content we are sending
			httpPost.setHeader("content-type", "text/plain");
			
			try {
			    StringEntity entity = new StringEntity(cosa_serializada, "UTF-8");
			    entity.setContentType("text/plain");
			    httpPost.setEntity(entity);
				
			    // execute is a blocking call, it's best to call this code in a
			    // thread separate from the ui's
			} catch (SocketException ex){
				Log.e("SendOnlyOneFact", "No existe conexión con el servidor");
			    ex.printStackTrace();
			} catch (Exception ex){
			    ex.printStackTrace();
			} 
			return null;
		}

		@Override
		protected void onPostExecute(Void result){
			if (response != null){
				try {
					this.get();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					e1.printStackTrace();
				}

				
				BufferedReader reader = null;
				String respuesta = null;
				try {
					reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 8192);
					respuesta = reader.readLine();
					getActividades().get(kkk).getMeasure().setId_Data(respuesta);
					
			        reader.close();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (SocketException ex){
				    ex.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				

			    // When HttpClient instance is no longer needed,
			    // shut down the connection manager to ensure
				// immediate deallocation of all system resources
				httpClient.getConnectionManager().shutdown();
				
				kkk++;
				if (kkk < getActividades().size())
					new SendMeasure().execute(parametros[0], parametros[1], kkk);
				else if (kkk == getActividades().size())
					new SendFact().execute(parametros[0]);
			} else {
				// Entra aquí cuando ocurre una excepción
				// La IP del servidor es incorrecta
				// El servidor no está ejecutándose
				progressDialog.dismiss();
				progressDialog = null;
				Toast.makeText(getmActivity(), getmActivity().getText(R.string.cannot_connect_server).toString(), Toast.LENGTH_LONG).show();
			}
		}
	}
	
	/** Clase auxiliar que envía un Fact mediante la creación de un hilo
	 *  @author David
	 */
	private class SendFact extends AsyncTask<Object, Void, Void>{
		private HttpResponse response;
		private HttpClient httpClient;

		@Override
		protected void onPreExecute(){
			httpClient = new DefaultHttpClient();
			// Evitamos que se piense que vamos a continuar con más envíos
			httpClient.getParams().setBooleanParameter("http.protocol.expect-continue", false);
		}
		
		@Override
		protected void onProgressUpdate(Void... values){
			// No hace nada
		}
		
		// SOLAMENTE EL MÉTODO doInBackground(...) SE EJECUTA EN EL HILO SECUNDARIO
		// ¡EL RESTO LO HACE EN EL HILO PRINCIPAL!
		@Override
		protected Void doInBackground(Object... params) {
			Thread.currentThread().setName("SendFact");

			Fact fact = new Fact();
			String xml = fact.serialize(getActividades(), getmActivity());
			
			// Enviarlo mediante REST
			HttpHost targetHost = new HttpHost(((GateDefinition) params[0]).getServer(), ((GateDefinition) params[0]).getPort(), "http");
			
			HttpPost httpPost = new HttpPost("http://" + ((GateDefinition) params[0]).getServer() + ":" + ((GateDefinition) params[0]).getPort() + "/" + ((GateDefinition) params[0]).getWeb_service());
			// Also be sure to tell the server what kind of content we are sending
			httpPost.setHeader("content-type", "text/xml");
			try {
			    StringEntity entity = new StringEntity(xml, "UTF-8");
			    entity.setContentType("text/xml");
			    httpPost.setEntity(entity);

			    // execute is a blocking call, it's best to call this code in a
			    // thread separate from the ui's
			    response = httpClient.execute(targetHost, httpPost);
			    entity.consumeContent();
			} catch (SocketException ex){
				System.err.println("No existe conexión con el servidor");
			    ex.printStackTrace();
			} catch (Exception ex){
			    ex.printStackTrace();
			}
			// Borramos el xml
			xml = null;
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result){
			if (response != null){
				try {
					this.get();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					e1.printStackTrace();
				}
				
				BufferedReader reader = null;
				String respuesta = null;
				try {
					reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 8192);
					respuesta = reader.readLine();
			        System.out.println(respuesta);
			        reader.close();
			        if (respuesta.equals("OK")){
						// Dismiss the progress dialog
						if (progressDialog != null){
							progressDialog.dismiss();
							progressDialog = null;
						}
						
						Toast.makeText(getmActivity(), getmActivity().getText(R.string.data_ok).toString(), Toast.LENGTH_LONG).show();

						// do something useful with the response
						System.out.println("Se enviaron los datos");
						
						// Salimos al menú principal
						gListenersNotify();
					} else {
						Toast.makeText(getmActivity(), getmActivity().getText(R.string.server_no_response).toString(), Toast.LENGTH_LONG).show();
					}
				} catch (IllegalStateException e) {
					Toast.makeText(getmActivity(), getmActivity().getText(R.string.unknown_error).toString(), Toast.LENGTH_LONG).show();
					if (progressDialog != null){
						progressDialog.dismiss();
						progressDialog = null;
					}
					e.printStackTrace();
				} catch (IOException e) {
					if (progressDialog != null){
						progressDialog.dismiss();
						progressDialog = null;
					}
					Toast.makeText(getmActivity(), getmActivity().getText(R.string.unknown_error).toString(), Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			} else {
				// Entra aquí cuando ocurre una excepción
				// La IP del servidor es incorrecta
				// El servidor no está ejecutándose
				
				// Dismiss the progress dialog
				if (progressDialog != null){
					progressDialog.dismiss();
					progressDialog = null;
				}
				
				// GUARDAR EL HECHO EN LA LISTA DE HECHOS!
				
				Toast.makeText(getmActivity(), getmActivity().getText(R.string.cannot_connect_server).toString(), Toast.LENGTH_LONG).show();
				
				// Salimos al menú principal
				gListenersNotify();
			}

		    // When HttpClient instance is no longer needed,
		    // shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpClient.getConnectionManager().shutdown();
		}
	} 

	
	private static void copyStackInArrayList(Stack<net.david.Activities.Activity> copia, ArrayList<net.david.Activities.Activity> facts){
		ArrayList<net.david.Activities.Activity> inversa = new ArrayList<net.david.Activities.Activity>();
		while (!copia.isEmpty())
			inversa.add(copia.pop());
		
		int k = inversa.size()-1;
		while (k >= 0){ // Ordenamos la lista
			facts.add(inversa.get(k));
			k--;
		}
	}
}
