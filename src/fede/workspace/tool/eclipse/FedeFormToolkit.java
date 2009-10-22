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

package fede.workspace.tool.eclipse;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;

import fr.imag.adele.cadse.core.ui.EPosLabel;
import fr.imag.adele.cadse.core.ui.IFedeFormToolkit;


/**
 * The Class FedeFormToolkit.
 * 
 * @author <a href="mailto:stephane.chomat@imag.fr">Stephane Chomat</a>
 */
public class FedeFormToolkit extends FormToolkit implements IFedeFormToolkit{

	/**
	 * Instantiates a new fede form toolkit.
	 * 
	 * @param display
	 *            the display
	 */
	public FedeFormToolkit(Display display) {
		super(display);
	}

	/**
	 * Instantiates a new fede form toolkit.
	 * 
	 * @param colors
	 *            the colors
	 */
	public FedeFormToolkit(FormColors colors) {
		super(colors);
	}

	/**
	 * Creates the label.
	 * 
	 * @param container
	 *            the container
	 * @param hspan
	 *            the hspan
	 * @param label
	 *            the label
	 * @param posLabel
	 *            the pos label
	 * 
	 * @return the label
	 */
	public Label createLabel(Composite container, int hspan, 
			String label, EPosLabel posLabel) {
        Label labelWidget;
        GridData gd;
        
        labelWidget = createLabel(container,label);
        
        return labelWidget;
    }

	/**
	 * Creates the group.
	 * 
	 * @param container
	 *            the container
	 * @param label
	 *            the label
	 * 
	 * @return the group
	 */
	public Group createGroup(Object container, String label) {
		Group g = new Group((Composite) container,0);
		if (label != null)
			g.setText(label);
		adapt(g, false, false);
		return g;
	}

	/* (non-Javadoc)
	 * @see fr.imag.adele.cadse.core.ui.IFedeFormToolkit#createButton(java.lang.Object, java.lang.String, int)
	 */
	public Object createButton(Object container, String text, int style) {
		return super.createButton((Composite) container, text, style);
	}

	/* (non-Javadoc)
	 * @see fr.imag.adele.cadse.core.ui.IFedeFormToolkit#createText(java.lang.Object, java.lang.String, int)
	 */
	public Object createText(Object ocontainer, String defaultValue, int style) {
		return super.createText((Composite) ocontainer, defaultValue, style);
	}
}
