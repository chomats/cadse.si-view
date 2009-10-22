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
package fede.workspace.tool.view.menu;

import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWizard;

import fede.workspace.model.manager.properties.impl.ui.UIWizardDialog;
import fede.workspace.model.manager.properties.impl.ui.WizardController;
import fede.workspace.tool.view.WSPlugin;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.DefaultItemManager;
import fr.imag.adele.cadse.core.IItemManager;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.IMenuAction;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.ui.Pages;

/**
 * A <code>BaseNewWizardMenu</code> is used to populate a menu manager with
 * New Wizard actions for the current perspective's new wizard shortcuts,
 * including an Other... action to open the new wizard dialog.
 * 
 * @since 3.1
 */
public class MenuNewAction extends IMenuAction {

	private IWorkbenchWindow	workbenchWindow;
	private final Item			parent;
	private final ItemType		destItemType;
	private LinkType			lt;
	private String				label;

	/**
	 * Creates a new wizard shortcut menu for the IDE.
	 * 
	 * @param window
	 *            the window containing the menu
	 * @param parent
	 *            the item parent for the new item or the item from the new item
	 *            is created..
	 * @param string
	 *            label
	 */
	public MenuNewAction(IWorkbenchWindow window, Item parent, LinkType lt, ItemType destination, String label) {
		Assert.isNotNull(window);

		this.workbenchWindow = window;
		this.parent = parent;
		this.destItemType = destination;
		this.lt = lt;
		if (label == null) {
			label = this.destItemType.getDisplayName();
		}
		this.label = label;
	}

	/**
	 * Returns the window in which this menu appears.
	 * 
	 * @return the window in which this menu appears
	 */
	protected IWorkbenchWindow getWindow() {
		return workbenchWindow;
	}

	public int compareTo(MenuNewAction arg0) {
		return this.destItemType.getDisplayName().compareTo(arg0.destItemType.getDisplayName());
	}

	@Override
	public URL getImage() {
		return WSPlugin.getDefault().getImageURLFrom(this.destItemType, null);
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String getMenuPath() {
		return NEW_MENU;
	}

	@Override
	public boolean isSeparator() {
		return false;
	}

	public IWorkbenchWizard createWizard(IItemManager ip, Item theItemParent, LinkType theLinkType, ItemType desType)
			throws CadseException {
		if (ip.isAbstract(theItemParent, theLinkType)) {
			return null;
		}
		Pages f = null;
		if (ip instanceof DefaultItemManager) {
			f = ((DefaultItemManager) ip).createCreationPages(theItemParent, theLinkType, desType);
		}
		if (f == null) {
			f = desType.getGoodCreationPage(theItemParent, desType, theLinkType);
		}
		if (f != null) {
			return new WizardController(f, theItemParent, theLinkType);
		}
		return null;
	}

	@Override
	public void run(IItemNode[] selection) throws CadseException {
		try {
			final IItemManager ip = destItemType.getItemManager();
			if (ip == null) {
				return;
			}
			IWorkbenchWizard wizard = createWizard(ip, this.parent, lt, destItemType);
			if (wizard == null) {
				String message;
				if (parent != null) {
					message = MessageFormat.format(
							"Cannot create an item of type {0} from {1} of type {2} the link {3} : no pages found",
							destItemType.getName(), parent.getName(), parent.getType().getName(), lt.getName());
				} else {
					message = MessageFormat.format("Cannot create an item of type {0} : no pages found", destItemType
							.getName());
				}

				MessageDialog.openError(workbenchWindow.getShell(), "Cannot create wizard  : no pages found", message);
				WSPlugin.log(new Status(Status.ERROR, WSPlugin.PLUGIN_ID, 0, message, null));
				return;
			}
			UIWizardDialog wd = new UIWizardDialog(workbenchWindow.getShell(), (WizardController) wizard);
			wd.open();
		} catch (Throwable e1) {
			e1.printStackTrace();
			String message;
			if (parent != null) {
				message = MessageFormat.format("Cannot create an item of type {0} from {1} of type {2} the link {3}",
						destItemType.getName(), parent.getName(), parent.getType().getName(), lt.getName());
			} else {
				message = MessageFormat.format("Cannot create an item of type {0}", destItemType.getName());
			}

			MessageDialog.openError(workbenchWindow.getShell(), "Cannot create wizard", message);
			WSPlugin.log(new Status(Status.ERROR, WSPlugin.PLUGIN_ID, 0, message, e1));
		}
	}

	/**
	 * If parent exists, return the type of the link we want to create from
	 * parent to new item
	 * 
	 * @return a link type or null
	 */
	public LinkType getLinkType() {
		return lt;
	}

	/**
	 * Return the Item from we want to create the new item or null if no parent
	 * 
	 * @return an item or null
	 */
	public Item getParent() {
		return parent;
	}

	/**
	 * Return the item type of the item we want to create
	 * 
	 * @return an item type
	 */
	public ItemType getDestItemType() {
		return destItemType;
	}

}
