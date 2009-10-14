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
package fede.workspace.model.manager.properties.impl.ic;

import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;

import fede.workspace.model.manager.properties.IFieldContenProposalProvider;
import fede.workspace.model.manager.properties.Proposal;
import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.ui.IInteractionController;

public final class IC_BooleanText extends IC_Abstract implements IInteractionController, IFieldContenProposalProvider,
		IContentProposalProvider {

	public char[] getAutoActivationCharacters() {
		return new char[0];
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
		return null;
	}

	public Object getValueFromProposal(Proposal proposal) {
		return proposal.getValue();
	}

	public IContentProposal[] getProposals(String contents, int position) {
		IContentProposal[] ret = new IContentProposal[2];
		ret[0] = new Proposal("true", "true", "", 4, Boolean.TRUE);
		ret[1] = new Proposal("false", "false", "", 5, Boolean.FALSE);
		return ret;
	}

	public ItemType getType() {
		return CadseGCST.IC_BOOLEAN_TEXT;
	}

}