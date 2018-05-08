package net.rs.lamsi.utils;

import java.awt.Component;
import java.util.Vector;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WindowStyleUtil {
  public static final int STYLE_SYSTEM = 0, STYLE_JAVA = 1, STYLE_NIMBUS = 2;
  private final static Logger logger = LoggerFactory.getLogger(WindowStyleUtil.class);

  private static Vector<Component> frames = new Vector<Component>();

  public static void registerComponent(Component frame) {
    frames.addElement(frame);
  }

  public static void changeWindowStyleOfAll(int style) {
    for (Component c : frames)
      changeWindowStyle(c, style);
  }

  public static void changeWindowStyle(Component frame, int style) {
    try {
      switch (style) {
        case STYLE_SYSTEM:
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
          break;
        case STYLE_JAVA:
          UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
          break;
        case STYLE_NIMBUS:
          UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
          break;
      }
      if (frame != null) {
        // update
        SwingUtilities.updateComponentTreeUI(frame);
        // frame.pack();
      }
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
        | UnsupportedLookAndFeelException e) {
      // TODO Auto-generated catch block
      logger.error("", e);
    }
  }
}
