package pl.elfdump.wloczykij.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.IOException;

public class ImageUtil {
    public static Bitmap scaleBitmapToMaxSize(Bitmap bitmap, int maxSize) {
        if (bitmap.getWidth() <= maxSize && bitmap.getHeight() <= maxSize)
            return bitmap;

        int new_width = bitmap.getWidth();
        int new_height = bitmap.getHeight();

        if (new_width > maxSize) {
            new_height = (int) (new_height * (maxSize / (float) new_width));
            new_width = maxSize;
        }

        if (new_height > maxSize) {
            new_width = (int) (new_width * (maxSize / (float) new_height));
            new_height = maxSize;
        }

        return Bitmap.createScaledBitmap(bitmap, new_width, new_height, true);
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static Bitmap rotateImageExif(String filename, Bitmap bitmap) throws IOException {
        ExifInterface ei = new ExifInterface(filename);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED);

        switch(orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(bitmap, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(bitmap, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(bitmap, 270);
            case ExifInterface.ORIENTATION_NORMAL:
            default:
                return bitmap;
        }
    }
}
