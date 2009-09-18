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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import fr.imag.adele.cadse.core.CadseRootCST;
import fr.imag.adele.cadse.core.CompactUUID;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.ui.EPosLabel;
import fr.imag.adele.cadse.core.ui.IFedeFormToolkit;
import fr.imag.adele.cadse.core.ui.IInteractionController;
import fr.imag.adele.cadse.core.ui.IModelController;
import fr.imag.adele.cadse.core.ui.IPageController;

/**
 * attribut : check-label : string value : Boolean
 * 
 * @author chomats
 * 
 */
public class DCheckBoxUI extends DAbstractField {

	private Button	_control;

	private Boolean	_value;

	public DCheckBoxUI(CompactUUID id, String shortName) {
		super(id, shortName);
	}

	public DCheckBoxUI(String key, String label, EPosLabel poslabel, IModelController mc, IInteractionController ic) {
		super(key, label, poslabel, mc, ic);
	}

	public Object __getVisualValue() {
		_value = _control.getSelection() ? Boolean.TRUE : Boolean.FALSE;
		return _value;
	}

	@Override
	public Composite createControl(final IPageController globalUIController, IFedeFormToolkit toolkit,
			Object ocontainer, int hspan) {

		GridData gd;

		String label = getLabel();
		_control = (Button) toolkit.createButton(ocontainer, label, SWT.CHECK);
		_control.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				globalUIController.broadcastValueChanged(DCheckBoxUI.this, __getVisualValue());
			}

			public void widgetSelected(SelectionEvent e) {
				globalUIController.broadcastValueChanged(DCheckBoxUI.this, __getVisualValue());
			}

		});
		if (!isEditable()) {
			_control.setEnabled(false);
		}

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = hspan;
		_control.setLayoutData(gd);
		_control.setData(CADSE_MODEL_KEY, this);
		return (Composite) ocontainer;
	}

	@Override
	public void dispose() {
		super.dispose();
		_control = null;
		_value = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see model.workspace.workspace.managers.ui.DisplayManager#getDefaultPosLabel()
	 */
	@Override
	protected EPosLabel getDefaultPosLabel() {
		return EPosLabel.none;
	}

	@Override
	public Control getMainControl() {
		return _control;
	}

	@Override
	public Object[] getSelectedObjects() {
		return new Boolean[] { _value };
	}

	public ItemType getType() {
		return CadseRootCST.DCHECK_BOX;
	}

	@Override
	public Object getUIObject(int index) {
		return _control;
	}

	@Override
	public Object getVisualValue() {
		return _value;
	}

	@Override
	public void internalSetEditable(boolean v) {
		_control.setEnabled(v);
	}

	@Override
	public void internalSetVisible(boolean v) {
		super.internalSetVisible(v);
		_control.setVisible(v);
	}

	@Override
	public void setEnabled(boolean v) {
		_control.setEnabled(v);
	}

	public void setVisualValue(Object visualValue, boolean sendNotification) {
		if (visualValue == null) {
			visualValue = Boolean.FALSE;
		}
		assert visualValue instanceof Boolean;

		_value = (Boolean) visualValue;
		(_control).setSelection(_value.booleanValue());
	}

}
