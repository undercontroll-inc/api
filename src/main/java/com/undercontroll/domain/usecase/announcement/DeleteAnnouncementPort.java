package com.undercontroll.domain.usecase.announcement;

public interface DeleteAnnouncementPort {
    record Input(Integer id) {}

    void execute(Input input);
}
