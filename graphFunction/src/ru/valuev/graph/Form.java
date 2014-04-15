package ru.valuev.graph;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ru.valuev.graph.plotGraph.Plotter;

import java.awt.event.MouseAdapter;

public class Form extends JFrame {
	private JPanel panel;
	// private Plotter p;
	private JTextField txtCoordinates;
	private JTextField txtWheel;

	private int currentX;
	private int currentY;

	private double xStart;
	private double yMin;
	private double xFinal;
	private double yMax;

	private double scale = 1;

	private JTextField txtXstart;
	private JTextField txtYmin;
	private JTextField txtXfinal;
	private JTextField txtYmax;
	
	private Point[] points;
	private int movingPointX = 0;
	private JTextField txtMovingPointX;
	private JTextField txtMovingPointY;

	public Form() {
		setSize(new Dimension(616, 322));

		getContentPane().setBackground(SystemColor.control);
		getContentPane().setLayout(null);

		intializeCordiante();

		panel = new JPanel() {
			private Plotter p;

			@Override
			public void paintComponent(Graphics g) {
				// I don't know? How it's fixed
				p = new Plotter(g, new Plotter.Function() {
					public double fun(double x) {
						return func1(x);
					}
				}, this.getWidth(), this.getHeight());

				p.addFunction(new Plotter.Function() {
					public double fun(double x) {
						return func2(x);
					}
				});

				p.draw(xStart, yMin, xFinal, yMax, null, movingPointX);
				txtMovingPointX.setText(String.format("%.4f", p.getMovingX()));
				txtMovingPointY.setText(String.format("%.4f", p.getMovingY()));
			}
		};
		panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				panel.requestFocusInWindow();
			}
		});
		panel.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				panelKeyPressed(e);
			}
		});
		panel.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {

				double factor = (e.getWheelRotation() == 1) ? 2d : (1d / 2d);

				if (scale < 16d) {
					xStart = ((xStart == 0) ? 1 : xStart) * factor;
					yMin = ((yMin == 0) ? 1 : yMin) * factor;
					xFinal = ((xFinal == 0) ? 1 : xFinal) * factor;
					yMax = ((yMax == 0) ? 1 : yMax) * factor;
					
					outputCoordinate(xStart, yMin, xFinal, yMax);
					repaint();
					
					scale *= factor;
				}
				
				if (scale >= 16d && factor == 1 / 2d)
					scale *= factor;
				txtWheel.setText(scale + "");
			}
		});

		panel.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				panelMouseDragged(e);
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				currentX = e.getX();
				currentY = e.getY();
				
				//JOptionPane.showMessageDialog(null, "Mouse move");
				
				movingPointX = currentX;
				panel.repaint();
			}
		});

		panel.setBounds(177, 11, 421, 240);
		getContentPane().add(panel);

		this.setMinimumSize(this.getSize());
		this.pack();
		panel.requestFocusInWindow();

		txtCoordinates = new JTextField();
		txtCoordinates.setBounds(10, 101, 138, 20);
		getContentPane().add(txtCoordinates);
		txtCoordinates.setColumns(10);

		txtWheel = new JTextField();
		txtWheel.setBounds(10, 132, 138, 20);
		getContentPane().add(txtWheel);
		txtWheel.setColumns(10);

		JLabel lblYmin = new JLabel("yMin");
		lblYmin.setBounds(10, 194, 46, 14);
		getContentPane().add(lblYmin);

		JLabel lblXstart = new JLabel("xStart");
		lblXstart.setBounds(10, 163, 46, 14);
		getContentPane().add(lblXstart);

		txtXstart = new JTextField();
		txtXstart.setText("xStart");
		txtXstart.setBounds(66, 160, 86, 20);
		getContentPane().add(txtXstart);
		txtXstart.setColumns(10);

		txtYmin = new JTextField();
		txtYmin.setText("yMin");
		txtYmin.setBounds(66, 191, 86, 20);
		getContentPane().add(txtYmin);
		txtYmin.setColumns(10);

		JLabel lblXfinal = new JLabel("xFinal");
		lblXfinal.setBounds(10, 220, 46, 14);
		getContentPane().add(lblXfinal);

		txtXfinal = new JTextField();
		txtXfinal.setText("xFinal");
		txtXfinal.setBounds(66, 222, 86, 20);
		getContentPane().add(txtXfinal);
		txtXfinal.setColumns(10);

		JLabel lblYmax = new JLabel("yMax");
		lblYmax.setBounds(14, 256, 46, 14);
		getContentPane().add(lblYmax);

		txtYmax = new JTextField();
		txtYmax.setText("yMax");
		txtYmax.setBounds(66, 253, 86, 20);
		getContentPane().add(txtYmax);
		txtYmax.setColumns(10);
		
		txtMovingPointX = new JTextField();
		txtMovingPointX.setBounds(45, 11, 103, 20);
		getContentPane().add(txtMovingPointX);
		txtMovingPointX.setColumns(10);
		
		txtMovingPointY = new JTextField();
		txtMovingPointY.setBounds(45, 42, 103, 20);
		getContentPane().add(txtMovingPointY);
		txtMovingPointY.setColumns(10);
		
		JLabel lblX = new JLabel("x =");
		lblX.setBounds(10, 14, 46, 14);
		getContentPane().add(lblX);
		
		JLabel lblY = new JLabel("y = ");
		lblY.setBounds(10, 45, 46, 14);
		getContentPane().add(lblY);
		
		points = new Point[] { new Point(0, 0)};
	}

	private void intializeCordiante() {
		xStart = -5;
		yMin = -5;
		xFinal = 5;
		yMax = 5;
	}

	private double func1(double x) {
		return 1 / x;
	}

	private double func2(double x) {
		return x * x;
	}

	
	private Point[] getPoints() {
		return points;
	}

	private void panelKeyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			xStart += scale / 35;
			xFinal += scale / 35;
			break;
		case KeyEvent.VK_RIGHT:
			xStart -= scale / 35;
			xFinal -= scale / 35;
			break;
		case KeyEvent.VK_UP:
			yMin -= scale / 35;
			yMax -= scale / 35;
			break;
		case KeyEvent.VK_DOWN:
			yMin += scale / 35;
			yMax += scale / 35;
			break;
		}

		panel.repaint();
	}

	private void panelMouseDragged(MouseEvent e) {
		double x = currentX - e.getX();
		double y = e.getY() - currentY;

		currentX = e.getX();
		currentY = e.getY();

		x *= (scale / 35);
		y *= (scale / 35);

		xStart += x;
		yMin += y;
		xFinal += x;
		yMax += y;

		txtCoordinates.setText("dragged: " + x + " " + y);
		outputCoordinate(xStart, yMin, xFinal, yMax);

		panel.repaint();
	}

	private void outputCoordinate(double x0, double y0, double x1, double y1) {
		txtXstart.setText(Double.toString(x0));
		txtYmin.setText(Double.toString(y0));
		txtXfinal.setText(Double.toString(x1));
		txtYmax.setText(Double.toString(x1));
	}
}
