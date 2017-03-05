package com.example.individualassignment;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class ConfirmOrder extends Activity {
	ItemAndCart c;
	
	double total=0, discountedTotal = 0;
	String pName, pQty, pCode; 								//p stands for product 'eg pName = product name'
	
	int amount, selectedProductTotalPrice;
	
	Map<String, Integer> cartList;
	Map<String, String> productAndCode;

	LinearLayout selectedItems, col1, col2, productRow;
	TextView pQtyTxtView, pNameTxtView, pPriceTxtView, subtotalTxtView; 
	TextView totalPriceTxtView, afterDiscountTxtView, selectedItemLabel;
	Button removeBtn, purchaseBtn;
	LinearLayout.LayoutParams wrapContent = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		/*
		 * retrieving important values from previous activity
		 * prior to perform any action
		 */
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
		setContentView(R.layout.activity_confirm_order);	
		selectedItems = (LinearLayout) findViewById(R.id.selectedItems);
		Bundle getCart = getIntent().getExtras();
		c = (ItemAndCart) getCart.getSerializable("selectedList");
		cartList = c.getCartList();
		productAndCode = c.getProductAndCode();
		createCartList();
	
	}

	OnClickListener removeFromCart = new OnClickListener(){
		/*
		 * remove item from cart according
		 * to the amount user entered
		 * the amount is limited to no more than 5 digits
		 * 
		 * user will not able to remove the item from the cart list
		 * if he entered an invalid amount 
		 * eg: amount entered is greater than the actual amount of the item
		 */
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			final LinearLayout col2 = (LinearLayout) v.getParent();
			LinearLayout productRow = (LinearLayout) col2.getParent();
			col1 = (LinearLayout) productRow.getChildAt(0);
			pNameTxtView = (TextView) col1.getChildAt(0);
			pName = pNameTxtView.getText().toString();
			pCode = productAndCode.get(pName);
			
			final EditText input = new EditText(ConfirmOrder.this);
			InputFilter [] filter = new InputFilter[1];
			filter[0] = new InputFilter.LengthFilter(10000);
			input.setInputType(InputType.TYPE_CLASS_NUMBER);
			input.setFilters(filter);

			AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmOrder.this);
			builder.setTitle("Purchasing");
			builder.setMessage("Remove " + pName + " from cart. Amount: ");
			builder.setView(input);
			
			builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
					if(input.getText().toString().isEmpty()){
						Toast.makeText(ConfirmOrder.this, R.string.enterAmount, Toast.LENGTH_SHORT).show();
						dialog.cancel();
					}
					else{
						//check if the item is in the cart
						amount =  Integer.parseInt(input.getText().toString());
						if(cartList.containsKey(pName)){
							if(amount > cartList.get(pName)){ 
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.wrongAmount).toString(), Toast.LENGTH_SHORT).show();
							}
							else{ //successfully remove product in terms of amount from cart
								cartList.put(pName, cartList.get(pName) - amount);
								if(cartList.get(pName) == 0){
									cartList.remove(pName);
									Toast.makeText(getApplicationContext(), 
											getResources().getString(R.string.removed).toString(), Toast.LENGTH_SHORT).show();
									c.setCartList(cartList);
									clearLayout();
									createCartList();
								}
								else{
									computeTotalPrice();
									if(c.getisMember()){
										afterDiscountTxtView = (TextView) findViewById(R.id.afterDiscount);
										afterDiscountTxtView.setText(ConfirmOrder.this.getResources().getString(R.string.afterDiscount).toString() + Double.toString(discountedTotal));
									}
									selectedProductTotalPrice = c.getProductCodeAndPrice().get(pCode) * cartList.get(pName);
									pQtyTxtView = (TextView) col2.getChildAt(0);
									pQtyTxtView.setText(getResources().getString(R.string.qty).toString() + cartList.get(pName).toString());
	
									pPriceTxtView = (TextView) col2.getChildAt(1);
									pPriceTxtView.setText(getResources().getString(R.string.rm).toString() + Integer.toString(selectedProductTotalPrice));
	
									totalPriceTxtView = (TextView) findViewById(R.id.totalPriceLabel);
									totalPriceTxtView.setText(getResources().getString(R.string.totalPrice).toString() + Double.toString(total));
									c.setCartList(cartList); 
								}
							}
						}	
					}
				}
			});
			
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.cancel();
				}
			});
			
			builder.show().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);	
			
		}
	};
	
	OnClickListener buy = new OnClickListener(){
		/*
		 * Check out the cart list
		 * 
		 */
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(cartList.isEmpty()){
				Toast.makeText(getApplicationContext(), 
						getResources().getString(R.string.emptyCartList).toString(), Toast.LENGTH_SHORT).show();
			}
			else{
				Toast.makeText(getApplicationContext(), 
						getResources().getString(R.string.success).toString(), Toast.LENGTH_SHORT).show();
			}
		}
		
	};
	
	public void clearLayout(){
		/*
		 * reset the layout as the items is added dynamically
		 * before updating the selected item list
		 */
		int childCount = selectedItems.getChildCount();
		selectedItems.removeViews(1, childCount-1);
		total = 0;
		
	}
	
	public void createCartList(){
		/*
		 * display item to the user on the screen
		 * add layouts/views dynamically 
		 * 
		 */
		cartList = c.getCartList();
		for(HashMap.Entry<String, Integer> entry : cartList.entrySet()){
			int dpMargin = (int) getResources().getDimension(R.dimen.productRowMargin);
			productRow = new LinearLayout(this);
			col1 = new LinearLayout(this); 
			col2 = new LinearLayout(this);
			
			pNameTxtView = new TextView(ConfirmOrder.this);
			pQtyTxtView = new TextView(ConfirmOrder.this);
			pPriceTxtView = new TextView(ConfirmOrder.this);
			subtotalTxtView = new TextView(ConfirmOrder.this);
			removeBtn = new Button(this);
			
			LinearLayout.LayoutParams marginForCol1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,1);
			LinearLayout.LayoutParams marginForCol2 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1);
			LinearLayout.LayoutParams marginForProductRow = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			LinearLayout.LayoutParams marginForBtn = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			marginForBtn.setMargins(0, 0, 0, 5);
			marginForCol1.setMargins(5, 0, 35, 0);
			
			marginForProductRow.setMargins(dpMargin, dpMargin, dpMargin, dpMargin);
			
			pCode = productAndCode.get(entry.getKey());
			selectedProductTotalPrice = c.getProductCodeAndPrice().get(pCode) * entry.getValue();
			
			pNameTxtView.setText(entry.getKey());
			pNameTxtView.setMinWidth((int) getResources().getDimension(R.dimen.minWidth));
			pNameTxtView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			
			pQtyTxtView.setText(getResources().getString(R.string.qty).toString() + entry.getValue().toString());
			pQtyTxtView.setLayoutParams(wrapContent);
			
			subtotalTxtView.setText(getResources().getString(R.string.subtotal));
			subtotalTxtView.setGravity(Gravity.END);
			
			pPriceTxtView.setText(getResources().getString(R.string.rm).toString() + Integer.toString(selectedProductTotalPrice));
			pPriceTxtView.setLayoutParams(wrapContent);
			
			removeBtn.setText(R.string.removeProduct);
			removeBtn.setTextSize(12);
			
			removeBtn.setLayoutParams(marginForBtn);
			removeBtn.setBackground(getResources().getDrawable(R.drawable.button_layout));
			removeBtn.setOnClickListener(removeFromCart);
			productRow.setOrientation(LinearLayout.HORIZONTAL);
			productRow.addView(col1); 
			productRow.addView(col2);
			productRow.setLayoutParams(marginForProductRow);
			
			col1.setOrientation(LinearLayout.VERTICAL);
			col1.addView(pNameTxtView); col1.addView(subtotalTxtView);
			col1.setLayoutParams(marginForCol1);
			col1.setGravity(Gravity.START);
			
			col2.setOrientation(LinearLayout.VERTICAL);
			col2.addView(pQtyTxtView); col2.addView(pPriceTxtView); col2.addView(removeBtn);
			col2.setPadding(20, 0, 20, 0);
			col2.setLayoutParams(marginForCol2);
			col2.setGravity(Gravity.CENTER);
			
			productRow.setBackground(getResources().getDrawable(R.drawable.layout_border));
			selectedItems.addView(productRow);

		}
		computeTotalPrice();
		purchaseBtn = new Button(ConfirmOrder.this);
		LinearLayout.LayoutParams gravityEnd = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		gravityEnd.gravity = Gravity.END;
		gravityEnd.setMargins(20, 10, 20, 10);
		purchaseBtn.setText(R.string.checkOut);
		purchaseBtn.setLayoutParams(wrapContent);
		purchaseBtn.setLayoutParams(gravityEnd);
		purchaseBtn.setOnClickListener(buy);
		
		gravityEnd.gravity = Gravity.END;
		totalPriceTxtView = new TextView(ConfirmOrder.this);
		totalPriceTxtView.setText(getResources().getString(R.string.totalPrice).toString() + Double.toString(total));
		totalPriceTxtView.setLayoutParams(gravityEnd);
		totalPriceTxtView.setId(R.id.totalPriceLabel);
		
		if(c.getisMember()){
			afterDiscountTxtView = new TextView(ConfirmOrder.this);
			afterDiscountTxtView.setText(this.getResources().getString(R.string.afterDiscount).toString() + Double.toString(discountedTotal));
			afterDiscountTxtView.setLayoutParams(gravityEnd);
			afterDiscountTxtView.setId(R.id.afterDiscount);
			
			selectedItems.addView(totalPriceTxtView);
			selectedItems.addView(afterDiscountTxtView);
			selectedItems.addView(purchaseBtn);
			
		}
		else{
			selectedItems.addView(totalPriceTxtView);
			selectedItems.addView(purchaseBtn);
		}
		
	}
	
	public void computeTotalPrice(){
		/*
		 * calculate total price
		 * apply discount for member
		 * no  discount for non member 
		 */
		total = 0;
		for(HashMap.Entry<String, Integer> entry : cartList.entrySet()){
			pCode = productAndCode.get(entry.getKey());
			selectedProductTotalPrice = c.getProductCodeAndPrice().get(pCode) * entry.getValue();
			total += selectedProductTotalPrice;
		}
		if(c.isMember){
			discountedTotal = total * 0.9;
		}
	}
	
	@Override
	public void onBackPressed() {
		/*
		 * return back to previous activity
		 * with updated selected item list
		 */
		// TODO Auto-generated method stub
		Intent sendBack = new Intent();
		Bundle b = new Bundle();
		b.putSerializable("updatedList", c);
		sendBack.putExtras(b);
		setResult(Activity.RESULT_OK, sendBack);
		super.onBackPressed();
	}
	
	
	

}
