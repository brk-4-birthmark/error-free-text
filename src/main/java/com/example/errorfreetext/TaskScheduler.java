package com.example.errorfreetext;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskScheduler {

    private final TaskRepository taskRepository;
    private final YandexSpellerClient yandexClient;

    public TaskScheduler(TaskRepository taskRepository, YandexSpellerClient yandexClient) {
        this.taskRepository = taskRepository;
        this.yandexClient = yandexClient;
    }

    @Scheduled(fixedDelay = 5000)
    public void processTasks() {
        List<Task> newTasks = taskRepository.findByStatus(TaskStatus.NEW);

        for (Task task : newTasks) {
            try {
                task.setStatus(TaskStatus.PROCESSING);
                taskRepository.save(task);

                System.out.println(">>> Начинаю обработку задачи: " + task.getId());

                String corrected = yandexClient.correctText(task.getOriginalText());

                task.setCorrectedText(corrected);
                task.setStatus(TaskStatus.DONE);
                taskRepository.save(task);

                System.out.println(">>> Задача успешно обработана: " + task.getId());

            } catch (Exception e) {
                task.setStatus(TaskStatus.ERROR);
                task.setErrorMessage(e.getMessage());
                taskRepository.save(task);
                System.out.println(">>> ОШИБКА при обработке задачи: " + e.getMessage());
            }
        }
    }
}