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

import org.eclipse.swt.widgets.Composite;

import fr.imag.adele.cadse.core.Item;


@Deprecated
abstract class MelusineField {
    protected IContainerField thePage;
    
    public void createField(IContainerField thePage, Composite container, int hspan) {
        this.thePage = thePage;
   }
    
    public int getHSpan(Item item) {
        return 2;
    }
    
    public void initialize() {
        
    }
    
    public String fieldDialogChanged()  {
        return null;
    }
    
    public void dialogHasChanged() {
        
    }

}
