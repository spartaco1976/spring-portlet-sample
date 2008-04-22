/*
 * Copyright 2005-2008 the original author or authors.
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

package sample.portlet;

import java.util.Map;

import javax.portlet.PortletRequest;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.mock.web.portlet.MockActionRequest;
import org.springframework.mock.web.portlet.MockActionResponse;
import org.springframework.mock.web.portlet.MockPortletRequest;
import org.springframework.mock.web.portlet.MockRenderRequest;
import org.springframework.mock.web.portlet.MockRenderResponse;
import org.springframework.security.AccessDeniedException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.context.SecurityContextImpl;
import org.springframework.security.providers.TestingAuthenticationToken;
import org.springframework.security.providers.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.portlet.ModelAndView;

import sample.domain.Book;

public class TestBooksPortlet extends TestCase {

	private static final Log logger = LogFactory.getLog(TestBooksPortlet.class);

	public static final String TESTUSER = "testuser";
	public static final String TESTCRED = PortletRequest.FORM_AUTH;
	public static final String TESTROLE = "testrole";

	protected static final ApplicationContext appContext;
	protected static final ApplicationContext booksPortletContext;

    static {
        try {
    		appContext = new FileSystemXmlApplicationContext(
    		        new String[]{"WEB-INF/context/applicationContext.xml"});
    		booksPortletContext = new FileSystemXmlApplicationContext(
    		        new String[]{"WEB-INF/context/portlet/books.xml"}, appContext);
        } catch (Exception ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    private MockRenderRequest createRenderRequest() {
		MockRenderRequest request = new MockRenderRequest();
		applyRequestSecurity(request);
		return request;
    }

    private MockRenderResponse createRenderResponse() {
		MockRenderResponse response = new MockRenderResponse();
		return response;
    }

    private MockActionRequest createActionRequest() {
    	MockActionRequest request = new MockActionRequest();
		applyRequestSecurity(request);
		return request;
    }

    private MockActionResponse createActionResponse() {
    	MockActionResponse response = new MockActionResponse();
		return response;
    }

    private void applyRequestSecurity(MockPortletRequest request) {
		request.setRemoteUser(TESTUSER);
		request.setUserPrincipal(new TestingAuthenticationToken(TESTUSER, TESTCRED, null));
		request.addUserRole(TESTROLE);
		request.setAuthType(PortletRequest.FORM_AUTH);
    }

    private void setupSecurityContext(PortletRequest request) {
    	PreAuthenticatedAuthenticationToken token =
    		new PreAuthenticatedAuthenticationToken(TESTUSER, TESTCRED,
    			new GrantedAuthority[] { new GrantedAuthorityImpl("ROLE_USER"), new GrantedAuthorityImpl("ROLE_" + TESTROLE) } );
		token.setDetails(request);
		SecurityContext context = new SecurityContextImpl();
		context.setAuthentication(token);
		SecurityContextHolder.setContext(context);
    }

    private void cleanupSecurityContext() {
		SecurityContextHolder.clearContext();
    }

	public void testBookViewController() throws Exception {

		BookViewController controller = (BookViewController)booksPortletContext.getBean("bookViewController");

		MockRenderRequest request = createRenderRequest();
		MockRenderResponse response = createRenderResponse();

		// set the parameter for the book we want to get
		request.addParameter("book", "1");

		setupSecurityContext(request);

		try {

			// ask the controller to handle the request
			ModelAndView mav = controller.handleRenderRequest(request, response);

			// got back a model-and-view?
			assertNotNull("should get model and view back", mav);

			// show the viewname
			logger.info("view: " + mav.getViewName());

			// get the model
			Map<?,?> model = mav.getModel();

			// show the model include the book
			assertTrue("model should contain a book", model.containsKey("book"));
			Book book = (Book)model.get("book");

			// check the book's details
			logger.info("book.author: " + book.getAuthor());
			assertEquals("incorrect author", "Neal Stephenson", book.getAuthor());
			logger.info("book.title: " + book.getTitle());
			assertEquals("incorrect title", "Snow Crash", book.getTitle());
			logger.info("book.count: " + book.getCount());
			assertEquals("incorrect count", Integer.valueOf(50), book.getCount());

		} finally {

			cleanupSecurityContext();

		}
	}

	public void testBookDeleteController() {

		BookDeleteController controller = (BookDeleteController)booksPortletContext.getBean("bookDeleteController");

		MockActionRequest request = createActionRequest();
		MockActionResponse response = createActionResponse();

		// set the parameter for the book we want to delete
		request.addParameter("book", "1");

		setupSecurityContext(request);

		try {

			// ask the controller to handle the request
			controller.handleActionRequest(request, response);

			fail("should not have been able to delete the book");

		} catch (AccessDeniedException e) {

			logger.info("unable to delete book as expected because access was denied");

		} catch (Exception e) {

			fail("caught expected exception: " + e);

		} finally {

			cleanupSecurityContext();

		}
	}

}
