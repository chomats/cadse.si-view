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
 *
 * Copyright (C) 2006-2010 Adele Team/LIG/Grenoble University, France
 */
package fede.workspace.tool.view;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PropertyDialogAction;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;

import fede.plugin.workspace.filters.CustomFiltersActionGroup;
import fede.workspace.tool.view.node.OldItemInViewer;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.WSModelState;
import fr.imag.adele.cadse.core.WorkspaceListener;
import fr.imag.adele.cadse.core.transaction.delta.ImmutableItemDelta;
import fr.imag.adele.cadse.core.transaction.delta.ImmutableWorkspaceDelta;
import fr.imag.adele.cadse.core.impl.CadseIllegalArgumentException;
import fr.imag.adele.fede.workspace.si.view.View;

/**
 * Cette vue repr?sente les item du workspace courant. Nous avons trois mode
 * d'affichage : - aggr?gations; - relations et link - relations et relations
 * inverse.
 * 
 * Le menu contextuel a une zone particuli?re. "WS-Actions" pour les actions du
 * workspace.
 * 
 */

public class WSLinkView extends ViewPart implements ISelectionListener {

	private static final class RefreshWSView implements Runnable {
		private final Collection<IItemNode>	structUpdateItems;
		private final Collection<IItemNode>	updateItems;
		private final TreeViewer				fTreeViewer;
		private final IItemNode					rootWS;
		private final int						structFlag;
		private final boolean					recursifUpdate;

		private RefreshWSView(TreeViewer fTreeViewer, IItemNode rootWS, int structFlag, IItemNode structUpdateItem) {
			this(fTreeViewer, rootWS, structFlag, Collections.singleton(structUpdateItem), Collections.EMPTY_LIST,
					false);
		}

		private RefreshWSView(TreeViewer fTreeViewer, IItemNode rootWS, int structFlag,
				Collection<IItemNode> structUpdateItems, Collection<IItemNode> updateItems, boolean recursifUpdate) {
			this.structUpdateItems = structUpdateItems;
			this.fTreeViewer = fTreeViewer;
			this.rootWS = rootWS;
			this.structFlag = structFlag;
			this.updateItems = updateItems;
			this.recursifUpdate = recursifUpdate;
		}

		public void run() {

			ISelection s = fTreeViewer.getSelection();
			if (updateItems.size() != 0) {
				if (recursifUpdate) {
					for (IItemNode iiv : updateItems) {
						udpateLocal(iiv);
					}
				} else {
					fTreeViewer.update(updateItems.toArray(), null);
				}

			}

			for (IItemNode iiv : structUpdateItems) {
				IItemNode theLocalIIV = iiv;
				if (theLocalIIV.getParent() == rootWS) {
					theLocalIIV = rootWS;
				}

				boolean open = false;
				if (theLocalIIV.isOpen()) {
					theLocalIIV.close();
					open = true;
				}
				if (theLocalIIV == rootWS) {
					theLocalIIV.open();
					theLocalIIV.getChildren();
				}
				fTreeViewer.update(iiv, null);

				fTreeViewer.refresh(theLocalIIV, true);
				if (open) {
					expandIIV(theLocalIIV);
				} else {
					theLocalIIV.close();
				}
			}

			fTreeViewer.setSelection(s);
		}

		private void udpateLocal(final IItemNode iiv) {
			fTreeViewer.update(iiv, null);
			if (iiv.isOpen()) {
				for (IItemNode childIIv : iiv.getChildren()) {
					udpateLocal(childIIv);
				}
			}
		};

		private void expandIIV(final IItemNode iiv) {
			fTreeViewer.setExpandedState(iiv, true);
			for (IItemNode childIIv : iiv.getChildren()) {
				if (childIIv.isOpen()) {
					expandIIV(childIIv);
				}
			}
		}
	}

	public static final String			WS_MB_ADDITIONS			= "WS-Actions";

	private static final String			KEY_SHOW_LINKTYPE		= WSPlugin.NAMESPACE_ID + ".view.SHOW_LINKTYPE";	//$NON-NLS-1$

	private static final String			KEY_SHOW_TOOLTIP		= WSPlugin.NAMESPACE_ID + ".view.SHOW_TOOLTIP";

	private static final String			KEY_SHOW_KIND			= WSPlugin.NAMESPACE_ID + ".view.KEY_SHOW_KIND";

	private static final String			KEY_SHOW_LINK_TYPE_NAME	= WSPlugin.NAMESPACE_ID
																		+ ".view.KEY_SHOW_LINK_TYPE_NAME";

	private IItemNode					rootWS					= new OldItemInViewer();

	private DrillDownAdapter			drillDownAdapter;

	private TreeViewer					fTreeViewer;

	private int							_contentProviderFlag	= 0;

	private CustomFiltersActionGroup	fCustomFiltersActionGroup;

	private LinkViewContentProvider		contentProvider;

	private Action						showLinkType;
	private Action						openItem;
	private Action						showToolTips;
	private Action						showLinkTypeName;
	private Action						showKind;

	protected boolean					_showToolTip			= false;

	private TreeToolTipListener			toolTipListener			= null;

	private boolean						_showKind;

	private boolean						_showLinkTypeName;

	private IMemento					localMemento;

	private WorkspaceListener			_listener;

	/**
	 * The constructor.
	 */
	public WSLinkView() {
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
		fCustomFiltersActionGroup.fillActionBars(bars);
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(Composite parent) {

		// creation du tree viewer.
		fTreeViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(fTreeViewer) {
			@Override
			public void goInto(Object newInput) {
				super.goInto(new OldItemInViewer((ItemInViewer) newInput));
			}

		};

		contentProvider = new LinkViewContentProvider();
		fTreeViewer.setContentProvider(contentProvider); // gestionnaire de
		// contenu
		contentProvider.setContentProviderFlag(getContentProviderFlag());
		fTreeViewer.setLabelProvider(new ViewLinkLabelProvider(parent, this)); // gestionnaire
		// d'affichage
		// viewer.setSorter(new NameSorter());
		fTreeViewer.setInput(new OldItemInViewer(rootWS));
		fTreeViewer.addTreeListener(new ITreeViewerListener() {

			public void treeCollapsed(TreeExpansionEvent event) {
				if (event.getElement() instanceof ItemInViewer) {
					ItemInViewer iiv = (ItemInViewer) event.getElement();
					iiv.close();
				}

			}

			public void treeExpanded(TreeExpansionEvent event) {
				if (event.getElement() instanceof ItemInViewer) {
					ItemInViewer iiv = (ItemInViewer) event.getElement();
					iiv.open();
				}
			}
		});

		new Thread(new Runnable() {
			public void run() {
				synchronized (this) {
					while (View.getInstance() == null || View.getInstance().getWorkspaceLogique() == null) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// ignored ...
						}
					}
				}
				_listener = new LinkWL();
				View.getInstance().getWorkspaceLogique().addListener(_listener, 0xFFFF);
			}
		}, "add listener").start();

		fCustomFiltersActionGroup = new CustomFiltersActionGroup(this, this.fTreeViewer);
		fCustomFiltersActionGroup.restoreState(localMemento);
		fTreeViewer.setSorter(new ViewerSorter());
		setToolTip(getToolTip());
		getSite().setSelectionProvider(fTreeViewer);
		getSite().getPage().addSelectionListener(this);
		makeActions();
		hookContextMenu();
		contributeToActionBars();
	}

	@Override
	public void dispose() {
		super.dispose();
		View.getInstance().getWorkspaceLogique().removeListener(_listener);
		getSite().getPage().removeSelectionListener(this);
	}

	public void setToolTip(boolean toolTip) {
		if (toolTipListener != null) {
			toolTipListener.dispose();
			toolTipListener = null;
		}
		this._showToolTip = toolTip;
		if (toolTip) {
			toolTipListener = new TreeToolTipListener(fTreeViewer.getTree(), new ItemInViewerToolTip());
		}

	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(new GroupMarker(WS_MB_ADDITIONS));

		manager.add(new Separator());

		manager.add(showLinkType);
		manager.add(showToolTips);
		manager.add(showLinkTypeName);
		manager.add(showKind);
		manager.add(openItem);

		manager.add(new Separator());

		drillDownAdapter.addNavigationActions(manager);
		manager.add(new Separator());

		// Other plug-ins can contribute there actions here
		manager.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		manager.add(new Separator());
		PropertyDialogAction a = new PropertyDialogAction(getSite(), fTreeViewer);
		manager.add(a);
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(showLinkType);
		manager.add(showToolTips);
		manager.add(showLinkTypeName);
		manager.add(showKind);
	}

	private void fillLocalToolBar(IToolBarManager manager) {

		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
	}

	public TreeViewer getFTreeViewer() {
		return fTreeViewer;
	}

	private void hookContextMenu() {
		MenuManager manager = new MenuManager("#PopupMenu");
		manager.setRemoveAllWhenShown(true);
		manager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager1) {
				WSLinkView.this.fillContextMenu(manager1);
			}
		});

		Menu menu = manager.createContextMenu(fTreeViewer.getControl());

		fTreeViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(manager, fTreeViewer);
	}

	/**
	 * Restore the state of this view : three options : show all items, show the
	 * relations "to", show the relations "from"
	 */
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		if (memento != null) {
			Integer i = memento.getInteger(KEY_SHOW_LINKTYPE);
			this._contentProviderFlag = i == null ? 0 : i.intValue();

			i = memento.getInteger(KEY_SHOW_TOOLTIP);
			_showToolTip = (i == null ? false : i.intValue() == 1);

			i = memento.getInteger(KEY_SHOW_KIND);
			_showKind = (i == null ? false : i.intValue() == 1);

			i = memento.getInteger(KEY_SHOW_LINK_TYPE_NAME);
			_showLinkTypeName = (i == null ? false : i.intValue() == 1);

			this.localMemento = memento;
		}

	}

	/**
	 * Save the state of this view through the sessions : three options : show
	 * all items, show the relations "to", show the relations "from"
	 */
	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);
		memento.putInteger(KEY_SHOW_LINKTYPE, _contentProviderFlag);
		memento.putInteger(KEY_SHOW_TOOLTIP, getToolTip() ? 1 : 0);
		memento.putInteger(KEY_SHOW_KIND, getShowKind() ? 1 : 0);
		memento.putInteger(KEY_SHOW_LINK_TYPE_NAME, getShowLinkTypeName() ? 1 : 0);
		fCustomFiltersActionGroup.saveState(memento);
	}

	// create view actions
	private void makeActions() {
		openItem = new Action("Open Editor Item", IAction.AS_PUSH_BUTTON) {

			@Override
			public void run() {
				openEditorItem();
			}
		};

		showLinkType = new Action("Show the name of the links", IAction.AS_CHECK_BOX) {
			@Override
			public void run() {
				_contentProviderFlag = (_contentProviderFlag & ~ItemInViewer.SHOW_RELATION_OUTGOING)
						| (~_contentProviderFlag & ItemInViewer.SHOW_RELATION_OUTGOING);
				contentStructreChanged();
			}
		};
		showLinkType.setChecked((_contentProviderFlag & ItemInViewer.SHOW_RELATION_OUTGOING) != 0);
		showToolTips = new Action("Show tool tip", IAction.AS_CHECK_BOX) {
			@Override
			public void run() {
				setToolTip(!getToolTip());
			}
		};
		showToolTips.setChecked(getToolTip());

		showLinkTypeName = new Action("Show link name.", IAction.AS_CHECK_BOX) {
			@Override
			public void run() {
				setShowLinkTypeName(!getShowLinkTypeName());
			}
		};
		showLinkTypeName.setChecked(getShowLinkTypeName());

		showKind = new Action("Show kind of link.", IAction.AS_CHECK_BOX) {
			@Override
			public void run() {
				setShowKind(!getShowKind());
			}
		};
		showKind.setChecked(getShowKind());
	}

	protected void openEditorItem() {
		// ISelection sel = this.fTreeViewer.getSelection();
		// if (sel == null) return;
		// if (sel.isEmpty()) return;
		// if (!(sel instanceof IStructuredSelection)) return;
		// IStructuredSelection ssel = (IStructuredSelection) sel;
		// Object obj = ssel.getFirstElement();
		// if (obj == null) return;
		// if (!(obj instanceof ItemInViewer)) return;
		//
		// ItemInViewer iiv = (ItemInViewer) obj;
		// Item item = iiv.getItem();
		// if (item == null) return;
		//
		// try {
		// WSPlugin.getDefault().getWorkbench()
		// .getActiveWorkbenchWindow()
		// .getActivePage()
		// .openEditor(new ItemEditorInput(item), ItemEditorPart.ID);
		// } catch (PartInitException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	protected void setShowKind(boolean b) {
		_showKind = b;
		updateTree();
	}

	protected boolean getShowKind() {
		return _showKind;
	}

	protected void setShowLinkTypeName(boolean b) {
		_showLinkTypeName = b;
		updateTree();
	}

	protected boolean getShowLinkTypeName() {
		return _showLinkTypeName;
	}

	protected boolean getToolTip() {
		return this._showToolTip;
	}

	public void refresh() {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				fTreeViewer.refresh();
			};
		});
	}

	public void reset() {
		rootWS.close();
		rootWS.open();
		ItemInViewer input = new OldItemInViewer(rootWS);
		fTreeViewer.setInput(input);
		fTreeViewer.setSelection(new StructuredSelection(input));
		fTreeViewer.refresh();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		fTreeViewer.getControl().setFocus();
	}

	void setContentProviderFlag(int addFlag, int removeflag) {
		int newflag = (_contentProviderFlag | addFlag) & ~removeflag;
		if (newflag != _contentProviderFlag) {
			_contentProviderFlag = newflag;
			contentStructreChanged();
		}
	}

	void contentStructreChanged() {
		contentProvider.setContentProviderFlag(getContentProviderFlag());
		rootWS.close();
		rootWS.open();
		refresh(rootWS);
	}

	public int getContentProviderFlag() {
		return _contentProviderFlag;
	}

	public void refresh(final IItemNode iiv) {
		if (iiv == null) {
			throw new CadseIllegalArgumentException("The item is null!!!");
		}
		PlatformUI.getWorkbench().getDisplay().asyncExec(
				new RefreshWSView(fTreeViewer, rootWS, getContentProviderFlag(), iiv));
	}

	public void updateTree() {
		PlatformUI.getWorkbench().getDisplay().asyncExec(
				new RefreshWSView(fTreeViewer, rootWS, getContentProviderFlag(), Collections.EMPTY_LIST, Collections
						.singleton(rootWS), true));

	}

	public IItemNode getRootWS() {
		return rootWS;
	}

	class LinkWL extends WorkspaceListener {
		@Override
		public void workspaceChanged(ImmutableWorkspaceDelta wd) {
			Set<Item> refreshStruct = new HashSet<Item>();
			Set<Item> refreshUpdate = new HashSet<Item>();

			if (wd.currentModelHasState(WSModelState.RUN)) {
				rootWS.open();
				refresh(rootWS);
				return;
			}
			for (ImmutableItemDelta itemDelta : wd.getItems()) {
				if (itemDelta.hasResolvedOutgoingLink() || itemDelta.hasUnresolvedOutgoingLink()
						|| itemDelta.hasAddedOutgoingLink() || itemDelta.hasRemovedOutgoingLink()) {
					refreshStruct.add(itemDelta.getItem());
					continue;
				}
				if (itemDelta.hasSetAttributes()) {
					refreshUpdate.add(itemDelta.getItem());
				}
			}
			final Set<IItemNode> refreshItemsStruct = findItemInViewer(refreshStruct, true);
			final Set<IItemNode> refreshItemsUpdate = findItemInViewer(refreshUpdate, true);

			PlatformUI.getWorkbench().getDisplay().asyncExec(
					new RefreshWSView(fTreeViewer, rootWS, getContentProviderFlag(), refreshItemsStruct,
							refreshItemsUpdate, false));

		}
	}

	private Set<IItemNode> findItemInViewer(Set<Item> itemFound, boolean returnIfFound) {
		Set<IItemNode> ret = new HashSet<IItemNode>();
		findItemInViewer(ret, getRootWS(), itemFound, returnIfFound);
		return ret;
	}

	private void findItemInViewer(Set<IItemNode> refreshItems, IItemNode current, Set<Item> itemFound,
			boolean returnIfFound) {
		if (itemFound.contains(current.getItem())) {
			refreshItems.add(current);
			if (returnIfFound) {
				return;
			}
		}
		if (current.hasChildren()) {
			for (IItemNode childIIV : current.getChildren()) {
				findItemInViewer(refreshItems, childIIV, itemFound, returnIfFound);
			}
		}
	}

	public void refreshUnresolvedLink() {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				refreshUnresolvedLink(rootWS);
			}
		});
	}

	private void refreshUnresolvedLink(IItemNode riiv) {
		IItemNode iiv;
		if (!riiv.isOpen()) {
			return;
		}
		IItemNode[] children = riiv.getChildren();
		for (int i = 0; i < children.length; i++) {
			iiv = children[i];
			Link l = iiv.getLink();
			if (l != null) {
				fTreeViewer.update(iiv, null);
			}
			if (iiv.isOpen()) {
				refreshUnresolvedLink(iiv);
			}
		}
	}

	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == IPropertySheetPage.class) {
			return new FieldsPropertySheetPage();
		}
		return super.getAdapter(adapter);
	}

	public void refresh(Item item) {
		refresh(rootWS, item);
	}

	private void refresh(IItemNode iiv, Item item) {
		if (iiv.getItem() == item) {
			refresh(iiv);
		}
		if (iiv.hasChildren()) {
			for (IItemNode childIIV : iiv.getChildren()) {
				refresh(childIIV, item);
			}
		}
	}

	private void setRoot(Item item) {
		fTreeViewer.setInput(item);
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			Object o = ssel.getFirstElement();
			if (o instanceof ItemInViewer) {
				Item item = ((ItemInViewer) o).getItem();
				if (item != null) {
					setRoot(item);
				}
			}
		}
	}

}
