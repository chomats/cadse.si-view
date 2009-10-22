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
package fede.workspace.model.manager.properties.impl.ic;

import org.eclipse.swt.graphics.Image;

import fr.imag.adele.cadse.core.Item;
import fede.workspace.model.manager.properties.IC_ForCheckedViewer;
import fede.workspace.tool.view.WSPlugin;

public abstract class IC_AbstractForChecked  extends IC_Abstract implements IC_ForCheckedViewer {
	

	public Image toImageFromObject(Object obj) {
		Item item = (Item) obj;
		return WSPlugin.getDefault().getImageFrom(item.getType(), item);
	}

	public String toStringFromObject(Object obj) {
		Item item = (Item) obj;
		return WSPlugin.getManager(item).getDisplayName(item);
	}

	
	
	public void edit(Object o) {
	}

	public void select(Object data) {
	}


}