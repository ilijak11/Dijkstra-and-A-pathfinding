package pkg;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Node extends Canvas {
	
	public enum Status{ Wall, Start, End, Plain, Closed, Open, Path };
	int Fcost; // total cost
	int Gcost; // distance to start
	int Hcost; // distance to end
	int x;
	int y;
	Node parent = null;
	Status status;
	Color background;
	Field field;
	
	
	public Node(int x, int y, Field field) {
		this.x = x;
		this.y = y;
		Hcost = Gcost = 10000000;
		this.field = field;
		status = Status.Plain;
		init();
	}
	
	private void init() {
		addMouseListener(new MouseAdapter() {
				
			@Override
			public void mouseEntered(MouseEvent e) {
				if(Shared.editable) {
					if((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
						if(status == Status.Plain) status = Status.Wall;
						else if(status == Status.Wall) status = Status.Plain;
						else return;
						System.out.println("pressed " + Node.this.x + " " + Node.this.y);
						repaint();
					}
				}
			}
			
			
			@Override
			public void mousePressed(MouseEvent e) {
				if(Shared.editable) {
					if(status == Status.Plain) status = Status.Wall;
					else if(status == Status.Wall) status = Status.Plain;
					else return;
				} else {
					if(field.start == null && field.end == null) {
						status = Status.Start;
						field.start = Node.this;
						System.out.println("Start: " + Node.this.x + " " + Node.this.y);
					} else if(field.start != null && field.end == null) {
						if(Node.this.status == Status.Start) {
							status = Status.Plain;
							field.start = null;
							Shared.selectedSE = false;
							System.out.println("Start removed");
						} else {
							status = Status.End;
							field.end = Node.this;
							Shared.selectedSE = true;
							System.out.println("End: " + Node.this.x + " " + Node.this.y);
						}
					} else if(field.start == null && field.end != null) {
						if(Node.this.status == Status.End) {
							status = Status.Plain;
							field.end = null;
							Shared.selectedSE = false;
							System.out.println("End removed");
						} else {
							status = Status.Start;
							field.start = Node.this;
							Shared.selectedSE = true;
							System.out.println("Start: " + Node.this.x + " " + Node.this.y);
						}
					} else {
						if(Node.this.status == Status.End) {
							status = Status.Plain;
							field.end = null;
							Shared.selectedSE = false;
							System.out.println("End removed");
						} else if(Node.this.status == Status.Start) {
							status = Status.Plain;
							field.start = null;
							Shared.selectedSE = false;
							System.out.println("Start removed");
						} else {
							field.start.status = Status.Plain;
							field.start.repaint();
							status = Status.Start;
							Shared.selectedSE = true;
							field.start = Node.this;
							System.out.println("Start: " + Node.this.x + " " + Node.this.y);
						}
					}
				}
				System.out.println("pressed " + Node.this.x + " " + Node.this.y);
				repaint();
			}
		});
		repaint();
	}
	
	
	public void paint(Graphics g) {
		if(status == Status.Wall) background = Color.BLACK;
		else if (status == Status.End) {
			background = Color.BLUE;
			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial", Font.BOLD, 10));
			g.drawString("E", getWidth()/2, getHeight()/2);
		} else if (status == Status.Start) {
			background = Color.BLUE;
			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial", Font.BOLD, 10));
			g.drawString("S", getWidth()/2, getHeight()/2);
		} else if (status == Status.Closed) background = Color.RED;
		  else if (status == Status.Open) background = Color.GREEN;
		  else if (status == Status.Path) background = Color.CYAN;
		else background = Color.GRAY;
		setBackground(background);
	}
	
	public int distanceBetween(Node n) {
		int x = Math.abs(n.x - this.x);
		int y = Math.abs(n.y - this.y);
		return x + y;
	}
	
	public List<Node> getSurroundingNodes(){
		List<Node> ret = new ArrayList<>();
		for(int i = -1; i<=1; i++) {
			for(int j = -1; j<=1; j++) {
				if(x+i < 0 || y+j < 0 || x+i >= field.x || y+j >= field.y || (i == 0 && j == 0)) continue;
				if(i == -1 && j == -1) {
					if(field.field[x-1][y].status == Status.Wall 
							&& field.field[x][y-1].status == Status.Wall) continue;
				} else if(i == 1 && j == 1) { 
					if(field.field[x+1][y].status == Status.Wall 
							&& field.field[x][y+1].status == Status.Wall) continue;
				} else if(i == -1 && j == 1) {
					if(field.field[x-1][y].status == Status.Wall 
							&& field.field[x][y+1].status == Status.Wall) continue;
				} else if(i == 1 && j == -1) {
					if(field.field[x+1][y].status == Status.Wall 
							&& field.field[x][y-1].status == Status.Wall) continue;
				}
				if(field.field[x+i][y+j].status != Status.Wall) ret.add(field.field[x+i][y+j]);
				//System.out.println((x+i) + "," + (y+j));
			}
		}
		return ret;
	}
	
	public static void main(String[] args) {
		
		Field f = new Field(4,4);
		
		for(int i = 0; i<10; i++) {
			int x1 = new Random().nextInt(4);
			int y1 = new Random().nextInt(4);
			int x2 = new Random().nextInt(4);
			int y2 = new Random().nextInt(4);
			
			System.out.println("Node1(" + x1 + "," + y1 + ")-Node2("
					+ x2 + "," + y2 + ") - dist=" + f.field[x1][y1].distanceBetween(f.field[x2][y2]));
			
			System.out.println("Surrounding nodes for Node(" + x1 + "," + y1 + "):");
			f.field[x1][y1].getSurroundingNodes();
			System.out.println("______________");
		}
	}
	
	
}
