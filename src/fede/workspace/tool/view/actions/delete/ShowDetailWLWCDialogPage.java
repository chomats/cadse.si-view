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
package fede.workspace.tool.view.actions.delete;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;
import java.util.TreeSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import fede.workspace.model.manager.properties.impl.ic.IC_ContextMenu;
import fede.workspace.model.manager.properties.impl.ic.IC_TreeModel;
import fede.workspace.model.manager.properties.impl.ui.DGridUI;
import fede.workspace.model.manager.properties.impl.ui.DSashFormUI;
import fede.workspace.model.manager.properties.impl.ui.DTextUI;
import fede.workspace.model.manager.properties.impl.ui.DTreeModelUI;
import fede.workspace.model.manager.properties.impl.ui.DetailWizardDialog;
import fede.workspace.model.manager.properties.impl.ui.WizardController;
import fede.workspace.tool.view.node.AbstractCadseViewNode;
import fede.workspace.tool.view.node.CadseViewModelController;
import fede.workspace.tool.view.node.FilterItem;
import fede.workspace.tool.view.node.FilteredItemNode;
import fede.workspace.tool.view.node.FilteredItemNodeModel;
import fede.workspace.tool.view.node.ItemNode;
import fede.workspace.tool.view.node.ItemsRule;
import fede.workspace.tool.view.node.LinkNode;
import fede.workspace.tool.view.node.Rule;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.delta.DeleteOperation;
import fr.imag.adele.cadse.core.delta.ItemDelta;
import fr.imag.adele.cadse.core.delta.LinkDelta;
import fr.imag.adele.cadse.core.delta.SetAttributeOperation;
import fr.imag.adele.cadse.core.impl.internal.LogicalWorkspaceTransactionImpl;
import fr.imag.adele.cadse.core.impl.internal.ui.PagesImpl;
import fr.imag.adele.cadse.core.impl.ui.AbstractActionPage;
import fr.imag.adele.cadse.core.impl.ui.AbstractModelController;
import fr.imag.adele.cadse.core.impl.ui.MC_AttributesItem;
import fr.imag.adele.cadse.core.impl.ui.PageImpl;
import fr.imag.adele.cadse.core.transaction.LogicalWorkspaceTransaction;
import fr.imag.adele.cadse.core.ui.EPosLabel;
import fr.imag.adele.cadse.core.ui.IActionPage;
import fr.imag.adele.cadse.core.ui.Pages;
import fr.imag.adele.cadse.core.ui.UIField;

/**
 * Dialog used for asking delete of items to commit. Informations provided by
 * the user are : - list of items to commit - message for this commit operation
 * 
 * Informations gived by the dialog : - errors which forbid to commit.
 * 
 * @author Thomas
 * 
 */
public class ShowDetailWLWCDialogPage extends PageImpl {
	/*
	 * UI fields.
	 */
	protected Button							_addItemField;

	protected Button							_addAllItemsField;

	protected Button							_deselectAllItemsField;

	protected DSashFormUI						_selectSashField;

	protected DSashFormUI						_rootSashField;

	protected DTreeModelUI						_treeField;

	/**
	 * Status and definition of commit operation.
	 */
	protected final LogicalWorkspaceTransaction	_transaction;

	/*
	 * State of this dialog.
	 */
	protected ItemDelta							_currentItemOperation	= null;

	protected HashSet<Item>						_itemsToShow			= new HashSet<Item>();
	protected HashSet<Item>						_rootElements			= new HashSet<Item>();
	protected HashSet<Item>						_itemsToOpen			= new HashSet<Item>();

	private DTreeModelUI						_treeFieldInItem;

	private DTextUI								_textField;

	static private class SetAttNode extends AbstractCadseViewNode {

		protected SetAttNode(CadseViewModelController ctl, AbstractCadseViewNode parent,
				SetAttributeOperation attributeOperation) {
			super(ctl, OTHER, parent);
			_attributeOperation = attributeOperation;
		}

		SetAttributeOperation	_attributeOperation;

		@Override
		public Object getElementModel() {
			return _attributeOperation;
		}

		@Override
		public Item getItem() {
			return null;
		}

		@Override
		public Link getLink() {
			return null;
		}

		@Override
		public LinkType getLinkType() {
			return null;
		}

		@Override
		public String toString() {
			return _attributeOperation.toString();
		}

	}

	public class LinkOperationFromCurrentItemOperation extends Rule {
		Comparator<LinkDelta>	sortFct	= null;

		public LinkOperationFromCurrentItemOperation(Comparator<LinkDelta> sortFct) {
			super();
			this.sortFct = sortFct;
		}

		public LinkOperationFromCurrentItemOperation() {
			super();
			this.sortFct = null;
		}

		@Override
		public void computeChildren(FilteredItemNode root, AbstractCadseViewNode node, List<AbstractCadseViewNode> ret) {
			if (_currentItemOperation == null) {
				return;
			}
			Collection<LinkDelta> values = _currentItemOperation.getOutgoingLinkOperations();

			if (sortFct != null) {
				TreeSet<LinkDelta> values2 = new TreeSet<LinkDelta>(sortFct);
				values2.addAll(values);
				values = values2;
			}
			for (LinkDelta valueItem : values) {
				if (valueItem.isModified()) {
					ret.add(new LinkNode(root, node, valueItem) {
						@Override
						public String toString() {
							return "Link " + getLinkType().getDisplayName();
						}
					});
				}
			}
			Collection<SetAttributeOperation> attOperation = _currentItemOperation.getSetAttributeOperation();
			if (attOperation != null) {
				for (SetAttributeOperation setAttributeOperation : attOperation) {
					if (setAttributeOperation.isModified()) {
						ret.add(new SetAttNode(root, node, setAttributeOperation));
					}
				}
			}
		}
	}

	public class ItemOperationFromCurrentItemOperation extends Rule {
		Comparator<LinkDelta>	sortFct	= null;

		public ItemOperationFromCurrentItemOperation(Comparator<LinkDelta> sortFct) {
			super();
			this.sortFct = sortFct;
		}

		public ItemOperationFromCurrentItemOperation() {
			super();
			this.sortFct = null;
		}

		@Override
		public void computeChildren(FilteredItemNode root, AbstractCadseViewNode node, List<AbstractCadseViewNode> ret) {
			ItemDelta currentOperation = (ItemDelta) node.getItem();
			if (currentOperation == null) {
				return;
			}

			Collection<LinkDelta> values = currentOperation.getOutgoingLinkOperations();

			if (sortFct != null) {
				TreeSet<LinkDelta> values2 = new TreeSet<LinkDelta>(sortFct);
				values2.addAll(values);
				values = values2;
			}

			ONE: for (LinkDelta valueItem : values) {
				final ItemDelta dest = valueItem.getDestination();
				// allready show
				IItemNode n = node;
				while (n != null) {
					if (n.getElementModel() == dest) {
						continue ONE;
					}
					n = n.getParent();
				}
				if (dest != null && (dest.isModified() || _itemsToShow.contains(dest))) {
					ret.add(new ItemNode(root, node, dest));
				}
			}
		}
	}

	static public class DestinationFromLinkOperation extends Rule {

		public DestinationFromLinkOperation() {
			super();
		}

		@Override
		public void computeChildren(FilteredItemNode root, AbstractCadseViewNode node, List<AbstractCadseViewNode> ret) {
			Link currentOperation = node.getLink();
			if (currentOperation == null) {
				return;
			}
			ret.add(new ItemNode(root, node, currentOperation.getDestination()));
		}
	}

	static public class ItemsFromWorkspaceCopy extends Rule {
		Comparator<ItemDelta>			sortFct	= null;
		boolean							modifiedOnly;
		LogicalWorkspaceTransactionImpl	copy;
		FilterItem						filter;

		public ItemsFromWorkspaceCopy(LogicalWorkspaceTransactionImpl copy, Comparator<ItemDelta> sortFct,
				boolean modifiedOnly, FilterItem filter) {
			super();
			this.modifiedOnly = modifiedOnly;
			this.sortFct = sortFct;
			this.filter = filter;
			this.copy = copy;
		}

		@Override
		public void computeChildren(FilteredItemNode root, AbstractCadseViewNode node, List<AbstractCadseViewNode> ret) {
			Collection<ItemDelta> values = copy.getItemOperations();
			if (sortFct != null) {
				TreeSet<ItemDelta> values2 = new TreeSet<ItemDelta>(sortFct);
				values2.addAll(values);
				values = values2;
			}
			for (ItemDelta valueItem : values) {
				if (modifiedOnly && !valueItem.isModified()) {
					continue;
				}

				if (filter == null || filter.accept(valueItem)) {
					ret.add(new ItemNode(root, node, valueItem));
				}
			}
		}
	}

	/*
	 * Classes for Model controllers and Interaction controllers.
	 */

	public class MyMC_AttributesItem extends MC_AttributesItem {

		@Override
		public Item getItem() {
			return _currentItemOperation;
		}

		@Override
		public Object getValue() {
			if (getItem() == null) {
				return "";
			}
			Object _ret = super.getValue();
			if (_ret == null) {
				return "";
			}
			return _ret;
		}

		@Override
		public void notifieValueChanged(UIField field, Object value) {
			// read only value
		}
	}

	public class CommitActionPage extends AbstractActionPage {

		@Override
		public void doFinish(Object monitor) throws Exception {
			_transaction.commit();
		}
	}

	/**
	 * The Class CanCreationLinkAction.
	 */
	static public class DoNotDeleteContent extends Action {

		/** The linktype. */
		private List<ItemDelta>	items;

		/**
		 * Instantiates a new can creation link action.
		 * 
		 * @param ic
		 *            the ic
		 * @param itemsel
		 *            the itemsel
		 * @param viewlinktype
		 *            the viewlinktype
		 * @param view
		 *            the view
		 */
		public DoNotDeleteContent(List<ItemDelta> items) {
			super("do not delete content");
			this.items = items;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.action.Action#run()
		 */
		@Override
		public void run() {
			for (ItemDelta i : items) {
				i.getDeleteOperation().setDeleteContent(false);
			}
		}
	}

	/**
	 * The Class CanCreationLinkAction.
	 */
	static public class DoDeleteContent extends Action {

		/** The linktype. */
		private List<ItemDelta>	items;

		/**
		 * Instantiates a new can creation link action.
		 * 
		 * @param ic
		 *            the ic
		 * @param itemsel
		 *            the itemsel
		 * @param viewlinktype
		 *            the viewlinktype
		 * @param view
		 *            the view
		 */
		public DoDeleteContent(List<ItemDelta> items) {
			super("do delete content");
			this.items = items;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.action.Action#run()
		 */
		@Override
		public void run() {
			for (ItemDelta i : items) {
				i.getDeleteOperation().setDeleteContent(true);
			}
		}
	}

	/**
	 * The Class CanCreationLinkAction.
	 */
	static public class DoNotDeleteMapping extends Action {

		/** The linktype. */
		private List<ItemDelta>	items;

		/**
		 * Instantiates a new can creation link action.
		 * 
		 * @param ic
		 *            the ic
		 * @param itemsel
		 *            the itemsel
		 * @param viewlinktype
		 *            the viewlinktype
		 * @param view
		 *            the view
		 */
		public DoNotDeleteMapping(List<ItemDelta> items) {
			super("do not delete physical content");
			this.items = items;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.action.Action#run()
		 */
		@Override
		public void run() {
			for (ItemDelta i : items) {
				i.getDeleteOperation().setDeleteMapping(false);
			}
		}
	}

	/**
	 * The Class CanCreationLinkAction.
	 */
	static public class DoDeleteMapping extends Action {

		/** The linktype. */
		private List<ItemDelta>	items;

		/**
		 * Instantiates a new can creation link action.
		 * 
		 * @param ic
		 *            the ic
		 * @param itemsel
		 *            the itemsel
		 * @param viewlinktype
		 *            the viewlinktype
		 * @param view
		 *            the view
		 */
		public DoDeleteMapping(List<ItemDelta> items) {
			super("do delete physical content");
			this.items = items;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.action.Action#run()
		 */
		@Override
		public void run() {
			for (ItemDelta i : items) {
				i.getDeleteOperation().setDeleteMapping(true);
			}
		}
	}

	public class ModifiedItemTreeIC extends IC_TreeModel implements IC_ContextMenu {

		@Override
		public ItemType getType() {
			return null;
		}

		/**
		 * Create the structured model to show.
		 */
		@Override
		protected FilteredItemNodeModel getTreeModel() {
			if (model == null) {
				model = new FilteredItemNodeModel();

				// roots are root elements which are root items
				model.addRule(FilteredItemNodeModel.ROOT_ENTRY, new ItemsRule(null, _rootElements));

				// children are all destinations items
				// model.addRule(CadseGCST.ITEM, new
				// LinkTypeCategoryRule());
				model.addRule(CadseGCST.ITEM, new ItemOperationFromCurrentItemOperation());
			}
			return super.getTreeModel();
		}

		@Override
		public ILabelProvider getLabelProvider() {
			WCLabelDecorator wsl = new WCLabelDecorator();
			return new DecoratingLabelProvider(wsl, wsl);
		}

		public boolean hasRemoveAllWhenShown() {
			return true;
		}

		@Override
		public void select(Object data) {
			if (data instanceof ItemNode && ((ItemNode) data).getItem() != null) {
				_currentItemOperation = (ItemDelta) ((ItemNode) data).getItem();
				((ModifiedInItemTreeIC) _treeFieldInItem.getInteractionController()).refreshAll();
				_treeFieldInItem.setVisualValue(_treeFieldInItem.getVisualValue());
				if (_currentItemOperation != null) {
					_textField.setVisualValue(_currentItemOperation.toString());
				}
			}
		}

		public void menuAboutToHide(Object[] selection, IMenuManager manager) {

		}

		public void menuAboutToShow(Object[] selection, IMenuManager manager) {
			// delete item
			List<ItemDelta> doNotDeleteContent = new ArrayList<ItemDelta>();
			List<ItemDelta> doDeleteContent = new ArrayList<ItemDelta>();
			List<ItemDelta> doNotDeleteMapping = new ArrayList<ItemDelta>();
			List<ItemDelta> doDeleteMapping = new ArrayList<ItemDelta>();
			for (Object o : selection) {
				if (o instanceof ItemDelta) {
					ItemDelta oper = (ItemDelta) o;
					if (oper.isDeleted()) {
						DeleteOperation deleteOperation = oper.getDeleteOperation();
						if (deleteOperation.isDeleteContent()) {
							doNotDeleteContent.add(oper);
						} else {
							doDeleteContent.add(oper);
						}
						if (deleteOperation.isDeleteMapping()) {
							doNotDeleteMapping.add(oper);
						} else {
							doDeleteMapping.add(oper);
						}

					}
				}
			}
			if (doDeleteContent.size() != 0) {
				manager.add(new DoNotDeleteContent(doNotDeleteContent));
			}
			if (doDeleteContent.size() != 0) {
				manager.add(new DoDeleteContent(doNotDeleteContent));
			}

			if (doNotDeleteMapping.size() != 0) {
				manager.add(new DoNotDeleteMapping(doNotDeleteMapping));
			}
			if (doDeleteMapping.size() != 0) {
				manager.add(new DoDeleteMapping(doDeleteMapping));
			}
		}

	}

	public class ModifiedInItemTreeIC extends IC_TreeModel {

		@Override
		public ItemType getType() {
			return null;
		}

		/**
		 * Create the structured model to show.
		 */
		@Override
		protected FilteredItemNodeModel getTreeModel() {
			if (model == null) {
				model = new FilteredItemNodeModel();

				// roots are root elements which are root items
				model.addRule(FilteredItemNodeModel.ROOT_ENTRY, new LinkOperationFromCurrentItemOperation());

			}
			return super.getTreeModel();
		}

		@Override
		public ILabelProvider getLabelProvider() {
			WCLabelDecorator wsl = new WCLabelDecorator();
			return new DecoratingLabelProvider(wsl, wsl);
		}

		@Override
		public void select(Object data) {
			if (data instanceof IItemNode && ((IItemNode) data).getElementModel() != null) {
				_textField.setVisualValue(((IItemNode) data).getElementModel().toString());
			}
		}
	}

	public class MC_CommitTree extends AbstractModelController {

		public Object getValue() {
			return null;
		}

		public void notifieValueChanged(UIField field, Object value) {

		}

		public ItemType getType() {
			return null;
		}

		@Override
		public void notifieSubValueAdded(UIField field, Object added) {

		}

		@Override
		public void notifieSubValueRemoved(UIField field, Object removed) {

		}

	}

	/**
	 * Create the dialog structure... DSashFormUI DSashFormUI DGrillUI
	 * (selection part) _treeField _ DGrillUI (selection dependent part)
	 * _errorsField _modifiedAttrsField _reqNewRevField DGrillUI (selection
	 * independent part) _commentField
	 */
	public ShowDetailWLWCDialogPage(LogicalWorkspaceTransaction copy, String title, String label) {
		super("#showDetailWLWC", title, title, title, false, 3);

		// set manipulated data
		_transaction = copy;
		computeItemsToShow();

		// create all UI fields
		_treeField = createTreeField(false);
		_treeFieldInItem = createTreeFieldInItem(false);
		// _addItemField = createAddItemField();
		// _addAllItemsField = createAddAllItemsField();
		// _deselectAllItemsField = createDeselectAllItemsField();
		_textField = createTextField();
		_textField.setFlagVScroll(true);
		_textField.setEditable(false);
		_textField.setFlag(Item.UI_RUNNING, true);

		// _modifiedAttrsField = createModifiedAttrsField();
		// _reqNewRevField = createReqNewRevField();
		// _commentField = createCommentField();
		//
		MyMC_AttributesItem defaultMc = new MyMC_AttributesItem();
		_rootSashField = new DSashFormUI("#rootSash", label, label == null ? EPosLabel.none : EPosLabel.top, defaultMc,
				null);
		//
		// /*
		// * Selection part
		// */
		_selectSashField = new DSashFormUI("#selectSash", "", EPosLabel.none, defaultMc, null);
		//
		// // create selection part containing a tree
		// DGridUI treeGrild = new DGridUI("#tree", "", EPosLabel.none,
		// defaultMc, null);
		_selectSashField.setWeight(60); // 60% , 40%
		_selectSashField.setHorizontal(false);
		// treeGrild.setChildren(_treeField);
		//
		// // create part with editors for selected node
		DGridUI selectDependentFieldsGrild = new DGridUI("#selectEdit", "", EPosLabel.none, defaultMc, null);
		selectDependentFieldsGrild.setChildren(_textField);
		//
		// _selectDependentFields.add(_errorsField);
		// _selectDependentFields.add(_modifiedAttrsField);
		// _selectDependentFields.add(_reqNewRevField);
		//
		_selectSashField.setChildren(_treeFieldInItem, selectDependentFieldsGrild);
		//
		// /*
		// * Selection independent part
		// */
		//
		// // create part with editors independent of selected node
		// DGridUI selectIndependentFieldsGrid = new DGridUI("#noSelectEdit",
		// "", EPosLabel.none, defaultMc, null);
		// selectIndependentFieldsGrid.setChildren(_commentField);
		//
		_rootSashField.setChildren(_treeField, _selectSashField);
		_rootSashField.setHorizontal(true);
		// // 50%
		// // 50%
		_rootSashField.setWeight(50);

		// add main field
		addLast(_rootSashField);

		// configure the page
		setActionPage(null);

		// add listeners
		registerListener();
	}

	/**
	 * Register listener or validator if need
	 */
	protected void registerListener() {
		// fieldExtends.addValidateContributor(this);
	}

	/**
	 * Create a tree field.
	 */
	public DTreeModelUI createTreeField(boolean checkBox) {
		return new DTreeModelUI("#list", "Items to delete", EPosLabel.none, new MC_CommitTree(),
				new ModifiedItemTreeIC(), checkBox);
	}

	/**
	 * Create a tree field.
	 */
	public DTreeModelUI createTreeFieldInItem(boolean checkBox) {
		return new DTreeModelUI("#list-in", "", EPosLabel.none, new MC_CommitTree(), new ModifiedInItemTreeIC(),
				checkBox);
	}

	/**
	 * Create a text field to display the errors related to selected item.
	 */
	public DTextUI createTextField() {
		return new DTextUI("#textField", "Description", EPosLabel.top, new MyMC_AttributesItem(), null, 10, "", true,
				false, true);
	}

	/**
	 * 
	 * @return
	 */
	private IActionPage getFinishAction() {
		return new CommitActionPage();
	}

	/**
	 * Open Commit Definition dialog.
	 * 
	 * @param commitState
	 *            status and definition of commit operation
	 * @throws CadseException
	 */
	static public void openDialog(final LogicalWorkspaceTransaction copy, final String title, final String label,
			final boolean performFinish) throws CadseException {

		/**
		 * Create a new display wen call getDefault(). Worksbench is not
		 * started. This method is called by federation in start level.
		 * 
		 */
		Display d = PlatformUI.getWorkbench().getDisplay();

		d.syncExec(new Runnable() {
			public void run() {
				try {
					final ShowDetailWLWCDialogPage p = new ShowDetailWLWCDialogPage(copy, title, label);
					final Pages f = new PagesImpl(false, p.getFinishAction(), p);
					WizardController wc = new WizardController(f) {

						@Override
						public boolean hasShowDetail() {
							return false;
						}

						@Override
						public boolean performFinish() {
							if (!performFinish) {
								return true;
							}

							IRunnableWithProgress op = new IRunnableWithProgress() {
								public void run(IProgressMonitor monitor) throws InvocationTargetException,
										InterruptedException {
									try {
										f.doFinish(monitor);
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
								MessageDialog.openError(getShell(), "Error", realException.getMessage());
								return false;
							}

							return true;
						}

						@Override
						public boolean performCancel() {
							super.performCancel();

							copy.rollback();

							return true;
						}
					};
					if (performFinish) {
						WizardDialog wd = new WizardDialog(null, wc);
						wd.setPageSize(800, 500);
						wd.open();
					} else {
						DetailWizardDialog wd = new DetailWizardDialog(null, wc);
						wd.setPageSize(800, 500);
						wd.open();
					}
					// TODO open commit progression dialog
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void initAfterUI() {
		super.initAfterUI();
	}

	@Override
	public ItemType getParentItemType() {
		return CadseGCST.ITEM;
	}

	protected void computeItemsToShow() {
		Collection<ItemDelta> itemsToCommit = _transaction.getItemOperations();
		Collection<ItemDelta> itemsOrphan = new ArrayList<ItemDelta>();

		_itemsToOpen.clear();
		_itemsToShow.clear();
		_rootElements.clear();
		if ((itemsToCommit == null) || itemsToCommit.isEmpty()) {
			return;
		}

		for (ItemDelta item : itemsToCommit) {
			if (!item.isModified()) {
				continue;
			}
			if (item.getType().isRootElement()) {
				_rootElements.add(item);
			}

			if (_itemsToShow.contains(item)) {
				continue;
			}

			_itemsToShow.add(item);

			Stack<ItemDelta> pathToRoot = new Stack<ItemDelta>();
			if (!findPathToRoot(item, pathToRoot)) {
				itemsOrphan.add(item);
				continue;
			}

			// add all items on the path
			ItemDelta curItem = pathToRoot.pop();
			ItemDelta lastModified = null;
			while (curItem != null) {
				if (curItem.isModified()) {
					lastModified = curItem;
				}
				if (curItem != item) {
					_itemsToShow.add(curItem);
					if (curItem.getType().isRootElement()) {
						_rootElements.add(curItem);
					}
				}

				curItem = pathToRoot.isEmpty() ? null : pathToRoot.pop();
			}
			if (lastModified != null) {
				_itemsToOpen.add(lastModified);
			}
		}

		ONE: for (ItemDelta itemOperation : itemsOrphan) {
			// finally try all other links
			List<LinkDelta> incomingLinks = itemOperation.getIncomingLinks(true, true);
			for (LinkDelta link : incomingLinks) {
				ItemDelta source = link.getSource();
				if (itemsOrphan.contains(source) || _itemsToShow.contains(source)) {
					if (link.isPart() || link.isAggregation()) {
						continue ONE;
					}
				}
			}
			_rootElements.add(itemOperation);
		}
	}

	private boolean findPathToRoot(ItemDelta item, Stack<ItemDelta> pathToRoot) {
		pathToRoot.push(item);

		if (item.getType().isRootElement()) {
			return true;
		}

		// first try to navigate to parent
		ItemDelta parentItem = item.getPartParent(false, true);
		if (parentItem != null) {
			if (findPathToRoot(parentItem, pathToRoot)) {
				return true;
			}
		}

		// second try to navigate thought aggregate links
		List<LinkDelta> incomingLinks = item.getIncomingLinks(true, true);
		for (LinkDelta link : incomingLinks) {
			if (link.isPart() || !link.isAggregation()) {
				continue;
			}

			if (findPathToRoot(link.getSource(), pathToRoot)) {
				return true;
			}
		}

		// // finally try all other links
		// for (LinkOperation link : incomingLinks) {
		// if (link.isPart() || link.isAggregation()) {
		// continue;
		// }
		//
		// if (findPathToRoot(link.getSource(), pathToRoot)) {
		// return true;
		// }
		// }

		pathToRoot.pop();

		return false;
	}
}
