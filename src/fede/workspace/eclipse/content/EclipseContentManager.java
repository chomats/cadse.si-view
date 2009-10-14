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
package fede.workspace.eclipse.content;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.CompactUUID;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.impl.ContentItemImpl;

/**
 * This content manager implements the building behavior for the default eclipse
 * mapping.
 * 
 * This class implements the generic behavior and delegates to an specific
 * composer to handle the specialized variants for the different kinds of
 * eclipse mappings.
 * 
 * @author vega
 * 
 */
public class EclipseContentManager extends ContentItemImpl {

	public EclipseContentManager(CompactUUID id) {
		super(id);
	}

	@Override
	public Object getMainResource() {
		// return
		// ResourcesPlugin.getWorkspace().getRoot().getProject(getItem().getLongName());
		return null;
	}

	@Override
	public Object[] getResources() {
		return new Object[] { getMainResource() };
	}

	@Override
	public Object[] getResources(String kind) {
		// if ("project".equals(kind) && getMainResource() != null) {
		// return new Object[] { getMainResource() };
		// }
		return null;
	}

	@Override
	public String[] getKindsResource() {
		return new String[] {}; // "project"
	}

	@Override
	public void setResources(String kind, Object[] resource) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void create() throws CadseException {
	}

	@Override
	public void delete() throws CadseException {
	}

}
