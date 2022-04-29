package pkg;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MyWindow extends Frame {
	
	Field field;
	Panel speedCtr = new Panel();
	Panel dash = new Panel(new GridLayout(1,0,3,3));
	Panel controls = new Panel(new GridLayout(3,2,3,3));
	Button start = new Button("Start");
	Button edit = new Button("Draw walls");
	Button clear = new Button("Clear");
	Button repeat = new Button("Repeat");
	Label label = new Label("Set START and FINISH");
	Label speedLab = new Label("Fast");
	CheckboxGroup speed = new CheckboxGroup();
	Checkbox fast = new Checkbox("fast", true, speed);
	Checkbox medium = new Checkbox("medium", false, speed);
	Checkbox slow = new Checkbox("slow", false, speed);
	CheckboxGroup algorithm = new CheckboxGroup();
	Checkbox astar = new Checkbox("A*", true, algorithm);
	Checkbox dijkstra = new Checkbox("Dijkstra", false, algorithm);
	Label algLab = new Label("A*");
	Panel algCtr = new Panel();
	Pathfinder pathfinder;
	Thread thread;
	
	public MyWindow(int x, int y) {
		field = new Field(x,y);
		pathfinder = new Pathfinder(field, label);
		setBounds(200,25, 1000, 1000);
		setTitle("A* pathfinding");
		populateWindow();
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {	
				if(thread != null) thread.interrupt();
				dispose();
			}
		});
		setResizable(true);
		setVisible(true);
	}
	
	private void populateWindow() {
		astar.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				algLab.setText("A*");
				Shared.alg = 0;
				algLab.revalidate();
			}
		});
		dijkstra.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				algLab.setText("Dijkstra");
				Shared.alg = 1;
				algLab.revalidate();
			}
		});
		fast.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				speedLab.setText("Fast");
				Shared.speed = 1;
				speedLab.revalidate();
			}
		});
		medium.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				speedLab.setText("Medium");
				Shared.speed = 20;
				speedLab.revalidate();
			}
		});
		slow.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				speedLab.setText("Slow");
				Shared.speed = 200;
				speedLab.revalidate();
			}
		});
		edit.setBackground(Color.cyan);
		edit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Shared.editable = !Shared.editable;
				if(Shared.editable) {
					label.setText("Draw walls");
					edit.setBackground(Color.blue);
				} else {
					label.setText("Set Start and Finish");
					edit.setBackground(Color.cyan);
				}
			}
		});
		clear.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				pathfinder = new Pathfinder(field, label);
				if(thread != null) {
					thread.interrupt();
					try {
						thread.join();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				thread = new Thread(pathfinder);
				field.clear();
				label.setText("Set Start and Finish");
				label.revalidate();
			}
		});
		start.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!Shared.selectedSE) {
					String msg = "";
					if(field.start == null) msg += "Start point not set ";
					if(field.end == null) msg += "End point not set";
					msg += "!";
					label.setText(msg);
				} else if(Shared.editable) {
					return;
				} else {
					Shared.started = true;
					if(thread != null) {
						thread.interrupt();
						try {
							thread.join();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					thread = new Thread(pathfinder);
					thread.start();
					label.setText("Started!");
					label.revalidate();
				}
			}
		});
		repeat.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(Shared.started) {
					if(thread != null) {
						thread.interrupt();
						try {
							thread.join();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					pathfinder.clear();
					pathfinder = new Pathfinder(field, label);
					thread = new Thread(pathfinder);
					thread.start();
					label.setText("Started!");
					label.revalidate();
				}
				
			}
		});
		algCtr.add(astar);
		algCtr.add(dijkstra);
		algCtr.add(algLab);
		speedCtr.add(fast);
		speedCtr.add(medium);
		speedCtr.add(slow);
		speedCtr.add(speedLab);
		controls.add(start);
		controls.add(repeat);
		controls.add(edit);
		controls.add(clear);
		controls.add(speedCtr);
		controls.add(algCtr);
		label.setAlignment(Label.CENTER);
		label.setFont(new Font("Arial", Font.BOLD, 36));
		speedLab.setFont(new Font("Arial", Font.BOLD, 15));
		algLab.setFont(new Font("Arial", Font.BOLD, 15));
		dash.add(label);
		//dash.add(speedCtr);
		dash.add(controls);
		add(dash, BorderLayout.SOUTH);
		add(field, BorderLayout.CENTER);
	}

	public static void main(String[] args) {
		new MyWindow(50,50);
	}

}
