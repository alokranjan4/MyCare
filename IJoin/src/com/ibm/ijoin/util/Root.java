/**
 * 
 */
package com.ibm.ijoin.util;

import java.util.Map;

/**
 * @author Adeeb
 *
 */
public class Root {
	String name;
	Map<String,Object> childs;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Map<String, Object> getChilds() {
		return childs;
	}
	public void setChilds(Map<String, Object> childs) {
		this.childs = childs;
	}
	@Override
	public String toString() {
		return "Root [name=" + name + ", childs=" + childs + "]";
	}
}
