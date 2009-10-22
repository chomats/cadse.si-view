/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
/**
 *
 */
package fr.imag.adele.cadse.eclipse.view;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.PlatformUI;

import fede.workspace.tool.view.node.AbstractCadseViewNode;
import fr.imag.adele.cadse.core.ChangeID;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.WSModelState;
import fr.imag.adele.cadse.core.WorkspaceListener;
import fr.imag.adele.cadse.core.delta.ImmutableItemDelta;
import fr.imag.adele.cadse.core.delta.ImmutableWorkspaceDelta;
import fr.imag.adele.cadse.core.impl.CadseIllegalArgumentException;
import fr.imag.adele.fede.workspace.si.view.View;

public class SelfViewContentProvider implements ITreeContentProvider {
	private AbstractCadseViewNode	rootWS;

	TreeViewer						_viewer;

	private WorkspaceListener		_listener;

	public SelfViewContentProvider() {
	}

	public void dispose() {
		if (_listener != null) {
			View.getInstance().getWorkspaceLogique().removeListener(_listener);
		}
	}

	public Object[] getElements(Object parent) {
		if (parent instanceof AbstractCadseViewNode) {
			AbstractCadseViewNode isrt = (AbstractCadseViewNode) parent;
			return isrt.getChildren();
		}
		return getChildren(parent);
	}

	public Object[] getChildren(Object parent) {

		if (parent instanceof AbstractCadseViewNode) {
			return ((AbstractCadseViewNode) parent).getChildren();
		}

		return new Object[0];
	}

	public Object getParent(Object child) {
		if (child instanceof AbstractCadseViewNode) {
			return ((AbstractCadseViewNode) child).getParent();
		}
		return null;
	}

	public boolean hasChildren(Object parent) {
		return getChildren(parent).length != 0;
	}

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		rootWS = (AbstractCadseViewNode) newInput;
		_viewer = (TreeViewer) v;
		_listener = new WorkspaceListener() {
			@Override
			public void workspaceChanged(final ImmutableWorkspaceDelta wd) {
				if (rootWS == null) {
					return;
				}
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

					public void run() {
						Set<Item> refreshStruct = new HashSet<Item>();
						Set<Item> refreshUpdate = new HashSet<Item>();

						if (wd.currentModelHasState(WSModelState.RUN)) {
							refreshAll();
							return;
						}
						for (ImmutableItemDelta itemDelta : wd.getItems()) {
							if (!itemDelta.getItem().isResolved()) {
								continue;
							}
							if (rootWS == null) {
								return;
							}
							ItemType it = itemDelta.getItem().getType();
							if (itemDelta.isCreated() || itemDelta.isDeleted()) {
								rootWS.recomputeChildren();
							}
							if (!isItemType(it)) {
								continue;
							}

							if (itemDelta.hasResolvedOutgoingLink() || itemDelta.hasUnresolvedOutgoingLink()
									|| itemDelta.hasAddedOutgoingLink() || itemDelta.hasRemovedOutgoingLink()) {
								recomputeChildren(itemDelta.getItem());
								refreshStruct.add(itemDelta.getItem());
								continue;
							}
							if (itemDelta.hasSetAttributes()) {
								refreshUpdate.add(itemDelta.getItem());
							}
						}

					}
				});
			}
		};
		View.getInstance().getWorkspaceLogique().addListener(
				_listener,
				ChangeID.toFilter(ChangeID.CREATE_ITEM, ChangeID.DELETE_ITEM, ChangeID.CREATE_OUTGOING_LINK,
						ChangeID.DELETE_OUTGOING_LINK, ChangeID.SET_ATTRIBUTE, ChangeID.MODEL_STATE));
	}

	public void refreshAll() {
		if (rootWS != null) {
			rootWS.recomputeChildren();
			// try sans updateTree();
		}
	}

	public void refresh(final AbstractCadseViewNode iiv) {
		if (iiv == null) {
			throw new CadseIllegalArgumentException("The item is null!!!");
		}
		PlatformUI.getWorkbench().getDisplay().asyncExec(new RefreshWSView(_viewer, iiv));
	}

	public void updateTree() {
		PlatformUI.getWorkbench().getDisplay().asyncExec(
				new RefreshWSView(_viewer, Collections.EMPTY_LIST, Collections.singleton(rootWS), true));

	}

	public void notifieChangeEvent(ChangeID id, final Object... values) {

	}

	private void recomputeChildren(Item item) {
		if (rootWS == null) {
			return;
		}
		recomputeChildren(item, rootWS);
	}

	private void recomputeChildren(Item item, AbstractCadseViewNode current) {
		Item currentItem = current.getItem();
		if (currentItem != null && item.equals(currentItem)) {
			current.recomputeChildren();
		}
		if (current.isOpen() && current.hasChildren()) {
			for (AbstractCadseViewNode childIIV : current.getChildren()) {
				recomputeChildren(item, childIIV);
			}
		}
	}

	public boolean isItemType(ItemType it) {
		return true;
	}

}