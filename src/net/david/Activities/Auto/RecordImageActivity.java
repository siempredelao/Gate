package net.david.Activities.Auto;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import net.david.Constantes;
import net.david.Activities.Activity;
import net.david.Definitions.ActivityDefinition;
import net.david.Definitions.Auto.RecordImageProperty;
import net.david.Facts.Auto.RecordImageMeasure;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public final class RecordImageActivity extends Activity {
	private Uri foto								= null;

	private boolean camaraActiva					= false;
	
	private Camera camara;
	private SurfaceView ventanaCamara				= null;
	private SurfaceHolder previewHolder				= null;
	private static Uri u							= null;
	private Camera.PictureCallback photoCallback	= null;

	public Uri getFoto() {
		return foto;
	}

	public RecordImageActivity(ActivityDefinition definition, android.app.Activity act, View view){
		super(definition, act, null, view);
		
		this.foto = null;
	}

	@Override
	public void createMeasure() {
		this.setMeasure(new RecordImageMeasure());
	}
	
	@Override
	public void copiarActivity(Activity activity, android.app.Activity act) {
		// No hace nada en una actividad automática
	}

	@Override
	public void guardarEstado(android.app.Activity act) {
		// No hace nada en una actividad automática
	}

	@Override
	protected void anadeContenedorInformacion(ActivityDefinition definition,
			android.app.Activity act, View view, float densidad) {

		LayoutParams rlilp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		rlilp.height = LayoutParams.WRAP_CONTENT;
		rlilp.width = LayoutParams.FILL_PARENT;
		
		RelativeLayout rli = new RelativeLayout(act);
		rli.setLayoutParams(rlilp);
		
		// Este procedimiento será característico de cada sensor
		// Cada elemento lo añadiremos al RelativeLayout "rli" contenedor de la
		// información. Posteriormente, añadiremos dicho RelativeLayout a la vista
		RecordImageQuery cameraQuery = new RecordImageQuery();
		cameraQuery.insertaElementos((RecordImageProperty) definition.getQueryDefinition(), act, densidad, rli);
		
		// A continuación diferenciamos entre una vista deslizable o no
		// Si es deslizable, metemos el relativeLayout dentro de un scrollview
		// y lo agregamos a la vista
		// Si no es deslizable, configuramos sus parámetros y lo agregamos a la
		// vista directamente
		agregaInformacionVista(definition, act, view, densidad, rlilp, rli, Constantes.ACTIVIDAD_AUTOMATICA);
	}

	@Override
	public void run(final android.app.Activity activity) {
		System.gc();
		this.ventanaCamara = (SurfaceView) activity.findViewById(Constantes.CANVAS_CAMARA);
        this.previewHolder = this.ventanaCamara.getHolder();
        
        SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        	public void surfaceCreated(SurfaceHolder holder) {
        		if (holder.isCreating()){
	    	    	if (camara == null){ 
	    	    		camara = Camera.open();
		    	    	// ¡ATENCIÓN! Esta línea de código no lo resuelve
		    	    	// para todos los dispositivos
		    	    	camara.setDisplayOrientation(90);
		    	    	try {
		    	    		camara.setPreviewDisplay(previewHolder);
		    	    	} catch (IOException e) {
		    	    		camara.release();
		    	    		camara = null;
		    	    	}
	    	    	}
    	    	}
        	}
        	
    		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    			// Cuando camaraActiva está a verdadero significará que la camara
    			// sigue activa y no ha sido cerrada
    			if (camaraActiva){
    				camara.stopPreview();
    			}
    			Camera.Parameters parameters = camara.getParameters();
    			// Seleccionamos el flash automático
    			if (activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH))
    				parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
    			parameters.setPictureFormat(PixelFormat.JPEG);
    			try {
    				camara.setParameters(parameters);
    				camara.startPreview();
    			} catch (Exception e) {
    				Log.i("startPreview", "Error al iniciar la cámara");
    			}
    			camaraActiva = true;
    		}
    		
    		public void surfaceDestroyed(SurfaceHolder holder) {
    			if (camaraActiva && (camara != null)){
	    			// Se cierra la cámara y se liberan los recursos asociados
	    			camara.stopPreview();
	    			camaraActiva = false;
	    			camara.release();
	    			camara = null;
    			}
    			camaraActiva = false;
    		}
    	};

    	// Guardar la foto al sacarla en la carpeta NotifiKRtera
    	photoCallback = new Camera.PictureCallback() {
    		public void onPictureTaken(byte[] data, Camera camera) {
    			// do something with the photo JPEG (data[]) here!
    			Bitmap bm_rotado = null;
				WeakReference<Bitmap> wbm = null;
    			if (data != null){
    				wbm = new WeakReference<Bitmap>(BitmapFactory.decodeByteArray(data, 0, data.length));
    				Matrix m = new Matrix();
    				m.postRotate(90);
    				// Asignamos la foto por si hiciera falta
    				bm_rotado = Bitmap.createBitmap(wbm.get(), 0, 0, wbm.get().getWidth(), wbm.get().getHeight(), m, true);
    			}
    			
    			File pictureFile = getOutputMediaFile();
    	        if (pictureFile == null){
    	            return;
    	        }

    	        try {
    	            FileOutputStream fos = new FileOutputStream(pictureFile);
    	            bm_rotado.compress(Bitmap.CompressFormat.JPEG, 100, fos);
    	            fos.close();
    				wbm.clear();

    	            // Actualizamos la galería
    	    	    activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+Environment.getExternalStorageDirectory())));
    	        } catch (FileNotFoundException e) {
    	            Log.d("onPictureTaken()", "File not found: " + e.getMessage());
    	        } catch (IOException e) {
    	            Log.d("onPictureTaken()", "Error accessing file: " + e.getMessage());
    	        }
    			
    			camera.startPreview();
    		}
    	};

		this.previewHolder.addCallback(surfaceCallback);
        this.previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	
	/** Crea un fichero para almacenar la imagen.
	 *  @author David
	 *  @return Fichero creado */
	private File getOutputMediaFile(){
	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "NotifiKRtera");

	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
	    String fichero = mediaStorageDir.getPath() + File.separator +"IMG_"+ timeStamp + ".jpg";
	    File mediaFile = new File(fichero);
		
	    u = Uri.fromFile(mediaFile);
	    System.out.println("Nuevo fichero "+ u);
	    foto = u;
	    return mediaFile;
	}

	
	@Override
	public Object saveData(android.app.Activity act){
		try{
			// Enfocamos y sacamos la foto
			camara.autoFocus(new AutoFocusCallback(){
		    	Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
		    	    public void onShutter() {
		    	    	// Reproduce un sonido propio aquí (si no, se usa el que está por defecto)
		    	    }
		    	};
		    	
		    	@Override
				public void onAutoFocus(boolean arg0, Camera arg1) {
		    		if (camara != null)
		    			camara.takePicture(shutterCallback, null, photoCallback);
				}
			});
		} catch (RuntimeException e){
			Log.i("saveData A_CameraActivity", "RuntimeException autofocus failed");
		} catch (Exception e){
			Log.i("saveData A_CameraActivity", "Exception autofocus failed");
		}
		return u;
	}

	@Override
	public void stop() {
		if (camaraActiva && (camara != null)){
			// Se cierra la cámara y se liberan los recursos asociados
			camara.stopPreview();
			camaraActiva = false;
			camara.release();
			camara = null;
		}
	}

}
