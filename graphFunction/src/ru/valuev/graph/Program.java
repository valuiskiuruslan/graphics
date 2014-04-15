package ru.valuev.graph;

public class Program {

	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				Form f = new Form();
				f.setDefaultCloseOperation(Form.EXIT_ON_CLOSE);
				f.setVisible(true);				
			}			
		});
	}

}
