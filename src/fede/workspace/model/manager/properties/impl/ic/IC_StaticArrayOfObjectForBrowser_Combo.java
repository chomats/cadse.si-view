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

import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.CadseRootCST;


public class IC_StaticArrayOfObjectForBrowser_Combo extends IC_AbstractForBrowser_Combo  {

	private final Object[] values;
	
	public IC_StaticArrayOfObjectForBrowser_Combo(String title, String message, final Object[] values) {
		super(title, message);
		this.values = values;
	}

	public Object[] getValues() {
		return values;
	}

	@Override
	public String toString(Object value) {
		return value.toString();
	}
	

	@Override
	protected Object[] getSelectableValues() {
		return values;
	}
	
	public ItemType getType() {
		return CadseRootCST.IC_STATIC_ARRAY_OF_OBJECT_FOR_BROWSER_COMBO;
	}
}
