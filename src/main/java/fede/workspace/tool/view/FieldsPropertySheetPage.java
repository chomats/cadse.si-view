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
package fede.workspace.tool.view;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.ScrolledPageBook;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.IPropertySource;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.ChangeID;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.WorkspaceListener;
import fr.imag.adele.cadse.core.transaction.delta.ImmutableItemDelta;
import fr.imag.adele.cadse.core.transaction.delta.ImmutableWorkspaceDelta;
import fr.imag.adele.cadse.core.impl.CadseCore;
import fr.imag.adele.cadse.core.ui.IFieldDescription;
import fr.imag.adele.cadse.core.ui.view.FilterContext;
import fr.imag.adele.cadse.eclipse.view.AbstractCadseTreeViewUI;
import fr.imag.adele.cadse.eclipse.view.AbstractCadseView;
import fr.imag.adele.fede.workspace.as.eclipse.SWTService;
import fr.imag.adele.fede.workspace.as.eclipse.SWTService.MyPropertySheetPage;
import fr.imag.adele.fede.workspace.si.view.View;

/**
 * @see IPropertySource
 */
public class FieldsPropertySheetPage extends Page implements IPropertySheetPage {

	IPropertySheetPage		current;;
	Item					lastItem;
	PageBook				pageBook		= null;
	private MyPropertySheetPage	propertySource;

	public FieldsPropertySheetPage() {
	}

	@Override
	public void createControl(Composite parent) {
		pageBook = new PageBook(parent, 0);
		pageBook.setLayoutData(new GridData(GridData.FILL_BOTH));
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
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setController(AbstractCadseTreeViewUI view, IItemNode itemNode) throws CadseException, PartInitException {
		if (propertySource != null)
			propertySource.dispose();
		propertySource = null;
		View viewService = View.getInstance();
		if (viewService == null || viewService.getSwtService() == null)
			return;
		propertySource = viewService.getSwtService().createPropertySheetPage(view, itemNode);
		propertySource.init(getSite());
		
		FormToolkit toolkit = new FormToolkit(pageBook.getDisplay());
		
		ScrolledForm scrolledFrom = toolkit.createScrolledForm(pageBook);
		scrolledFrom.setLayoutData(new GridData(GridData.FILL_BOTH));
		scrolledFrom.setAlwaysShowScrollBars(false);
		scrolledFrom.getBody().setLayout(new GridLayout());
		
		propertySource.createControl(scrolledFrom.getBody());
		pageBook.showPage(scrolledFrom);
		
		
	}

	@Override
	public void dispose() {
		super.dispose();
		pageBook.dispose();
		if (propertySource != null)
			propertySource.dispose();
	}

}
