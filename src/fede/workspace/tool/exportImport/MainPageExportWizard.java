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
/**
 *
 */
package fede.workspace.tool.exportImport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;

import fede.workspace.tool.view.ItemInViewer;
import fede.workspace.tool.view.node.OldItemInViewer;
import fede.workspace.tool.view.node.RootNode;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.LogicalWorkspace;
import fr.imag.adele.cadse.eclipse.view.AbstractCadseTreeViewUI;
import fr.imag.adele.cadse.eclipse.view.ViewContentProvider;
import fr.imag.adele.cadse.eclipse.view.ViewLabelProvider;

class TT extends AbstractCadseTreeViewUI {

	TT(IShellProvider shellprovider, IWorkbenchWindow workbenchWindow, IWorkbenchPartSite workbenchPartSite,
			IViewSite viewsite) {
		super(shellprovider, workbenchWindow, workbenchPartSite, viewsite);
	}

	@Override
	public boolean isRefItemType(ItemType it, LogicalWorkspace cadseModel) {
		return false;
	}

	@Override
	protected boolean isLink(Link link) {
		return false;
	}

	@Override
	public boolean isItemType(ItemType it, LogicalWorkspace cadseModel) {
		return false;
	}

	@Override
	public boolean isFirstItemType(ItemType it, LogicalWorkspace cadseModel) {
		return false;
	}

	@Override
	public boolean isCreateLink(LinkType lt) {
		return false;
	}

	@Override
	public boolean isAggregationLink(Link link) {
		return false;
	}

	@Override
	public ItemType[] getFirstItemType(LogicalWorkspace cadseModel) {
		return null;
	}

	@Override
	public String getDislplayCreate(LinkType link) {
		return null;
	}

	public int isSelected(IItemNode node) {
		return IItemNode.DESELECTED;
	}

}

class MainPageExportWizard extends MainPageExportImportWizard {

	private CheckboxTreeViewer		fTreeViewer;
	List<ItemInViewer>				root;
	List<Item>						selectedExport;
	private AbstractCadseTreeViewUI	uicontroller;

	protected MainPageExportWizard(List<Item> selectedExport, IShellProvider shellprovider,
			IWorkbenchWindow workbenchWindow, IWorkbenchPartSite workbenchPartSite, IViewSite viewsite) {
		super("Exports items and resources");
		this.selectedExport = selectedExport;
		root = new ArrayList<ItemInViewer>();
		uicontroller = new TT(shellprovider, workbenchWindow, workbenchPartSite, viewsite);
	}

	@Override
	protected void createTreeViewer(Composite container) {
		fTreeViewer = new CheckboxTreeViewer(container, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CHECK
				| SWT.BORDER);
		ViewContentProvider contentProvider = new ViewContentProvider(uicontroller);
		fTreeViewer.setContentProvider(contentProvider); // gestionnaire de
		// contenu
		fTreeViewer.setLabelProvider(new ViewLabelProvider(uicontroller)); // gestionnaire
		// d'affichage
		// viewer.setSorter(new NameSorter());

		if (selectedExport == null || selectedExport.size() == 0) {
			final RootNode rootNode = new RootNode(uicontroller);
			uicontroller.add(rootNode);
			fTreeViewer.setInput(rootNode);
		} else {
			boolean selectLast = true;
			Item i = selectedExport.get(0);

			ItemInViewer l = new OldItemInViewer(selectedExport);
			fTreeViewer.setInput(l);
			fTreeViewer.setExpandedState(l, true);
			ItemInViewer[] ii = l.getChildren(0);
			int max = ii.length - (selectLast ? 0 : 1);
			for (int j = 0; j < max; j++) {
				fTreeViewer.setChecked(ii[j], true);
			}
			root.addAll(Arrays.asList(ii));

			checkedItem = fTreeViewer.getCheckedElements();
		}

		fTreeViewer.addCheckStateListener(new ICheckStateListener() {

			public void checkStateChanged(CheckStateChangedEvent event) {
				if (event.getChecked()) {
					fTreeViewer.setSubtreeChecked(event.getElement(), true);
				}
				checkedItem = fTreeViewer.getCheckedElements();
				dialogChanged();
			}
		});
		fTreeViewer.addFilter(new ViewerFilter() {
			@Override
			public boolean select(org.eclipse.jface.viewers.Viewer viewer, Object parentElement, Object element) {
				if (element instanceof ItemInViewer) {
					ItemInViewer iiv = (ItemInViewer) element;
					if (iiv.getKind() == ItemInViewer.LINK_OUTGOING && iiv.getLink() != null
							&& !iiv.getLink().isAggregation()) {
						return false;
					}
				}
				return true;
			};
		});
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 2;
		gd.verticalSpan = 3;
		gd.minimumHeight = 50;
		fTreeViewer.getTree().setLayoutData(gd);
	}

	@Override
	protected void selectAll() {
		for (ItemInViewer ii : root) {
			fTreeViewer.setSubtreeChecked(ii, true);
		}
		checkedItem = fTreeViewer.getCheckedElements();
		dialogChanged();
	}

	@Override
	protected void deselectAll() {
		for (ItemInViewer ii : root) {
			fTreeViewer.setSubtreeChecked(ii, false);
		}
		checkedItem = fTreeViewer.getCheckedElements();
		dialogChanged();
	}
}