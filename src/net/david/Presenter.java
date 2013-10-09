package net.david;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.david.Definitions.GateDefinition;
import net.david.Display.MyView;
import net.david.Gates.Gate;
import net.david.Gates.GateFinishedListener;
import net.david.Gates.GatePresenter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

public class Presenter {
	private GateDefinition definition;
	private static MyView mainView;
	private android.app.Activity act;
	private ArrayList<GateFinishedListener> glisteners;
	private int file;
	private String gateName;
	
	/** Constructor de la clase Presenter. Se encarga de crear la Gate.
	 *  @author David
	 *  @param activity	La Activity que va a usar el framework para desplegarse
	 *  @param file	Fichero en formato recurso de Android
	 *  @param guid	Identificador �nico de la aplicaci�n
	 *  @param gateName	Nombre que aparecer� en el bot�n asociado a la Gate
	 */
	public Presenter(android.app.Activity activity, int file, String guid, String gateName){
		this.definition = new GateDefinition();
		Presenter.mainView = new MyView(activity);
		this.act = activity;
		this.glisteners = new ArrayList<GateFinishedListener>();
		this.file = file;
		Constantes.guid = guid;
		this.gateName = gateName;
		
		this.addGateFinishedListener(new GateFinishedListener() {
			@Override
			public void gateFinishedNotify() {
				try {
					run();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/** M�todo para a�adir un nuevo GateFinishedListener
	 *  @author David
	 *  @param gateListener El nuevo GateFinishedListener a a�adir
	 */
	protected synchronized void addGateFinishedListener(GateFinishedListener gateListener){
		this.glisteners.add(gateListener);
	}
	
	/** M�todo para eleminar un GateFinishedListener espec�fico
	 *  @author David
	 *  @param gateListener El  GateFinishedListener a eliminar
	 */
	protected synchronized void removeGateFinishedListener(GateFinishedListener gateListener){
		this.glisteners.remove(gateListener);
	}
	
	/** M�todo para la notificaci�n del evento que indica que la Gate ha finalizado.
	 *  @author David
	 */
	protected void glistenersNotify(){
		for (GateFinishedListener glistener: glisteners) glistener.gateFinishedNotify();
	}
	
	/** M�todo para iniciar la ejecuci�n de la Gate.
	 *  @author David
	 *  @throws Exception
	 */
	public void run() throws Exception {
		this.loadDefinition(file);
		
		final float DENSIDAD = act.getResources().getDisplayMetrics().density;
		int lastId = -1;
		
		RelativeLayout.LayoutParams btlp = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		btlp.width = LayoutParams.FILL_PARENT;
		btlp.height = LayoutParams.FILL_PARENT;
		btlp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		btlp.leftMargin = (int) (5 * DENSIDAD);
		btlp.rightMargin = (int) (5 * DENSIDAD);
		btlp.bottomMargin = (int) (5 * DENSIDAD);
		btlp.topMargin = (int) (5 * DENSIDAD);

		if (lastId == -1) btlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
		if (lastId != -1) btlp.addRule(RelativeLayout.BELOW, lastId);
		if (lastId == -1) btlp.topMargin = (int) (5 * DENSIDAD);
		lastId = Constantes.GATE;
		
		
		// Bot�n de inicio de Gate
		final Button button = new Button(this.act);
		button.setId(Constantes.GATE);
		button.setText(this.gateName);
		button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		
		
		button.setLayoutParams(btlp);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v){
				// Asociar su setOnClickListener con la creaci�n de una Gate
				GatePresenter gatePresenter = new GatePresenter(act, definition);
				try {
					Gate gate = gatePresenter.create(glisteners);
					gatePresenter.run(gate);
				} catch (Exception e){
					e.printStackTrace();
				}
			}
			@Override
			protected void finalize() throws Throwable {
				super.finalize();
			}
		});
		Presenter.mainView.getView().addView(button);

		this.show();
	}
	
	/** M�todo para mostrar en pantalla la Gate.
	 *  @author David
	 */
	private void show() {
		this.act.setContentView(Presenter.mainView.getView());
	}

	
	/** M�todo para cargar la definici�n de la Gate. Lee el fichero XML de definici�n
	 *  y crea las clases de definici�n correspondientes para crear la Gate.
	 *  @author David
	 *  @param file Fichero de definici�n de Gate que se encuentra como recurso en /res/raw
	 *  @throws Exception 
	 */
	private void loadDefinition(int file) throws Exception {
		// Obtenemos la referencia al fichero XML de entrada
		InputStream fil = act.getResources().openRawResource(file);
		if (fil != null){
			// Instanciamos la f�brica para DOM
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			
			// Creamos un nuevo parser DOM
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			// Realizamos la lectura completa del XML
			Document dom = (Document) builder.parse(fil);
			
			// Nos posicionamos en el nodo principal del �rbol (<gate>)
			Element root = dom.getDocumentElement();
			
			definition.deserializeFromXML(root, this.act);
		}
	}
}