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
package fede.workspace.tool.view.oper;

import fede.workspace.tool.view.WSPlugin;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.IItemManager;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.oper.WSODeleteItem;
import fr.imag.adele.cadse.core.oper.annotation.OperParameter;
import fr.imag.adele.cadse.core.oper.annotation.OperTest;
import fr.imag.adele.cadse.core.oper.annotation.ParameterKind;
import fr.imag.adele.fede.workspace.si.view.View;

@OperTest(testMustBeStopped = true)
public class WSODeleteItemAndContent extends WSODeleteItem {

	@OperParameter(constructorPosition = 3, type = ParameterKind.boolean_value)
	private boolean	deleteEclipseResource;
	@OperParameter(constructorPosition = 2, type = ParameterKind.string_value)
	private String	errorMsg	= null;

	public WSODeleteItemAndContent(Item item, boolean deleteContent, String errorMsg, boolean deleteEclipseResource) {
		super(item, deleteContent);
		this.errorMsg = errorMsg;
		this.deleteEclipseResource = deleteEclipseResource;
	}

	public boolean isDeleteEclipseResource() {
		return deleteEclipseResource;
	}

	@Override
	protected void excecuteImpl() throws Throwable {
		IItemManager im = WSPlugin.getManager(getItem());
		String error = im.canDeleteItem(getItem());
		if (error == null && errorMsg != null) {
			throw new CadseException("Cannot delete Item before");
		}
		if (error != null && errorMsg == null) {
			throw new CadseException("Cannot delete Item after");
		}

		if (error != null) {
			return;
		}

		View.setDeleteOption(isDeleteEclipseResource(), isDeleteContent());
		super.excecuteImpl();
		View.unsetDeleteOption();
	}

	@Override
	public Class<?> getType() {
		return WSODeleteItemAndContent.class;
	}

	public String getErrorMsg() {
		return errorMsg;
	}
}
