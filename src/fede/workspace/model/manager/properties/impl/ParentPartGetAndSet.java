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

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemState;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.impl.ui.AbstractModelController;
import fr.imag.adele.cadse.core.ui.RunningModelController;
import fr.imag.adele.cadse.core.ui.UIField;

public class ParentPartGetAndSet extends AbstractModelController implements RunningModelController {

	public Object getValue() {
		Item item = getItem();
		Item parentPart = item.getPartParent();
		return parentPart;
	}

	@Override
	public void init() throws CadseException {
		super.init();
		Item curentItem = getItem();
		if (curentItem.getState() == ItemState.NOT_IN_WORKSPACE) {
			getUIField().setEnabled(false);
		}
	}

	public void notifieValueChanged(UIField field, Object value) {
	}

	public ItemType getType() {
		// TODO Auto-generated method stub
		return null;
	}
}
