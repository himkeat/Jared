package com.example.individualassignment;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ItemAndCart implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5425707150512225093L;
	String [] productList = {"Super Men Shampoo", "Super Women Shampoo", "Super Baby Shampoo", 
			"Super Face Wash", "Super Facial Soap", "Super Hand Soap", "Super Hand Wash",
			"Super Sanitizer", "Super Toothpaste", "Super Body Shampoo"};
	int [] priceList = {25, 35, 40, 20, 15, 10, 15, 10, 5, 20};
	String [] productsCode = {"P001", "P002", "P003", "P004", "P005", "P006",
			"P007", "P008", "P009", "P010"};
	boolean isMember;
	Map<String, Integer> productCodeAndPrice;
	Map<String, Integer> cartList;
	Map<String, String> productAndCode;
	
	ItemAndCart() {
		
		cartList = new HashMap<String, Integer>();
    	productCodeAndPrice = new HashMap<String, Integer>();
    	productAndCode = new HashMap<String, String>();
    	for(int i=0; i<productList.length; i++){
    		productCodeAndPrice.put(productsCode[i], priceList[i]); //initializing product and price map
    		productAndCode.put(productList[i], productsCode[i]); //initializing product's code and product map
		}
	}
	
	public boolean getisMember() {
		return isMember;
	}

	public void setIsMember(boolean memberOrNot) {
		this.isMember = memberOrNot;
	}

	public String[] getProductList() {
		return productList;
	}

	public void setProductList(String[] productList) {
		this.productList = productList;
	}

	public int[] getPriceList() {
		return priceList;
	}

	public void setPriceList(int[] priceList) {
		this.priceList = priceList;
	}

	public String[] getProductsCode() {
		return productsCode;
	}

	public void setProductsCode(String[] productsCode) {
		this.productsCode = productsCode;
	}

	public Map<String, String> getProductAndCode() {
		return productAndCode;
	}

	public void setProductAndCode(Map<String, String> productAndCode) {
		this.productAndCode = productAndCode;
	}

	public Map<String, Integer> getProductCodeAndPrice() {
		return productCodeAndPrice;
	}
	public void setProductCodeAndPrice(Map<String, Integer> productAndPrice) {
		this.productCodeAndPrice = productAndPrice;
	}
	public Map<String, Integer> getCartList() {
		return cartList;
	}
	public void setCartList(Map<String, Integer> cartList) {
		this.cartList = cartList;
	}
	
    public void cartListPut(String key, int value){
    	cartList.put(key, value);
    }
    
    public void productAndPricePut(String key, int value){
    	productCodeAndPrice.put(key, value);
    }
}
