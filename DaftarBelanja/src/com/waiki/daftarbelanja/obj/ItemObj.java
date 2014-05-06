package com.waiki.daftarbelanja.obj;

import java.util.Date;

public class ItemObj {

	private int id;
	private String name;
	private int qty;
	private int price;
	private Date itemDate;

	public ItemObj(String name, int qty, int price, Date itemDate) {
		super();
		this.name = name;
		this.qty = qty;
		this.price = price;
		this.itemDate = itemDate;
	}

	public ItemObj(int id, String name, int qty, int price, Date itemDate) {
		super();
		this.id = id;
		this.name = name;
		this.qty = qty;
		this.price = price;
		this.itemDate = itemDate;
	}

	public int getId() {
		return id;
	}

	public Date getItemDate() {
		return itemDate;
	}

	public void setItemDate(Date itemDate) {
		this.itemDate = itemDate;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

}
