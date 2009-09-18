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

import java.util.HashMap;
import java.util.Map;

import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.attribute.EnumAttributeType;
import fr.imag.adele.cadse.core.impl.ui.MC_AttributesItem;
import fr.imag.adele.cadse.core.CadseRootCST;
import fr.imag.adele.cadse.core.ui.UIField;


public class StringToEnumModelController<T extends Enum<T>> extends MC_AttributesItem {

	private Class<T> enumclass;
	private volatile transient Map<String, T> enumConstantDirectory = null;
	private T defaultValue;
	
	public StringToEnumModelController(Class<T> enumclass, T defaulfvalue) {
		this.enumclass = enumclass;
		this.defaultValue = defaulfvalue;
	}
	
	Map<String, T> enumConstantDirectory() {
		if (enumConstantDirectory == null) {
            T[] universe = enumclass.getEnumConstants();  // Does unnecessary clone
            if (universe == null)
                throw new IllegalArgumentException(
                		enumclass.getName() + " is not an enum type");
            Map<String, T> m = new HashMap<String, T>(2 * universe.length);
            for (T constant : universe)
                m.put(((Enum)constant).name(), constant);
            enumConstantDirectory = m;
        }
        return enumConstantDirectory;
	}
	 
	@Override
	public Object getValue() {
		Object value = super.getValue();
		if (value == null ) {
			if (defaultValue == null)
				return null;
			super.notifieValueChanged(getUIField(), defaultValue.toString());
			return defaultValue;
		}
		if (value instanceof String)
			return enumConstantDirectory().get(value);
		return value;
	}
	
	@Override
	public void notifieValueChanged(UIField field, Object value) {
		//TODO
		super.notifieValueChanged(field, value == null ? null : value.toString());
	}

	@Override
	public void init(UIField field) {
		super.init(field);
		if (enumclass == null) {
			EnumAttributeType<T> type = (EnumAttributeType<T>) field.getAttributeDefinition();
			if (type != null) {
				enumclass = (Class<T>) type.getAttributeType();
				defaultValue = type.getDefaultValue();
			}
		}
	}

	@Override
	public Object defaultValue() {
		return defaultValue;
	}
	
	
	@Override
	public ItemType getType() {
		return CadseRootCST.STRING_TO_ENUM_MODEL_CONTROLLER;
	}
}
