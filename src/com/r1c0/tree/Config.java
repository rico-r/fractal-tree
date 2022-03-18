package com.r1c0.tree;

import android.content.SharedPreferences;

public class Config{
	private boolean useSeed, triangleTip, antiAlias;
	private int depth;
	private SharedPreferences.Editor editor;
	private String seed;
	private String initLength, initSize;
	private String angle, angleDev;
	private String relLength, relSize;
	private String relLengthDev, relSizeDev;

	public Config(MainActivity main, SharedPreferences pref){
		this.editor = pref.edit();
		useSeed = pref.getBoolean("use_seed", false);
		triangleTip = pref.getBoolean("sharp_tip", true);
		antiAlias = pref.getBoolean("anti_alias", false);
		depth = pref.getInt("iteration", 13);
		seed = pref.getString("seed", "");
		initLength = pref.getString("init_length", "");
		initSize = pref.getString("init_size", "");
		angle = pref.getString("angle", "");
		angleDev = pref.getString("angle_deviation", "");
		relLength = pref.getString("relative_length", "");
		relLengthDev = pref.getString("relative_length_deviation", "");
		relSize = pref.getString("relative_size", "");
		relSizeDev = pref.getString("relative_size_deviation", "");
		
		main.useSeed.setChecked(useSeed);
		main.sharpTip.setChecked(triangleTip);
		main.seed.setText(seed);
		main.seed.setEnabled(useSeed);
		main.antiAlias.setChecked(antiAlias);
		main.cv.setAntiAlias(antiAlias);
		main.iteration.setSelection(depth);
		main.initLength.setText(initLength);
		main.initSize.setText(initSize);
		main.angle.setText(angle);
		main.angleDev.setText(angleDev);
		main.relLength.setText(relLength);
		main.relLengthDev.setText(relLengthDev);
		main.relSize.setText(relSize);
		main.relSizeDev.setText(relSizeDev);
	}

	public void setAntiAlias(boolean antiAlias){
		this.antiAlias = antiAlias;
		editor.putBoolean("anti_alias", antiAlias);
	}

	public boolean isAntiAlias(){
		return antiAlias;
	}

	public void setAngle(String angle){
		this.angle = angle;
		editor.putString("angle", angle);
	}

	public String getAngle(){
		return angle;
	}

	public void setAngleDev(String angleDev){
		this.angleDev = angleDev;
		editor.putString("angle_deviation", angleDev);
	}

	public String getAngleDev(){
		return angleDev;
	}

	public void setRelLength(String relLength){
		this.relLength = relLength;
		editor.putString("relative_length", relLength);
	}

	public String getRelLength(){
		return relLength;
	}

	public void setRelSize(String relSize){
		this.relSize = relSize;
		editor.putString("relative_size", relSize);
	}

	public String getRelSize(){
		return relSize;
	}

	public void setRelLengthDev(String relLengthDev){
		this.relLengthDev = relLengthDev;
		editor.putString("relative_length_deviation", relLengthDev);
	}

	public String getRelLengthDev(){
		return relLengthDev;
	}

	public void setRelSizeDev(String relSizeDev){
		this.relSizeDev = relSizeDev;
		editor.putString("relative_size_deviation", relSizeDev);
	}

	public String getRelSizeDev(){
		return relSizeDev;
	}

	public void setInitLength(String initLength){
		this.initLength = initLength;
		editor.putString("init_length", initLength);
	}

	public String getInitLength(){
		return initLength;
	}

	public void setInitSize(String initSize){
		this.initSize = initSize;
		editor.putString("init_size", initSize);
	}

	public String getInitSize(){
		return initSize;
	}

	public void setDepth(int iteration){
		this.depth = iteration;
		editor.putInt("depth", iteration);
	}

	public int getDepth(){
		return depth;
	}

	public void setTriangleTip(boolean sharpTip){
		this.triangleTip = sharpTip;
		editor.putBoolean("sharp_tip", sharpTip);
	}

	public boolean isTriangleTip(){
		return triangleTip;
	}

	public void setSeed(String seed){
		this.seed = seed;
		editor.putString("seed", seed);
	}

	public String getSeed(){
		return seed;
	}

	public void setUseSeed(boolean useSeed){
		this.useSeed = useSeed;
		editor.putBoolean("use_seed", useSeed);
	}

	public boolean isUseSeed(){
		return useSeed;
	}
	
	public void save(){
		editor.apply();
	}
}
