package modele;

import java.util.ArrayList;

public class IAMove {
	int direction;
	ArrayList<Boolean> selected;
	
	public IAMove(int direction, ArrayList<Boolean> selected) {
		this.direction = direction;
		this.selected = selected;
	}

	public int getDirection() {
		return direction;
	}

	public ArrayList<Boolean> getSelected() {
		return selected;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public void setSelected(ArrayList<Boolean> selected) {
		this.selected = selected;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("IAMove [");
		sb.append(this.direction + ", ");
		for(int i=0; i<this.selected.size(); i++) {
			if(this.selected.get(i)) sb.append("T");
			else sb.append("F");
		}
		sb.append("] ");
		return sb.toString();
	}
}
