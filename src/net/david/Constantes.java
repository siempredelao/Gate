package net.david;

//Clase que contiene todas las macros
public class Constantes {
	// Identificador único de la aplicación
	public static String guid;
	
    // Identificadores de las opciones de los AlertDialog
	public static final int CAMARA  					= 1;
	public static final int GALERIA 					= 2;
	public static final int GPS     					= 3;
	public static final int MAPA    					= 4;
	
	
	// Tipos de actividades: automática y no automática
	public static final boolean ACTIVIDAD_AUTOMATICA 	= false;
	public static final boolean ACTIVIDAD_NO_AUTOMATICA = true;
	
	// Vista completa
	public static final int VISTA						= 007;
	
	// Elementos de la pantalla de inicio
	public static final int GATE						= 100;
	public static final int BOTON_HECHOS				= 110;
	public static final int BOTON_TAREAS				= 120;
	
	// Elementos de pantalla no-automática
	public static final int BARRA_BAJA					= 200;
	public static final int ACTIVITY_MESSAGE			= 210;
	public static final int GATE_MESSAGE				= 220;
	public static final int PROGRESSBAR					= 230;
	public static final int BOTON_OPCIONES				= 240;
	public static final int INFO_R						= 250;
	public static final int INFO_S						= 260;
	public static final int BOTON_ATRAS 				= 270;
	public static final int BOTON_SIGUIENTE				= 280;
	// Elementos de pantalla automática
	public static final int BOTONES						= 230;
	public static final int START						= 310;
	public static final int STOP						= 320;
	public static final int INFO_AUTO					= 330;
	// Elementos de pantalla resumen
	public static final int VISOR_HTML					= 400;
	
	// Cámara de fotos - Serie 1XXX
	public static final int TITULO_CAMARA				= 1100;
	public static final int CANVAS_CAMARA				= 1200;
	public static final int PREVISUALIZA_CAMARA 		= 1300;
	public static final int GALERIA_CAMARA 				= 1400;
	// Mapa - Serie 2XXX
	public static final int TITULO_MAPA					= 2100;
	public static final int CONTENIDO_MAPA				= 2200;
	// Formulario - Serie 3XXX
	public static final int TITULO_FORMULARIO			= 3100;
	public static final int TITULO_ITEM_FORMULARIO		= 3200;
	public static final int CONTENIDO_ITEM_FORMULARIO	= 3300;
	// GPS - Serie 4XXX
	public static final int TITULO_GPS					= 4100;
	public static final int CONTENIDO_GPS				= 4200;
	// Lista - Serie 5XXX
	public static final int TITULO_LISTA 				= 5100;
	public static final int CONTENIDO_LISTA 			= 5200;
	
	
	
	// Identificadores de los cuadros de diálogo
	public static final int DIALOGO_SIN_FOTO			= 0;
	public static final int DIALOGO_FOTO_INICIAL		= 1;
	public static final int DIALOGO_FOTO_TOMADA			= 2;
	public static final int DIALOGO_FOTO_SIGUIENTE		= 3;
	public static final int DIALOGO_MAX_FOTOS			= 4;
	public static final int DIALOGO_MAPA				= 5;
	public static final int DIALOGO_DESCRIPCION			= 6;
	public static final int DIALOGO_BUSQUEDA			= 7;
	public static final int DIALOGO_GPS					= 8;
	
	
	
	// Mapas y GPS
	public static final int ZOOM						= 18;
	public static final int GPS_INTERVALO 				= 60000;	// 60 segundos
	public static final int GPS_DISTANCIA_METROS 		= 10;		// 10 metros
	
	
	// Tamaño del ImageView
	public static final int TAMANIO_PREVISUALIZA		= 300;
	public static final int TAMANIO_MINIATURA			= 100;

}
