package net.rs.lamsi.multiimager.FrameModules;


import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.miginfocom.swing.MigLayout;
import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;
import net.rs.lamsi.general.framework.listener.ColorChangedListener;
import net.rs.lamsi.general.framework.modules.Collectable2DSettingsModule;
import net.rs.lamsi.general.heatmap.Heatmap;
import net.rs.lamsi.general.settings.image.sub.SettingsZoom;
import net.rs.lamsi.multiimager.Frames.ImageLogicRunner;

public class ModuleTemplate extends Collectable2DSettingsModule<SettingsZoom, Collectable2D> {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  //

  // AUTOGEN

  /**
   * Create the panel.
   */
  public ModuleTemplate() {
    super("Zoom", false, SettingsZoom.class, Collectable2D.class);
    getLbTitle().setText("Zoom");

    JPanel panel = new JPanel();
    getPnContent().add(panel, BorderLayout.CENTER);
    panel.setLayout(new MigLayout("", "[][grow][grow]", "[][][]"));

  }

  @Override
  public void setCurrentHeatmap(Heatmap heat) {
    super.setCurrentHeatmap(heat);
    // extract
    if (heat != null && getSettings() != null) {
      getSettings().setXrange(heat.getPlot().getDomainAxis().getRange());
      getSettings().setYrange(heat.getPlot().getRangeAxis().getRange());

      setAllViaExistingSettings(getSettings());
    }
  }


  // ################################################################################################
  // Autoupdate
  @Override
  public void addAutoupdater(ActionListener al, ChangeListener cl, DocumentListener dl,
      ColorChangedListener ccl, ItemListener il) {}

  @Override
  public void addAutoRepainter(ActionListener al, ChangeListener cl, DocumentListener dl,
      ColorChangedListener ccl, ItemListener il) {
    // getTxtXLower().getDocument().addDocumentListener(dl);
  }

  // ################################################################################################
  // LOGIC
  // Paintsclae from Image
  @Override
  public void setAllViaExistingSettings(SettingsZoom si) {
    ImageLogicRunner.setIS_UPDATING(false);
    // set all to panels


    // finished
    ImageLogicRunner.setIS_UPDATING(true);
    // ImageEditorWindow.getEditor().fireUpdateEvent(true);
  }

  @Override
  public SettingsZoom writeAllToSettings(SettingsZoom si) {
    if (si != null) {
      try {
        // set all to si


      } catch (Exception ex) {
        logger.error("", ex);
      }
    }
    return si;
  }

  // ################################################################################################
  // GETTERS AND SETTERS

}
