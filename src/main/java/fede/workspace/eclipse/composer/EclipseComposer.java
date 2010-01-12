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
package fede.workspace.eclipse.composer;

import org.eclipse.core.resources.IResource;

import fr.imag.adele.cadse.core.ContentItem;
import fr.imag.adele.cadse.core.build.Composer;
import fr.imag.adele.cadse.core.build.IExporterTarget;
import fede.workspace.eclipse.MelusineProjectManager;
import fede.workspace.eclipse.exporter.EclipseExporterTarget;

public class EclipseComposer extends Composer {

	public EclipseComposer(ContentItem contentManager, String... exporterTypes) {
		super(contentManager, exporterTypes);
	}

	@Override
	protected IExporterTarget getTarget() {
		IResource r = MelusineProjectManager.getResource(getItem(), true);
		if (r == null) throw new NullPointerException();
		return new EclipseExporterTarget(r.getProject());
	}

	
}
