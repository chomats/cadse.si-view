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

import fr.imag.adele.cadse.core.CompactUUID;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.Link;
import fede.workspace.tool.view.ItemInViewer;

public class FilterDoublon extends ViewerFilter {
	
	
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (element instanceof ItemInViewer) {
            ItemInViewer itemInViewer = ((ItemInViewer)element);
            ItemInViewer parent = itemInViewer.getParent();
            if (parent == null) return true;
            
            int kind = itemInViewer.getKind();
            if (kind != ItemInViewer.LINK_OUTGOING)
                return true;
            Link orignalLink = itemInViewer.getLink();
            if (orignalLink == null)
                return true;
            CompactUUID id = orignalLink.getDestinationId();
            
            int indexthis = -1;
            int indexfirst_notderived= -1;
            int indexfirst_derived = -1;
            IItemNode[] children = parent.getChildren();
            for (int i = 0; i < children.length; i++) {
            	IItemNode child = children[i];
                if (child.getKind() != ItemInViewer.LINK_OUTGOING) 
                    continue;
                Link l = child.getLink();
                if (l == null) continue;
                if (!l.getDestinationId().equals(id))
                    continue;
                if (l.equals(orignalLink)) 
                    indexthis = i;
                if (indexfirst_derived == -1 && l.isDerived())
                    indexfirst_derived = i;
                if (indexfirst_notderived == -1 && !l.isDerived())
                    indexfirst_notderived = i;
                
            }
            if (indexfirst_notderived != -1) {
                return (indexthis == indexfirst_notderived);
            }
            if (indexfirst_derived != -1) {
                return (indexthis == indexfirst_derived);
            }
            return false;
        }
        return true;
    }
}