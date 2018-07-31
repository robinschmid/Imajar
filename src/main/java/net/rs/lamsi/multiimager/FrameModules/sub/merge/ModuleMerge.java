package net.rs.lamsi.multiimager.FrameModules.sub.merge;


import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rs.lamsi.general.datamodel.image.ImageMerge;
import net.rs.lamsi.general.framework.listener.ColorChangedListener;
import net.rs.lamsi.general.framework.modules.Collectable2DSettingsModule;
import net.rs.lamsi.general.settings.image.SettingsImageMerge;
import net.rs.lamsi.multiimager.Frames.ImageLogicRunner;

public class ModuleMerge extends Collectable2DSettingsModule<SettingsImageMerge, ImageMerge> {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private JTable table;
  //
  private MergeTableModel tableModel;
  private ActionListener repaintListener;

  /**
   * Create the panel.
   */
  public ModuleMerge() {
    super("Merge", false, SettingsImageMerge.class, ImageMerge.class);
    getLbTitle().setText("Merge");

    JPanel panel = new JPanel();
    getPnContent().add(panel, BorderLayout.CENTER);
    panel.setLayout(new BorderLayout(0, 0));

    JScrollPane scrollPane = new JScrollPane();
    panel.add(scrollPane, BorderLayout.CENTER);


    tableModel = new MergeTableModel();
    table = new JTable(tableModel);
    scrollPane.setViewportView(table);

    JPanel panel_1 = new JPanel();
    getPnContent().add(panel_1, BorderLayout.NORTH);

    JButton btnApply = new JButton("Redraw");
    btnApply.addActionListener(e -> repaintChart());
    panel_1.add(btnApply);
  }

  // ################################################################################################
  // Autoupdate
  @Override
  public void addAutoupdater(ActionListener al, ChangeListener cl, DocumentListener dl,
      ColorChangedListener ccl, ItemListener il) {}

  @Override
  public void addAutoRepainter(ActionListener al, ChangeListener cl, DocumentListener dl,
      ColorChangedListener ccl, ItemListener il) {
    this.repaintListener = al;
    // getTxtYUpper().getDocument().addDocumentListener(dl);
    tableModel.addTableModelListener(e -> {
      // trigger repaint
      al.actionPerformed(null);
    });
  }

  // ################################################################################################
  // LOGIC
  // Paintsclae from Image
  @Override
  public void setAllViaExistingSettings(SettingsImageMerge si) {
    ImageLogicRunner.setIS_UPDATING(false);

    tableModel.setMergeSettings(si);

    // finished
    ImageLogicRunner.setIS_UPDATING(true);
    // ImageEditorWindow.getEditor().fireUpdateEvent(true);
  }

  @Override
  public SettingsImageMerge writeAllToSettings(SettingsImageMerge si) {
    if (si != null) {
      try {
        // automatically
      } catch (Exception ex) {
        logger.error("", ex);
      }
    }
    return si;
  }


  private void repaintChart() {
    repaintListener.actionPerformed(null);
  }
}
