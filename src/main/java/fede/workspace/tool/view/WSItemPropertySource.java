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
package fede.workspace.tool.view;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource2;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.Item;




public class WSItemPropertySource implements IPropertySource2 {
	//IAdaptable id;
	Item item;
	
	public WSItemPropertySource(Item item) {
		this.item = item;
	}
	
	private IPropertyDescriptor[] propertyDescriptors;
	 
	public boolean isPropertyResettable(Object id) {
		return true;
	}

	public boolean isPropertySet(Object id) {
		return true;
	}

	public Object getEditableValue() {
		return this;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		//IItemManager ip = WSPlugin.getManager(this.item.getType());
		propertyDescriptors = null;
//		if (ip != null)
//			propertyDescriptors = ip.getProperties(item);
		if (propertyDescriptors == null)
			propertyDescriptors = new IPropertyDescriptor[0];
		return propertyDescriptors;
    }
	

	public Object getPropertyValue(Object id) {
		return NotNull(item.getAttribute((String)id));
	}

	private Object NotNull(Object attribute) {
		return attribute!=null?attribute:"";
	}

	public void resetPropertyValue(Object id) {
		try {
			item.setAttribute((String)id,"");
		} catch (CadseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setPropertyValue(Object id, Object value) {
		try {
			item.setAttribute((String)id,value);
		} catch (CadseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}


