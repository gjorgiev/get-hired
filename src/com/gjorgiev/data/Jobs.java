package com.gjorgiev.data;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

public class Jobs implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int _itemcount = 0;
	private List<Job> _itemlist;
	
	public Jobs() {
		_itemlist = new Vector<Job>(0);
	}

	public void addItem(Job item) {
		_itemlist.add(item);
		_itemcount++;
	}

	public Job getItem(int location) {
		return _itemlist.get(location);
	}

	public int getItemCount() {
		return _itemcount;
	}
}
