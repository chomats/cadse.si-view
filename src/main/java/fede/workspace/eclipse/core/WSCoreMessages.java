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

package fede.workspace.eclipse.core;

import org.eclipse.osgi.util.NLS;

/**
 * The Class WSCoreMessages.
 * 
 * @author <a href="mailto:stephane.chomat@imag.fr">Stephane Chomat</a>
 */
public class WSCoreMessages extends NLS {
	
	/** The Constant BUNDLE_NAME. */
	private static final String BUNDLE_NAME = "fede.workspace.eclipse.core.messages";//$NON-NLS-1$
	// package: org.eclipse.ui.ide
    
    /** The WS choose workspace dialog_dialog name. */
	public static String WSChooseWorkspaceDialog_dialogName;
    
    /** The WS choose workspace dialog_dialog title. */
    public static String WSChooseWorkspaceDialog_dialogTitle;
    
    /** The WS choose workspace dialog_dialog message. */
    public static String WSChooseWorkspaceDialog_dialogMessage;

	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, WSCoreMessages.class);
	}
}