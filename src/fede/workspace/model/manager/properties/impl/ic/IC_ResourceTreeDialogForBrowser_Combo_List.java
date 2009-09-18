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
package fede.workspace.model.manager.properties.impl.ic;

import java.util.ArrayList;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import fede.workspace.eclipse.MelusineProjectManager;
import fede.workspace.model.manager.properties.IInteractionControllerForBrowserOrCombo;
import fede.workspace.model.manager.properties.IInteractionControllerForList;
import fr.imag.adele.cadse.core.CadseRootCST;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.impl.CadseIllegalArgumentException;

/**
 * ic IInteractiveListController DefaultResourceListInteractiveController {
 * string select-message; string select-title ; int select-type-root : 0 ->
 * workspace root , 1+ -> relatif current item object :
 * org.eclipse.core.resources.IResource }
 */

public class IC_ResourceTreeDialogForBrowser_Combo_List extends IC_AbstractTreeDialogForList_Browser_Combo implements
		IInteractionControllerForList, ISelectionStatusValidator, IInteractionControllerForBrowserOrCombo {

	protected static final String	REFRESH_LABEL	= "Re&fresh";
	protected static final int		REFRESH_ID		= 1025;
	private int						selectRoot;

	public IC_ResourceTreeDialogForBrowser_Combo_List(String title, String message, int selectRoot) {
		super(title, message);
		this.selectRoot = selectRoot;
	}

	@Override
	public Object[] transAndAddObject(Object[] object) {
		return transforme(object);
	}

	protected Object[] transforme(Object[] ret) {
		return ret;
	}

	@Override
	protected ElementTreeSelectionDialog newTreeDialog(Shell parentShell) {
		ElementTreeSelectionDialog lsd = new ElementTreeSelectionDialog(parentShell, getLabelProvider(),
				getTreeContentProvider()) {

			@Override
			protected void createButtonsForButtonBar(Composite parent) {
				super.createButtonsForButtonBar(parent);
				createButton(parent, REFRESH_ID, REFRESH_LABEL, false);
			}

			@Override
			protected void buttonPressed(int buttonId) {
				// TODO Auto-generated method stub
				super.buttonPressed(buttonId);
				if (buttonId == REFRESH_ID) {
					ISelection sel = getTreeViewer().getSelection();
					if (sel != null && sel instanceof IStructuredSelection) {
						IStructuredSelection ssel = (IStructuredSelection) sel;
						if (ssel.isEmpty()) {
							try {
								ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
							} catch (CoreException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						for (Object v : ssel.toArray()) {
							if (v instanceof IResource) {
								try {
									((IResource) v).refreshLocal(IResource.DEPTH_INFINITE, null);
								} catch (CoreException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		};
		return lsd;
	}

	public ILabelProvider getLabelProvider() {
		return new WorkbenchLabelProvider();
	}

	public IStatus validate(Object[] selection) {
		return Status.OK_STATUS;
	}

	@Override
	protected Object getInputValues() {
		return getRootSelect();
	}

	protected IResource getRootSelect() {
		if (selectRoot >= 1) {
			Item theItem = getItem();
			IResource r = MelusineProjectManager.getResource(theItem);
			if (r == null) {
				throw new CadseIllegalArgumentException("Cannot find the resource form the item {0}.", theItem.getId());
			}
			while (selectRoot-- > 1) {
				r = r.getParent();
			}
			return r;
		}
		if (selectRoot == 0) {
			return ResourcesPlugin.getWorkspace().getRoot();
		}
		return null;

	}

	@Override
	protected ViewerFilter getFilter() {
		return null;
	}

	public String toString(Object value) {
		if (value == null) {
			return "<none>";
		}
		if (value instanceof IResource) {
			IResource rvalue = (IResource) value;
			return rvalue.getFullPath().toPortableString();
		}
		return value.toString();
	}

	@Override
	protected ITreeContentProvider getTreeContentProvider() {
		return new WorkbenchContentProvider();
	}

	public Object[] getValues() {
		IResource r = getRootSelect();
		if (r == null) {
			return new Object[0];
		}
		final ArrayList<IResource> allr = new ArrayList<IResource>();
		// allr.add(r);

		try {
			r.accept(new IResourceVisitor() {
				public boolean visit(IResource resource) throws CoreException {
					allr.add(resource);
					return true;
				}
			});
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return allr.toArray();
	}

	public ItemType getType() {
		return CadseRootCST.IC_RESOURCE_TREE_DIALOG_FOR_BROWSER_COMBO_LIST;
	}

	public void edit(Shell shell, ITreeSelection sel) {
	}
}