package com.waiki.daftarbelanja.common;

import java.util.ArrayList;

import com.waiki.daftarbelanja.obj.ItemObj;

import android.app.Application;

public class GlobalClass extends Application {

	private ArrayList<ItemObj> batchDeleteItem;
	
	public GlobalClass() {
		batchDeleteItem = new ArrayList<ItemObj>();
	}

	public ArrayList<ItemObj> getBatchDeleteItem() {
		return batchDeleteItem;
	}

	public void setBatchDeleteItem(ItemObj item) {
		batchDeleteItem.add(item);
	}
	
	public void resetBatchDeleteItem() {
		batchDeleteItem.clear();
	}

}
