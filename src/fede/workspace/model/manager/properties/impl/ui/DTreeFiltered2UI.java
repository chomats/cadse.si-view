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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
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
public class DTreeFiltered2UI extends DAbstractField {

	public DTreeFiltered2UI(String key, String label, EPosLabel poslabel, IModelController mc,
			IInteractionControllerForList ic) {
		super(key, label, poslabel, mc, ic);
		uiControler = ic;
	}

	List<Object>					fElements;

	private FilteredTree			fFilteredTree;

	IInteractionControllerForList	uiControler;

	@Override
	public Object getVisualValue() {
		return fElements;
	}

	@Override
	public Object createControl(final IPageController fieldController, IFedeFormToolkit toolkit, Object container,
			int hspan) {

		GridData gd;
		fFilteredTree = new FilteredTree((Composite) container, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL,
				new PatternFilter());

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 100;
		gd.verticalSpan = 2;
		gd.horizontalSpan = hspan - 1;
		fFilteredTree.setLayoutData(gd);
		fFilteredTree.getViewer().setContentProvider(uiControler.getContentProvider());
		fFilteredTree.setData(CADSE_MODEL_KEY, this);
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
		Object[] ret = uiControler.selectOrCreateValues(fFilteredTree.getShell());
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
		fFilteredTree.getViewer().setInput(fElements.toArray());

	}

	@Override
	public Object getUIObject(int index) {
		switch (index) {
			case 0:
				return fFilteredTree;
			default:
				break;
		}
		return null;
	}

	@Override
	public void setEnabled(boolean v) {
		fFilteredTree.setEnabled(v);
	}

	@Override
	public void internalSetEditable(boolean v) {
		fFilteredTree.setEnabled(v);
	}

	@Override
	public void internalSetVisible(boolean v) {
		super.internalSetVisible(v);
		fFilteredTree.setVisible(v);
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
		return fFilteredTree;
	}

	@Override
	public Object[] getSelectedObjects() {
		return ((StructuredSelection) fFilteredTree.getViewer().getSelection()).toArray();
	}
}
