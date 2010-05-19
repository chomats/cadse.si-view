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
package fede.workspace.model.manager;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import fr.imag.adele.cadse.core.Item;

public class IEMResourceProject {

	
    
    public static String convertLastPoint(String value) {
        int lastPoint = value.lastIndexOf('.');
        return value.substring(lastPoint + 1);
    }

    public static String convertMinusFristChar(String value) {
        if (value.length() == 0)
            return value;
        return value.toLowerCase().charAt(0) + value.substring(1);
    }
    
    public static String getDefaultPackageIntenal(Item item) {
        return getDefaultPackage(item)+".internal";
    }
    
    public static String getDefaultPackage(Item item) {
    	return "default.package";
    }
    
    public static void createFolder(IProgressMonitor monitor, IProject p, String name) throws CoreException {
    	if (!p.isOpen())
    		p.open(monitor);
		IFolder f = p.getFolder(name);
    	if (!f.exists()) f.create(true,true,monitor);
	}

    
}
