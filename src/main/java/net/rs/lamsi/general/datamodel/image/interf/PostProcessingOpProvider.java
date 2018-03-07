package net.rs.lamsi.general.datamodel.image.interf;

import java.util.List;
import net.rs.lamsi.general.heatmap.dataoperations.PostProcessingOp;

public interface PostProcessingOpProvider {

  public List<PostProcessingOp> getPostProcessingOp();
}
