package pkg;

import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Panel;

import pkg.Node.Status;

public class Field extends Panel {
	
	int x;
	int y;
	Node[][] field;
	Node start = null;
	Node end = null;
	
	public Field(int x, int y) {
		this.x = x;
		this.y = y;
		field = new Node[x][y];
		init();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	private void init() {
		setLayout(new GridLayout(x,y,1,1));
		
		for(int i = 0; i<x; i++)
			for(int j = 0; j<y; j++) {
				field[i][j] = new Node(i,j, this);
				add(field[i][j]);
			}
	}
	
	public void clear() {
		for(int i = 0; i<x; i++)
			for(int j = 0; j<y; j++) {
				field[i][j].status = Status.Plain;
				field[i][j].repaint();
			}
		this.start = null;
		this.end = null;
	}

}
