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

import java.util.List;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;

import fr.imag.adele.cadse.core.CompactUUID;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.attribute.EnumAttributeType;
import fr.imag.adele.cadse.core.CadseGCST;

public class IC_EnumForList extends IC_AbstractForList  {

	private Class<?> values;
	
	public IC_EnumForList(String title, String message, Class values) {
		super(title, message);
		this.values = values;
	}
	
	

	public IC_EnumForList(CompactUUID id) {
		super(id);
	}



	@Override
	public Object[] getValues() {
		if (values == null) {
			EnumAttributeType<?> enumAttribute = (EnumAttributeType<?>) getUIField().getAttributeDefinition();
			values = enumAttribute.getAttributeType();
		}
		return values.getEnumConstants();
	}

	public ILabelProvider getLabelProvider() {
		return new LabelProvider();
	}
	
	@Override
	public String canAddObject(Object[] object) {
		Object visualValue = getUIField().getVisualValue();
		
		if (visualValue == null 
				|| !(visualValue instanceof List) 
				|| object == null) 
			return null;
		
		List<Object> fElements = (List<Object>) visualValue;
		for (Object o : object) {
			if (fElements.contains(o))
				return "Already contains "+o.toString();
		}
		return null;
	}
	
	public ItemType getType() {
		return CadseGCST.IC_ENUM_FOR_LIST;
	}
	
}
