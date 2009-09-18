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
package fede.workspace.model.manager.properties.editor;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

import fr.imag.adele.cadse.core.CompactUUID;
import fr.imag.adele.cadse.core.Item;
import fede.workspace.tool.view.WSPlugin;
import fr.imag.adele.fede.workspace.si.view.View;

public class ItemEditorInputFactory implements IElementFactory {
	/**
     * Factory id. The workbench plug-in registers a factory by this name
     * with the "org.eclipse.ui.elementFactories" extension point.
     */
    private static final String ID_FACTORY = "fede.workspace.view.ItemEditorInputFactory"; //$NON-NLS-1$

    /**
     * Tag for the IFile.fullPath of the file resource.
     */
    private static final String TAG_ID = "id"; //$NON-NLS-1$

    /**
     * Creates a new factory.
     */
    public ItemEditorInputFactory() {
    }

    /* (non-Javadoc)
     * Method declared on IElementFactory.
     */
    public IAdaptable createElement(IMemento memento) {
        // Get the file name.
        String id = memento.getString(TAG_ID);
        if (id == null) {
			return null;
		}

        // Get a handle to the IFile...which can be a handle
        // to a resource that does not exist in workspace
        Item item = View.getInstance().getWorkspaceDomain().getLogicalWorkspace().getItem(CompactUUID.fromString(id));
                
        if (item != null) {
			return new ItemEditorInput(item);
		} else {
			return null;
		}
    }

    /**
     * Returns the element factory id for this class.
     * 
     * @return the element factory id
     */
    public static String getFactoryId() {
        return ID_FACTORY;
    }

    /**
     * Saves the state of the given file editor input into the given memento.
     *
     * @param memento the storage area for element state
     * @param input the file editor input
     */
    public static void saveState(IMemento memento, Item input) {
        memento.putString(TAG_ID, input.getId().toString());
    }
}
