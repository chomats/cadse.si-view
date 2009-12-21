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
package fede.workspace.eclipse.exporter;

import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.CoreException;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.build.IExportedContent;
import fr.imag.adele.cadse.core.build.IExporterTarget;
import fede.workspace.eclipse.composer.EclipseExportedContent;

public class EclipseExporterTarget implements IExporterTarget {
	IContainer targetContainer;
	
	
	public EclipseExporterTarget(IContainer targetContainer) {
		super();
		this.targetContainer = targetContainer;
	}

	public IContainer getTargetContainer() {
		return targetContainer;
	}

	public List<IExportedContent> getRepositoryComponents(String exporterType) throws CadseException {
		try {
			return EclipseExportedContent.getPackagedItems(targetContainer, exporterType, null);
		} catch (CoreException e) {
			throw new CadseException(e.getMessage());
		}
	}
}
