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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
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
import org.eclipse.swt.widgets.Tree;

import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.Link;

public class ShadowDeleteDialog extends StatusDialog {

	Item						item;
	boolean						shadow				= false;
	private Button				checkDeleteEclipse;
	private Button				checkDeleteContent;
	private CheckboxTreeViewer	viewer;

	boolean						didDeleteEclipse	= false;
	boolean						didDeleteContent	= false;
	List<Link>					linkFromSelected	= new ArrayList<Link>();

	public ShadowDeleteDialog(Shell parent, Item item, boolean shadow) {
		super(parent);
		this.item = item;
		this.shadow = shadow;
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
		l.setText("Do you want " + (shadow ? "shadow" : "delete") + " the item " + item.getId() + " ?");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		l.setLayoutData(gd);

		l = new Label(inner, SWT.LEFT);
		l.setText("Select the link to delete with this item.");

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		gd.verticalIndent = 5;
		l.setLayoutData(gd);

		Tree tree = new Tree(inner, SWT.CHECK | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		viewer = new CheckboxTreeViewer(tree);
		// List<Link> lks = item.getIncomingLinks();
		// Link[] toArray = lks.toArray(new Link[lks.size()]);
		// Link[] arrayLks = toArray;

		viewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Item) {
					return "Links";
				}
				if (element instanceof Link) {
					Link l = (Link) element;
					return l.getSource().getId() + "--> ( " + l.getLinkType().getName() + " ) "
							+ item.getId();
				}
				return super.getText(element);
			}
		});
		viewer.setContentProvider(new ITreeContentProvider() {

			public Object[] getChildren(Object parentElement) {
				return getElements(parentElement);
			}

			public Object getParent(Object element) {
				if (element instanceof Link) {
					return ((Link) element).getResolvedDestination();
				}
				return null;
			}

			public boolean hasChildren(Object element) {
				if (element instanceof ShadowDeleteDialog) {
					return true;
				}
				if (element instanceof Item) {
					Item item = (Item) element;
					List<? extends Link> lks = item.getIncomingLinks();
					return lks.size() > 0;
				}
				return false;
			}

			public Object[] getElements(Object inputElement) {
				if (inputElement instanceof ShadowDeleteDialog) {
					return new Object[] { item };
				}
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
		viewer.setInput(this);

		viewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(org.eclipse.jface.viewers.CheckStateChangedEvent event) {
				if (event.getElement() instanceof Item) {
					Item item = (Item) event.getElement();
					linkFromSelected.clear();
					List<? extends Link> lks = item.getIncomingLinks();
					for (Iterator iter = lks.iterator(); iter.hasNext();) {
						Link l = (Link) iter.next();
						viewer.setChecked(l, event.getChecked());
						if (event.getChecked()) {
							linkFromSelected.add(l);
						}
					}
				} else if (event.getElement() instanceof Link) {
					if (event.getChecked()) {
						linkFromSelected.add((Link) event.getElement());
					} else {
						linkFromSelected.remove(event.getElement());
					}
				}
			};
		});
		// viewer.setCheckedElements(arrayLks) ;
		// viewer.setChecked(item,true);
		// linkFromSelected.addAll(Arrays.asList(arrayLks));
		viewer.setExpandedState(item, true);

		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 1;
		gd.verticalSpan = 25;
		tree.setLayoutData(gd);
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

		checkDeleteEclipse.addSelectionListener(new SelectionListener() {
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

	public Link[] doDeleteLinks() {
		return this.linkFromSelected.toArray(new Link[this.linkFromSelected.size()]);
	}

}
