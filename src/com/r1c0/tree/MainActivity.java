package com.r1c0.tree;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity
	extends Activity
	implements Runnable,
		Animation.AnimationListener,
		View.OnClickListener,
		CompoundButton.OnCheckedChangeListener{

	private Animation hideAnim, showAnim;
	private boolean settingsVisible;
	private Config cfg;
	View settings;
	MyCanvasView cv;
	Button button;
	EditText seed;
	EditText angle, angleDev;
	EditText initLength, initSize;
	EditText relLength, relLengthDev;
	EditText relSize, relSizeDev;
	CheckBox useSeed, sharpTip, antiAlias;
	Spinner iteration;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		cv = new MyCanvasView(this);
		setContentView(cv);

		ViewGroup root = (ViewGroup) cv.getParent();
		LayoutInflater.from(this).inflate(R.layout.settings, root);
		settings = root.getChildAt(1);
		hideAnim = AnimationUtils.loadAnimation(this, R.anim.hide_settings);
		showAnim = AnimationUtils.loadAnimation(this, R.anim.show_settings);
		hideAnim.setAnimationListener(this);
		showAnim.setAnimationListener(this);
		settings.startAnimation(showAnim);

		button = (Button) findViewById(R.id.button);
		seed = (EditText) findViewById(R.id.seed);
		useSeed = (CheckBox) findViewById(R.id.use_seed);
		sharpTip = (CheckBox) findViewById(R.id.sharp_tip);
		antiAlias = (CheckBox) findViewById(R.id.anti_alias);
		iteration = (Spinner) findViewById(R.id.iteration_count);
		initLength = (EditText) findViewById(R.id.initial_length);
		initSize = (EditText) findViewById(R.id.initial_size);
		angle = (EditText) findViewById(R.id.angle);
		angleDev = (EditText) findViewById(R.id.angle_deviation);
		relLength = (EditText) findViewById(R.id.relative_length);
		relLengthDev = (EditText) findViewById(R.id.relative_length_deviation);
		relSize = (EditText) findViewById(R.id.relative_size);
		relSizeDev = (EditText) findViewById(R.id.relative_size_deviation);

		String item[] = new String[17];
		for(int i = 0; i < item.length; i++){
			item[i] = String.valueOf(i);
		}
		iteration.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, item));

 		cfg = new Config(this, getSharedPreferences("config", MODE_PRIVATE));

		useSeed.setOnCheckedChangeListener(this);
		antiAlias.setOnCheckedChangeListener(this);
		button.setOnClickListener(this);
	}

	@Override
	public void onClick(View unused){
		String msg[] = {""};
		if(cv.isGenerating()){
			button.setText("Generate");
			cv.stop();
		}else{
			cfg.setTriangleTip(sharpTip.isChecked());
			cfg.setSeed(seed.getText().toString());
			cfg.setDepth(iteration.getSelectedItemPosition());
			cfg.setInitLength(initLength.getText().toString());
			cfg.setInitSize(initSize.getText().toString());
			cfg.setAngle(angle.getText().toString());
			cfg.setAngleDev(angleDev.getText().toString());
			cfg.setRelLength(relLength.getText().toString());
			cfg.setRelLengthDev(relLengthDev.getText().toString());
			cfg.setRelSize(relSize.getText().toString());
			cfg.setRelSizeDev(relSizeDev.getText().toString());

			cv.getConfig().parseConfig(cfg, msg);
			if(msg[0].length() != 0){
				Toast.makeText(this, msg[0], 0).show();
				return;
			}
			cfg.save();
			cv.generate();
			button.setText("Stop");
			if(!cfg.isUseSeed()){
				seed.setText(String.valueOf(cv.getConfig().seed));
			}
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton v, boolean checked){
		switch(v.getId()){
			case R.id.use_seed:
				seed.setEnabled(checked);
				cfg.setUseSeed(checked);
				break;

			case R.id.anti_alias:
				cv.setAntiAlias(checked);
				cfg.setAntiAlias(checked);
				break;
		}
	}

	@Override
	public void run(){
		if(settingsVisible){
			settings.startAnimation(hideAnim);
		}else{
			settings.setVisibility(View.VISIBLE);
			settings.startAnimation(showAnim);
		}
	}

	public void onAnimationEnd(Animation unused){
		if(settingsVisible){
			settings.setVisibility(View.INVISIBLE);
		}
		settingsVisible = !settingsVisible;
	}

	public void onAnimationStart(Animation p1){}
	public void onAnimationRepeat(Animation p1){}
}
