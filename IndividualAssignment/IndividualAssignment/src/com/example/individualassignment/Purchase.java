package com.example.individualassignment;

import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class Purchase extends Activity {
	
	protected static final long MAX_CLICK_DURATION = 1000;
	private final int MAX_CLICK_DISTANCE = 50;
	private long pressStartTime;
	private float pressedX, pressedY;
	
	String cardNumber, addedToCartMessage, amount, pName, pCode; 	
	ItemAndCart c = new ItemAndCart();
	Member member = new Member();
	Map<String, Integer> cart = c.getCartList();					
	
	//XML View components
	OnTouchListener hover;									
	OnClickListener addToCart;								
	ScrollView parentScrollView;
	TextView label, name, productName, addedToCart, productCode;	//Labels for the info
	LinearLayout productNameLayout, pricesLayout, subParent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_purchase);
		String temp = this.getIntent().getStringExtra("cardNumber");
		cardNumber = temp;
		
		/*
		 * Checking if the user is a member
		 * then perform action according to the result
		 */
		label = (TextView) findViewById(R.id.memberLabel);
		name = (TextView) findViewById(R.id.memberName);
		
		if (temp == null){
			label.setText(getString(R.string.welcome));
			name.setText(null);
			c.setIsMember(false);
		}
		else {
			c.setIsMember(true);
			label.setText(getString(R.string.welcome));
			name.setText(member.getMemberList().get(Integer.parseInt(cardNumber)));
		}
		
		initialize();
		
		addToCart = new OnClickListener(){
			/*
			 * Event handler for adding product to cart list
			 * a dialog box will be shown to user asking for an input of amount
			 * null value is not accepted
			 */
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LinearLayout productRow = (LinearLayout) v;
				int childCount = productRow.getChildCount();
				for(int i=0; i < childCount; i++){
					View temp = productRow.getChildAt(i);
					if (temp instanceof LinearLayout){
						productName = (TextView)((LinearLayout)temp).getChildAt(0);
						pName = productName.getText().toString();
						addedToCart = (TextView)((LinearLayout)temp).getChildAt(1);
					}
				}

				AlertDialog.Builder builder = new AlertDialog.Builder(Purchase.this);
				final EditText input = new EditText(Purchase.this);
				input.setInputType(InputType.TYPE_CLASS_NUMBER);
				input.setHint(Purchase.this.getResources().getString(R.string.amount));
				
				builder.setTitle(Purchase.this.getResources().getString(R.string.add).toString());
				builder.setMessage("Adding " + pName + " to cart. Amount: ");
				builder.setView(input);
				
				builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if(input.getText().toString().isEmpty()){
							amount = "0";
							addedToCartMessage = Purchase.this.getResources().getString(R.string.productNotAdded).toString();
						}
						else{
							amount =  input.getText().toString();
							addedToCartMessage = Purchase.this.getResources().getString(R.string.productIsAdded).toString();
							
							//check if the item is in the cart
							if(cart.containsKey(pName)){
								cart.put(pName, cart.get(pName) + Integer.parseInt(amount));
							}
							else{
								cart.put(pName, Integer.parseInt(amount));
							}
							addedToCart.setVisibility(View.VISIBLE);
						}
						
						
						Toast.makeText(getApplicationContext(), 
								addedToCartMessage, Toast.LENGTH_SHORT).show();
						
					}
				});
				
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.cancel();
					}
				});
				builder.show();
			}
			
		};
		parentScrollView = (ScrollView) findViewById(R.id.parent);
		hover = new OnTouchListener(){
			/*
			 * Highlight the item that is currently touching by the user
			 * the highlight will disappear when user lifted up his finger
			 * or when user is scrolling the screen
			 */
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				LinearLayout temp = (LinearLayout) v;
				switch (event.getAction()){
				case MotionEvent.ACTION_DOWN:
					temp.setBackgroundColor(Color.LTGRAY);
					pressStartTime = System.currentTimeMillis();                
		            pressedX = event.getX();
		            pressedY = event.getY();
					break;
				case MotionEvent.ACTION_UP:
					long pressDuration = System.currentTimeMillis() - pressStartTime;
					temp.setBackground(getResources().getDrawable(R.drawable.layout_border));
					if (pressDuration < MAX_CLICK_DURATION && distance(pressedX, pressedY, event.getX(), event.getY()) < MAX_CLICK_DISTANCE) {
		                // Click event has occurred
						v.performClick();
						break;
		            }
				default:
					temp.setBackground(getResources().getDrawable(R.drawable.layout_border));
					break;
				}
				return true;
			}
		};
		subParent = (LinearLayout) findViewById(R.id.subparent);
		int subParentChild = subParent.getChildCount();
		/*
		 * Assigning listener onto each of the item
		 */
		for(int i=0; i < subParentChild; i++){
			String rowId = "row" + i;
			if(subParent.getChildAt(i).getId() == getResources().getIdentifier(rowId, "id", getPackageName())){
				LinearLayout child = (LinearLayout) subParent.getChildAt(i);
				child.setOnTouchListener(hover);
				child.setOnClickListener(addToCart);
			}
		}
	
	}
	
	@Override
	public void onBackPressed() {
		/*
		 * Alert user to double confirm that he wants to discard the cart list if he has selected at least 1 item
		 * Logout and back to the login screen otherwise
		 */
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(Purchase.this);
		builder.setTitle(R.string.loggingOut);
		builder.setMessage(R.string.discard);
		builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				setResult(Activity.RESULT_OK, null);
				finish();
			}
		});
		
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
		
		if(cart.isEmpty()){
			setResult(Activity.RESULT_OK);
			Purchase.this.finish();
		}
		else{
			builder.show();
		}
	}



	private void initialize(){
		/*
		 * initialize all those layouts, views
		 * displaying all the items
		 */
		
		for(int i=0; i<c.productList.length; i++){ //initializing the map
			c.productCodeAndPrice.put(c.productsCode[i], c.priceList[i]);
		}
		
		for(int i=0; i<10; i++){ //initialize product row start from row number
			String number = Integer.toString(i+1);
			String pNameId = "pName" + number, priceId = "price" + number, pCodeId = "pCode" + number;
			int nameResId = getResources().getIdentifier(pNameId, "id", getPackageName());
			int priceResId = getResources().getIdentifier(priceId, "id", getPackageName());
			int pCodeResId = getResources().getIdentifier(pCodeId, "id", getPackageName());
			TextView productName = (TextView) findViewById(nameResId);
			productName.setText(c.productList[i]);
			TextView priceAmmount = (TextView) findViewById(priceResId);
			priceAmmount.setText(Purchase.this.getResources().getString(R.string.rm) + Integer.toString(c.priceList[i]));
			TextView pCode = (TextView) findViewById(pCodeResId);
			LinearLayout.LayoutParams marginForCode = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			LinearLayout.LayoutParams marginForPrice = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			pCode.setText(c.productsCode[i]);
			marginForCode.setMargins(5, 1, 35, 0);
			marginForPrice.setMargins(0, 0, 15, 0);
			pCode.setLayoutParams(marginForCode);
			
			//check if the product is already in the cart list
			String pBtnId = "pBtn" + (i+1);
			int pBtnResId = getResources().getIdentifier(pBtnId, "id", getPackageName());
			TextView pBtnLayout = (TextView) findViewById(pBtnResId);
			if(cart.get(c.productList[i]) != null){
				pBtnLayout.setVisibility(View.VISIBLE);		
			}
			else{
				pBtnLayout.setVisibility(View.GONE);
			}
		}
	}
	
	//distance and pxToDp method
	//purpose of detecting whether the user is scrolling
	//or clicking/tapping on the screen
	private float distance(float x1, float y1, float x2, float y2) {
	    float dx = x1 - x2;
	    float dy = y1 - y2;
	    float distanceInPx = (float) Math.sqrt(dx * dx + dy * dy);
	    return pxToDp(distanceInPx);
	}

	private float pxToDp(float px) {
	    return px / getResources().getDisplayMetrics().density;
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		/* 
		 * update cart list when 'back' button
		 * is pressed on confirm order activity
		 * in the case of any product(s) is removed from the cart
		 */
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
		if(requestCode == 1){
			if(resultCode == RESULT_OK){
				c = (ItemAndCart) data.getSerializableExtra("updatedList");
				cart = c.getCartList();
				initialize();
			}
		}
		else{
			Toast.makeText(getApplicationContext(), "Failed to update cart list", 
					Toast.LENGTH_SHORT).show();;
		}
		
	}
	
	public void compute(View v){
		/*
		 * onclick listener assigned to XML component
		 * to start computing those selected items
		 */
		c.setCartList(cart);
		Bundle b = new Bundle();
		b.putSerializable("selectedList", c);
		Intent intent = new Intent(this, ConfirmOrder.class);
		intent.putExtras(b);
		
		startActivityForResult(intent, 1);
	}

}
