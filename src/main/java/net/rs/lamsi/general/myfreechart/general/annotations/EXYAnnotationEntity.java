package net.rs.lamsi.general.myfreechart.general.annotations;

import java.awt.Shape;
import java.io.Serializable;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.entity.XYAnnotationEntity;

public class EXYAnnotationEntity extends XYAnnotationEntity implements Serializable {

  private static final long serialVersionUID = 1L;
  private XYAnnotation ann;


  public EXYAnnotationEntity(XYAnnotation ann, Shape hotspot, int rendererIndex, String toolTipText,
      String urlText) {
    super(hotspot, rendererIndex, toolTipText, urlText);
    this.ann = ann;
  }

  public XYAnnotation getAnnotation() {
    return ann;
  }
}
