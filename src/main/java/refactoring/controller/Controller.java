package refactoring.controller;

import refactoring.HttpRequest;
import refactoring.HttpResponse;

public interface Controller {
    void service(HttpRequest request, HttpResponse response);
}
