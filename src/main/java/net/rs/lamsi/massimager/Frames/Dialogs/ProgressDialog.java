package net.rs.lamsi.massimager.Frames.Dialogs;


import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import net.rs.lamsi.massimager.Threads.ProgressUpdateTask;

public class ProgressDialog extends JDialog {
	private static ProgressDialog instance;
	
	private final JPanel contentPanel = new JPanel();
	private JProgressBar progressBar;

	private int stepwidth=0;
 

	/**
	 * Create the dialog.
	 */
	public ProgressDialog(JFrame mainframe) {
		super(mainframe, "Progress Dialog", ModalityType.MODELESS);
		
		setBounds(100, 100, 256, 159);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		 
	    progressBar = new JProgressBar(0, 1000);
	    progressBar.setValue(200);
	    progressBar.setStringPainted( true );

	    contentPanel.add(BorderLayout.CENTER, progressBar);
	    contentPanel.add(BorderLayout.NORTH, new JLabel("Progress..."));
	    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); 
	    
	    this.pack();
	}
	
	public static void initDialog(JFrame mainframe) {
		instance = new ProgressDialog(mainframe);
	}

	// Allways a new Task
	public ProgressUpdateTask startTask(ProgressUpdateTask task) {
		setVisibleDialog(true);
		
	    // UpdatingTask  
		task.execute(); 
		return task;
	}
 

	public static void setProgress(int promille) {
		getInst().getProgressBar().setValue(promille);    
	}
	public static int getProgress() {
		return getInst().getProgressBar().getValue();  
	}
	
	public void setVisibleDialog(boolean flag) {
		getProgressBar().setValue(0);
		setVisible(flag);
	}
	
	public static ProgressDialog getInst() {
		return instance;
	}
	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public static void addProgressStep(double a) {
		setProgress(getProgress()+(int)(getInst().getStepwidth()*a));
	}
	public void setProgressBar(JProgressBar progressBar) {
		this.progressBar = progressBar;
	}

	public static void setProgressSteps(int steps) {
		getInst().setStepWidth(steps);
	}

	protected void setStepWidth(int steps) {
		if(steps>0) 
			this.stepwidth = progressBar.getMaximum()/steps;
	} 
	public int getStepwidth() {
		return stepwidth;
	} 
}
