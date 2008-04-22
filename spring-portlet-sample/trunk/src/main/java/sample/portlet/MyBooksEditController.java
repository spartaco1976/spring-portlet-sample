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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.mvc.AbstractController;

import sample.domain.Book;
import sample.service.BookService;

public class MyBooksEditController extends AbstractController {

    private BookService bookService;

    @Override
	public ModelAndView handleRenderRequestInternal(RenderRequest request, RenderResponse response) {

		SortedSet<Book> myBooks = loadMyBooks(request);
    	SortedSet<Book> allBooks = bookService.getAllBooks();
		allBooks.removeAll(myBooks);

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("myBooks", myBooks);
		model.put("allBooks", allBooks);

        return new ModelAndView("myBooksEdit", model);
	}

	@Override
	protected void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {

	    String what = request.getParameter("what");
	    if (what == null) return;

	    Integer id = Integer.valueOf(request.getParameter("book"));
	    if (id == null) return;

		Book book = bookService.getBook(id);
	    if (book == null) return;

		SortedSet<Book> myBooks = loadMyBooks(request);

    	if (what.equals("add")) {
    		myBooks.add(book);
    	} else if (what.equals("remove")) {
    		myBooks.remove(book);
	    }

    	storeMyBooks(request, myBooks);
	}

	private SortedSet<Book> loadMyBooks(PortletRequest request) {
		SortedSet<Book> myBooks = new TreeSet<Book>();
		PortletPreferences prefs = request.getPreferences();
		String[] keys = prefs.getValues("myBooks", null);
		if (keys != null) {
			for (int i = 0; i < keys.length; i++) {
				try {
					Integer key = Integer.valueOf(keys[i]);
					Book book = bookService.getBook(key);
					if (book != null)
						myBooks.add(book);
				} catch (NumberFormatException ex) {
				}
			}
		}
		return myBooks;
	}

	private void storeMyBooks(PortletRequest request, SortedSet<Book> myBooks) {

		ArrayList<String> keys = new ArrayList<String>();
		for (Iterator<Book> i = myBooks.iterator(); i.hasNext();) {
			Book book = i.next();
			keys.add(book.getKey().toString());
		}

		String[] keysArr = keys.toArray(new String[] {});


		try {
			PortletPreferences prefs = request.getPreferences();
			prefs.setValues("myBooks", keysArr);
			prefs.store();
		} catch (Exception e) {
			logger.warn("unable to set portlet preference", e);
		}
	}

	@Required
    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }
}
