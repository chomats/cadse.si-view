package fede.workspace.model.manager.properties.impl.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import fr.imag.adele.cadse.core.CompactUUID;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.ui.EPosLabel;
import fr.imag.adele.cadse.core.ui.IFedeFormToolkit;
import fr.imag.adele.cadse.core.ui.IInteractionController;
import fr.imag.adele.cadse.core.ui.IModelController;
import fr.imag.adele.cadse.core.ui.IPageController;
import fr.imag.adele.cadse.core.ui.UIField;

public class DSashFormUI extends DAbstractField {

	UIField[]	children;
	boolean		horizontal	= true;
	SashForm	sashForm;
	int			hone		= 50;

	public DSashFormUI(CompactUUID uuid, String key, String label, EPosLabel poslabel, IModelController mc,
			IInteractionController ic) {
		super(uuid, key, label, poslabel, mc, ic);
	}

	public DSashFormUI(CompactUUID uuid, String key) {
		super(uuid, key);
	}

	public DSashFormUI(String key, String label, EPosLabel poslabel, IModelController mc, IInteractionController ic) {
		super(key, label, poslabel, mc, ic);
	}

	public void setChildren(UIField... children) {
		assert children.length == 2;
		this.children = children;
	}

	@Override
	public boolean hasChildren() {
		return true;
	}

	@Override
	public UIField[] getChildren() {
		return children;
	}

	@Override
	public Object createControl(IPageController globalUIController, IFedeFormToolkit toolkit, Object container,
			int hspan) {
		GridData gridData = new GridData(GridData.FILL_BOTH);
		sashForm = new SashForm((Composite) container, horizontal ? SWT.HORIZONTAL : SWT.VERTICAL);
		sashForm.setLayoutData(gridData);

		GridLayout gridLayout = new GridLayout(hspan, false);
		FieldsController.createFieldsControl(globalUIController, toolkit, sashForm, children, hspan, gridLayout);
		sashForm.setWeights(new int[] { hone, 100 - hone });
		sashForm.setData(CADSE_MODEL_KEY, this);
		return container;
	}

	@Override
	public Object getUIObject(int index) {
		return sashForm;
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

	public void setWeight(int hone) {
		assert hone >= 0 && hone <= 100;
		this.hone = hone;
	}

	public void setHorizontal(boolean horizontal) {
		this.horizontal = horizontal;
	}

	@Override
	public Control getMainControl() {
		return this.sashForm;
	}

	@Override
	public Object[] getSelectedObjects() {
		return null;
	}

}
