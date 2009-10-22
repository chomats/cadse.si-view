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

import org.eclipse.jface.action.IMenuManager;

public interface IC_ContextMenu  {
	
	boolean hasRemoveAllWhenShown();
	
    /**
     * Notifies this listener that the menu is about to be hidden by
     * the given menu manager.
     *
     * @param manager the menu manager
     */
    public void menuAboutToHide(Object[] selection, IMenuManager manager);
    
    /**
     * Notifies this listener that the menu is about to be shown by
     * the given menu manager.
     *
     * @param manager the menu manager
     */
    public void menuAboutToShow(Object[] selection, IMenuManager manager);
}
