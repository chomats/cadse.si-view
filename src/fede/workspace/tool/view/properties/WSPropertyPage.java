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
package fede.workspace.tool.view.properties;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

import fr.imag.adele.cadse.core.Item;

@Deprecated
public class WSPropertyPage extends PropertyPage implements IWorkbenchPropertyPage {

	private static final String PATH_TITLE = "Id:";
	private static final String OWNER_TITLE = "&Type:";
	private Text ownerText;

	/**
	 * Constructor for SamplePropertyPage.
	 */
	public WSPropertyPage() {
		super();
	}

	Item getItem( ) {
		return ((Item)getElement());
	}
	
	private void addFirstSection(Composite parent) {
		Composite composite = parent ; //createDefaultComposite(parent);

		//Label for path field
		Label pathLabel = new Label(composite, SWT.NONE);
		pathLabel.setText(PATH_TITLE);

		// Path text field
		Text pathValueText = new Text(composite, SWT.SIMPLE | SWT.READ_ONLY);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		pathValueText.setLayoutData(gd);
		pathValueText.setText(getItem().getId().toString());
	}

	private void addSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 2;
		separator.setLayoutData(gridData);
	}

	private void addSecondSection(Composite parent) {
		Composite composite = parent ; //createDefaultComposite(parent);

		// Label for owner field
		Label ownerLabel = new Label(composite, SWT.NONE);
		ownerLabel.setText(OWNER_TITLE);

		// Owner text field
		ownerText = new Text(composite, SWT.SIMPLE | SWT.READ_ONLY);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		ownerText.setLayoutData(gd);
		ownerText.setText( getItem().getType().getName());
		// Populate owner text field
		
	}

	/**
	 * @see PreferencePage#createContents(Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);

		addFirstSection(composite);
		addSeparator(composite);
		addSecondSection(composite);
		return composite;
	}

//	private Composite createDefaultComposite(Composite parent) {
//		Composite composite = new Composite(parent, SWT.NULL);
//		GridLayout layout = new GridLayout();
//		layout.numColumns = 2;
//		composite.setLayout(layout);
//
//		GridData data = new GridData();
//		data.verticalAlignment = GridData.FILL;
//		data.horizontalAlignment = GridData.FILL;
//		composite.setLayoutData(data);
//
//		return composite;
//	}

	@Override
	protected void performDefaults() {
	}
	
	@Override
	public boolean performOk() {
		return true;
	}

}