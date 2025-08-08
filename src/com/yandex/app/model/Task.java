package com.yandex.app.model;

import com.google.gson.annotations.SerializedName;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("id")
    private int id;

    @SerializedName("status")
    private TaskStatus status;

    @SerializedName("startTime")
    private LocalDateTime startTime;

    @SerializedName("duration")
    private Duration duration;

    // Конструктор по умолчанию для Gson
    public Task()
    {
    }

    public Task(String title, String description)
    {
        this(title, description, null, 0);
    }

    public Task(String title, String description, LocalDateTime startTime, long durationMinutes)
    {
        this.title = title;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.startTime = startTime;
        this.duration = Duration.ofMinutes(durationMinutes);
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public TaskStatus getStatus()
    {
        return status;
    }

    public void setStatus(TaskStatus status)
    {
        this.status = status;
    }

    public Duration getDuration()
    {
        return duration;
    }

    public void setDuration(Duration duration)
    {
        this.duration = duration;
    }

    public LocalDateTime getStartTime()
    {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime)
    {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime()
    {
        if (startTime == null || duration == null)
        {
            return null;
        }
        return startTime.plus(duration);
    }

    public TaskType getTaskType()
    {
        return TaskType.TASK;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }

    @Override
    public String toString()
    {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + (duration != null ? duration.toMinutes() + "m" : "null") +
                '}';
    }
}