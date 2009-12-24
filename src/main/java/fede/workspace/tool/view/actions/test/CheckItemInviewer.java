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
package fede.workspace.tool.view.actions.test;

import java.net.URL;

import fede.workspace.tool.view.oper.WSCheckItemInViewer;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.IMenuAction;
import fr.imag.adele.cadse.eclipse.view.AbstractCadseView;
import fr.imag.adele.fede.workspace.si.view.View;

public class CheckItemInviewer extends IMenuAction {

	private IItemNode	node;
	AbstractCadseView	view;

	public CheckItemInviewer(IItemNode node, AbstractCadseView view) {
		this.node = node;
		this.view = view;
	}

	@Override
	public URL getImage() {
		return null;
	}

	@Override
	public String getLabel() {
		return "Check in view " + node.toString();
	}

	@Override
	public void run(IItemNode[] selection) throws CadseException {
		WSCheckItemInViewer oper = new WSCheckItemInViewer(view, node);
		oper.execute();
		if (View.isStarted()) {
			View.getInstance().getTestService().registerIfNeed(oper);
		}
	}

}
