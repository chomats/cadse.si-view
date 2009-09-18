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
package fede.workspace.model.wizard;

import org.eclipse.swt.widgets.Shell;

import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.LinkType;


public interface IContainerField {

    public abstract boolean dialogChanged();

    public abstract Item        getParentItem();
    public abstract LinkType    getIncomingLinkType();
    
    public abstract Item getTheCurrentItem();

    public abstract Shell  getShell();
}