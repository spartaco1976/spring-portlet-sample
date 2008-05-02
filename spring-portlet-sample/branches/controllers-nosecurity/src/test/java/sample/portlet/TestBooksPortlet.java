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

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.mock.web.portlet.MockRenderRequest;
import org.springframework.mock.web.portlet.MockRenderResponse;
import org.springframework.web.portlet.ModelAndView;

import sample.domain.Book;

public class TestBooksPortlet extends TestCase {

	private static final Log logger = LogFactory.getLog(TestBooksPortlet.class);

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
		return request;
    }

    private MockRenderResponse createRenderResponse() {
		MockRenderResponse response = new MockRenderResponse();
		return response;
    }

	public void testBookViewController() throws Exception {

		BookViewController controller = (BookViewController)booksPortletContext.getBean("bookViewController");

		MockRenderRequest request = createRenderRequest();
		MockRenderResponse response = createRenderResponse();

		// set the parameter for the book we want to get
		request.addParameter("book", "1");

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

		}
	}

}
