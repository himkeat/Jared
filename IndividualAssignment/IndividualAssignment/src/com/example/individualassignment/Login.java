package com.example.individualassignment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {
	private final int REQUEST_CODE = 1;
	OnClickListener submit, skip;
	Button submitButton, skipButton;
	EditText memberNumber;
	Member member;
	ItemAndCart c;
	String message;
	
	/*
	 * Login activity
	 * Buttons: submit (for member only), skip(for non member only)
	 */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		member = new Member();
		c = new ItemAndCart();
		
		submit = new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent submission = new Intent(Login.this, Purchase.class);
				
				if(memberNumber.getText().toString().isEmpty()){
					message = "Please enter member card number or skip";
					Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
				}
				else{
					int number = Integer.parseInt(memberNumber.getText().toString());
					if(member.getMemberNumber().contains(number)){
						submission.putExtra("cardNumber", Integer.toString(number));
						startActivityForResult(submission, REQUEST_CODE);
					}
					else{
						message = "Invalid member number";
						Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
					}
				}
			}
			
		};
		
		skip = new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent submission = new Intent(Login.this, Purchase.class);
				startActivity(submission);
			}	
		};
		
		submitButton = (Button) findViewById(R.id.submit);
		skipButton = (Button) findViewById(R.id.skip);
		memberNumber = (EditText) findViewById(R.id.cardNumber);
		submitButton.setOnClickListener(submit);
		skipButton.setOnClickListener(skip);
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 1){
			
			if(resultCode == RESULT_OK){
				finish();
				startActivity(getIntent());
				Toast.makeText(Login.this, this.getResources().getString(R.string.loggedOut).toString(), Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(Login.this, this.getResources().getString(R.string.failedToLogout).toString(), Toast.LENGTH_SHORT).show();
			}
			
		}
	}
	
	
}
