package com.smarttiger.myview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {

	private EditText inputEdit;
	private Button inputButton;
	
	private RelativeLayout viewLayout;
	private Bezier1 bezier1;
	private Bezier2 bezier2;
	private BezierHeart bezierHeart;
	private RulerView rulerView;
	private FractalView fractalView;
	private ProgressBar progressBar;
	private ProgressBarView progressBar1;
	private CircleProgressImageView progressBar2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initView();
		
	}
	
	private boolean bezier2Mode = false;
	private void initView() {
		inputEdit = (EditText) findViewById(R.id.input_edit);
		inputButton = (Button) findViewById(R.id.input_button);
		viewLayout = (RelativeLayout) findViewById(R.id.view_layout);
		bezier1 = (Bezier1) findViewById(R.id.bezier1);
		bezier2 = (Bezier2) findViewById(R.id.bezier2);
		bezierHeart = (BezierHeart) findViewById(R.id.bezierHeart);
		rulerView = (RulerView) findViewById(R.id.rulerView);
		fractalView = (FractalView) findViewById(R.id.fractalView);
		progressBar = (ProgressBar) findViewById(R.id.progress);
		progressBar1 = (ProgressBarView) findViewById(R.id.progress1);
		progressBar2 = (CircleProgressImageView) findViewById(R.id.progress2);
		
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
				else if(text.equals("ruler")) {
					showView(rulerView);
				}
				else if(text.equals("fractal")) {
					showView(fractalView);
				}
				else if(text.equals("progress")) {
					showView(progressBar);
				}
				else if(text.equals("progress1")) {
					showView(progressBar1);
				}
				else if(text.equals("progress2")) {
					showView(progressBar2);
					progressBar2.setShouldShowProgress(true);
					new ProgressThread().start();
				}
			}
		});
	}
	
	private void showView(View view) {
		for (int i = 0; i < viewLayout.getChildCount(); i++) {
			viewLayout.getChildAt(i).setVisibility(View.GONE);
		}
		view.setVisibility(View.VISIBLE);
	}
	
	private void showLeafLoading()
	{
		Intent intent = new Intent(this, LeafLoadingActivity.class);
		startActivity(intent);
	}
	
	
	// The action to start AddAccountActivity from ContactsDAV.
    public static final String ACTION_ADD_ACCOUNT = "davdroid.intent.action.ADD_ACCOUNT";
    // ContactsDAV at.bitfire.davdroid.Constants#EXTRA_ACCOUNT_TYPE.
    public static final String EXTRA_ACCOUNT_TYPE = "extra_account_type";
    // The account type of iCloud, Sync by CardDAV.
    public static final String ICLOUD_ACCOUNT_TYPE = "com.zui.davdroid.account.icloud";
	private void showSiderBar()
	{
		Intent intent = new Intent(this, SiderBarActivity.class);
		startActivity(intent);
//		Intent intent = new Intent(ACTION_ADD_ACCOUNT);
//        intent.putExtra(EXTRA_ACCOUNT_TYPE, ICLOUD_ACCOUNT_TYPE);
//        startActivity(intent);
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
	}
	
	
	class ProgressThread extends Thread{
        @Override
        public void run() {
            for(int i = 0; i < 60; i++){
            	Message msg = new Message();
            	msg.what = 0;
            	msg.arg1 = i;
                handler.sendMessage(msg);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        	Message msg = new Message();
        	msg.what = 1;
            handler.sendMessage(msg);
        }
    };
    
	private Handler handler = new Handler(){
	    @Override
	    public void handleMessage(Message msg) {
	        switch(msg.what){
	            case 0:
	            	int progress = msg.arg1;
	            	progressBar2.setProgress(progress);
	                break;
	            case 1:
	            	progressBar2.setShouldRippling(true);
	                break;
	            }
	        }
	};
	
}
