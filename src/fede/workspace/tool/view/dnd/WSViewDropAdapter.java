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
package fede.workspace.tool.view.dnd;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Tree;

import fede.workspace.tool.view.ItemInViewer;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.delta.LinkDelta;
import fr.imag.adele.cadse.core.transaction.LogicalWorkspaceTransaction;

/**
 * Supports dropping gadgets into a table viewer.
 */
public class WSViewDropAdapter extends ViewerDropAdapter {

	private Point	pt;

	public WSViewDropAdapter(TreeViewer viewer) {
		super(viewer);
	}

	@Override
	public void drop(DropTargetEvent event) {
		pt = new Point(event.x, event.y);
		super.drop(event);
	}

	/**
	 * Method declared on ViewerDropAdapter
	 */
	@Override
	public boolean performDrop(Object data) {

		ItemInViewer[] toDrop = (ItemInViewer[]) data;
		if (toDrop == null) {
			return false;
		}
		ItemInViewer target = (ItemInViewer) getCurrentTarget();
		if (target == null) {
			return false;
		}
		Item item = target.getItem();
		if (item == null) {
			return false;
		}
		int oper = getCurrentOperation();

		if (oper == DND.DROP_LINK) {
			if (toDrop.length != 1) {
				return false;
			}
			Item sourceItem = toDrop[0].getItem();
			if (sourceItem == null) {
				return false;
			}
			Item targetItem = item;
			ItemType itSource = sourceItem.getType();
			ItemType itTarget = targetItem.getType();

			ArrayList<LinkType> selectingLinkType = new ArrayList<LinkType>();
			List<LinkType> outgoingLinkType = itSource.getOutgoingLinkTypes();
			for (LinkType lt : outgoingLinkType) {
				if (lt.getDestination() == itTarget || lt.getDestination().isSuperTypeOf(itTarget)) {
					selectingLinkType.add(lt);
				}
			}
			if (selectingLinkType.size() == 0) {
				return false;
			}
			LinkType selectedLinkType = null;
			if (selectingLinkType.size() == 1) {
				selectedLinkType = selectingLinkType.get(0);
			} else {
				ShowLinkType slt = new ShowLinkType((Tree) getViewer().getControl(), selectingLinkType, pt);
				int index = slt.getSelectedIndex();
				if (index < 0 || index >= selectingLinkType.size()) {
					return false;
				}
				selectedLinkType = selectingLinkType.get(index);
			}
			createLink(sourceItem, targetItem, selectedLinkType);
			return true;

		}
		System.out.println("Operation :" + oper);

		for (int i = 0; i < toDrop.length; i++) {
			if (getCurrentLocation() == LOCATION_BEFORE | getCurrentLocation() == LOCATION_AFTER) {
				if (target.getKind() == ItemInViewer.LINK_OUTGOING && toDrop[i].getKind() == ItemInViewer.LINK_OUTGOING) {
					Link ltarget = target.getLink();
					Link lsource = toDrop[i].getLink();

					if (ltarget.getSource() == lsource.getSource() && ltarget.getLinkType() == lsource.getLinkType()
							&& ltarget.getDestinationId() != lsource.getDestinationId()) {
						// is a move or
						// System.err.println("trace move (before :
						// "+(getCurrentLocation() ==
						// LOCATION_BEFORE)+")"+lsource+ "\n "+ltarget);
						try {
							LogicalWorkspaceTransaction t = lsource.getSource().getLogicalWorkspace()
									.createTransaction();
							final LinkDelta linkDelta = t.getLink(lsource);
							if (getCurrentLocation() == LOCATION_BEFORE) {
								linkDelta.moveBefore(ltarget);
							} else {
								linkDelta.moveAfter(ltarget);
							}
							t.commit();

						} catch (CadseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// refresh
						target.getParent().close();
						target.getParent().open();
						getViewer().refresh();
						continue;
					}
				}
			} else if (getCurrentLocation() == LOCATION_ON) {
				Item itemToDrop = toDrop[i].getItem();

				LogicalWorkspaceTransaction t = itemToDrop.getLogicalWorkspace().createTransaction();
				try {
					t.getItem(itemToDrop.getId()).migratePartLink(item, null);
					t.commit();
				} catch (CadseException e) {
					e.printStackTrace();
					t.rollback();
				}

			}
		}
		// all gadgets in a table are children of the root
		// Gadget parent = (Gadget)getViewer().getInput();
		// Gadget[] toDrop = (Gadget[])data;
		// for (int i = 0; i < toDrop.length; i++) {
		// //get the flat list of all gadgets in this tree
		// Gadget[] flatList = toDrop[i].flatten();
		// for (int j = 0; j < flatList.length; j++) {
		// flatList[j].setParent(parent);
		// }
		// ((TableViewer)getViewer()).add(flatList);
		// }
		return true;
	}

	private void createLink(Item sourceItem, Item targetItem, LinkType selectedLinkType) {
		try {
			sourceItem.createLink(selectedLinkType, targetItem);
		} catch (CadseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Method declared on ViewerDropAdapter
	 */
	@Override
	public boolean validateDrop(Object target, int op, TransferData type) {
		return ItemTransfer.getInstance().isSupportedType(type);
	}
}
