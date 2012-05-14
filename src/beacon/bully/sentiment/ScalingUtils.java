package beacon.bully.sentiment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.DisplayMetrics;

public class ScalingUtils {
	public static final double SCALE_3_2 = 3.0/2.0;
	public static final double SCALE_4_3 = 4.0/3.0;
	public static final double SCALE_5_3 = 5.0/3.0;
	public static final double SCALE_8_5 = 8.0/5.0;
	public static final double SCALE_9_5 = 9.0/5.0;
	public static final double SCALE_128_75 = 128.0/75.0;
	public static DisplayMetrics metrics = new DisplayMetrics();
	public static final double[] scales = {SCALE_3_2,SCALE_4_3,SCALE_5_3,SCALE_8_5,SCALE_9_5,SCALE_128_75};
	
	public static int getWidth(Activity active, SharedPreferences appPrefs, Editor prefEditor) {
		if (appPrefs.getInt("device_width", 0) < 1) {
			active.getWindowManager().getDefaultDisplay().getMetrics(metrics);
			prefEditor.putInt("device_width", metrics.widthPixels);
		}
		return appPrefs.getInt("device_width", 0);
	}
	
	public static double getMyScale(Activity active) {
		
		active.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		double scale = (double)metrics.heightPixels/(double)metrics.widthPixels;
		HashMap<Double,Double> storeScales = new HashMap<Double,Double>();
		ArrayList<Double> sortScales = new ArrayList<Double>();
		for (double scaled:scales) {
			sortScales.add(Math.abs(scale-scaled));
			storeScales.put(Math.abs(scale-scaled), scaled);
		}
		Collections.sort(sortScales);
		return storeScales.get(sortScales.get(0));
	}
	
	public static int getScaledGraphic(String graphic, double scale) {
		if (graphic.equals("bully_for_you")) {
			if (scale == SCALE_8_5) {
				return R.drawable.bully_for_you_1_6;
			}
			else {
				return R.drawable.bully_for_you;
			}
		} else if (graphic.equals("bully_background")) {
			if (scale == SCALE_8_5) {
				return R.drawable.bully_background_1_6;
			}
			else {
				return R.drawable.bully_background;
			}
		} else if (graphic.equals("candidate")) {
			if (scale == SCALE_8_5) {
				return R.drawable.candidate_1_6;
			}
			else {
				return R.drawable.candidate;
			}
		} 
		
		return 0;
		
	}
}
