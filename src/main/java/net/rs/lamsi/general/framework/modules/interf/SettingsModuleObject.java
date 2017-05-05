package net.rs.lamsi.general.framework.modules.interf;

import net.rs.lamsi.general.datamodel.image.interf.Collectable2D;

/**
 * is added to a Heatmap or SettingsModule 
 * T - Collectable2D, Image2D, ...
 * @author r_schm33
 *
 * @param <S>
 */
public interface SettingsModuleObject<S> {
	
	public void setCurrentImage(S img);
	
	public S getCurrentImage();
}
