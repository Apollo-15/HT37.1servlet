package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebServlet("/api/orders")
public class OrderServlet extends HttpServlet {

    private final Map<Integer, Order> orders = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String CONTENT = "app/json";

    @Override
    protected void doGet(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException {
        String idParam = servletRequest.getParameter("id");
        if (idParam == null) {
            servletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            servletResponse.getWriter().write("{\"error\":\"Missing id parameter\"}");
            return;
        }
        try {
            int id = Integer.parseInt(idParam);
            Order order = orders.get(id);
            if (order == null) {
                servletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
                servletResponse.getWriter().write("{\"error\":\"Order not found\"}");
                return;
            }
            servletResponse.setContentType("application/json");
            servletResponse.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(servletResponse.getWriter(), order);
        } catch (NumberFormatException e) {
            servletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            servletResponse.getWriter().write("{\"error\":\"Invalid id format\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException {
        try {
            Order order = objectMapper.readValue(servletRequest.getInputStream(), Order.class);
            if (order == null || order.getOrderId() == 0) {
                servletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                servletResponse.getWriter().write("{\"error\":\"Invalid order data\"}");
                return;
            }
            orders.put(order.getOrderId(), order);
            servletResponse.setContentType("application/json");
            servletResponse.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(servletResponse.getWriter(), order);
        } catch (Exception e) {
            servletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            servletResponse.getWriter().write("{\"error\":\"Invalid JSON format\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException {
        try {
            Order updatedOrder = objectMapper.readValue(servletRequest.getInputStream(), Order.class);
            if (updatedOrder == null || updatedOrder.getOrderId() == 0) {
                servletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                servletResponse.getWriter().write("{\"error\":\"Invalid order data\"}");
                return;
            }
            if (!orders.containsKey(updatedOrder.getOrderId())) {
                servletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
                servletResponse.getWriter().write("{\"error\":\"Order not found\"}");
                return;
            }
            orders.put(updatedOrder.getOrderId(), updatedOrder);
            servletResponse.setContentType("application/json");
            servletResponse.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(servletResponse.getWriter(), updatedOrder);
        } catch (Exception e) {
            servletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            servletResponse.getWriter().write("{\"error\":\"Invalid JSON format\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException {
        String idParam = servletRequest.getParameter("id");
        if (idParam == null) {
            servletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            servletResponse.getWriter().write("{\"error\":\"Missing id parameter\"}");
            return;
        }
        try {
            int id = Integer.parseInt(idParam);
            Order removed = orders.remove(id);
            if (removed == null) {
                servletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
                servletResponse.getWriter().write("{\"error\":\"Order not found\"}");
                return;
            }
            servletResponse.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NumberFormatException e) {
            servletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            servletResponse.getWriter().write("{\"error\":\"Invalid id format\"}");
        }
    }
}