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
package fede.workspace.tool.view.addlink;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkType;
import fede.workspace.tool.view.WSPlugin;

/**
 * 
 * @author chomats
 *
 */

public class AddLinkWizardPage extends WizardPage {
	private CCombo relationCombo;

	private Label from;
	private Label to;

	private LinkType[] relations = null;

	private Item item_source;
	private Item item_dest;

    private Button swapBt;
	
	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public AddLinkWizardPage(Item item_source, Item item_dest) {
		super("wizardPage");
		
		this.item_source = item_source;
		this.item_dest = item_dest;
		
	}
	
	void swap() {
		Item i = this.item_dest;
		this.item_dest = this.item_source;
		this.item_source = i;
		initialize();
	}
	
	void initialize() {
		
		List<LinkType> selectRT = selectLinkType(item_source, item_dest);
		if (selectRT.size() == 0) {
			Item tmp = item_source;
			item_source = item_dest;
			item_dest = tmp;
			
			selectRT = selectLinkType(item_source, item_dest);
		}
		setTitle("Create a link from "+ item_source.getDisplayName() + " to "+item_dest.getDisplayName());
		
		setDescription("This wizard creates a new link form "+item_source.getDisplayName()+" to"+item_dest.getDisplayName());
		
		this.relations = (LinkType[]) selectRT.toArray(new LinkType[selectRT.size()]);
		String[] items = new String[relations.length];
		for (int i = 0; i < items.length; i++) {
			items[i] = relations[i].getName() + " - "+relations[i].getDestination().getId();
		}
		relationCombo.setItems(items);
		if (items.length >0) {
            relationCombo.select(0);
            dialogChanged();
        }
      
		from.setText(item_source.getDisplayName());
		to.setText(item_dest.getDisplayName());
		
	}
	
	private static List<LinkType> selectLinkType(Item source, Item dest) {
		List<LinkType> l = source.getType().getOutgoingLinkTypes();
		List<LinkType> selectRT = new ArrayList<LinkType>();
		for (LinkType rt : l) {
	        if (rt.isDerived())
                continue;
			if (rt.getDestination().equals(dest.getType()) ||
					rt.getDestination().isSuperTypeOf(dest.getType()))
				selectRT.add(rt);
		}
		return selectRT;
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 9;
		 // from
		Label label = new Label(container, SWT.NULL);
		label.setText("&From:");
		from = new Label(container, SWT.NULL);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		from.setLayoutData(gd);
//		 to
		label = new Label(container, SWT.NULL);
		label.setText("&To:");
		to = new Label(container, SWT.NULL);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		to.setLayoutData(gd);
		
		swapBt = new Button(container, SWT.PUSH);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		gd.horizontalSpan = 2;
		swapBt.setLayoutData(gd);
		swapBt.setText("Swap");
		swapBt.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				swap();
			};
			public void widgetDefaultSelected(SelectionEvent e) {};
		});
		
		label = new Label(container, SWT.NULL);
		label.setText("&Relation:");

		relationCombo = new CCombo(container, SWT.BORDER | SWT.SINGLE);
		 gd = new GridData(GridData.FILL_HORIZONTAL);
		relationCombo.setLayoutData(gd);
		relationCombo.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				dialogChanged();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				dialogChanged();
			}
		});
		relationCombo.setEditable(false);


		
		initialize();
		dialogChanged();
		setControl(container);
	}

	

	/**
	 * Ensures that both text fields are set.
	 */

	private void dialogChanged() {
        if (this.item_dest.isReadOnly())
            swapBt.setEnabled(false);

		LinkType rt = getLinkType();
		if (rt == null) {
			updateStatus("Relation must be specified");
			return;
		}
		if (rt.isPart()) {
			updateStatus("Relation cannot be a part relation.");
			return;
		}
		Link l = item_source.getOutgoingLink(rt,item_dest.getId());
		if (l != null) {
			updateStatus(
					MessageFormat.format("Cannot create a second link of type {0} to {1}.",
							rt.getName(),item_dest.getName()));
			return;
		}
		if (rt.getMax() != -1) {
			List<Link> ltLink = item_source.getOutgoingLinks(rt);
			if (ltLink.size() >= rt.getMax()) {
				updateStatus(
						MessageFormat.format("Cannot create a link of type {0} : the max link is {1}.",
								rt.getName(),rt.getMax()));
				return;
			}
		}
		String error = WSPlugin.getManager(item_source).canCreateLink(item_source, item_dest, rt);
		
		updateStatus(error);
	}

	

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public LinkType getLinkType() {
		int i = this.relationCombo.getSelectionIndex();
		if (i == -1)
			return null;
		return this.relations[i];
	}

	public Item getItemSource() {
		return this.item_source;
	}

	public Item getItemDest() {
		return this.item_dest;
	}
}