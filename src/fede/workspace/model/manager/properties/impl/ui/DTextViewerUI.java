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

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.ui.EPosLabel;
import fr.imag.adele.cadse.core.ui.IFedeFormToolkit;
import fr.imag.adele.cadse.core.ui.IInteractionController;
import fr.imag.adele.cadse.core.ui.IModelController;
import fr.imag.adele.cadse.core.ui.IPageController;

public class DTextViewerUI extends DAbstractField {

	public DTextViewerUI(String key, String label, EPosLabel poslabel, IModelController mc, IInteractionController ic) {
		super(key, label, poslabel, mc, ic);
		// TODO Auto-generated constructor stub
	}

	public static final String	FLAGS	= "flags";

	ITextViewer					fTextViewer;

	private int					vspan	= 1;

	private int					style	= SWT.SINGLE;

	@Override
	public Object createControl(IPageController globalUIController, IFedeFormToolkit toolkit, Object container,
			int hspan) {

		fTextViewer = createTextViewer((Composite) container);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = hspan;
		gd.verticalSpan = this.vspan;
		if (gd.verticalSpan != 1) {
			gd.verticalAlignment = GridData.FILL;
			gd.grabExcessVerticalSpace = true;
		}
		fTextViewer.getTextWidget().setLayoutData(gd);
		// fTextViewer.c
		// WordRule rule = new WordRule(new IWordDetector() {
		// public boolean isWordStart(char c) {
		// return c == '{';
		// }
		// public boolean isWordPart(char c) {
		// return Character.isDigit(c) || c == '}';
		// }
		// });
		return null;
	}

	protected ITextViewer createTextViewer(Composite parent) {
		return new TextViewer(parent, getFlags());
	}

	/**
	 * @see SWT#FULL_SELECTION
	 * @see SWT#MULTI
	 * @see SWT#READ_ONLY
	 * @see SWT#SINGLE
	 * @see SWT#WRAP
	 */
	protected int getFlags() {
		return style; // getDescription().getLocal(FLAGS, SWT.SINGLE);
	}

	public void forceChange() {
		// TODO Auto-generated method stub

	}

	public int getVSpan() {
		return 1;
	}

	@Override
	public Object getUIObject(int index) {
		return fTextViewer;
	}

	@Override
	public Object getVisualValue() {
		return fTextViewer.getDocument().get();
	}

	@Override
	public void internalSetEditable(boolean v) {
		fTextViewer.setEditable(v);
	}

	@Override
	public void setEnabled(boolean v) {
		fTextViewer.setEditable(v);
	}

	@Override
	public void internalSetVisible(boolean v) {
		super.internalSetVisible(v);
		fTextViewer.getTextWidget().setVisible(v);
	}

	public void setVisualValue(Object visualValue, boolean sendNotification) {
		fTextViewer.getDocument().set((String) visualValue);
	}

	public ItemType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Control getMainControl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] getSelectedObjects() {
		// TODO Auto-generated method stub
		return null;
	}

}
