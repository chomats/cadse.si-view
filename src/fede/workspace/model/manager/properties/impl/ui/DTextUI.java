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
package fede.workspace.model.manager.properties.impl.ui;

import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalListener;
import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;

import fede.workspace.model.manager.properties.IFieldContenProposalProvider;
import fede.workspace.model.manager.properties.Proposal;
import fr.imag.adele.cadse.core.CadseRootCST;
import fr.imag.adele.cadse.core.CompactUUID;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.attribute.IAttributeType;
import fr.imag.adele.cadse.core.ui.EPosLabel;
import fr.imag.adele.cadse.core.ui.IFedeFormToolkit;
import fr.imag.adele.cadse.core.ui.IInteractionController;
import fr.imag.adele.cadse.core.ui.IModelController;
import fr.imag.adele.cadse.core.ui.IPageController;
import fr.imag.adele.cadse.core.util.Convert;

public class DTextUI extends DAbstractField implements IContentProposalListener {

	private ContentAssistCommandAdapter	_contentAssistField;
	private String						_currentValue;
	private String						_currentValueToSend;
	private Text						_textControl;
	private String						_toolTips			= null;
	private int							_vspan				= 1;
	private boolean						_sendNotification	= true;

	public DTextUI(CompactUUID id, String shortName) {
		super(id, shortName);
	}

	public DTextUI(String key, String label, EPosLabel poslabel, IModelController mc, IInteractionController ic) {
		super(key, label, poslabel, mc, ic);
	}

	public DTextUI(String key, String label, EPosLabel poslabel, IModelController mc, IInteractionController ic,
			int style, int vspan, String tooltip) {
		this(key, label, poslabel, mc, ic, vspan, tooltip);
	}

	public DTextUI(String key, String label, EPosLabel poslabel, IModelController mc, IInteractionController ic,
			int vspan, String tooltip) {
		super(key, label, poslabel, mc, ic);

		if (vspan != 0) {
			this._vspan = vspan;
		}
		this._toolTips = tooltip;
	}

	public DTextUI(String key, String label, EPosLabel poslabel, IModelController mc, IInteractionController ic,
			int vspan, String tooltip, boolean multiLine, boolean noBorder, boolean wrapLine) {
		super(key, label, poslabel, mc, ic);

		this._vspan = vspan;
		if (this._vspan == 0) {
			this._vspan = 1;
		}
		this._toolTips = tooltip;
		setFlagMultiLine(multiLine);
		setFlagNoBorder(noBorder);
		setFlagWrapLine(wrapLine);
	}

	public String __getVisualValue() {
		return (_textControl).getText();
	}

	@Override
	public boolean commitSetAttribute(IAttributeType<?> type, String key, Object value) {
		if (key.equals(CadseRootCST.DTEXT_at_MULTI_LINE)) {
			boolean ret = Convert.toBoolean(value, CadseRootCST.DTEXT_at_MULTI_LINE_, false);
			if (ret != getFlag(Item.UI_TEXT_MULTI_LINE)) {
				setFlagMultiLine(ret);
				return true;
			}
			return false;
		}
		if (key.equals(CadseRootCST.DTEXT_at_NO_BORDER)) {
			boolean ret = Convert.toBoolean(value, CadseRootCST.DTEXT_at_NO_BORDER_, false);
			if (ret != getFlag(Item.UI_NO_BORDER)) {
				setFlagNoBorder(ret);
				return true;
			}
			return false;
		}
		if (key.equals(CadseRootCST.DTEXT_at_WRAP_LINE)) {
			boolean ret = Convert.toBoolean(value, CadseRootCST.DTEXT_at_WRAP_LINE_, false);
			if (ret != getFlag(Item.UI_TEXT_WRAP_LINE)) {
				setFlagWrapLine(ret);
				return true;
			}
			return false;
		}
		if (key.equals(CadseRootCST.DTEXT_at_TOOL_TIP)) {
			String ret = Convert.toString(value, CadseRootCST.DTEXT_at_TOOL_TIP_, "");
			if (!Convert.equals(ret, _toolTips)) {
				_toolTips = ret;
				return true;
			}
			return false;
		}
		if (key.equals(CadseRootCST.DTEXT_at_VERTICAL_SPAN)) {
			int ret = Convert.toInt(value, CadseRootCST.DTEXT_at_VERTICAL_SPAN_, 1);
			if (ret != this._vspan) {
				this._vspan = ret;
				return true;
			}
			return false;
		}
		return super.commitSetAttribute(type, key, value);
	}

	@Override
	public Object createControl(final IPageController globalUIController, final IFedeFormToolkit toolkit,
			Object ocontainer, int hspan) {

		final IFieldContenProposalProvider proposer = getContentAssistant();
		// Container ocontainer;
		int style = 0;
		style |= (getFlag(Item.UI_TEXT_MULTI_LINE)) ? SWT.MULTI : SWT.SINGLE;
		style |= (getFlag(Item.UI_TEXT_WRAP_LINE)) ? SWT.WRAP : 0;
		style |= (getFlag(Item.UI_NO_BORDER)) ? 0 : SWT.BORDER;
		style |= (getFlag(Item.UI_HSCROLL)) ? 0 : SWT.H_SCROLL;
		style |= (getFlag(Item.UI_VSCROLL)) ? 0 : SWT.V_SCROLL;
		if (!isEditable()) {
			style |= SWT.READ_ONLY;
		}

		_textControl = (Text) toolkit.createText(ocontainer, "", style);
		_textControl.setData(CADSE_MODEL_KEY, this);
		if (isEditable() && proposer != null) {
			IControlContentAdapter contentAdapter = new ProposerTextContentAdapter(this, globalUIController, proposer);
			_contentAssistField = new ContentAssistCommandAdapter(_textControl, contentAdapter, proposer
					.getContentProposalProvider(), proposer.getCommandId(), proposer.getAutoActivationCharacters(),
					true);
			_contentAssistField.setProposalAcceptanceStyle(proposer.getProposalAcceptanceStyle());
			_contentAssistField.addContentProposalListener(this);
		}

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = hspan;
		gd.verticalSpan = this._vspan;
		if (gd.verticalSpan != 1) {
			gd.verticalAlignment = GridData.FILL;
			gd.grabExcessVerticalSpace = true;
		}
		_textControl.setLayoutData(gd);

		_textControl.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (!_sendNotification) {
					return;
				}
				setTextModified();
			}
		});

		_textControl.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				if (e.character == '\u001b') { // Escape character
					cancelEditor();
				} else if (e.character == '\n' || e.character == '\r') {
					sendModificationIfNeed(_currentValueToSend, true);
				}
			}

			public void keyReleased(KeyEvent e) {
			}

		});

		_textControl.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
			}

			public void focusLost(FocusEvent e) {
				sendModificationIfNeed(_currentValueToSend, true);
			}
		});

		// textControl.setEnabled(getLocal(IFieldDescription.ENABLE,true));
		// textControl.setEditable(getLocal(IFieldDescription.EDITABLE,true));

		if (_toolTips != null) {
			_textControl.setToolTipText(_toolTips);
		}

		return ocontainer;
	}

	protected void cancelEditor() {
		_sendNotification = false;
		_textControl.setText(_currentValue);
		_currentValueToSend = _currentValue;
		_textControl.setSelection(_currentValue.length(), _currentValue.length());
		_sendNotification = true;
	}

	@Override
	public void dispose() {
		super.dispose();
		_textControl = null;
		_contentAssistField = null;
		_currentValue = null;
		_currentValueToSend = null;
	}

	protected IFieldContenProposalProvider getContentAssistant() {
		IInteractionController uc = getInteractionController();
		if (uc instanceof IFieldContenProposalProvider) {
			return (IFieldContenProposalProvider) uc;
		}
		return null;
	}

	@Override
	public Control getMainControl() {
		return _textControl;
	}

	@Override
	public Object[] getSelectedObjects() {
		return new Object[] { this._currentValue };
	}

	public ItemType getType() {
		return CadseRootCST.DTEXT;
	}

	@Override
	public Object getUIObject(int index) {
		return _textControl;
	}

	@Override
	public Object getVisualValue() {
		if (_currentValueToSend == null) {
			_currentValueToSend = __getVisualValue();
		}
		return _currentValueToSend;
	}

	@Override
	public int getVSpan() {
		return _vspan;
	}

	@Override
	public <T> T internalGetOwnerAttribute(IAttributeType<T> type) {
		if (CadseRootCST.DTEXT_at_MULTI_LINE_ == type) {
			return (T) Boolean.valueOf(getFlag(Item.UI_TEXT_MULTI_LINE));
		}
		if (CadseRootCST.DTEXT_at_NO_BORDER_ == type) {
			return (T) Boolean.valueOf(getFlag(Item.UI_NO_BORDER));
		}
		if (CadseRootCST.DTEXT_at_WRAP_LINE_ == type) {
			return (T) Boolean.valueOf(getFlag(Item.UI_TEXT_WRAP_LINE));
		}
		if (CadseRootCST.DTEXT_at_TOOL_TIP_ == type) {
			return (T) this._toolTips;
		}
		if (CadseRootCST.DTEXT_at_VERTICAL_SPAN_ == type) {
			return (T) Integer.valueOf(this._vspan);
		}

		return super.internalGetOwnerAttribute(type);
	}

	@Override
	public void internalSetEditable(boolean v) {
		this._textControl.setEditable(v);
		if (this._labelWidget != null) {
			this._labelWidget.setEnabled(v);
		}
	}

	@Override
	public void internalSetVisible(boolean v) {
		super.internalSetVisible(v);
		this._textControl.setVisible(v);
	}

	@Override
	public boolean isDisposed() {
		return this._textControl == null || this._textControl.isDisposed();
	}

	protected synchronized void sendModificationIfNeed(String value, boolean send) {
		if (!isEditable()) {
			return;
		}
		if (send) {
			// true, if error
			if (!globalController.broadcastValueChanged(DTextUI.this, value)) {
				_currentValue = value;
			}
		} else {
			globalController.setMessage(null, IPageController.ERROR);

			// validate value and if it's ok, test other fields
			// true if error
			if (!validateValueChanged(value)) {
				getPages().validateFields(this, getPage());
			}
		}
	}

	@Override
	public void setEnabled(boolean v) {
		super.setEnabled(v);
		this._textControl.setEnabled(v);
	}

	public void setFlagMultiLine(boolean f) {
		setFlag(UI_TEXT_MULTI_LINE, f);
	}

	public void setFlagNoBorder(boolean f) {
		setFlag(UI_NO_BORDER, f);
	}

	public void setFlagWrapLine(boolean f) {
		setFlag(UI_TEXT_WRAP_LINE, f);
	}

	public void setFlagHScroll(boolean f) {
		setFlag(UI_HSCROLL, f);
	}

	public void setFlagVScroll(boolean f) {
		setFlag(UI_VSCROLL, f);
	}

	protected synchronized void setTextModified() {
		_currentValueToSend = __getVisualValue();
		sendModificationIfNeed(_currentValueToSend, false);
	}

	public void setVisualValue(Object visualValue, boolean sendNotification) {
		if (!isRunning()) {
			return;
		}
		if (visualValue == null) {
			visualValue = "";
		}
		if (visualValue.equals(_currentValueToSend)) {
			return; // to do nothing;
		}

		_sendNotification = sendNotification;
		try {
			_currentValueToSend = _currentValue = visualValue.toString();
			_textControl.setText(_currentValue);
			// _textControl.setSelection(_currentValue.length(),
			// _currentValue.length());
		} finally {
			_sendNotification = true;
		}
	}

	public void proposalAccepted(IContentProposal proposal) {
		IFieldContenProposalProvider proposer = getContentAssistant();

		if (proposer != null) {
			if (((Proposal) proposal).getValue() == null) {
				return;
			}
			if (proposer.getProposalAcceptanceStyle() == ContentProposalAdapter.PROPOSAL_INSERT) {
				return;
			}

			Object newValue = proposer.getValueFromProposal((Proposal) proposal);
			// setVisualValue(newValue);
			if (newValue != null) {
				_currentValue = newValue.toString();
				globalController.broadcastValueChanged(this, newValue);

			} else {
				globalController.broadcastValueDeleted(this, _currentValue);
				_currentValue = null;
			}

		}

	}

}
