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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;

import fede.workspace.model.manager.properties.IC_ForCheckedViewer;
import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.CompactUUID;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.ui.EPosLabel;
import fr.imag.adele.cadse.core.ui.IFedeFormToolkit;
import fr.imag.adele.cadse.core.ui.IModelController;
import fr.imag.adele.cadse.core.ui.IPageController;

/**
 * This class handles a tabbed panel containing a list of selected items
 * belonging to the configuration, choosen from the associated domain.
 * 
 * @author vega
 * 
 */
public class DCheckedListUI extends DAbstractField implements SelectionListener {

	private boolean					_edit				= false;

	private int						_heightHint			= 200;

	private Object[]				_sources;
	private Set<Object>				_sources_selected	= new HashSet<Object>();
	private Map<Object, TreeItem>	_treeItems;
	private Tree					_treeObjects;

	private IC_ForCheckedViewer		_uiControler;
	private int						_widthHint			= 400;

	public DCheckedListUI(CompactUUID id, String shortName) {
		super(id, shortName);
	}

	public DCheckedListUI(String key, String label, EPosLabel poslabel, IModelController mc, IC_ForCheckedViewer ic) {
		super(key, label, poslabel, mc, ic);
	}

	/**
	 * 
	 * @param object
	 *            l'object deselectionne.
	 * @return Un message d'erreur si impossible or null.
	 */
	protected String canObjectDeselected(Object object) {
		return null;
	}

	/**
	 * 
	 * @param object
	 *            l'object selectionne.
	 * @return Un message d'erreur si impossible or null.
	 */
	protected String canObjectSelected(Object object) {
		return null;
	}

	@Override
	public Object createControl(IPageController globalUIController, IFedeFormToolkit toolkit, Object container,
			int hspan) {
		GridData gd;

		_treeObjects = new Tree((Composite) container, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		_treeObjects.addSelectionListener(this);
		_treeObjects.setData(CADSE_MODEL_KEY, this);

		boolean edit = this._edit;// ",false);

		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = hspan - 1;
		gd.verticalSpan = edit ? 3 : 2;
		gd.heightHint = this._heightHint;// 200);
		gd.widthHint = this._widthHint;// ,400);

		_treeObjects.setLayoutData(gd);

		setSource(_uiControler.getSources());

		Button selectAll = ((FormToolkit) toolkit).createButton((Composite) container, SELECT_ALL_BUTTON, SWT.PUSH);
		gd = new GridData(GridData.CENTER);
		selectAll.setLayoutData(gd);
		selectAll.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				selectAll();
			}
		});
		selectAll.setData(CADSE_MODEL_KEY, this);

		Button deselectAll = ((FormToolkit) toolkit).createButton((Composite) container, DESELECT_ALL_BUTTON, SWT.PUSH);
		gd = new GridData(GridData.CENTER);
		deselectAll.setLayoutData(gd);
		deselectAll.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				deselectAll();
			}
		});
		deselectAll.setData(CADSE_MODEL_KEY, this);

		if (edit) {
			Button editButton = ((FormToolkit) toolkit).createButton((Composite) container, EDIT_BUTTON, SWT.PUSH);
			gd = new GridData(GridData.CENTER);
			editButton.setLayoutData(gd);
			editButton.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
				}

				public void widgetSelected(SelectionEvent e) {
					edit();
				}
			});
			editButton.setData(CADSE_MODEL_KEY, this);
		}
		return _treeObjects;
	}

	protected TreeItem createTreeItem(Object obj) {
		TreeItem ti = new TreeItem(this._treeObjects, treeItemDefaultStyle());
		ti.setText(toStringFromObject(obj));
		ti.setData(obj);
		ti.setImage(toImageFromObject(obj));
		_treeItems.put(obj, ti);
		return ti;
	}

	protected void deselectAll() {
		for (TreeItem ti : this._treeItems.values()) {
			ti.setChecked(false);
		}
		globalController.broadcastSubValueRemoved(this, getVisualValue());
		_sources_selected.clear();
	}

	@Override
	public void dispose() {
		super.dispose();
		_sources = null;
		_sources_selected.clear();
		_treeItems = null;
		_treeObjects = null;
	}

	protected void edit() {
		Object o = null;
		TreeItem[] sel = _treeObjects.getSelection();
		if (sel != null) {
			for (int i = 0; i < sel.length; i++) {
				if (sel[i].getChecked()) {
					o = sel[i].getData();
				}
			}
			if (o != null) {
				_uiControler.edit(o);
			}
		}

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
	public int getHSpan() {
		return 2;
	}

	@Override
	public Control getMainControl() {
		return this._treeObjects;
	}

	@Override
	public Object[] getSelectedObjects() {
		return _sources_selected.toArray();
	}

	public Object[] getSource() {
		return _sources;
	}

	public ItemType getType() {
		return CadseGCST.DCHECKED_LIST;
	}

	@Override
	public Object getUIObject(int index) {
		return _treeObjects;
	}

	@Override
	public Object getVisualValue() {
		return this._sources_selected.toArray();
	}

	@Override
	public int getVSpan() {
		return _edit ? 3 : 2;
	}

	@Override
	public void init(IPageController globalController) {
		super.init(globalController);
		_uiControler = (IC_ForCheckedViewer) getInteractionController();
	}

	@Override
	public void internalSetEditable(boolean v) {
		_treeObjects.setEnabled(v);
	}

	@Override
	public void internalSetVisible(boolean v) {
		super.internalSetVisible(v);
		_treeObjects.setVisible(v);
	}

	protected void objectDeselected(Object removed) {
		_sources_selected.remove(removed);
		globalController.broadcastSubValueRemoved(this, removed);
	}

	protected void objectSelected(Object added) {
		_sources_selected.add(added);
		globalController.broadcastSubValueAdded(this, added);
	}

	protected void selectAll() {
		for (TreeItem ti : this._treeItems.values()) {
			ti.setChecked(true);
		}

		_sources_selected.addAll(Arrays.asList(_sources));
		globalController.broadcastSubValueAdded(this, getVisualValue());
	}

	public void selectObject(Object object, boolean sel) {
		if (this._treeObjects.isDisposed()) {
			return;
		}

		TreeItem ti = this._treeItems.get(object);
		if (ti == null) {
			return;
		}
		ti.setChecked(sel);
		if (sel) {
			_sources_selected.add(object);
		} else {
			_sources_selected.remove(object);
		}
	}

	@Override
	public void setEnabled(boolean v) {
		_treeObjects.setEnabled(v);
	}

	public void setSource(Object[] source) {
		Object[] oldSource = this._sources;
		this._sources = source;

		/*
		 * Skip unnecessary updates due to selection refresh
		 */
		if ((oldSource == null) && (source == null)) {
			return;
		}

		if ((oldSource != null) && (source != null) && (Arrays.asList(oldSource).equals(Arrays.asList(source)))) {
			return;
		}

		/*
		 * Set view input element and initialize checkd state
		 */
		Arrays.sort(_sources, new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				return toStringFromObject(o1).compareTo(toStringFromObject(o2));
			}
		});

		if (_treeItems == null) {
			_treeItems = new HashMap<Object, TreeItem>();
		} else {
			_treeItems.clear();
		}

		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
				if (_treeObjects.isDisposed()) {
					return;
				}

				_treeObjects.removeAll();
				for (Object object : _sources) {
					createTreeItem(object);
				}
				for (Object obj : new ArrayList<Object>(_sources_selected)) {
					TreeItem ti = _treeItems.get(obj);
					if (ti == null) {
						_sources_selected.remove(obj);
						continue;
					}
					ti.setChecked(true);
				}
			};
		});

	}

	public void setVisualValue(final Object visualValue, boolean sendNotification) {
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
				Object[] selectedObject = null;
				if (visualValue instanceof List) {
					selectedObject = ((List) visualValue).toArray();
				} else if (visualValue instanceof Object[]) {
					selectedObject = (Object[]) visualValue;
				}
				_sources_selected.clear();
				if (selectedObject == null) {
					return;
				}
				for (Object obj : selectedObject) {
					selectObject(obj, true);
				}
			};
		});
	}

	protected Image toImageFromObject(Object obj) {
		return _uiControler.toImageFromObject(obj);
	}

	protected String toStringFromObject(Object element) {
		return _uiControler.toStringFromObject(element);
	}

	protected int treeItemDefaultStyle() {
		return SWT.NONE;
	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent event) {
		if (event.detail == SWT.CHECK) {

			TreeItem item = (TreeItem) event.item;
			Object obj = item.getData();
			if (obj != null) {
				if (item.getChecked()) {
					String error = canObjectSelected(obj);
					if (error == null) {
						objectSelected(obj);
					} else {
						item.setChecked(false);
						globalController.setMessage(error, IPageController.ERROR);
					}
				} else {
					String error = canObjectDeselected(obj);
					if (error == null) {
						objectDeselected(obj);
					} else {
						item.setChecked(true);
						globalController.setMessage(error, IPageController.ERROR);
					}
				}
			}
		}
		TreeItem item = (TreeItem) event.item;
		if (item != null && item.getChecked()) {
			_uiControler.select(item.getData());
		}
	}

}