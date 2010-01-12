package fede.workspace.tool.view.decorator;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;

import fr.imag.adele.cadse.core.delta.ItemDelta;
import fede.workspace.tool.view.WSPlugin;

public class WorkspaceCopyLabelDecorator implements ILightweightLabelDecorator {

	public void addListener(ILabelProviderListener listener) {
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object element, String property) {
		return true;
	}

	public void removeListener(ILabelProviderListener listener) {
	}

	public void decorate(Object element, IDecoration decoration) {
		if (element instanceof ItemDelta) {
			if (((ItemDelta) element).isDeleted()) {
				ImageDescriptor overlay = WSPlugin.getImageDescriptor("icons/delete_ovr.gif");
				decoration.addOverlay(overlay, IDecoration.BOTTOM_LEFT);
			}

			if (((ItemDelta) element).isAdded()) {
				ImageDescriptor overlay = WSPlugin.getImageDescriptor("icons/add_ovr.gif");
				decoration.addOverlay(overlay, IDecoration.BOTTOM_LEFT);
			}
		}
	}

}
