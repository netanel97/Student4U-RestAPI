package entities;

import java.util.HashMap;

public class CommandAttributes {
	private HashMap<String, String> key1;
	
	public CommandAttributes() {
		super();
	}

	public CommandAttributes(HashMap<String, String> key1) {
		super();
		this.key1 = key1;
	}

	public HashMap<String, String> getKey1() {
		return key1;
	}

	public void setKey1(HashMap<String, String> key1) {
		this.key1 = key1;
	}

	@Override
	public String toString() {
		return "CommandAttributes [key1=" + key1 + "]";
	}
}
