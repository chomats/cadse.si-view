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
import fede.workspace.tool.view.actions.AddCadseModelAction;
import fede.workspace.tool.view.actions.CreateLinkAction;
import fede.workspace.tool.view.actions.DeleteItemAction;
import fede.workspace.tool.view.actions.DeleteLinkAction;
import fede.workspace.tool.view.actions.GenerateAction;
import fede.workspace.tool.view.actions.RefreshAction;
import fede.workspace.tool.view.actions.RenameAction;
import fede.workspace.tool.view.actions.test.CancelTestAction;
import fede.workspace.tool.view.actions.test.CheckAttributeInModel;
import fede.workspace.tool.view.actions.test.CheckContentInModel;
import fede.workspace.tool.view.actions.test.CheckItemInModel;
import fede.workspace.tool.view.actions.test.CheckItemInviewer;
import fede.workspace.tool.view.actions.test.RunTestAction;
import fede.workspace.tool.view.actions.test.StartTestAction;
import fede.workspace.tool.view.actions.test.StopTestAction;
import fede.workspace.tool.view.addlink.LinkRootNode;
import fede.workspace.tool.view.node.AbstractCadseViewNode;
import fr.imag.adele.cadse.core.Group;
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
			IWorkbenchWindow workbenchWindow = getWorkbenchWindow();
			fillContextMenu(manager, workbenchWindow, ssel);
		}

	}

	public IContributionItem findUsingPath(IContributionManager manager, String path) {
		String group;
		String rest = null;
		IContributionItem findMenuManager = null;
		int separator = path.indexOf('/');
		if (separator != -1) {
			group = path.substring(0, separator);
			rest = path.substring(separator + 1);
		} else {
			group = path;
		}
		findMenuManager = manager.find(group);
		if (findMenuManager == null) {
			findMenuManager = new Separator(group);
			manager.add(findMenuManager);
		}
		if (rest != null) {
			path = rest;
			separator = path.indexOf('/');
			String id;
			if (separator != -1) {
				id = path.substring(0, separator);
				rest = path.substring(separator + 1);
			} else {
				id = path;
			}
			findMenuManager = manager.find(id);
			if (findMenuManager == null) {
				findMenuManager = new MenuManager(id, id);
				manager.appendToGroup(group, findMenuManager);
			}
		}

		if (findMenuManager instanceof IMenuManager && rest != null) {
			manager = (IMenuManager) findMenuManager;
			return findUsingPath(manager, rest);
		}
		return findMenuManager;
	}

	protected IWorkbenchWindow getWorkbenchWindow() {
		return workbenchWindow;
	}

	protected void fillContextMenu(IMenuManager manager, IWorkbenchWindow workbenchWindow, IStructuredSelection ssel) {
		View viewComponent = View.getInstance();
		if (viewComponent == null) {
			return;
		}

		Item cxtItem;

		Menu principalMenu = new Menu();

		// create les different context.
		principalMenu.insert(null, new Group(IMenuAction.CONTEXT_1_MENU), true);
		principalMenu.insert(null, new Group(IMenuAction.CONTEXT_2_MENU), true);
		principalMenu.insert(null, new Group(IMenuAction.CONTEXT_3_MENU), true);
		principalMenu.insert(null, new Group(IMenuAction.CONTEXT_4_MENU), true);
		principalMenu.insert(null, new Group(IMenuAction.CONTEXT_5_MENU), true);
		principalMenu.insert(null, new Group(IMenuAction.CONTEXT_6_MENU), true);

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

		ArrayList<Item> itemsToRecreatePartLink = new ArrayList<Item>();
		for (IItemNode in : selection) {
			Item i = in.getItem();
			if (i == null) {
				continue;
			}
			Item p = i.getPartParent();
			if (p == null) {
				continue;
			}
			Link l = Accessor.getPartParentLink(i);
			if (l != null) {
				continue;
			}
			itemsToRecreatePartLink.add(i);
		}
		if (itemsToRecreatePartLink.size() != 0) {
			principalMenu.insert(IMenuAction.CONTEXT_5_MENU, new RecreatePartLinkAction(itemsToRecreatePartLink,
					getShellProvider()), true);
		}

		if (ssel.size() == 1 && ssel.getFirstElement() instanceof IItemNode) {
			IItemNode iiv = (IItemNode) ssel.getFirstElement();
			cxtItem = iiv.getItem();
			if (cxtItem != null) {
				Menu newmenu = computeNewMenu(workbenchWindow, cxtItem);
				if (newmenu != null) {
					principalMenu.insert(IMenuAction.CONTEXT_1_MENU, newmenu, true);
				}
			}
		} else if (ssel.size() == 0) {
			Menu newmenu = computeNewMenu(workbenchWindow, null);
			if (newmenu != null) {
				principalMenu.insert(IMenuAction.CONTEXT_1_MENU, newmenu, true);
			}
		}

		Set<IItemNode> itemsToDelete = getItemsToDelete(ssel);
		if (itemsToDelete.size() != 0) {
			principalMenu.insert(IMenuAction.CONTEXT_1_MENU, new DeleteItemAction(itemsToDelete, getShellProvider(),
					viewUIController.getViewPart()), true);
		}

		if (Platform.inDevelopmentMode()) {
			Set<IItemNode> itemsToRename = getItemsToRename(ssel);
			if (itemsToRename.size() != 0) {
				principalMenu.insert(IMenuAction.CONTEXT_1_MENU, new RenameAction(itemsToRename, getShellProvider()),
						true);
			}
		}

		Set<IItemNode> linksToDelete = getLinksToDelete(ssel);
		if (linksToDelete.size() != 0) {
			principalMenu.insert(IMenuAction.CONTEXT_2_MENU, new DeleteLinkAction(linksToDelete, getShellProvider()),
					true);
		}

		Set<IItemNode> itemsToRefesh = getItemsToRefresh(ssel);
		if (itemsToRefesh.size() != 0) {
			principalMenu.insert(IMenuAction.CONTEXT_2_MENU, new RefreshAction(itemsToRefesh, getCadseTreeViewerUI()),
					true);
		}

		List<Item> generateContent = getGenerateObject(ssel);
		if (generateContent.size() != 0) {
			principalMenu.insert(IMenuAction.CONTEXT_2_MENU, new GenerateAction(generateContent), true);
		}

		if (ssel.size() == 1) {
			AbstractCadseViewNode node = (AbstractCadseViewNode) ssel.getFirstElement();
			Item item = node.getItem();

			if (item != null
					&& item.isResolved()
					&& LinkRootNode.getLinkTypeNodeAndItemTypeNode(new LinkRootNode(), item.getType(), item,
							viewUIController).length != 0) {
				principalMenu.insert(IMenuAction.CONTEXT_2_MENU, new CreateLinkAction(item, getShellProvider(),
						viewUIController), true);
			}
		}
		Set<IActionContributor> visited = new HashSet<IActionContributor>();
		for (ItemType it : types) {
			it.getItemManager().contributeMenuAction(principalMenu, selection);
			runContributor(viewUIController, visited, principalMenu, selection, it);
		}
		if (types.size() == 0) {
			// seul les contributor des sur le type "ItemType" de cadseRoot
			// sont appelé.
			runContributor(viewUIController, visited, principalMenu, selection, CadseCore.theItem);
		}

		principalMenu.insert(IMenuAction.CONTEXT_2_MENU, new AddCadseModelAction(), true);

		if (viewComponent.getTestService() != null && viewComponent.getTestService().isEnableTests()) {
			Menu testMenu = new Menu("test", "Test", null);
			if (viewComponent.getTestService().isRecordedTests()) {
				testMenu.insert(null, new StopTestAction(), true);
				testMenu.insert(null, new CancelTestAction(getShellProvider()), true);
				if (selection.length == 1) {
					IItemNode node = selection[0];
					Menu testCheckMenu = new Menu("check", "check", null);
					testCheckMenu.insert(null, new CheckItemInviewer(node, this.viewUIController.getViewPart()), true);
					Item item = node.getItem();
					if (item != null) {
						testCheckMenu.insert(null, new CheckItemInModel(item, viewComponent), true);
						testCheckMenu.insert(null, new CheckContentInModel(item, viewComponent), true);

						String[] keys = item.getType().getAttributeTypeIds();
						ArrayList<String> allKeys = new ArrayList<String>(Arrays.asList(item.getAttributeKeys()));
						allKeys.addAll(Arrays.asList(keys));
						keys = allKeys.toArray(new String[allKeys.size()]);
						if (keys.length != 0) {
							Arrays.sort(keys);
							testCheckMenu.insert(null, IMenuAction.SEPARATOR, true);
							for (String k : keys) {
								testCheckMenu.insert(null, new CheckAttributeInModel(item, k, viewComponent), true);
							}
						}
					}
					testMenu.insert(null, testCheckMenu, true);
				}
			} else {
				testMenu.insert(null, new StartTestAction(getShellProvider()), true);
			}
			testMenu.insert(null, new RunTestAction(getShellProvider()), true);
			principalMenu.insert(IMenuAction.CONTEXT_2_MENU, testMenu, true);
		}

		manager.add(new MenuActionContributionItem(workbenchWindow, selection, principalMenu.getChildren()));
	}

	private void runContributor(ViewDescription viewDescription, Set<IActionContributor> visited, Menu principalMenu,
			IItemNode[] selection, ItemType it) {
		IActionContributor[] c = it.getAllActionContribution();
		for (int i = 0; i < c.length; i++) {
			if (visited.contains(c[i])) {
				continue;
			}
			visited.add(c[i]);
			try {
				c[i].contributeMenuAction(viewDescription, principalMenu, selection);
			} catch (Throwable e) {
				it.getCadseDomain().log("CallContributor", "call an contributor " + c[i].getClass(), e);
			}
		}
	}

	protected AbstractCadseTreeViewUI getCadseTreeViewerUI() {
		return viewUIController;
	}

	protected IShellProvider getShellProvider() {
		return shellprovider;
	}

	protected Set<IItemNode> getLinksToDelete(IStructuredSelection ssel) {
		HashSet<IItemNode> links = new HashSet<IItemNode>();
		Object[] selObjects = ssel.toArray();
		for (int i = 0; i < selObjects.length; i++) {
			Object obj = selObjects[i];
			if (obj instanceof IItemNode) {
				IItemNode iv = ((IItemNode) obj);
				Link l = iv.getLink();

				if (l != null && l.getLinkType().isPart() && l.isLinkResolved()) {
					continue;
				}

				if (canDeleteLink(l)) {
					links.add(iv);
				}
			}
		}
		return links;
	}

	protected List<Item> getGenerateObject(IStructuredSelection ssel) {
		List<Item> ret = new ArrayList<Item>();
		Object[] selObjects = ssel.toArray();
		for (int i = 0; i < selObjects.length; i++) {
			try {
				Object obj = selObjects[i];
				if (obj instanceof IItemNode) {
					IItemNode iiv = (IItemNode) obj;
					Item item = iiv.getItem();
					if (item != null && item.isResolved() && item.itemHasContent()
							&& item.getContentItem() instanceof IGenerateContent) {
						ret.add(item);
					}
				}
			} catch (Throwable e) {
				WSPlugin.logException(e);
			}
		}
		return ret;
	}

	protected boolean canDeleteLink(Link l) {
		return l != null && !l.isReadOnly() && l.getSource().getType().getItemManager().canDeleteLink(l) == null;
	}

	protected boolean canCreate(Item itemParent, LinkType lt, ItemType it) {
		try {
			IItemManager im = it.getItemManager();

			if (lt == null) {
				return false;
			}
			if (it.isAbstract()) {
				return false;
			}
			if (im.isAbstract(itemParent, lt)) {
				return false;
			}
			if (im.canCreateMeItem(itemParent, lt, it) != null) {
				return false;
			}
			if (lt.getSource().getItemManager().canCreateChildItem(itemParent, lt, it) != null) {
				return false;
			}
			LogicalWorkspace cadseModel = viewUIController.getCadseModel();
			if (cadseModel == null) {
				return false; // not loading ...
			}
			if (viewUIController != null && viewUIController.isRefItemType(it, cadseModel)) {
				return false;
			}

			return true;
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}

	}

	protected Set<IItemNode> getItemsToDelete(IStructuredSelection ssel) {
		HashSet<IItemNode> items = new HashSet<IItemNode>();
		ONE: for (Object obj : ssel.toArray()) {
			IItemNode iiv;
			if (obj instanceof IItemNode) {
				iiv = ((IItemNode) obj);
			} else {
				continue;
			}
			Item item = iiv.getItem();
			if (item == null || !item.isResolved() || item.isStatic()) {
				continue;
			}

			if (item.getType().getItemManager().canDeleteItem(item) != null) {
				continue;
			}
			Link itemLink = iiv.getLink();

			if (itemLink != null && !itemLink.getLinkType().isPart() && item.getType().hasIncomingParts()) {
				continue;
			}

			for (Link l : item.getIncomingLinks()) {
				if (l.isReadOnly()) {
					continue ONE;
				}
			}

			items.add(iiv);
		}

		return items;
	}

	protected Set<IItemNode> getItemsToRename(IStructuredSelection ssel) {
		HashSet<IItemNode> items = new HashSet<IItemNode>();
		for (Object obj : ssel.toArray()) {
			IItemNode iiv;
			if (obj instanceof IItemNode) {
				iiv = ((IItemNode) obj);
			} else {
				continue;
			}
			if (iiv.getItem() == null || !iiv.getItem().isResolved()) {
				continue;
			}
			Item item = iiv.getItem();

			// cannot rename a readonly item, a static item is readonly
			if (item.isReadOnly() || item.isStatic()) {
				continue;
			}
			if (item.getType().getItemManager().canRenameItem(item) != null) {
				continue;
			}

			items.add(iiv);
		}

		return items;
	}

	/**
	 * Adds the items to show to the given list.
	 * 
	 * @param list
	 *            the list to add items to
	 */
	protected Menu computeNewMenu(IWorkbenchWindow workbenchWindow, Item parent) {
		Comparator<IMenuAction> comparator = new Comparator<IMenuAction>() {

			public int compare(IMenuAction o1, IMenuAction o2) {
				return o1.getLabel().compareTo(o2.getLabel());
			}

		};
		LogicalWorkspace cadseModel = viewUIController.getCadseModel();
		if (cadseModel == null) {
			return null;
		}
		if (parent == null || !parent.isResolved()) {
			if (viewUIController != null) {

				SortedSet<IMenuAction> list = new TreeSet<IMenuAction>(comparator);
				ItemType[] types = viewUIController.getFirstItemType(cadseModel);
				for (ItemType it : types) {
					if (it == null) {
						continue;
					}

					if (it.hasIncomingParts()) {
						WSPlugin.logErrorMessage(
								"ItemType {0} is a root element in a cadse view but it has incoming part link", it
										.getName());
					}

					// test si le manager indique qu'il est impossible de creer
					// l'�l�ment.
					if (it.isAbstract() || it.getItemManager().isAbstract(null, null)) {
						continue;
					}

					if (!viewUIController.canCreateItem(it)) {
						continue;
					}

					IItemManager manager = it.getItemManager();
					if (manager.canCreateMeItem(null, null, it) != null) {
						continue;
					}

					list.add(new MenuNewAction(workbenchWindow, null, null, it, viewUIController.getDislplayCreate(
							null, it)));
				}
				return new Menu("new", "New", null, new ArrayList(list));
			}
			return null;
		}

		List<IMenuAction> retlist = new ArrayList<IMenuAction>();
		if (addShortcuts(retlist)) {
			retlist.add(IMenuAction.SEPARATOR);
		}

		int addsep = 0;
		for (LinkType lt : parent.getType().getOutgoingLinkTypes()) {
			if (lt.isDerived()) {
				continue;
			}

			if (viewUIController == null && !lt.isAggregation()) {
				continue;
			}

			if (viewUIController != null && viewUIController.isRefItemType(lt.getDestination(), cadseModel)) {
				continue;
			}

			if (viewUIController != null && !viewUIController.isCreateLink(lt)) {
				continue;
			}

			if (!lt.isPart() && lt.getDestination().hasIncomingParts()) {
				continue;
			}

			if (lt.getMax() != LinkType.UNBOUNDED) {
				List<Link> linkscreated = parent.getOutgoingLinks(lt);
				if (linkscreated.size() >= lt.getMax()) {
					continue;
				}
			}
			SortedSet<IMenuAction> list = new TreeSet<IMenuAction>(comparator);

			ItemType it = lt.getDestination();

			ItemType[] subType = it.getSubTypes();

			boolean canCreate = canCreate(parent, lt, it);

			if (canCreate) {
				list
						.add(new MenuNewAction(workbenchWindow, parent, lt, it, viewUIController.getDislplayCreate(lt,
								it)));
			}
			if (subType.length != 0) {
				addItems(workbenchWindow, list, parent, lt, subType);
			}
			if (addsep > 1 || list.size() > 1) {
				retlist.add(IMenuAction.SEPARATOR);
			}
			retlist.addAll(list);
			addsep = list.size();
		}

		if (viewUIController != null) {
			ItemType[] types = viewUIController.getFirstItemType(cadseModel);
			for (ItemType it : types) {
				if (viewUIController.isRefItemType(it, cadseModel)) {
					continue;
				}

				if (it.isAbstract() || it.getItemManager().isAbstract(null, null)) {
					continue;
				}

				if (!it.hasIncomingParts()) {
					if (addsep > 0) {
						retlist.add(IMenuAction.SEPARATOR);
					}
					addsep = 0;
					retlist.add(new MenuNewAction(workbenchWindow, null, null, it, viewUIController.getDislplayCreate(
							null, it)));
				}
			}
		}

		Menu menu = new Menu("new", "New", null, retlist);
		parent.getType().getItemManager().contributeMenuNewAction(menu, parent);

		// IMenuAction[] dy =
		// parent.getType().getItemManager().getMenuNewAction(parent);
		// if (dy != null && dy.length != 0) {
		// for (IMenuAction dynamic : dy) {
		// retlist.add(dynamic);
		// }
		// retlist.add(IMenuAction.SEPARATOR);
		// }
		return menu;
	}

	/**
	 * Adds the items to show to the given list.
	 * 
	 * @param list
	 *            the list to add items to
	 * @param subitemtypes
	 */
	protected void addItems(IWorkbenchWindow workbenchWindow, SortedSet<IMenuAction> list, Item parent, LinkType lt,
			ItemType[] subType) {

		for (ItemType it : subType) {

			ItemType[] _subType = it.getSubTypes();

			boolean canCreate = canCreate(parent, lt, it);

			if (canCreate) {
				list
						.add(new MenuNewAction(workbenchWindow, parent, lt, it, viewUIController.getDislplayCreate(lt,
								it)));
			}
			if (_subType.length != 0) {
				addItems(workbenchWindow, list, parent, lt, _subType);
			}
		}
	}

	/**
	 * Adds the new wizard shortcuts for the current perspective to the given
	 * list.
	 * 
	 * @param list
	 *            the list to add items to
	 * @return <code>true</code> if any items were added, <code>false</code>
	 *         if none were added
	 */
	protected static boolean addShortcuts(List<IMenuAction> list) {
		boolean added = false;
		// IWorkbenchPage page = workbenchWindow.getActivePage();
		// if (page != null) {
		// String[] wizardIds = page.getNewWizardShortcuts();
		// for (int i = 0; i < wizardIds.length; i++) {
		// IAction action = getAction(wizardIds[i]);
		// if (action != null) {
		// if (!WorkbenchActivityHelper.filterItem(action)) {
		// list.add(new ActionContributionItem(action));
		// added = true;
		// }
		// }
		// }
		// }
		return added;
	}

	protected Set<IItemNode> getItemsToRefresh(IStructuredSelection ssel) {
		HashSet<IItemNode> items = new HashSet<IItemNode>();
		for (Object obj : ssel.toArray()) {
			if (obj != null && obj instanceof IItemNode) {
				items.add((IItemNode) obj);
			}
		}
		return items;

	}
}
