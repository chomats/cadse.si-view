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
/**
 *
 */
package fede.workspace.model.manager.properties.impl.mc;

import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;

import fede.workspace.model.manager.properties.IFieldContenProposalProvider;
import fede.workspace.model.manager.properties.Proposal;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.attribute.IAttributeType;
import fr.imag.adele.cadse.core.attribute.IntegerAttributeType;
import fr.imag.adele.cadse.core.impl.ui.MC_AttributesItem;
import fr.imag.adele.cadse.core.ui.IInteractionController;
import fr.imag.adele.cadse.core.ui.IModelController;
import fr.imag.adele.cadse.core.ui.UIField;
import fr.imag.adele.cadse.core.util.Convert;

final public class MaxModelController extends MC_AttributesItem implements IInteractionController,
		IFieldContenProposalProvider, IContentProposalProvider {

	public static final String		MIN					= "min";
	public static final String		MAX					= "max";
	private IntegerAttributeType	minAttribute;

	private boolean					_cannotBeUndefined	= false;

	public MaxModelController() {
		super();
	}

	public MaxModelController(boolean cannotBeUndefined) {
		super();

		this._cannotBeUndefined = cannotBeUndefined;
	}

	public MaxModelController(IntegerAttributeType min) {
		super();
		this.minAttribute = min;
	}

	public static final String	UNBOUNDED	= "unbounded";
	static final String			MINUS_1		= "-1";

	@Override
	public boolean validValue(UIField field, Object value) {
		return validValueChanged(field, value);
	}

	@Override
	public boolean validValueChanged(UIField field, Object value) {
		try {
			if (value.equals(UNBOUNDED)) {
				return false;
			}

			boolean error = super.validValueChanged(field, value);
			if (error) {
				return error;
			}

			IAttributeType<?> attRef = field.getAttributeDefinition();
			int max = 1;
			if (attRef != null) {
				Integer i = (Integer) attRef.convertTo(value);
				if (i == null) {
					if (_cannotBeUndefined) {
						setMessageError("The field '" + getAttributeName() + "' must be defined");
						return true;
					}
					return false;
				}
				max = i.intValue();
			} else {
				if (value == null || "".equals(value) || "null".equals(value)) {
					return false;
				}

				max = Integer.parseInt((String) value);
			}
			if (max <= 0) {
				setMessageError("The field '" + getUIField().getName() + "' must be > 0");
				return true;
			}
			int min = getMin(getItem());
			if (max != -1 && max < min) {
				setMessageError("The field '" + getUIField().getName() + "' must be upper or equal at min value ("
						+ min + ")");
				return true;
			}
		} catch (NumberFormatException e) {
			setMessageError(e.getMessage());
			return true;
		}

		return false;
	}

	@Override
	public Object getValue() {
		Object value = super.getValue();
		if (value == null) {
			return null;
		}
		if (value.equals(MINUS_1) || value.equals(-1)) {
			return UNBOUNDED;
		}
		return value;
	}

	@Override
	public void notifieValueChanged(UIField field, Object value) {
		if (value.equals(UNBOUNDED)) {
			value = -1;
		}
		super.notifieValueChanged(field, value);
	}

	@Override
	public Object defaultValue() {
		Object ret = super.defaultValue();
		if (ret != null) {
			if (ret.equals(-1) || ret.equals("-1")) {
				return UNBOUNDED;
			}
			return ret;
		}
		return 1;
	}

	public int getMin() {
		Item item = getItem();
		if (this.minAttribute != null) {
			Object value = item.getAttribute(this.minAttribute.getName());
			Object realvalue = this.minAttribute.convertTo(value);
			if (realvalue == null) {
				return -1;
			}
			return ((Integer) realvalue).intValue();
		}
		return MaxModelController.getMin(item);
	}

	public static int getMin(Item item) {
		Object minStr = item.getAttribute(MIN);
		if (minStr != null) {
			try {
				return Convert.toInt(minStr, null);
			} catch (NumberFormatException e) {

			}
		}
		try {
			item.setAttribute(MIN, 0);
		} catch (CadseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;

	}

	public static int getMax(Item item) {

		Object maxStr = item.getAttribute(MAX);
		if (maxStr != null) {
			try {
				return Convert.toInt(maxStr, null);
			} catch (NumberFormatException e) {

			}
		}
		try {
			item.setAttribute(MAX, -1);
		} catch (CadseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;

	}

	public char[] getAutoActivationCharacters() {
		IAttributeType<?> attRef = getUIField().getAttributeDefinition();
		if (attRef != null && attRef.canBeUndefined()) {
			return new char[] { 'u', 'U', 'n' };
		}
		return new char[] { 'u', 'U' };
	}

	public String getCommandId() {
		return ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS;
	}

	public IContentProposalProvider getContentProposalProvider() {
		return this;
	}

	public int getProposalAcceptanceStyle() {
		return ContentProposalAdapter.PROPOSAL_REPLACE;
	}

	public Object setControlContents(String newValue) {
		return newValue;
	}

	public Object getValueFromProposal(Proposal proposal) {
		return proposal.getContent();
	}

	public IContentProposal[] getProposals(String contents, int position) {

		Proposal proposal_value_unbounded = new Proposal(UNBOUNDED, UNBOUNDED, "no limit value", UNBOUNDED.length());
		if (position == 1 && contents.length() >= 1 && (contents.charAt(0) == 'u' || contents.charAt(0) == 'U')) {
			return new IContentProposal[] { proposal_value_unbounded };
		}
		IAttributeType<?> attRef = getUIField().getAttributeDefinition();
		Proposal proposal_value_1 = new Proposal("1", "1", "singleton value", 1);
		if (attRef != null && attRef.canBeUndefined()) {
			Proposal proposal_value_null = new Proposal("null", "null", "null value", 4);
			if (position == 1 && contents.length() >= 1 && (contents.charAt(0) == 'n' || contents.charAt(0) == 'N')) {
				return new IContentProposal[] { proposal_value_null };
			}
			return new IContentProposal[] { proposal_value_null, proposal_value_1, proposal_value_unbounded };
		}

		return new IContentProposal[] { proposal_value_1, proposal_value_unbounded };
	}

	public IModelController getModelController() {
		return this;
	}

	public void setModelController(IModelController mc) {

	}
}