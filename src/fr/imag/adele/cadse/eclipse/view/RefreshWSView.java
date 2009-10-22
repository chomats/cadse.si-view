/**
 * 
 */
package fr.imag.adele.cadse.eclipse.view;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.jface.viewers.TreeViewer;

import fr.imag.adele.cadse.core.IItemNode;
import fede.workspace.tool.view.ItemInViewer;
import fede.workspace.tool.view.node.AbstractCadseViewNode;

public final class RefreshWSView implements Runnable {
		private final Collection<AbstractCadseViewNode> structUpdateItems;
		private final Collection<AbstractCadseViewNode> updateItems;
		private final TreeViewer fTreeViewer;
		private final boolean recursifUpdate;
		
		public RefreshWSView(TreeViewer fTreeViewer, 
				AbstractCadseViewNode structUpdateItem) {
			this(fTreeViewer,Collections.singleton(structUpdateItem), Collections.EMPTY_LIST, false);
		}
		
		public RefreshWSView(TreeViewer fTreeViewer, 
				Collection<AbstractCadseViewNode> structUpdateItems, Collection<AbstractCadseViewNode> updateItems, boolean recursifUpdate) {
			this.structUpdateItems = structUpdateItems;
			this.fTreeViewer = fTreeViewer;
			this.updateItems = updateItems;
			this.recursifUpdate = recursifUpdate;
		}

		public void run() {
			if (updateItems.size() != 0) {
				if (recursifUpdate) {
					for (ItemInViewer iiv : updateItems) {
						udpateLocal(iiv);
					}
				} else
					fTreeViewer.update(updateItems.toArray(), null);
				
			}
			
			for (AbstractCadseViewNode iiv : structUpdateItems) {
				iiv.recomputeChildren();
				fTreeViewer.refresh(iiv,true);
			}
		}
		
		private void udpateLocal(final IItemNode iiv) {
			fTreeViewer.update(iiv,null);
			if (iiv.isOpen()) 
				for (IItemNode childIIv : iiv.getChildren()) {
					udpateLocal(childIIv);
				}
		}
	}