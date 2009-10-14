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

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalListener;
import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.jface.fieldassist.IControlCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.fieldassist.ContentAssistField;

import fede.workspace.model.manager.properties.IFieldContenProposalProvider;
import fede.workspace.model.manager.properties.IInteractionControllerForBrowserOrCombo;
import fede.workspace.model.manager.properties.Proposal;
import fede.workspace.tool.view.WSPlugin;
import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.CompactUUID;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.ui.EPosLabel;
import fr.imag.adele.cadse.core.ui.IFedeFormToolkit;
import fr.imag.adele.cadse.core.ui.IInteractionController;
import fr.imag.adele.cadse.core.ui.IModelController;
import fr.imag.adele.cadse.core.ui.IPageController;
import fr.imag.adele.cadse.core.util.Convert;

/**
 * Display a browser field which has a text field and a button "...".
 * 
 * @author chomats
 * 
 */
public class DBrowserUI extends DAbstractField implements IContentProposalListener {
	private Button				_buttonBrowser;
	private ContentAssistField	_contentAssistField;
	private Text				_textControl;
	private Object				_value;

	private String				_currentValueTextToSend;

	private String				_currentValueText;
	private int					_style	= SWT.BORDER | SWT.SINGLE;
	private boolean				_sendNotification;

	public DBrowserUI(CompactUUID uuid, String key) {
		super(uuid, key);
	}

	public DBrowserUI(CompactUUID uuid, String key, String label, EPosLabel poslabel, IModelController mc,
			IInteractionControllerForBrowserOrCombo ic) {
		super(uuid, key, label, poslabel, mc, ic);
	}

	public DBrowserUI(CompactUUID uuid, String key, String label, EPosLabel poslabel, IModelController mc,
			IInteractionControllerForBrowserOrCombo ic, int style) {
		super(uuid, key, label, poslabel, mc, ic);
		if (style != 0) {
			this._style = style;
		}
	}

	public DBrowserUI(String key, String label, EPosLabel poslabel, IModelController mc,
			IInteractionControllerForBrowserOrCombo ic) {
		super(null, key, label, poslabel, mc, ic);
	}

	/**
	 * 
	 * @param key
	 * @param label
	 * @param poslabel
	 * @param mc
	 * @param ic
	 * @param style
	 */
	public DBrowserUI(String key, String label, EPosLabel poslabel, IModelController mc,
			IInteractionControllerForBrowserOrCombo ic, int style) {
		super(null, key, label, poslabel, mc, ic);
		if (style != 0) {
			this._style = style;
		}
	}

	@Override
	public Composite createControl(final IPageController globalUIController, IFedeFormToolkit toolkit,
			Object ocontainer, int hspan) {

		Control swtControl;
		IFieldContenProposalProvider proposer = getContentAssistant();
		Composite container = (Composite) ocontainer;

		if (!isEditable()) {
			_style |= SWT.READ_ONLY;
		}
		if (isEditable() && proposer != null) {
			IControlContentAdapter contentAdapter = new ProposerTextContentAdapter(this, globalUIController, proposer);

			_contentAssistField = new ContentAssistField(container, _style, new IControlCreator() {
				public Control createControl(Composite controlParent, int style) {
					return new Text(controlParent, style);
				}
			}, contentAdapter, proposer.getContentProposalProvider(), proposer.getCommandId(), proposer
					.getAutoActivationCharacters());
			_contentAssistField.getContentAssistCommandAdapter().setProposalAcceptanceStyle(
					proposer.getProposalAcceptanceStyle());
			_textControl = (Text) _contentAssistField.getControl();
			swtControl = _contentAssistField.getLayoutControl();
			_contentAssistField.getContentAssistCommandAdapter().addContentProposalListener(this);
		} else {
			swtControl = _textControl = new Text(container, _style);
		}
		_textControl.setData(CADSE_MODEL_KEY, this);
		// swtControl.setData(CADSE_MODEL_KEY, this);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = hspan - 1;
		swtControl.setLayoutData(gd);

		if (getInteractionController().hasDeleteFunction()) {
			_textControl.addKeyListener(new KeyListener() {

				public void keyPressed(KeyEvent e) {
					if (e.keyCode == 8 || e.keyCode == 127) {
						deleteValue(globalUIController);
					}
				}

				public void keyReleased(KeyEvent e) {
					// TODO Auto-generated method stub

				}

			});
		}
		_textControl.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (!_sendNotification) {
					return;
				}
				_currentValueTextToSend = _textControl.getText();
				sendModificationIfNeed(_currentValueTextToSend, false);

			}
		});

		_textControl.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				if (e.character == '\u001b') { // Escape character
					cancelEditor();
				} else if (e.character == '\n' || e.character == '\r') {
					sendModificationIfNeed(_currentValueTextToSend, true);
				}
			}

			public void keyReleased(KeyEvent e) {
			}

		});

		_textControl.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
			}

			public void focusLost(FocusEvent e) {
				if (_currentValueTextToSend != null)
					sendModificationIfNeed(_currentValueTextToSend, true);
			}
		});

		_buttonBrowser = new Button(container, SWT.PUSH);
		_buttonBrowser.setText(SELECT_BUTTON);
		_buttonBrowser.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(@SuppressWarnings("unused")
			SelectionEvent e) {
				handleSelect(globalUIController);
			}
		});
		_buttonBrowser.setData(CADSE_MODEL_KEY, this);

		internalSetEditable(isEditable());

		return container;
	}

	protected void cancelEditor() {
		// TODO Auto-generated method stub

	}

	protected void deleteValue(IPageController fieldController) {
		if (_value != null) {
			Object oldValue = _value;
			_value = null;
			fieldController.broadcastValueDeleted(this, oldValue);
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		_textControl = null;
		_buttonBrowser = null;
		_value = null;
		_contentAssistField = null;
		_currentValueText = null;
		_currentValueTextToSend = null;
	}

	protected Object fromString(String value2) {
		return getInteractionController().fromString(value2);
	}

	protected IFieldContenProposalProvider getContentAssistant() {
		IInteractionController uc = getInteractionController();
		if (uc instanceof IFieldContenProposalProvider) {
			return (IFieldContenProposalProvider) uc;
		}
		return null;
	}

	@Override
	public int getHSpan() {
		return 2;
	}

	@Override
	public IInteractionControllerForBrowserOrCombo getInteractionController() {
		return (IInteractionControllerForBrowserOrCombo) super.getInteractionController();
	}

	@Override
	public Control getMainControl() {
		return this._textControl;
	}

	@Override
	public Object[] getSelectedObjects() {
		return new Object[] { _value };
	}

	public ItemType getType() {
		return CadseGCST.DBROWSER;
	}

	@Override
	public Object getUIObject(int index) {
		switch (index) {
			case 0:
				return this._textControl;
			case 1:
				return this._buttonBrowser;
		}
		return null;
	}

	@Override
	public Object getVisualValue() {
		return _value;
	}

	protected void handleSelect(IPageController fieldController) {
		Object ret = getInteractionController().selectOrCreateValue(this._buttonBrowser.getShell());

		if (ret != null) {
			setVisualValue(ret, false);
			fieldController.broadcastValueChanged(this, getVisualValue());
		}
	}

	@Override
	public void internalSetEditable(boolean v) {
		_textControl.setEditable(v);
		_buttonBrowser.setEnabled(v);
	}

	@Override
	public void internalSetVisible(boolean v) {
		super.internalSetVisible(v);
		if (_textControl != null) {
			_textControl.setVisible(v);
		}
		if (_buttonBrowser != null) {
			_buttonBrowser.setVisible(v);
		}
	}

	@Override
	public void setEnabled(boolean v) {
		_buttonBrowser.setEnabled(v);
		_textControl.setEnabled(v);
	}

	public void setVisualValue(Object visualValue, boolean sendNotification) {
		_value = visualValue;
		if (_textControl == null || _textControl.isDisposed()) 
			return ;
		
		final String valueText = toString(_value);
		if (valueText.equals(_currentValueText)) {
			return;
		}
		_currentValueText = valueText;
		_sendNotification = sendNotification;
		try {
			_textControl.setText(valueText);
			_textControl.setSelection(valueText.length(), valueText.length());
		} finally {
			_sendNotification = true;
		}
	}

	public String toString(Object object) {
		String ret;
		try {
			ret = getInteractionController().toString(object);
			if (ret == null) {
				ret = "";
			}
		} catch (Throwable e) {
			ret = "<invalid value>";
			globalController.setMessage("Internal error " + e.getClass().getCanonicalName() + ": " + e.getMessage(),
					IPageController.ERROR);
			WSPlugin.log(new Status(Status.ERROR, WSPlugin.PLUGIN_ID, "Internal error in DBrowwserUI.toString", e));
		}
		return ret;
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
				_value = newValue;
				globalController.broadcastValueChanged(this, newValue);

			} else {
				globalController.broadcastValueDeleted(this, _value);
				_value = null;
			}

		}

	}

	protected synchronized void sendModificationIfNeed(String value, boolean send) {
		if (!isEditable()) {
			return;
		}

		Object goodValue = fromString(value);
		if (Convert.equals(goodValue, _value)) {
			return;
		}

		if (send) {
			// true, if error
			if (!globalController.broadcastValueChanged(this, goodValue)) {
				_currentValueText = value;
				_value = goodValue;
			}
		} else {
			globalController.setMessage(null, IPageController.ERROR);

			// validate value and if it's ok, test other fields
			// true if error
			if (!validateValueChanged(goodValue)) {
				getPages().validateFields(this, getPage());
			}
		}
	}
}