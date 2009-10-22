package fede.workspace.model.manager.properties.impl.ui;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import fede.workspace.model.manager.properties.impl.ic.IC_TreeModel;
import fede.workspace.tool.view.node.FilteredItemNode;
import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.CompactUUID;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.ui.EPosLabel;
import fr.imag.adele.cadse.core.ui.IFedeFormToolkit;
import fr.imag.adele.cadse.core.ui.IInteractionController;
import fr.imag.adele.cadse.core.ui.IModelController;
import fr.imag.adele.cadse.core.ui.IPageController;

public class DTreeModelUI extends DAbstractField implements ISelectionChangedListener, SelectionListener,
		ICheckStateListener, TreeListener {

	private boolean				_useCheckBox	= true;
	private Tree				_treeControl;
	private CheckboxTreeViewer	_treeViewer;
	private FilteredItemNode	_rootNode;

	@Override
	public void dispose() {
		super.dispose();
		_treeControl = null;
		_treeViewer = null;
		_rootNode = null;
	}

	public DTreeModelUI(CompactUUID uuid, String key, String label, EPosLabel poslabel, IModelController mc,
			IC_TreeModel ic, boolean checkBox) {
		super(uuid, key, label, poslabel, mc, ic);
		this._useCheckBox = checkBox;
	}

	@Override
	public IC_TreeModel getInteractionController() {
		return (IC_TreeModel) super.getInteractionController();
	}

	public DTreeModelUI(String key, String label, EPosLabel poslabel, IModelController mc, IInteractionController ic,
			boolean checkBox) {
		super(key, label, poslabel, mc, ic);
		this._useCheckBox = checkBox;
	}

	public DTreeModelUI(CompactUUID uuid, String key) {
		super(uuid, key);
	}

	@Override
	public Object createControl(IPageController globalUIController, IFedeFormToolkit toolkit, Object container,
			int hspan) {

		int style = SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL;
		if (_useCheckBox) {
			style |= SWT.CHECK;
		}
		_treeControl = new Tree((Composite) container, style);
		_treeControl.addSelectionListener(this);
		_treeControl.addTreeListener(this);
		_treeControl.setData(CADSE_MODEL_KEY, this);
		int fillkind = GridData.FILL_BOTH;
		// /if (fillBoth)
		// fillkind = GridData.FILL_BOTH;
		// boolean fillBoth = true;

		GridData gd;
		gd = new GridData(fillkind);
		gd.horizontalSpan = hspan;
		// gd.verticalSpan = 3;
		// gd.minimumHeight = 150;
		// if (!fillBoth) {
		// gd.minimumHeight = 150;
		// // gd.grabExcessVerticalSpace = true;
		// } else
		// gd.grabExcessVerticalSpace = false;
		_treeControl.setLayoutData(gd);

		_treeViewer = new CheckboxTreeViewer(_treeControl) {

			@Override
			protected void doUpdateItem(Item item, Object element) {
				super.doUpdateItem(item, element);
				if (item.isDisposed()) {
					return;
				}

				if (item instanceof TreeItem) {
					final TreeItem treeItem = ((TreeItem) item);
					IItemNode node = (IItemNode) treeItem.getData();
					int s = node.isSelected();
					treeItem.setChecked(s == IItemNode.SELECTED);
					treeItem.setGrayed(s == IItemNode.GRAY_SELECTED);
				}
			}

		};
		_treeViewer.addCheckStateListener(this);
		_treeViewer.setUseHashlookup(true);

		_treeViewer.addSelectionChangedListener(this);
		_rootNode = getInteractionController().getOrCreateFilteredNode();
		_rootNode.setTreeViewer(_treeViewer);

		_treeViewer.setContentProvider(getInteractionController().getContentProvider());
		_treeViewer.setLabelProvider(getInteractionController().getLabelProvider());
		_treeViewer.setInput(_rootNode);

		createContextMenu(_treeControl);
		return container;
	}

	@Override
	public Object getUIObject(int index) {
		return _treeControl;
	}

	@Override
	public Object getVisualValue() {
		return _rootNode;
	}

	public CheckboxTreeViewer getTreeViewer() {
		return _treeViewer;
	}

	public void widgetSelected(SelectionEvent event) {
		if (event.detail == SWT.CHECK) {

			TreeItem item = (TreeItem) event.item;
			Object obj = item.getData();
			if (obj != null) {
				if (item.getChecked()) {
					String error = canObjectSelected(obj);
					if (error == null) {
						objectSelected(obj);
					} else {
						item.setChecked(false);
						globalController.setMessage(error, IPageController.ERROR);
					}
				} else {
					String error = canObjectDeselected(obj);
					if (error == null) {
						objectDeselected(obj);
					} else {
						item.setChecked(true);
						globalController.setMessage(error, IPageController.ERROR);
					}
				}
			}
		}
		TreeItem item = (TreeItem) event.item;
		if (item != null) {
			getInteractionController().select(item.getData());
		}
	}

	/**
	 * 
	 * @param object
	 *            l'object selectionne.
	 * @return Un message d'erreur si impossible or null.
	 */
	protected String canObjectSelected(Object object) {
		return getInteractionController().canObjectSelected(object);
	}

	/**
	 * 
	 * @param object
	 *            l'object deselectionne.
	 * @return Un message d'erreur si impossible or null.
	 */
	protected String canObjectDeselected(Object object) {
		return getInteractionController().canObjectDeselected(object);
	}

	protected void objectSelected(Object added) {
		globalController.broadcastSubValueAdded(this, added);
	}

	protected void objectDeselected(Object removed) {
		globalController.broadcastSubValueRemoved(this, removed);
	}

	@Override
	public void internalSetEditable(boolean v) {
		// TODO Auto-generated method stub

	}

	public void setVisualValue(Object visualValue, boolean sendNotification) {
		if (visualValue instanceof FilteredItemNode) {
			_rootNode = (FilteredItemNode) visualValue;
			_rootNode.setTreeViewer(_treeViewer);
			_treeViewer.setInput(_rootNode);
		}
	}

	public ItemType getType() {
		return CadseGCST.DISPLAY;
	}

	public void selectionChanged(SelectionChangedEvent event) {
		// TODO Auto-generated method stub

	}

	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub

	}

	public void checkStateChanged(CheckStateChangedEvent event) {
		event.getElement();
	}

	@Override
	public Object[] getSelectedObjects() {
		return _treeViewer.getCheckedElements();
	}

	public void treeCollapsed(TreeEvent e) {
		if (e.item.getData() != null) {
			getInteractionController().treeCollapsed(e.item.getData());
		}

	}

	public void treeExpanded(TreeEvent e) {
		// TODO Auto-generated method stub

	}

	public void selectNode(IItemNode n) {
		_treeViewer.setChecked(n, true);
	}

	@Override
	public Control getMainControl() {
		return _treeControl;
	}

	public void setExpandedNodes(IItemNode... n) {
		_treeViewer.setExpandedElements(n);
	}

}
