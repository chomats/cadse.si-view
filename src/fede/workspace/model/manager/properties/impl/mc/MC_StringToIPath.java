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

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import fr.imag.adele.cadse.core.impl.ui.MC_AttributesItem;
import fr.imag.adele.cadse.core.ui.UIField;

public class MC_StringToIPath extends MC_AttributesItem {
	
	public MC_StringToIPath() {
		super();
	}
	
	@Override
	public Object getValue() {
		return abstractToVisualValue(super.getValue());
	}

	public  IPath abstractToVisualValue(Object value) {
		String path = (String) value;
		
		if (path == null || path.length() == 0)
            return null;
		
		return new Path(path);
	}
	
	@Override
	public void notifieValueChanged(UIField field, Object value) {
		super.notifieValueChanged(field, visualToAbstractValue(value));
	}
	
	protected Object visualToAbstractValue(Object value) {
		IPath r = (IPath) value;
		if (r == null)
			return null;
		return r.toPortableString();
	}
	

}
