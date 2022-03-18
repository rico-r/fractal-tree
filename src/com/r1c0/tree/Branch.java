package com.r1c0.tree;

import android.graphics.Path;
import java.util.LinkedList;
import java.util.Random;

public class Branch{
	private Branch child1, child2;
	private final float absX, absY;
	private final double angle;
	private final float len, size;

	public Branch(float absX, float absY, double angle, float len, float size){
		this.absX = absX;
		this.absY = absY;
		this.angle = angle;
		this.len = len;
		this.size = size;
	}

	public int addBranch(LinkedList<Branch> dst, ParsedConfig cfg, Random random){
		float cos = (float) Math.cos(angle);
		float sin = (float) Math.sin(angle);
		float endX = absX + cos * len;
		float endY = absY + sin * len;		
		dst.add(child1 = new Branch(
			endX, endY,
			angle + cfg.angleAdd + random.nextDouble() * cfg.angleMul,
			len * (cfg.relLengthAdd + cfg.relLengthMul * random.nextFloat()),
			size * (cfg.relSizeAdd + cfg.relSizeMul * random.nextFloat())));
		dst.add(child2 = new Branch(
			endX, endY,
			angle - cfg.angleAdd - random.nextDouble() * cfg.angleMul,
			len * (cfg.relLengthAdd + cfg.relLengthMul * random.nextFloat()),
			size * (cfg.relSizeAdd + cfg.relSizeMul * random.nextFloat())));
		return 2;
	}

	public void drawToPath(ParsedConfig cfg, Path path){
		float cos = (float) Math.cos(angle);
		float sin = (float) Math.sin(angle);
		float endX = absX + cos * len;
		float endY = absY + sin * len;
		synchronized(path){
			if(child1 != null && child2 != null){
				path.moveTo(absX - sin * size, absY + cos * size);
				path.lineTo(absX + sin * size, absY - cos * size);
				float size_max = Math.max(child1.size, child2.size);
				path.lineTo(endX + sin * size_max, endY - cos * size_max);
				path.lineTo(endX - sin * size_max, endY + cos * size_max);
				path.close();
			}else{
				path.moveTo(absX - sin * size, absY + cos * size);
				path.lineTo(absX + sin * size, absY - cos * size);
				if(cfg.triangleTip){
					path.lineTo(endX, endY);
				}else{
					path.lineTo(endX + sin * size, endY - cos * size);
					path.lineTo(endX - sin * size, endY + cos * size);
				}
				path.close();
			}
		}
	}
}
