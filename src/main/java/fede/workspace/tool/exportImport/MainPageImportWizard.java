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

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import fede.workspace.tool.view.WSPlugin;
import fr.imag.adele.cadse.core.CadseDomain;
import fr.imag.adele.cadse.core.IItemManager;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.fede.workspace.as.persistence.IPersistence;
import fr.imag.adele.fede.workspace.si.view.View;

class MainPageImportWizard extends MainPageExportImportWizard {

	private Table			fTreeViewer;
	private HashSet<Item>	checkedItem	= new HashSet<Item>();
	CadseDomain				wd;

	protected MainPageImportWizard(CadseDomain wd) {
		super("Import items and resources");
		this.wd = wd;
	}

	@Override
	protected void createTreeViewer(Composite container) {
		fTreeViewer = new Table(container, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CHECK);
		// viewer.setSorter(new NameSorter());
		fTreeViewer.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// if (fTreeViewer.isDisposed())
				// return;
				// if (e.item != null) {
				// TableItem ti = (TableItem) e.item;
				// if (ti.getChecked()) {
				// checkedItem.add((Item) ti.getData());
				// } else {
				// checkedItem.remove((Item) ti.getData());
				// }
				// }
			}

			public void widgetSelected(SelectionEvent e) {
				if (fTreeViewer.isDisposed()) {
					return;
				}
				if (e.item != null) {
					TableItem ti = (TableItem) e.item;
					if (ti.getChecked()) {
						checkedItem.add((Item) ti.getData());
					} else {
						checkedItem.remove(ti.getData());
					}
					dialogChanged();
				}

			};
		});

		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 2;
		gd.verticalSpan = 3;
		gd.minimumHeight = 50;
		fTreeViewer.setLayoutData(gd);
	}

	@Override
	protected void setDestinationValue(String value) {
		super.setDestinationValue(value);
		fTreeViewer.removeAll();
		checkedItem.clear();
		IPersistence p;
		try {
			p = View.getInstance().getPersitence();
			Item[] repo = p.readOrphanRepository(wd.getLogicalWorkspace(), getDestinationFile());
			if (repo != null) {
				Arrays.sort(repo, new Comparator<Item>() {
					public int compare(Item o1, Item o2) {
						return o1.getId().compareTo(o2.getId());
					}
				});
				for (Item itemRepo : repo) {
					TableItem ti = new TableItem(fTreeViewer, SWT.NONE);
					ti.setText(itemRepo.getName());
					ti.setData(itemRepo);
					IItemManager ip = WSPlugin.getManager(itemRepo);
					if (ip != null) {
						ti.setImage(WSPlugin.getDefault().getImageFrom(itemRepo.getType(), itemRepo));
					}
				}
			}
			fTreeViewer.redraw();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public String fieldDialogChanged() {
		if (!getDestinationFile().exists()) {
			return "Select a valid direcory";
		}

		if (checkedItem == null | checkedItem.size() == 0) {
			return "Select one or more items";
		}

		return null;
	}

	public Set<Item> getCheckedItem() {
		return checkedItem;
	}

	@Override
	protected void selectAll() {
		TableItem[] tis = fTreeViewer.getItems();
		for (int i = 0; i < tis.length; i++) {
			tis[i].setChecked(true);
			checkedItem.add((Item) tis[i].getData());
		}
		dialogChanged();
	}

	@Override
	protected void deselectAll() {
		TableItem[] tis = fTreeViewer.getItems();
		for (int i = 0; i < tis.length; i++) {
			tis[i].setChecked(false);
			checkedItem.remove(tis[i].getData());
		}
		dialogChanged();
	}

}