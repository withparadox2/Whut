package com.withparadox2.whut.dao;

import java.io.Serializable;

public class SaveTwoDimArray implements Serializable {
	String[][] pingJiaoUrls;
	String[][] customArray;
	public static final String NAME = "saveState";
	private static final long serialVersionUID = 1L;
	private static SaveTwoDimArray singletonObject;

	public static SaveTwoDimArray getSingletonObject() {
		if (singletonObject == null) {
			singletonObject = new SaveTwoDimArray();
		}
		return singletonObject;
	}

	public void setPingJiaoUrls(String[][] str) {
		this.pingJiaoUrls = str;
	}

	public String[][] getPingJiaoUrls() {
		return pingJiaoUrls;
	}

	public void setCustomArray(String[][] str) {
		this.customArray = str;
	}

	public String[][] getCustomArray() {
		return customArray;
	}
}