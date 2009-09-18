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

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.ui.IPage;
import fr.imag.adele.cadse.core.ui.IPageController;
import fr.imag.adele.cadse.core.ui.UIField;
import fede.workspace.tool.eclipse.FedeFormToolkit;

/**
 */

public class FieldsWizardPage extends WizardPage implements IPageController {

	IPage			page;
	private boolean	init;

	/**
	 * Constructor for FieldsWizardPage.
	 * 
	 * @param theCurrentItem
	 * @throws CadseException
	 */
	public FieldsWizardPage(IPage page) throws CadseException {
		super("wizardPage");
		this.page = page;
		// pageDesc.put("wizard-page",this);
		setTitle(page.getTitle());
		setDescription(page.getDescription());
		setPageComplete(page.isPageComplete());
		page.init(this);
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		this.init = true;
		FedeFormToolkit tk = new FedeFormToolkit(parent.getDisplay());
		try {
			setControl(FieldsController.createPage(page, this, tk, parent));
			page.initAfterUI();
			// Reset visual value. and set UI_running at true
			page.resetVisualValue();
			setMessage(null, IPageController.ERROR);
			page.validateFields(null);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.init = false;
	}

	public void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	@Override
	public void setMessage(String newMessage, int newType) {
		super.setMessage(newMessage, newType);
		if (newType == IPageController.ERROR) {
			setPageComplete(newMessage == null);
		}
	}

	public IPage getPageDesc() {
		return this.page;
	}

	public IPage getPage() {
		return this.page;
	}

	@Override
	public void dispose() {
		super.dispose();
		page.dispose();
	}

	public boolean broadcastSubValueAdded(UIField field, Object added) {
		if (init) {
			return true;
		}
		setMessage(null, IPageController.ERROR);
		return field.broadcastSubValueAdded(this, added);
	}

	public boolean broadcastSubValueRemoved(UIField field, Object removed) {
		if (init) {
			return true;
		}
		setMessage(null, IPageController.ERROR);
		return field.broadcastSubValueRemoved(this, removed);
	}

	public void broadcastThisFieldHasChanged(UIField fd) {
		fd.updateValue();
	}

	public boolean broadcastValueChanged(UIField field, Object value) {
		if (init) {
			return true;
		}
		setMessage(null, IPageController.ERROR);
		return field.broadcastValueChanged(this, value);
	}

	public boolean broadcastValueDeleted(UIField field, Object oldvalue) {
		if (init) {
			return true;
		}
		setMessage(null, IPageController.ERROR);
		return field.broadcastValueDeleted(this, oldvalue);
	}

}