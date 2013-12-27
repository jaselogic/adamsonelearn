package com.jaselogic.adamsonelearn;

public class DrawerListItem {
	public enum ItemType {
		NAME,
		SIMPLE,
		SEPARATOR
	}
	
	public ItemType itemType;
	public String label;
	public int imageResource;
}
