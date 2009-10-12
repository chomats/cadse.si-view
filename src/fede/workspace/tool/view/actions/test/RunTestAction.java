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

import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.wizard.WizardDialog;

import fede.workspace.model.manager.properties.impl.ui.WizardController;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.IMenuAction;
import fr.imag.adele.cadse.core.ui.Pages;
import fr.imag.adele.cadse.ui.field.core.FieldsCore;

public class RunTestAction extends IMenuAction {
	private IShellProvider	shellProvider;

	public RunTestAction(IShellProvider shellProvider) {
		this.shellProvider = shellProvider;
	}

	@Override
	public URL getImage() {
		return null;
	}

	@Override
	public String getLabel() {
		return "Run";
	}

	@Override
	public void run(IItemNode[] selection) throws CadseException {

		try {
			StartTestActionPage myaction = new StartTestActionPage();
			myaction.setDirectoryPath(null);
			Pages f = FieldsCore.createWizard(myaction, FieldsCore.createPage("page1", "Record test",
					"Start a new test", 4, myaction.createNameField(), myaction.createDescritionField(), myaction
							.createDirectoryField()));

			WizardController wc = new WizardController(f);
			WizardDialog wd = new WizardDialog(shellProvider.getShell(), wc);
			wd.setPageSize(300, 200);
			wd.open();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
