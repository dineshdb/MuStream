package com.sankalpa.mustream;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

/**
 * Created by deenesh12 on 1/26/18.
 */

public class QRCodeGenerator extends AsyncTask<String, Integer, Bitmap> {
    int QRcodeWidth = 500;
    @Override
    protected Bitmap doInBackground(String... strings) {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(strings[0], BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );
        } catch (IllegalArgumentException Illegalargumentexception) {
            return null;
        } catch (WriterException e1) {
            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();
        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;
            for (int x = 0; x < bitMatrixWidth; x++) {
                pixels[offset + x] = bitMatrix.get(x, y) ? Color.BLACK :Color.WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }
    protected void onPostExecute(Long result) {
        
    }

}
