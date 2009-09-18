/**
 *
 */
package fede.workspace.model.manager.properties.impl.mc;

import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.WorkspaceListener;
import fr.imag.adele.cadse.core.delta.ImmutableItemDelta;
import fr.imag.adele.cadse.core.delta.ImmutableWorkspaceDelta;
import fr.imag.adele.cadse.core.ui.UIField;

public class ItemLinkTypeWorkspaceListener extends WorkspaceListener {
	Item		item;
	UIField		uifield;
	LinkType	lt;

	public ItemLinkTypeWorkspaceListener(Item item, UIField uifield, LinkType lt) {
		super();
		this.item = item;
		this.uifield = uifield;
		this.lt = lt;
	}

	@Override
	public void workspaceChanged(ImmutableWorkspaceDelta delta) {
		ImmutableItemDelta _id = delta.getItem(item);
		if (_id != null) {
			if (_id.hasAddedOutgoingLink()) {
				for (Link l : _id.getLinksAdded()) {
					if (l.getLinkType().equals(lt)) {
						uifield.thisFieldHasChanged();
						return;
					}
				}
			}
			if (_id.hasRemovedOutgoingLink()) {
				for (Link l : _id.getLinksRemoved()) {
					if (l.getLinkType().equals(lt)) {
						uifield.thisFieldHasChanged();
						return;
					}
				}
			}
		}
	}

}