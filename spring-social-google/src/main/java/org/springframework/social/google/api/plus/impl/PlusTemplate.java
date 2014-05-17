/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.social.google.api.plus.impl;

import static org.springframework.util.StringUtils.hasText;

import org.springframework.social.google.api.impl.AbstractGoogleApiOperations;
import org.springframework.social.google.api.plus.ActivitiesPage;
import org.springframework.social.google.api.plus.Activity;
import org.springframework.social.google.api.plus.ActivityComment;
import org.springframework.social.google.api.plus.ActivityCommentsPage;
import org.springframework.social.google.api.plus.ActivityQueryBuilder;
import org.springframework.social.google.api.plus.PeoplePage;
import org.springframework.social.google.api.plus.Person;
import org.springframework.social.google.api.plus.PersonQueryBuilder;
import org.springframework.social.google.api.plus.PlusOperations;
import org.springframework.social.google.api.plus.moments.Moment;
import org.springframework.social.google.api.plus.moments.MomentQueryBuilder;
import org.springframework.social.google.api.plus.moments.MomentsPage;
import org.springframework.social.google.api.plus.moments.impl.MomentQueryBuilderImpl;
import org.springframework.web.client.RestTemplate;

/**
 * {@link PlusOperations} implementation.
 * 
 * @author Gabriel Axel
 */
public class PlusTemplate extends AbstractGoogleApiOperations implements PlusOperations {

	private static final String PEOPLE_URL = "https://www.googleapis.com/plus/v1/people/";
	private static final String ACTIVITIES_PUBLIC = "/activities/public";
	private static final String ACTIVITIES_URL = "https://www.googleapis.com/plus/v1/activities/";

	private static final String COMMENTS_URL = "https://www.googleapis.com/plus/v1/comments/";
	private static final String COMMENTS = "/comments";

	static final String PEOPLE_SEARCH_URL = "https://www.googleapis.com/plus/v1/people";
	private static final String PLUSONERS = "/people/plusoners";
	private static final String RESHARERS = "/people/resharers";

	private static final String MOMENTS_URL = PEOPLE_URL + "me/moments/vault";

	public PlusTemplate(RestTemplate restTemplate, boolean isAuthorized) {
		super(restTemplate, isAuthorized);
	}

	public Activity getActivity(String id) {
		return getEntity(ACTIVITIES_URL + id, Activity.class);
	}

	public ActivitiesPage getActivities(String userId, String pageToken) {
		StringBuilder sb = new StringBuilder(PEOPLE_URL).append(userId).append(ACTIVITIES_PUBLIC);
		if (pageToken != null) {
			sb.append("?pageToken=").append(pageToken);
		}
		return getEntity(sb.toString(), ActivitiesPage.class);
	}

	public ActivitiesPage getActivities(String userId) {
		return getActivities(userId, null);
	}

	public ActivitiesPage searchPublicActivities(String query, String pageToken) {
		return activityQuery().searchFor(query).fromPage(pageToken).getPage();
	}

	public ActivityQueryBuilder activityQuery() {
		return new ActivityQueryBuilderImpl(restTemplate);
	}

	public ActivityComment getComment(String id) {
		return getEntity(COMMENTS_URL + id, ActivityComment.class);
	}

	public ActivityCommentsPage getComments(String activityId, String pageToken) {
		StringBuilder sb = new StringBuilder(ACTIVITIES_URL).append(activityId).append(COMMENTS);
		if (hasText(pageToken)) {
			sb.append("?pageToken=").append(pageToken);
		}
		return getEntity(sb.toString(), ActivityCommentsPage.class);
	}

	public Person getPerson(String id) {
		return getEntity(PEOPLE_URL + id, Person.class);
	}

	public Person getGoogleProfile() {
		return getPerson("me");
	}

	public PersonQueryBuilder personQuery() {
		return new PersonQueryBuilderImpl(restTemplate);
	}

	public PeoplePage getPeopleInCircles(String id, String pageToken) {
		return getEntity(PEOPLE_URL + id + "/people/visible", PeoplePage.class);
	}

	public PeoplePage searchPeople(String query, String pageToken) {
		return personQuery().searchFor(query).fromPage(pageToken).getPage();
	}

	public PeoplePage getActivityPlusOners(String activityId, String pageToken) {
		return getEntity(ACTIVITIES_URL + activityId + PLUSONERS, PeoplePage.class);
	}

	public PeoplePage getActivityResharers(String activityId, String pageToken) {
		return getEntity(ACTIVITIES_URL + activityId + RESHARERS, PeoplePage.class);
	}

	public Moment insertMoment(Moment moment) {
		return saveEntity(MOMENTS_URL, moment);
	}

	public MomentQueryBuilder momentQuery() {
		return new MomentQueryBuilderImpl(MOMENTS_URL, restTemplate);
	}

	public MomentsPage getMoments(String pageToken) {
		return momentQuery().getPage();
	}

	public void deleteMoment(String id) {
		deleteEntity("https://www.googleapis.com/plus/v1/moments", id);
	}
}