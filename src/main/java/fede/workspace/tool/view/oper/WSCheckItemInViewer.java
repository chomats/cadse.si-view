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

import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.oper.WSCheckOperation;
import fr.imag.adele.cadse.core.oper.annotation.OperParameter;
import fr.imag.adele.cadse.core.oper.annotation.OperTest;
import fr.imag.adele.cadse.core.oper.annotation.ParameterKind;
import fr.imag.adele.cadse.eclipse.view.AbstractCadseView;

@OperTest(testMustBeStopped = false)
public class WSCheckItemInViewer extends WSCheckOperation {

	@OperParameter(constructorPosition = 0, type = ParameterKind.string_value)
	private String	viewId;
	@OperParameter(constructorPosition = 1, type = ParameterKind.string_value)
	private String	viewSecondId;
	@OperParameter(constructorPosition = 2, type = ParameterKind.string_value)
	private String	nodeIdentifier;

	public WSCheckItemInViewer(AbstractCadseView view, IItemNode node) throws CadseException {
		// this.view = view;
		view.getPartName();
		// this.node = node;
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchPage activePage = workbench.getActiveWorkbenchWindow().getActivePage();
		IViewReference viewrefence = (IViewReference) activePage.getReference(view);
		viewId = viewrefence.getId();
		viewSecondId = viewrefence.getSecondaryId();
	}

	public WSCheckItemInViewer(String viewId, String viewSecondId, String nodeIdentifier) {
		super();
		this.viewSecondId = viewSecondId;
		this.viewId = viewId;
		this.nodeIdentifier = nodeIdentifier;
	}

	@Override
	protected void excecuteImpl() throws Throwable {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchPage activePage = workbench.getActiveWorkbenchWindow().getActivePage();
		AbstractCadseView view = (AbstractCadseView) activePage.showView(viewId, viewSecondId,
				IWorkbenchPage.VIEW_ACTIVATE);
		throw new CadseException("Cannot find node : {0}", nodeIdentifier);
	}

	public String getViewId() {
		return viewId;
	}

	public String getViewSecondId() {
		return viewSecondId;
	}

	public String getNodeIdentifier() {
		return nodeIdentifier;
	}

	@Override
	public String getDiplayComment() {
		return "check item in viewer";
	}
}
