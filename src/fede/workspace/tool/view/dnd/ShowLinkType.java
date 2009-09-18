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
package fede.workspace.tool.view.dnd;


import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

import fr.imag.adele.cadse.core.LinkType;

public class ShowLinkType implements Listener, SelectionListener{
     private Tree tree;
    Shell tipShell = null;
   
    List tipLabel = null;
    
    final Listener labelListener = new Listener() {
        public void handleEvent(Event event) {
          
          switch (event.type) {
          case SWT.MouseDown:
        	  List tipLabel = (List) event.widget;
        	  
            dispose();
            break;
            // Assuming table is single select, set the selection as if
            // the mouse down event went through to the table
            // desactive le changement de selection.
            //tree.setSelection(new TreeItem[] { (TreeItem) e.item });
           // tree.notifyListeners(SWT.Selection, e);
          // fall through
          case SWT.MouseMove:
          case SWT.MouseExit:
            dispose();
            break;
          
	        case SWT.KeyDown:
	      	  System.out.println("Key code : "+event.keyCode);
	      	  System.out.println("Key character : "+event.character);
	      	  if (SWT.ESC == event.character) {
	      		  
	      		  dispose();
	      	  }
	      
	      	  break;
	        }
        }
      };
	private int selectedIndex = -1;
      
    public ShowLinkType(Tree tree,  ArrayList<LinkType> selecting, Point pt) {
        this.tree = tree;
        this.tree.setToolTipText("");// Disable native tooltip
        
        this.tree.addListener(SWT.Dispose, this);
        this.tree.addListener(SWT.KeyDown, this);
        this.tree.addListener(SWT.MouseMove, this);
        
       
      
          
          tipShell = new Shell(tree.getShell(), SWT.ON_TOP | SWT.TOOL);   
          FillLayout f = new FillLayout();
          f.marginHeight =5;
          f.marginWidth = 5;
          tipShell.setLayout(f);
          tipLabel = new List(tipShell, SWT.V_SCROLL);
          
          tipLabel.setForeground(Display.getCurrent()
              .getSystemColor(SWT.COLOR_INFO_FOREGROUND));
          tipLabel.setBackground(Display.getCurrent()
              .getSystemColor(SWT.COLOR_INFO_BACKGROUND));
         
          for (LinkType type : selecting) {
			tipLabel.add(type.toString());
		  }
          tipLabel.addSelectionListener(this);
          tipLabel.addListener(SWT.KeyDown, labelListener);
          tipLabel.addListener(SWT.MouseExit, labelListener);
         // tipLabel.addListener(SWT.MouseDown, labelListener);
         // tipLabel.addListener(SWT.MouseMove, labelListener);
          Point size = tipShell.computeSize(SWT.DEFAULT, SWT.DEFAULT);
         
          
          Rectangle displayRect = tipShell.getDisplay().getBounds();
          if (size.x+pt.x > (displayRect.x+displayRect.height)) {
          	pt.x = displayRect.x+displayRect.height-size.x;
          }
          if (size.y+pt.y > (displayRect.y+displayRect.width)) {
          	size.y = displayRect.width-pt.y;
          	pt.y -= 18;
          }
          tipShell.setBounds(pt.x, pt.y, size.x, size.y);
          tipShell.setVisible(true);
        
    }
    
    public int getSelectedIndex() {
    	while (tipShell != null) {
    		try {
				wait(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
		return selectedIndex;
	}
    
    public void handleEvent(Event event) {
        switch (event.type) {
        case SWT.Dispose:
        case SWT.KeyDown:
        case SWT.MouseMove: {
          if (tipShell == null)
            break;
          tipShell.dispose();
          tipShell = null;
          tipLabel = null;
          break;
        }
        }
      }
    
    public void dispose() {
        if (tipShell != null && !tipShell.isDisposed())
            tipShell.dispose();
        if (tree != null && !tree.isDisposed()) {
            this.tree.removeListener(SWT.Dispose, this);
            this.tree.removeListener(SWT.KeyDown, this);
            this.tree.removeListener(SWT.MouseMove, this);
            this.tree.removeListener(SWT.MouseHover, this);
        }
    }

	public void widgetDefaultSelected(SelectionEvent e) {
		this.selectedIndex = tipLabel.getSelectionIndex();
		dispose();
	}

	public void widgetSelected(SelectionEvent e) {
		this.selectedIndex = tipLabel.getSelectionIndex();
		dispose();
	}
    
    
}