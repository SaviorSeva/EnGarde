package modele;

import java.awt.Point;

public class InterfaceElementPosition {
	private InterfaceElementType ele;
	private Point p1;
	private Point p2;
	private int nombre;
	
	public InterfaceElementPosition(InterfaceElementType ele, Point p1, Point p2, int nombre) {
		this.ele = ele;
		this.p1 = p1;
		this.p2 = p2;
		this.nombre = nombre;
	}
	
	public InterfaceElementPosition(InterfaceElementType ele, int x1, int x2, int y1, int y2, int nombre) {
		this.ele = ele;
		this.p1 = new Point(x1, y1);
		this.p2 = new Point(x2, y2);
		this.nombre = nombre;
	}

	public InterfaceElementType getEle() {
		return ele;
	}

	public void setEle(InterfaceElementType ele) {
		this.ele = ele;
	}

	public Point getP1() {
		return p1;
	}

	public Point getP2() {
		return p2;
	}

	public void setP1(Point p1) {
		this.p1 = p1;
	}

	public void setP2(Point p2) {
		this.p2 = p2;
	}

	public int getNombre() {
		return nombre;
	}

	public void setNombre(int nombre) {
		this.nombre = nombre;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(ele.toString() + ", [");
		sb.append(p1.toString() + "; ");
		sb.append(p2.toString() + "; ");
		sb.append("] " + nombre + ".");
		return sb.toString();
	}
}
