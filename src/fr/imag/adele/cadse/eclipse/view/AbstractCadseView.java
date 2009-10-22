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
package fr.imag.adele.cadse.eclipse.view;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;

import fede.workspace.model.manager.properties.impl.ui.FieldsPropertySheetPage;
import fede.workspace.tool.view.node.AbstractCadseViewNode;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.LogicalWorkspace;
import fr.imag.adele.cadse.core.impl.CadseCore;

/**
 * Cette vue represente les item du workspace courant. Nous avons trois mode
 * d'affichage : - aggregations; - relations et link - relations et relations
 * inverse.
 * 
 * Le menu contextuel a une zone particuli?re. "WS-Actions" pour les actions du
 * workspace.
 * 
 */

public abstract class AbstractCadseView extends ViewPart {
	static public final char		MEM_CATEGORY	= 'c';
	static public final char		MEM_ITEM		= 'i';
	static public final char		MEM_ITEMTYPE	= 't';
	static public final char		MEM_LINK		= 'l';
	static public final char		MEM_LINKTYPE	= 'z';

	private AbstractCadseTreeViewUI	uicontroller;

	/**
	 * The constructor.
	 */
	public AbstractCadseView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(Composite parent) {
		this.uicontroller.createPartControl(parent);
	}

	@Override
	public void dispose() {
		super.dispose();
		this.uicontroller.dispose();
	}

	public void setToolTip(boolean toolTip) {
		this.uicontroller.setToolTip(toolTip);

	}

	protected void fillContextMenu(IMenuManager manager) {
		this.uicontroller.fillContextMenu(manager);
	}

	protected void fillLocalPullDown(IMenuManager manager) {
		this.uicontroller.fillLocalPullDown(manager);
	}

	protected void fillLocalToolBar(IToolBarManager manager) {
		this.uicontroller.fillLocalToolBar(manager);
	}

	public TreeViewer getFTreeViewer() {
		return this.uicontroller.getFTreeViewer();
	}

	/**
	 * Restore the state of this view : three options : show all items, show the
	 * relations "to", show the relations "from"
	 */
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		this.uicontroller = createUIController(site);
		this.uicontroller.loadState(memento);
		this.uicontroller.setViewPart(this);
	}

	abstract protected AbstractCadseTreeViewUI createUIController(IViewSite site);

	// this.uicontroller = new
	// AbstractCadseTreeViewUI(site,site.getWorkbenchWindow(), site);

	/**
	 * Save the state of this view through the sessions : three options : show
	 * all items, show the relations "to", show the relations "from"
	 */
	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);
		this.uicontroller.saveState(memento);
	}

	protected void openEditorItem() {
		this.uicontroller.openEditorItem();
	}

	public void refresh() {
		this.uicontroller.refresh();
	}

	public void reset() {
		this.uicontroller.reset();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		this.uicontroller.setFocus();

	}

	@Override
	public void setTitleImage(Image titleImage) {
		super.setTitleImage(titleImage);
	}

	@Override
	public void setTitleToolTip(String toolTip) {
		super.setTitleToolTip(toolTip);
	}

	public int getContentProviderFlag() {
		return this.uicontroller.getContentProviderFlag();
	}

	public void refresh(final AbstractCadseViewNode iiv) {
		this.uicontroller.refresh(iiv);
	}

	public void updateTree() {
		this.uicontroller.updateTree();
	}

	public AbstractCadseViewNode getRootWS() {
		return this.uicontroller.getRootWS();
	}

	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == IPropertySheetPage.class) {
			return new FieldsPropertySheetPage();
		}
		return super.getAdapter(adapter);
	}

	public void refresh(Item item) {
		this.uicontroller.refresh(item);
	}

	public LogicalWorkspace getCadseModel() {
		return CadseCore.getCadseDomain().getLogicalWorkspace();
	}

	/**
	 * Return the view controller
	 * 
	 * @return
	 */

	public AbstractCadseTreeViewUI getViewController() {
		return uicontroller;
	}
}