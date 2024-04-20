package com.serotonin.mango.web.mvc.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.AbstractController;

public class LandingController extends AbstractController {
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView(new LandingView());
    }

    static class LandingView implements View {
        public String getContentType() {
            return null;
        }

        public void render(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
            response.getWriter().write("hi");
        }
    }
}
