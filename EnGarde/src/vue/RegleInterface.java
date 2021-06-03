package vue;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

public class RegleInterface implements Runnable{
	String s;
	
	public RegleInterface() {
		this.s = regleString();
	}
	
	private String regleString() {
		StringBuilder sb = new StringBuilder("<html>");
		sb.append("");
		return sb.toString();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
}
