package fede.workspace.model.manager.properties.impl.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import fede.workspace.tool.eclipse.FedeFormToolkit;
import fr.imag.adele.cadse.core.CompactUUID;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.ui.EPosLabel;
import fr.imag.adele.cadse.core.ui.IFedeFormToolkit;
import fr.imag.adele.cadse.core.ui.IInteractionController;
import fr.imag.adele.cadse.core.ui.IModelController;
import fr.imag.adele.cadse.core.ui.IPageController;
import fr.imag.adele.cadse.core.ui.UIField;

public class DGridUI extends DAbstractField {

	UIField[]	children;
	Composite	composite;
	int			hspan					= 0;
	boolean		makeColumnsEqualWidth	= false;

	public DGridUI(CompactUUID uuid, String key, IModelController mc, IInteractionController ic) {
		super(uuid, key, "", EPosLabel.none, mc, ic);
	}

	public DGridUI(CompactUUID uuid, String key) {
		super(uuid, key);
	}

	public DGridUI(String key, String label, EPosLabel poslabel, IModelController mc, IInteractionController ic) {
		super(key, label, poslabel, mc, ic);
	}

	@Override
	public Object createControl(IPageController globalUIController, IFedeFormToolkit toolkit, Object container,
			int hspan) {
		GridData gridData = new GridData(GridData.FILL_BOTH);
		int style = SWT.NULL;
		if (!getFlag(Item.UI_NO_BORDER)) {
			style |= SWT.BORDER;
		}
		composite = ((FedeFormToolkit) toolkit).createComposite((Composite) container, style);
		composite.setData(CADSE_MODEL_KEY, this);
		composite.setLayoutData(gridData);
		if (this.hspan == 0) {
			this.hspan = hspan;
		}

		GridLayout gridLayout = new GridLayout(this.hspan, makeColumnsEqualWidth);
		FieldsController.createFieldsControl(globalUIController, toolkit, composite, children, this.hspan, gridLayout);
		return container;
	}

	public void setChildren(UIField... children) {
		this.children = children;
	}

	@Override
	public UIField[] getChildren() {
		return children;
	}

	@Override
	public boolean hasChildren() {
		return true;
	}

	@Override
	public Object getUIObject(int index) {
		return composite;
	}

	@Override
	public Object getVisualValue() {
		return null;
	}

	@Override
	public void internalSetEditable(boolean v) {
	}

	public void setVisualValue(Object visualValue, boolean sendNotification) {
	}

	public ItemType getType() {
		return null;
	}

	public void setHspan(int hspan) {
		this.hspan = hspan;
	}

	public void setMakeColumnsEqualWidth(boolean makeColumnsEqualWidth) {
		this.makeColumnsEqualWidth = makeColumnsEqualWidth;
	}

	@Override
	public Control getMainControl() {
		return this.composite;
	}

	@Override
	public Object[] getSelectedObjects() {
		return null;
	}

}
