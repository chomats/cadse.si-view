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
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import fede.workspace.tool.eclipse.FedeFormToolkit;
import fede.workspace.tool.view.WSPlugin;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.ui.EPosLabel;
import fr.imag.adele.cadse.core.ui.IFedeFormToolkit;
import fr.imag.adele.cadse.core.ui.IFieldDescription;
import fr.imag.adele.cadse.core.ui.IPage;
import fr.imag.adele.cadse.core.ui.IPageController;
import fr.imag.adele.cadse.core.ui.Pages;
import fr.imag.adele.cadse.core.ui.UIField;

/**
 */

public class FieldsController implements IPageController {

	private Pages	pages;

	/**
	 * Constructor for FieldsWizardPage.
	 * 
	 * @param theCurrentItem
	 */
	public FieldsController() {
	}

	public FieldsController(Pages desc) {
		this.pages = desc;
	}

	public Pages getPages() {
		return pages;
	}

	static public Composite createPage(IPage page, IPageController globalUIController, FedeFormToolkit toolkit,
			Composite parent) {

		Composite container = toolkit.createComposite(parent);
		int maxHspan = page.getHSpan();
		return createFieldsControl(globalUIController, toolkit, container, page.getFields(), maxHspan, null);

	}

	public static Composite createFieldsControl(IPageController globalUIController, IFedeFormToolkit toolkit,
			Composite container, UIField[] fields, int max, GridLayout layout) {

		if (layout == null) {
			layout = new GridLayout();
			container.setLayoutData(new FormData(300, 300));
		}
		container.setLayout(layout);

		int maxHspan = max;
		for (UIField mf : fields) {
			mf.init(globalUIController);

			int h = mf.getHSpan();

			if (mf.getPosLabel().equals(EPosLabel.left)) {
				h++;
			}
			if (h > maxHspan) {
				maxHspan = h;
			}
		}
		layout.numColumns = maxHspan;
		layout.verticalSpacing = 5; // 9

		for (UIField mf : fields) {
			try {
				createControl(globalUIController, (FedeFormToolkit) toolkit, mf, container, maxHspan);
			} catch (Throwable e) {
				WSPlugin.logException(e);
			}
		}
		return container;
	}

	/**
	 * @throws CadseException
	 * @see IDialogPage#createControl(Composite)
	 */
	static public Composite createControlPage(IPageController globalUIcontroller, FedeFormToolkit toolkit,
			Composite parent, Pages pages) throws CadseException {
		pages.init(globalUIcontroller);

		TabFolder container = new TabFolder(parent, SWT.V_SCROLL + SWT.H_SCROLL);
		toolkit.adapt(container);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		for (IPage mf : pages.getPages()) {
			String title = mf.getLabel();
			TabItem ti = new TabItem(container, SWT.NONE);
			ti.setText(title);
			// TODO set an image...
			ti.setControl(createPage(mf, globalUIcontroller, toolkit, container));
		}
		for (IPage page : pages.getPages()) {
			page.initAfterUI();
		}
		for (IPage page : pages.getPages()) {
			// Reset visual value. and set UI_running at true
			page.resetVisualValue();
		}

		globalUIcontroller.setMessage(null, IPageController.ERROR);
		pages.validateFields(null, null);

		return container;
	}

	static protected Composite createControl(IPageController globalUIcontroller, FedeFormToolkit toolkit,
			UIField field, Composite container, int hspan) {
		if (field.isHidden()) {
			return container;
		}

		int hspan_label = hspan;
		if (field.getPosLabel().equals(EPosLabel.left) || field.getPosLabel().equals(EPosLabel.right)) {
			hspan_label--;
		}
		container = createLabelField(toolkit, field, container, hspan);
		Composite ret = (Composite) field.createControl(globalUIcontroller, toolkit, container, hspan_label);
		container = createLabelFieldAfter(toolkit, field, container, hspan);
		return ret;
	}

	static protected Composite createLabelField(FedeFormToolkit toolkit, UIField field, Composite container, int hspan) {
		GridData gd;
		if (field.getPosLabel().equals(EPosLabel.left)) {
			Label l = toolkit.createLabel(container, field.getLabel());
			gd = new GridData();
			gd.verticalSpan = field.getVSpan();
			l.setLayoutData(gd);
			if (!field.isEditable()) {
				l.setEnabled(false);
			}
			field.put(IFieldDescription.LABEL_WIDGET, l);
		} else if (field.getPosLabel().equals(EPosLabel.top)) {
			Label l = toolkit.createLabel(container, field.getLabel());
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = hspan;
			l.setLayoutData(gd);
			if (!field.isEditable()) {
				l.setEnabled(false);
			}
			field.put(IFieldDescription.LABEL_WIDGET, l);
		} else if (field.getPosLabel().equals(EPosLabel.group)) {
			Group g = toolkit.createGroup(container, field.getLabel());
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = hspan;
			g.setLayoutData(gd);
			container = g;
			container.setLayout(new org.eclipse.swt.layout.GridLayout(hspan, false));
		}
		return container;
	}

	static protected Composite createLabelFieldAfter(FedeFormToolkit toolkit, UIField field, Composite container,
			int hspan) {
		if (field.getPosLabel().equals(EPosLabel.right)) {
			toolkit.createLabel(container, field.getLabel());
		}
		return container;
	}

	public void updateStatus(String message) {
		// setErrorMessage(message);
		// setPageComplete(message == null);
	}

	public boolean broadcastSubValueAdded(UIField field, Object added) {
		return field.broadcastSubValueAdded(this, added);
	}

	public boolean broadcastSubValueRemoved(UIField field, Object removed) {
		return field.broadcastSubValueRemoved(this, removed);
	}

	public void broadcastThisFieldHasChanged(UIField fd) {
		fd.updateValue();
	}

	public boolean broadcastValueChanged(UIField field, Object value) {
		return field.broadcastValueChanged(this, value);
	}

	public boolean broadcastValueDeleted(UIField field, Object oldvalue) {
		return field.broadcastValueDeleted(this, oldvalue);
	}

	public void dispose() {
		if (pages != null) {
			pages.dispose();
		}
	}

	public String getMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getMessageType() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setMessage(String newMessage, int newType) {
		// TODO Auto-generated method stub

	}
}