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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import fede.workspace.model.manager.properties.IInteractionControllerForBrowserOrCombo;
import fr.imag.adele.cadse.core.CadseRootCST;
import fr.imag.adele.cadse.core.CompactUUID;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.ui.EPosLabel;
import fr.imag.adele.cadse.core.ui.IFedeFormToolkit;
import fr.imag.adele.cadse.core.ui.IModelController;
import fr.imag.adele.cadse.core.ui.IPageController;

/**
 * attributes : -ui controller : IInteractiveComboBoxController - combo-values :
 * String[] si IC_StaticArrayOfObjectForBrowser_Combo comme interactive
 * controller ///- enable-editor : Boolean, default : Boolean.FALSE value -
 * Object or String
 */

public class DComboUI extends DAbstractField {

	protected CCombo						attributWidget;
	IInteractionControllerForBrowserOrCombo	boxControler;
	Object[]								values;
	private boolean							edit;

	public DComboUI(String key, String label, EPosLabel poslabel, IModelController mc,
			IInteractionControllerForBrowserOrCombo ic, boolean edit) {
		super(key, label, poslabel, mc, ic);
		this.edit = edit;
	}

	public DComboUI(CompactUUID id, String shortName) {
		super(id, shortName);
	}

	@Override
	public Composite createControl(final IPageController globalUIController, IFedeFormToolkit toolkit,
			Object ocontainer, int hspan) {
		GridData gd;
		Composite container = (Composite) ocontainer;
		attributWidget = new CCombo(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = hspan;
		attributWidget.setLayoutData(gd);
		attributWidget.setData(CADSE_MODEL_KEY, this);

		attributWidget.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				globalUIController.broadcastValueChanged(DComboUI.this, getVisualValue());
			}

		});
		attributWidget.setEditable(this.edit);

		boxControler = (IInteractionControllerForBrowserOrCombo) getInteractionController();
		values = boxControler.getValues();
		String[] valuesString = new String[values.length];
		for (int i = 0; i < valuesString.length; i++) {
			valuesString[i] = boxControler.toString(values[i]);
		}
		attributWidget.setItems(valuesString);

		return container;
	}

	@Override
	public Object getVisualValue() {
		int index = attributWidget.getSelectionIndex();

		if (index == -1) {
			return attributWidget.getText();
		}
		return values[index];
	}

	public void setVisualValue(Object visualValue, boolean sendNotification) {
		if (visualValue == null) {
			return;
		}

		for (int i = 0; i < values.length; i++) {
			if (values[i].equals(visualValue)) {
				attributWidget.select(i);
				return;
			}
		}
		if (visualValue instanceof String) {
			attributWidget.setText((String) visualValue);
		} else if (values.length > 0) {
			attributWidget.select(0);
		}
	}

	@Override
	public Object getUIObject(int index) {
		return attributWidget;
	}

	@Override
	public void setEnabled(boolean enabled) {
		attributWidget.setEnabled(enabled);
	}

	@Override
	public void internalSetEditable(boolean v) {
		attributWidget.setEditable(v);
	}

	@Override
	public void internalSetVisible(boolean v) {
		super.internalSetVisible(v);
		attributWidget.setVisible(v);
	}

	public ItemType getType() {
		return CadseRootCST.DCOMBO;
	}

	@Override
	public Control getMainControl() {
		return this.attributWidget;
	}

	@Override
	public Object[] getSelectedObjects() {
		return new Object[] { getVisualValue() };
	}

}