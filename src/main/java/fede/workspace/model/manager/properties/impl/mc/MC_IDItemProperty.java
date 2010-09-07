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
 *
 * Copyright (C) 2006-2010 Adele Team/LIG/Grenoble University, France
 */
package fede.workspace.model.manager.properties.impl.mc;

import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.impl.ui.AbstractModelController;
import fr.imag.adele.cadse.core.ui.UIField;

/**
 * IValueController
 * Test
 * Attribute 
 * 		LinkType : incoming-link-type
 */

public class MC_IDItemProperty extends AbstractModelController  {
	
	

	public MC_IDItemProperty() {
	}
	
	@Override
	public void initAfterUI() {
		super.initAfterUI();
		getUIField().setEditable(false);
	}
	
	public Object getValue() {
		Item item = (Item) getItem();
		try {
		   return item.getId().toString();
		} catch (Throwable e) {
		}
		return "";
	}
	
	public void notifieValueChanged(UIField field, Object value) {
	}
		
	@Override
	public Object defaultValue() {
		return "";
	}

	public ItemType getType() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
