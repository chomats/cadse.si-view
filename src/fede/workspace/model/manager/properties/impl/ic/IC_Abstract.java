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
package fede.workspace.model.manager.properties.impl.ic;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.CompactUUID;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.attribute.IAttributeType;
import fr.imag.adele.cadse.core.impl.internal.AbstractGeneratedItem;
import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.ui.IFieldDescription;
import fr.imag.adele.cadse.core.ui.IInteractionController;
import fr.imag.adele.cadse.core.ui.IModelController;
import fr.imag.adele.cadse.core.ui.UIField;

public class IC_Abstract extends AbstractGeneratedItem implements IInteractionController {
	

	public IC_Abstract(CompactUUID id) {
		super(id);
	}

	public IC_Abstract() {
	}

	@Override
	public String getName() {
		return "ic";
	}

	public void setUIField(UIField ui) {
		this._parent = ui;
	}

	public IModelController getModelController() {
		return ((UIField) _parent).getModelController();
	}

	public UIField getUIField() {
		return ((UIField) _parent);
	}

	public void dispose() {
	}

	public void init() throws CadseException {
	}

	public void initAfterUI() {
	}

	public Item getItem() {
		return (Item) ((UIField) _parent).getContext();
	}

	public Item getParentItem() {
		return (Item) ((UIField) _parent).get(IFieldDescription.PARENT_CONTEXT);
	}

	public void setParent(Item parent, LinkType lt) {
		 _parent = (UIField) parent;
	}

	@Override
	public <T> T internalGetOwnerAttribute(IAttributeType<T> type) {
		if (CadseGCST.ITEM_at_NAME_ == type) {
			return (T) "ic";
		}
		return super.internalGetOwnerAttribute(type);
	}
	
	@Override
	public ItemType getType() {
		return CadseGCST.INTERACTION_CONTROLLER;
	}

}
