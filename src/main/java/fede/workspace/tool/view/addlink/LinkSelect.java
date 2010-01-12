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

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;

import fede.workspace.tool.eclipse.ICreateLinkActionLinkManager;
import fede.workspace.tool.view.node.AbstractCadseViewNode;
import fede.workspace.tool.view.node.ItemTypeNode;
import fede.workspace.tool.view.node.RootNode;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.ILinkTypeManager;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.eclipse.view.IViewLinkManager;
import fr.imag.adele.cadse.eclipse.view.SelfViewContentProvider;
import fr.imag.adele.cadse.eclipse.view.SelfViewLabelProvider;

public class LinkSelect extends Composite implements ISelectionChangedListener, IContributionItem, SelectionListener {

	private ToolBar					toolBar						= null;
	private SashForm				sashForm					= null;
	private Tree					linktree					= null;
	private Tree					itemtree					= null;
	private TreeViewer				linkTreeViewer				= null;
	private TreeViewer				itemTreeViewer				= null;
	private LinkRootNode			linkRootNode;													// @jve:decl-index=0:
	private ItemRootNode			itemRootNode;													// @jve:decl-index=0:
	private Item					itemParent;
	private ToolItem				toolItem;
	private Image					menuImage;
	private Image					disabledMenuImage;
	private MenuManager				menuManager					= null;

	private MenuItem				reposelectedmenu			= null;

	private Item					selectedDest;
	private LinkType				selectedLinkType;

	private ITreeContentProvider	defautlViewContentProvider	= new SelfViewContentProvider();
	private ILabelProvider			defaultViewLabelProvider	= new SelfViewLabelProvider();

	public LinkSelect(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	public void setItemParent(Item itemParent) {
		this.itemParent = itemParent;
		linkRootNode.setParentType(itemParent.getType());
		linkRootNode.setParentItem(getItemParent());
		linkTreeViewer.setInput(linkRootNode);
	}

	public Item getItemParent() {
		return itemParent;
	}

	public RootNode getLinkRoot() {
		return null;
	}

	private void initialize() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		this.setLayout(gridLayout);
		setSize(new Point(600, 300));
		// createToolBar();
		createSashForm();
	}

	// /**
	// * This method initializes toolBar
	// *
	// */
	// private void createToolBar() {
	// GridData gridData1 = new GridData();
	// gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
	// gridData1.grabExcessHorizontalSpace = true;
	// toolBar = new ToolBar(this, SWT.BORDER | SWT.FLAT);
	// toolBar.setLayoutData(gridData1);
	// toolBar.setMenu(showDialogMenu());
	// toolItem = new ToolItem(toolBar, SWT.PUSH);
	//
	// toolBar.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
	//
	// menuImage = ImageDescriptor.createFromFile(PopupDialog.class,
	// "images/popup_menu.gif").createImage();//$NON-NLS-1$
	// disabledMenuImage = ImageDescriptor
	// .createFromFile(PopupDialog.class,
	// "images/popup_menu_disabled.gif").createImage();//$NON-NLS-1$
	//
	// toolItem.setImage(menuImage);
	// toolItem.setDisabledImage(disabledMenuImage);
	// toolItem.setToolTipText(JFaceResources.getString("PopupDialog.menuTooltip"));
	// //$NON-NLS-1$
	// toolItem.addSelectionListener(new SelectionAdapter() {
	// @Override
	// public void widgetSelected(SelectionEvent e) {
	// Menu menu = showDialogMenu();
	// Rectangle bounds = toolBar.getBounds();
	// Point topLeft = new Point(bounds.x, bounds.y + bounds.height);
	// topLeft = getShell().toDisplay(topLeft);
	// menu.setLocation(topLeft.x, topLeft.y);
	// menu.setVisible(true);
	// }
	// });
	// toolItem.addDisposeListener(new DisposeListener() {
	// public void widgetDisposed(DisposeEvent e) {
	// menuImage.dispose();
	// menuImage = null;
	// disabledMenuImage.dispose();
	// disabledMenuImage = null;
	// }
	// });
	// }

	protected Menu showDialogMenu() {

		if (menuManager == null) {
			menuManager = new MenuManager();
			fillDialogMenu(menuManager);
		}

		return menuManager.createContextMenu(getShell());
	}

	private void fillDialogMenu(MenuManager menuManager2) {
		menuManager2.add(this);
	}

	/**
	 * This method initializes sashForm
	 * 
	 */
	private void createSashForm() {
		GridData gridData = new GridData(GridData.FILL_BOTH);
		sashForm = new SashForm(this, SWT.HORIZONTAL);
		sashForm.setLayoutData(gridData);

		linktree = new Tree(sashForm, SWT.SINGLE);
		linkTreeViewer = new TreeViewer(linktree);
		linkTreeViewer.setUseHashlookup(true);

		linkTreeViewer.addSelectionChangedListener(this);
		linkRootNode = new LinkRootNode();
		if (itemParent != null) {
			linkRootNode.setParentType(itemParent.getType());
			linkRootNode.setParentItem(getItemParent());
		}
		linkRootNode.setTreeViewer(linkTreeViewer);

		linkTreeViewer.setContentProvider(defautlViewContentProvider);
		linkTreeViewer.setLabelProvider(defaultViewLabelProvider);
		linkTreeViewer.setInput(linkRootNode);

		itemtree = new Tree(sashForm, SWT.SINGLE);
		itemTreeViewer = new TreeViewer(itemtree);
		itemTreeViewer.setUseHashlookup(true);
		itemTreeViewer.addSelectionChangedListener(this);
		itemRootNode = new ItemRootNode();
		itemRootNode.setTreeViewer(itemTreeViewer);

		itemTreeViewer.setContentProvider(defautlViewContentProvider);
		itemTreeViewer.setLabelProvider(defaultViewLabelProvider);
		itemTreeViewer.setInput(itemRootNode);

		sashForm.setWeights(new int[] { 50, 50 });

	}

	public void selectionChanged(SelectionChangedEvent event) {
		if (event.getSource() == linkTreeViewer) {
			AbstractCadseViewNode node = (AbstractCadseViewNode) ((IStructuredSelection) event.getSelection())
					.getFirstElement();
			if (node != null) {
				if (node.getKind() == AbstractCadseViewNode.LINK_TYPE_OUTGOING) {
					selectLinkType(node);

				} else if (node.getKind() == AbstractCadseViewNode.ITEM_TYPE) {
					selectItemType(node);
				}
			}
		} else if (event.getSource() == itemTreeViewer) {
			Object node = ((IStructuredSelection) event.getSelection()).getFirstElement();
			if (node != null) {
				if (node instanceof IItemNode) {
					selectedDest = ((IItemNode) node).getItem();
				} else if (node instanceof Item) {
					selectedDest = (Item) node;
				} else if (node instanceof Link) {
					selectedDest = ((Link) node).getDestination();
				}
			}
		}
	}

	private void selectLinkType(AbstractCadseViewNode node) {
		selectItemType(node.getChildren()[0]);
	}

	protected void selectItemType(AbstractCadseViewNode node) {
		selectedLinkType = getLinkTypeFromNode(node);

		if (selectedLinkType != null) {
			ILinkTypeManager m = selectedLinkType.getManager();
			if (m instanceof ICreateLinkActionLinkManager) {
				ICreateLinkActionLinkManager wmanager = (ICreateLinkActionLinkManager) m;
				ITreeContentProvider cp = wmanager.getContentProvider();
				if (cp == null) {
					cp = defautlViewContentProvider;
				}
				ILabelProvider lp = wmanager.getLabelProvider();
				if (lp == null) {
					lp = defaultViewLabelProvider;
				}
				itemTreeViewer.setContentProvider(cp);
				itemTreeViewer.setLabelProvider(lp);
				Object specificInput = wmanager.getInputValues(itemParent, itemTreeViewer, itemParent,
						getSelectedLinkType(), ((ItemTypeNode) node).getItemType());

				if (specificInput != null) {
					itemTreeViewer.setInput(specificInput);
					return;
				}
			}
		}
		itemRootNode.setItemTypeNode((ItemTypeNode) node);
		itemRootNode.setItemParent(getItemParent());
		itemRootNode.setSelectedLinkType(getSelectedLinkType());
		itemTreeViewer.setInput(itemRootNode);
	}

	private LinkType getLinkTypeFromNode(AbstractCadseViewNode node) {
		while (node != null) {
			if (node.getKind() == AbstractCadseViewNode.LINK_TYPE_OUTGOING) {
				break;
			}
			node = (AbstractCadseViewNode) node.getParent();
		}
		if (node != null) {
			return node.getLinkType();
		}
		return null;
	}

	public void fill(Composite parent) {
	}

	public void fill(Menu parent, int index) {
	}

	public void fill(ToolBar parent, int index) {
	}

	public void fill(CoolBar parent, int index) {
	}

	public String getId() {
		return null;
	}

	public boolean isDirty() {
		return false;
	}

	public boolean isDynamic() {
		return false;
	}

	public boolean isGroupMarker() {
		return false;
	}

	public boolean isSeparator() {
		return false;
	}

	public void saveWidgetState() {
	}

	public void setParent(IContributionManager parent) {
	}

	public void update(String id) {
	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {

	}

	public LinkType getSelectedLinkType() {
		return selectedLinkType;
	}

	public Item getSelectedDest() {
		return selectedDest;
	}

	public void setLinkViewManager(IViewLinkManager viewLinkManager) {
		linkRootNode.setLinkViewManager(viewLinkManager);
	}

}
