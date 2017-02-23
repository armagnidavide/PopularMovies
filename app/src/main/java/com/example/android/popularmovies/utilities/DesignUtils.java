package com.example.android.popularmovies.utilities;


import android.content.res.Configuration;
import android.graphics.Point;
import android.util.Log;

public class DesignUtils {
    /**
     * @param density density of the device range 1-4 from mdpi to xxxhdpi
     * @return size of the movie's poster to download for each grid item
     */
    public static String calculatePosterSizeForGrid(float density) {
        Log.e("density", density + "");
        if (density >= 2.6) {
            //xxxhdpi
            //xxhdpi
            return "/w500";
        } else if (density >= 1.5) {
            //xhdpi
            //hdpi
            return "/w342";
        }
        //mdpi
        return "/w185";
    }

    /**
     * calculates the size of the movie's image to download, in function of density and the X-size in dp of the device.
     *
     * @param density density of the device range 1-4 from mdpi to xxxhdpi
     * @param width   X-size in dp
     * @return size of the movie's poster to download for DetailsActivity
     */
    public static String calculatePosterSizeForDetails(float density, float width, float height) {
        float minSize = Math.min(width, height);
        Log.e("minSize", minSize + "");
        if (minSize >= 750) {
            return "/w780";
        } else if (minSize >= 450) {
            if (density >= 2.0) {
                //xxxhdpi
                //xxhdpi
                //xhdpi
                return "/w780";
            } else if (density > 1.5) {
                //hdpi
                return "/w500";
            }
            //mdpi
            return "/w342";
        } else if (density > 2.5) {
            //xxxhdpi
            //xxhdpi
            return "/w780";
        } else if (density > 1.5) {
            //xhdpi
            //hdpi
            return "/w500";
        }
        //mdpi
        return "/w185";
    }

    public static float calculateScreenHeight(Point size, float density) {
        return size.y / density;
    }

    public static float calculateScreenWidth(Point size, float density) {
        return size.x / density;
    }

    public static int getScreenOrientation(float width, float height) {
        int orientation;
        if (width < height) {
            orientation = Configuration.ORIENTATION_PORTRAIT;
        } else {
            orientation = Configuration.ORIENTATION_LANDSCAPE;
        }
        return orientation;
    }

    /**
     * calculates the number of columns in function of the X-size in dp of the device .So it changes if
     * the orientation goes from portrait to landscape
     *
     * @param orientation portrait or landscape
     * @param width       X-size in dp
     * @return number of columns for the gridLayoutManager
     */
    public static int calculateNumberOfColumns(int orientation, float width) {
        int numberOfColumns;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (width <= 480) {
                numberOfColumns = 3;
                return numberOfColumns;
            } else if (width <= 720) {
                numberOfColumns = 4;
                return numberOfColumns;
            }
            numberOfColumns = 5;
            return numberOfColumns;

        } else if (width >= 1200) {
            numberOfColumns = 8;
            return numberOfColumns;
        } else if (width >= 960) {
            numberOfColumns = 6;
            return numberOfColumns;
        } else if (width >= 720) {
            numberOfColumns = 5;
            return numberOfColumns;
        }
        numberOfColumns = 4;
        return numberOfColumns;
    }
}
