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
package fede.workspace.tool.view.menu;

import java.net.URL;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.util.Policy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbenchWindow;

import fede.workspace.tool.view.WSPlugin;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.IMenuAction;

/**
 * A <code>BaseNewWizardMenu</code> is used to populate a menu manager with
 * New Wizard actions for the current perspective's new wizard shortcuts,
 * including an Other... action to open the new wizard dialog.
 * 
 * @since 3.1
 */
public class MenuActionContributionItem extends ContributionItem implements Listener {

	private static final String	REF	= "##ref##";
	private IWorkbenchWindow	workbenchWindow;
	IItemNode[]					selection;
	private MenuItem[]			widget;
	IMenuAction[]				dy;

	/**
	 * Creates a new wizard shortcut menu for the IDE.
	 * 
	 * @param window
	 *            the window containing the menu
	 * @param parent
	 *            the item parent for the new item or the item from the new item
	 *            is created..
	 */
	public MenuActionContributionItem(IWorkbenchWindow window, IItemNode[] selection, IMenuAction[] dy) {
		super(null);
		Assert.isNotNull(window);
		Assert.isNotNull(selection);

		this.workbenchWindow = window;
		this.selection = selection;
		this.dy = dy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.IContributionItem#dispose()
	 */
	@Override
	public void dispose() {
		if (workbenchWindow != null) {
			super.dispose();
			workbenchWindow = null;
		}
	}

	@Override
	public void fill(Menu menu, int index) {
		int style = SWT.PUSH;
		widget = new MenuItem[dy.length];
		boolean appendsepartor = false;
		for (int i = 0; i < dy.length; i++) {
			final IMenuAction aMenuAction = dy[i];

			if (aMenuAction.isMenu()) {
				IMenuAction[] menuChildren = aMenuAction.getChildren();
				if (menuChildren.length != 0) {
					MenuManager subMenuManager = new MenuManager(aMenuAction.getLabel(), aMenuAction.getId());
					subMenuManager.add(new MenuActionContributionItem(this.workbenchWindow, this.selection,
							menuChildren));
					subMenuManager.fill(menu, index);
					if (index >= 0) {
						index++;
					}
					appendsepartor = true;
				}
				continue;
			}
			if (aMenuAction.isSeparator()) {
				if (appendsepartor) {
					if (index >= 0) {
						new MenuItem(menu, SWT.SEPARATOR, index++);
					} else {
						new MenuItem(menu, SWT.SEPARATOR);
					}
					appendsepartor = false;
				}
				continue;
			}
			if (aMenuAction.isGroup()) {
				continue;
			}

			MenuItem mi;
			if (index >= 0) {
				mi = new MenuItem(menu, style, index++);
			} else {
				mi = new MenuItem(menu, style);
			}
			mi.setText(aMenuAction.getLabel());
			mi.setData(aMenuAction);
			appendsepartor = true;

			try {
				URL url = aMenuAction.getImage();
				if (url != null) {
					ImageRegistry ir = WSPlugin.getDefault().getImageRegistry();
					Image image = ir.get(url.toString());
					if (image == null) {
						ImageDescriptor desc = ImageDescriptor.createFromURL(url);
						image = desc.createImage();
						if (image != null) {
							ir.put(url.toString(), image);
						}
					}
					if (image != null) {
						mi.setImage(image);
					}
				} else {
					ImageDescriptor desc = (ImageDescriptor) aMenuAction.getImageDescriptor();
					if (desc != null) {
						Image image = desc.createImage();
						if (image != null) {
							mi.setImage(image);
						}
					}
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
			widget[i] = mi;
			mi.setData(REF, aMenuAction);
			mi.addListener(SWT.Dispose, this);
			mi.addListener(SWT.Selection, this);

		}
		super.fill(menu, index + dy.length);
	}

	public void handleEvent(Event event) {
		switch (event.type) {
			case SWT.Dispose:
				handleWidgetDispose(event);
				break;
			case SWT.Selection:
				Widget ew = event.widget;
				if (ew != null) {
					handleWidgetSelection(event, ((MenuItem) ew).getSelection());
				}
				break;
		}
	}

	/**
	 * Handles a widget dispose event for the widget corresponding to this item.
	 */
	protected void handleWidgetDispose(Event e) {
		// Check if our widget is the one being disposed.
		// Clear the widget field.
		widget = null;

	}

	/**
	 * Handles a widget selection event.
	 */
	protected void handleWidgetSelection(Event e, boolean selection) {

		Widget item = e.widget;
		if (item == null) {
			return;
		}

		if (widget == null) {
			return;
		}

		boolean trace = Policy.TRACE_ACTIONS;
		IMenuAction aMenuAction = (IMenuAction) item.getData(REF);
		if (aMenuAction == null) {
			return;
		}

		long ms = System.currentTimeMillis();
		if (trace) {
			System.out.println("Running action: " + aMenuAction.getLabel()); //$NON-NLS-1$
		}

		try {
			aMenuAction.run(this.selection);
		} catch (CadseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (trace) {
			System.out.println((System.currentTimeMillis() - ms) + " ms to run action: " + aMenuAction.getLabel()); //$NON-NLS-1$
		}

	}

	/**
	 * Returns the window in which this menu appears.
	 * 
	 * @return the window in which this menu appears
	 */
	protected IWorkbenchWindow getWindow() {
		return workbenchWindow;
	}

}
