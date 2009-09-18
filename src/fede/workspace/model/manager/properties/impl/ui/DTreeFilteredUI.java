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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import fede.workspace.model.manager.properties.IInteractionControllerForList;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.ui.EPosLabel;
import fr.imag.adele.cadse.core.ui.IFedeFormToolkit;
import fr.imag.adele.cadse.core.ui.IModelController;
import fr.imag.adele.cadse.core.ui.IPageController;

/**
 * value
 * <li>List&lt;Object&gt;<br>
 * </li>
 * 
 * @author chomats
 * 
 */
public class DTreeFilteredUI extends DAbstractField {

	public DTreeFilteredUI(String key, String label, EPosLabel poslabel, IModelController mc,
			IInteractionControllerForList ic) {
		super(key, label, poslabel, mc, ic);
		uiControler = ic;
	}

	List<Object>					fElements;
	private FilteredTree			packageTable;
	private Button					buttonAdd;
	private Button					buttonRemove;

	IInteractionControllerForList	uiControler;

	@Override
	public Object getVisualValue() {
		return fElements;
	}

	@Override
	public Object createControl(final IPageController fieldController, IFedeFormToolkit toolkit, Object ocontainer,
			int hspan) {

		GridData gd;
		Composite container = (Composite) ocontainer;
		packageTable = new FilteredTree(container, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL, new PatternFilter());

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 100;
		gd.verticalSpan = 2;
		gd.horizontalSpan = hspan - 1;
		packageTable.setLayoutData(gd);
		packageTable.getViewer().setContentProvider(uiControler.getContentProvider());
		packageTable.setData(CADSE_MODEL_KEY, this);

		buttonAdd = new Button(container, SWT.PUSH);
		buttonAdd.setText("Add...");
		buttonAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(@SuppressWarnings("unused")
			SelectionEvent e) {
				handleAdd(fieldController);
			}
		});
		buttonAdd.setData(this);

		buttonRemove = new Button(container, SWT.PUSH);
		buttonRemove.setText("Remove...");
		buttonRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(@SuppressWarnings("unused")
			SelectionEvent e) {
				ISelection sel = packageTable.getViewer().getSelection();
				if (sel == null) {
					return;
				}
				if (sel instanceof IStructuredSelection) {
					IStructuredSelection ssel = (IStructuredSelection) sel;
					if (ssel.size() == 0) {
						return;
					}
					handleRemove(ssel.toArray(), fieldController);
				}

			}
		});
		buttonRemove.setData(this);
		return container;

	}

	protected void handleRemove(Object[] sel, IPageController fieldController) {
		String error = uiControler.canRemoveObject(sel);
		if (error != null) {
			fieldController.setMessage(error, IPageController.ERROR);
			return;
		}
		Object[] removedObj = uiControler.removeObject(sel);
		for (Object obj : removedObj) {
			fElements.remove(obj);
		}
		setVisualValue(fElements);
		fieldController.broadcastValueChanged(this, getVisualValue());
	}

	protected void handleAdd(IPageController fieldController) {
		Object[] ret = uiControler.selectOrCreateValues(packageTable.getShell());
		if (ret != null) {
			String error = uiControler.canAddObject(ret);
			if (error != null) {
				fieldController.setMessage(error, IPageController.ERROR);
				return;
			}
			ret = uiControler.transAndAddObject(ret);
			fElements.addAll(Arrays.asList(ret));
			setVisualValue(fElements);
			fieldController.broadcastValueChanged(this, getVisualValue());
		}
	}

	protected ILabelProvider getLabelProvider() {
		return uiControler.getLabelProvider();
	}

	public void setVisualValue(Object visualValue, boolean sendNotification) {
		if (visualValue == null) {
			visualValue = new ArrayList<Object>();
		}
		assert visualValue instanceof List;
		fElements = (List<Object>) visualValue;
		packageTable.getViewer().setInput(fElements.toArray());

	}

	@Override
	public Object getUIObject(int index) {
		switch (index) {
			case 0:
				return packageTable;
			case 1:
				return buttonAdd;
			case 2:
				return buttonRemove;

			default:
				break;
		}
		return null;
	}

	@Override
	public void setEnabled(boolean v) {
		packageTable.setEnabled(v);
		if (buttonAdd != null) {
			buttonAdd.setEnabled(v);
		}
		if (buttonRemove != null) {
			buttonRemove.setEnabled(v);
		}
	}

	@Override
	public void internalSetEditable(boolean v) {

	}

	@Override
	public void internalSetVisible(boolean v) {
		super.internalSetVisible(v);
		packageTable.setVisible(v);
		if (buttonAdd != null) {
			buttonAdd.setVisible(v);
		}
		if (buttonRemove != null) {
			buttonRemove.setVisible(v);
		}
	}

	@Override
	public int getHSpan() {
		return 2;
	}

	public ItemType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Control getMainControl() {
		return this.packageTable;
	}

	@Override
	public Object[] getSelectedObjects() {
		return ((StructuredSelection) this.packageTable.getViewer().getSelection()).toArray();
	}
}
