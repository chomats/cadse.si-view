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
package fede.workspace.tool.view.actions;

import java.util.List;

import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.Link;

@Deprecated
public class DeleteItemDialog extends StatusDialog {

	Item				item;
	private Button		checkDeleteEclipse;
	private Button		checkDeleteContent;
	private ListViewer	viewer;

	boolean				didDeleteEclipse	= false;
	boolean				didDeleteContent	= false;

	public DeleteItemDialog(Shell parent, Item item) {
		super(parent);
		this.item = item;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);

		Composite inner = new Composite(composite, SWT.NONE);
		inner.setFont(composite.getFont());

		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 1;
		inner.setLayout(layout);
		inner.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label l = new Label(inner, SWT.LEFT);
		l.setText("Do you want delete the item " + item.getName() + " ?");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		l.setLayoutData(gd);

		l = new Label(inner, SWT.LEFT);
		l.setText("Theses links will delete with this item.");

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		gd.verticalIndent = 5;
		l.setLayoutData(gd);

		viewer = new ListViewer(inner, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);

		viewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Item) {
					return "Links";
				}
				if (element instanceof Link) {
					Link link = (Link) element;
					return link.getSource().getName() + "--> ( " + link.getLinkType().getName() + " ) "
							+ item.getName();
				}
				return super.getText(element);
			}
		});
		viewer.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {

				if (inputElement instanceof Item) {
					Item item = (Item) inputElement;
					List<? extends Link> lks = item.getIncomingLinks();
					Link[] arrayLks = lks.toArray(new Link[lks.size()]);

					return arrayLks;
				}
				return new Object[0];
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

		});
		viewer.setInput(item);

		viewer.getList().addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				viewer.getList().deselectAll();
			}

			public void widgetSelected(SelectionEvent e) {
				viewer.getList().deselectAll();
			}
		});

		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 1;
		gd.verticalSpan = 25;
		viewer.getList().setLayoutData(gd);
		checkDeleteEclipse = new Button(inner, SWT.CHECK);
		checkDeleteEclipse.setText("Voulez vous supprimer la resource eclipse associee ?");
		checkDeleteContent = new Button(inner, SWT.CHECK);
		checkDeleteContent.setText("Voulez vous supprimer le contenu de la resource eclipse associee ?");
		checkDeleteContent.setEnabled(false);
		checkDeleteEclipse.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				checkDeleteContent.setEnabled(checkDeleteEclipse.getSelection());
				didDeleteEclipse = checkDeleteEclipse.getSelection();

				if (!checkDeleteEclipse.getSelection()) {
					checkDeleteContent.setSelection(false);
				}
			}
		});

		checkDeleteContent.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				didDeleteContent = checkDeleteContent.getSelection();
			}
		});
		return composite;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
	}

	public boolean doDeleteEclipseResource() {
		return didDeleteEclipse;
	}

	public boolean doDeleteContent() {
		return didDeleteContent;
	}

}
