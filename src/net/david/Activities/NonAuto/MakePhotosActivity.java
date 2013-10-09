package net.david.Activities.NonAuto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import net.david.Constantes;
import net.david.R;
import net.david.Activities.Activity;
import net.david.Activities.ActivityListener;
import net.david.Definitions.ActivityDefinition;
import net.david.Definitions.NonAuto.MakePhotosProperty;
import net.david.Facts.NonAuto.MakePhotosMeasure;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public final class MakePhotosActivity extends Activity {
	
	private ArrayList<Uri> lista_fotos;
	private boolean fotoTomada;
	private int menuContextualFoto;
	private boolean camaraActiva;
	private boolean sinFoto;
	private int maxPhotos;
	
	private static Camera camara;
	private static SurfaceView ventanaCamara				= null;
	private static SurfaceHolder previewHolder				= null;
	private static ImageView iv								= null;
	private static Uri u									= null;
	private ImageView opciones								= null;
	private static Bitmap bitmap							= null;
    private static SparseArray<SoftReference<Bitmap>> cache = null;
	

	public ArrayList<Uri> getLista_fotos() {
		return lista_fotos;
	}
	
	public void setLista_fotos(ArrayList<Uri> lista_fotos) {
		this.lista_fotos = lista_fotos;
	}
	
	@Override
	public void createMeasure(){
		this.setMeasure(new MakePhotosMeasure());
	}
	
	public MakePhotosActivity(ActivityDefinition definition, android.app.Activity act,
							ArrayList<ActivityListener> aListeners, View view) {
		super(definition, act, aListeners, view);
		
		// No hace falta pero BAH!
		super.setListeners(aListeners);
		
		
		this.lista_fotos = new ArrayList<Uri>();
		this.fotoTomada = false;
		this.menuContextualFoto = Constantes.DIALOGO_FOTO_INICIAL;
		this.camaraActiva = false;
		this.sinFoto = false;
		this.maxPhotos = ((MakePhotosProperty) definition.getQueryDefinition()).getMaxPhotos();
	}
	
	@Override
	public void anadeContenedorInformacion(ActivityDefinition definition, android.app.Activity act,
			View view, final float densidad){
		LayoutParams rlilp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		rlilp.height = LayoutParams.WRAP_CONTENT;
		rlilp.width = LayoutParams.FILL_PARENT;
		
		RelativeLayout rli = new RelativeLayout(act);
		rli.setLayoutParams(rlilp);
		
		// Este procedimiento será característico de cada sensor
		// Cada elemento lo añadiremos al RelativeLayout "rli" contenedor de la
		// información. Posteriormente, añadiremos dicho RelativeLayout a la vista
		MakePhotosQuery cameraQuery = new MakePhotosQuery();
		cameraQuery.insertaElementos((MakePhotosProperty) definition.getQueryDefinition(), act, densidad, rli);
		
		// A continuación diferenciamos entre una vista deslizable o no
		// Si es deslizable, metemos el relativeLayout dentro de un scrollview
		// y lo agregamos a la vista
		// Si no es deslizable, configuramos sus parámetros y lo agregamos a la
		// vista directamente
		agregaInformacionVista(definition, act, view, densidad, rlilp, rli, Constantes.ACTIVIDAD_NO_AUTOMATICA);
	}

	
	@SuppressWarnings({ "unchecked"})
	@Override
	public void copiarActivity(Activity activity, android.app.Activity act){
		this.lista_fotos = ((ArrayList<Uri>) ((MakePhotosActivity) activity).lista_fotos.clone());
		this.fotoTomada = ((MakePhotosActivity) activity).fotoTomada;
		this.menuContextualFoto = ((MakePhotosActivity) activity).menuContextualFoto;
		this.camaraActiva = ((MakePhotosActivity) activity).camaraActiva;
		this.sinFoto = ((MakePhotosActivity) activity).sinFoto;
		this.maxPhotos = ((MakePhotosActivity) activity).maxPhotos;
	}
	
	@Override
	public void guardarEstado(android.app.Activity act) {
		// Las operaciones de modificación de los atributos se hacen directamente
		// sobre los mismos, así que este procedimiento no hace nada en este caso
	}
	
	@Override
	public void run(final android.app.Activity activity) {
		System.gc();
		MakePhotosActivity.ventanaCamara = (SurfaceView) activity.findViewById(Constantes.CANVAS_CAMARA);
		MakePhotosActivity.iv = (ImageView) activity.findViewById(Constantes.PREVISUALIZA_CAMARA);
        MakePhotosActivity.previewHolder = MakePhotosActivity.ventanaCamara.getHolder();
        
        SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        	public void surfaceCreated(SurfaceHolder holder) {
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
    	final Camera.PictureCallback photoCallback = new Camera.PictureCallback() {
    		public void onPictureTaken(byte[] data, Camera camera) {
    			// do something with the photo JPEG (data[]) here!
    			Bitmap bm_rotado = null;
    			if (data != null){
    				Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
    				Matrix m = new Matrix();
    				m.postRotate(90);
    				// Asignamos la foto por si hiciera falta
    				bm_rotado = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
    			}
    			
    			File pictureFile = getOutputMediaFile();
    	        if (pictureFile == null){
    	            return;
    	        }

    	        try {
    	            FileOutputStream fos = new FileOutputStream(pictureFile);
    	            bm_rotado.compress(Bitmap.CompressFormat.JPEG, 100, fos);
    	            fos.close();

    	            // Actualizamos la galería
    	    	    activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+Environment.getExternalStorageDirectory())));
    	            
    	            // Guardamos automáticamente la foto en la lista
    				lista_fotos.add(u);
    				
    				// Y de paso cambiamos el menú contextual
    				if (lista_fotos.size() == maxPhotos){
						menuContextualFoto = Constantes.DIALOGO_MAX_FOTOS;
    					Toast.makeText(activity, "Máximo de fotos permitido alcanzado", Toast.LENGTH_LONG).show();
    				} else
						menuContextualFoto = Constantes.DIALOGO_FOTO_TOMADA;
    	            
    	        } catch (FileNotFoundException e) {
    	            Log.d("onPictureTaken()", "File not found: " + e.getMessage());
    	        } catch (IOException e) {
    	            Log.d("onPictureTaken()", "Error accessing file: " + e.getMessage());
    	        }
    		}
    	};
    	

		MakePhotosActivity.previewHolder.addCallback(surfaceCallback);
        MakePhotosActivity.previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        ventanaCamara.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Si ya la foto había sido tomada, deshabilitamos la ventana
				if (fotoTomada){
					Log.i("onClick() camara", "deshabilitado");
					return;
				}
				// Si no, enfocamos y sacamos la foto
				camara.autoFocus(new AutoFocusCallback(){
			    	Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
			    	    public void onShutter() {
			    	      // Reproduce un sonido propio aquí (si no, se usa el que está por defecto)
			    	    }
			    	};
			    	
			    	@Override
					public void onAutoFocus(boolean arg0, Camera arg1) {
			    		fotoTomada = true;
						camara.takePicture(shutterCallback, null, photoCallback);
					}
				});
			}
		});
        

        opciones = (ImageView) activity.findViewById(Constantes.BOTON_OPCIONES);
        opciones.setEnabled(true);
		opciones.setVisibility(ImageView.VISIBLE);
        opciones.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Aquí dependiendo del estado en el que estemos, podríamos
				// abrir un diálogo u otro
				showDialog(menuContextualFoto, activity);
			}
		});
        
        if (sinFoto){
        	ventanaCamara.setVisibility(SurfaceView.INVISIBLE);
			iv.setVisibility(ImageView.VISIBLE);
			iv.setImageResource(net.david.R.drawable.no_image);
        }
        if (fotoTomada){
        	ventanaCamara.setVisibility(SurfaceView.INVISIBLE);
        	iv.setVisibility(ImageView.VISIBLE);
        	iv.setImageBitmap(null);
        	
        	bitmap = null;
        	InputStream is;
			try {
				is = activity.getContentResolver().openInputStream(lista_fotos.get(lista_fotos.size()-1));
				
				// Hacemos invisible el cuadro de la cámara
				ventanaCamara.setVisibility(SurfaceView.INVISIBLE);
				bitmap = decodeSampledBitmapFromStream(is, Constantes.TAMANIO_PREVISUALIZA, Constantes.TAMANIO_PREVISUALIZA);
				is.close();
			    iv.setVisibility(ImageView.VISIBLE);
				iv.setImageBitmap(bitmap);	
			} catch (FileNotFoundException e) {}
			catch (OutOfMemoryError	e){
				bitmap = null;
				System.gc();
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("Error al cerrar el inputstream");
				e.printStackTrace();
			}
        }
	}
	
	/** Decode and sample down a bitmap from a input stream to the requested width and height.
     * @param is The input stream of the file to decode
     * @param reqWidth The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return A bitmap sampled down from the original with the same aspect ratio and dimensions
     *         that are equal to or greater than the requested width and height
     */
	private static Bitmap decodeSampledBitmapFromStream(InputStream is,
														int reqWidth,
														int reqHeight) {
		
	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    
	    InputStream aux = inputStreamCopy(is);
	    BitmapFactory.decodeStream(aux, null, options);  // SkImageDecoder::Factory returned null
	    try {
			aux.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    System.out.println("Foto reducida " + options.inSampleSize + " veces");
	    
	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeStream(aux, null, options);
	}

	
	/** Returns a copy of the InputStream specified.
     * @param is The input stream of the file to decode
     * @return A copy of the InputStream is
     */
	private static InputStream inputStreamCopy(InputStream is) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int len;
		byte[] buffer = new byte[1024];
		
		try {
			while ((len = is.read(buffer)) > -1) baos.write(buffer, 0, len);
			InputStream aux = new ByteArrayInputStream(baos.toByteArray());
			baos.flush();
			baos.close();
			return aux;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
     * Calculate an inSampleSize for use in a {@link BitmapFactory.Options} object when decoding
     * bitmaps using the decode* methods from {@link BitmapFactory}. This implementation calculates
     * the closest inSampleSize that will result in the final decoded bitmap having a width and
     * height equal to or larger than the requested width and height. This implementation
     * ensure a power of 2 is returned for inSampleSize which can be faster when decoding but
     * results in a larger bitmap which isn't as useful for caching purposes.
     *
     * @param options An options object with out* params already populated (run through a decode*
     *            method with inJustDecodeBounds==true
     * @param reqWidth The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
	private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) {
	        if (width > height)
	            inSampleSize = Math.round((float)height / (float)reqHeight);
	        else
	            inSampleSize = Math.round((float)width / (float)reqWidth);
	        
        // This offers some additional logic in case the image has a strange
        // aspect ratio. For example, a panorama may have a much larger
        // width than height. In these cases the total pixels might still
        // end up being too large to fit comfortably in memory, so we should
        // be more aggressive with sample down the image (=larger
        // inSampleSize).
        final float totalPixels = width * height;

        // Anything more than 2x the requested pixels we'll sample down
        // further.
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap)
            inSampleSize++;
	    }
	    for (int i=5; i>0; i--){
	    	if (Math.pow(2, i)<=inSampleSize){
	    		inSampleSize = (int) Math.pow(2, i);
	    		break;
	    	}
	    }
	    return inSampleSize;
	}
	
	private Dialog showDialog(int id, android.app.Activity activity) {
    	Dialog dialogo = null;

    	switch(id){
    		case Constantes.DIALOGO_SIN_FOTO:
    			dialogo = crearDialogoSeleccionSinFoto(activity);
    			break;
    		case Constantes.DIALOGO_FOTO_INICIAL:
    			dialogo = crearDialogoSeleccionFotoInicial(activity);
    			break;
    		case Constantes.DIALOGO_FOTO_TOMADA:
    			dialogo = crearDialogoSeleccionFotoTomada(activity);
    			break;
    		case Constantes.DIALOGO_FOTO_SIGUIENTE:
    			dialogo = crearDialogoSeleccionFotosSiguientes(activity);
    			break;
    		case Constantes.DIALOGO_MAX_FOTOS:
    			dialogo = crearDialogoMaxFotos(activity);
    			break;
    		default:
    			dialogo = null;
    			break;
    	}
    	dialogo.show();
    	return dialogo;
    }
	
	// Tratamiento del diálogo que saldrá cuando se haya seleccionado sin imagen
	private Dialog crearDialogoSeleccionSinFoto(final android.app.Activity activity) {
		// Declaramos cada ítem con su correspondiente icono
    	final Item[] items = {
    		    new Item(activity.getText(R.string.add_photo).toString(), android.R.drawable.ic_menu_camera)
		};

		ListAdapter adapter = new ArrayAdapter<Item>(
		    activity,
		    android.R.layout.select_dialog_item,
		    android.R.id.text1,
		    items){
		        public View getView(int position, View convertView, ViewGroup parent) {
		            // User super class to create the View
		            View v = super.getView(position, convertView, parent);
		            TextView tv = (TextView)v.findViewById(android.R.id.text1);

		            // Put the image on the TextView
		            tv.setCompoundDrawablesWithIntrinsicBounds(items[position].getIcon(), 0, 0, 0);

		            // Add margin between image and text (support various screen densities)
		            int dp5 = (int) (5 * activity.getResources().getDisplayMetrics().density + 0.5f);
		            tv.setCompoundDrawablePadding(dp5);

		            return v;
		        }
		    };

	    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    	
	    builder.setTitle(net.david.R.string.selecciona);
    	builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int item) {
	        	// Tratamiento de las opciones cámara y galería
	        	switch(item){
	        	case 0:	// Sacar foto
	        			// Quitar la imagen del ImageView, activar la cámara
	        			sinFoto = false;
	        			menuContextualFoto = Constantes.DIALOGO_FOTO_INICIAL;
	        			ventanaCamara.setVisibility(SurfaceView.VISIBLE);
	        			iv.setVisibility(ImageView.INVISIBLE);
	        			System.gc();
    					break;
	        	}
	        }
	    });
    	
    	return builder.create();
	}


	// Tratamiento del diálogo inicial que saldrá cuando se abra la cámara en
    // una primera instancia
    private Dialog crearDialogoSeleccionFotoInicial(final android.app.Activity activity){
    	// Declaramos cada ítem con su correspondiente icono
    	final Item[] items = {
    		    new Item(activity.getText(R.string.recover_photo).toString(), android.R.drawable.ic_menu_gallery),
    		    new Item(activity.getText(R.string.no_photo).toString(), android.R.drawable.ic_menu_close_clear_cancel)
		};

		ListAdapter adapter = new ArrayAdapter<Item>(
		    activity,
		    android.R.layout.select_dialog_item,
		    android.R.id.text1,
		    items){
		        public View getView(int position, View convertView, ViewGroup parent) {
		            // User super class to create the View
		            View v = super.getView(position, convertView, parent);
		            TextView tv = (TextView)v.findViewById(android.R.id.text1);

		            // Put the image on the TextView
		            tv.setCompoundDrawablesWithIntrinsicBounds(items[position].getIcon(), 0, 0, 0);

		            // Add margin between image and text (support various screen densities)
		            int dp5 = (int) (5 * activity.getResources().getDisplayMetrics().density + 0.5f);
		            tv.setCompoundDrawablePadding(dp5);

		            return v;
		        }
		    };

	    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    	
	    builder.setTitle(net.david.R.string.selecciona);
    	builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int item) {
	        	// Tratamiento de las opciones cámara y galería
	        	switch(item){
	        	case 0:	// Recuperar foto galería
						escondePrevioGaleria(activity);
	        			System.gc();
	        			crearGaleria(activity);
    					break;
	        	case 1: // Sin foto
	        			// Colocar una foto vacía en el SurfaceView
		        		// Por si no está visible, hacemos visible el ImageView
	        			// e inhabilitamos el SurfaceView para previsualizar
	        			sinFoto = true;
	        			ventanaCamara.setVisibility(SurfaceView.INVISIBLE);
	        			iv.setVisibility(ImageView.VISIBLE);
	        			iv.setImageResource(net.david.R.drawable.no_image);
	        			menuContextualFoto = Constantes.DIALOGO_SIN_FOTO;
	        			break;
	        	}
	        }
	        
			private void escondePrevioGaleria(final android.app.Activity activity) {
				RelativeLayout barrabaja = (RelativeLayout) activity.findViewById(Constantes.BARRA_BAJA);
				barrabaja.setVisibility(RelativeLayout.INVISIBLE);
				ProgressBar pb = (ProgressBar) activity.findViewById(Constantes.PROGRESSBAR);
				pb.setVisibility(ProgressBar.INVISIBLE);
				ImageView opciones = (ImageView) activity.findViewById(Constantes.BOTON_OPCIONES);
				opciones.setVisibility(ImageView.INVISIBLE);
				TextView titulo = (TextView) activity.findViewById(Constantes.TITULO_CAMARA);
				if (titulo != null) titulo.setVisibility(TextView.INVISIBLE);
				SurfaceView camara = (SurfaceView) activity.findViewById(Constantes.CANVAS_CAMARA);
				camara.setVisibility(SurfaceView.INVISIBLE);
				ImageView previsualiza = (ImageView) activity.findViewById(Constantes.PREVISUALIZA_CAMARA);
				previsualiza.setVisibility(ImageView.INVISIBLE);
			}
	    });
    	
    	return builder.create();
    }
    
    

    static Cursor imageCursor = null;
    private static int imageColumnIndex;
    private static GridView imageGrid = null;
    private static int count;
    
    /** Procedimiento para crear una galería de imágenes.
     * @author David
     * @param activity Activity de Android necesaria para realizar diversas operaciones
     */
	private void crearGaleria(final android.app.Activity activity) {
    	
    	if (imageCursor != null) imageCursor = null;
    	
    	// Set up an array of the Thumbnail Image ID column we want
        String[] projection = {MediaStore.Images.Media._ID};
        // Create the cursor pointing to the SDCard
        imageCursor = activity.managedQuery( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, // Which columns to return
                null,       // Return all rows
                null,
                MediaStore.Images.Media._ID+"");
        
        // Get the column index of the Thumbnails Image ID
        imageColumnIndex = imageCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
        count = imageCursor.getCount();
        
        if (imageGrid != null) imageGrid = null;
        
        imageGrid = (GridView) activity.findViewById(Constantes.GALERIA_CAMARA);
        imageGrid.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        imageGrid.setVisibility(GridView.VISIBLE);
        imageGrid.setBackgroundResource(net.david.R.drawable.degradado);
        imageGrid.setAdapter(new ImageAdapter(activity));
        imageGrid.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK){
					muestraPostGaleriaBotonAtras(activity);
					imageGrid = null;
					return true;
				}
				return false;
			}
			
			private void muestraPostGaleriaBotonAtras(android.app.Activity activity) {
				imageGrid.setVisibility(GridView.INVISIBLE);
				RelativeLayout barrabaja = (RelativeLayout) activity.findViewById(Constantes.BARRA_BAJA);
				barrabaja.setVisibility(RelativeLayout.VISIBLE);
				ProgressBar pb = (ProgressBar) activity.findViewById(Constantes.PROGRESSBAR);
				pb.setVisibility(ProgressBar.VISIBLE);
				ImageView opciones = (ImageView) activity.findViewById(Constantes.BOTON_OPCIONES);
				opciones.setVisibility(ImageView.VISIBLE);
				TextView titulo = (TextView) activity.findViewById(Constantes.TITULO_CAMARA);
				if (titulo != null) titulo.setVisibility(TextView.VISIBLE);
				SurfaceView camara = (SurfaceView) activity.findViewById(Constantes.CANVAS_CAMARA);
				camara.setVisibility(SurfaceView.VISIBLE);
			}
		});
        
        imageGrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				imageCursor.moveToPosition(position);
				String uri = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media._ID));
				
				// Ocultar GridView y mostrar el resto, con la imagen en el ImageView
				lista_fotos.add(Uri.withAppendedPath( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + uri));
				fotoTomada = true;
				
				muestraPostGaleria(activity, Uri.withAppendedPath( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + uri));
			}
			
			private void muestraPostGaleria(android.app.Activity activity, Uri uri) {
				imageGrid.setVisibility(GridView.INVISIBLE);
				RelativeLayout barrabaja = (RelativeLayout) activity.findViewById(Constantes.BARRA_BAJA);
				barrabaja.setVisibility(RelativeLayout.VISIBLE);
				ProgressBar pb = (ProgressBar) activity.findViewById(Constantes.PROGRESSBAR);
				pb.setVisibility(ProgressBar.VISIBLE);
				ImageView opciones = (ImageView) activity.findViewById(Constantes.BOTON_OPCIONES);
				opciones.setVisibility(ImageView.VISIBLE);
				TextView titulo = (TextView) activity.findViewById(Constantes.TITULO_CAMARA);
				if (titulo != null) titulo.setVisibility(TextView.VISIBLE);
				// Si se eligió foto mostramos el imageview, si no, la cámara de nuevo
				if (fotoTomada == true){
					iv.setVisibility(ImageView.VISIBLE);
					
					InputStream is;
					try {
						is = activity.getContentResolver().openInputStream(uri);
						
						// Hacemos invisible el cuadro de la cámara
						ventanaCamara.setVisibility(SurfaceView.INVISIBLE);
						bitmap = decodeSampledBitmapFromStream(is, Constantes.TAMANIO_PREVISUALIZA, Constantes.TAMANIO_PREVISUALIZA);
					    iv.setVisibility(ImageView.VISIBLE);
						iv.setImageBitmap(bitmap);
						is.close();
					} catch (FileNotFoundException e) {}
					catch (OutOfMemoryError	e){
						bitmap = null;
						System.gc();
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					// Y además cambiamos el menú contextual que deberá aparecer al
					// pulsar sobre el botón
					if (lista_fotos.size() == maxPhotos){
						menuContextualFoto = Constantes.DIALOGO_MAX_FOTOS;
						Toast.makeText(activity, activity.getText(R.string.max_photos_reached).toString(), Toast.LENGTH_LONG).show();
					} else
						menuContextualFoto = Constantes.DIALOGO_FOTO_TOMADA;
				} else {
					SurfaceView camara = (SurfaceView) activity.findViewById(Constantes.CANVAS_CAMARA);
					camara.setVisibility(SurfaceView.VISIBLE);
				}
				System.gc();
			}
		});
	}

    
    /** Clase adaptador de las imágenes para la galería en forma de miniatura.
     * @author David
     */
    private static class ImageAdapter extends BaseAdapter {

        private Context context;
        int mGalleryItemBackground;
        private Bitmap b = null;

        public ImageAdapter(Context localContext) {
            context = localContext;
			if (cache == null)
            	cache = new SparseArray<SoftReference<Bitmap>>();
        }
        @Override
        public int getCount() {
        	return count;
        }
        @Override
        public Object getItem(int position) {
            return position;
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	ImageView imageView = new ImageView(context);

            // Move cursor to current position
	        imageCursor.moveToPosition(position);
	        // Get the current value for the requested column
	        int imageID = imageCursor.getInt(imageColumnIndex);
	        // Set the content of the image based on the provided URI
	        try {
	        	if (cache.get(imageID) != null){
	        		// Por si se destruyó la referencia a la foto
	        		if (cache.get(imageID).get() == null){
	        			cache.remove(imageID);
	        			b = MediaStore.Images.Thumbnails.getThumbnail(	context.getContentResolver(),
								imageID,
								MediaStore.Images.Thumbnails.MICRO_KIND,
								null);
	        			cache.put(imageID, new SoftReference<Bitmap>(b));
	        		} else {
	        			// Si no, foto en caché, la rescatamos
		        		b = cache.get(imageID).get();
	        		}
	        	} else {
	        		// La miniatura no existía, la creamos y la añadimos a caché
	        		b = MediaStore.Images.Thumbnails.getThumbnail(	context.getContentResolver(),
																		imageID,
	        															MediaStore.Images.Thumbnails.MICRO_KIND,
																		null);
	        		cache.put(imageID, new SoftReference<Bitmap>(b));
	        	}
	        } catch (OutOfMemoryError e){
				b = null;
				System.gc();
	        	e.printStackTrace();
	        }

			imageView.setImageBitmap(b);
	        imageView.setVisibility(ImageView.VISIBLE);
	        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
	        Display display = wm.getDefaultDisplay();
            imageView.setLayoutParams(new GridView.LayoutParams(display.getWidth()/3, display.getWidth()/4));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setBackgroundResource(mGalleryItemBackground);
            return imageView;
        }
    }


	// Tratamiento del diálogo que saldrá cuando se abra la cámara en la segunda
    // y sucesivas fotos
    private Dialog crearDialogoSeleccionFotosSiguientes(final android.app.Activity activity){
    	// Declaramos cada ítem con su correspondiente icono
    	final Item[] items = {
    		    new Item(activity.getText(R.string.recover_photo).toString(), android.R.drawable.ic_menu_gallery),
    		    new Item(activity.getText(R.string.cancel_take_photo).toString(), android.R.drawable.ic_menu_close_clear_cancel)
		};

		ListAdapter adapter = new ArrayAdapter<Item>(
		    activity,
		    android.R.layout.select_dialog_item,
		    android.R.id.text1,
		    items){
		        public View getView(int position, View convertView, ViewGroup parent) {
		            // User super class to create the View
		            View v = super.getView(position, convertView, parent);
		            TextView tv = (TextView)v.findViewById(android.R.id.text1);

		            // Put the image on the TextView
		            tv.setCompoundDrawablesWithIntrinsicBounds(items[position].getIcon(), 0, 0, 0);

		            // Add margin between image and text (support various screen densities)
		            int dp5 = (int) (5 * activity.getResources().getDisplayMetrics().density + 0.5f);
		            tv.setCompoundDrawablePadding(dp5);

		            return v;
		        }
		    };

	    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    	
	    builder.setTitle(net.david.R.string.selecciona);
    	builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int item) {
	        	// Tratamiento de las opciones cámara y galería
	        	switch(item){
	        	case 0:	// Recuperar foto galería
	        			// Se abre la galería, se elige una foto y se muestra
	        			// en el SurfaceView
						escondePrevioGaleria(activity);
	        			System.gc();
	        			crearGaleria(activity);
    					break;
	        	case 1: // Cancelar y volver atrás
	        			// Como no había foto sacada ni recuperada, lo que hacemos
	        			// es colocar la última foto puesta en el canvas
		        		// Por si no está visible, hacemos visible el ImageView
	        			// e inhabilitamos el SurfaceView para previsualizar
	        			ventanaCamara.setVisibility(SurfaceView.INVISIBLE);
	        			iv.setVisibility(ImageView.VISIBLE);
	        			iv.setImageBitmap(null);
	        			
	        			InputStream is;
						try {
							is = activity.getContentResolver().openInputStream(lista_fotos.get(lista_fotos.size()-1));
							
							// Hacemos invisible el cuadro de la cámara
							ventanaCamara.setVisibility(SurfaceView.INVISIBLE);
							bitmap = decodeSampledBitmapFromStream(is, Constantes.TAMANIO_PREVISUALIZA, Constantes.TAMANIO_PREVISUALIZA);
						    iv.setVisibility(ImageView.VISIBLE);
							iv.setImageBitmap(bitmap);
							is.close();
						} catch (FileNotFoundException e) {}
						catch (OutOfMemoryError	e){
							bitmap = null;
							System.gc();
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
	        			
	        			fotoTomada = true;
	        			// Y asignamos el nuevo menú contextual
	        			menuContextualFoto = Constantes.DIALOGO_FOTO_TOMADA;
	        			break;
	        	}
	        }
	        
	        private void escondePrevioGaleria(final android.app.Activity activity) {
				RelativeLayout barrabaja = (RelativeLayout) activity.findViewById(Constantes.BARRA_BAJA);
				barrabaja.setVisibility(RelativeLayout.INVISIBLE);
				ProgressBar pb = (ProgressBar) activity.findViewById(Constantes.PROGRESSBAR);
				pb.setVisibility(ProgressBar.INVISIBLE);
				ImageView opciones = (ImageView) activity.findViewById(Constantes.BOTON_OPCIONES);
				opciones.setVisibility(ImageView.INVISIBLE);
				TextView titulo = (TextView) activity.findViewById(Constantes.TITULO_CAMARA);
				if (titulo != null) titulo.setVisibility(TextView.INVISIBLE);
				SurfaceView camara = (SurfaceView) activity.findViewById(Constantes.CANVAS_CAMARA);
				camara.setVisibility(SurfaceView.INVISIBLE);
				ImageView previsualiza = (ImageView) activity.findViewById(Constantes.PREVISUALIZA_CAMARA);
				previsualiza.setVisibility(ImageView.INVISIBLE);
			}
	    });
    	
    	return builder.create();
    }
    
    
    // Tratamiento del diálogo que saldrá cuando se haya tomado una foto o se
    // haya obtenido una foto de la galería
    private Dialog crearDialogoSeleccionFotoTomada(final android.app.Activity activity){
    	// Declaramos cada ítem con su correspondiente icono
    	final Item[] items = {
    		    new Item(activity.getText(R.string.add_new_photo).toString(), android.R.drawable.ic_menu_add),
    		    new Item(activity.getText(R.string.dismiss_photo).toString(), android.R.drawable.ic_menu_delete)
		};

		ListAdapter adapter = new ArrayAdapter<Item>(
		    activity,
		    android.R.layout.select_dialog_item,
		    android.R.id.text1,
		    items){
		        public View getView(int position, View convertView, ViewGroup parent) {
		            // User super class to create the View
		            View v = super.getView(position, convertView, parent);
		            TextView tv = (TextView)v.findViewById(android.R.id.text1);

		            // Put the image on the TextView
		            tv.setCompoundDrawablesWithIntrinsicBounds(items[position].getIcon(), 0, 0, 0);

		            // Add margin between image and text (support various screen densities)
		            int dp5 = (int) (5 * activity.getResources().getDisplayMetrics().density + 0.5f);
		            tv.setCompoundDrawablePadding(dp5);

		            return v;
		        }
		    };

	    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    	
    	builder.setTitle(net.david.R.string.selecciona);
    	builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int item) {        	
	        	// Tratamiento de las opciones cámara y galería
	        	switch(item){
	        	case 0:	// Agregar nueva foto
	        			// Asignamos el nuevo menú contextual
	        			menuContextualFoto = Constantes.DIALOGO_FOTO_SIGUIENTE;
	        			// Por si no está visible, hacemos visible el surfaceView
	        			// e inhabilitamos el ImageView para previsualizar
	        			ventanaCamara.setVisibility(SurfaceView.VISIBLE);
	        			iv.setVisibility(ImageView.INVISIBLE);
	        			// Habilitamos el hacer click en la ventana
	        			fotoTomada = false;
	        			// Se abre de nuevo la cámara
	        			if (camara != null)
	        				camara.startPreview();
    					break;
	        	case 1:	// Descartar foto
	        			// Por si no está visible, hacemos visible el surfaceView
	        			// e inhabilitamos el ImageView para previsualizar
	        			ventanaCamara.setVisibility(SurfaceView.VISIBLE);
	        			iv.setVisibility(ImageView.INVISIBLE);
	        			// Habilitamos el hacer click en la ventana
	        			fotoTomada = false;
	        			
	        			// Eliminamos la foto actual de la lista (será la última
	        			// foto de la lista)
	        			lista_fotos.remove(lista_fotos.size()-1);
	        			// Establecemos el nuevo menú contextual
	        			// Si la lista de fotos se queda sin fotos será el diálogo inicial
	        			if (lista_fotos.isEmpty()){
	        				menuContextualFoto = Constantes.DIALOGO_FOTO_INICIAL;
	        			}
	        			// Si la lista de fotos contiene fotos, será el diálogo fotos siguientes
	        			else
	        				menuContextualFoto = Constantes.DIALOGO_FOTO_SIGUIENTE;
	        			// Se abre de nuevo la cámara
	        			if (camara != null)
	        				camara.startPreview();
	        			break;
	        	}
	        }
	    });
    	
    	return builder.create();
    }
	
    // Tratamiento del diálogo que saldrá cuando se haya alcanzado el máximo
    // de fotos
    private Dialog crearDialogoMaxFotos(final android.app.Activity activity){
    	// Declaramos cada ítem con su correspondiente icono
    	final Item[] items = {
    		    new Item(activity.getText(R.string.dismiss_photo).toString(), android.R.drawable.ic_menu_delete)
		};

		ListAdapter adapter = new ArrayAdapter<Item>(
		    activity,
		    android.R.layout.select_dialog_item,
		    android.R.id.text1,
		    items){
		        public View getView(int position, View convertView, ViewGroup parent) {
		            // User super class to create the View
		            View v = super.getView(position, convertView, parent);
		            TextView tv = (TextView)v.findViewById(android.R.id.text1);

		            // Put the image on the TextView
		            tv.setCompoundDrawablesWithIntrinsicBounds(items[position].getIcon(), 0, 0, 0);

		            // Add margin between image and text (support various screen densities)
		            int dp5 = (int) (5 * activity.getResources().getDisplayMetrics().density + 0.5f);
		            tv.setCompoundDrawablePadding(dp5);

		            return v;
		        }
		    };

	    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    	
    	builder.setTitle(net.david.R.string.selecciona);
    	builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int item) {        	
	        	// Tratamiento de las opciones cámara y galería
	        	switch(item){
	        	case 0:	// Descartar foto
	        			// Por si no está visible, hacemos visible el surfaceView
	        			// e inhabilitamos el ImageView para previsualizar
	        			ventanaCamara.setVisibility(SurfaceView.VISIBLE);
	        			iv.setVisibility(ImageView.INVISIBLE);
	        			// Habilitamos el hacer click en la ventana
	        			fotoTomada = false;
	        			
	        			// Eliminamos la foto actual de la lista (será la última
	        			// foto de la lista)
	        			lista_fotos.remove(lista_fotos.size()-1);
	        			// Establecemos el nuevo menú contextual
	        			// Si la lista de fotos se queda sin fotos será el diálogo inicial
	        			if (lista_fotos.isEmpty()){
	        				menuContextualFoto = Constantes.DIALOGO_FOTO_INICIAL;
	        			}
	        			// Si la lista de fotos contiene fotos, será el diálogo fotos siguientes
	        			else
	        				menuContextualFoto = Constantes.DIALOGO_FOTO_SIGUIENTE;
	        			// Se abre de nuevo la cámara
	        			if (camara != null)
	        				camara.startPreview();
	        			break;
	        	}
	        }
	    });
    	
    	return builder.create();
    }
    
    /** Crea un fichero para almacenar la imagen.
	 *  @author David
	 *  @return Fichero creado */
	private static File getOutputMediaFile(){
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
	    return mediaFile;
	}

	@Override
	public Object saveData(android.app.Activity act) {
		// No hace nada en una activity no-automática
		return null;
	}

	@Override
	public void stop() {
		if (cache != null){
			cache.clear();
			cache = null;
		}
		if (bitmap != null){
			bitmap.recycle();
			bitmap = null;
		}
		if (imageGrid != null){
			imageGrid.removeAllViewsInLayout();
			imageGrid = null;
		}
		System.gc();
	}
}