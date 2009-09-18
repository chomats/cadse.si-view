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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.attribute.SymbolicBitMapAttributeType;
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
public class DSymbolicBitMapUI extends DAbstractField {

	SymbolicBitMapAttributeType	attributeDefinition;
	String[]					labels;
	Button[]					controls;
	Object						value;
	int							col;
	private Group				g;

	public DSymbolicBitMapUI(String key, String label, EPosLabel poslabel, IModelController mc,
			IInteractionController ic, SymbolicBitMapAttributeType attributeDefinition, String[] labels, int col) {
		super(key, label, poslabel, mc, ic);
		this.attributeDefinition = attributeDefinition;
		this.labels = labels;
		value = attributeDefinition.getDefaultValue();
		if (col == -1) {
			col = 3;
		}
	}

	@Override
	public Object getVisualValue() {
		int loc_value = ((Integer) value).intValue();
		for (int i = 0; i < controls.length; i++) {
			if (controls[i] != null) {
				boolean v = controls[i].getSelection();
				int flag = 1 << i;
				if (v != ((loc_value & flag) != 0)) {
					if (v) {
						loc_value |= flag;
					} else {
						loc_value &= ~flag;
					}
				}
				assert v == ((loc_value & flag) != 0);
			}
		}
		return loc_value;
	}

	@Override
	public Composite createControl(final IPageController globalUIController, IFedeFormToolkit toolkit,
			Object ocontainer, int hspan) {

		GridData gd;

		g = (Group) toolkit.createGroup(ocontainer, getLabel());
		g.setData(CADSE_MODEL_KEY, this);
		g.setLayout(new GridLayout(col, false));
		controls = new Button[labels.length];
		for (int i = 0; i < labels.length; i++) {
			if (labels[i] == null) {
				controls[i] = null;
				continue; // reseved or private position
			}
			controls[i] = (Button) toolkit.createButton(g, labels[i], SWT.CHECK);
			controls[i].setData(i);
			controls[i].setData(CADSE_MODEL_KEY, this);
			controls[i].addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					globalUIController.broadcastValueChanged(DSymbolicBitMapUI.this, getVisualValue());
				}

				public void widgetDefaultSelected(SelectionEvent e) {
					globalUIController.broadcastValueChanged(DSymbolicBitMapUI.this, getVisualValue());
				}

			});
		}
		if (!isEditable()) {
			setEnabled(false);
		}

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = hspan;
		g.setLayoutData(gd);
		return (Composite) ocontainer;
	}

	public void setVisualValue(Object visualValue, boolean sendNotification) {
		if (visualValue == null) {
			visualValue = attributeDefinition.getDefaultValue();
		}
		assert visualValue instanceof Integer;
		int loc_value = ((Integer) visualValue).intValue();
		for (int i = 0; i < controls.length; i++) {
			if (controls[i] != null) {
				controls[i].setSelection((loc_value & (1 << i)) != 0);
			}
		}
		this.value = visualValue;
	}

	@Override
	public Object getUIObject(int index) {
		return controls;
	}

	@Override
	public void setEnabled(boolean v) {
		for (int i = 0; i < controls.length; i++) {
			if (controls[i] != null) {
				controls[i].setEnabled(v);
			}
		}
	}

	@Override
	public void internalSetEditable(boolean v) {
		for (int i = 0; i < controls.length; i++) {
			if (controls[i] != null) {
				controls[i].setEnabled(v);
			}
		}
	}

	@Override
	public void internalSetVisible(boolean v) {
		super.internalSetVisible(v);
		for (int i = 0; i < controls.length; i++) {
			if (controls[i] != null) {
				controls[i].setVisible(v);
			}
		}
	}

	public ItemType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Control getMainControl() {
		return g;
	}

	@Override
	public Object[] getSelectedObjects() {
		return null;
	}

}
