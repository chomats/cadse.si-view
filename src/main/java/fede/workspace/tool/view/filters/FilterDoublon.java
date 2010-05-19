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
/**
 * 
 */
package fede.workspace.tool.view.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import java.util.ArrayList;
import java.util.UUID;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.Link;

public class FilterDoublon extends ViewerFilter {

	@Override
	public Object[] filter(Viewer viewer, Object parentElement,
			Object[] elements) {
		try {
			int size = elements.length;
			ArrayList out = new ArrayList(size);
			for (int i = 0; i < size; ++i) {
				Object element = elements[i];
				if (element instanceof IItemNode) {
					IItemNode itemInViewer = ((IItemNode) element);
					IItemNode parent = itemInViewer.getParent();
					if (parent == null) {
						out.add(element);
						continue;
					}

					int kind = itemInViewer.getKind();
					if (kind != IItemNode.LINK_OUTGOING) {
						out.add(element);
						continue;
					}
					Link orignalLink = itemInViewer.getLink();
					if (orignalLink == null) {
						out.add(element);
						continue;
					}
					UUID id = orignalLink.getDestinationId();

					int indexthis = -1;
					int indexfirst_notderived = -1;
					int indexfirst_derived = -1;
					for (int j = 0; j < elements.length; j++) {
						IItemNode child = (IItemNode) elements[j];
						if (child.getKind() != IItemNode.LINK_OUTGOING)
							continue;
						Link l = child.getLink();
						if (l == null)
							continue;
						if (!l.getDestinationId().equals(id))
							continue;
						if (l.equals(orignalLink))
							indexthis = j;
						if (indexfirst_derived == -1 && l.isDerived())
							indexfirst_derived = j;
						if (indexfirst_notderived == -1 && !l.isDerived())
							indexfirst_notderived = j;

					}
					if (indexfirst_notderived != -1) {
						if (indexthis == indexfirst_notderived) {
							out.add(element);
						}
					}
					if (indexfirst_derived != -1) {
						if (indexthis == indexfirst_derived) {
							out.add(element);
						}
					}
				} else {
					out.add(element);
				}
			}
			return out.toArray();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return elements;
		}
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		return true;
	}
}