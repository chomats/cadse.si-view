package fede.workspace.model.manager;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String	BUNDLE_NAME	= "fede.workspace.model.manager.messages";	//$NON-NLS-1$
	public static String		mc_cannot_set_name;
	public static String		mc_name_already_exists;
	public static String		mc_name_must_be_specified;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
