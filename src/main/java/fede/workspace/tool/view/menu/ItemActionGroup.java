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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.internal.ui.preferences.OverlayPreferenceStore.TypeDescriptor;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionGroup;

import fede.workspace.tool.view.WSPlugin;
import fede.workspace.tool.view.actions.CreateLinkAction;
import fede.workspace.tool.view.actions.DeleteLinkAction;
import fede.workspace.tool.view.actions.GenerateAction;
import fede.workspace.tool.view.actions.RefreshAction;
import fede.workspace.tool.view.actions.test.CancelTestAction;
import fede.workspace.tool.view.actions.test.CheckAttributeInModel;
import fede.workspace.tool.view.actions.test.CheckContentInModel;
import fede.workspace.tool.view.actions.test.CheckItemInModel;
import fede.workspace.tool.view.actions.test.CheckItemInviewer;
import fede.workspace.tool.view.actions.test.StopTestAction;
import fede.workspace.tool.view.addlink.LinkRootNode;
import fede.workspace.tool.view.node.AbstractCadseViewNode;
import fede.workspace.tool.view.node.RootNode;
import fr.imag.adele.cadse.core.ExtendedType;
import fr.imag.adele.cadse.core.IGenerateContent;
import fr.imag.adele.cadse.core.IItemManager;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.IMenuAction;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.LogicalWorkspace;
import fr.imag.adele.cadse.core.Menu;
import fr.imag.adele.cadse.core.MenuGroup;
import fr.imag.adele.cadse.core.TypeDefinition;
import fr.imag.adele.cadse.core.impl.CadseCore;
import fr.imag.adele.cadse.core.impl.internal.Accessor;
import fr.imag.adele.cadse.core.ui.IActionContributor;
import fr.imag.adele.cadse.core.ui.view.ViewDescription;
import fr.imag.adele.cadse.eclipse.view.AbstractCadseTreeViewUI;
import fr.imag.adele.fede.workspace.si.view.View;

public class ItemActionGroup extends ActionGroup {
	private final IShellProvider			shellprovider;
	private final AbstractCadseTreeViewUI	viewUIController;
	private final IWorkbenchWindow			workbenchWindow;

	public ItemActionGroup(AbstractCadseTreeViewUI viewUI) {
		shellprovider = viewUI.getShellProvider();
		workbenchWindow = viewUI.getWorkbenchWindow();
		viewUIController = viewUI;
	}

	@Override
	public void fillContextMenu(IMenuManager manager) {

		ActionContext cxt = getContext();

		ISelection sel = cxt.getSelection();
		if (sel instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) sel;
			fillContextMenu(manager, workbenchWindow, ssel);
		}

	}

	protected void fillContextMenu(IMenuManager manager, IWorkbenchWindow workbenchWindow, IStructuredSelection ssel) {
		View viewComponent = View.getInstance();
		if (viewComponent == null) {
			return;
		}

		Menu principalMenu = new Menu();

		// create les different context.
		principalMenu.insert(null, new MenuGroup(IMenuAction.CONTEXT_1_MENU), true);
		principalMenu.insert(null, new MenuGroup(IMenuAction.CONTEXT_2_MENU), true);
		principalMenu.insert(null, new MenuGroup(IMenuAction.CONTEXT_3_MENU), true);
		principalMenu.insert(null, new MenuGroup(IMenuAction.CONTEXT_4_MENU), true);
		principalMenu.insert(null, new MenuGroup(IMenuAction.CONTEXT_5_MENU), true);
		principalMenu.insert(null, new MenuGroup(IMenuAction.CONTEXT_6_MENU), true);

		List<Object> objects = Arrays.asList(ssel.toArray());
		IItemNode[] selection = objects.toArray(new IItemNode[objects.size()]);
		HashSet<ItemType> types = new HashSet<ItemType>();
		for (IItemNode in : selection) {
			Item i = in.getItem();
			if (i != null) {
				types.add(i.getType());
			}
			Link l = in.getLink();
			if (l != null) {
				types.add(l.getSource().getType());
				types.add(l.getDestination().getType());
			}
		}

		Set<IActionContributor> visited = new HashSet<IActionContributor>();
		for (ItemType it : types) {
			runContributor(viewUIController, visited, principalMenu, selection, it);
		}
		if (types.size() == 0) {
			// seul les contributor des sur le type "ItemType" de cadseRoot
			// sont appelé.
			if (CadseCore.theItem != null)
				runContributor(viewUIController, visited, principalMenu, selection, CadseCore.theItem);
		}

		manager.add(new MenuActionContributionItem(workbenchWindow, selection, principalMenu.getChildren()));
	}

	private void runContributor(ViewDescription viewDescription, Set<IActionContributor> visited, Menu principalMenu,
			IItemNode[] selection, ItemType it) {
		Set<IActionContributor> c = it.getAllActionContribution();
		for (IActionContributor action : c) {
			if (visited.contains(action)) {
				continue;
			}
			visited.add(action);
			try {
				action.contributeMenuAction(viewDescription, principalMenu, selection);
			} catch (Throwable e) {
				it.getCadseDomain().log("CallContributor", "call an contributor " + action.getClass(), e);
			}
		}
	}
}
