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
package fede.workspace.model.manager.properties;

import org.eclipse.swt.widgets.Shell;

import fr.imag.adele.cadse.core.ui.IInteractionController;
import fede.workspace.model.manager.properties.impl.ic.IC_EnumForBrowser_Combo;
import fede.workspace.model.manager.properties.impl.ic.IC_LinkForBrowser_Combo_List;

/**
 * This interface is used by the DBrowserUI to get the values to show, display a
 * dialogue to select or create a new value, to transform object in string
 * format, to transform string value to object value. The ui-field manipulates
 * object value of Type A (String, Item, Link, ....). This controller can
 * display other type and transforms the object of Type B into an object of type
 * A.
 * 
 * The implementation of this controller :
 * <ul>
 * <li><b>{@link IC_EnumForBrowser_Combo}</b> the interaction controller for
 * enum attribute definition</li>
 * <li><b>{@link IC_LinkForBrowser_Combo_List}</b> the interaction controller
 * for link attribute definition</li>
 * <li><b>{@link IC_JavaClassForBrowser_Combo}</b> the interaction controller
 * for java element (class, enum, interface)</li>
 * </ul>
 * 
 * @author Adele team
 * 
 */

public interface IInteractionControllerForBrowserOrCombo extends IInteractionController {
	/**
	 * @return value to display in ui field
	 */
	Object[] getValues();

	/**
	 * Show a dialog to display the value to select, and create the good value
	 * if need.
	 * 
	 * @param parentShell
	 * @return
	 */
	Object selectOrCreateValue(Shell parentShell);

	/**
	 * 
	 * @param value
	 *            a value of type A.
	 * @return the representation of the value in string.
	 */
	String toString(Object value);

	/**
	 * 
	 * @param value
	 *            a value in string : a representation.
	 * @return a represented object which representation value corresponds to
	 *         the value
	 */
	Object fromString(String value);

	/**
	 * 
	 * @return has delete function (to transform value to null)
	 */
	boolean hasDeleteFunction();

	/**
	 * 
	 * @return return the title of the window
	 */
	String getTitle();

	String getMessage();

}
