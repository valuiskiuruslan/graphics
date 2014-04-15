package ru.valuev.graph.plotGraph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

public class Plotter {
	public interface Function {
		public double fun(double x);
	}

	public static final double STEP = 0.001;

	private Graphics2D graph;
	private List<Function> functions;
	private int displayWidth;
	private int displayHeight;

	private double xStart;
	private double yMin;
	private double xFinal;
	private double yMax;

	private final int DISPLACEMENT_WIDTH = 40;
	private final int DISPLACEMENT_HEIGHT = 20;

	private final int AMOUNT_CELLS = 10;

	private int widthOutput;
	private int heightOutput;

	private double scaleX;
	private double scaleY;

	public Plotter(Graphics graph, Function f, int width, int height) {
		this.graph = (Graphics2D) graph;
		this.functions = new LinkedList<Function>();
		this.functions.add(f);
		this.displayWidth = width;
		this.displayHeight = height;
	}

	public void addFunction(Function f) {
		this.functions.add(f);
	}

	public void removeFunction(Function f) {
		this.functions.remove(f);
	}

	public void removeFunctionByIndex(int index) {
		this.functions.remove(index);
	}

	public void setDisplayWidth(int width) {
		this.displayWidth = width;
	}

	public void setDisplayHeight(int height) {
		this.displayHeight = height;
	}

	public void draw(double xStart, double yMin, double xFinal, double yMax) {
		draw(xStart, yMin, xFinal, yMax, null, 0);
	}

	public void draw(double xStart, double yMin, double xFinal, double yMax,
			Point[] points, int xRight) {
		this.xStart = xStart;
		this.yMin = yMin;
		this.xFinal = xFinal;
		this.yMax = yMax;

		clearDisplay();
		drawGrid();

		drawAxes();

		for (Function f : functions) {
			drawGraphic(f);
		}

//		if (points != null)
//			for (Point p : points) {
//				drawPoint(p);
//			}
		
		drawMovingPoint(xRight, functions.get(1));

		signCoordinates();
	}

	private void drawGraphic(Function f) {
		graph.setColor(new Color(255, 0, 0));
		graph.setStroke(new BasicStroke(2.0f));

		int xLeft = DISPLACEMENT_WIDTH;
		int yLeft = (int) Math.round(getHeightOutput() + yMin * getScaleY()
				- f.fun(xStart) * getScaleY());
		// graph.drawOval(xLeft, yLeft, 1, 1);
		graph.drawLine(xLeft, yLeft, xLeft, yLeft);

		double stepByX = 0;
		for (double x = xStart; x <= xFinal; x += STEP) {
			stepByX += STEP;

			int xRight = DISPLACEMENT_WIDTH
					+ (int) Math.round(stepByX * getScaleX());
			int yRight = getHeightOutput()
					+ (int) Math.round(yMin * getScaleY() - f.fun(x)
							* getScaleY());

			// if (yRight <= getHeightOutput())
			// graph.drawLine(xLeft, yLeft, xRight, yRight);
			// xLeft = xRight;
			// yLeft = yRight;

			// if (yRight <= getHeightOutput())
			// graph.drawOval(xRight - 1, yRight - 2, 2, 2);

			if (yRight <= getHeightOutput())
				graph.drawLine(xRight, yRight, xRight, yRight);

		}

	}

	private void drawPoint(Point p) {
		int zeroByOX = DISPLACEMENT_WIDTH
				- (int) Math.round(xStart * getScaleX());
		int outX = zeroByOX + (int) Math.round(p.x * getScaleX());

		int zeroByOY = getHeightOutput() + (int) Math.round(yMin * getScaleY());
		int outY = zeroByOY - (int) Math.round(p.y * getScaleY());
		// int zeroByOY = -(int) Math.round(yMin * getScaleY());
		// int outY = zeroByOY - (int) Math.round(p.y * getScaleY());

		graph.setColor(new Color(0, 0, 255));
		graph.setStroke(new BasicStroke(4.0f));
		if (outX >= DISPLACEMENT_WIDTH && outY <= getHeightOutput())
			graph.drawLine(outX, outY, outX, outY);
	}
	
	private double movingX = 0.0;
	private double movingY = 0.0;
	
	public double getMovingX() {
		return movingX;
	}
	
	public double getMovingY() {
		return movingY;
	}
	
	private void drawMovingPoint(int xRight, Function f) {
		double stepByX = (xRight - DISPLACEMENT_WIDTH) / getScaleX();
		
		movingX = xStart + stepByX;
		movingY = f.fun(movingX);
		
		int zeroByOY = getHeightOutput() + (int) Math.round(yMin * getScaleY());
		int yRight = zeroByOY - (int) Math.round(movingY * getScaleY());
		
		graph.setColor(Color.BLACK);
		graph.setStroke(new BasicStroke(6.0f));
		
		graph.drawLine(xRight, yRight, xRight, yRight);
	}

	// Maybe its method need to do abstract??
	private void signCoordinates() {
		graph.setColor(Color.BLUE);
		graph.setFont(new Font(Font.SANS_SERIF, 0, 10));
		double stepByX = (xFinal - xStart) / AMOUNT_CELLS;
		double x = xStart;
		for (int i = 0; i <= getWidthOutput(); i += (int) getWidthOutput()
				/ AMOUNT_CELLS) {
			graph.drawString(String.format("%.2f", x), i + 30,
					getHeightOutput() + DISPLACEMENT_HEIGHT - 5);
			x += stepByX;
		}

		double stepByY = (yMax - yMin) / AMOUNT_CELLS;
		double y = yMax;
		for (int i = 0; i <= getHeightOutput(); i += (int) getHeightOutput()
				/ AMOUNT_CELLS) {
			graph.drawString(String.format("%8.2f", y), 0, i + 5);
			y -= stepByY;
		}
	}

	private void drawAxes() {
		graph.setColor(new Color(11, 3, 130));
		graph.setStroke(new BasicStroke(3.0f));
		// Save and create fonts
		Font oldFont = graph.getFont();
		Font newFont = new Font("TimesRoman", 0, 14);
		int offset = 5;

		// Draw axis OY
		int outXStart = DISPLACEMENT_WIDTH
				- (int) Math.round(xStart * getScaleX());
		int outYStart = 0;
		int outXEnd = DISPLACEMENT_WIDTH
				- (int) Math.round(xStart * getScaleX());
		int outYEnd = getHeightOutput();

		if (outXStart >= DISPLACEMENT_WIDTH) {
			graph.drawLine(outXStart, outYStart, outXEnd, outYEnd);

			// Draw arrow OY
			graph.fillPolygon(new int[] { outXStart - offset, outXStart,
					outXStart + offset }, new int[] { outYStart + 2 * offset,
					outYStart, outYStart + 2 * offset }, 3);

			// Sign axis OY
			graph.setFont(newFont);
			graph.drawString("y", outXStart - 2 * offset, outYStart + 2
					* offset);
		}

		int coordinatesZeroX = outXStart - 2 * offset;
		int coordinatesZeroY = -1;

		// -----------------------------------------------------------------
		// Draw axis OX
		outXStart = DISPLACEMENT_WIDTH;
		outYStart = getHeightOutput() + (int) Math.round(yMin * getScaleY());
		outXEnd = getWidthOutput() + DISPLACEMENT_WIDTH;
		outYEnd = getHeightOutput() + (int) Math.round(yMin * getScaleY());

		if (outYStart <= getHeightOutput()) {
			graph.drawLine(DISPLACEMENT_WIDTH, outYStart, getWidthOutput()
					+ DISPLACEMENT_WIDTH, outYEnd);

			// Draw arrow OX
			offset = 5;
			graph.fillPolygon(new int[] { outXEnd - 2 * offset, outXEnd,
					outXEnd - 2 * offset }, new int[] { outYEnd - offset,
					outYEnd, outYEnd + offset }, 3);

			// Sign axis OX

			graph.drawString("x", outXEnd - 2 * offset, outYEnd + 3 * offset);
		}

		coordinatesZeroY = outYEnd + 3 * offset;

		// Sign 0
		if (coordinatesZeroX >= DISPLACEMENT_WIDTH
				&& coordinatesZeroY <= getHeightOutput())
			graph.drawString("0", coordinatesZeroX, coordinatesZeroY);

	}

	private void drawGrid() {
		graph.setColor(Color.black);

		// Draw a line parallel to the c OY
		for (int i = 0; i <= getWidthOutput(); i += (int) Math
				.round(getWidthOutput() / AMOUNT_CELLS)) {
			graph.drawLine(i + DISPLACEMENT_WIDTH, 0, i + DISPLACEMENT_WIDTH,
					getHeightOutput());
		}

		// for (int i = widthOutput; i >= 0; i -= Math.round(widthOutput /
		// AMOUNT_CELLS)) {
		// graph.drawLine(i + DISPLACEMENT_WIDTH, getHeightOutput(), i +
		// DISPLACEMENT_WIDTH, 0);
		// }

		// // Draw a line parallel to the axis OX
		// for (int i = 0; i <= getHeightOutput(); i += (int)
		// Math.round(getHeightOutput() / AMOUNT_CELLS)) {
		// graph.drawLine(DISPLACEMENT_WIDTH, i, widthOutput +
		// DISPLACEMENT_WIDTH, i);
		// }
		//
		for (int i = getHeightOutput(); i >= 0; i -= (int) Math
				.round(getHeightOutput() / AMOUNT_CELLS)) {
			graph.drawLine(getWidthOutput() + DISPLACEMENT_WIDTH, i,
					DISPLACEMENT_WIDTH, i);
		}
	}

	private boolean isDetermineScale = false;

	private double getScaleX() {
		if (!isDetermineScale)
			determineScale();
		return scaleX;
	}

	private double getScaleY() {
		if (!isDetermineScale)
			determineScale();
		return scaleY;
	}

	private void determineScale() {
		scaleX = getWidthOutput() / Math.abs(xFinal - xStart);
		scaleY = getHeightOutput() / Math.abs(yMax - yMin);

		isDetermineScale = true;
	}

	private boolean isDetermineOutputRegion = false;

	private int getWidthOutput() {
		if (!isDetermineOutputRegion)
			determineOutputRegion();
		return widthOutput;
	}

	private int getHeightOutput() {
		if (!isDetermineOutputRegion)
			determineOutputRegion();
		return heightOutput;
	}

	private void determineOutputRegion() {
		widthOutput = displayWidth - DISPLACEMENT_WIDTH;
		heightOutput = displayHeight - DISPLACEMENT_HEIGHT;
	}

	private void clearDisplay() {
		graph.setColor(new Color(220, 220, 220));
		graph.fillRect(0, 0, this.displayWidth, this.displayHeight);
	}

	private void resetFields() {
		this.isDetermineScale = false;
	}

}
