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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;

import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.oper.WSOCreateLink;
import fr.imag.adele.fede.workspace.as.test.TestException;
import fr.imag.adele.fede.workspace.si.view.View;

/**
 * 
 * @author chomats
 * 
 */

public class AddLinkWizard extends Wizard {
	private AddLinkWizardPage	page;
	private Item				item_source;
	private Item				item_dest;

	/**
	 * Constructor for NewItemWorksapceWizard.
	 * 
	 * @param selection
	 */
	public AddLinkWizard(Item item_source, Item item_dest) {
		super();

		if (item_source.isReadOnly()) {
			this.item_dest = item_source;
			this.item_source = item_dest;
		} else {
			this.item_source = item_source;
			this.item_dest = item_dest;
		}
		setNeedsProgressMonitor(true);
	}

	/**
	 * Adding the page to the wizard.
	 */

	@Override
	public void addPages() {
		page = new AddLinkWizardPage(item_source, item_dest);
		addPage(page);
	}

	/**
	 * This method is called when 'Finish' button is pressed in the wizard. We
	 * will create an operation and run it using wizard as execution context.
	 */
	@Override
	public boolean performFinish() {
		final LinkType rt = page.getLinkType();
		final Item theItem_source = page.getItemSource();
		final Item theItem_dest = page.getItemDest();
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(theItem_source, theItem_dest, rt, monitor);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		} catch (Throwable e) {
			MessageDialog.openError(getShell(), "Error", e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * The worker method. It will find the container, create the file if missing
	 * or just replace its contents, and open the editor on the newly created
	 * file.
	 */

	private void doFinish(Item theItem_source, Item theItem_dest, LinkType rt, IProgressMonitor monitor) {

		monitor.beginTask("Creating link " + rt.getName() + " from " + theItem_source.getDisplayName() + " to "
				+ theItem_dest.getDisplayName(), 1);
		WSOCreateLink oper = new WSOCreateLink(theItem_source, rt, theItem_dest);
		oper.execute();
		if (oper.getEx() != null) {
			oper.getEx().printStackTrace();
		}
		try {
			View.getInstance().getTestService().registerIfNeed(oper);
		} catch (TestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// try {
		// theItem_source.createLink(rt,theItem_dest);
		//			
		// } catch (CadseException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		monitor.worked(1);
	}

}