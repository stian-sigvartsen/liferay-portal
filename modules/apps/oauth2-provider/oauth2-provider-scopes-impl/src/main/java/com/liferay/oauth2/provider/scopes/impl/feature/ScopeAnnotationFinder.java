/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 * <p>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.oauth2.provider.scopes.impl.feature;

import com.liferay.oauth2.provider.scopes.api.RequiresScope;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxrs.model.ClassResourceInfo;
import org.apache.cxf.jaxrs.model.MethodDispatcher;
import org.apache.cxf.jaxrs.model.OperationResourceInfo;
import org.apache.cxf.jaxrs.utils.ResourceUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

class ScopeAnnotationFinder {

	public static Collection<String> find(Class<?> clazz, Bus bus) {
		ClassResourceInfo classResourceInfo =
			ResourceUtils.createClassResourceInfo(
				clazz, clazz, true, true, bus);

		Set<String> scopes = new HashSet<>();

		doFind(new HashSet<>(), scopes, true, classResourceInfo);

		return scopes;
	}

	private static void doFind(
		Set<ClassResourceInfo> visited, Set<String> accum,
		boolean recurse, ClassResourceInfo classResourceInfo) {

		visited.add(classResourceInfo);

		Class<?> resourceClass = classResourceInfo.getResourceClass();

		RequiresScope declaredAnnotation =
			resourceClass.getDeclaredAnnotation(RequiresScope.class);

		if (declaredAnnotation != null) {
			accum.addAll(Arrays.asList(declaredAnnotation.value()));
		}

		MethodDispatcher methodDispatcher =
			classResourceInfo.getMethodDispatcher();

		Set<OperationResourceInfo> operationResourceInfos =
			methodDispatcher.getOperationResourceInfos();

		for (OperationResourceInfo operationResourceInfo :
			operationResourceInfos) {

			doFind(visited, accum, recurse, operationResourceInfo);
		}

		visited.remove(classResourceInfo);
	}

	private static void doFind(
		Set<ClassResourceInfo> visited, Set<String> accum,
		boolean recurse, OperationResourceInfo operationResourceInfo) {

		Method annotatedMethod = operationResourceInfo.getAnnotatedMethod();

		RequiresScope declaredAnnotation =
			annotatedMethod.getDeclaredAnnotation(RequiresScope.class);

		if (declaredAnnotation != null) {
			accum.addAll(Arrays.asList(declaredAnnotation.value()));
		}

		if (operationResourceInfo.isSubResourceLocator()) {
			ClassResourceInfo classResourceInfo =
				operationResourceInfo.getClassResourceInfo();

			Class<?> returnType =
				operationResourceInfo.getAnnotatedMethod().getReturnType();

			ClassResourceInfo subResource =
				classResourceInfo.getSubResource(
					returnType, returnType);

			if (subResource != null) {
				if (recurse) {
					doFind(visited, accum, false, subResource);
				}
			}
		}
	}

}
