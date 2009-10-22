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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;

import fr.imag.adele.cadse.core.impl.ui.MC_AttributesItem;
import fr.imag.adele.cadse.core.ui.UIField;

public class StringToResourceSimpleModelController extends MC_AttributesItem {

	public StringToResourceSimpleModelController() {
		super();
	}

	@Override
	public Object getValue() {
		return abstractToVisualValue(super.getValue());
	}

	@Override
	public void notifieValueChanged(UIField field, Object value) {
		super.notifieValueChanged(field, visualToAbstractValue(value));
	}

	@Override
	public boolean validValue(UIField field, Object value) {
		return super.validValue(field, visualToAbstractValue(value));
	}

	@Override
	public boolean validValueChanged(UIField field, Object visualValue) {
		return super.validValueChanged(field, visualToAbstractValue(visualValue));
	}

	protected Object abstractToVisualValue(Object value) {
		String rString = (String) value;

		if (rString == null) {
			return null;
		}

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		return root.findMember(new Path(rString));

	}

	protected Object visualToAbstractValue(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof IResource) {
			IResource r = (IResource) value;

			return r.getFullPath().toPortableString();
		}
		return value.toString();
	}

	@Override
	public Object defaultValue() {
		return null;
	}
}
