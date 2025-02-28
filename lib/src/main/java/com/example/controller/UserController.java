package com.example.controller;

import com.example.domain.Donor;
import com.example.service.UserService;
import io.micrometer.core.annotation.Timed;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;

import io.micronaut.http.server.cors.CrossOrigin;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;

@ExecuteOn(TaskExecutors.IO)
@Controller("/users")
@CrossOrigin("*")
public class UserController {

    @Inject
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Post("/add")
    @Timed("custom-user-create-timed")
/*    @Scheduled(fixedRate = "${custom.thread.count.updateFrequency:15s}",
            initialDelay = "${custom.thread.count.initialDelay:0s}")*/
    public HttpResponse<Donor> createUser(@Body Donor donor) {
        return userService.saveUser(donor);
    }
    @Get("/request/{id}")
    public HttpResponse<Donor> requestBlood(@PathVariable Long id) {
        return userService.requestBlood(id);
    }

    @Get("/{id}")
    public HttpResponse<Donor> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @Get("/")
    public HttpResponse<List<Donor>> getAllUsers() {
        return userService.getAllUsers();
    }


    @Put("/{id}")
    public HttpResponse<Donor> updateUser(@PathVariable Long id, @Body Donor updatedDonor) {
        return userService.updateUser(id, updatedDonor);
    }

    @Delete("/{id}")
    public HttpResponse<Void> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }
    @Get("/search")
    public HttpResponse<List<Donor>> listUsers(
                                                    @QueryValue String type,
                                                    @QueryValue String location) {

        return userService.searchUser(location,type);
    }
}







