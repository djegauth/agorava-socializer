/*
 * Copyright 2012 Agorava
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.agorava.socializer;

import java.util.Collections;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.agorava.LinkedIn;
import org.agorava.core.api.event.SocialEvent;
import org.agorava.core.api.event.StatusUpdated;
import org.agorava.linkedin.NetworkUpdateService;
import org.agorava.linkedin.model.CurrentShare;
import org.agorava.linkedin.model.NetworkUpdateParameters;
import org.agorava.linkedin.model.NewShare;
import org.agorava.linkedin.model.NewShare.NewShareVisibility;
import org.agorava.linkedin.model.NewShare.NewShareVisibilityCode;
import org.agorava.linkedin.model.UpdateTypeInput;
import org.jboss.logging.Logger;

//import org.jboss.solder.logging.Logger;

/**
 * @author Antoine Sabot-Durand
 */
@Named
@RequestScoped
public class LinkedInController {

	@Produces
	@Named
	private NewShare linkedInShare;

	@Inject
	private NetworkUpdateService updateService;

	private String updates;

	@Inject
	Logger log;

	@PostConstruct
	public void init() {
		linkedInShare = new NewShare("", null, new NewShareVisibility(
				NewShareVisibilityCode.CONNECTIONS_ONLY));
	}

	public String sendUpdate() {
		updateService.share(linkedInShare);
		return "ok";
	}

	public CurrentShare receiveCurrentShare() {
		CurrentShare result = updateService.getCurrentShare();
		return result;
	}

	public void setUpdates(String updates) {
		this.updates = updates;
	}

	public String getUpdates() {
		NetworkUpdateParameters parameters = new NetworkUpdateParameters(null,
				true, 0, 20, null, null, false, false,
				Collections.singletonList(UpdateTypeInput.SHAR));
		this.updates = updateService.getNetworkUpdatesJson(parameters);
		return updates;
	}

	protected void statusUpdateObserver(
			@Observes @LinkedIn StatusUpdated statusUpdate) {
		if (statusUpdate.getStatus().equals(SocialEvent.Status.SUCCESS)) {
			log.debugf("Status update with : %s ", statusUpdate.getMessage());
			init();
		}
	}
}
