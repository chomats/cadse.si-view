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
package fede.workspace.tool.view;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class TreeToolTipListener implements Listener{
    private static final String TOOL_TIP_LABEL_DATA = "TOOL_TIP_LABEL_DATA";
    private Tree tree;
    Shell tipShell = null;
   
    Text tipLabel = null;
    ToolTipTreeItemLabelProvider labelProvider;
    final Listener labelListener = new Listener() {
        public void handleEvent(Event event) {
          Text label = (Text) event.widget;
          Shell shell = label.getShell();
          switch (event.type) {
          case SWT.MouseDown:
            Event e = new Event();
            e.item = (TreeItem) label.getData(TOOL_TIP_LABEL_DATA);
            // Assuming table is single select, set the selection as if
            // the mouse down event went through to the table
            // desactive le changement de selection.
            //tree.setSelection(new TreeItem[] { (TreeItem) e.item });
           // tree.notifyListeners(SWT.Selection, e);
          // fall through
          case SWT.MouseMove:
          case SWT.MouseExit:
            shell.dispose();
            break;
          }
        }
      };
      
    public TreeToolTipListener(Tree tree, ToolTipTreeItemLabelProvider labelProvider) {
        this.tree = tree;
        this.labelProvider = labelProvider;
        this.tree.setToolTipText("");// Disable native tooltip
        
        this.tree.addListener(SWT.Dispose, this);
        this.tree.addListener(SWT.KeyDown, this);
        this.tree.addListener(SWT.MouseMove, this);
        this.tree.addListener(SWT.MouseHover, this);

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
        case SWT.MouseHover: {
            
          TreeItem item = tree.getItem(new Point(event.x, event.y));
          if (item != null) {
            if (tipShell != null && !tipShell.isDisposed())
              tipShell.dispose();
            tipShell = new Shell(tree.getShell(), SWT.ON_TOP | SWT.TOOL);   
            FillLayout f = new FillLayout();
            f.marginHeight =5;
            f.marginWidth = 5;
            tipShell.setLayout(f);
            tipLabel = new Text(tipShell, SWT.V_SCROLL);
            tipLabel.setEditable(false);
            tipLabel.setForeground(Display.getCurrent()
                .getSystemColor(SWT.COLOR_INFO_FOREGROUND));
            tipLabel.setBackground(Display.getCurrent()
                .getSystemColor(SWT.COLOR_INFO_BACKGROUND));
            tipLabel.setData(TOOL_TIP_LABEL_DATA, item);
            tipLabel.setText(labelProvider.getText(item));
            tipLabel.addListener(SWT.MouseExit, labelListener);
            tipLabel.addListener(SWT.MouseDown, labelListener);
            tipLabel.addListener(SWT.MouseMove, labelListener);
            Point size = tipShell.computeSize(SWT.DEFAULT, SWT.DEFAULT);
            Rectangle rect = item.getBounds(0);
            Point pt = tree.toDisplay(rect.x, rect.y);
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
    
  public static void main(String[] args) {
    final Display display = new Display();
    final Shell shell = new Shell(display);
    shell.setLayout(new FillLayout());
    final Tree tree = new Tree(shell, SWT.BORDER);
    for (int i = 0; i < 20; i++) {
        TreeItem item = new TreeItem(tree, SWT.NONE);
        item.setText("item " + i);
    }
    TreeToolTipListener tt = new TreeToolTipListener(tree, new ToolTipTreeItemLabelProvider() {
        public String getText(TreeItem item) {
            return "tooltip " + item.getText();
        }
    });
    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
    tt.dispose();
    display.dispose();
  }
}