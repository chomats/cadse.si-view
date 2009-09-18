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
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.attribute.IAttributeType;
import fr.imag.adele.cadse.core.impl.internal.AbstractGeneratedItem;
import fr.imag.adele.cadse.core.impl.ui.UIFieldImpl;
import fr.imag.adele.cadse.core.CadseRootCST;
import fr.imag.adele.cadse.core.ui.IFieldDescription;
import fr.imag.adele.cadse.core.ui.IInteractionController;
import fr.imag.adele.cadse.core.ui.IModelController;
import fr.imag.adele.cadse.core.ui.UIField;

public abstract class IC_Abstract extends AbstractGeneratedItem implements IInteractionController {
	protected UIField	ui;

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
		this.ui = ui;
	}

	public IModelController getModelController() {
		return ui.getModelController();
	}

	public UIField getUIField() {
		return ui;
	}

	public void dispose() {
	}

	public void init() throws CadseException {
	}

	public void initAfterUI() {
	}

	public Item getItem() {
		return (Item) ui.getContext();
	}

	public Item getParentItem() {
		return (Item) ui.get(IFieldDescription.PARENT_CONTEXT);
	}

	public void setParent(Item parent, LinkType lt) {
		ui = (UIField) parent;
	}

	@Override
	public <T> T internalGetOwnerAttribute(IAttributeType<T> type) {
		if (CadseRootCST.ITEM_TYPE_at_NAME_ == type) {
			return (T) "ic";
		}
		return super.internalGetOwnerAttribute(type);
	}

}
