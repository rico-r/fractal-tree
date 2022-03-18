package com.r1c0.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public abstract class WideCanvasView extends View{
	private final ArrayList<Integer> pointers;
	private final Timer timer;
	private float sx, sy, lx, ly;
	private float lastPointerDistance;
	private boolean isZooming, isValidClick;
	private boolean isScrollOverriden;
	private mTimer task;
	private long lastTouchTime;
	private long lastDrawTime;
	private float friction;
	protected float vx, vy;
	protected float dx, dy;
	protected long clickTimeout = 200;
	protected float minZoom,maxZoom;
	protected float scale, vw, vh;
	protected float minX, minY, maxX, maxY;

	public WideCanvasView(Context ctx, AttributeSet attr){
		super(ctx, attr);
		pointers = new ArrayList<Integer>();
		lastDrawTime = System.currentTimeMillis();
		timer = new Timer();
		scale = 1;
		minZoom = 0.4f;
		maxZoom = 2f;
		setFriction(0.02f);
		maxX = 1000;
		maxY = 1000;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom){
		super.onLayout(changed, left, top, right, bottom);
		vw = right - left;
		vh = bottom - top;
	}

	@Override
	protected void onDraw(Canvas cnv){
		vx *= friction; vy *= friction;
		if(Math.hypot(vx, vy)<20){
			vx=0; vy=0;
		}
		long currentTime = System.currentTimeMillis();
		if(pointers.size()==0){
			float deltaTime = (currentTime - lastDrawTime) / 1000f;
			scroll(vx * deltaTime, vy * deltaTime);
		}
		lastDrawTime = currentTime;
		transformCanvas(cnv);
	}

	protected void transformCanvas(Canvas cnv){
		cnv.scale(scale, scale);
		cnv.translate(dx, dy);
	}

	protected void reverseTransformCanvas(Canvas cnv){
		cnv.translate(-dx, -dy);
		cnv.scale(1/scale, 1/scale);
	}

	private float getPointerDistance(MotionEvent e) {
		int index1 = e.findPointerIndex(pointers.get(0));
		int index2 = e.findPointerIndex(pointers.get(1));
		return (float) Math.hypot(e.getX(index1) - e.getX(index1), e.getY(index2) - e.getY(index2));
	}

	@Override
	public boolean onTouchEvent(MotionEvent e){
		float x=e.getX(), y=e.getY();
		long currentTime = e.getEventTime();
		switch(e.getAction()){
			case MotionEvent.ACTION_DOWN:
				isZooming = false;
				isValidClick = true;
				vx = 0; vy = 0;
				sx = x; sy = y;
				if(task!=null){
					task.cancel();
					task = null;
				}
				if(Math.hypot(x - lx, y - ly)<10){
					isZooming = (currentTime - lastTouchTime) < clickTimeout;
				}
				if(!isZooming){
					isScrollOverriden = onStartScroll(x, y);
				}
			case MotionEvent.ACTION_POINTER_DOWN:
				pointers.add(e.getPointerId(e.getActionIndex()));
				if(pointers.size()>1){
					isValidClick = false;
					lastPointerDistance = getPointerDistance(e);
				}
				break;

			case MotionEvent.ACTION_MOVE:
				if(pointers.size()>1){
					float pointerDistance = getPointerDistance(e);
					float S = 1 / scale;
					scale *= Math.exp((pointerDistance - lastPointerDistance) / 400);
					scale = Math.max(minZoom, Math.min(maxZoom, scale));
					S = (1 / scale - S) / 2;
					scroll(S * vw, S * vh);
					lastPointerDistance = pointerDistance;
				}else if(isZooming){
					float S = 1 / scale;
					scale *= Math.exp((y-ly)/200);
					scale = Math.max(minZoom, Math.min(maxZoom, scale));
					S = (1 / scale - S)/2;
					scroll(S * vw, S * vh);
				}else if(isScrollOverriden){
					isScrollOverriden = onScroll(x - sx, y - sy, x - lx, y - ly);
				}else{
					scroll((x - lx) / scale, (y - ly) / scale);
					if(currentTime != lastTouchTime){
						final float decay = 0.4f;
						final float multiplier = 1.25f;
						float constant = decay * multiplier * 1000.0f / (currentTime - lastTouchTime) / scale;
						vx = vx * (1 - decay) + (x - lx) * constant;
						vy = vy * (1 - decay) + (y - ly) * constant;
					}
				}
				isValidClick &= Math.hypot(x - sx, y - sy) < 20;
				break;

			case MotionEvent.ACTION_UP:
				if(!isZooming && isScrollOverriden){
					onScrollEnd();
				}
				if(isValidClick) timer.schedule(task = new mTimer(this), clickTimeout);
			case MotionEvent.ACTION_POINTER_UP:
				pointers.remove(new Integer(e.getPointerId(e.getActionIndex())));
				if(pointers.size()==1){
					int index = e.findPointerIndex(pointers.get(0));
					x = e.getX(index);
					y = e.getY(index);
				}else if(pointers.size()>1){
					lastPointerDistance = getPointerDistance(e);
				}
				break;
		}
		lx=x; ly=y;
		lastTouchTime = currentTime;
		return true;
	}

	private void scroll(float x, float y){
		if(vw/scale>maxX-minX){
			dx = (vw / scale - (maxX + minX)) / 2;
		}else{
			dx = Math.min(-minX, Math.max(vw/scale-maxX, dx+x));
		}

		if(vh/scale>maxY-minY){
			dy = (vh / scale - (maxY + minY)) / 2;
		}else{
			dy = Math.min(-minY, Math.max(vh/scale-maxY, dy+y));
		}
	}

	public void setFriction(float friction){
		this.friction = 1 - friction;
	}

	public float getFriction(){
		return friction;
	}

	public void onClick(float x, float y){}
	public boolean onStartScroll(float x, float y){return false;}
	public boolean onScroll(float tx, float ty, float Dx, float Dy){return false;}
	public void onScrollEnd(){}

	private static class mTimer extends TimerTask{
		WideCanvasView m;

		public mTimer(WideCanvasView m){
			this.m = m;
		}

		@Override
		public void run(){
			m.task = null;
			m.onClick(m.sx, m.sy);
		}
	}
}
