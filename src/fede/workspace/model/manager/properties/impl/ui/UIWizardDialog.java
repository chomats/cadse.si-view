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
package fede.workspace.model.manager.properties.impl.ui;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import fr.imag.adele.cadse.core.ui.UIField;

public class UIWizardDialog extends WizardDialog {

	public UIWizardDialog(Shell parentShell, WizardController newWizard) {
		super(parentShell, newWizard);
	}

	@Override
	protected void nextPressed() {
		WizardController wizardController = (WizardController) getWizard();
		if (wizardController.nextPressed()) {
			super.nextPressed();
		}
	}

	@Override
	protected void backPressed() {
		WizardController wizardController = (WizardController) getWizard();
		if (wizardController.backPressed()) {
			super.backPressed();
		}
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		WizardController wizardController = (WizardController) getWizard();
		if (wizardController.hasShowDetail()) {
			createButton(parent, IDialogConstants.DETAILS_ID, "Operations detail", false);
		}
		super.createButtonsForButtonBar(parent);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.DETAILS_ID) {
			WizardController wizardController = (WizardController) getWizard();
			wizardController.showDetailDialog();
		}
		super.buttonPressed(buttonId);
	}

	@Override
	public void create() {
		super.create();
		WizardController wizardController = (WizardController) getWizard();
		getShell().setData(UIField.CADSE_MODEL_KEY, wizardController.pages);
	}

}
