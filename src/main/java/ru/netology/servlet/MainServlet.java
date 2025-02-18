package ru.netology.servlet;

import ru.netology.controller.PostController;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {

    private static final String GET_METHOD = "GET";
    private static final String POST_METHOD = "POST";
    private static final String DELETE_METHOD = "DELETE";
    private static final String API_POSTS_PATH = "/api/posts";
    private static final String API_POSTS_ID_REGEX = "/api/posts/\\d+";

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

            if (method.equals(GET_METHOD) && path.equals(API_POSTS_PATH)) {
                controller.all(resp);
                return;
            }

            if (method.equals(GET_METHOD) && path.matches(API_POSTS_ID_REGEX)) {
                final var id = parseId(path);
                controller.getById(id, resp);
                return;
            }

            // Обработка POST /api/posts
            if (method.equals(POST_METHOD) && path.equals(API_POSTS_PATH)) {
                controller.save(req.getReader(), resp);
                return;
            }

            if (method.equals(DELETE_METHOD) && path.matches(API_POSTS_ID_REGEX)) {
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