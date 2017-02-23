package com.example.android.popularmovies.utilities;


import android.content.res.Configuration;
import android.graphics.Point;

public class DesignUtils {
    /*
    Possible sizes for movies's posters
     */
    private final static String SMALL_SIZE = "/w185";
    private final static String MEDIUM_SIZE = "/w342";
    private final static String BIG_SIZE = "/w500";
    private final static String GIANT_SIZE = "/w780";


    /**
     * Return the correct size to download for movie's poster in function of the device's density.(for the GridLayout)
     *
     * @param density density of the device
     * @return movie's poster size
     */
    public static String calculatePosterSizeForGrid(float density) {
        if (density >= 2.6) {
            //xxxhdpi
            //xxhdpi
            return BIG_SIZE;
        } else if (density >= 1.5) {
            //xhdpi
            //hdpi
            return MEDIUM_SIZE;
        }
        //mdpi
        return SMALL_SIZE;
    }

    /**
     * Calculate the size of the movie's image to download, in function of density and the X-size in dp of the device.(for DetailsActivity)
     *
     * @param density density of the device
     * @param width   X-size in dp
     * @return size of the movie's poster
     */
    public static String calculatePosterSizeForDetails(float density, float width, float height) {
        float minSize = Math.min(width, height);
        if (minSize >= 750) {
            return GIANT_SIZE;
        } else if (minSize >= 450) {
            if (density >= 2.0) {
                //xxxhdpi
                //xxhdpi
                //xhdpi
                return GIANT_SIZE;
            } else if (density > 1.5) {
                //hdpi
                return BIG_SIZE;
            }
            //mdpi
            return MEDIUM_SIZE;
        } else if (density > 2.5) {
            //xxxhdpi
            //xxhdpi
            return GIANT_SIZE;
        } else if (density > 1.5) {
            //xhdpi
            //hdpi
            return BIG_SIZE;
        }
        //mdpi
        return SMALL_SIZE;
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
     * Calculate the number of columns in function of the X-size in dp of the device .So it changes if(for the GridLayoutManager).
     * the orientation goes from portrait to landscape
     *
     * @param orientation portrait or landscape
     * @param width       X-size in dp
     * @return number of columns
     */
    public static int calculateNumberOfColumns(int orientation, float width) {
        int numberOfColumns;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (width <= 480) {
                //small
                //normal
                numberOfColumns = 3;
                return numberOfColumns;
            } else if (width <= 720) {
                //large
                numberOfColumns = 4;
                return numberOfColumns;
            }
            //xlarge
            numberOfColumns = 5;
            return numberOfColumns;

        } else if (width >= 1200) {
            //big xlarge
            numberOfColumns = 8;
            return numberOfColumns;
        } else if (width >= 960) {
            //xlarge
            numberOfColumns = 6;
            return numberOfColumns;
        } else if (width >= 720) {
            //large
            numberOfColumns = 5;
            return numberOfColumns;
        }
        //normal
        //small
        numberOfColumns = 4;
        return numberOfColumns;
    }
}
