package vue;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import java.awt.GridLayout;
import javax.swing.JRadioButton;

public class FrameInitialise extends JFrame {
	/**
	 * @wbp.nonvisual location=-33,57
	 */
	private final JRadioButton radioButton = new JRadioButton("New radio button");

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FrameInitialise frame = new FrameInitialise();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public FrameInitialise() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 450);
		getContentPane().setLayout(new GridLayout(1, 0, 0, 0));
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
	}

}
