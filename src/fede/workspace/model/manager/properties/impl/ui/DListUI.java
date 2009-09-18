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
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
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
import fr.imag.adele.cadse.core.CadseRootCST;
import fr.imag.adele.cadse.core.CompactUUID;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.attribute.IAttributeType;
import fr.imag.adele.cadse.core.ui.EPosLabel;
import fr.imag.adele.cadse.core.ui.IFedeFormToolkit;
import fr.imag.adele.cadse.core.ui.IModelController;
import fr.imag.adele.cadse.core.ui.IPageController;
import fr.imag.adele.cadse.core.util.Convert;

/**
 * value
 * <li>List&lt;Object&gt;<br>
 * </li>.
 * 
 * @author chomats
 */
public class DListUI extends DAbstractField {

	static boolean	_temp;

	private final class MyFilteredTree extends FilteredTree {

		private MyFilteredTree(Composite parent, int style, PatternFilter filter, boolean showfilter) {
			super(parent, style, filter);
		}

		@Override
		protected void createControl(Composite parent, int treeStyle) {
			// IMPORTANT : impossible sinon de changer la valeur de
			// showFilterControls dans un constructeur.
			// car la methode create control est appeler dans le contructreur du
			// parent...
			if (showFilterControls) {
				showFilterControls = _temp;
			}
			super.createControl(parent, treeStyle);
		}

		@Override
		protected Control createTreeControl(Composite parent, int style) {
			final Control ret = super.createTreeControl(parent, style);
			ret.setData(CADSE_MODEL_KEY, DListUI.this);
			return ret;
		}

	}

	/** The edit. */
	private boolean	add_remove	= true;
	private boolean	update;
	private boolean	order;
	private boolean	re_order;
	private boolean	showfilter;

	/**
	 * Instantiates a new ui list.
	 * 
	 * @param key
	 *            the key
	 * @param label
	 *            the label
	 * @param poslabel
	 *            the poslabel
	 * @param mc
	 *            the mc
	 * @param ic
	 *            the ic
	 * @param edit
	 *            the edit
	 * @param showfilter
	 *            the showfilter
	 */
	public DListUI(String key, String label, EPosLabel poslabel, IModelController mc, IInteractionControllerForList ic,
			boolean edit, boolean showfilter) {
		super(key, label, poslabel, mc, ic);
		this.add_remove = edit;
		this.showfilter = showfilter;
		this.order = false;
	}

	/**
	 * Instantiates a new ui list.
	 * 
	 * @param key
	 *            the key
	 * @param label
	 *            the label
	 * @param poslabel
	 *            the poslabel
	 * @param mc
	 *            the mc
	 * @param ic
	 *            the ic
	 * @param edit
	 *            the edit
	 * @param showfilter
	 *            the showfilter
	 */
	public DListUI(String key, String label, EPosLabel poslabel, IModelController mc, IInteractionControllerForList ic,
			boolean edit, boolean showfilter, boolean order, boolean update) {
		super(key, label, poslabel, mc, ic);
		this.add_remove = edit;
		this.showfilter = showfilter;
		this.order = order;
		this.update = update;
	}

	public DListUI(String key, String label, EPosLabel poslabel, IModelController mc, IInteractionControllerForList ic,
			boolean edit, boolean showfilter, boolean order) {
		super(key, label, poslabel, mc, ic);
		this.add_remove = edit;
		this.showfilter = showfilter;
		this.order = true;

	}

	public DListUI(CompactUUID id, String shortName) {
		super(id, shortName);
	}

	/** The elements. */
	List<Object>			fElements;

	/** The package table. */
	private FilteredTree	packageTable;

	/** The button add. */
	private Button			buttonAdd;

	/** The button remove. */
	private Button			buttonRemove;
	private Button			buttonUp;
	private Button			buttonDown;
	private Button			buttonReOrder;
	private Button			buttonEdit;

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.UIField#getVisualValue()
	 */
	@Override
	public Object getVisualValue() {
		return fElements;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.UIField#getVSpan()
	 */
	@Override
	public int getVSpan() {
		int vspan = 0;
		if (isEditable()) {
			if (add_remove) {
				vspan++;
				vspan++;
			}
			if (order) {
				vspan++;
				vspan++;
			}
			if (re_order) {
				vspan++;
			}
			if (update) {
				vspan++;
			}
		}
		if (vspan == 0) {
			vspan = 1;
		}
		return vspan;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.UIField#createControl(fr.imag.adele.cadse.core.ui.IPageController,
	 *      fr.imag.adele.cadse.core.ui.IFedeFormToolkit, java.lang.Object, int)
	 */
	@Override
	public Composite createControl(final IPageController fieldController, IFedeFormToolkit toolkit, Object ocontainer,
			int hspan) {

		GridData gd;
		Composite container = (Composite) ocontainer;
		// IMPORTANT : impossible sinon de changer la valeur de
		// showFilterControls dans un constructeur.
		// car la methode create control est appeler dans le contructreur du
		// parent...
		_temp = showfilter;
		packageTable = new MyFilteredTree(container, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL, createPatternFilter(),
				showfilter);

		if (getPage().isLast(this)) {
			gd = new GridData(GridData.FILL_BOTH);
		} else {
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.heightHint = 100;
		}

		int vspan = 0;
		if (isEditable()) {
			if (add_remove) {
				buttonAdd = new Button(container, SWT.PUSH);
				buttonAdd.setText(ADD_BUTTON);
				buttonAdd.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(@SuppressWarnings("unused")
					SelectionEvent e) {
						handleAdd(fieldController);
					}
				});
				buttonAdd.setData(CADSE_MODEL_KEY, this);

				vspan++;

				buttonRemove = new Button(container, SWT.PUSH);
				buttonRemove.setText(REMOVE_BUTTON);
				buttonRemove.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(@SuppressWarnings("unused")
					SelectionEvent e) {
						ISelection sel = packageTable.getViewer().getSelection();
						if (sel == null) {
							return;
						}
						handleRemove((ITreeSelection) sel, fieldController);
					}
				});
				buttonRemove.setData(CADSE_MODEL_KEY, this);

				vspan++;

			}
			if (order) {
				buttonUp = new Button(container, SWT.PUSH);
				buttonUp.setText(UP_BUTTON);
				buttonUp.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(@SuppressWarnings("unused")
					SelectionEvent e) {
						ISelection sel = packageTable.getViewer().getSelection();
						if (sel == null) {
							return;
						}
						handleUp((ITreeSelection) sel, fieldController);
					}
				});
				buttonUp.setData(CADSE_MODEL_KEY, this);

				vspan++;

				buttonDown = new Button(container, SWT.PUSH);
				buttonDown.setText(DOWN_BUTTON);
				buttonDown.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(@SuppressWarnings("unused")
					SelectionEvent e) {
						ISelection sel = packageTable.getViewer().getSelection();
						if (sel == null) {
							return;
						}
						handleDown((ITreeSelection) sel, fieldController);
					}
				});
				buttonDown.setData(CADSE_MODEL_KEY, this);

				vspan++;
			}
			if (re_order) {
				buttonReOrder = new Button(container, SWT.PUSH);
				buttonReOrder.setText(RE_ORDER_BUTTON);
				buttonReOrder.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(@SuppressWarnings("unused")
					SelectionEvent e) {
						ISelection sel = packageTable.getViewer().getSelection();
						if (sel == null) {
							return;
						}
						handleReOrder((ITreeSelection) sel, fieldController);
					}
				});
				buttonReOrder.setData(CADSE_MODEL_KEY, this);
				vspan++;
			}
			if (update) {
				buttonEdit = new Button(container, SWT.PUSH);
				buttonEdit.setText(EDIT_BUTTON);
				buttonEdit.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(@SuppressWarnings("unused")
					SelectionEvent e) {
						ISelection sel = packageTable.getViewer().getSelection();
						if (sel == null) {
							return;
						}
						handleEdit((ITreeSelection) sel, fieldController);
					}
				});
				buttonEdit.setData(CADSE_MODEL_KEY, this);
				vspan++;
			}
		}
		if (vspan == 0) {
			vspan = 1;
		}
		gd.verticalSpan = vspan;
		gd.horizontalSpan = (vspan != 1) ? hspan - 1 : hspan; // un place pour
		// les boutons
		// ...
		packageTable.setLayoutData(gd);
		packageTable.getViewer().setLabelProvider(getLabelProvider());
		packageTable.getViewer().setContentProvider(((IInteractionControllerForList) _ic).getContentProvider());

		return container;

	}

	protected void handleReOrder(ITreeSelection sel, IPageController fieldController) {
		// TODO Auto-generated method stub

	}

	protected void handleEdit(ITreeSelection sel, IPageController fieldController) {
		if (((IStructuredSelection) sel).size() == 0) {
			Object value = ((IStructuredSelection) sel).getFirstElement();

			int index = fElements.indexOf(value);
			if (index == -1) {
				return;
			}
			value = ((IInteractionControllerForList) _ic).edit(packageTable.getShell(), value, index);
			if (value != null) {
				fElements.set(index, value);
				updateValue();
				fieldController.broadcastValueChanged(this, getVisualValue());
			}
		}

	}

	protected PatternFilter createPatternFilter() {
		return new PatternFilter();
	}

	/**
	 * Handle remove.
	 * 
	 * @param sel
	 *            the sel
	 * @param fieldController
	 *            the field controller
	 */
	protected void handleRemove(ITreeSelection sel, IPageController fieldController) {
		Object[] obj = sel.toArray();
		if (obj == null) {
			return;
		}
		if (obj.length == 0) {
			return;
		}

		String error = ((IInteractionControllerForList) _ic).canRemoveObject(obj);
		if (error != null) {
			fieldController.setMessage(error, IPageController.ERROR);
			return;
		}
		Object[] removedObj = ((IInteractionControllerForList) _ic).removeObject(obj);
		for (Object o : removedObj) {
			fElements.remove(o);
		}
		setVisualValue(fElements);
		fieldController.broadcastValueChanged(this, getVisualValue());
	}

	/**
	 * Handle add.
	 * 
	 * @param fieldController
	 *            the field controller
	 */
	protected void handleAdd(IPageController fieldController) {
		Object[] ret = selectOrCreateValue();
		if (ret != null) {
			String error = ((IInteractionControllerForList) _ic).canAddObject(ret);
			if (error != null) {
				fieldController.setMessage(error, IPageController.ERROR);
				return;
			}
			ret = ((IInteractionControllerForList) _ic).transAndAddObject(ret);
			if (fElements == null || (!(fElements instanceof ArrayList))) {
				fElements = new ArrayList<Object>();
			}
			fElements.addAll(Arrays.asList(ret));
			setVisualValue(fElements);
			fieldController.broadcastValueChanged(this, getVisualValue());
		}
	}

	protected void handleDown(ITreeSelection sel, IPageController fieldController) {
		Object[] obj = sel.toArray();
		if (obj == null) {
			return;
		}
		if (obj.length == 0) {
			return;
		}
		if (((IInteractionControllerForList) _ic).moveDown(obj)) {
			updateValue();
		}

	}

	@Override
	public void updateValue() {
		ITreeSelection sel = (ITreeSelection) packageTable.getViewer().getSelection();
		super.updateValue();
		packageTable.getViewer().setSelection(sel, true);
	}

	protected void handleUp(ITreeSelection sel, IPageController fieldController) {
		Object[] obj = sel.toArray();
		if (obj == null) {
			return;
		}
		if (obj.length == 0) {
			return;
		}
		if (((IInteractionControllerForList) _ic).moveUp(obj)) {
			updateValue();
		}
	}

	/**
	 * Select or create value.
	 * 
	 * @return the object[]
	 */
	private Object[] selectOrCreateValue() {
		return ((IInteractionControllerForList) _ic).selectOrCreateValues(packageTable.getShell());
	}

	/**
	 * Gets the label provider.
	 * 
	 * @return the label provider
	 */
	protected ILabelProvider getLabelProvider() {
		return ((IInteractionControllerForList) _ic).getLabelProvider();
	}

	public void setVisualValue(Object visualValue, boolean sendNotification) {
		if (visualValue == null) {
			visualValue = new ArrayList<Object>();
		}
		assert visualValue instanceof List;
		fElements = (List<Object>) visualValue;
		/*
		 * On peur fournir une liste non modifiable comme une liste vide.
		 */
		if (fElements != null && !(fElements instanceof ArrayList)) {
			fElements = new ArrayList<Object>(fElements);
		}
		TreeViewer viewer = packageTable.getViewer();
		if (viewer.getContentProvider() != null) {
			viewer.setInput(fElements);
		} else {

		}
	}

	@Override
	public boolean isRunning() {
		return super.isRunning() && packageTable.getViewer().getContentProvider() != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.UIField#getUIObject(int)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.UIField#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean v) {
		// packageTable.setEnabled(v || order);
		if (buttonAdd != null) {
			buttonAdd.setEnabled(v);
		}
		if (buttonRemove != null) {
			buttonRemove.setEnabled(v);
		}
		if (buttonDown != null) {
			buttonDown.setEnabled(v || order);
		}
		if (buttonUp != null) {
			buttonUp.setEnabled(v || order);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.UIField#setEditable(boolean)
	 */
	@Override
	public void internalSetEditable(boolean v) {
		if (buttonAdd != null) {
			buttonAdd.setEnabled(v);
		}
		if (buttonRemove != null) {
			buttonRemove.setEnabled(v);
		}
		if (buttonDown != null) {
			buttonDown.setEnabled(v);
		}
		if (buttonUp != null) {
			buttonUp.setEnabled(v);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fede.workspace.model.manager.properties.impl.ui.DAbstractField#setVisible(boolean)
	 */
	@Override
	public void internalSetVisible(boolean v) {
		super.internalSetVisible(v);
		packageTable.setEnabled(v);
		if (buttonAdd != null) {
			buttonAdd.setEnabled(v);
		}
		if (buttonRemove != null) {
			buttonRemove.setEnabled(v);
		}
		if (buttonDown != null) {
			buttonDown.setEnabled(v);
		}
		if (buttonUp != null) {
			buttonUp.setEnabled(v);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.ui.UIField#getHSpan()
	 */
	@Override
	public int getHSpan() {
		return 2;
	}

	public ItemType getType() {
		return CadseRootCST.DLIST;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see model.workspace.workspace.managers.ui.DisplayManager#getDefaultPosLabel()
	 */
	@Override
	protected EPosLabel getDefaultPosLabel() {
		return EPosLabel.top;
	}

	@Override
	public <T> T internalGetOwnerAttribute(IAttributeType<T> type) {
		if (CadseRootCST.DLIST_at_EDITABLE_BUTTON_ == type) {
			return (T) Boolean.valueOf(add_remove);
		}
		if (CadseRootCST.DLIST_at_ORDER_BUTTON_ == type) {
			return (T) Boolean.valueOf(order);
		}
		if (CadseRootCST.DLIST_at_UPDATE_BUTTON_ == type) {
			return (T) Boolean.valueOf(update);
		}
		if (CadseRootCST.DLIST_at_SHOW_FILTER_ == type) {
			return (T) Boolean.valueOf(showfilter);
		}

		return super.internalGetOwnerAttribute(type);
	}

	@Override
	public Control getMainControl() {
		return this.packageTable;
	}

	@Override
	public Object[] getSelectedObjects() {
		return ((StructuredSelection) this.packageTable.getViewer().getSelection()).toArray();
	}

	@Override
	public boolean commitSetAttribute(IAttributeType<?> type, String key, Object value) {
		if (type == (CadseRootCST.DLIST_at_EDITABLE_BUTTON_)) {
			add_remove = Convert.toBoolean(value, CadseRootCST.DLIST_at_EDITABLE_BUTTON_, true);
			return true;
		}
		if (type == (CadseRootCST.DLIST_at_ORDER_BUTTON_)) {
			order = Convert.toBoolean(value, CadseRootCST.DLIST_at_ORDER_BUTTON_, true);
			return true;
		}
		if (type == (CadseRootCST.DLIST_at_UPDATE_BUTTON_)) {
			update = Convert.toBoolean(value, CadseRootCST.DLIST_at_UPDATE_BUTTON_, true);
			return true;
		}
		if (type == (CadseRootCST.DLIST_at_SHOW_FILTER_)) {
			showfilter = Convert.toBoolean(value, CadseRootCST.DLIST_at_SHOW_FILTER_, true);
			return true;
		}
		return super.commitSetAttribute(type, key, value);
	}

}
