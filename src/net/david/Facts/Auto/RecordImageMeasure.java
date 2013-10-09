package net.david.Facts.Auto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import net.david.Constantes;
import net.david.Activities.Activity;
import net.david.Activities.Auto.RecordImageActivity;
import net.david.Definitions.ActivityDefinition;
import net.david.Facts.Measure;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

public final class RecordImageMeasure extends Measure {
		/**
	     * Decode and sample down a bitmap from a input stream to the requested width and height.
	     *
	     * @param is The input stream of the file to decode
	     * @param reqWidth The requested width of the resulting bitmap
	     * @param reqHeight The requested height of the resulting bitmap
	     * @return A bitmap sampled down from the original with the same aspect ratio and dimensions
	     *         that are equal to or greater than the requested width and height
	     */
	private Bitmap decodeSampledBitmapFromStream(InputStream is,
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
			Log.d("decodeSampledBitmapFromStream", "Error al resetear el stream");
			e.printStackTrace();
		}

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeStream(aux, null, options);
	}
	
	private InputStream inputStreamCopy(InputStream is) {
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
     * height equal to or larger than the requested width and height. This implementation does not
     * ensure a power of 2 is returned for inSampleSize which can be faster when decoding but
     * results in a larger bitmap which isn't as useful for caching purposes.
     *
     * @param options An options object with out* params already populated (run through a decode*
     *            method with inJustDecodeBounds==true
     * @param reqWidth The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
	private int calculateInSampleSize(
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

	private String imagenEnBase64_2(Uri uri, android.app.Activity activity) {
		InputStream is;
		try {
			is = activity.getContentResolver().openInputStream(uri);
			
			Bitmap bitmap = decodeSampledBitmapFromStream(is, Constantes.TAMANIO_PREVISUALIZA, Constantes.TAMANIO_PREVISUALIZA);
			if (bitmap == null)
				return "";
			System.gc();
			int bytes = bitmap.getWidth()*bitmap.getHeight()*2;
			ByteBuffer buffer = ByteBuffer.allocate(bytes);
			bitmap.copyPixelsToBuffer(buffer);
			is.close();
			return Base64.encodeToString(buffer.array(), Base64.DEFAULT); // Este es el que hace los GC FOR MALLOC
//			return new String(buffer.array());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getMeasureToHTML(android.app.Activity act, Activity activity, ActivityDefinition activityDefinition) {
		return null;
	}

	@Override
	public String serializeData(Activity activity, android.app.Activity act) {
		String serialization = "";
		RecordImageActivity aca = (RecordImageActivity) activity;
		if (aca.getFoto() != null){
			// Abrir la foto y volcarla en base64
			System.gc();
			serialization = serialization.concat("<photo>");
			serialization = serialization.concat(imagenEnBase64_2(aca.getFoto(), act));
			serialization = serialization.concat("</photo>");
		} else 
			serialization = "<photo>" + "" + "</photo>";
		return serialization;
	}
	
	@Override
	public String generateFinalMeasure(){
		return "  <measure type=\"camera\">\n" + this.getId_Data() + "\n  </measure>";
	}
}
