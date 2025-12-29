package com.ecom.util;

public enum OrderStatus {

	In_PROGRESS(1, "In Progress"), ORDER_RECEIVED(2, "Order Received"), ORDER_PACKED(3, "Order Packed"),
	OUT_FOR_DELIVERY(4, "Out for Delivery"), DELIVERED(5, "Delivered"), CANCEL(6, "Cancelled");

	private Integer id;

	private String name;

	private OrderStatus(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
