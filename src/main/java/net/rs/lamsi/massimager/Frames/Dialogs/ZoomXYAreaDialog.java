package net.rs.lamsi.massimager.Frames.Dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.xy.XYDataset;

public class ZoomXYAreaDialog extends JDialog {

	private ChartPanel chartPanel;
	
	private final JPanel contentPanel = new JPanel();
	private JLabel lbXMaxVal;
	private JLabel lbXMinVal;
	private JTextField txtXMin;
	private JTextField txtXMax;

	/**
	 * Create the dialog.
	 */
	public ZoomXYAreaDialog() {
		setBounds(100, 100, 253, 141); 
		
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[][][][][][grow]", "[][]"));
		{
			JLabel lblXmin = new JLabel("x(min) =");
			contentPanel.add(lblXmin, "cell 0 0,alignx trailing");
		}
		{
			lbXMinVal = new JLabel("0");
			contentPanel.add(lbXMinVal, "cell 1 0");
		}
		{
			JLabel lblX = new JLabel("x0");
			contentPanel.add(lblX, "cell 4 0,alignx trailing");
		}
		{
			txtXMin = new JTextField();
			contentPanel.add(txtXMin, "cell 5 0,alignx leading");
			txtXMin.setColumns(10);
		}
		{
			JLabel lblXmax = new JLabel("x(max) =");
			contentPanel.add(lblXmax, "cell 0 1,alignx trailing");
		}
		{
			lbXMaxVal = new JLabel("0");
			contentPanel.add(lbXMaxVal, "cell 1 1");
		}
		{
			JLabel lblX_1 = new JLabel("x1");
			contentPanel.add(lblX_1, "cell 4 1,alignx trailing");
		}
		{
			txtXMax = new JTextField();
			contentPanel.add(txtXMax, "cell 5 1");
			txtXMax.setColumns(10);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						zoomInX(); 
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	public void setPlotChartPanel(ChartPanel chartPanel) {
		this.chartPanel = chartPanel;
		try {
			XYDataset data = chartPanel.getChart().getXYPlot().getDataset();
			getLbXMinVal().setText(data.getXValue(0, 0)+"");
			getLbXMinVal().setText(data.getXValue(0, data.getItemCount(0))+"");
		}catch(Exception ex) { 
		}
	}

	public void zoomInX() {
		if(chartPanel!=null) {
			try {
				double x0 = Double.valueOf(getTxtXMin().getText());
				double x1 = Double.valueOf(getTxtXMax().getText());
				
				NumberAxis axis = (NumberAxis) chartPanel.getChart().getXYPlot().getDomainAxis();
				
				axis.setLowerBound(x0);
				axis.setUpperBound(x1);
				 
				chartPanel.restoreAutoRangeBounds();
				
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public JLabel getLbXMaxVal() {
		return lbXMaxVal;
	}
	public JLabel getLbXMinVal() {
		return lbXMinVal;
	}
	public JTextField getTxtXMin() {
		return txtXMin;
	}
	public JTextField getTxtXMax() {
		return txtXMax;
	}
}
