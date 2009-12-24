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
package fede.workspace.tool.view.adapter;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterFactory;

import fr.imag.adele.cadse.core.Item;
import fede.workspace.tool.view.WSPlugin;

/**
 * Convertis an object Item to an other object (IResource, IPropertySource ...)
 * @author chomats
 *
 */
public class WSResourceAdapterFactory implements IAdapterFactory {

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		Object i = igetAdapter(adaptableObject, adapterType);
		//System.out.println("WSResourceAdapterFactory:"+adaptableObject+" -> " +adapterType+" = "+i);
		return i;
	}
	
	Object igetAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType.isInstance(adaptableObject)) {
            return adaptableObject;
        }
		if (adapterType == Item.class) {
            try {
                return WSPlugin.getItemFromResource((IResource) adaptableObject);
            } catch (Throwable e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
		}
		
		
		return null;
	}
	
	public Class[] getAdapterList() {
		return new Class[] {Item.class };
	}

}
