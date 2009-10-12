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

package fr.imag.adele.cadse.impl.exportimport;

import java.net.URL;

import org.eclipse.jface.wizard.WizardDialog;

import fede.workspace.model.manager.properties.impl.ui.WizardController;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.IMenuAction;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ui.Pages;
import fr.imag.adele.cadse.ui.field.core.FieldsCore;

/**
 * The Class ExportCadseAction.
 * 
 * @author <a href="mailto:stephane.chomat@imag.fr">Stephane Chomat</a>
 */
public class ExportCadseAction extends IMenuAction {

	/** The Constant EXPORT_BINARY_CADSE. */
	public static final String	EXPORT_BINARY_CADSE	= "Export cadse";

	/** The cadsedef. */
	private Item[]				cadsedef;

	/**
	 * Instantiates a new export cadse action.
	 * 
	 * @param cadsedef
	 *            the cadsedef
	 */
	public ExportCadseAction(Item... cadsedef) {
		this.cadsedef = cadsedef;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.IMenuAction#getId()
	 */
	@Override
	public String getId() {
		return EXPORT_BINARY_CADSE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.IMenuAction#getImage()
	 */
	@Override
	public URL getImage() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.IMenuAction#getLabel()
	 */
	@Override
	public String getLabel() {
		return EXPORT_BINARY_CADSE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.core.IMenuAction#run(fr.imag.adele.cadse.core.IItemNode[])
	 */
	@Override
	public void run(IItemNode[] selection) throws CadseException {
		try {

			ExportCadsePagesAction myaction = new ExportCadsePagesAction();
			myaction.setCadsedef(cadsedef);
			myaction.setSelectJar(null);
			Pages f = FieldsCore.createWizard(myaction, FieldsCore.createPage("page1", "Export cadse", "Export cadse",
					4, myaction.createNameField(), myaction.createImportField(), myaction.createTimeStampField()));

			WizardController wc = new WizardController(f);
			WizardDialog wd = new WizardDialog(null, wc);
			wd.setPageSize(300, 200);
			wd.open();

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
