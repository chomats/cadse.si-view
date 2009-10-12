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

import fede.workspace.model.manager.Messages;
import fede.workspace.tool.view.WSPlugin;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.CompactUUID;
import fr.imag.adele.cadse.core.IItemManager;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemState;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.impl.CadseCore;
import fr.imag.adele.cadse.core.impl.ui.MC_AttributesItem;
import fr.imag.adele.cadse.core.ui.UIField;

/**
 * MC controller to valid the name field.
 */

public class MC_ShortNameItemProperty extends MC_AttributesItem {

	private static final String	EMPTY_STRING	= "";

	public MC_ShortNameItemProperty() {
	}

	public MC_ShortNameItemProperty(CompactUUID id) {
		super(id);
	}

	@Override
	public void initAfterUI() {
		super.initAfterUI();
		Item item = getItem();
		getUIField().setEditable(isEditable(item));
	}

	private boolean isEditable(Item item) {
		return item.getState() == ItemState.NOT_IN_WORKSPACE;
	}

	@Override
	public Object getValue() {
		String shortId = EMPTY_STRING;
		Item item = getItem();
		try {
			shortId = item.getName();
			if (shortId == Item.NO_VALUE_STRING) {
				shortId = EMPTY_STRING;
			}
		} catch (Throwable e) {
		}
		return shortId;
	}

	@Override
	public void notifieValueChanged(UIField field, Object value) {
		Item item = getItem();

		try {
			if (isEditable(item)) {
				item.setName((String) value);
			}
		} catch (CadseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean validValue(UIField field, Object value) {
		Item item = getItem();
		if (!isEditable(item)) {
			return false;
		}

		final String shortId = getItem().getName();

		if (shortId.length() == 0 && item.getType().getSpaceKeyType() != null) {
			setMessageError(Messages.mc_name_must_be_specified);
			return true;
		}

		IItemManager im = item.getType().getItemManager();
		String message = im.validateShortName(item, shortId);
		if (message != null) {
			setMessageError(message);
			return true;
		}

		return super.validValue(field, value);
	}

	@Override
	public boolean validValueChanged(UIField field, Object value) {
		Item item = getItem();
		if (!isEditable(item)) {
			return false;
		}
		final String shortId = (String) value;

		if (item.getState() != ItemState.NOT_IN_WORKSPACE) {
			return false;
		}
		
		if (item.isReadOnly()) {
			return false;
		}

		if (shortId.length() == 0) { // && item.getType().getSpaceKeyType()
			setMessageError(Messages.mc_name_must_be_specified);
			return true;
		}

		IItemManager im = item.getType().getItemManager();
		String message = im.validateShortName(item, shortId);
		if (message != null) {
			setMessageError(message);
			return true;
		}

		try {
			item.setName(shortId);
		} catch (CadseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			setMessageError(Messages.bind(Messages.mc_cannot_set_name, e1.getMessage()));
			return true;
		}
		if (item.getType().hasQualifiedNameAttribute()) {
			String un = im.computeQualifiedName(item, shortId, item.getPartParent(), item.getPartParentLinkType());
			try {
				item.setQualifiedName(un);
			} catch (CadseException e) {
				WSPlugin.logException(e);
				setMessageError(e.getMessage());
				return true;
			}
		}
		if (item.getLogicalWorkspace().existsItem(item)) {
			setMessageError(Messages.mc_name_already_exists);
			return true;
		}

		return false;
	}

	@Override
	public void notifieValueDeleted(UIField field, Object oldvalue) {
	}

	@Override
	public boolean validValueDeleted(UIField field, Object removed) {
		return validValueChanged(field, EMPTY_STRING);
	}

	@Override
	public Object defaultValue() {
		return EMPTY_STRING;
	}

	@Override
	public ItemType getType() {
		return CadseGCST.MC_NAME_ATTRIBUTE;
	}

}