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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.internal.util.BundleUtility;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import fr.imag.adele.cadse.core.CompactUUID;
import fr.imag.adele.cadse.core.IItemManager;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.LogicalWorkspace;
import fr.imag.adele.cadse.core.impl.CadseCore;
import fr.imag.adele.fede.workspace.si.view.View;

/**
 * The main plugin class to be used in the desktop.
 */
public class WSPlugin extends AbstractUIPlugin {

	/**
	 * The unique identifier constant (value "<code>Tool.Workspace.View</code>")
	 */
	public static final String	NAMESPACE_ID	= "fede.tool.workspace.view";					//$NON-NLS-1$
	public static final String	PLUGIN_ID		= "fr.imag.adele.cadse.si.workspace.view";		//$NON-NLS-1$

	/**
	 * 
	 * 
	 */
	public static final String	PROVIDER		= "fede.tool.workspace.view.ItemTypeProvider";	//$NON-NLS-1$

	public static final String	ID_VIEWER		= "fede.workspace.tool.view.WSView";
	public static final String	ID_LINKS		= "fede.workspace.tool.view.WSLinkView";
	public static final String	ID_CONTENT		= "fede.workspace.tool.view.WSContentView";

	// The shared instance.
	private static WSPlugin		plugin;

	/**
	 * The constructor.
	 */
	public WSPlugin() {
		plugin = this;
	}

	// static WSView view;
	/**
	 * This method is called upon plug-in activation
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		Job startJob = new Job("Start bundles") {
			protected IStatus run(IProgressMonitor monitor) {
				Bundle b = Platform.getBundle("org.apache.felix.org.apache.felix.ipojo");
				if (b != null) {
					try {
						if (b.getState() != Bundle.ACTIVE) {
							b.start();
						}

					} catch (BundleException e) {
						logException(e);
					}
				}
				b = Platform.getBundle("fr.imag.adele.ipojo.autostart");
				if (b != null) {
					try {
						if (b.getState() != Bundle.ACTIVE) {
							b.start();
						}
					} catch (BundleException e) {
						logException(e);
					}
				}
				return Status.OK_STATUS;
			}
		};
		// startJob.setSystem(true);
		startJob.schedule();

	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static WSPlugin getDefault() {
		if (plugin == null) {
			try {
				Platform.getBundle(PLUGIN_ID).start();
			} catch (BundleException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * Returns an image for the image file at the given plug-in relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image
	 */
	public static Image getImage(String path) {
		ImageRegistry ir = getDefault().getImageRegistry();
		Image image = ir.get(path);
		if (image != null) {
			return image;
		}

		final ImageDescriptor imageDesc = AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
		image = imageDesc.createImage(false);
		if (image == null) {
			return null; // cannot create it
		}
		ir.put(path, image);
		return image;
	}

	public static IItemManager getManager(Item it) {
		IItemManager ip = it.getType().getItemManager();
		if (ip == null) {
			throw new IllegalArgumentException(MessageFormat.format("The item {0} has no item manager", it.getId()));
		}
		return ip;
	}

	public static IItemManager getManager(ItemType it) {
		IItemManager ip = it.getItemManager();
		if (ip == null) {
			throw new IllegalArgumentException(MessageFormat.format("The item {0} has no item manager", it.getId()));
		}
		return ip;
	}

	public static Item getItemFromResource(IResource object) {
		try {
			if (View.getInstance() == null) {
				return sGetItemFromResource(object);
			}
			try {
				return View.getInstance().getItemFromResource(object);
			} catch (CoreException ignored) {
				WSPlugin.logException(ignored);
				return null;
			}
			// providerManager.setViewer(this);

			// when get the first provider : providerManager.registerProvider();
		} catch (Throwable e) {
			e.printStackTrace();
			// .logerror("Can't find Eclipse Service", e);
			return null;
		}
	}

	/**
	 * Get the item associated with an eclipse resource.
	 * 
	 * @param resource
	 *            the resource
	 * 
	 * @return the item from resource
	 * 
	 * @throws CoreException
	 *             the core exception
	 */
	public static Item sGetItemFromResource(IResource resource) throws CoreException {
		CompactUUID id = sGetUUIDFromResource(resource);
		LogicalWorkspace wl = CadseCore.getLogicalWorkspace();
		if (wl == null) {
			return null;
		}
		return wl.getItem(id);
	}

	/**
	 * Get the item associated with an eclipse resource.
	 * 
	 * @param resource
	 *            the resource
	 * 
	 * @return the UUID from resource
	 * 
	 * @throws CoreException
	 *             the core exception
	 */
	public static CompactUUID sGetUUIDFromResource(IResource resource) throws CoreException {
		if (resource == null) {
			throw new CoreException(new Status(Status.ERROR, WSPlugin.PLUGIN_ID, "the resouces is null"));
		}

		if (!resource.exists()) {
			throw new CoreException(new Status(Status.ERROR, WSPlugin.PLUGIN_ID, "the resouces doesn't exist."));
		}

		// get item identification from the resource properties
		String idStr = resource.getPersistentProperty(View.ITEM_ID_PROPERTY);
		// If there is no item identification associated with this resource
		// check parent resource
		if (idStr == null) {
			throw new CoreException(new Status(Status.ERROR, WSPlugin.PLUGIN_ID,
					"the resouces doesn't have id property."));
		}

		try {
			return CompactUUID.fromString(idStr);
		} catch (Throwable e) {
			throw new CoreException(new Status(Status.ERROR, WSPlugin.PLUGIN_ID, "the id property is not valid "
					+ idStr + " for " + resource.getFullPath().toPortableString(), e));

		}
	}

	public static CompactUUID getUUIDFromResource(IResource object) {
		try {
			try {
				return View.getInstance().getUUIDFromResource(object);
			} catch (CoreException ignored) {
				return null;
			}
			// providerManager.setViewer(this);

			// when get the first provider : providerManager.registerProvider();
		} catch (Throwable e) {
			// FedeLog.logerror("Can't find the Eclipse service.", e);
			e.printStackTrace();
		}
		return null;
	}

	public static boolean testAttribute(Object target, String name, String value) {

		Item item = null;
		if (target instanceof ItemInViewer) {
			item = ((ItemInViewer) target).getItem();
		}
		if (target instanceof Item) {
			item = (Item) target;
		}
		if (item != null && "type".equals(name)) {
			return item.getType().getId().equals(value);
		}
		return false;
	}

	public void registerIcom(Bundle b) {
		load(getImageRegistry(), b);
	}

	protected void load(ImageRegistry reg, Bundle b) {

		Properties p = new Properties();
		try {
			p.load(b.getResource("model/model-impl.properties").openStream());
		} catch (InvalidPropertiesFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (Object key : p.keySet()) {
			String pname = (String) key;
			if (pname.startsWith("ws.impl.icons.")) {
				String icon_key = pname.substring("ws.impl.icons.".length());
				reg.put(icon_key, imageDescriptorFromPlugin(b, p.getProperty(pname)));
			}
		}
	}

	public static ImageDescriptor imageDescriptorFromPlugin(Bundle bundle, String imageFilePath) {
		if (!BundleUtility.isReady(bundle)) {
			return null;
		}

		// look for the image (this will check both the plugin and fragment
		// folders
		URL fullPathString = BundleUtility.find(bundle, imageFilePath);
		if (fullPathString == null) {
			try {
				fullPathString = new URL(imageFilePath);
			} catch (MalformedURLException e) {
				return null;
			}
		}

		if (fullPathString == null) {
			return null;
		}
		return ImageDescriptor.createFromURL(fullPathString);
	}

	public Image getImageFrom(ItemType it, Item item) {
		ImageRegistry ir = getImageRegistry();
		URL url = getImageURLFrom(it, item);
		if (url == null) {
			return null;
		}
		try {
			String key = url.toString();
			Image image = ir.get(key);
			if (image == null) {
				ir.put(key, ImageDescriptor.createFromURL(url));
				image = ir.get(key);
			}
			return image;
		} catch (Throwable e) {
			WSPlugin.log(new Status(Status.ERROR, WSPlugin.PLUGIN_ID, 0, "cannot create image from " + url + ": "
					+ e.getMessage(), e));
		}
		return null;
	}

	public ImageDescriptor getImageDescriptorFrom(ItemType it, Item item) {
		ImageRegistry ir = getImageRegistry();
		URL url = getImageURLFrom(it, item);
		if (url == null) {
			return null;
		}
		try {
			String key = url.toString();
			ImageDescriptor image = ir.getDescriptor(key);
			if (image == null) {
				ir.put(key, ImageDescriptor.createFromURL(url));
				image = ir.getDescriptor(key);
			}
			return image;
		} catch (Throwable e) {
			WSPlugin.log(new Status(Status.ERROR, WSPlugin.PLUGIN_ID, 0, "cannot create image from " + url + ": "
					+ e.getMessage(), e));
		}
		return null;
	}
	
	public static URL getImageURL(String symbolicName, String imagePath) {
		Bundle b = Platform.getBundle(symbolicName);
		if (b== null) { return null; }
		
		return b.getEntry(imagePath);
	}

	public URL getImageURLFrom(ItemType it, Item item) {
		IItemManager im = getManager(it);
		if (im.hasImageByItem()) {
			URL url = im.getImage(item);
			if (url != null) {
				return url;
			}
		}
		return it.getImage();
	}

	public static void logException(Throwable e) {
		WSPlugin.log(new Status(Status.ERROR, WSPlugin.PLUGIN_ID, 0, e.getMessage(), e));
	}

	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	public static void logErrorMessage(String message) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, 0, message, null));
	}

	public static void logErrorMessage(String message, Object... param) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, 0, MessageFormat.format(message, param), null));
	}
}
