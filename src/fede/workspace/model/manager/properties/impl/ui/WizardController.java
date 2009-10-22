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
package fede.workspace.model.manager.properties.impl.ui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

import fede.workspace.tool.view.actions.delete.ShowDetailWLWCDialogPage;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.transaction.LogicalWorkspaceTransaction;
import fr.imag.adele.cadse.core.ui.IFieldDescription;
import fr.imag.adele.cadse.core.ui.IPage;
import fr.imag.adele.cadse.core.ui.IPageController;
import fr.imag.adele.cadse.core.ui.Pages;
import fr.imag.adele.cadse.core.ui.UIField;
import fr.imag.adele.cadse.core.ui.view.NewContext;

/**
 * This is a sample new wizard. Its role is to create a new file resource in the
 * provided container. If the container resource (a folder or a project) is
 * selected in the workspace when the wizard is opened, it will accept it as the
 * target container. The wizard creates one file with the extension "mpe". If a
 * sample multi-page editor (also available as a template) is registered for the
 * same extension, it will be able to open it.
 */

public class WizardController extends Wizard implements IWorkbenchWizard, IPageController {

	Pages			pages;
	IWizardPage[]	copy_pages;

	public WizardController(Pages pages, Item parentItem, LinkType theLinkType) throws CadseException {
		super();
		setNeedsProgressMonitor(true);
		this.pages = pages;
		this.pages.initLocal(this);
		this.pages.putLocal(IFieldDescription.PARENT_CONTEXT, parentItem);
		this.pages.putLocal(IFieldDescription.INCOMING_LINK_TYPE, theLinkType);
	}
	
	public WizardController(Pages pages, NewContext c) throws CadseException {
		super();
		setNeedsProgressMonitor(true);
		this.pages = pages;
		this.pages.initLocal(this);
		this.pages.putLocal(IFieldDescription.PARENT_CONTEXT, c.getPartParent());
		this.pages.putLocal(IFieldDescription.INCOMING_LINK_TYPE, c.getPartLinkType());
	}

	public WizardController(Pages pages) throws CadseException {
		super();
		setNeedsProgressMonitor(true);
		this.pages = pages;
		this.pages.initLocal(this);
	}

	public LogicalWorkspaceTransaction getCopy() {
		return pages.getCopy();
	}

	@Override
	public void addPages() {
		for (IPage afd : this.pages.getPages()) {
			try {
				addPage(new FieldsWizardPage(afd));
			} catch (CadseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		copy_pages = getPages();
	}

	/**
	 * This method is called when 'Finish' button is pressed in the wizard. We
	 * will create an operation and run it using wizard as execution context.
	 */
	@Override
	public boolean performFinish() {
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				try {
					pages.doFinish(monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} catch (Throwable e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(false, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			if (realException instanceof NullPointerException) {
				MessageDialog.openError(getShell(), "Error", "Null pointeur exception");
				realException.printStackTrace();
				return false;
			}
			String message = realException.getMessage();
			if (message == null || message.length() == 0) {
				realException.printStackTrace();
				message = realException.getClass().getName() + ":" + realException.getStackTrace()[0];
			}
			MessageDialog.openError(getShell(), "Error", message);
			return false;
		}
		return true;
	}

	@Override
	public boolean performCancel() {
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					pages.doCancel(monitor);
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
		}
		return true;
	}

	/**
	 * We will accept the selection in the workbench to see if we can initialize
	 * from it.
	 * 
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		try {
			int currentpage = findIndex(page);
			int nextPage = pages.getNextPageIndex(currentpage);
			if (nextPage == -1) {
				return null;
			}
			return copy_pages[nextPage];
		} catch (Exception realException) {
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return null;
		}
	}

	private int findIndex(IWizardPage page) {
		if (copy_pages == null) {
			copy_pages = super.getPages();
		}
		for (int i = 0; i < copy_pages.length; i++) {
			if (copy_pages[i] == page) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public IWizardPage getPreviousPage(IWizardPage page) {
		try {
			int currentpage = findIndex(page);
			int prevPage = pages.getPrevPageIndex(currentpage);
			if (prevPage == -1) {
				return null;
			}
			return copy_pages[prevPage];
		} catch (Exception realException) {
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return null;
		}
	}

	public Pages getPagesDescription() {
		return pages;
	}

	public boolean backPressed() {
		final int currentPage = findIndex(getContainer().getCurrentPage());
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					pages.doPrevPageAction(monitor, currentPage);
				} catch (Exception e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(false, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}

	public boolean nextPressed() {
		final int currentPage = findIndex(getContainer().getCurrentPage());
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					pages.doNextPageAction(monitor, currentPage);
				} catch (Exception e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(false, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public String getWindowTitle() {
		IWizardPage currentPage = getContainer().getCurrentPage();
		if (currentPage == null) {
			return super.getWindowTitle();
		}
		return ((FieldsWizardPage) currentPage).getTitle();
	}

	public boolean broadcastSubValueAdded(UIField field, Object added) {
		return field.broadcastSubValueAdded(this, added);
	}

	public boolean broadcastSubValueRemoved(UIField field, Object removed) {
		return field.broadcastSubValueRemoved(this, removed);
	}

	public void broadcastThisFieldHasChanged(UIField fd) {
		fd.updateValue();
	}

	public boolean broadcastValueChanged(UIField field, Object value) {
		return field.broadcastValueChanged(this, value);
	}

	public boolean broadcastValueDeleted(UIField field, Object oldvalue) {
		return field.broadcastValueDeleted(this, oldvalue);
	}

	public String getMessage() {
		IWizardPage currentPage = getContainer().getCurrentPage();
		if (currentPage == null) {
			return null;
		}
		return currentPage.getMessage();
	}

	public int getMessageType() {
		IWizardPage currentPage = getContainer().getCurrentPage();
		if (currentPage == null) {
			return -1;
		}
		return ((WizardPage) currentPage).getMessageType();
	}

	public void setMessage(String newMessage, int newType) {
		IWizardPage currentPage = getContainer().getCurrentPage();
		if (currentPage == null) {
			return;
		}
		((WizardPage) currentPage).setMessage(newMessage, newType);

	}

	public boolean hasShowDetail() {
		return true;
	}

	public void showDetailDialog() {
		if (getCopy() != null) {
			try {
				ShowDetailWLWCDialogPage.openDialog(getCopy(), "Operations detail", null, false);
			} catch (CadseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}