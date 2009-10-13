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
package fede.workspace.tool.view.actions;

import java.util.ArrayList;

import fede.workspace.eclipse.core.CadseDialogPage;
import fede.workspace.role.initmodel.ErrorWhenLoadedModel;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.CadseRuntime;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.ui.MenuAction;
import fr.imag.adele.fede.workspace.si.view.View;

public class AddCadseModelAction extends MenuAction {

	public AddCadseModelAction() {
		super();
		init("add-cadse model", "Executed CADSEs", null, 0, null);
	}

	@Override
	public void run(IItemNode[] selection) throws CadseException {

		CadseRuntime[] sCadsesNameToLoad = getCadseRuntimeFromSelection(selection);
		if (sCadsesNameToLoad == null) {
			sCadsesNameToLoad = CadseDialogPage.openDialog(false);
		}
		if (sCadsesNameToLoad != null && sCadsesNameToLoad.length != 0) {
			try {
				View.getInstance().getInitModel().executeCadses(sCadsesNameToLoad);
			} catch (ErrorWhenLoadedModel e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private CadseRuntime[] getCadseRuntimeFromSelection(IItemNode[] selection) {
		ArrayList<CadseRuntime> ret = new ArrayList<CadseRuntime>();
		for (IItemNode n : selection) {
			if (n.getItem() != null && n.getItem() instanceof CadseRuntime) {
				if (n.getItem().getType() == CadseGCST.CADSE_DEFINITION)
					continue;
				ret.add((CadseRuntime) n.getItem());
			}
		}

		if (ret.size() == 0) {
			return null;
		}

		return ret.toArray(new CadseRuntime[ret.size()]);
	}

}
