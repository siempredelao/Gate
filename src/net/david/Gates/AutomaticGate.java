package net.david.Gates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import net.david.Constantes;
import net.david.R;
import net.david.Activities.ActivityFactory;
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

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

public final class AutomaticGate extends Gate {
	private Handler handler;
	private Timer timer;
	@SuppressWarnings("rawtypes")
	private ArrayList<AsyncTask> listaHijos = new ArrayList<AsyncTask>();
	
	public AutomaticGate(GateDefinition definition, Activity activity, ArrayList<GateFinishedListener> glisteners){
		final float DENSIDAD = activity.getResources().getDisplayMetrics().density;
		
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		lp.width = LayoutParams.MATCH_PARENT;
		lp.height = LayoutParams.MATCH_PARENT;

		super.setActividades(new ArrayList<net.david.Activities.Activity>());
		super.setgView(new MyView(activity));
		super.getgView().getView().setLayoutParams(lp);
		super.setGlisteners(glisteners);
		super.setmActivity(activity);
		
		buildGate(activity, super.getgView().getView(), DENSIDAD);
	}

	protected void buildGate(Activity activity, View view, final float densidad) throws DOMException {
		LayoutParams blp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		blp.width = LayoutParams.FILL_PARENT;
		blp.height = (int) (60 * densidad);
		blp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		blp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		blp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		
		LinearLayout botones = new LinearLayout(activity);
		botones.setId(Constantes.BOTONES);
		botones.setWeightSum(2);
		botones.setLayoutParams(blp);
		botones.setBackgroundResource(R.drawable.degradado);
		((ViewGroup) view).addView(botones);
		
		
		LinearLayout.LayoutParams strlp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1);
		strlp.width = LayoutParams.WRAP_CONTENT;
		strlp.height = LayoutParams.WRAP_CONTENT;
		strlp.gravity = Gravity.CENTER_VERTICAL;
		
		Button start = new Button(activity);
		start.setId(Constantes.START);
		start.setText(activity.getText(net.david.R.string.start));
		start.setLayoutParams(strlp);
		botones.addView(start);
		
		LinearLayout.LayoutParams stplp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1);
		stplp.width = LayoutParams.WRAP_CONTENT;
		stplp.height = LayoutParams.WRAP_CONTENT;
		stplp.gravity = Gravity.CENTER_VERTICAL;
		
		Button stop = new Button(activity);
		stop.setId(Constantes.STOP);
		stop.setText(activity.getText(net.david.R.string.stop));
		stop.setLayoutParams(stplp);
		botones.addView(stop);
	}

	@Override
	protected void execute(final GateDefinition definition, final Activity act, net.david.Activities.Activity copia) {
		int i=0;
		while (i < definition.getActivities().size()){
			ActivityFactory activityFactory = new ActivityFactory();
			// Invocar a la actividad indicada por la posición del gate
			super.setActividad(activityFactory.load(definition.getActivities().get(i),
								act,
								super.getgView().getView(),
								null,
								Constantes.ACTIVIDAD_AUTOMATICA));
			
			super.getActividades().add(super.getActividad());
			i++;
		}
		
		act.setContentView(super.getgView().getView());
		
		// Arrancamos cada actividad
		for (net.david.Activities.Activity a: super.getActividades()) a.run(act);
		
		final Button comenzar = (Button) act.findViewById(Constantes.START);
		final Button finalizar = (Button) act.findViewById(Constantes.STOP);

		if (comenzar.isEnabled())
			finalizar.setEnabled(false);
		
		this.handler = new Handler();
		
		if (comenzar != null){
			comenzar.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					comenzar.setEnabled(false);
					finalizar.setEnabled(true);
					try {
						saveAndSendFactsAtInterval(definition, getActividades(), act);
					} catch (OutOfMemoryError e){
						Log.d("OutOfMemoryError", "Petó!!");
						e.printStackTrace();
						timer.cancel();
						timer.purge();
						gListenersNotify();
					}
				}
			});
		}
		if (finalizar != null){
			finalizar.setOnClickListener(new View.OnClickListener() {
				private ProgressDialog progressDialog = null; 
				@Override
				public void onClick(View v) {
					progressDialog = ProgressDialog.show(getmActivity(), "", act.getText(R.string.finish_sending_data).toString());
					finalizar.setEnabled(false);
					comenzar.setEnabled(true);
					if (timer != null){
						timer.cancel();
						
						// Detenemos el proceso de las activades
						for (net.david.Activities.Activity act : getActividades())
							act.stop();
						for (@SuppressWarnings("rawtypes") AsyncTask actual : listaHijos){
							actual.cancel(true);
						}
					}

					// Salimos al menú principal
					gListenersNotify();
					
					// Dismiss the progress dialog
					progressDialog.dismiss();
				}
			});
		}
	}

	/** Procedimiento auxiliar para guardar los datos asociados a la Gate cada intervalo de tiempo especificado
	 *  @author David
	 * @param definition
	 * @param actividades
	 * @param act
	 * @throws OutOfMemoryError
	 */
	private void saveAndSendFactsAtInterval(final GateDefinition definition,
									final ArrayList<net.david.Activities.Activity> actividades,
									final android.app.Activity act) throws OutOfMemoryError {
		
		float intervalo = 60/definition.getRate();
		
		TimerTask tarea = new TimerTask() {
			@Override
			public void run() { // Ejecución cada intervalo
				handler.post(new Runnable() {
					@Override
					public void run() {
						System.gc();
						for (int k=0; k<actividades.size(); k++)
							listaHijos.add(new SendMeasure().execute(definition, act, k));
						listaHijos.add(new SendAutoFacts().execute(definition));
					}
				});
			}
		};
		timer = new Timer();
		timer.schedule(tarea, 1000, (long) intervalo*1000);
	}
	
	
	/** Clase auxiliar que envía un único Measure mediante la creación de un hilo
	 *  @author David
	 */
	private class SendMeasure extends AsyncTask<Object, Void, Void>{
		private HttpResponse response;
		private int kkk;
		private HttpClient httpClient;
		
		@Override
		protected void onPreExecute(){
			httpClient = new DefaultHttpClient();
			// Evitamos que se piense que vamos a continuar con más envíos
			httpClient.getParams().setBooleanParameter("http.protocol.expect-continue", false);
		}
		@Override
		protected void onProgressUpdate(Void... values){
		}
		@Override
		protected Void doInBackground(Object... params) {
			kkk = ((Integer) params[2]);
			
			// Guardar los datos creados
			getActividades().get(kkk).saveData((android.app.Activity) params[1]);
			
			if (getActividades().get(kkk).getMeasure() == null)
				getActividades().get(kkk).createMeasure();
			// Serializar los datos creados
			String cosa_serializada = getActividades().get(kkk).getMeasure().serializeData(getActividades().get(kkk), (android.app.Activity) params[1]);
			
			Thread.currentThread().setName("A_SendMeasure-" + kkk);
			
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
			    response = httpClient.execute(targetHost, httpPost);
			    
			    entity.consumeContent();
			} catch (SocketException ex){
				Log.e("SendMeasure", "No existe conexión con el servidor");
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
			} else {
				// Entra aquí cuando ocurre una excepción
				// La IP del servidor es incorrecta
				// El servidor no está ejecutándose
				Toast.makeText(getmActivity(), getmActivity().getText(R.string.cannot_connect_server).toString(), Toast.LENGTH_LONG).show();
			}
		}
	}
	
	/** Clase auxiliar que envía un Fact mediante la creación de un hilo
	 *  @author David
	 */
	private class SendAutoFacts extends AsyncTask<Object, Void, Void>{
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
		}
		@Override
		protected Void doInBackground(Object... params) {
			Thread.currentThread().setName("A_SendAutoFacts");
			try {
				// Obtener el XML a partir de ese arraylist
				Fact fact = new Fact();
				String xml = fact.serialize(getActividades(), getmActivity());
				
				// Enviarlo mediante REST
				HttpHost targetHost = new HttpHost(((GateDefinition) params[0]).getServer(), ((GateDefinition) params[0]).getPort(), "http");
	
				HttpPost httpPost = new HttpPost("http://" + ((GateDefinition) params[0]).getServer() + ":" + ((GateDefinition) params[0]).getPort() + "/" + ((GateDefinition) params[0]).getWeb_service());
				// Also be sure to tell the server what kind of content we are sending
				httpPost.setHeader("content-type", "text/xml");
			
			    StringEntity entity = new StringEntity(xml, "UTF-8");
			    entity.setContentType("text/xml");
			    httpPost.setEntity(entity);

			    // execute is a blocking call, it's best to call this code in a
			    // thread separate from the ui's
			    response = httpClient.execute(targetHost, httpPost);
			    entity.consumeContent();
			} catch (SocketException ex){
				Log.e("SendFacts", "No existe conexión con el servidor");
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
			        System.out.println(respuesta);
			        if (respuesta.equals("OK")){
//						Toast.makeText(getmActivity(), "Datos enviados!", Toast.LENGTH_LONG).show();

						// do something useful with the response
						System.out.println("Se enviaron los datos");
					} else {
						Toast.makeText(getmActivity(), getmActivity().getText(R.string.server_no_response).toString(), Toast.LENGTH_LONG).show();
					}
			        reader.close();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (SocketException ex){
				    ex.printStackTrace();
				}  catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				// Entra aquí cuando ocurre una excepción
				// La IP del servidor es incorrecta
				// El servidor no está ejecutándose
				
				// GUARDAR EL HECHO EN LA LISTA DE HECHOS!
				Toast.makeText(getmActivity(), getmActivity().getText(R.string.cannot_connect_server).toString(), Toast.LENGTH_LONG).show();
			}

		    // When HttpClient instance is no longer needed,
		    // shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpClient.getConnectionManager().shutdown();
		}
	}
}
