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

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ScrolledPageBook;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.IPropertySource;

import fede.workspace.tool.eclipse.FedeFormToolkit;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.ChangeID;
import fr.imag.adele.cadse.core.DefaultItemManager;
import fr.imag.adele.cadse.core.IItemManager;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.WorkspaceListener;
import fr.imag.adele.cadse.core.delta.ImmutableItemDelta;
import fr.imag.adele.cadse.core.delta.ImmutableWorkspaceDelta;
import fr.imag.adele.cadse.core.ui.IFieldDescription;
import fr.imag.adele.cadse.core.ui.IPageController;
import fr.imag.adele.cadse.core.ui.Pages;
import fr.imag.adele.cadse.core.ui.UIField;
import fr.imag.adele.cadse.core.ui.view.FilterContext;
import fr.imag.adele.cadse.eclipse.view.AbstractCadseTreeViewUI;
import fr.imag.adele.cadse.eclipse.view.AbstractCadseView;
import fr.imag.adele.fede.workspace.si.view.View;

/**
 * The standard implementation of property sheet page which presents a table of
 * property names and values obtained from the current selection in the active
 * workbench part.
 * <p>
 * This page obtains the information about what to properties display from the
 * current selection (which it tracks).
 * </p>
 * <p>
 * The model for this page is a hierarchy of <code>IPropertySheetEntry</code>.
 * The page may be configured with a custom model by setting the root entry.
 * <p>
 * If no root entry is set then a default model is created which uses the
 * <code>IPropertySource</code> interface to obtain the properties of the
 * current slection. This requires that the selected objects provide an
 * <code>IPropertySource</code> adapter (or implement
 * <code>IPropertySource</code> directly). This restiction can be overcome by
 * providing this page with an <code>IPropertySourceProvider</code>. If
 * supplied, this provider will be used by the default model to obtain a
 * property source for the current selection
 * </p>
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * @see IPropertySource
 */
public class FieldsPropertySheetPage extends Page implements IPropertySheetPage, IPageController {

	FedeFormToolkit		myFormToolkit;
	IItemNode			lastItemNode;
	Item				lastItem;
	ScrolledPageBook	pageBook		= null;
	WorkspaceListener	_listener;
	boolean				_initListener	= false;
	private Pages		lastItemPages;

	@Override
	public void createControl(Composite parent) {
		myFormToolkit = new FedeFormToolkit(parent.getDisplay());
		pageBook = myFormToolkit.createPageBook(parent, SWT.V_SCROLL);
		pageBook.setLayoutData(new GridData(GridData.FILL_BOTH));
		_listener = new WorkspaceListener() {
			@Override
			public void workspaceChanged(ImmutableWorkspaceDelta delta) {
				for (final ImmutableItemDelta itemDelta : delta.getItems()) {
					if (itemDelta.isDeleted()) {
						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
							public void run() {
								pageBook.removePage(itemDelta.getItem());
							}
						});
					}
				}

			}
		};
		initListener();
	}

	private void initListener() {
		if (_initListener) {
			return;
		}
		if (View.getInstance() == null) {
			return;
		}
		if (View.getInstance().getWorkspaceLogique() == null) {
			return;
		}
		View.getInstance().getWorkspaceLogique().addListener(_listener, ChangeID.toFilter(ChangeID.DELETE_ITEM));
	}

	protected Composite createEmptyComposite(Composite parent) {
		Composite container = new Composite(parent, SWT.NO_BACKGROUND);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.verticalSpacing = 9;
		Label t = new Label(container, SWT.NONE);
		t.setText("No item selected.");
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_CENTER + GridData.VERTICAL_ALIGN_CENTER);
		t.setLayoutData(gd);
		return container;
	}

	@Override
	public Control getControl() {
		return pageBook;
	}

	@Override
	public void setFocus() {
		pageBook.setFocus();
	}

	private LinkType getContainmentLinkTypeParent(Item item) {
		for (Link l : item.getIncomingLinks()) {
			if (l.getLinkType().isPart()) {
				return l.getLinkType();
			}
		}
		return null;
	}

	protected IItemNode descFormSel(ISelection selection) {
		if (!(selection instanceof IStructuredSelection)) {
			return null;
		}
		IStructuredSelection sssel = (IStructuredSelection) selection;
		Object element = sssel.getFirstElement();
		if (element == null || !(element instanceof IItemNode)) {
			return null;
		}
		IItemNode iiv = (IItemNode) element;

		return iiv;
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		initListener();
		AbstractCadseTreeViewUI view = null;
		if (part instanceof AbstractCadseView) {
			view = ((AbstractCadseView) part).getViewController();
		}
		IItemNode desc = descFormSel(selection);
		try {
			setController(view, desc);
		} catch (CadseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setController(AbstractCadseTreeViewUI view, IItemNode itemNode) throws CadseException {
		if (this.pageBook == null) {
			return;
		}
		if (view == null) {
			return;
		}
		if (itemNode == null) {
			return;
		}
		if (itemNode.getItem() != null && itemNode.getItem() == lastItem) {
			return;
		}
		Item item = itemNode.getItem();
		if (item == null || !item.isResolved()) {
			pageBook.showEmptyPage();
			return;
		}

		if (lastItem != null) {
			pageBook.removePage(lastItem, false);
		}
		if (lastItemPages != null) {
			lastItemPages.dispose();
		}
		lastItemNode = itemNode;
		lastItem = item;

		lastItemPages = null;
		IItemManager im = item.getType().getItemManager();
		if (im instanceof DefaultItemManager) {
			lastItemPages = ((DefaultItemManager) im).createModificationPage(item);
		}
		if (lastItemPages == null) {
			lastItemPages = lastItem.getType().getGoodModificationPage(itemNode);
		}
		lastItemPages.setItem(lastItem);
		lastItemPages.putLocal(IFieldDescription.PARENT_CONTEXT, lastItem.getPartParent());
		lastItemPages.putLocal(IFieldDescription.INCOMING_LINK_TYPE, getContainmentLinkTypeParent(lastItem));

		lastItemPages.setFilterContext(new FilterContext(lastItem, null, view, null, null, itemNode, lastItemPages));
		if (!pageBook.hasPage(lastItem)) {
			Composite page = pageBook.createPage(lastItem);
			page.setLayout(new GridLayout());
			myFormToolkit.adapt(page);
			FieldsController.createControlPage(this, this.myFormToolkit, page, lastItemPages);
			page.setData("mel-prop-desc", lastItemPages);
			page.setData("mel-prop-", this);
		}
		pageBook.showPage(lastItem);

	}

	@Override
	public void dispose() {
		super.dispose();
		pageBook.dispose();

		View.getInstance().getWorkspaceDomain().getLogicalWorkspace().removeListener(_listener);
	}

	public boolean broadcastSubValueAdded(UIField field, Object added) {
		clearMessage();
		return field.broadcastSubValueAdded(this, added);
	}

	private void clearMessage() {
		IStatusLineManager statusLine = getSite().getActionBars().getStatusLineManager();
		if (statusLine != null) {
			statusLine.setErrorMessage(null);
			statusLine.setMessage(null);
		}
	}

	public boolean broadcastSubValueRemoved(UIField field, Object removed) {
		clearMessage();
		return field.broadcastSubValueRemoved(this, removed);
	}

	public void broadcastThisFieldHasChanged(final UIField fd) {
		if (!fd.isRunning()) {
			throw new IllegalStateException("ui field not running " + fd);
		}
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
				fd.updateValue();
			}
		});
	}

	public boolean broadcastValueChanged(UIField field, Object value) {
		clearMessage();
		return field.broadcastValueChanged(this, value);
	}

	public boolean broadcastValueDeleted(UIField field, Object oldvalue) {
		clearMessage();
		return field.broadcastValueDeleted(this, oldvalue);
	}

	public String getMessage() {
		return null;
	}

	public int getMessageType() {
		return 0;
	}

	public void setMessage(String newMessage, int newType) {
		IStatusLineManager statusLine = getSite().getActionBars().getStatusLineManager();
		if (statusLine != null) {
			Image newImage = null;
			if (newMessage != null) {
				switch (newType) {
					case IMessageProvider.NONE:
						break;
					case IMessageProvider.INFORMATION:
						newImage = JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_INFO);
						break;
					case IMessageProvider.WARNING:
						newImage = JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_WARNING);
						break;
					case IMessageProvider.ERROR:
						newImage = JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_ERROR);
						break;
				}
			}
			if (newType == IPageController.ERROR) {
				statusLine.setErrorMessage(newImage, newMessage);
			} else {
				statusLine.setMessage(newImage, newMessage);
			}
		}

	}

}
