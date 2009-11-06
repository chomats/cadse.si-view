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
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.attribute.IAttributeType;
import fr.imag.adele.cadse.core.attribute.IntegerAttributeType;
import fr.imag.adele.cadse.core.impl.ui.mc.MC_AttributesItem;
import fr.imag.adele.cadse.core.ui.RuningInteractionController;
import fr.imag.adele.cadse.core.ui.RunningModelController;
import fr.imag.adele.cadse.core.ui.IPageController;
import fr.imag.adele.cadse.core.ui.UIField;

final public class MinModelController extends MC_AttributesItem implements RuningInteractionController,
		IFieldContenProposalProvider, IContentProposalProvider {
	IAttributeType<?>	maxAttribute		= null;

	private boolean		_cannotBeUndefined	= false;

	public MinModelController(boolean cannotBeUndefined) {
		super();

		this._cannotBeUndefined = cannotBeUndefined;
	}

	public MinModelController() {
		super();
	}

	public MinModelController(IntegerAttributeType max) {
		super();
		this.maxAttribute = max;
	}

	@Override
	public boolean validValueChanged(IPageController uiPlatform, UIField field, Object value) {
		boolean error = super.validValueChanged(uiPlatform, field, value);
		if (error) {
			return error;
		}
		try {
			IAttributeType<?> attRef = field.getAttributeDefinition();
			int min = 1;
			if (attRef != null) {
				Integer i = (Integer) attRef.convertTo(value);
				if (i == null) {
					if (_cannotBeUndefined) {
						uiPlatform.setMessageError("The field '" + getUIField().getLabel() + "' must be defined");
						return true;
					}
					return false;
				}
				min = i.intValue();
			} else {
				if (value == null || "".equals(value) || "null".equals(value)) {
					return false;
				}

				min = Integer.parseInt((String) value);
			}
			if (min <= -1) {
				uiPlatform.setMessageError("The field '" + getUIField().getLabel() + "' must be > -1");
				return true;
			}
			int max = getMax(uiPlatform);
			if (max != -1 && max < min) {
				uiPlatform.setMessageError("The field '" + getUIField().getLabel() + "' must be less or equal at max value (" + max
						+ ")");
				return true;
			}
		} catch (NumberFormatException e) {
			uiPlatform.setMessageError(e.getMessage());
			return true;
		}

		return false;
	}

	protected int getMax(IPageController uiPlatform) {
		Item item = uiPlatform.getItem(getUIField());
		if (this.maxAttribute != null) {
			Object value = item.getAttribute(this.maxAttribute);
			Object realvalue = this.maxAttribute.convertTo(value);
			if (realvalue == null) {
				return -1;
			}
			return ((Integer) realvalue).intValue();
		}
		return MaxModelController.getMax(item);
	}

	@Override
	public Object defaultValue() {
		Object ret = super.defaultValue();
		if (ret != null) {
			return ret;
		}
		return "0";
	}

	public char[] getAutoActivationCharacters() {
		IAttributeType<?> attRef = getUIField().getAttributeDefinition();
		if (attRef != null && attRef.canBeUndefined()) {
			return new char[] { '0', '1', 'n' };
		}
		return new char[] { '0', '1' };
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
		IAttributeType<?> attRef = getUIField().getAttributeDefinition();
		Proposal proposal_value_0 = new Proposal("0", "0", "optional value", 1);
		Proposal proposal_value_1 = new Proposal("1", "1", "mandatory value", 1);
		if (attRef != null && attRef.canBeUndefined()) {
			Proposal proposal_value_null = new Proposal("null", "null", "null value", 4);
			if (position == 1 && contents.length() >= 1 && (contents.charAt(0) == 'n' || contents.charAt(0) == 'N')) {
				return new IContentProposal[] { proposal_value_null };
			}
			return new IContentProposal[] { proposal_value_null, proposal_value_0, proposal_value_1 };
		}
		return new IContentProposal[] { proposal_value_0, proposal_value_1 };
	}

	public RunningModelController getModelController() {
		return this;
	}
}