package org.storefrontlibraryfacades.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;
import org.storefrontlibraryfacades.constants.StorefrontlibraryfacadesConstants;

@SuppressWarnings("PMD")
public class StorefrontlibraryfacadesManager extends GeneratedStorefrontlibraryfacadesManager
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger( StorefrontlibraryfacadesManager.class.getName() );
	
	public static final StorefrontlibraryfacadesManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (StorefrontlibraryfacadesManager) em.getExtension(StorefrontlibraryfacadesConstants.EXTENSIONNAME);
	}
	
}
