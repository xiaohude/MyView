package com.smarttiger.myview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	private EditText inputEdit;
	private Button inputButton;
	private Bezier1 bezier1;
	private Bezier2 bezier2;
	private BezierHeart bezierHeart;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initView();
		

//		showLeafLoading();
		showSiderBar();
	}
	
	private boolean bezier2Mode = false;
	private void initView() {
		inputEdit = (EditText) findViewById(R.id.input_edit);
		inputButton = (Button) findViewById(R.id.input_button);
		bezier1 = (Bezier1) findViewById(R.id.bezier1);
		bezier2 = (Bezier2) findViewById(R.id.bezier2);
		bezierHeart = (BezierHeart) findViewById(R.id.bezierHeart);
		
		inputButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String text = inputEdit.getText().toString();
				if(text.equals("1"))
					showView(bezier1);
				else if(text.equals("2")) {
					showView(bezier2);
					bezier2Mode = !bezier2Mode;
					bezier2.setMode(bezier2Mode);
				}
				else if(text.equals("heart")) {
					showView(bezierHeart);
					bezierHeart.changeCirculate();
				}
				else if(text.equals("leaf")) {
					showLeafLoading();
				}
				else if(text.equals("siderbar")) {
					showSiderBar();
				}
			}
		});
	}
	
	private void showView(View view) {
		bezier1.setVisibility(View.GONE);
		bezier2.setVisibility(View.GONE);
		bezierHeart.setVisibility(View.GONE);
		view.setVisibility(View.VISIBLE);
	}
	
	private void showLeafLoading()
	{
		Intent intent = new Intent(this, LeafLoadingActivity.class);
		startActivity(intent);
	}
	
	private void showSiderBar()
	{
		Intent intent = new Intent(this, SiderBarActivity.class);
		startActivity(intent);
	}
}
