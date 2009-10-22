package fr.imag.adele.cadse.impl.exportimport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.JAXBException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;

import adele.util.io.ZipUtil;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.CadseRuntime;
import fr.imag.adele.cadse.core.CompactUUID;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.ProjectAssociation;
import fr.imag.adele.cadse.core.impl.CadseCore;
import fr.imag.adele.cadse.core.transaction.LogicalWorkspaceTransaction;

public class ExportImportCadseFunction {

	Set<Link>						outgoinglinks				= new HashSet<Link>();
	Set<ItemType>					requireItemType				= new HashSet<ItemType>();
	Set<CadseRuntime>				requireCadse				= new HashSet<CadseRuntime>();

	Set<IProject>					projects					= new HashSet<IProject>();

	final HashSet<Item>				items						= new HashSet<Item>();

	HashMap<String, CompactUUID>	projectsMap					= new HashMap<String, CompactUUID>();

	HashMap<File, String>			files;

	/** The Constant MELUSINE_DIR. */
	public static final String		MELUSINE_DIR				= ".melusine-dir/";

	/** The Constant MELUSINE_DIR_CADSENAME. */
	public static final String		MELUSINE_DIR_CADSENAME		= ".melusine-dir/cadsename";

	/** The Constant MELUSINE_DIR_CADSENAME_ID. */
	public static final String		MELUSINE_DIR_CADSENAME_ID	= ".melusine-dir/cadsename.id";

	/** The Constant MELUSINE_DIR_CADSENAME_ID. */
	public static final String		REQUIRE_CADSEs				= ".melusine-dir/require-cadses";
	/** The Constant MELUSINE_DIR_CADSENAME_ID. */
	public static final String		REQUIRE_ITEM_TYPEs			= ".melusine-dir/require-its";
	/** The Constant MELUSINE_DIR_CADSENAME_ID. */

	public static final String		PROJECTS					= ".melusine-dir/projects";

	public void exportItems(IProgressMonitor pmo, File file, String exportNameFile, boolean tstamp, Item... rootItems)
			throws FileNotFoundException, IOException {
		CadseCore.getCadseDomain().beginOperation("Export cadse");
		try {

			// String qname = CadseDefinitionManager.getUniqueName(cadsedef);
			File pf = new File(file, exportNameFile + "-cadse.zip");
			if (tstamp) {
				Date d = new Date(System.currentTimeMillis());
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HHmm");
				System.out.println(formatter.format(d));
				pf = new File(file, exportNameFile + "-cadse-" + formatter.format(d) + ".zip");
			}
			ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(pf));

			files = new HashMap<File, String>();

			pmo.beginTask("export cadse items ", 3);

			File wsFile = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
			File melusineDir = new File(wsFile, ".melusine");
			for (Item rootItem : rootItems) {
				pmo.setTaskName(rootItem.getName());
				getPersistanceFileAll(melusineDir, rootItem);
			}

			includesContents(pmo, projects, files);
			pmo.worked(1);
			pmo.setTaskName("zip entries...");
			ZipUtil.zip(files, outputStream);

			pmo.worked(2);
			// ZipUtil.addEntryZip(outputStream, new
			// ByteArrayInputStream(qname.getBytes()), MELUSINE_DIR_CADSENAME,
			// -1);
			// ZipUtil.addEntryZip(outputStream, new
			// ByteArrayInputStream(cadsedef.getId().toString().getBytes()),
			// MELUSINE_DIR_CADSENAME_ID, -1);

			ArrayList<CompactUUID> requireCadseIds = new ArrayList<CompactUUID>();
			for (CadseRuntime cr : requireCadse) {
				if (items.contains(cr)) {
					continue;
				}

				requireCadseIds.add(cr.getId());
			}

			ZipUtil.addEntryZip(outputStream, new ByteArrayInputStream(toByteArray(requireCadseIds)), REQUIRE_CADSEs,
					-1);

			ArrayList<CompactUUID> requireItemIds = new ArrayList<CompactUUID>();
			for (ItemType cr : requireItemType) {
				if (items.contains(cr)) {
					continue;
				}
				requireItemIds.add(cr.getId());
			}

			ZipUtil.addEntryZip(outputStream, new ByteArrayInputStream(toByteArray(requireItemIds)),
					REQUIRE_ITEM_TYPEs, -1);

			ZipUtil.addEntryZip(outputStream, new ByteArrayInputStream(toByteArray(projectsMap)), PROJECTS, -1);

			pmo.worked(3);
			outputStream.close();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			CadseCore.getCadseDomain().endOperation();
		}
	}

	private byte[] toByteArray(Object v) throws IOException {
		ByteArrayOutputStream cd = new ByteArrayOutputStream();
		ObjectOutputStream outObj = new ObjectOutputStream(cd);

		outObj.writeObject(v);
		outObj.flush();
		outObj.close();

		return cd.toByteArray();
	}

	private void includesContents(IProgressMonitor pmo, Set<IProject> projects, HashMap<File, String> files) {
		for (IProject p : projects) {
			File eclipseProjectFile = p.getLocation().toFile();
			files.put(eclipseProjectFile, p.getName());
		}
	}

	/**
	 * Gets the persistance file all.
	 * 
	 * @param melusineDir
	 *            the melusine dir
	 * @param item
	 *            the item
	 * @param files
	 *            the files
	 * @param items
	 *            the items
	 * 
	 * @return the persistance file all
	 */
	void getPersistanceFileAll(File melusineDir, Item item) {

		if (items.contains(item)) {
			System.err.println("entry duplicate " + item.getId() + " " + item.getQualifiedName());
			return;
		}

		items.add(item);
		ItemType it = item.getType();
		if (it != null) {
			if (!requireItemType.contains(it)) {
				requireItemType.add(it);
				CadseRuntime cr = it.getCadseRuntime();
				if (cr != null) {
					requireCadse.add(cr);
				}
			}
		}
		IProject r = item.getMainMappingContent(IProject.class);
		if (r != null) {
			projects.add(r);
			projectsMap.put(r.getName(), item.getId());
		}

		File xmlfile = new File(melusineDir, item.getId().toString() + ".ser");
		files.put(xmlfile, MELUSINE_DIR);
		xmlfile = new File(melusineDir, item.getId().toString() + ".xml");
		if (xmlfile.exists()) {
			files.put(xmlfile, MELUSINE_DIR);
		}

		List<? extends Link> links = item.getOutgoingLinks();
		for (Link link : links) {
			if (!link.getLinkType().isPart()) {
				if (!items.contains(link.getDestination())) {
					outgoinglinks.add(link);
				}

				continue;
			}
			if (!link.isLinkResolved()) {
				outgoinglinks.add(link);
				continue;
			}
			getPersistanceFileAll(melusineDir, link.getDestination());
		}
	}

	public Set<Link> getOutgoinglinks() {
		return outgoinglinks;
	}

	public Set<ItemType> getRequireItemType() {
		return requireItemType;
	}

	public Set<CadseRuntime> getRequireCadse() {
		return requireCadse;
	}

	public Set<IProject> getProjects() {
		return projects;
	}

	public HashSet<Item> getItemsHash() {
		return items;
	}

	/**
	 * Read cadse uuid.
	 * 
	 * @param f
	 *            the root directory
	 * 
	 * @return the compact uuid
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException
	 */
	public Object readObject(File pf, String key) throws IOException, ClassNotFoundException {

		ObjectInputStream isr = new ObjectInputStream(new FileInputStream(new File(pf, key)));
		try {
			Object o = isr.readObject();
			return o;
		} finally {
			isr.close();
		}
	}

	public void importCadseItems(IProgressMonitor pmo, File file) throws IOException, MalformedURLException,
			JAXBException, CadseException, ClassNotFoundException {
		CadseCore.getCadseDomain().beginOperation("Import cadse");
		try {
			// File f =
			// ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
			File pf = File.createTempFile("cadse-temp", ".dir");
			pf.mkdirs();

			ZipUtil.unzipFile(file, pf);

			File melusineDir = new File(pf, ".melusine-dir");
			File[] filesserxml = melusineDir.listFiles();
			Collection<URL> itemdescription = new ArrayList<URL>();
			for (File fser : filesserxml) {
				if (fser.getName().endsWith(".ser")) {
					itemdescription.add(fser.toURI().toURL());
				}
			}
			Collection<ProjectAssociation> projectAssociationSet = new ArrayList<ProjectAssociation>();
			projectsMap = (HashMap<String, CompactUUID>) readObject(pf, PROJECTS);
			for (Map.Entry<String, CompactUUID> e : projectsMap.entrySet()) {
				ProjectAssociation pa = new ProjectAssociation(e.getValue(), e.getKey());
				projectAssociationSet.add(pa);
			}

			LogicalWorkspaceTransaction transaction = CadseCore.getLogicalWorkspace().createTransaction();

			transaction.loadItems(itemdescription);
			transaction.commit(false, true, false, projectAssociationSet);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			CadseCore.getCadseDomain().endOperation();
		}
	}

}
