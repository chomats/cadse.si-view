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

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;

abstract class MainPageExportImportWizard extends WizardPage {
    public static final String STORE_DESTINATION_NAMES_ID = "WizardFileSystemResourceExportPage1.STORE_DESTINATION_NAMES_ID";

    private static final int COMBO_HISTORY_LENGTH = 5;
    private CCombo fDestinationField;
    Object[] checkedItem = new Object[0];
   // File directory = null;

    protected MainPageExportImportWizard(String pageName) {
        super(pageName);
    }

    public void createControl(Composite parent) {
        
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.verticalSpacing = 9;
        layout.numColumns = 3;
        
        createFileField(container);
        createTreeViewer(container);
        
        Button b = new Button(container,SWT.PUSH);
        b.setText("Select All");
        b.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                selectAll();
            }
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        b = new Button(container,SWT.PUSH);
        b.setText("Deselect All");
        b.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                deselectAll();
            }
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        
        setControl(container);
        restoreWidgetValues();
        
        dialogChanged();
    }

    protected void deselectAll() {
        // TODO Auto-generated method stub
        
    }

    protected void selectAll() {
        // TODO Auto-generated method stub
        
    }

    abstract protected void createTreeViewer(Composite container);

    private void createFileField(Composite container) {
        GridData gd;
        Label label = new Label(container, SWT.NULL);
        label.setText("&File repository:");
        fDestinationField = new CCombo(container, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 1;
        fDestinationField.setLayoutData(gd);
        fDestinationField.setEditable(false);
        
        Button button = new Button(container, SWT.PUSH);
        button.setText("...");
        button.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(@SuppressWarnings("unused") SelectionEvent e) {
                handleSelectFile();
            }
        });
    }
    
    protected void handleSelectFile() {
        DirectoryDialog dialog = new DirectoryDialog(this.getShell());
        String selection = dialog.open();
        if (selection != null) {
            setDestinationValue(selection);
        }
    }

    void dialogChanged() {
        String error = fieldDialogChanged();
        setErrorMessage(error);
        setPageComplete(error== null);
    }

    
    public String fieldDialogChanged()  {
        if (!getDestinationFile().exists())
            return "Select a valid direcory";
        
        if (checkedItem == null | checkedItem.length == 0)
            return "Select one or more items";
        
        return null;
    }
    
    /**
     *  Hook method for saving widget values for restoration by the next instance
     *  of this class.
     */
    protected void internalSaveWidgetValues() {
        // update directory names history
        IDialogSettings settings = getDialogSettings();
        if (settings != null) {
            String[] directoryNames = settings
                    .getArray(STORE_DESTINATION_NAMES_ID);
            if (directoryNames == null)
                directoryNames = new String[0];

            directoryNames = addToHistory(directoryNames, getDestinationValue());
            settings.put(STORE_DESTINATION_NAMES_ID, directoryNames);

        }
    }
    /**
     * Adds an entry to a history, while taking care of duplicate history items
     * and excessively long histories.  The assumption is made that all histories
     * should be of length <code>WizardDataTransferPage.COMBO_HISTORY_LENGTH</code>.
     *
     * @param history the current history
     * @param newEntry the entry to add to the history
     */
    protected String[] addToHistory(String[] history, String newEntry) {
        java.util.ArrayList<String> l = new java.util.ArrayList<String>(Arrays.asList(history));
        addToHistory(l, newEntry);
        String[] r = new String[l.size()];
        l.toArray(r);
        return r;
    }

    /**
     * Adds an entry to a history, while taking care of duplicate history items
     * and excessively long histories.  The assumption is made that all histories
     * should be of length <code>WizardDataTransferPage.COMBO_HISTORY_LENGTH</code>.
     *
     * @param history the current history
     * @param newEntry the entry to add to the history
     */
    protected void addToHistory(List<String> history, String newEntry) {
        history.remove(newEntry);
        history.add(0, newEntry);

        // since only one new item was added, we can be over the limit
        // by at most one item
        if (history.size() > COMBO_HISTORY_LENGTH)
            history.remove(COMBO_HISTORY_LENGTH);
    }


    /**
     *  Hook method for restoring widget values to the values that they held
     *  last time this wizard was used to completion.
     */
    protected void restoreWidgetValues() {
        IDialogSettings settings = getDialogSettings();
        if (settings != null) {
            String[] directoryNames = settings
                    .getArray(STORE_DESTINATION_NAMES_ID);
            if (directoryNames == null)
                return; // ie.- no settings stored

            // destination
            setDestinationValue(directoryNames[0]);
            for (int i = 0; i < directoryNames.length; i++)
                addDestinationItem(directoryNames[i]);

        }
    }
    
    /**
     *  Add the passed value to self's destination widget's history
     *
     *  @param value java.lang.String
     */
    protected void addDestinationItem(String value) {
        fDestinationField.add(value);
    }

    /**
     *  Answer the contents of self's destination specification widget
     *
     *  @return java.lang.String
     */
    protected String getDestinationValue() {
        return fDestinationField.getText().trim();
    }

    public File getDestinationFile() {
        return new File(getDestinationValue());
    }
    /**
     *  Set the contents of the receivers destination specification widget to
     *  the passed value
     *
     */
    protected void setDestinationValue(String value) {
        fDestinationField.setText(value);
        dialogChanged();
    }
    
}