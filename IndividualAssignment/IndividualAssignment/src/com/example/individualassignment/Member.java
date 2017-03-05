package com.example.individualassignment;

import android.annotation.SuppressLint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Member {
	ArrayList<Integer> memberNumber = new ArrayList<Integer>();
	ArrayList<String> memberName = new ArrayList<String>();
	Map<Integer, String> memberList;
	@SuppressLint("UseSparseArrays")
	Member() {
		//default constructor
		memberList = new HashMap<Integer, String>();
		memberNumber.add(10001);
		memberNumber.add(20321);
		memberNumber.add(43009);
		memberNumber.add(14567);
		memberNumber.add(54321);
		
		memberName.add("Jason");
		memberName.add("Amy");
		memberName.add("Daniel");
		memberName.add("Susan");
		memberName.add("Raymond");
		for(int i = 0; i<memberNumber.size(); i++){
			memberList.put(memberNumber.get(i), memberName.get(i));
		}
		
	}
	
	public ArrayList<Integer> getMemberNumber() {
		return memberNumber;
	}
	
	public void setMemberNumber(ArrayList<Integer> memberNumber) {
		this.memberNumber = memberNumber;
	}
	
	public ArrayList<String> getMemberName() {
		return memberName;
	}
	
	public Map<Integer, String> getMemberList() {
		return memberList;
	}
	
	
}
