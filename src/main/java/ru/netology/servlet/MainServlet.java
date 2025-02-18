package ru.netology.servlet;

import ru.netology.controller.PostController;

import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {

    private PostController controller;

    @Override
    public void init() {
        final var repository = new PostRepository();
        final var service = new PostService(repository);
        controller = new PostController(service);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {

        try {
            final var path = req.getRequestURI();
            final var method = req.getMethod();

            String GET_METHOD = "GET";
            if (method.equals(GET_METHOD) && path.equals("/api/posts")) {
                controller.all(resp);
                return;
            }
            if (method.equals(GET_METHOD) && path.matches("/api/posts/\\d+")) {
                // easy way
                final var id = parseId(path);
                controller.getById(id, resp);
                return;
            }
            String POST_METHOD = "POST";
            if (method.equals(POST_METHOD) && path.equals("/api/posts")) {
                controller.save(req.getReader(), resp);
                return;
            }
            String DELETE_METHOD = "DELETE";
            if (method.equals(DELETE_METHOD) && path.matches("/api/posts/\\d+")) {
                // easy way
                final var id = parseId(path);
                controller.removeById(id, resp);
                return;
            }
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public long parseId(String path) {
        return Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
    }
}