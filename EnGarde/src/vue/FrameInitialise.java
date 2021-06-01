package vue;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import java.awt.GridLayout;
import javax.swing.JRadioButton;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;

public class FrameInitialise extends JFrame {

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
		
		Box verticalBox = Box.createVerticalBox();
		getContentPane().add(verticalBox);
		
		
		
		JLabel lblNewLabel = new JLabel("Page charger");
		verticalBox.add(lblNewLabel);
		
		JComboBox comboBox = new JComboBox();
		verticalBox.add(comboBox);
		
		Box horizontalBox = Box.createHorizontalBox();
		verticalBox.add(horizontalBox);
		
		JButton btnNewButton = new JButton("Charger");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		horizontalBox.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Quitter");
		horizontalBox.add(btnNewButton_1);
	}

}
