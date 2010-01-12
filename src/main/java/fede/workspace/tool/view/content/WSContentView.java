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
package fede.workspace.tool.view.content;

import org.eclipse.core.runtime.Platform;
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

import fede.workspace.tool.view.ItemInViewer;
import fede.workspace.tool.view.ItemInViewerToolTip;
import fede.workspace.tool.view.TreeToolTipListener;
import fede.workspace.tool.view.WSPlugin;
import fede.workspace.tool.view.node.OldItemInViewer;
import fr.imag.adele.cadse.core.ContentItem;
import fr.imag.adele.cadse.core.Item;

/**
 * Cette vue repr?sente les item du workspace courant. Nous avons trois mode
 * d'affichage : - aggr?gations; - relations et link - relations et relations
 * inverse.
 * 
 * Le menu contextuel a une zone particuli?re. "WS-Actions" pour les actions du
 * workspace.
 * 
 */

public class WSContentView extends ViewPart implements ISelectionListener {

	public static final String	WS_MB_ADDITIONS			= "WS-Actions";

	private static final String	KEY_SHOW_LINKTYPE		= WSPlugin.NAMESPACE_ID + ".view.SHOW_LINKTYPE";			//$NON-NLS-1$

	private static final String	KEY_SHOW_TOOLTIP		= WSPlugin.NAMESPACE_ID + ".view.SHOW_TOOLTIP";

	private static final String	KEY_SHOW_KIND			= WSPlugin.NAMESPACE_ID + ".view.KEY_SHOW_KIND";

	private static final String	KEY_SHOW_LINK_TYPE_NAME	= WSPlugin.NAMESPACE_ID + ".view.KEY_SHOW_LINK_TYPE_NAME";

	private DrillDownAdapter	drillDownAdapter;

	private TreeViewer			fTreeViewer;

	private int					_contentProviderFlag	= 0;

	private Action				showLinkType;
	private Action				openItem;
	private Action				showToolTips;
	private Action				showLinkTypeName;
	private Action				showKind;

	protected boolean			_showToolTip			= false;

	private TreeToolTipListener	toolTipListener			= null;

	private boolean				_showKind;

	private boolean				_showLinkTypeName;

	/**
	 * The constructor.
	 */
	public WSContentView() {
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
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

		// gestionnaire d'affichage
		// viewer.setSorter(new NameSorter());
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
				WSContentView.this.fillContextMenu(manager1);
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
	}

	protected boolean getShowKind() {
		return _showKind;
	}

	protected void setShowLinkTypeName(boolean b) {
		_showLinkTypeName = b;
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
		}
	}

	public int getContentProviderFlag() {
		return _contentProviderFlag;
	}

	private void setRoot(ContentItem cm) {
		IViewContentModel vcm = (IViewContentModel) Platform.getAdapterManager()
				.getAdapter(cm, IViewContentModel.class);
		if (vcm == null) {
			return;
		}

		fTreeViewer.setContentProvider(vcm.getContentProvider());
		fTreeViewer.setLabelProvider(vcm.getLabelProvider());
		fTreeViewer.setInput(cm);
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			Object o = ssel.getFirstElement();
			if (o != null) {
				Item item = (Item) Platform.getAdapterManager().getAdapter(o, Item.class);
				if (item != null) {
					if (item.itemHasContent()) {
						ContentItem cm = item.getContentItem();
						if (cm != null) {
							setRoot(cm);
						}
					}
				}
			}
		}
	}

}