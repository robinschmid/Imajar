package net.rs.lamsi.utils;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.general.framework.modules.tree.IconNodeRenderer;
import net.rs.lamsi.utils.useful.mylists.ListAction;
import net.rs.lamsi.utils.useful.mylists.TreeAction;

public class DialogLoggerUtil {

  private final static Logger logger = LoggerFactory.getLogger(DialogLoggerUtil.class);

  /*
   * Dialogs
   */
  public static void showErrorDialog(Component parent, String message, Exception e) {
    JOptionPane.showMessageDialog(parent, message + " \n" + e.getMessage(), "ERROR",
        JOptionPane.ERROR_MESSAGE);
  }

  public static void showErrorDialog(Component parent, String title, String message) {
    JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
  }

  public static void showMessageDialog(Component parent, String title, String message) {
    JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
  }

  public static boolean showDialogYesNo(Component parent, String title, String text) {
    Object[] options = {"Yes", "No"};
    int n = JOptionPane.showOptionDialog(parent, text, title, JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
    return n == 0;
  }

  /**
   * shows a message dialog just for a few given milliseconds
   * 
   * @param parent
   * @param title
   * @param message
   * @param time
   */
  public static void showMessageDialogForTime(final JFrame parent, final String title,
      final String message, final long time) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        TimeDialog dialog = new TimeDialog(parent, time);
        dialog.setLayout(new FlowLayout(FlowLayout.LEFT));
        dialog.add(new JLabel(message));
        dialog.setTitle(title);
        dialog.pack();
        centerOnScreen(dialog, true);
        dialog.startDialog();
        // log in window
        logger.debug("Show dialog: {}", message);
      }
    });
  }


  public static int[] showListDialogAndChoose(JFrame parent, Vector<Object> list,
      int selectionMode) {
    ChooseFromListDialog dialog = new ChooseFromListDialog(parent, list, selectionMode, 0);
    return dialog.getSelected();
  }

  public static int[] showListDialogAndChoose(JFrame parent, Object[] list, int selectionMode) {
    ChooseFromListDialog dialog = new ChooseFromListDialog(parent, list, selectionMode, 0);
    return dialog.getSelected();
  }

  public static int[] showListDialogAndChoose(JFrame parent, Vector<Object> list, int selectionMode,
      int selectedi) {
    ChooseFromListDialog dialog = new ChooseFromListDialog(parent, list, selectionMode, selectedi);
    return dialog.getSelected();
  }

  public static int[] showListDialogAndChoose(JFrame parent, Object[] list, int selectionMode,
      int selectedi) {
    ChooseFromListDialog dialog = new ChooseFromListDialog(parent, list, selectionMode, selectedi);
    return dialog.getSelected();
  }

  /**
   * show tree dialog and choose
   * 
   * @param parent
   * @param list
   * @param selectionMode TreeSelectionModel
   * @param selectedi
   * @return
   */
  public static TreePath[] showTreeDialogAndChoose(Window parent, DefaultMutableTreeNode root,
      int selectionMode, TreePath[] selections, String title, String message) {
    ChooseFromTreeDialog dialog =
        new ChooseFromTreeDialog(parent, root, selectionMode, selections, title, message);
    return dialog.getSelected();
  }

  /**
   * show tree dialog and choose
   * 
   * @param parent
   * @param list
   * @param selectionMode TreeSelectionModel
   * @param selectedi
   * @return
   */
  public static TreePath[] showTreeDialogAndChoose(Window parent, JTree tree, int selectionMode,
      String title, String message) {
    ChooseFromTreeDialog dialog =
        new ChooseFromTreeDialog(parent, (DefaultMutableTreeNode) tree.getModel().getRoot(),
            selectionMode, tree.getSelectionPaths(), title, message);
    return dialog.getSelected();
  }

  /**
   * Center on screen ( abslute true/false (exact center or 25% upper left) )o
   * 
   * @param c
   * @param absolute
   */
  public static void centerOnScreen(final Component c, final boolean absolute) {
    final int width = c.getWidth();
    final int height = c.getHeight();
    final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (screenSize.width / 2) - (width / 2);
    int y = (screenSize.height / 2) - (height / 2);
    if (!absolute) {
      x /= 2;
      y /= 2;
    }
    c.setLocation(x, y);
  }

  /**
   * Center on parent ( absolute true/false (exact center or 25% upper left) )
   * 
   * @param child
   * @param absolute
   */
  public static void centerOnParent(final Window child, final boolean absolute) {
    child.pack();
    boolean useChildsOwner = child.getOwner() != null
        ? ((child.getOwner() instanceof JFrame) || (child.getOwner() instanceof JDialog))
        : false;
    final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    final Dimension parentSize = useChildsOwner ? child.getOwner().getSize() : screenSize;
    final Point parentLocationOnScreen =
        useChildsOwner ? child.getOwner().getLocationOnScreen() : new Point(0, 0);
    final Dimension childSize = child.getSize();
    childSize.width = Math.min(childSize.width, screenSize.width);
    childSize.height = Math.min(childSize.height, screenSize.height);
    child.setSize(childSize);
    int x;
    int y;
    if ((child.getOwner() != null) && child.getOwner().isShowing()) {
      x = (parentSize.width - childSize.width) / 2;
      y = (parentSize.height - childSize.height) / 2;
      x += parentLocationOnScreen.x;
      y += parentLocationOnScreen.y;
    } else {
      x = (screenSize.width - childSize.width) / 2;
      y = (screenSize.height - childSize.height) / 2;
    }
    if (!absolute) {
      x /= 2;
      y /= 2;
    }
    child.setLocation(x, y);
  }

  // ################################################################################################################
  // internal dialog classes
  private static class TimeDialog extends JDialog implements Runnable {
    long time;

    public TimeDialog(JFrame parent, long time) {
      super(parent);
      this.time = time;
    }

    @Override
    public void run() {
      try {
        Thread.sleep(time);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        logger.error("",e);
      } finally {
        this.setVisible(false);
        this.dispose();
      }
    }

    public void startDialog() {
      setVisible(true);
      new Thread(this).start();
    }
  }

  private static class ChooseFromListDialog extends JDialog {

    private int[] selected = null;

    public int[] getSelected() {
      return selected;
    }

    public void setSelected(int[] selected) {
      this.selected = selected;
    }

    public ChooseFromListDialog(JFrame parent, Object[] list, int selectionMode, int selectedi) {
      super(parent);
      // create List
      final JList jList = new JList(list);
      init(parent, jList, selectionMode, selectedi);
    }

    public ChooseFromListDialog(JFrame parent, Vector<Object> list, int selectionMode,
        int selectedi) {
      super(parent);
      // create List
      final JList jList = new JList(list);
      init(parent, jList, selectionMode, selectedi);
    }

    private void init(JFrame parent, final JList jList, int selectionMode, int selectedi) {
      getContentPane().setLayout(new BorderLayout());
      jList.setSelectionMode(selectionMode);
      jList.setSelectedIndex(selectedi);
      // add mouse listener
      ListAction la = new ListAction(jList, new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
          // end
          selected = jList.getSelectedIndices();
          setVisible(false);
          dispose();
        }
      });
      // put list on screen
      JScrollPane scroll = new JScrollPane(jList);
      getContentPane().add(scroll, BorderLayout.CENTER);
      //
      JPanel pn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      getContentPane().add(pn, BorderLayout.SOUTH);
      // btn
      JButton btnOK = new JButton("OK");
      btnOK.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          selected = jList.getSelectedIndices();
          setVisible(false);
          dispose();
        }
      });
      pn.add(btnOK);

      JButton btnCancel = new JButton("Cancel");
      btnCancel.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          setVisible(false);
          dispose();
        }
      });
      pn.add(btnCancel);

      setSize(200, 400);
      setModalityType(ModalityType.APPLICATION_MODAL);
      centerOnScreen(this, true);
      //
      setVisible(true);
      getContentPane().validate();
    }
  }

  private static class ChooseFromTreeDialog extends JDialog {

    private TreePath[] selected = null;


    public ChooseFromTreeDialog(Window parent, DefaultMutableTreeNode root, int selectionMode,
        TreePath[] selections, String title, String message) {
      super(parent);
      // create List
      final JTree tree = new JTree(root);
      tree.setCellRenderer(new IconNodeRenderer());
      init(tree, selectionMode, selections, title, message);
    }

    private void init(final JTree tree, int selectionMode, TreePath[] selections, String title,
        String message) {
      setTitle(title);

      getContentPane().setLayout(new BorderLayout());
      tree.getSelectionModel().setSelectionMode(selectionMode);
      tree.setSelectionPaths(selections);
      if (message != null && !message.isEmpty()) {
        // message
        getContentPane().add(new JTextArea(message), BorderLayout.NORTH);
      }

      // add mouse listener
      new TreeAction(tree, new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
          // end
          selected = tree.getSelectionPaths();
          setVisible(false);
          dispose();
        }
      });
      // put list on screen
      JScrollPane scroll = new JScrollPane(tree);
      getContentPane().add(scroll, BorderLayout.CENTER);
      //
      JPanel pn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      getContentPane().add(pn, BorderLayout.SOUTH);
      // btn
      JButton btnOK = new JButton("OK");
      btnOK.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          selected = tree.getSelectionPaths();
          setVisible(false);
          dispose();
        }
      });
      pn.add(btnOK);

      JButton btnCancel = new JButton("Cancel");
      btnCancel.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          setVisible(false);
          dispose();
        }
      });
      pn.add(btnCancel);

      setSize(200, 400);
      setModalityType(ModalityType.APPLICATION_MODAL);
      centerOnScreen(this, true);
      //
      setVisible(true);
      getContentPane().validate();
    }

    public TreePath[] getSelected() {
      return selected;
    }

    public void setSelected(TreePath[] selected) {
      this.selected = selected;
    }
  }
}
