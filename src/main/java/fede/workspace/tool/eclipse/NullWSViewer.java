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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import fr.imag.adele.cadse.core.ChangeID;
import fr.imag.adele.cadse.core.IItemManager;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;

/**
 * The Class NullWSViewer.
 * 
 * @author <a href="mailto:stephane.chomat@imag.fr">Stephane Chomat</a>
 */
public class NullWSViewer implements IWSViewer {

    /* (non-Javadoc)
     * @see fede.workspace.tool.eclipse.IWSViewer#reset()
     */
    public void reset() {
    }

    /* (non-Javadoc)
     * @see fede.workspace.tool.eclipse.IWSViewer#refresh()
     */
    public void refresh() {
    }

    /* (non-Javadoc)
     * @see fede.workspace.tool.eclipse.IWSViewer#log(org.eclipse.core.runtime.IStatus)
     */
    public void log(IStatus status) {
    }

    /**
	 * Builds the referencing items.
	 * 
	 * @param item
	 *            the item
	 * @param forceBuilder
	 *            the force builder
	 * @param monitor
	 *            the monitor
	 * 
	 * @throws CoreException
	 *             the core exception
	 */
    public void buildReferencingItems(Item item, boolean forceBuilder, IProgressMonitor monitor) throws CoreException {
    }

    /* (non-Javadoc)
     * @see fede.workspace.tool.eclipse.IWSViewer#buildModifiedItem(fr.imag.adele.cadse.core.Item, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void buildModifiedItem(Item item, boolean forceBuilder,
            IProgressMonitor monitor) throws CoreException {
    }

    /* (non-Javadoc)
     * @see fede.workspace.tool.eclipse.IWSViewer#deleteEclipseResource(org.eclipse.core.resources.IResource, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void deleteEclipseResource(IResource resource, boolean keepContent,
            IProgressMonitor monitor) throws CoreException {
    }

    /* (non-Javadoc)
     * @see fr.imag.adele.cadse.core.IWSNotifieChange#notifieChangeEvent(fr.imag.adele.cadse.core.ChangeID, java.lang.Object[])
     */
    public void notifieChangeEvent(ChangeID id, Object... values) {
    }

    /* (non-Javadoc)
     * @see fede.workspace.tool.eclipse.IWSViewer#createDefaultManager(fr.imag.adele.cadse.core.ItemType)
     */
    public IItemManager createDefaultManager(ItemType itemType) {
        return null;
    }

	/* (non-Javadoc)
	 * @see fede.workspace.tool.eclipse.IWSViewer#refreshItem(fr.imag.adele.cadse.core.Item)
	 */
	public void refreshItem(Item item) {
	}


}
