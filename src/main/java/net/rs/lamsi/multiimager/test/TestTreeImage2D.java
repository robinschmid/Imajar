package net.rs.lamsi.multiimager.test;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.File;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import net.rs.lamsi.general.datamodel.image.Collection2D;
import net.rs.lamsi.general.datamodel.image.Image2D;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.tree.IconNode;
import net.rs.lamsi.massimager.Frames.FrameWork.modules.tree.IconNodeRenderer;
import net.rs.lamsi.massimager.Settings.image.SettingsImageDataImportTxt;
import net.rs.lamsi.massimager.Settings.image.SettingsImageDataImportTxt.IMPORT;
import net.rs.lamsi.multiimager.Frames.ImageEditorWindow;
import net.rs.lamsi.utils.FileAndPathUtil;

public class TestTreeImage2D extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		try {
			//UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// start MultiImager application
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ImageEditorWindow window = new ImageEditorWindow();
					window.setVisible(true);
					// load data ThermoMP17 Image qtofwerk.csv 
					String s = FileAndPathUtil.getPathOfJar().getParent()+"/data/qtofwerk.csv";
					File[] files = {new File(s)};

					SettingsImageDataImportTxt settingsDataImport = new SettingsImageDataImportTxt(IMPORT.CONTINOUS_DATA_TXT_CSV, true, ",", false);
					window.getLogicRunner().importTextDataToImage(settingsDataImport, files);

					s = FileAndPathUtil.getPathOfJar().getParent()+"/data/thermomp17.csv";
					File[] files2 = {new File(s)};

					settingsDataImport = new SettingsImageDataImportTxt(IMPORT.PRESETS_THERMO_MP17, true, "	", false);
					window.getLogicRunner().importTextDataToImage(settingsDataImport, files2);

					Vector<Image2D> list = window.getLogicRunner().getListImages();

					// show tree 
					TestTreeImage2D frame = new TestTreeImage2D(list);
					frame.setVisible(true);
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		});  
	}

	/**
	 * Create the frame.
	 * @param list 
	 */
	public TestTreeImage2D(Vector<Image2D> list) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane, BorderLayout.CENTER);

		// create node
		IconNode  root = new IconNode("Collections");
		IconNode  node = null;
		Collection2D coll = null;
		// add 
		for(Image2D img : list) {
			File f = new File(img.getSettImage().getRAWFilepath());
			if(coll==null || !coll.getPath().equals(f)) {
				// create new collection node
				coll = new Collection2D(f);
				coll.add(img);
				node = new IconNode(coll); 
				root.add(node);
				// add img to node
				IconNode in = new IconNode(img, false, img.getIcon(60)); 
				node.add(in); 
			}
			else {
				// add to coll
				coll.add(img);
				node.add(new IconNode(img, false, img.getIcon(60)));
			}
		}

		// create tree 
		JTree treeImage2D = new JTree(root);
		treeImage2D.addTreeSelectionListener(new TreeSelectionListener() {
			
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				JTree tree = (JTree) e.getSource(); 
				TreePath[] paths = tree.getSelectionPaths();
				for(TreePath p : paths) {
					System.out.println(p.getLastPathComponent().toString());
				}
			}
		});
		treeImage2D.setCellRenderer(new IconNodeRenderer());
		treeImage2D.setShowsRootHandles(true);
		treeImage2D.setRootVisible(false);
		scrollPane.setViewportView(treeImage2D);
	} 
	private String getCollectionName(Image2D img) {
		return img.getSettImage().getRAWFileName()+", "+img.getSettImage().getRAWFilepath();
	} 
	
}
