package com.liferay.portal.security.auth.verifier.internal.tracker;

import com.liferay.portal.kernel.security.access.control.AccessControlThreadLocal;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

@Component(
	property = {
		"after-filter=Session Max Allowed Filter", "servlet-context-name=",
		"servlet-filter-name=Remote Access Filter", "url-pattern=/en/*",
		"url-regex-ignore-pattern=^/html/.+\\.(css|gif|html|ico|jpg|js|png)(\\?.*)?$"
	},
	service = Filter.class
)
public class RemoteAccessFilter implements Filter {

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(
		ServletRequest servletRequest, ServletResponse servletResponse,
		FilterChain filterChain)
		throws IOException, ServletException {

		boolean remoteAccess = AccessControlThreadLocal.isRemoteAccess();

		AccessControlThreadLocal.setRemoteAccess(true);

		try {
			filterChain.doFilter(servletRequest, servletResponse);
		}
		finally {
			AccessControlThreadLocal.setRemoteAccess(remoteAccess);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) {
	}
}
