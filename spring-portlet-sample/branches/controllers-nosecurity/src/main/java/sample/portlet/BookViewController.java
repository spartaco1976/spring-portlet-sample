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

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.mvc.AbstractController;

import sample.service.BookService;

public class BookViewController extends AbstractController {

    private BookService bookService;

    @Override
	public ModelAndView handleRenderRequestInternal(RenderRequest request, RenderResponse response) {
	    // get the id and display it
	    Integer id = Integer.valueOf(request.getParameter("book"));
        return new ModelAndView("bookView", "book", bookService.getBook(id));
	}

    @Required
    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }
}
