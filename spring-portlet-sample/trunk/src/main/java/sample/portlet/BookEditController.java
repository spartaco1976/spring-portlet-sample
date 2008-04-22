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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.validation.BindException;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.PortletRequestDataBinder;
import org.springframework.web.portlet.mvc.SimpleFormController;

import sample.domain.Book;
import sample.service.BookService;

public class BookEditController extends SimpleFormController {

    private BookService bookService;

    @Override
	public void onSubmitAction(ActionRequest request, ActionResponse response,
			Object command,	BindException errors) {

		Book book = (Book) command;
	    Integer key = null;

    	try {
    	    key = Integer.valueOf(request.getParameter("book"));
    	} catch (NumberFormatException ex) {
    	}

		if (key == null) {
			key = bookService.addBook(book);
		} else {
			bookService.saveBook(book);
		}

		if (key == null) {
			response.setRenderParameter("action","books");
		} else {
			response.setRenderParameter("action","viewBook");
			response.setRenderParameter("book",key.toString());
		}
	}

    @Override
    protected Object formBackingObject(PortletRequest request){
    	Book book;
    	try {
    	    Integer key = Integer.valueOf(request.getParameter("book"));
    	    book = bookService.getBook(key);
    	} catch (NumberFormatException ex) {
        	book = new Book();
    	}
		return book;
	}

    @Override
	protected void initBinder(PortletRequest request, PortletRequestDataBinder binder) {
	    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	    binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
	    binder.setAllowedFields(new String[] {"author","title","description","availability","count","website","coverPng"});
	}

    @Override
	protected ModelAndView renderInvalidSubmit(RenderRequest request, RenderResponse response) {
	    return null;
	}

    @Override
	protected void handleInvalidSubmit(ActionRequest request, ActionResponse response) {
		response.setRenderParameter("action","books");
	}

    @Required
	public void setBookService(BookService bookService) {
	    this.bookService = bookService;
	}
}
