package com.r1c0.tree;

import java.util.Random;

public class ParsedConfig
{
	boolean triangleTip;
	long seed;
	int iteration;
	double angleAdd, angleMul;
	float initLength, initSize;
	float relLengthAdd, relLengthMul;
	float relSizeAdd, relSizeMul;

	public void parseConfig(Config cfg, String[] msg){
		triangleTip = cfg.isTriangleTip();
		iteration = cfg.getDepth();
		initLength = parseFloat(cfg.getInitLength(), 50, "invaild initial length;", msg);
		initSize = parseFloat(cfg.getInitSize(), 7, "invaild initial size;", msg);
		if(cfg.isUseSeed()){
			seed = parseLong(cfg.getSeed(), 0, "Invalid seed;", msg);
		}else{
			seed = new Random().nextLong();
		}
		{
			double val = Math.PI * parseDouble(cfg.getAngle(), 35, "invaild angle;", msg) / 180;
			double dev = Math.PI * parseDouble(cfg.getAngleDev(), 25, "invaild angle_deviation;", msg) / 180;
			angleAdd = val - dev;
			angleMul = 2 * dev;
		}{
			float rel = parseFloat(cfg.getRelLength(), 0.725f, "invaild rel_length;", msg);
			float dev = parseFloat(cfg.getRelLengthDev(), 0.075f, "invaild rel_length_deviation;", msg);
			relLengthAdd = rel - dev;
			relLengthMul = dev * 2;
		}{
			float rel = parseFloat(cfg.getRelSize(), 0.75f, "invaild rel_size;", msg);
			float dev = parseFloat(cfg.getRelSizeDev(), 0.01f, "invaild rel_size_deviation;", msg);
			relSizeAdd = rel - dev;
			relSizeMul = dev * 2;
		}
	}

	private double parseDouble(String str, double def, String err, String[]msg){
		if(str.length() == 0) return def;
		try{
			return Double.parseDouble(str);
		}catch(NumberFormatException e){
			msg[0] = msg[0].concat(err);
		}
		return def;
	}

	private float parseFloat(String str, float def, String err, String[]msg){
		if(str.length() == 0) return def;
		try{
			return Float.parseFloat(str);
		}catch(NumberFormatException e){
			msg[0] = msg[0].concat(err);
		}
		return def;
	}

	private long parseLong(String str, long def, String err, String[]msg){
		if(str.length() == 0) return def;
		try{
			return Long.parseLong(str);
		}catch(NumberFormatException e){
			msg[0] = msg[0].concat(err);
		}
		return def;
	}
}
