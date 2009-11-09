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
package fede.workspace.model.manager.properties.impl;

import java.util.Collection;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.ChangeID;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.delta.ImmutableWorkspaceDelta;
import fr.imag.adele.cadse.core.impl.CadseIllegalArgumentException;
import fr.imag.adele.cadse.core.impl.ui.AbstractModelController;
import fr.imag.adele.cadse.core.impl.ui.mc.ItemLinkTypeWorkspaceListener;
import fr.imag.adele.cadse.core.ui.UIPlatform;
import fr.imag.adele.cadse.core.ui.RunningModelController;
import fr.imag.adele.cadse.core.ui.UIField;
import fr.imag.adele.fede.workspace.si.view.View;

public class ArrayOfItemFromLinkModelController extends AbstractModelController implements RunningModelController {

	private Item							item;
	private LinkType						lt;
	private ItemLinkTypeWorkspaceListener	l;

	public ArrayOfItemFromLinkModelController(LinkType lt) {
		this.lt = lt;
	}

	@Override
	public void init(UIPlatform uiPlatform) {
		super.init(uiPlatform);
		item = getItem();
		if (item == null) {
			throw new CadseIllegalArgumentException("No item in the context.");
		}

		if (lt.getSource() == item.getType()) {
			throw new CadseIllegalArgumentException("The link type {0} in the item type {1} is bad.",
					lt.getName(), item.getType().getId());
		}

		this.l = new ItemLinkTypeWorkspaceListener(_uiPlatform, item, getUIField(), lt);
		item.addListener(l, ChangeID.toFilter(ChangeID.CREATE_OUTGOING_LINK, ChangeID.DELETE_OUTGOING_LINK,
				ChangeID.UNRESOLVE_INCOMING_LINK));
	}

	@Override
	public void dispose() {
		item.removeListener(l);
	}

	public Object getValue() {
		Collection<Item> ret = item.getOutgoingItems(lt, true);

		if (lt.getMax() == 1) {
			return ret.size() >= 1 ? ret.toArray()[0] : null;
		}
		return ret.toArray();
	}

	public void notifieValueChanged(UIField field, Object value) {
		throw new UnsupportedOperationException();
	}

	public void notifieChangeEvent(ChangeID id, Object... values) {
		ImmutableWorkspaceDelta wd = (ImmutableWorkspaceDelta) values[0];

	}

	@Override
	public void notifieSubValueAdded(UIField field, Object added) {
		try {
			if (added instanceof Object[]) {
				Object[] arrayadded = (Object[]) added;
				View.getInstance().getWorkspaceDomain().beginOperation("createLinks");
				try {
					for (int i = 0; i < arrayadded.length; i++) {
						Link l = item.getOutgoingLink(lt, ((Item) arrayadded[i]).getId());
						if (l == null) {
							item.createLink(lt, (Item) arrayadded[i]);
						}
					}
				} finally {
					View.getInstance().getWorkspaceDomain().endOperation();
				}
			} else {
				item.createLink(lt, (Item) added);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void notifieSubValueRemoved(UIField field, Object removed) {
		if (removed instanceof Object[]) {
			Object[] arrayremoved = (Object[]) removed;
			for (int i = 0; i < arrayremoved.length; i++) {
				Link l = item.getOutgoingLink(lt, ((Item) arrayremoved[i]).getId());
				if (l != null) {
					try {
						l.delete();
					} catch (CadseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} else {
			Link l = item.getOutgoingLink(lt, ((Item) removed).getId());
			if (l != null) {
				try {
					l.delete();
				} catch (CadseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public ItemType getType() {
		// TODO Auto-generated method stub
		return null;
	}

}
