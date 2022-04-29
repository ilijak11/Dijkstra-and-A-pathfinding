package pkg;

import java.awt.Label;
import java.util.ArrayList;
import java.util.List;

import pkg.Node.Status;

public class Pathfinder implements Runnable {
	
	Field field;
	List<Node> open = new ArrayList<>();
	List<Node> closed = new ArrayList<>();
	Node start;
	Node end;
	Label msg;
	
	public Pathfinder(Field f, Label l) {
		this.field = f;
		this.msg = l;
	}
	
	private void Astar() {
		this.start = field.start;
		this.end = field.end;
		start.Hcost = start.distanceBetween(end);
		start.Gcost = 0;
		open.add(this.start);
		Node curr = null;
		while(open.size() > 0) {
			if(Thread.interrupted()) return;
			curr = getMinFcost(open);
			closed.add(curr);
			if(curr != start) curr.status = Status.Closed;
			curr.repaint();
			curr.Fcost = curr.Hcost + curr.Gcost;
			if(curr == end) break;
			
			List<Node> neighbours = curr.getSurroundingNodes();
			for(Node n: neighbours) {
				if(n.status == Status.Closed || n == start) continue;
				n.Gcost = curr.Gcost + n.distanceBetween(curr);
				n.Hcost = n.distanceBetween(end);
				n.Fcost = n.Hcost + n.Gcost;
				
				if(open.contains(n)) {
					if(n.Gcost <= getMaxGcost(open)) continue;
				} 
				open.add(n);
				n.status = Status.Open;
				n.parent = curr;
				n.repaint();
			}
			try {
				Thread.sleep(Shared.speed);
			} catch (InterruptedException e) {
				return;
			}
		}
		if(curr != end) {
			System.out.println("No path!");
			msg.setText("No path!");
			msg.revalidate();
			return;
		}
		Node retrace = end;
		while(retrace != null) {
			retrace.status = Status.Path;
			retrace.repaint();
			retrace = retrace.parent;
		}
		start.status = Status.Start;
		start.repaint();
		end.status = Status.End;
		end.repaint();
		msg.setText("Path foun!");
		msg.revalidate();
	}
	
	private void Dijkstra() {
		this.start = field.start;
		this.end = field.end;
		start.Hcost = start.distanceBetween(end);
		start.Gcost = 0;
		open.add(this.start);
		Node curr = null;
		while(open.size() > 0) {
			if(Thread.interrupted()) return;
			curr = getMinFcost(open);
			closed.add(curr);
			if(curr != start) curr.status = Status.Closed;
			curr.Fcost = curr.Hcost + curr.Gcost;
			if(curr == end) break;
			
			List<Node> neighbours = curr.getSurroundingNodes();
			for(Node n: neighbours) {
				if(n.status == Status.Closed || n == start) continue;
				n.Fcost = curr.Gcost + n.distanceBetween(curr);
				n.Gcost = n.distanceBetween(start);
				if(open.contains(n)) {
					if(n.Gcost <= n.Fcost) continue;
				} else if(closed.contains(n)) {
					if(n.Gcost <= n.Fcost) continue;
					closed.remove(n);
					open.add(n);
					n.status = Status.Open;
				} else {
					open.add(n);
					n.status = Status.Open;
					n.Hcost = n.distanceBetween(end);
				}
				n.Gcost = n.Fcost;
				n.parent = curr;
				n.repaint();
			}
			closed.add(curr);
			curr.status = Status.Closed;
			curr.repaint();
			try {
				Thread.sleep(Shared.speed);
			} catch (InterruptedException e) {
				return;
			}
		}
		if(curr != end) {
			System.out.println("No path!");
			msg.setText("No path!");
			msg.revalidate();
			return;
		}
		Node retrace = end;
		while(retrace != null) {
			retrace.status = Status.Path;
			retrace.repaint();
			retrace = retrace.parent;
		}
		start.status = Status.Start;
		start.repaint();
		end.status = Status.End;
		end.repaint();
		msg.setText("Path foun!");
		msg.revalidate();
	}

	@Override
	public void run() {
		if(Shared.alg == 0) Astar();
		else Dijkstra();
	}
	
	public Node getMinFcost(List<Node> list) {
		int min = 10000000;
		Node ret = null;
		for(Node n: list) {
			if(n.Fcost < min) {
				min = n.Fcost;
				ret = n;
			}
		}
		list.remove(ret);
		return ret;
	}
	
	public int getMaxGcost(List<Node> list) {
		int max = 0;
		for(Node n: list) {
			if(n.Gcost > max) {
				max = n.Gcost;
			}
		}
		return max;
	}
	
	public void clear() {
		for(Node n: open) {
			if(n == start) continue;
			if(n == end) continue;
			n.status = Status.Plain;
			n.repaint();
		}
		for(Node n: closed) {
			if(n == start) continue;
			if(n == end) continue;
			n.status = Status.Plain;
			n.repaint();
		}
	}

}
