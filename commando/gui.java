import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.UIManager;
import java.awt.Dimension;
import java.awt.Toolkit;
//---
import javax.swing.JFrame;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
//---
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.border.Border;
import javax.swing.BorderFactory;

public class gui
{
	public gui()
	{
		Frame frame = new mf();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = frame.getSize();
		if (frameSize.height > screenSize.height)
		{
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width)
		{
			frameSize.width = screenSize.width;
		}
		frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
		frame.addWindowListener(new WindowAdapter()
			{
				public void windowClosing(WindowEvent e)
				{
					System.exit(0);
				}
			});
		frame.setVisible(true);
	}

	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		new gui();
	}

private class mf extends JFrame 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel statusBar = new JLabel();
	private JMenuItem menuFileExit = new JMenuItem();
	private JMenu menuFile = new JMenu();
	private JMenuBar menuBar = new JMenuBar();
	private JPanel panelCenter = new JPanel();
	private BorderLayout layoutMain = new BorderLayout();
	private JMenu jMenu1 = new JMenu();
	private JMenuItem jMenuItem1 = new JMenuItem();
	private JMenuItem jMenuItem2 = new JMenuItem();
	private JMenuItem jMenuItem3 = new JMenuItem();
	private JMenuItem jMenuItem4 = new JMenuItem();
	private JMenu menuHelp = new JMenu();
	private JMenuItem menuHelpAbout = new JMenuItem();

	public mf()
	{
		try
		{
			jbInit();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	private void jbInit() throws Exception
	{
		this.setJMenuBar(menuBar);
		this.getContentPane().setLayout(layoutMain);
		panelCenter.setLayout(null);
		jMenu1.setText("Dark Continent");
		jMenuItem1.setText("Save");
		jMenuItem1.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					DC_Save(e);
				}
			});
		jMenuItem2.setText("Save And Exit");
		jMenuItem2.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					DC_SaveExit(e);
				}
			});
		jMenuItem3.setText("Shutdown No Save");
		jMenuItem3.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					DC_Shutdown(e);
				}
			});
		jMenuItem4.setText("Scatter Items");
		jMenuItem4.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					DC_Scatter(e);
				}
			});
		menuHelp.setText("Help");
		menuHelpAbout.setText("About");
		menuHelpAbout.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent ae)
				{
					helpAbout_ActionPerformed(ae);
				}
			});
		this.setSize(new Dimension(400, 300));
		this.setTitle("Commando");
		menuFile.setText("Commando");
		menuFileExit.setText("Exit");
		menuFileExit.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent ae)
				{
					fileExit_ActionPerformed(ae);
				}
			});
		statusBar.setText("Lock and load...");
		menuHelp.add(menuHelpAbout);
		menuFile.add(menuFileExit);
		menuBar.add(menuFile);
		jMenu1.add(jMenuItem1);
		jMenu1.add(jMenuItem2);
		jMenu1.addSeparator();
		jMenu1.add(jMenuItem3);
		jMenu1.addSeparator();
		jMenu1.add(jMenuItem4);
		menuBar.add(jMenu1);
		menuBar.add(menuHelp);
		this.getContentPane().add(statusBar, BorderLayout.SOUTH);
		this.getContentPane().add(panelCenter, BorderLayout.CENTER);
	}

	void fileExit_ActionPerformed(ActionEvent e)
	{
		System.exit(0);
	}

	void helpAbout_ActionPerformed(ActionEvent e)
	{
		JOptionPane.showMessageDialog(this, new mf_AboutBoxPanel1(), "About", JOptionPane.PLAIN_MESSAGE);
	}

	private void DC_Save(ActionEvent e)
	{
		statusBar.setText( new cmd().send( "save" ));
	}

	private void DC_SaveExit(ActionEvent e)
	{
		statusBar.setText( new cmd().send( "quit" ));
	}

	private void DC_Shutdown(ActionEvent e)
	{
		statusBar.setText( new cmd().send( "bail" ));
	}
	private void DC_Scatter(ActionEvent e)
	{
		statusBar.setText( new cmd().send( "scat" ));
	}
}

private class mf_AboutBoxPanel1 extends JPanel 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Border border = BorderFactory.createEtchedBorder();
	private GridBagLayout layoutMain = new GridBagLayout();
	private JLabel labelCompany = new JLabel();
	private JLabel labelCopyright = new JLabel();
	private JLabel labelAuthor = new JLabel();
	private JLabel labelTitle = new JLabel();

	public mf_AboutBoxPanel1()
	{
		try
		{
			jbInit();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	private void jbInit() throws Exception
	{
		this.setLayout(layoutMain);
		this.setBorder(border);
		labelTitle.setText("Dark Continent Command Tool");
		labelAuthor.setText("by Linus Sphinx");
		labelCopyright.setText("Copyright 2003 All Rights Reserved");
		labelCompany.setText("A Full Sack Production");
		this.add(labelTitle, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 15, 0, 15), 0, 0));
		this.add(labelAuthor, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 15, 0, 15), 0, 0));
		this.add(labelCopyright, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 15, 0, 15), 0, 0));
		this.add(labelCompany, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 15, 5, 15), 0, 0));
	}
}
}