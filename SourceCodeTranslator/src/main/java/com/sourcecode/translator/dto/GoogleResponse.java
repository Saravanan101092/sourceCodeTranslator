package com.sourcecode.translator.dto;

import java.io.Serializable;
import java.util.ArrayList;

public class GoogleResponse implements Serializable{

	ArrayList<ArrayList<ArrayList<Object>>> object = new ArrayList<ArrayList<ArrayList<Object>>>();
	public ArrayList<ArrayList<ArrayList<Object>>> getObject() {
		return object;
	}
	public void setObject(ArrayList<ArrayList<ArrayList<Object>>> object) {
		this.object = object;
	}

}
