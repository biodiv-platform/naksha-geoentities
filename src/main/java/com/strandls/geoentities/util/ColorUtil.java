package com.strandls.geoentities.util;

import java.awt.Color;

/**
 * 
 * @author vilay
 *
 */

public class ColorUtil {
	/**
	 * 
	 * @param colorStr - Hex color (eg. #4F0386)
	 * @return return the color object from the hex color code 
	 */
	public static Color hex2Rgb(String colorStr) {
	    return new Color(
	            Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
	            Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
	            Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) );
	}

}
