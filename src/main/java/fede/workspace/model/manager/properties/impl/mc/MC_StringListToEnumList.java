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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.imag.adele.cadse.core.impl.ui.mc.MC_AttributesItem;
import fr.imag.adele.cadse.core.ui.UIField;


public class MC_StringListToEnumList<T> extends MC_AttributesItem {

	private Class<T> enumclass;
	private volatile transient Map<String, T> enumConstantDirectory = null;
	
	public MC_StringListToEnumList(Class<T> enumclass) {
		this.enumclass = enumclass;
	}
	
	@Override
	public Object getValue() {
		return abstractToVisualValue(super.getValue());
	}
	
	@Override
	public void notifieValueChanged(UIField field, Object value) {
		super.notifieValueChanged(field, visualToAbstractValue(value));
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
	 
	protected Object abstractToVisualValue(Object value) {
		if (value == null) {
			return null;
		}
		List<String> lvalue = (List<String>) value;
		List<T> retvalue = new ArrayList<T>();
		for (String o : lvalue) {
			retvalue.add(enumConstantDirectory().get(o));
		}
		return retvalue;
	}

	protected Object visualToAbstractValue(Object value) {
		if (value == null) 	return null;
		List lvalue = (List) value;
		List<String> retvalue = new ArrayList<String>();
		for (Object o : lvalue) {
			retvalue.add(o.toString());
		}
		return retvalue;
	}

	

	@Override
	public Object defaultValue() {
		return new ArrayList<T>();
	}
	
}
