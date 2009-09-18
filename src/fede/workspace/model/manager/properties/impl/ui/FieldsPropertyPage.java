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

import java.net.URL;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.DefaultItemManager;
import fr.imag.adele.cadse.core.IItemManager;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ui.IFieldDescription;
import fr.imag.adele.cadse.core.ui.Pages;
import fede.workspace.tool.eclipse.FedeFormToolkit;

public class FieldsPropertyPage extends PropertyPage {

	FieldsController	controller	= null;

	public FieldsPropertyPage() {
	}

	@Override
	protected Control createContents(Composite parent) {

		if (controller != null) {
			try {
				return FieldsController.createControlPage(controller, new FedeFormToolkit(parent.getDisplay()), parent,
						controller.getPages());
			} catch (CadseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.verticalSpacing = 9;
		Text t = new Text(container, SWT.NONE);
		t.setText("No item selected.");
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_CENTER + GridData.VERTICAL_ALIGN_CENTER);
		t.setLayoutData(gd);
		return container;
	}

	protected void updateStatus(String message) {
		setErrorMessage(message);
		// setPageComplete(message == null);
	}

	public Item getTheCurrentItem() {
		return (controller == null ? null : controller.getPages().getItem());
	}

	public Item getParentItem() {
		return (Item) (controller == null ? null : controller.getPages().getLocal(IFieldDescription.PARENT_CONTEXT));
	}

	@Override
	public void setElement(IAdaptable element) {
		super.setElement(element);
		Item item = ((Item) element);

		IItemManager im = item.getType().getItemManager();
		Pages pages = null;
		if (im instanceof DefaultItemManager) {
			pages = ((DefaultItemManager) im).createModificationPage(item);
		}
		if (pages == null) {
			pages = item.getType().getGoodModificationPage(item);
		}
		pages.setItem(item);
		setController(pages);
		URL url = im.getImage(item);
		if (url != null) {
			setImageDescriptor(ImageDescriptor.createFromURL(url));
		}
		setTitle(item.getType().getDisplayName());

	}

	private void setController(Pages desc) {
		this.controller = new FieldsController(desc);
	}

	@Override
	public boolean performOk() {
		// creatingItem.commit(null,null);
		return true;
	}

	@Override
	public void dispose() {
		super.dispose();
		if (controller != null) {
			controller.dispose();
		}
	}
}
