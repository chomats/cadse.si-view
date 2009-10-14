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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.CompactUUID;
import fr.imag.adele.cadse.core.ContentItem;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.impl.ContentItemImpl;

public abstract class SubFileContentManager extends ContentItemImpl {

	public SubFileContentManager(CompactUUID id) {
		super(id);
	}

	@Override
	public void create() throws CadseException {
	}

	protected InputStream getDefaultImputStream() {
		return new ByteArrayInputStream(new byte[0]);
	}

	@Override
	public void delete() throws CadseException {
	}

	@Override
	public String[] getKindsResource() {
		return null;
	}

	@Override
	public Object getMainResource() {
		return null;
	}

	@Override
	public Object[] getResources(String kind) {
		return null;
	}

	@Override
	public void setResources(String kind, Object[] resource) {
	}

	@Override
	public Object[] getResources() {
		return null;
	}

}
