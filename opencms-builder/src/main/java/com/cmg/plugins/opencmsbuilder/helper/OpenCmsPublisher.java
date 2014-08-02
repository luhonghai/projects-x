/*
 * Copyright (c) 2014 CMG Ltd All rights reserved.
 *
 * This software is the confidential and proprietary information of CMG
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with CMG.
 */

package com.cmg.plugins.opencmsbuilder.helper;

import com.cmg.plugins.opencmsbuilder.util.Logger;
import org.opencms.db.CmsPublishList;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsResourceFilter;
import org.opencms.lock.CmsLock;
import org.opencms.main.CmsException;
import org.opencms.main.OpenCms;
import org.opencms.publish.CmsPublishManager;
import org.opencms.publish.Messages;
import org.opencms.report.A_CmsReportThread;
import org.opencms.report.CmsShellReport;
import org.opencms.report.I_CmsReport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

public class OpenCmsPublisher extends A_CmsReportThread {
    /** The CmsObject used to start this thread. */
	private CmsObject m_cms;

	/** The list of resources to publish. */
	private CmsPublishList m_publishList;

	I_CmsReport m_report = null;

    private List<String> resources;

	protected OpenCmsPublisher(CmsObject cms, CmsPublishList publishList, List<String> resources) {
		super(cms, "Publishing Thread");
		m_cms = cms;
		m_publishList = publishList;
        this.resources = resources;
		m_report = new CmsShellReport(cms.getRequestContext().getLocale());
	}

	/**
	 * Publish all resource in a List.
	 * 
	 * @param resources
	 */
	public static void publishResources(List<String> resources, CmsObject cms) {
		try {			
			cms.getRequestContext().setSiteRoot("/");
			cms.getRequestContext().setCurrentProject(cms.readProject("Offline"));
			List publishResources = new ArrayList(resources.size());
			Iterator i = resources.iterator();
			while (i.hasNext()) {
				String resName = (String) i.next();
				try {
					if (cms.existsResource(resName, CmsResourceFilter.ALL)) {
						CmsResource res = cms.readResource(resName, CmsResourceFilter.ALL);
						publishResources.add(res);
						// check if the resource is locked
						CmsLock lock = cms.getLock(resName);
						if (!lock.isNullLock()) {
							// resource is locked, so unlock it
							cms.changeLock(resName);
							cms.unlockResource(resName);
						}
                        // cms.lockResource(res);
					}
				} catch (CmsException cmse) {
                    Logger.getLogger().error("Error while publishing resource", cmse);
				}
			}

			// create publish list for direct publish
			CmsPublishList publishList = OpenCms.getPublishManager().getPublishList(cms, publishResources, false, true);

			OpenCmsPublisher thread = new OpenCmsPublisher(cms, publishList,resources);
			thread.start();
			thread.join();
		} catch (Exception e) {
            Logger.getLogger().error("Error while publishing resource", e);
		}
	}
	
	public void releaseLockResources() {
		try {
            m_cms.getRequestContext().setSiteRoot("/");
            m_cms.getRequestContext().setCurrentProject(m_cms.readProject("Offline"));
			Iterator i = resources.iterator();
			while (i.hasNext()) {
				String resName = (String) i.next();

				try {
					if (m_cms.existsResource(resName, CmsResourceFilter.ALL)) {
                        Logger.getLogger().info("Found resource: " + resName);
                       // m_cms.changeLock(resName);
                        m_cms.unlockResource(resName);
					}
				} catch (CmsException cmse) {
                    Logger.getLogger().error("Error while release lock resource", cmse);
				}
			}
			
		} catch (Exception e) {
            Logger.getLogger().error("Error while release resource", e);
		}
	}

	@Override
	public String getReportUpdate() {
		return null;
	}

	/**
	 * @see Runnable#run()
	 */
	public void run() {
		try {
			m_report.println(Messages.get().container(Messages.RPT_PUBLISH_RESOURCE_BEGIN_0), I_CmsReport.FORMAT_HEADLINE);
			CmsPublishManager publishManager = OpenCms.getPublishManager();
			publishManager.publishProject(m_cms, m_report, m_publishList);
			while (publishManager.isRunning()) {
				this.sleep(500);
				if (!publishManager.isRunning()) {
					break;
				}
			}
			m_report.println(Messages.get().container(Messages.RPT_PUBLISH_RESOURCE_END_0), I_CmsReport.FORMAT_HEADLINE);
            //releaseLockResources();
		} catch (Exception e) {
			m_report.println(e);
		}
	}

}
