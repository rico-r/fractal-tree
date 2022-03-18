package com.r1c0.tree;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import com.r1c0.view.WideCanvasView;
import java.util.LinkedList;
import java.util.Random;

public class MyCanvasView
	extends WideCanvasView
	implements Runnable{

	private final MainActivity main;
	private final Path path;
	private final Paint paint;
	private final Random random;
	private final ParsedConfig cfg;
	private boolean generating;

	public MyCanvasView(MainActivity main){
		super(main, null);
		this.main = main;
		maxZoom = 1000;
		minZoom = 1;
		path = new Path();
		random = new Random();
		paint = new Paint();
		cfg = new ParsedConfig();
		paint.setColor(0xff000000);
		paint.setStyle(Paint.Style.FILL);
		path.setFillType(Path.FillType.WINDING);
	}

	private int iterate(LinkedList<Branch> list, int count){
		int total = 0;
		for(int i=0; i<count && generating; i++){
			Branch branch = list.removeFirst();
			total += branch.addBranch(list, cfg, random);
			branch.drawToPath(cfg, path);
		}
		return total;
	}

	private void iterateFinal(LinkedList<Branch> list){
		while(!list.isEmpty() && generating){
			list.removeFirst().drawToPath(cfg, path);
		}
	}
	
	public ParsedConfig getConfig(){
		return cfg;
	}

	public boolean isGenerating(){
		return generating;
	}

	public void generate(){
		generating = true;
		path.reset();
		random.setSeed(cfg.seed);
		new Thread(this).start();
	}
	
	public void stop(){
		generating = false;
	}

	public void run(){
		if(generating){
			LinkedList<Branch> list = new LinkedList<Branch>();
			list.add(new Branch(vw/2, vh*0.5f, -Math.PI/2, cfg.initLength, cfg.initSize));
			int count = 1;
			for(int i=0; i<cfg.iteration; i++){
				count = iterate(list, count);
			}
			iterateFinal(list);
			generating = false;
			post(this);
		}else{
			main.button.setText("generate");
		}
	}

	public void setAntiAlias(boolean antiAlias){
		paint.setAntiAlias(antiAlias);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom){
		if(path.isEmpty()){
			super.onLayout(changed, left, top, right, bottom);
			minX = -vw / 2;
			minY = -vh / 2;
			maxX = vw * 1.5f;
			maxY = vh * 1.5f;
		}
	}

	@Override
	public void onClick(float x, float y){
		if(main.settings.getAnimation() == null){
			main.settings.post(main);
		}
	}

	@Override
	protected void onDraw(Canvas cnv){
		super.onDraw(cnv);
		cnv.drawColor(-1);
		synchronized(path){
			cnv.drawPath(path, paint);
		}
		invalidate();
	}
}
