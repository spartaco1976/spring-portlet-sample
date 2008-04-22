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
import org.springframework.validation.Errors;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.PortletRequestDataBinder;
import org.springframework.web.portlet.mvc.AbstractWizardFormController;

import sample.domain.Book;
import sample.domain.BookValidator;
import sample.service.BookService;

class BookAddController extends AbstractWizardFormController {

    private BookService bookService;

    @Override
    protected void processFinish(
            ActionRequest request, ActionResponse response,
            Object command, BindException errors)
    		throws Exception {
		bookService.addBook((Book)command);
		response.setRenderParameter("action","books");
    }

    @Override
    protected void processCancel(
            ActionRequest request, ActionResponse response,
            Object command, BindException errors)
            throws Exception {
		response.setRenderParameter("action","books");
    }

    @Override
    protected void validatePage(
            Object command, Errors errors, int page, boolean finish) {
        if (finish) {
            this.getValidator().validate(command, errors);
            return;
        }
		Book book = (Book)command;
		BookValidator bookValidator = (BookValidator)getValidator();
		switch (page) {
			case 0: bookValidator.validateAuthor(book, errors);	break;
			case 1: bookValidator.validateTitle(book, errors); break;
			case 2: bookValidator.validateDescription(book, errors); break;
			case 3: bookValidator.validateAvailability(book, errors); break;
			case 4: bookValidator.validateCount(book, errors); break;
			case 5: bookValidator.validateWebsite(book, errors); break;
			case 6: bookValidator.validateCoverPng(book, errors); break;
			default: ;
		}
    }

    @Override
    protected void initBinder(PortletRequest request, PortletRequestDataBinder binder)
			throws Exception {
	    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	    binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
	    binder.setAllowedFields(new String[] {"author","title","description","availability","count","webiste","coverPng"});
    }

    @Override
	protected ModelAndView renderInvalidSubmit(RenderRequest request, RenderResponse response)
			throws Exception {
	    return null;
	}

    @Override
	protected void handleInvalidSubmit(ActionRequest request, ActionResponse response)
			throws Exception {
	    response.setRenderParameter("action","books");
	}

    @Required
	public void setBookService(BookService bookService) {
	    this.bookService = bookService;
	}

}
