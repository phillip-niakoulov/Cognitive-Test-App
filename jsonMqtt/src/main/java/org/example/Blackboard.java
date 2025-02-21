package org.example;

import java.beans.PropertyChangeSupport;

public class Blackboard extends PropertyChangeSupport {
	
	private static Blackboard instance;

	private char[] pad;
	
	private Blackboard() {
		super(new Object());
		pad = new char[]{'#', '#', '#'};
	}
	
	public static Blackboard getInstance() {
		if (instance == null)
			instance = new Blackboard();
		return instance;
	}
	
	public void set(char[] newPad) {
		pad = newPad;
		firePropertyChange("pad", null, pad);
	}

	public void clear() {
		pad = new char[]{'#', '#', '#'};
		firePropertyChange("pad", null, pad);
	}

	public char[] getPad() {
		return pad;
	}
	
}