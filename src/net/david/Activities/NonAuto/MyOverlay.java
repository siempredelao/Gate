package net.david.Activities.NonAuto;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.widget.ImageView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

/** Clase para representar una capa asociada a un mapa.
 *  @author David
 */
public class MyOverlay extends Overlay {
    private GeoPoint point;
    private ImageView fin;

	public MyOverlay(GeoPoint point, ImageView iv) {
    	super();
    	this.point = point;
    	fin = iv;
    }
    
    public ImageView getFin() {
		return fin;
	}

	public void setFin(ImageView fin) {
		this.fin = fin;
	}

	public GeoPoint getPoint() {
		return point;
	}

	public void setPoint(GeoPoint point) {
		this.point = point;
	}
   
	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
		super.draw(canvas, mapView, shadow);

		Point scrnPoint = new Point();
		mapView.getProjection().toPixels(this.point, scrnPoint);

		Bitmap marker = BitmapFactory.decodeResource(mapView.getResources(), net.david.R.drawable.marcador);
		canvas.drawBitmap(marker,
							scrnPoint.x - marker.getWidth()/2,
							scrnPoint.y - marker.getHeight(), null);
		return true;
	}
	
	@Override
	public boolean onTap(GeoPoint point, MapView mapView) {
//		Context contexto = mapView.getContext();
//		String msg = "Lat: " + point.getLatitudeE6()/1E6 + " - " + 
//		             "Lon: " + point.getLongitudeE6()/1E6;
//		Toast.makeText(contexto, msg, Toast.LENGTH_SHORT).show();
		
		// Establecemos el punto tocado como final
		setPoint(point);
		
		return true;
	}
} 
