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

import org.eclipse.jface.action.IMenuListener2;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import fede.workspace.model.manager.properties.impl.ic.IC_ContextMenu;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.CadseRootCST;
import fr.imag.adele.cadse.core.CompactUUID;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.impl.CollectedReflectLink;
import fr.imag.adele.cadse.core.impl.ReflectLink;
import fr.imag.adele.cadse.core.impl.ui.UIFieldImpl;
import fr.imag.adele.cadse.core.ui.EPosLabel;
import fr.imag.adele.cadse.core.ui.IFieldDescription;
import fr.imag.adele.cadse.core.ui.IInteractionController;
import fr.imag.adele.cadse.core.ui.IModelController;
import fr.imag.adele.cadse.core.ui.IPage;

public abstract class DAbstractField extends UIFieldImpl {

	protected Label		_labelWidget;
	private MenuManager	_menumanager	= null;

	public DAbstractField(CompactUUID uuid, String key) {
		super(uuid, key, null, null, null, null);
	}

	public DAbstractField(CompactUUID uuid, String key, String label, EPosLabel poslabel, IModelController mc,
			IInteractionController ic) {
		super(uuid, key, label, poslabel, mc, ic);
	}

	public DAbstractField(String key, String label, EPosLabel poslabel, IModelController mc, IInteractionController ic) {
		super(CompactUUID.randomUUID(), key, label, poslabel, mc, ic);
	}

	@Override
	protected void collectOutgoingLinks(LinkType linkType, CollectedReflectLink ret) {
		if (linkType == CadseRootCST.DISPLAY_lt_IC) {
			ret.addOutgoing(CadseRootCST.DISPLAY_lt_IC, _ic);
		}
		if (linkType == CadseRootCST.DISPLAY_lt_MC && _mc != null && !_mc.isAnonymous()) {
			ret.addOutgoing(CadseRootCST.DISPLAY_lt_MC, _mc);
		}
		super.collectOutgoingLinks(linkType, ret);
	}

	@Override
	public Link commitLoadCreateLink(LinkType lt, Item destination) throws CadseException {
		if (lt == CadseRootCST.DISPLAY_lt_IC) {
			_ic = (IInteractionController) destination;
			return new ReflectLink(lt, this, destination, 0);
		}
		if (lt == CadseRootCST.DISPLAY_lt_MC) {
			_mc = (IModelController) destination;
			return new ReflectLink(lt, this, destination, 0);
		}

		return super.commitLoadCreateLink(lt, destination);
	}

	protected void createContextMenu(Control parent) {
		if (this._ic instanceof IC_ContextMenu) {
			_menumanager = new MenuManager();
			final IC_ContextMenu contextMenu = ((IC_ContextMenu) this._ic);
			_menumanager.setRemoveAllWhenShown(contextMenu.hasRemoveAllWhenShown());
			_menumanager.createContextMenu(parent);
			_menumanager.addMenuListener(new IMenuListener2() {

				public void menuAboutToHide(IMenuManager manager) {
					contextMenu.menuAboutToHide(getSelectedObjects(), manager);
				}

				public void menuAboutToShow(IMenuManager manager) {
					contextMenu.menuAboutToShow(getSelectedObjects(), manager);
				}

			});
			getMainControl().setMenu(_menumanager.getMenu());
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		if (_menumanager != null) {
			_menumanager.dispose();
		}
		_menumanager = null;
		_labelWidget = null;
	}

	public abstract Control getMainControl();

	public abstract Object[] getSelectedObjects();

	@Override
	public void internalSetEditable(boolean v) {
		if (this._labelWidget != null) {
			this._labelWidget.setEnabled(editable);
		}
	}

	@Override
	public void internalSetVisible(boolean v) {
		if (_labelWidget != null) {
			_labelWidget.setVisible(v);
		}
	}

	@Override
	public void put(String key, Object value) {
		if (key == IFieldDescription.LABEL_WIDGET) {
			_labelWidget = (Label) value;
		}
		super.put(key, value);
	}

	@Override
	public void removeOutgoingLink(Link link, boolean notifie) {
		Item destination = link.getDestination();
		LinkType lt = link.getLinkType();
		if (lt == CadseRootCST.DISPLAY_lt_IC && destination.isResolved()) {
			_ic = null;
			return;
		}
		if (lt == CadseRootCST.DISPLAY_lt_MC && destination.isResolved()) {
			_mc = null;
			return;
		}
		super.removeOutgoingLink(link, notifie);
	}

	@Override
	public void setEnabled(boolean v) {
		if (this._labelWidget != null) {
			this._labelWidget.setEnabled(v);
		}
	}

	@Override
	public void setLabel(String label) {
		super.setLabel(label);
		if (_labelWidget != null) {
			_labelWidget.setText(label);
			_labelWidget.redraw();
		}
	}

	public void setLabelWidget(Label labelWidget) {
		this._labelWidget = labelWidget;
	}

	/**
	 * return a desciption of this field
	 */
	@Override
	public String toString() {
		IPage p = getPage();
		StringBuilder sb = new StringBuilder();
		sb.append("uifield ").append(getName());
		sb.append(" of type ");
		ItemType it = getType();
		if (it != null) {
			sb.append(it.getDisplayName());
		} else {
			sb.append(getClass().getSimpleName());
		}

		if (p != null) {
			sb.append(" in page ").append(p.getTitle()).append("(").append(p.getClass().getSimpleName()).append(")");
			if (p.getParentItemType() != null) {
				sb.append("in item type ").append(p.getParentItemType().getDisplayName());
			}
		}

		return sb.toString();
	}

	@Override
	public void updateValue() {
		Display d = Display.getCurrent();
		if (d == null) {
			d = Display.getDefault();
		}
		if (!isRunning()) {
			throw new IllegalStateException("ui field not running " + this);
		}
		d.asyncExec(new Runnable() {
			public void run() {
				setVisualValue(getValueForVisual(), false);
			}
		});
	}
}
