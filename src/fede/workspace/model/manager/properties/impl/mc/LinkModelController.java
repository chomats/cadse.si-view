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
package fede.workspace.model.manager.properties.impl.mc;

import java.util.List;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.ChangeID;
import fr.imag.adele.cadse.core.CompactUUID;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.attribute.IAttributeType;
import fr.imag.adele.cadse.core.impl.CadseCore;
import fr.imag.adele.cadse.core.impl.CadseIllegalArgumentException;
import fr.imag.adele.cadse.core.impl.ui.AbstractModelController;
import fr.imag.adele.cadse.core.oper.WSODeleteLink;
import fr.imag.adele.cadse.core.ui.IModelController;
import fr.imag.adele.cadse.core.ui.UIField;
import fr.imag.adele.cadse.core.util.Convert;

public class LinkModelController extends AbstractModelController implements IModelController {

	protected Item					item;
	protected LinkType				lt;
	private boolean					mandatory	= false;
	private String					msg			= null;
	boolean							init;
	ItemLinkTypeWorkspaceListener	mListener;

	public LinkModelController(boolean mandatory, String msg, LinkType lt) {
		this.mandatory = mandatory;
		this.msg = msg;
		this.lt = lt;
		init = false;
	}

	public LinkModelController(CompactUUID id) {
		super(id);
		init = true;
	}

	@Override
	public void init() throws CadseException {
		super.init();
		item = getItem();
		if (item == null) {
			throw new CadseIllegalArgumentException("No item in the context.");
		}
		if (init) {
			IAttributeType<?> attRef = getUIField().getAttributeDefinition();
			if (attRef == null) {
				throw new CadseIllegalArgumentException("Cannot find the link type {0} in the item type {1}.",
						getUIField().getAttributeName(), item.getType().getName());
			}
			if (attRef.getType() != CadseGCST.LINK) {
				throw new CadseIllegalArgumentException("The attribute {0} in the item type {1} is not a link type.",
						getUIField().getAttributeName(), item.getType().getName());
			}
			lt = (LinkType) attRef;
			mandatory = lt.mustBeInitializedAtCreationTime();
		}
		if (!item.isInstanceOf(lt.getSource())) {
			throw new CadseIllegalArgumentException("The link type {0} in the item type {1} is bad.", getUIField()
					.getAttributeName(), item.getType().getName());
		}

		// removed old api
		// item.getWorkspaceDomain().addListener(this);
		mListener = new ItemLinkTypeWorkspaceListener(item, getUIField(), lt);
		item.addListener(mListener, ChangeID.CREATE_OUTGOING_LINK.ordinal() + ChangeID.ORDER_OUTGOING_LINK.ordinal()
				+ ChangeID.DELETE_OUTGOING_LINK.ordinal());
	}

	@Override
	public void initAfterUI() {
		if (lt.isPart()) {
			getUIField().setEnabled(false);
		}
	}

	@Override
	public void dispose() {
		item.removeListener(mListener);
	}

	public Object getValue() {
		List<Link> ret = item.getOutgoingLinks(lt);

		if (lt.getMax() == 1) {
			return ret.size() >= 1 ? ret.get(0) : null;
		}
		return ret;
	}

	public void notifieValueChanged(UIField field, Object value) {
		// do nothing...
	}

	@Override
	public boolean validValueChanged(UIField field, Object value) {
		if (mandatory && value == null) {
			if (msg != null) {
				setMessageError(msg);
			} else {
				setMessageError("The link " + lt.getName() + " must be set");
			}
			return true;
		}
		return super.validValueChanged(field, value);
	}

	@Override
	public void notifieValueDeleted(UIField field, Object oldvalue) {
		if (oldvalue instanceof Link) {
			Link l = (Link) oldvalue;
			WSODeleteLink oper = new WSODeleteLink(l);
			oper.execute();
			CadseCore.registerInTestIfNeed(oper);
		}
	}

	public ItemType getType() {
		return CadseGCST.LINK_MODEL_CONTROLLER;
	}

	@Override
	public <T> T internalGetOwnerAttribute(IAttributeType<T> type) {
		if (CadseGCST.LINK_MODEL_CONTROLLER_at_ERROR_MESSAGE_ == type) {
			return (T) msg;
		}
		return super.internalGetOwnerAttribute(type);
	}

	@Override
	public boolean commitSetAttribute(IAttributeType<?> type, String key, Object value) {
		if (CadseGCST.LINK_MODEL_CONTROLLER_at_ERROR_MESSAGE_ == type) {
			msg = Convert.toString(value);
			return true;
		}
		return super.commitSetAttribute(type, key, value);
	}

}
