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
package fr.imag.adele.cadse.eclipse.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.views.properties.IPropertySheetPage;

import fede.plugin.workspace.filters.CustomFiltersActionGroup;
import fede.workspace.model.manager.properties.impl.ui.FieldsPropertySheetPage;
import fede.workspace.tool.view.ItemInViewer;
import fede.workspace.tool.view.ItemInViewerToolTip;
import fede.workspace.tool.view.TreeToolTipListener;
import fede.workspace.tool.view.WSPlugin;
import fede.workspace.tool.view.dnd.ItemTransfer;
import fede.workspace.tool.view.dnd.WSViewDragListener;
import fede.workspace.tool.view.dnd.WSViewDropAdapter;
import fede.workspace.tool.view.menu.ItemActionGroup;
import fede.workspace.tool.view.node.AbstractCadseViewNode;
import fede.workspace.tool.view.node.CadseViewModelController;
import fede.workspace.tool.view.node.ItemNode;
import fede.workspace.tool.view.node.ItemTypeNode;
import fede.workspace.tool.view.node.LinkNode;
import fede.workspace.tool.view.node.LinkTypeNode;
import fede.workspace.tool.view.node.RootNode;
import fede.workspace.tool.view.oper.WSCheckItemInViewer;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.ChangeID;
import fr.imag.adele.cadse.core.DerivedLink;
import fr.imag.adele.cadse.core.IItemManager;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.LogicalWorkspace;
import fr.imag.adele.cadse.core.WSModelState;
import fr.imag.adele.cadse.core.WorkspaceListener;
import fr.imag.adele.cadse.core.delta.ImmutableItemDelta;
import fr.imag.adele.cadse.core.delta.ImmutableWorkspaceDelta;
import fr.imag.adele.cadse.core.delta.ItemDelta;
import fr.imag.adele.cadse.core.impl.CadseIllegalArgumentException;
import fr.imag.adele.cadse.core.key.ISpaceKey;
import fr.imag.adele.cadse.core.oper.WSCheckAttribute;
import fr.imag.adele.cadse.core.oper.WSCheckItem;
import fr.imag.adele.cadse.core.transaction.LogicalWorkspaceTransaction;
import fr.imag.adele.cadse.core.ui.view.ViewDescription;
import fr.imag.adele.cadse.core.ui.view.ViewFilter;
import fr.imag.adele.cadse.core.util.ArraysUtil;
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

public abstract class AbstractCadseTreeViewUI extends WorkspaceListener implements CadseViewModelController,
		IViewLinkManager, IViewDisplayConfiguration, ViewDescription {
	private final class TreeViewerListener implements ITreeViewerListener {
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
	}

	private final class SelectionChangeListener implements ISelectionChangedListener {
		public void selectionChanged(SelectionChangedEvent event) {
			try {
				if (((IStructuredSelection) event.getSelection()).size() == 1) {
					IItemNode node = (IItemNode) ((IStructuredSelection) event.getSelection()).getFirstElement();
					if (View.isStarted()) {
						if (View.getInstance().getTestService().isRecordedTests()) {
							if (node.getItem() != null) {
								View.getInstance().getTestService().registerIfNeed(new WSCheckItem(node.getItem()));
								Item item = node.getItem();
								String[] attributesNames = item.getType().getAttributeTypeIds();
								for (int i = 0; i < attributesNames.length; i++) {
									View.getInstance().getTestService().registerIfNeed(
											new WSCheckAttribute(item, attributesNames[i]));
								}
							}
							View.getInstance().getTestService().registerIfNeed(
									new WSCheckItemInViewer(getViewPart(), node));

						}
					}
				}
			} catch (Throwable e) {
			}

		}
	}

	private final class DblClickListener implements IDoubleClickListener {
		public void doubleClick(DoubleClickEvent event) {
			IStructuredSelection sel = (IStructuredSelection) event.getSelection();
			ItemInViewer iiv = (ItemInViewer) sel.getFirstElement();
			if (iiv == null) {
				return;
			}
			Item item = iiv.getItem();
			if (item == null) {
				return;
			}

			IItemManager im = item.getType().getItemManager();
			im.doubleClick(item);

			LogicalWorkspaceTransaction copy = item.getLogicalWorkspace().createTransaction();
			ItemDelta oper = copy.getItem(item.getId());
			if (oper == null) {
				copy.rollback();
			} else {
				oper.doubleClick();
				try {
					copy.commit();
				} catch (CadseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

	private ItemActionGroup				_actionSet;

	public static final String			WS_MB_ADDITIONS			= "WS-Actions";

	private static final String			KEY_SHOW_LINKTYPE		= WSPlugin.NAMESPACE_ID + ".view.SHOW_LINKTYPE";	//$NON-NLS-1$

	private static final String			KEY_SHOW_TOOLTIP		= WSPlugin.NAMESPACE_ID + ".view.SHOW_TOOLTIP";

	private static final String			KEY_SHOW_KIND			= WSPlugin.NAMESPACE_ID + ".view.KEY_SHOW_KIND";

	private static final String			KEY_SHOW_Incomings		= WSPlugin.NAMESPACE_ID + ".view.Incomings";

	private static final String			KEY_SHOW_LINK_TYPE_NAME	= WSPlugin.NAMESPACE_ID
																		+ ".view.KEY_SHOW_LINK_TYPE_NAME";

	protected AbstractCadseViewNode		rootWS;

	private DrillDownAdapter			drillDownAdapter;

	private TreeViewer					fTreeViewer;

	private int							_contentProviderFlag	= 0;

	private CustomFiltersActionGroup	fCustomFiltersActionGroup;

	private ViewContentProvider			contentProvider;

	// private Action showLinkType;
	private Action						showToolTips;
	private Action						showLinkTypeName;
	private Action						showKind;
	private Action						showIncomings;

	protected boolean					_showToolTip			= false;

	private TreeToolTipListener			toolTipListener			= null;

	private boolean						_showKind;
	private boolean						_showIncomings;

	private boolean						_showLinkTypeName;

	private IMemento					localMemento;

	protected boolean					_isRecomputeChildren	= false;

	protected Font						italique;

	protected Font						gras;

	private final IShellProvider		shellprovider;

	private final IWorkbenchWindow		workbenchWindow;

	private final IWorkbenchPartSite	workbenchPartSite;

	private final IViewSite				viewsite;

	private Action						refresh;

	private Action						openPropertyView;

	/**
	 * The constructor.
	 */
	public AbstractCadseTreeViewUI(IShellProvider shellprovider, IWorkbenchWindow workbenchWindow,
			IWorkbenchPartSite workbenchPartSite, IViewSite viewsite) {
		rootWS = new RootNode(this);
		add(rootWS);
		this.shellprovider = shellprovider;
		this.workbenchWindow = workbenchWindow;
		this.workbenchPartSite = workbenchPartSite;
		this.viewsite = viewsite;
		setKind(WorkspaceListener.ListenerKind.UI);

	}

	public AbstractCadseTreeViewUI(IViewSite site) {
		rootWS = new RootNode(this);
		add(rootWS);
		this.shellprovider = site;
		this.workbenchWindow = site.getWorkbenchWindow();
		this.workbenchPartSite = site;
		this.viewsite = site;
		setKind(WorkspaceListener.ListenerKind.UI);
	}

	protected void contributeToActionBars() {
		IActionBars bars = getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
		if (fCustomFiltersActionGroup != null) {
			fCustomFiltersActionGroup.fillActionBars(bars);
		}
		setKind(WorkspaceListener.ListenerKind.UI);
	}

	protected IActionBars getActionBars() {
		return this.getViewSite().getActionBars();
	}

	protected IViewSite getViewSite() {
		return viewsite;
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(final Composite parent) {
		if (View.getInstance() == null) {
			createPartControlWait(parent, "No service view avaible !!!");
		}
		if (View.getInstance() != null && View.getInstance().getWorkspaceDomain() == null) {
			createPartControlWait(parent, "No service cadse domain avaible !!!");
		}
		createControlAfter(parent);
	}

	private void createPartControlWait(final Composite parent, String msg) {

		View.addAfterListener(new ViewAfterInit() {
			public void afterInit() {

				View.getInstance().getWorkspaceDomain().getLogicalWorkspace().addListener(AbstractCadseTreeViewUI.this,
						0xFFFFF);
				refresh();
			}
		});
	}

	public void createControlAfter(Composite parent) {
		if (fTreeViewer != null) {
			return;
		}
		Font f = parent.getFont();
		FontData fd = f.getFontData()[0];
		fd.setStyle(SWT.ITALIC);
		italique = new Font(null, fd);
		fd.setStyle(SWT.BOLD);
		gras = new Font(null, fd);

		// creation du tree viewer.
		fTreeViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(fTreeViewer);

		contentProvider = new ViewContentProvider(this);
		fTreeViewer.setContentProvider(contentProvider); // gestionnaire de
		// contenu
		fTreeViewer.setLabelProvider(new ViewLabelProvider(this)); // gestionnaire
		// d'affichage
		fTreeViewer.setUseHashlookup(true);
		fTreeViewer.setInput(rootWS);
		fTreeViewer.addTreeListener(new TreeViewerListener());

		fTreeViewer.addDoubleClickListener(new DblClickListener());
		fTreeViewer.addPostSelectionChangedListener(new SelectionChangeListener());

		int ops = DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_TARGET_MOVE;
		Transfer[] transfers = new Transfer[] { ItemTransfer.getInstance() };
		fTreeViewer.addDropSupport(ops, transfers, new WSViewDropAdapter(fTreeViewer));

		fTreeViewer.addDragSupport(ops, transfers, new WSViewDragListener(fTreeViewer));

		if (View.getInstance() != null && View.getInstance().getWorkspaceDomain() != null
				&& View.getInstance().getWorkspaceDomain().getLogicalWorkspace() != null) {
			View.getInstance().getWorkspaceDomain().getLogicalWorkspace().addListener(this, 0xFFFFF);
		}
		if (getSite() != null) {
			fCustomFiltersActionGroup = new CustomFiltersActionGroup(getSite().getId(), this.fTreeViewer);
			if (localMemento != null) {
				fCustomFiltersActionGroup.restoreState(localMemento);
			}
		} else {
			Logger log = Logger.getLogger("Cadseview");
			log.log(Level.WARNING, "Get site is null, cannot instanciate Custom filter");
		}
		// fTreeViewer.setSorter(new WSViewerSorter());
		setToolTip(getToolTip());
		setSelectionProvider();

		makeActions();
		hookContextMenu();
		contributeToActionBars();
	}

	private void setSelectionProvider() {
		getSite().setSelectionProvider(fTreeViewer);
	}

	public void dispose() {
		if (_actionSet != null) {
			_actionSet.dispose();
		}
		removeListener();
	}

	private void removeListener() {
		if (View.getInstance() != null && View.getInstance().getWorkspaceDomain() != null
				&& View.getInstance().getWorkspaceDomain().getLogicalWorkspace() != null) {
			View.getInstance().getWorkspaceDomain().getLogicalWorkspace().removeListener(this);
		}
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

	protected void fillContextMenu(IMenuManager manager) {

		fillActionSet(manager);

		manager.add(new GroupMarker(WS_MB_ADDITIONS));

		// manager.add(new Separator());

		manager.add(showToolTips);
		manager.add(showLinkTypeName);
		manager.add(showKind);
		manager.add(showIncomings);
		fillflags(manager);

		manager.add(new Separator());

		drillDownAdapter.addNavigationActions(manager);
		manager.add(new Separator());

		// Other plug-ins can contribute there actions here
		manager.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		// manager.add(new Separator());
		manager.add(openPropertyView);
		// PropertyDialogAction a = new PropertyDialogAction(getSite(),
		// fTreeViewer);
		// manager.add(a);
	}

	protected void fillActionSet(IMenuManager manager) {
		_actionSet.setContext(new ActionContext(fTreeViewer.getSelection()));
		_actionSet.fillContextMenu(manager);
		_actionSet.setContext(null);
	}

	protected void fillflags(IMenuManager manager) {

	}

	protected void fillLocalPullDown(IMenuManager manager) {
		// manager.add(showLinkType);
		manager.add(showToolTips);
		manager.add(showLinkTypeName);
		manager.add(showKind);
		manager.add(showIncomings);
		manager.add(refresh);
	}

	protected void fillLocalToolBar(IToolBarManager manager) {

		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
	}

	public TreeViewer getFTreeViewer() {
		return fTreeViewer;
	}

	protected void hookContextMenu() {
		MenuManager manager = new MenuManager("#PopupMenu");
		manager.setRemoveAllWhenShown(true);

		manager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager1) {
				AbstractCadseTreeViewUI.this.fillContextMenu(manager1);
			}

		});

		Menu menu = manager.createContextMenu(fTreeViewer.getControl());
		fTreeViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(manager, fTreeViewer);
	}

	protected IWorkbenchPartSite getSite() {
		return workbenchPartSite;
	}

	/**
	 * Restore the state of this view : three options : show all items, show the
	 * relations "to", show the relations "from"
	 */
	public void loadState(IMemento memento) throws PartInitException {
		if (memento != null) {
			Integer i = memento.getInteger(KEY_SHOW_LINKTYPE);
			this._contentProviderFlag = i == null ? 0 : i.intValue();

			i = memento.getInteger(KEY_SHOW_TOOLTIP);
			_showToolTip = (i == null ? false : i.intValue() == 1);

			i = memento.getInteger(KEY_SHOW_KIND);
			_showKind = (i == null ? false : i.intValue() == 1);

			i = memento.getInteger(KEY_SHOW_LINK_TYPE_NAME);
			_showLinkTypeName = (i == null ? false : i.intValue() == 1);

			i = memento.getInteger(KEY_SHOW_Incomings);
			_showIncomings = (i == null ? false : i.intValue() == 1);

			this.localMemento = memento;
		}

	}

	/**
	 * Save the state of this view through the sessions : three options : show
	 * all items, show the relations "to", show the relations "from"
	 */
	public void saveState(IMemento memento) {
		memento.putInteger(KEY_SHOW_LINKTYPE, _contentProviderFlag);
		memento.putInteger(KEY_SHOW_TOOLTIP, getToolTip() ? 1 : 0);
		memento.putInteger(KEY_SHOW_KIND, getShowKind() ? 1 : 0);
		memento.putInteger(KEY_SHOW_Incomings, getShowIncomings() ? 1 : 0);
		memento.putInteger(KEY_SHOW_LINK_TYPE_NAME, getShowLinkTypeName() ? 1 : 0);
		if (fCustomFiltersActionGroup != null) {
			fCustomFiltersActionGroup.saveState(memento);
		}
	}

	// create view actions
	protected void makeActions() {
		_actionSet = createActionSet();

		openPropertyView = new Action("Open properties view", IAction.AS_PUSH_BUTTON) {

			@Override
			public void run() {
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
							"org.eclipse.ui.views.PropertySheet");
				} catch (PartInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		};

		refresh = new Action("refresh", IAction.AS_PUSH_BUTTON) {

			@Override
			public void run() {
				refresh();
			}
		};

		// showLinkType = new Action("Show the name of the links",
		// IAction.AS_CHECK_BOX) {
		// @Override
		// public void run() {
		// _contentProviderFlag = (_contentProviderFlag &
		// ~ItemInViewer.SHOW_RELATION_OUTGOING)
		// | (~_contentProviderFlag & ItemInViewer.SHOW_RELATION_OUTGOING);
		// contentStructreChanged();
		// }
		// };
		// showLinkType.setChecked((_contentProviderFlag &
		// ItemInViewer.SHOW_RELATION_OUTGOING) != 0);
		showToolTips = new Action("Show tool tip", IAction.AS_CHECK_BOX) {
			@Override
			public void run() {
				setToolTip(!getToolTip());
			}
		};
		showToolTips.setChecked(getToolTip());

		showLinkTypeName = new Action("Show link name", IAction.AS_CHECK_BOX) {
			@Override
			public void run() {
				setShowLinkTypeName(!getShowLinkTypeName());
				contentStructreChanged();
			}
		};
		showLinkTypeName.setChecked(getShowLinkTypeName());

		showKind = new Action("Show kind of link", IAction.AS_CHECK_BOX) {
			@Override
			public void run() {
				setShowKind(!getShowKind());
				contentStructreChanged();
			}
		};
		showKind.setChecked(getShowKind());

		showIncomings = new Action("Show incomings links", IAction.AS_CHECK_BOX) {
			@Override
			public void run() {
				setShowIncomings(!getShowIncomings());
				contentStructreChanged();
			}
		};
		showIncomings.setChecked(getShowIncomings());
	}

	protected ItemActionGroup createActionSet() {
		return new ItemActionGroup(this);
	}

	protected ItemActionGroup getActionSet() {
		return _actionSet;
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

	public void setShowIncomings(boolean b) {
		_showIncomings = b;
		updateTree();
	}

	protected boolean getShowIncomings() {
		return _showIncomings;
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
		// can be null if no CadseDomain
		if (fTreeViewer != null) {
			return;
		}
		rootWS.close();
		rootWS.open();
		fTreeViewer.setInput(rootWS);
		fTreeViewer.setSelection(new StructuredSelection(rootWS));
		fTreeViewer.refresh();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		// can be null if no CadseDomain
		if (fTreeViewer != null) {
			fTreeViewer.getControl().setFocus();
		}
	}

	void setContentProviderFlag(int addFlag, int removeflag) {
		int newflag = (_contentProviderFlag | addFlag) & ~removeflag;
		if (newflag != _contentProviderFlag) {
			_contentProviderFlag = newflag;
			contentStructreChanged();
		}
	}

	protected void contentStructreChanged() {
		rootWS.close();
		rootWS.open();
		refresh(rootWS);
	}

	public int getContentProviderFlag() {
		return _contentProviderFlag;
	}

	public void refresh(final AbstractCadseViewNode iiv) {
		if (iiv == null) {
			throw new CadseIllegalArgumentException("The item is null!!!");
		}
		PlatformUI.getWorkbench().getDisplay().asyncExec(new RefreshWSView(fTreeViewer, iiv));
	}

	public void updateTree() {
		PlatformUI.getWorkbench().getDisplay().asyncExec(
				new RefreshWSView(fTreeViewer, Collections.EMPTY_LIST, Collections.singleton(rootWS), true));

	}

	public AbstractCadseViewNode getRootWS() {
		return rootWS;
	}

	@Override
	public void workspaceChanged(final ImmutableWorkspaceDelta wd) {
		if (PlatformUI.getWorkbench().getDisplay().isDisposed()) {
			removeListener();
			return;
		}
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
				Set<IItemNode> refreshStruct = new HashSet<IItemNode>();
				Set<IItemNode> refreshRemoveNode = new HashSet<IItemNode>();
				Set<IItemNode> refreshUpdate = new HashSet<IItemNode>();

				if (wd.currentModelHasState(WSModelState.RUN)) {
					rootWS.recomputeChildren();
					loadView();
					return;
				}
				if (wd.getLoadedItems() != null) {
					rootWS.recomputeChildren();
					return;
				}

				for (ImmutableItemDelta itemDelta : wd.getItems()) {

					if (itemDelta.isDeleted()) {
						refreshRemoveNode.addAll(getNodeFromElement(itemDelta.getItem()));
					}
					for (Link l : itemDelta.getLinksRemoved()) {
						refreshRemoveNode.addAll(getNodeFromElement(l));
					}
					if (itemDelta.isCreated() || itemDelta.isDeleted() || itemDelta.hasResolvedOutgoingLink()
							|| itemDelta.hasUnresolvedOutgoingLink() || itemDelta.hasResolvedIncomingLink()
							|| itemDelta.hasRemovedOutgoingLink()) {

						refreshStruct.addAll(getParentNode(itemDelta.getItem()));
						continue;
					}
					if (itemDelta.hasSetAttributes()) {
						refreshUpdate.addAll(getNodeFromElement(itemDelta.getItem()));
						continue;
					}
				}
				if (fTreeViewer.getTree().isDisposed()) {
					return;
				}

				if (refreshRemoveNode.size() != 0) {
					fTreeViewer.remove(refreshRemoveNode.toArray());
				}
				refreshUpdate.removeAll(refreshRemoveNode);
				if (refreshUpdate.size() != 0) {
					if (fTreeViewer.getTree().isDisposed()) {
						return;
					}
					fTreeViewer.update(refreshUpdate.toArray(), null);
				}

				for (IItemNode iiv : refreshStruct) {
					((AbstractCadseViewNode) iiv).recomputeChildren();
					if (fTreeViewer.getTree().isDisposed()) {
						return;
					}
					fTreeViewer.refresh(iiv, true);
				}

			}
		});

	}

	/**
	 * You can create item when the view is loaded.
	 * 
	 */
	protected void loadView() {

	}

	public Object getAdapter(Class adapter) {
		if (adapter == IPropertySheetPage.class) {
			return new FieldsPropertySheetPage();
		}
		return null;
	}

	public void refresh(Item item) {
		refresh(rootWS, item);
	}

	private void refresh(AbstractCadseViewNode iiv, Item item) {
		if (iiv.getItem() == item) {
			refresh(iiv);
		}
		if (iiv.hasChildren()) {
			for (AbstractCadseViewNode childIIV : iiv.getChildren()) {
				refresh(childIIV, item);
			}
		}
	}

	public LogicalWorkspace getCadseModel() {
		if (View.getInstance() == null) {
			return null;
		}
		return View.getInstance().getWorkspaceLogique();
	}

	public abstract boolean isItemType(ItemType it, LogicalWorkspace cadseModel);

	public abstract boolean isRefItemType(ItemType it, LogicalWorkspace cadseModel);

	public abstract boolean isFirstItemType(ItemType it, LogicalWorkspace cadseModel);

	public boolean isFirstItem(Item item, LogicalWorkspace cadseModel) {
		return (isFirstItemType(item.getType(), cadseModel));
	}

	public abstract ItemType[] getFirstItemType(LogicalWorkspace cadseModel);

	protected abstract boolean isLink(Link link);

	public abstract boolean isCreateLink(LinkType lt);

	@Deprecated
	public String getDislplayCreate(LinkType link) {
		return null;
	}

	public String getDislplayCreate(LinkType link, ItemType destItemType) {
		return getDislplayCreate(link);
	}

	public abstract boolean isAggregationLink(Link link);

	public Font getDisplayFont(IItemNode node) {
		return this.gras;
	}

	public Font getDisplayFont(LinkNode node) {
		if (node.getLink() != null && !node.getLink().isLinkResolved()) {
			return this.italique;
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.eclipse.view.IViewDisplayConfiguration#getDisplayImage(fede.workspace.tool.view.node.LinkNode)
	 */
	public Image getDisplayImage(LinkNode node) {
		Item destination = node.getItem();
		ItemType it = destination.getType();
		Image ret = WSPlugin.getDefault().getImageFrom(it, destination);
		if (ret != null) {
			return ret;
		}

		String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.eclipse.view.IViewDisplayConfiguration#getDisplayImage(fr.imag.adele.cadse.core.IItemNode)
	 */
	public Image getDisplayImage(IItemNode node) {
		Item destination = node.getItem();
		ItemType it = destination.getType();
		Image ret = WSPlugin.getDefault().getImageFrom(it, destination);
		if (ret != null) {
			return ret;
		}

		String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.eclipse.view.IViewDisplayConfiguration#getDisplayText(fr.imag.adele.cadse.core.IItemNode)
	 */
	public String getDisplayText(IItemNode node) {
		StringBuilder begin = new StringBuilder();
		begin.append(node.getItem().getDisplayName());
		return begin.toString();
	}

	protected String toStringKind(LinkType lt) {
		StringBuilder ret = new StringBuilder();
		ret.append("[");
		if (lt.isPart()) {
			ret.append("p");
		} else {
			ret.append(" ");
		}
		if (lt.isAggregation()) {
			ret.append("a");
		} else {
			ret.append(" ");
		}
		if (lt.isRequire()) {
			ret.append("r");
		} else {
			ret.append(" ");
		}
		if (lt.isComposition()) {
			ret.append("c");
		} else {
			ret.append(" ");
		}
		ret.append("] ");
		return ret.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.eclipse.view.IViewDisplayConfiguration#getDisplayText(fede.workspace.tool.view.node.LinkNode)
	 */
	public String getDisplayText(LinkNode node) {

		Link link = node.getLink();
		StringBuilder begin = new StringBuilder();
		LinkType lt = link.getLinkType();
		link.toString();
		if (node.getKind() == ItemInViewer.LINK_INCOMING) {
			begin.append("<-- ");
		}
		if (getShowKind()) {
			if (lt == null) {
				begin.append("[???] ");
			} else {
				begin.append(toStringKind(lt));
			}
		}
		if (getShowLinkTypeName()) {
			if (lt == null) {
				begin.append("??? ");
			} else {
				begin.append(lt.getDisplayName()).append(" ");
			}
		}
		if (begin.length() > 0) {
			begin.append("--> ");
		}
		Item dest = null;
		if (node.getKind() == ItemInViewer.LINK_INCOMING) {
			dest = link.getSource();
		} else {
			dest = link.getDestination();
		}
		begin.append(node.getItem().getDisplayName());

		return begin.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.eclipse.view.IViewDisplayConfiguration#getDisplayText(fr.imag.adele.cadse.core.Link,
	 *      fr.imag.adele.cadse.core.Item)
	 */
	public String getDisplayText(Link link, Item destination) {
		return destination.getDisplayName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.eclipse.view.IViewDisplayConfiguration#getDisplayToolTip(fr.imag.adele.cadse.core.Item)
	 */
	public String getDisplayToolTip(Item theItem) {
		StringBuilder sb = new StringBuilder();
		sb.append("\n#class: " + theItem.getClass().getName());
		ItemType itemType = theItem.getType();
		sb.append("\n#type: ").append(itemType.getId()).append("\n");
		sb.append("       ").append(itemType.getName()).append("\n");
		sb.append("\n#id         : ").append(theItem.getId());
		if (itemType.getSpaceKeyType() != null) {
			ISpaceKey key = theItem.getKey();
			if (key == null) {
				sb.append("\n#key        : null key");
			} else {
				sb.append("\n#key        : ");
				key.getQualifiedString(sb);
			}
		} else {
			sb.append("\n#key        : no key");
		}
		sb.append("\n#qualified name  : ").append(theItem.getQualifiedName());
		sb.append("\n#name : ").append(theItem.getName());
		sb.append("\n#display name : ").append(theItem.getDisplayName());
		tooTipParent(theItem, sb);
		if (theItem.isReadOnly()) {
			sb.append("\nitem readonly");
		}
		List<Item> parentComposite = theItem.getCompositeParent();
		if (parentComposite != null && parentComposite.size() > 0) {
			sb.append("\ncomposite parent:\n");
			for (Item pitem : parentComposite) {
				sb.append("   - ");
				sb.append(pitem.getQualifiedName());

			}
		}
		Set<Item> comp = theItem.getComponents();
		if (comp.size() > 0) {
			sb.append("\ncomponants:");
			Item[] linkArray = comp.toArray(new Item[0]);
			Arrays.sort(linkArray, new Comparator<Item>() {

				public int compare(Item o1, Item o2) {
					return o1.getQualifiedName().compareTo(o2.getQualifiedName());
				}
			});
			for (Item link2 : linkArray) {
				sb.append("\n   - ").append(link2.getQualifiedName());
			}
		}
		Set<DerivedLink> derivedLink = theItem.getDerivedLinks();
		if (derivedLink.size() > 0) {
			sb.append("\nderived links:");
			for (DerivedLink link2 : derivedLink) {
				sb.append("\n   - ").append(link2.getDestinationQualifiedName()).append(" (").append(
						link2.getLinkType().getName()).append(")");
			}
		}

		return sb.toString();
	}

	private void tooTipParent(Item theItem, StringBuilder sb) {
		Item parent = theItem.getPartParent();
		if (parent != null) {
			sb.append("\n#parent id : ").append(parent.getId());
			ISpaceKey pkey = parent.getKey();
			if (pkey != null) {
				sb.append("\n#parent key : ");
				pkey.getQualifiedString(sb);
			}
			sb.append("\n#parent un : ").append(parent.getQualifiedName());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.eclipse.view.IViewDisplayConfiguration#getDisplayToolTip(fr.imag.adele.cadse.core.Link)
	 */
	public String getDisplayToolTip(Link link) {
		StringBuilder sb = new StringBuilder();
		if (link == null) {
			return "link is null !!";
		}
		sb.append("\n#class: " + link.getClass().getName());
		sb.append("\n").append(link.toString());
		if (link.isReadOnly()) {
			sb.append("\nlink readonly\n");
		}
		if (link.getLinkType() == null) {

		}
		Item theItem = link.getDestination(false);
		sb.append("\n#class: " + theItem.getClass().getName());
		ItemType itemType = theItem.getType();
		if (itemType == null) {
			sb.append("\n#type: no volue !!!");
		} else {
			sb.append("\n#type: ").append(itemType.getId());
			sb.append("\n     : ").append(itemType.getName());
		}
		sb.append("\n#id         : ").append(theItem.getId());
		if (itemType != null && theItem.isResolved()) {
			if (itemType.getSpaceKeyType() != null) {
				ISpaceKey key = theItem.getKey();
				if (key == null) {
					sb.append("\n#key        : null key");
				} else {
					sb.append("\n#key        : ");
					key.getQualifiedString(sb);
				}
			} else {
				sb.append("\n#key        : no key");
			}
		}
		sb.append("\n#qualified name  : ").append(theItem.getQualifiedName());
		sb.append("\n#name : ").append(theItem.getName());
		sb.append("\n#display name : ").append(theItem.getDisplayName());
		tooTipParent(theItem, sb);
		if (link.isLinkResolved()) {

			if (/* heItem.isResolved() && */theItem.isReadOnly()) {
				sb.append("\nitem readonly");
			}
			List<Item> parentComposite = theItem.getCompositeParent();
			if (parentComposite != null && parentComposite.size() > 0) {
				sb.append("\ncomposite parent:\n");
				for (Item pitem : parentComposite) {
					sb.append("   - ");
					sb.append(pitem.getQualifiedName());

				}
			}

			Set<Item> comp = theItem.getComponents();
			if (comp != null && comp.size() > 0) {
				sb.append("\ncomponants:");
				Item[] linkArray = comp.toArray(new Item[0]);
				Arrays.sort(linkArray, new Comparator<Item>() {

					public int compare(Item o1, Item o2) {
						return o1.getQualifiedName().compareTo(o2.getQualifiedName());
					}
				});
				for (Item link2 : linkArray) {
					sb.append("\n   - ").append(link2.getQualifiedName());
				}
			}
			Set<DerivedLink> derivedLink = theItem.getDerivedLinks();
			if (derivedLink != null && derivedLink.size() > 0) {
				sb.append("\nderived links:");
				for (DerivedLink link2 : derivedLink) {
					sb.append("\n   - ").append(link2.getDestinationQualifiedName()).append(" (").append(
							link2.getLinkType().getName()).append(")");
				}
			}
		}

		// case NOT_ATTACHED_OR_ONLY_IN_WORKSPACE:
		// item = (Item) value;
		// sb.append(item.getType().getId());
		//
		// break;

		return sb.toString();
	}

	public AbstractCadseViewNode[] getChildren(AbstractCadseViewNode node) {
		LogicalWorkspace cadseModel = getCadseModel();
		if (cadseModel == null) {
			return AbstractCadseViewNode.EMPTY;
		}

		if (node == rootWS) {
			return getFirstChildren();
		}
		if (node.getKind() == ItemInViewer.ITEM || node.getKind() == ItemInViewer.LINK_OUTGOING) {
			Item item = node.getItem();

			// if (!item.isResolved()) return AbstractCadseViewNode.EMPTY;
			List<LinkNode> ret = new ArrayList<LinkNode>();
			List<? extends Link> outgoingLinks = item.getOutgoingLinks();
			if (outgoingLinks != null) {
				for (Link l : outgoingLinks) {
					if (!isItemType(l.getDestinationType(), cadseModel)) {
						continue;
					}
					if (isLink(l)) {
						ret.add(createLinkNode(node, l));
					}
				}
			}
			if (_showIncomings) {
				List<? extends Link> incomingsLinks = item.getIncomingLinks();
				if (incomingsLinks != null) {
					for (Link l : incomingsLinks) {
						ret.add(createLinkIncomingNode(node, l));
					}
				}
			}
			return sort(item, ret.toArray(new LinkNode[ret.size()]));
		}
		return AbstractCadseViewNode.EMPTY;
	}

	protected AbstractCadseViewNode[] sort(Item itemParent, LinkNode[] nodes) {
		return nodes;
	}

	protected AbstractCadseViewNode[] getFirstChildren() {
		LogicalWorkspace model = getCadseModel();
		if (model == null) {
			return AbstractCadseViewNode.EMPTY;
		}
		ItemType[] itemtypes = getFirstItemType(model);
		List<ItemNode> ret = new ArrayList<ItemNode>();
		for (ItemType it : itemtypes) {
			if (it == null) {
				continue;
			}

			it.addListener(this, ChangeID.toFilter(ChangeID.DELETE_ITEM, ChangeID.CREATE_ITEM));
			List<Item> itemByType;
			while (true) {
				try {
					itemByType = it.getItems();
					break;
				} catch (Throwable e) {
					try {
						Thread.sleep(20);
					} catch (InterruptedException e1) {
					}
				}
			}
			for (Item item : itemByType) {
				if (item.getType() != it) {
					continue;
				}
				ret.add(createItemNode(rootWS, item));
			}
		}
		return ret.toArray(new ItemNode[ret.size()]);
	}

	public LinkNode createLinkNode(AbstractCadseViewNode parent, Link l) {
		return parent.getOrCreateNode(l);
	}

	public LinkNode createLinkIncomingNode(AbstractCadseViewNode parent, Link l) {
		return parent.getOrCreateIncomingNode(l);
	}

	public ItemNode createItemNode(AbstractCadseViewNode parent, Item item) {
		if (parent == null) {
			parent = rootWS;
		}
		return parent.getOrCreateNode(item);
	}

	public boolean hasChildren(AbstractCadseViewNode node) {
		return node.getChildren().length != 0;
	}

	public boolean isRecomputeChildren() {
		return _isRecomputeChildren;
	}

	public void setRecomputeChildren(boolean recomputeChildren) {
		_isRecomputeChildren = recomputeChildren;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.eclipse.view.IViewDisplayConfiguration#getDisplayToolTip(fr.imag.adele.cadse.core.LinkType)
	 */
	public String getDisplayToolTip(LinkType linkType) {
		return "LinkType: " + linkType.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.eclipse.view.IViewDisplayConfiguration#getDisplayImage(fede.workspace.tool.view.node.LinkTypeNode)
	 */
	public Image getDisplayImage(LinkTypeNode node) {

		Image ret = WSPlugin.getDefault().getImageFrom(node.getLinkType().getDestination(), null);
		if (ret != null) {
			return ret;
		}

		String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.eclipse.view.IViewDisplayConfiguration#getDisplayText(fede.workspace.tool.view.node.LinkTypeNode)
	 */
	public String getDisplayText(LinkTypeNode node) {
		return node.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.eclipse.view.IViewDisplayConfiguration#getDisplayFont(fede.workspace.tool.view.node.LinkTypeNode)
	 */
	public Font getDisplayFont(LinkTypeNode node) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.eclipse.view.IViewDisplayConfiguration#getDisplayToolTip(fr.imag.adele.cadse.core.ItemType)
	 */
	public String getDisplayToolTip(ItemType itemType) {
		return "ItemType:" + itemType.getId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.eclipse.view.IViewDisplayConfiguration#getDisplayImage(fede.workspace.tool.view.node.ItemTypeNode)
	 */
	public Image getDisplayImage(ItemTypeNode node) {
		Image ret = WSPlugin.getDefault().getImageFrom(node.getItemType(), null);
		if (ret != null) {
			return ret;
		}

		String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.eclipse.view.IViewDisplayConfiguration#getDisplayText(fede.workspace.tool.view.node.ItemTypeNode)
	 */
	public String getDisplayText(ItemTypeNode node) {
		return node.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.eclipse.view.IViewDisplayConfiguration#getDisplayFont(fede.workspace.tool.view.node.ItemTypeNode)
	 */
	public Font getDisplayFont(ItemTypeNode node) {
		return null;
	}

	public IShellProvider getShellProvider() {
		return shellprovider;
	}

	public IWorkbenchWindow getWorkbenchWindow() {
		return workbenchWindow;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.cadse.eclipse.view.IViewDisplayConfiguration#getDisplayToolTip(fede.workspace.tool.view.node.AbstractCadseViewNode)
	 */
	public String getDisplayToolTip(AbstractCadseViewNode node) {
		if (node instanceof LinkNode) {
			return getDisplayToolTip(node.getLink());
		}
		if (node instanceof ItemNode) {
			return getDisplayToolTip(node.getItem());
		}
		if (node instanceof LinkTypeNode) {
			return getDisplayToolTip(node.getLinkType());
		}
		if (node instanceof ItemTypeNode) {
			return getDisplayToolTip(node.getItemType());
		}
		return "";
	}

	public boolean isAggregationLink(AbstractCadseViewNode node) {
		if (node instanceof LinkNode) {
			return isAggregationLink(node.getLink());
		}
		return true;
	}

	public boolean canCreateItem(ItemType it) {
		LogicalWorkspace cadseModel = getCadseModel();
		if (cadseModel == null) {
			return false;
		}
		return !isRefItemType(it, cadseModel);
	}

	public boolean canCreateFrom(Item itemparent, LinkType alt, ItemType anIT) {
		LogicalWorkspace cadseModel = getCadseModel();
		if (cadseModel == null) {
			return false;
		}
		if (!isCreateLink(alt)) {
			return false;
		}
		if (!isItemType(anIT, cadseModel)) {
			return false;
		}
		return true;
	}

	public boolean canCreateFrom(Item itemparent, LinkType alt) {
		LogicalWorkspace cadseModel = getCadseModel();
		if (cadseModel == null) {
			return false;
		}
		if (!isCreateLink(alt)) {
			return false;
		}
		if (!isItemType(alt.getDestination(), cadseModel)) {
			return false;
		}
		return true;
	}

	public boolean canCreateLinkFrom(Item parentitem, LinkType alt) {
		return true;
	}

	// private HashMap<Object, Object> elements;

	public void add(AbstractCadseViewNode node) {
		// Object element = node.getElementModel();
		// if (element == null) {
		// throw new NullPointerException();
		// }
		//
		// if (elements == null) {
		// elements = new HashMap<Object, Object>();
		// }
		// Object v = elements.get(element);
		//
		// if (v == null) {
		// elements.put(element, node);
		// } else {
		// if (v instanceof List) {
		// ((List) v).add(node);
		// } else {
		// ArrayList nv = new ArrayList();
		// nv.add(v);
		// nv.add(node);
		// elements.put(element, nv);
		// }
		// }
	}

	public void remove(AbstractCadseViewNode node) {
		// if (elements == null) {
		// return;
		// }
		//
		// Object element = node.getElementModel();
		// if (element == null) {
		// return;
		// }
		//
		// Object v = elements.get(element);
		//
		// if (v == null) {
		//
		// } else {
		// if (v instanceof List) {
		// ((List) v).remove(node);
		// if (((List) v).size() == 1) {
		// elements.put(element, ((List) v).get(0));
		// }
		// } else {
		// elements.remove(element);
		// }
		// }
	}

	public List<AbstractCadseViewNode> getNodeFromElement(Object element) {
		ArrayList<AbstractCadseViewNode> ret = new ArrayList<AbstractCadseViewNode>();
		ArrayList<AbstractCadseViewNode> visite = new ArrayList<AbstractCadseViewNode>();
		visite.add(rootWS);

		while (!visite.isEmpty()) {
			AbstractCadseViewNode node = visite.remove(visite.size() - 1);

			if (node.getElementModel() == element) {
				ret.add(node);
			}

			if (node.isOpen()) {
				List<AbstractCadseViewNode> children = node.children();
				if (children != null) {
					visite.addAll(children);
				}
			}
		}

		return ret;
	}

	public List<AbstractCadseViewNode> getNodeFromElement(Item item) {
		ArrayList<AbstractCadseViewNode> ret = new ArrayList<AbstractCadseViewNode>();
		ret.addAll(getNodeFromElement((Object) item));
		for (Link il : item.getIncomingLinks()) {
			if (isLink(il)) {
				ret.addAll(getNodeFromElement(il));
			}
		}
		return ret;
	}

	public List<Object> getParentElement(Object element) {
		LogicalWorkspace cadseModel = getCadseModel();
		if (cadseModel == null) {
			return Collections.EMPTY_LIST;
		}
		if (element instanceof Link) {
			Link l = (Link) element;
			ArrayList<Object> ret = new ArrayList<Object>();
			if (isFirstItem(l.getSource(), cadseModel)) {
				ret.add(l.getSource());
			}
			for (Link il : l.getSource().getIncomingLinks()) {
				if (isLink(il)) {
					ret.add(il);
				}
			}
			return ret;
		}
		if (element instanceof Item) {
			ArrayList<Object> ret = new ArrayList<Object>();
			Item source = (Item) element;
			for (Link il : source.getIncomingLinks()) {
				if (isLink(il)) {
					ret.add(il);
				}
			}
			if (isFirstItem(source, cadseModel)) {
				ret.add(rootWS);
			}

			return ret;
		}
		return Collections.EMPTY_LIST;
	}

	public Set<IItemNode> getParentNode(Object element) {
		List<AbstractCadseViewNode> nodeOfElement = getNodeFromElement(element);

		Set<IItemNode> ret = new HashSet<IItemNode>();
		for (AbstractCadseViewNode el : nodeOfElement) {
			ret.add(el);
			if (el.getParent() != null) {
				ret.add(el.getParent());
			}
		}
		List<Object> parentElement = getParentElement(element);
		for (Object el : parentElement) {
			ret.addAll(getNodeFromElement(el));
		}
		return ret;
	}

	AbstractCadseView	viewPart;

	public AbstractCadseView getViewPart() {
		return viewPart;
	}

	public void setViewPart(AbstractCadseView viewPart) {
		this.viewPart = viewPart;
	}

	public int isSelected(IItemNode node) {
		return IItemNode.DESELECTED;
	}

	ViewFilter[]	_filters	= null;

	final public void addFilter(ViewFilter f) {
		_filters = ArraysUtil.add(ViewFilter.class, _filters, f);
	}

	final public ViewFilter[] getFilters() {
		return _filters;
	}

	final public void removeFilter(ViewFilter f) {
		_filters = ArraysUtil.remove(ViewFilter.class, _filters, f);
	}

	public ItemType[] getFirstItem() {
		return getFirstItemType(getCadseModel());
	}

	public Object getWindowProvider() {
		return getShellProvider();
	}

}